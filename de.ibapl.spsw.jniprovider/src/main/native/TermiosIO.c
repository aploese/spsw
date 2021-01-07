/*
 * SPSW - Drivers for the serial port, https://github.com/aploese/spsw/
 * Copyright (C) 2009-2021, Arne Plöse and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
#include "spsw-jni.h"

#include <termios.h>
#include <poll.h>
#include <errno.h>
#include <unistd.h>
#include <stdlib.h>
#include <time.h>
#include <sys/ioctl.h>
#include <stdint.h>

#include "de_ibapl_spsw_jniprovider_GenericTermiosSerialPortSocket.h"

#ifdef __cplusplus
extern "C" {
#endif
    
#define PORT_FD_IDX 0
#define CLOSE_FD_IDX 1
    
    int32_t readBuffer(JNIEnv *env, jobject sps, void *buff, int32_t len) {
        struct pollfd fds[2];
        fds[PORT_FD_IDX].fd = (*env)->GetIntField(env, sps, spsw_fd);
        fds[PORT_FD_IDX].events = POLLIN;
        fds[CLOSE_FD_IDX].fd = (*env)->GetIntField(env, sps, spsw_closeEventReadFd);
        fds[CLOSE_FD_IDX].events = POLLIN;

        ssize_t nread = read(fds[PORT_FD_IDX].fd, buff, (uint32_t) len);
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
        } else if (nread == len) {
            return (int32_t) nread;
        }

        //read from buffer did not read all, so pollTimeout is needed
        const int pollTimeout = (*env)->GetIntField(env, sps,
                spsw_pollReadTimeout);

        //read from buffer did not read all, so the overall time out may be needed
        //See javaTimeNanos() in file src/os/linux/vm/os_linux.cpp of hotspot sources
        struct timespec endTime;
        //calculate endTime
        if (pollTimeout > 0) {
            clock_gettime(CLOCK_MONOTONIC, &endTime);
            endTime.tv_sec += pollTimeout / 1000; //full seconds
            endTime.tv_nsec += (pollTimeout % 1000) * 1000000; // reminder goes to nanos
            if (endTime.tv_nsec > 1000000000) {
                //Overflow occured
                endTime.tv_sec += 1;
                endTime.tv_nsec -= 1000000000;
            }
        }


        if (nread == 0) {
            //Nothing read yet so use the pollReadTimeout to wait for any data 
            // a guard is needed for len == 0 so we do not wait here...
            int poll_result = poll(fds, 2, pollTimeout);

            if (poll_result == 0) {
                //Timeout
                throw_TimeoutIOException(env, 0, "readBuffer poll timeout");
                return -1;
            } else if ((poll_result < 0)) {
                throw_InterruptedIOExceptionWithError(env, 0,
                        "readBytes poll: Error during poll");
                return -1;
            } else {
                if (fds[CLOSE_FD_IDX].revents == POLLIN) {
                    //we can read from close_event_fd => port is closing
                    throw_AsynchronousCloseException(env);
                    return -1;
                } else if (fds[PORT_FD_IDX].revents == POLLIN) {
                    //Happy path just check if its the right event...
                    nread = read(fds[PORT_FD_IDX].fd, buff, (uint32_t) len);
                    if (nread < 0) {
                        if ((*env)->GetIntField(env, sps, spsw_fd) == INVALID_FD) {
                            throw_AsynchronousCloseException(env);
                            return -1;
                        } else if (nread == 0) {
                            throw_IOException(env, "Should never happen: Nothing read after poll");
                            return -1;
                        } else {
                            throw_InterruptedIOExceptionWithError(env, 0,
                                    "readBytes read error: Should never happen");
                            return -1;
                        }
                    } else if (nread == len) {
                        return (int32_t) nread;
                    }
                } else if ((fds[PORT_FD_IDX].revents & POLLHUP) == POLLHUP) {
                    //i.e. happens when the USB to serial adapter is removed
                    throw_IOException(env, PORT_FD_INVALID);
                    return -1;
                } else if ((fds[PORT_FD_IDX].revents & POLLNVAL) == POLLNVAL) {
                    throw_AsynchronousCloseException(env);
                    return -1;
                } else {
                    throw_InterruptedIOExceptionWithError(env, 0,
                            "readBytes poll: received poll event");
                    return -1;
                }
            }
        }

        ssize_t overallRead = nread;

        const int interByteTimeout = (*env)->GetIntField(env, sps,
                spsw_interByteReadTimeout);

        //Loop over poll and read to aquire as much bytes as possible either
        // a poll timeout, the overallReadTimeout a full read buffer or an error
        // breaks the loop
        while (overallRead < len) {

            int poll_result;
            if (pollTimeout == -1) {
                //wait infinite, so just interbyte timeout counts
                poll_result = poll(fds, 2, interByteTimeout);
            } else {
                struct timespec currentTime;
                clock_gettime(CLOCK_MONOTONIC, &currentTime);
                const int32_t remainingTimeOut = (int32_t) (endTime.tv_sec - currentTime.tv_sec) * 1000 + (int32_t) ((endTime.tv_nsec - currentTime.tv_nsec) / 1000000L);
                if (remainingTimeOut < 0) {
                    //interbyte timeout or overalll timeout, something was read - do return whats read
                    return (int32_t) overallRead;
                }
                poll_result = poll(fds, 2, interByteTimeout < remainingTimeOut ? interByteTimeout : remainingTimeOut);
            }


            if (poll_result == 0) {
                //This is the interbyte timeout - We are done
                return (int32_t) overallRead;
            } else if ((poll_result < 0)) {
                throw_InterruptedIOExceptionWithError(env, 0,
                        "readBytes poll: Error during poll");
                return -1;
            } else {
                if (fds[CLOSE_FD_IDX].revents == POLLIN) {
                    //we can read from close_event_fd => port is closing
                    throw_AsynchronousCloseException(env);
                    return -1;
                } else if (fds[PORT_FD_IDX].revents == POLLIN) {
                    //Happy path
                } else if ((fds[PORT_FD_IDX].revents & POLLHUP) == POLLHUP) {
                    //i.e. happens when the USB to serial adapter is removed
                    throw_IOException(env, PORT_FD_INVALID);
                    return -1;
                } else if ((fds[PORT_FD_IDX].revents & POLLNVAL) == POLLNVAL) {
                    throw_AsynchronousCloseException(env);
                    return -1;
                } else {
                    throw_InterruptedIOExceptionWithError(env, 0,
                            "readBytes poll: received poll event");
                    return -1;
                }
            }

            //OK No timeout and no error, we should read at least one byte without blocking.
            nread = read(fds[PORT_FD_IDX].fd, buff + overallRead, (uint32_t) len - (size_t) overallRead);
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
        return (int32_t) overallRead;
    }

    /*
     * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
     * Method:    sendBreak0
     * Signature: (I)V
     */
    JNIEXPORT void JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_sendBreak0(
            JNIEnv *env, jobject sps, jint duration) {
        if (duration <= 0) {
            throw_IllegalArgumentException(env, "sendBreak duration must be grater than 0");
            return;
        }

        const int fd = (*env)->GetIntField(env, sps, spsw_fd);
        if (ioctl(fd, TIOCSBRK)) {
            throw_ClosedOrNativeException(env, sps, "Can't sendBreak");
            return;
        }
        if (usleep((uint32_t) duration * 1000)) {
            throw_RuntimeException(env, "Error sendBreak usleep failed");
            return;
        }
        if (ioctl(fd, TIOCCBRK)) {
            throw_ClosedOrNativeException(env, sps, "Can't sendBreak");
            return;
        }
    }

    int32_t writeBuffer(JNIEnv *env, jobject sps, void *buff, int32_t len) {
        const int fd = (*env)->GetIntField(env, sps, spsw_fd);
        const int pollTimeout = (*env)->GetIntField(env, sps, spsw_pollWriteTimeout);

        ssize_t written = write(fd, buff, (uint32_t) len);

        if (written == len) {
            //all was written
            return (int32_t) written;
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
                return (int32_t) written;
            }
        }

        //calc endTime only if write all to buff failed.
        //See javaTimeNanos() in file src/os/linux/vm/os_linux.cpp of hotspot sources
        struct timespec endTime;
        //endTime holds the now the start time, the real end time will be calculated if needed
        if (pollTimeout > 0) {
            clock_gettime(CLOCK_MONOTONIC, &endTime);
            endTime.tv_sec += pollTimeout / 1000; //full seconds
            endTime.tv_nsec += (pollTimeout % 1000) * 1000000; // reminder goes to nanos
            if (endTime.tv_nsec > 1000000000) {
                //Overflow occured
                endTime.tv_sec += 1;
                endTime.tv_nsec -= 1000000000;
            }
        }

        struct pollfd fds[2];
        fds[PORT_FD_IDX].fd = fd;
        fds[PORT_FD_IDX].events = POLLOUT;
        fds[CLOSE_FD_IDX].fd = (*env)->GetIntField(env, sps, spsw_closeEventReadFd);
        fds[CLOSE_FD_IDX].events = POLLIN;

        //written cant be < 0 so this is save
        size_t offset = (size_t) written;

        do {

            int32_t remainingTimeOut;
            if (pollTimeout >= 0) {
                struct timespec currentTime;
                clock_gettime(CLOCK_MONOTONIC, &currentTime);
                remainingTimeOut = (int32_t) (endTime.tv_sec - currentTime.tv_sec) * 1000 + (int32_t) ((endTime.tv_nsec - currentTime.tv_nsec) / 1000000L);
                if (remainingTimeOut < 0) {
                    throw_TimeoutIOException(env, (size_t) written, "writeBuffer overallWriteTimeout");
                    return (int32_t) written;
                }
            } else {
                remainingTimeOut = -1;
            }

            int poll_result = poll(fds, 2, remainingTimeOut);

            if (poll_result == 0) {
                //Timeout occured
                throw_TimeoutIOException(env, offset, "writeBuffer poll timeout");
                return (int32_t) written;
            } else if ((poll_result < 0)) {
                throw_InterruptedIOExceptionWithError(env, offset, "poll timeout with error writeBytes");
                return (int32_t) written;
            } else {
                if (fds[CLOSE_FD_IDX].revents == POLLIN) {
                    //we can read from close_event_fd => port is closing
                    throw_AsynchronousCloseException(env);
                    return (int32_t) written;
                } else if (fds[PORT_FD_IDX].revents == POLLOUT) {
                    //Happy path all is right...
                } else if ((fds[PORT_FD_IDX].revents & POLLHUP) == POLLHUP) {
                    //i.e. happens when the USB to serial adapter is removed
                    throw_IOException(env, PORT_FD_INVALID);
                    return -1;
                } else if ((fds[PORT_FD_IDX].revents & POLLNVAL) == POLLNVAL) {
                    throw_AsynchronousCloseException(env);
                    return -1;
                } else {
                    throw_InterruptedIOExceptionWithError(env, offset, "poll returned with poll event writeBytes");
                    return (int32_t) written;
                }
            }

            written = write(fd, buff + offset, (uint32_t) len - offset);

            if (written < 0) {
                if (fd == INVALID_FD) {
                    throw_AsynchronousCloseException(env);
                } else {
                    throw_InterruptedIOExceptionWithError(env, 0, "unknown port error 2 writeBytes");
                }
                return (int32_t) written;
            }

            offset += (size_t) written;

        } while (offset < (uint32_t) len);
        return (int32_t) written;
    }

    /*
     * Class:     de_ibapl_spsw_jniprovider_GenericTermiosSerialPortSocket
     * Method:    sendXOFF
     * Signature: ()V
     */
    JNIEXPORT void JNICALL Java_de_ibapl_spsw_jniprovider_GenericTermiosSerialPortSocket_sendXOFF
    (JNIEnv *env, __attribute__ ((unused)) jobject sps) {
        throw_IllegalArgumentException(env, "sendXOFF not implemented yet");
    }

    /*
     * Class:     de_ibapl_spsw_jniprovider_GenericTermiosSerialPortSocket
     * Method:    sendXON
     * Signature: ()V
     */
    JNIEXPORT void JNICALL Java_de_ibapl_spsw_jniprovider_GenericTermiosSerialPortSocket_sendXON
    (JNIEnv *env, __attribute__ ((unused)) jobject sps) {
        //TODO How ??? tcflow ?
        throw_IllegalArgumentException(env, "sendXON not implemented yet");
    }

#ifdef __cplusplus
}
#endif
