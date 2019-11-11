/*
 * SPSW - Drivers for the serial port, https://github.com/aploese/spsw/
 * Copyright (C) 2009-2019, Arne Plöse and individual contributors as indicated
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
#include <unistd.h>
#include <sys/ioctl.h>
#include <fcntl.h>
#include <errno.h>
#include <poll.h>

#ifdef HAVE_SYS_EVENTFD_H
#include <sys/eventfd.h>
#endif

#ifdef __cplusplus
extern "C" {
#endif

    /*
     * Class:     de_ibapl_spsw_jniprovider_GenericTermiosSerialPortSocket_FdCleaner
     * Method:    closeFds
     * Signature: (III)V
     */
    JNIEXPORT void JNICALL Java_de_ibapl_spsw_jniprovider_GenericTermiosSerialPortSocket_00024FdCleaner_closeFds
    (__attribute__ ((unused)) JNIEnv *env, __attribute__ ((unused)) jclass clazz, jint fd, jint close_event_read_fd, jint close_event_write_fd) {
        if (fd != -1) {
            jbyte evt_buff[8];
            evt_buff[5] = 1;
            evt_buff[6] = 1;
            evt_buff[7] = 1;
            write(close_event_write_fd, &evt_buff, 8);

            usleep(1000); //1ms
            close(close_event_write_fd);
        }
        if (fd != -1) {
            close(fd);
        }
        if (fd != -1) {
            close(close_event_read_fd);
        }
    }

    /*
     * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
     * Method:    close0
     * Signature: ()V
     */
    JNIEXPORT void JNICALL Java_de_ibapl_spsw_jniprovider_GenericTermiosSerialPortSocket_close0(
            JNIEnv *env, jobject sps) {
        //Mark port as closed...
        const int fd = (*env)->GetIntField(env, sps, spsw_fd);
        (*env)->SetIntField(env, sps, spsw_fd, INVALID_FD);

        //unlock read/write polls that wait
        const int close_event_write_fd = (*env)->GetIntField(env, sps, spsw_closeEventWriteFd);
        const int close_event_read_fd = (*env)->GetIntField(env, sps, spsw_closeEventReadFd);
        jbyte evt_buff[8];
        evt_buff[5] = 1;
        evt_buff[6] = 1;
        evt_buff[7] = 1;
        write(close_event_write_fd, &evt_buff, 8);

        usleep(1000); //1ms
        //cleanup
        (*env)->SetIntField(env, sps, spsw_closeEventWriteFd, INVALID_FD);
        (*env)->SetIntField(env, sps, spsw_closeEventReadFd, INVALID_FD);

        if (tcflush(fd, TCIOFLUSH) != 0) {
            //        perror("NATIVE Error Close - tcflush");
        }

        if (close_event_write_fd == close_event_read_fd) {
            close(close_event_read_fd);
        }
        {
            close(close_event_write_fd);
            close(close_event_read_fd);
        }
        if (close(fd) != 0) {
            throw_IOException_NativeError(env, "close0 closing port");
        }
    }

    /*
     * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
     * Method:    getInBufferBytesCount
     * Signature: ()I
     */
    JNIEXPORT jint JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_getInBufferBytesCount(
            JNIEnv *env, jobject sps) {
        int fd = (*env)->GetIntField(env, sps, spsw_fd);
        jint returnValue = -1;
        int result = ioctl(fd, FIONREAD, &returnValue);
        if (result != 0) {
            throw_ClosedOrNativeException(env, sps, "Can't read in buffer size");
        }
        return returnValue;
    }

    /*
     * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
     * Method:    getOutBufferBytesCount
     * Signature: ()I
     */
    JNIEXPORT jint JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_getOutBufferBytesCount(
            JNIEnv *env, jobject sps) {
        int fd = (*env)->GetIntField(env, sps, spsw_fd);
        jint returnValue = -1;
        int result = ioctl(fd, TIOCOUTQ, &returnValue);
        if (result != 0) {
            throw_ClosedOrNativeException(env, sps, "Can't read out buffer size");
        }
        return returnValue;
    }

    /*
     * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
     * Method:    open0
     * Signature: (Ljava/lang/String;I)V
     */
    JNIEXPORT void JNICALL Java_de_ibapl_spsw_jniprovider_GenericTermiosSerialPortSocket_open0
    (JNIEnv *env, jobject sps, jstring portName, jint paramBitSet) {

        //Do not try to reopen port and therefore failing and overriding the file descriptor
        if ((*env)->GetIntField(env, sps, spsw_fd) != INVALID_FD) {
            throw_IOException_Opend(env);
            return;
        }

        const char* port = (*env)->GetStringUTFChars(env, portName, JNI_FALSE);
        int fd = open(port, O_RDWR | O_NOCTTY | O_NONBLOCK);

        (*env)->ReleaseStringUTFChars(env, portName, port);

        if (fd == INVALID_FD) {
            (*env)->SetIntField(env, sps, spsw_fd, INVALID_FD);
            switch (errno) {
                case EBUSY:
                    throw_IOException_withPortName(env, "Port is busy: \"%s\"", portName);
                    break;
                case ENOENT:
                    throw_IOException_withPortName(env, "Port not found: \"%s\"", portName);
                    break;
                case EACCES:
                    throw_IOException_withPortName(env, "Permission denied: \"%s\"", portName);
                    break;
                case EIO:
                    throw_IOException_withPortName(env, "Not a serial port: \"%s\"", portName);
                    break;
                default:
                    throw_IOException_NativeError(env, "open");
            }
            return;
        }

        // The port is open, but maybe not configured ... setParam and getParam needs this to be set for their field access
        (*env)->SetIntField(env, sps, spsw_fd, fd);

        //check termios structure for separating real serial devices from others
        struct termios settings;
        if (tcgetattr(fd, &settings)) {
            close(fd); //since 2.7.0
            (*env)->SetIntField(env, sps, spsw_fd, INVALID_FD);
            switch (errno) {
                case ENOTTY:
                    throw_IOException_withPortName(env, "Not a serial port: \"%s\"", portName);
                    break;
                default:
                    throw_IOException_NativeError(env, "open tcgetattr");
            }
            return;
        }

        // Yes we use this port exclusively
        if (ioctl(fd, TIOCEXCL) != 0) {
            close(fd);
            (*env)->SetIntField(env, sps, spsw_fd, INVALID_FD);
            throw_IOException_NativeError(env, "Can't set exclusive access");
            return;
        }

        //set basic settings
        settings.c_cflag |= (CREAD | CLOCAL);
        settings.c_lflag = 0;
        /* Raw input*/
        settings.c_iflag = 0;
        /* Raw output */
        settings.c_oflag = 0;
        settings.c_cc[VMIN] = 0; // If there is not anything just pass
        settings.c_cc[VTIME] = 0; // No timeout

        if (paramBitSet != SPSW_NO_PARAMS_TO_SET) {
            //set Speed etc.
            if (setParams(env, sps, &settings, paramBitSet)) {
                close(fd);
                (*env)->SetIntField(env, sps, spsw_fd, INVALID_FD);
                return;
            }
        } else {

            if (tcsetattr(fd, TCSANOW, &settings) != 0) {
                close(fd);
                (*env)->SetIntField(env, sps, spsw_fd, INVALID_FD);
                throw_IOException_NativeError(env, "Can't call tcsetattr TCSANOW");
                return;
            }
        }

        // flush the device
        if (tcflush(fd, TCIOFLUSH) != 0) {
            close(fd);
            (*env)->SetIntField(env, sps, spsw_fd, INVALID_FD);
            throw_IOException_NativeError(env, "Can't flush device");
            return;
        }

        //on linux to avoid read/close problem maybe this helps?

#ifdef HAVE_SYS_EVENTFD_H
        int close_event_fd = eventfd(0, EFD_NONBLOCK); //counter is zero so nothing to read is available
        if (close_event_fd == INVALID_FD) {
            close(fd);
            throw_IOException_NativeError(env, "Can't create close_event_fd");
            return;
        }

        (*env)->SetIntField(env, sps, spsw_closeEventReadFd, close_event_fd);
        (*env)->SetIntField(env, sps, spsw_closeEventWriteFd, close_event_fd);
#else
        int pipedes[2];
        if (pipe(pipedes)) {
            close(fd);
            throw_IOException_NativeError(env, "Can't create close_pipe_fd");
            return;
        }
        (*env)->SetIntField(env, sps, spsw_closeEventReadFd, pipedes[0]);
        (*env)->SetIntField(env, sps, spsw_closeEventWriteFd, pipedes[1]);
#endif
    }

    /*
     * Class:     de_ibapl_spsw_jniprovider_GenericTermiosSerialPortSocket
     * Method:    drainOutputBuffer
     * Signature: ()V
     */
    JNIEXPORT void JNICALL Java_de_ibapl_spsw_jniprovider_GenericTermiosSerialPortSocket_drainOutputBuffer
    (JNIEnv *env, jobject sps) {

        const int pollTimeout = (*env)->GetIntField(env, sps, spsw_pollWriteTimeout);
        struct pollfd fds[2];
        fds[0].fd = (*env)->GetIntField(env, sps, spsw_fd);
        fds[0].events = POLLOUT;
        fds[1].fd = (*env)->GetIntField(env, sps, spsw_closeEventReadFd);
        fds[1].events = POLLIN;

        int poll_result = poll(&fds[0], 2, pollTimeout);

        if (poll_result == 0) {
            //Timeout
            throw_TimeoutIOException(env, 0, "Timeout drainOutputBuffer");
            return;
        } else if ((poll_result < 0)) {
            throw_IOException_NativeError(env, "drainOutputBuffer poll: Error during poll");
            return;
        } else {
            if (fds[1].revents == POLLIN) {
                //we can read from close_event_ds => port is closing
                throw_AsynchronousCloseException(env);
                return;
            } else if (fds[0].revents == POLLOUT) {
                //Happy path all is right... no-op
            } else {
                // closed?
                throw_ClosedOrNativeException(env, sps, "drainOutputBuffer poll => : received unexpected event and port not closed");
                return;
            }
        }

        int result = tcdrain(fds[0].fd);
        if (result != 0) {
            throw_ClosedOrNativeException(env, sps, "Can't drain the output buffer");
        }
    }


#ifdef __cplusplus
}
#endif    