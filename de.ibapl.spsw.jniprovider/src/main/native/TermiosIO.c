#include "spsw-jni.h"

#include <termios.h>
#include <poll.h>
#include <errno.h>
#include <unistd.h>
#include <stdlib.h>
#include <time.h>

#include "de_ibapl_spsw_jniprovider_GenericTermiosSerialPortSocket.h"

#ifdef __cplusplus
extern "C" {
#endif
    
int readBuffer(JNIEnv *env, jobject sps, void *buff, int len) {

    struct pollfd fds[2];
    fds[0].fd = (*env)->GetIntField(env, sps, spsw_fd);
    fds[0].events = POLLIN;
    fds[1].fd = (*env)->GetIntField(env, sps, spsw_closeEventReadFd);
    fds[1].events = POLLIN;

    int nread = read(fds[0].fd, buff, len);
    if (nread < 0) {
        //Get the updatet from the class - someone could have closed it during wait...
        if ((*env)->GetIntField(env, sps, spsw_fd) == INVALID_FD) {
            throw_AsynchronousCloseException(env);
            return -1;
        } else if (EAGAIN == errno) {
            nread = 0;
        } else {
            throw_InterruptedIOExceptionWithError(env, 0,
                    "readBytes: read error during first invocation of read()");
            return -1;
        }
    }

    if (nread == 0) {
        //Nothing read yet

        const int pollTimeout = (*env)->GetIntField(env, sps,
                spsw_pollReadTimeout);
        int poll_result = poll(&fds[0], 2, pollTimeout);

        if (poll_result == 0) {
            //Timeout
            throw_TimeoutIOException(env, 0);
            return -1;
        } else if ((poll_result < 0)) {
            throw_InterruptedIOExceptionWithError(env, 0,
                    "readBytes poll: Error during poll");
            return -1;
        } else {
            if (fds[1].revents == POLLIN) {
                //we can read from close_event_fd => port is closing
                return -1;
            } else if (fds[0].revents == POLLIN) {
                //Happy path just check if its the right event...
                nread = read(fds[0].fd, buff, len);
                if (nread < 0) {
                    if ((*env)->GetIntField(env, sps, spsw_fd) == INVALID_FD) {
                        throw_AsynchronousCloseException(env);
                        return -1;
                    } else if (nread == 0) {
                        throw_TimeoutIOException(env, 0); //Is this right???
                        return -1;
                    } else {
                        throw_InterruptedIOExceptionWithError(env, 0,
                                "readBytes read error: Should never happen");
                        return -1;
                    }
                }
            } else {
                throw_InterruptedIOExceptionWithError(env, 0,
                        "readBytes poll: received poll event");
                return -1;
            }
        }
    }

    int overallRead = nread;

    const int interByteTimeout = (*env)->GetIntField(env, sps,
            spsw_interByteReadTimeout);

    //Loop over poll and read to aquire as much bytes as possible either
    // a poll timeout, a full read buffer or an error
    // breaks the loop
    while (overallRead < len) {

        int poll_result = poll(&fds[0], 2, interByteTimeout);

        if (poll_result == 0) {
            //This is the interbyte timeout - We are done
            return overallRead;
        } else if ((poll_result < 0)) {
            throw_InterruptedIOExceptionWithError(env, 0,
                    "readBytes poll: Error during poll");
            return -1;
        } else {
            if (fds[1].revents == POLLIN) {
                //we can read from close_event_fd => port is closing
                return -1;
            } else if (fds[0].revents == POLLIN) {
                //Happy path
            } else {
                throw_InterruptedIOExceptionWithError(env, 0,
                        "readBytes poll: received poll event");
                return -1;
            }
        }

        //OK No timeout and no error, we should read at least one byte without blocking.
        nread = read(fds[0].fd, buff + overallRead, len - overallRead);
        if (nread > 0) {
            overallRead += nread;
        } else {
            if ((*env)->GetIntField(env, sps, spsw_fd) == INVALID_FD) {
                throw_AsynchronousCloseException(env);
                return -1;
            } else if (nread == 0) {
                throw_InterruptedIOExceptionWithError(env, 0,
                        "readBytes: nothing to read after successful polling: Should never happen");
            } else {
                throw_InterruptedIOExceptionWithError(env, 0,
                        "readBytes read error: Should never happen");
                return -1;
            }
        }
    }
    //We reached this, because the read buffer is full.
    return overallRead;
}

/*
 * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
 * Method:    sendBreak
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_sendBreak(
        JNIEnv *env, jobject sps, jint duration) {
    if (duration <= 0) {
        throw_IllegalArgumentException(env, "sendBreak duration must be grater than 0");
        return;
    }

    const int fd = (*env)->GetIntField(env, sps, spsw_fd);
    if (tcsendbreak(fd, duration) != 0) {
        throw_ClosedOrNativeException(env, sps, "Can't sendBreak");
    }
}

int writeBuffer(JNIEnv *env, jobject sps, void *buff, int len) {
    const int fd = (*env)->GetIntField(env, sps, spsw_fd);
    const int pollTimeout = (*env)->GetIntField(env, sps, spsw_pollWriteTimeout);

    //See javaTimeNanos() in file src/os/linux/vm/os_linux.cpp of hotspot sources
    struct timespec endTime;
    clock_gettime(CLOCK_MONOTONIC, &endTime);

    int written = write(fd, buff, len);

    if (written == len) {
        //all was written
        return written;
    }

    if (written < 0) {
        if (EAGAIN == errno) {
            written = 0;
        } else {
            if (fd == INVALID_FD) {
                throw_AsynchronousCloseException(env);
            } else {
                throw_InterruptedIOExceptionWithError(env, 0, "unknown port error 1 writeBytes");
            }
            return written;
        }
    }

    struct pollfd fds[2];
    fds[0].fd = fd;
    fds[0].events = POLLOUT;
    fds[1].fd = (*env)->GetIntField(env, sps, spsw_closeEventReadFd);
    fds[1].events = POLLIN;

    int offset = written;
    //calculate the endtime...
    if (pollTimeout > 0) {
        endTime.tv_sec += pollTimeout / 1000; //full seconds
        endTime.tv_nsec += (pollTimeout % 1000) * 1000000; // reminder goes to nanos
        if (endTime.tv_nsec > 1000000000) {
            //Overflow occured
            endTime.tv_sec += 1;
            endTime.tv_nsec -= 1000000000;
        }
    }

    do {
        struct timespec currentTime;
        clock_gettime(CLOCK_MONOTONIC, &currentTime);

        jlong remainingTimeOut = (pollTimeout == -1) ? -1 : (endTime.tv_sec - currentTime.tv_sec) * 1000L + (endTime.tv_nsec - currentTime.tv_nsec) / 1000000L;

        int poll_result = poll(&fds[0], 2, remainingTimeOut);

        if (poll_result == 0) {
            //Timeout occured
            throw_TimeoutIOException(env, offset);
            return written;
        } else if ((poll_result < 0)) {
            throw_InterruptedIOExceptionWithError(env, offset, "poll timeout with error writeBytes");
            return written;
        } else {
            if (fds[1].revents == POLLIN) {
                //we can read from close_event_fd => port is closing
                throw_AsynchronousCloseException(env);
                return written;
            } else if (fds[0].revents == POLLOUT) {
                //Happy path all is right...
            } else {
                throw_InterruptedIOExceptionWithError(env, offset, "poll returned with poll event writeBytes");
                return written;
            }
        }

        written = write(fd, buff + offset, len - offset);

        if (written < 0) {
            if (fd == INVALID_FD) {
                throw_AsynchronousCloseException(env);
            } else {
                throw_InterruptedIOExceptionWithError(env, 0, "unknown port error 2 writeBytes");
            }
            return written;
        }

        offset += written;

    } while (offset < len);
    return written;
}

/*
 * Class:     de_ibapl_spsw_jniprovider_GenericTermiosSerialPortSocket
 * Method:    sendXOFF
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_jniprovider_GenericTermiosSerialPortSocket_sendXOFF
(JNIEnv *env, jobject sps) {
    throw_IllegalArgumentException(env, "sendXOFF not implemented yet");
}

/*
 * Class:     de_ibapl_spsw_jniprovider_GenericTermiosSerialPortSocket
 * Method:    sendXON
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_jniprovider_GenericTermiosSerialPortSocket_sendXON
(JNIEnv *env, jobject sps) {
    //TODO How ??? tcflow ?
    throw_IllegalArgumentException(env, "sendXON not implemented yet");
}

#ifdef __cplusplus
}
#endif