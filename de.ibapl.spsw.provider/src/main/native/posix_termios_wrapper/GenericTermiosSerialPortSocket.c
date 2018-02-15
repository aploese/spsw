/*-
 * #%L
 * SPSW Provider
 * %%
 * Copyright (C) 2009 - 2017 Arne Plöse
 * %%
 * SPSW - Drivers for the serial port, https://github.com/aploese/spsw/
 * Copyright (C) 2009-2018, Arne Plöse and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
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
 * #L%
 */

/* jSSC (Java Simple Serial Connector) - serial port communication library.
 * © Alexey Sokolov (scream3r), 2010-2014.
 *
 * This file is part of jSSC.
 *
 * jSSC is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * jSSC is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with jSSC.  If not, see <http://www.gnu.org/licenses/>.
 *
 * If you use jSSC in public project you can inform me about this by e-mail,
 * of course if you want it.
 *
 * e-mail: scream3r.org@gmail.com
 * web-site: http://scream3r.org | http://code.google.com/p/java-simple-serial-connector/
 */
#include <stdlib.h>

#include <stdio.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <termios.h>
#include <unistd.h>
#include <string.h>

#include <errno.h>//-D_TS_ERRNO use for Solaris C++ compiler


#ifdef __linux__
#include <linux/serial.h>
#endif
#ifdef __SunOS
#include <sys/filio.h>//Needed for FIONREAD in Solaris
#include <string.h>//Needed for select() function
#endif
#ifdef __APPLE__
#include <serial/ioss.h>//Needed for IOSSIOSPEED in Mac OS X (Non standard baudrate)
#endif

#include <poll.h>

#include <jni.h>

#include "de_ibapl_spsw_provider_GenericTermiosSerialPortSocket.h"

#undef SPSW_PORT_MODE_UNCHANGED
#define SPSW_PORT_MODE_UNCHANGED de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_PORT_MODE_UNCHANGED
#undef SPSW_PORT_MODE_RAW
#define SPSW_PORT_MODE_RAW de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_PORT_MODE_RAW

#undef SPSW_FLOW_CONTROL_NONE
#define SPSW_FLOW_CONTROL_NONE de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_FLOW_CONTROL_NONE
#undef SPSW_FLOW_CONTROL_RTS_CTS_IN
#define SPSW_FLOW_CONTROL_RTS_CTS_IN de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_FLOW_CONTROL_RTS_CTS_IN
#undef SPSW_FLOW_CONTROL_RTS_CTS_OUT
#define SPSW_FLOW_CONTROL_RTS_CTS_OUT de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_FLOW_CONTROL_RTS_CTS_OUT
#undef SPSW_FLOW_CONTROL_XON_XOFF_IN
#define SPSW_FLOW_CONTROL_XON_XOFF_IN de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_FLOW_CONTROL_XON_XOFF_IN
#undef SPSW_FLOW_CONTROL_XON_XOFF_OUT
#define SPSW_FLOW_CONTROL_XON_XOFF_OUT de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_FLOW_CONTROL_XON_XOFF_OUT

#undef SPSW_STOP_BITS_1
#define SPSW_STOP_BITS_1 de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_STOP_BITS_1
#undef SPSW_STOP_BITS_1_5
#define SPSW_STOP_BITS_1_5 de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_STOP_BITS_1_5
#undef SPSW_STOP_BITS_2
#define SPSW_STOP_BITS_2 de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_STOP_BITS_2

#undef SPSW_PARITY_NONE
#define SPSW_PARITY_NONE de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_PARITY_NONE
#undef SPSW_PARITY_ODD
#define SPSW_PARITY_ODD de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_PARITY_ODD
#undef SPSW_PARITY_EVEN
#define SPSW_PARITY_EVEN de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_PARITY_EVEN
#undef SPSW_PARITY_MARK
#define SPSW_PARITY_MARK de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_PARITY_MARK
#undef SPSW_PARITY_SPACE
#define SPSW_PARITY_SPACE de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_PARITY_SPACE

#undef INVALID_FD
#define INVALID_FD -1

jfieldID spsw_portName; /* id for field 'portName'  */
jfieldID spsw_fd; /* id for field 'fd'  */
jfieldID spsw_pollReadTimeout; // id for field overallReadTimeout
jfieldID spsw_pollWriteTimeout; // id for field overallWriteTimeout
jclass genericTermiosSerialPortSocket;
jclass serialPortSocketFactoryImpl;

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved) {
    JNIEnv *env;
    jint getEnvResult = ((*jvm)->GetEnv(jvm, (void **) &env, JNI_VERSION_1_2));
    if (getEnvResult != JNI_OK) {
        return getEnvResult;
    }
    genericTermiosSerialPortSocket = (*env)->FindClass(env, "Lde/ibapl/spsw/provider/GenericTermiosSerialPortSocket;");
    spsw_fd = (*env)->GetFieldID(env, genericTermiosSerialPortSocket, "fd", "I");
    spsw_pollReadTimeout = (*env)->GetFieldID(env, genericTermiosSerialPortSocket, "pollReadTimeout", "I");
    spsw_pollWriteTimeout = (*env)->GetFieldID(env, genericTermiosSerialPortSocket, "pollWriteTimeout", "I");
    spsw_portName = (*env)->GetFieldID(env, genericTermiosSerialPortSocket, "portName", "Ljava/lang/String;");

    // mark that the lib was loaded
    serialPortSocketFactoryImpl = (*env)->FindClass(env, "Lde/ibapl/spsw/provider/SerialPortSocketFactoryImpl;");
    jfieldID spsw_libLoaded = (*env)->GetStaticFieldID(env, serialPortSocketFactoryImpl, "libLoaded", "Z");
    (*env)->SetStaticBooleanField(env, serialPortSocketFactoryImpl, spsw_libLoaded, JNI_TRUE);
    return JNI_VERSION_1_2;
}

JNIEXPORT void JNICALL JNI_OnUnLoad(JavaVM *jvm, void *reserved) {
    JNIEnv *env;
    spsw_fd = 0;
    spsw_pollReadTimeout = 0;
    spsw_pollWriteTimeout = 0;
    spsw_portName = 0;

    jint getEnvResult = ((*jvm)->GetEnv(jvm, (void **) &env, JNI_VERSION_1_2));
    if (getEnvResult != JNI_OK) {
        return;
    }

    // mark that the lib was unloaded
    jfieldID spsw_libLoaded = (*env)->GetStaticFieldID(env, serialPortSocketFactoryImpl, "libLoaded", "Z");
    (*env)->SetStaticBooleanField(env, serialPortSocketFactoryImpl, spsw_libLoaded, JNI_FALSE);

    (*env)->DeleteLocalRef(env, genericTermiosSerialPortSocket);
    (*env)->DeleteLocalRef(env, serialPortSocketFactoryImpl);
}



// Helper method

static void throw_SerialPortException_NativeError(JNIEnv *env, const char *msg) {
    char buf[2048];
    snprintf(buf, 2048, "%s: Unknown port error %d: (%s)", msg, errno, strerror(errno));
    const jclass speClass = (*env)->FindClass(env, "de/ibapl/spsw/api/SerialPortException");
    if (speClass != NULL) {
        (*env)->ThrowNew(env, speClass, buf);
        (*env)->DeleteLocalRef(env, speClass);
    }
}

static void throw_Illegal_Argument_Exception(JNIEnv *env, const char *msg) {
    const jclass cls = (*env)->FindClass(env, "java/lang/IllegalArgumentException");
    if (cls != NULL) {
        (*env)->ThrowNew(env, cls, msg);
        (*env)->DeleteLocalRef(env, cls);
    }
}

static void throw_PortBusyException(JNIEnv *env, jstring portName) {
    const char* port = (*env)->GetStringUTFChars(env, portName, JNI_FALSE);
    const jclass cls = (*env)->FindClass(env, "de/ibapl/spsw/api/PortBusyException");
    if (cls != NULL) {
        (*env)->ThrowNew(env, cls, port);
        (*env)->DeleteLocalRef(env, cls);
    }
    (*env)->ReleaseStringUTFChars(env, portName, port);
}

static void throw_PortNotFoundException(JNIEnv *env, jstring portName) {
    const char* port = (*env)->GetStringUTFChars(env, portName, JNI_FALSE);
    const jclass cls = (*env)->FindClass(env, "de/ibapl/spsw/api/PortNotFoundException");
    if (cls != NULL) {
        (*env)->ThrowNew(env, cls, port);
        (*env)->DeleteLocalRef(env, cls);
    }
    (*env)->ReleaseStringUTFChars(env, portName, port);
}

static void throw_PermissionDeniedException(JNIEnv *env, jstring portName) {
    const char* port = (*env)->GetStringUTFChars(env, portName, JNI_FALSE);
    const jclass cls = (*env)->FindClass(env, "de/ibapl/spsw/api/PermissionDeniedException");
    if (cls != NULL) {
        (*env)->ThrowNew(env, cls, port);
        (*env)->DeleteLocalRef(env, cls);
    }
    (*env)->ReleaseStringUTFChars(env, portName, port);
}

static void throw_NotASerialPortException(JNIEnv *env, jstring portName) {
    const char* port = (*env)->GetStringUTFChars(env, portName, JNI_FALSE);
    const jclass cls = (*env)->FindClass(env, "de/ibapl/spsw/api/NotASerialPortException");
    if (cls != NULL) {
        (*env)->ThrowNew(env, cls, port);
        (*env)->DeleteLocalRef(env, cls);
    }
    (*env)->ReleaseStringUTFChars(env, portName, port);
}

static void throw_SerialPortException_Closed(JNIEnv *env) {
    const jclass speClass = (*env)->FindClass(env, "de/ibapl/spsw/api/SerialPortException");
    if (speClass != NULL) {
        const jfieldID spe_spsc = (*env)->GetStaticFieldID(env, speClass, "SERIAL_PORT_SOCKET_CLOSED", "Ljava/lang/String;");
        const jmethodID speConstructor = (*env)->GetMethodID(env, speClass, "<init>", "(Ljava/lang/String;)V");
        const jobject speEx = (*env)->NewObject(env, speClass, speConstructor, (*env)->GetStaticObjectField(env, speClass, spe_spsc));
        (*env)->Throw(env, speEx);
        (*env)->DeleteLocalRef(env, speClass);
    }
}

static void throw_InterruptedIOExceptionWithError(JNIEnv *env, int bytesTransferred, const char *msg) {
    char buf[2048];
    snprintf(buf, 2048, "%s: Unknown port error %d: (%s)", msg, errno, strerror(errno));
    const jclass iioeClass = (*env)->FindClass(env, "java/io/InterruptedIOException");
    if (iioeClass != NULL) {
        const jmethodID iioeConstructor = (*env)->GetMethodID(env, iioeClass, "<init>", "(Ljava/lang/String;)V");
        const jobject iioeEx = (*env)->NewObject(env, iioeClass, iioeConstructor, (*env)->NewStringUTF(env, buf));
        const jfieldID bytesTransferredId = (*env)->GetFieldID(env, iioeClass, "bytesTransferred", "I");
        (*env)->SetIntField(env, iioeEx, bytesTransferredId, bytesTransferred);
        (*env)->Throw(env, iioeEx);
        (*env)->DeleteLocalRef(env, iioeClass);
    }
}

static void throw_TimeoutIOException(JNIEnv *env, int bytesTransferred) {
    const jclass tioeClass = (*env)->FindClass(env, "de/ibapl/spsw/api/TimeoutIOException");
    if (tioeClass != NULL) {
        const jmethodID tioeConstructor = (*env)->GetMethodID(env, tioeClass, "<init>", "(Ljava/lang/String;)V");
        const jobject tioeEx = (*env)->NewObject(env, tioeClass, tioeConstructor, (*env)->NewStringUTF(env, "Timeout"));
        const jfieldID bytesTransferredId = (*env)->GetFieldID(env, tioeClass, "bytesTransferred", "I");
        (*env)->SetIntField(env, tioeEx, bytesTransferredId, bytesTransferred);
        (*env)->Throw(env, tioeEx);
        (*env)->DeleteLocalRef(env, tioeClass);
    }
}

static void throw_ClosedOrNativeException(JNIEnv *env, jobject object, const char *message) {
    if ((*env)->GetIntField(env, object, spsw_fd) == INVALID_FD) {
        throw_SerialPortException_Closed(env);
    } else {
        throw_SerialPortException_NativeError(env, message);
    }
}

static jboolean getLineStatus(JNIEnv *env, jobject object, int bitMask) {
    int fd = (*env)->GetIntField(env, object, spsw_fd);
    int lineStatus;
    if (ioctl(fd, TIOCMGET, &lineStatus) != 0) {
        throw_ClosedOrNativeException(env, object, "Can't get line status");
        return JNI_FALSE;
    }
    if ((lineStatus & bitMask) == bitMask) {
        return JNI_TRUE;
    } else {
        return JNI_FALSE;
    }
}

static void setLineStatus(JNIEnv *env, jobject object, jboolean enabled, int bitMask) {
    int fd = (*env)->GetIntField(env, object, spsw_fd);
    int lineStatus;
    if (ioctl(fd, TIOCMGET, &lineStatus) != 0) {
        throw_ClosedOrNativeException(env, object, "Can't get line status");
        return;
    }
    if (enabled == JNI_TRUE) {
        lineStatus |= bitMask;
    } else {
        lineStatus &= ~bitMask;
    }
    if (ioctl(fd, TIOCMSET, &lineStatus) != 0) {
        throw_ClosedOrNativeException(env, object, "Can't set line status");
    }
}

static jint speed_t2Baudrate(JNIEnv *env, speed_t baudRate) {
    switch (baudRate) {
        case B0:
            return 0;
        case B50:
            return 50;
        case B75:
            return 75;
        case B110:
            return 110;
        case B134:
            return 134;
        case B150:
            return 150;
        case B200:
            return 200;
        case B300:
            return 300;
        case B600:
            return 600;
        case B1200:
            return 1200;
        case B1800:
            return 1800;
        case B2400:
            return 2400;
        case B4800:
            return 4800;
        case B9600:
            return 9600;
        case B19200:
            return 19200;
        case B38400:
            return 38400;
#ifdef B57600
        case B57600:
            return 57600;
#endif
#ifdef B115200
        case B115200:
            return 115200;
#endif
#ifdef B230400
        case B230400:
            return 230400;
#endif
#ifdef B460800
        case B460800:
            return 460800;
#endif

#ifdef B500000
        case B500000:
            return 500000;
#endif
#ifdef B576000
        case B576000:
            return 576000;
#endif
#ifdef B921600
        case B921600:
            return 921600;
#endif
#ifdef B1000000
        case B1000000:
            return 1000000;
#endif

#ifdef B1152000
        case B1152000:
            return 1152000;
#endif
#ifdef B1500000
        case B1500000:
            return 1500000;
#endif
#ifdef B2000000
        case B2000000:
            return 2000000;
#endif
#ifdef B2500000
        case B2500000:
            return 2500000;
#endif

#ifdef B3000000
        case B3000000:
            return 3000000;
#endif
#ifdef B3500000
        case B3500000:
            return 3500000;
#endif
#ifdef B4000000
        case B4000000:
            return 4000000;
#endif
        default:
            throw_Illegal_Argument_Exception(env, "Baudrate not supported");
            return -1;
    }
}

static speed_t baudrate2speed_t(JNIEnv *env, jint baudRate) {
    switch (baudRate) {
        case 0:
            return B0;
        case 50:
            return B50;
        case 75:
            return B75;
        case 110:
            return B110;
        case 134:
            return B134;
        case 150:
            return B150;
        case 200:
            return B200;
        case 300:
            return B300;
        case 600:
            return B600;
        case 1200:
            return B1200;
        case 1800:
            return B1800;
        case 2400:
            return B2400;
        case 4800:
            return B4800;
        case 9600:
            return B9600;
        case 19200:
            return B19200;
        case 38400:
            return B38400;
#ifdef B57600
        case 57600:
            return B57600;
#endif
#ifdef B115200
        case 115200:
            return B115200;
#endif
#ifdef B230400
        case 230400:
            return B230400;
#endif
#ifdef B460800
        case 460800:
            return B460800;
#endif

#ifdef B500000
        case 500000:
            return B500000;
#endif
#ifdef B576000
        case 576000:
            return B576000;
#endif
#ifdef B921600
        case 921600:
            return B921600;
#endif
#ifdef B1000000
        case 1000000:
            return B1000000;
#endif

#ifdef B1152000
        case 1152000:
            return B1152000;
#endif
#ifdef B1500000
        case 1500000:
            return B1500000;
#endif
#ifdef B2000000
        case 2000000:
            return B2000000;
#endif
#ifdef B2500000
        case 2500000:
            return B2500000;
#endif

#ifdef B3000000
        case 3000000:
            return B3000000;
#endif
#ifdef B3500000
        case 3500000:
            return B3500000;
#endif
#ifdef B4000000
        case 4000000:
            return B4000000;
#endif
        default:
            throw_Illegal_Argument_Exception(env, "Baudrate not supported");
            return -1;
    }
}

/*
 * Port opening
 * 
 * In 2.2.0 added useTIOCEXCL
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_open(JNIEnv *env, jobject object, jstring portName, jint portMode) {

    //Do not try to reopen port and therefor failing and overriding the filedescriptor
    if ((*env)->GetIntField(env, object, spsw_fd) != INVALID_FD) {
        throw_SerialPortException_NativeError(env, "serial port socket has valid filedescriptor!");
        return;
    }

    const char* port = (*env)->GetStringUTFChars(env, portName, JNI_FALSE);
    int fd = open(port, O_RDWR | O_NOCTTY | O_NDELAY);

    (*env)->ReleaseStringUTFChars(env, portName, port);

    if (fd == INVALID_FD) {
        (*env)->SetIntField(env, object, spsw_fd, INVALID_FD);
        switch (errno) {
            case EBUSY:
                throw_PortBusyException(env, portName);
                break;
            case ENOENT:
                throw_PortNotFoundException(env, portName);
                break;
            case EACCES:
                throw_PermissionDeniedException(env, portName);
                break;
            case EIO:
                throw_NotASerialPortException(env, portName);
                break;
            default:
                throw_SerialPortException_NativeError(env, "open");
        }
        return;
    }

    //check termios structure for separating real serial devices from others
    struct termios settings;
    if (tcgetattr(fd, &settings) != 0) {
        close(fd); //since 2.7.0
        (*env)->SetIntField(env, object, spsw_fd, INVALID_FD);
        switch (errno) {
            case EIO:
                throw_NotASerialPortException(env, portName);
                break;
            default:
                throw_SerialPortException_NativeError(env, "open tcgetattr");
        }
        return;
    }

    // Yes we use this port exclusively    
    if (ioctl(fd, TIOCEXCL) != 0) {
        close(fd);
        (*env)->SetIntField(env, object, spsw_fd, INVALID_FD);
        throw_SerialPortException_NativeError(env, "Can't set exclusive access");
        return;
    }

    switch (portMode) {
        case SPSW_PORT_MODE_UNCHANGED:
            break;
        case SPSW_PORT_MODE_RAW:
            settings.c_cflag |= (CREAD | CLOCAL);

            settings.c_lflag = 0;
            /* Raw input*/
            settings.c_iflag = 0;
            /* Raw output */
            settings.c_oflag = 0;
            settings.c_cc[VMIN] = 1; // min 1 char to receive
            settings.c_cc[VTIME] = 1; // 1/10 s inter byte timeout
            if (tcsetattr(fd, TCSANOW, &settings) != 0) {
                close(fd);
                (*env)->SetIntField(env, object, spsw_fd, INVALID_FD);
                throw_SerialPortException_NativeError(env, "Can't call tcsetattr TCSANOW");
                return;
            }
            //Set the overallReadTimeout to infinity 
            (*env)->SetIntField(env, object, spsw_pollReadTimeout, -1);
            //Set the overallWriteTimeout to infinity 
            (*env)->SetIntField(env, object, spsw_pollWriteTimeout, -1);
            break;
        default:
            close(fd);
            (*env)->SetIntField(env, object, spsw_fd, INVALID_FD);
            throw_SerialPortException_NativeError(env, "Unknown terminal mode giving up");
            return;
    }

    // flush the device
    if (tcflush(fd, TCIOFLUSH) != 0) {
        close(fd);
        (*env)->SetIntField(env, object, spsw_fd, INVALID_FD);
        throw_SerialPortException_NativeError(env, "Can't flush device");
        return;
    }


    (*env)->SetIntField(env, object, spsw_fd, fd);
}

/* Closing the port */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_close0
(JNIEnv *env, jobject object) {

    int fd = (*env)->GetIntField(env, object, spsw_fd);
    (*env)->SetIntField(env, object, spsw_fd, INVALID_FD);

    struct termios settings;
    if (tcgetattr(fd, &settings) != 0) {
        //        perror("NATIVE Error Close - tcgetattr");
    }

    settings.c_cc[VMIN] = 0;
    settings.c_cc[VTIME] = 0;
    if (tcsetattr(fd, TCSANOW, &settings) != 0) {
        //        perror("NATIVE Error Close - tcsetattr");
    }

    if (tcflush(fd, TCIOFLUSH) != 0) {
        //        perror("NATIVE Error Close - tcflush");
    }

    if (close(fd) != 0) {
        throw_SerialPortException_NativeError(env, "close0 closing port");
    }
}

/* 
 * RTS line status changing (ON || OFF)
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_setRTS
(JNIEnv *env, jobject object, jboolean enabled) {
    setLineStatus(env, object, enabled, TIOCM_RTS);
}

JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_sendXON
(JNIEnv *env, jobject object) {
    //TODO How ??? tcflow ?
    throw_Illegal_Argument_Exception(env, "setXON not implementred yet");
}

JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_sendXOFF
(JNIEnv *env, jobject object) {
    throw_Illegal_Argument_Exception(env, "setXOFF not implementred yet");
}

/* 
 * DTR line status changing (ON || OFF)
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_setDTR
(JNIEnv *env, jobject object, jboolean enabled) {
    setLineStatus(env, object, enabled, TIOCM_DTR);
}

/* 
 * BRK line status changing (ON || OFF)
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_setBreak
(JNIEnv *env, jobject object, jboolean enabled) {
    const int fd = (*env)->GetIntField(env, object, spsw_fd);
    int arg;
    if (enabled == JNI_TRUE) {
        arg = TIOCSBRK;
    } else {
        arg = TIOCCBRK;
    }
    if (ioctl(fd, arg) != 0) {
        throw_ClosedOrNativeException(env, object, "Can't set Break");
    }
}

JNIEXPORT void Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_sendBreak
(JNIEnv *env, jobject object, jint duration) {
    const int fd = (*env)->GetIntField(env, object, spsw_fd);
    if (tcsendbreak(fd, duration) != 0) {
        throw_ClosedOrNativeException(env, object, "Can't sendBreak");
    }
}

JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_setXONChar
(JNIEnv *env, jobject object, jchar c) {
    const int fd = (*env)->GetIntField(env, object, spsw_fd);

    struct termios settings;
    if (tcgetattr(fd, &settings) != 0) {
        throw_ClosedOrNativeException(env, object, "setXONChar tcgetattr");
        return;
    }
    settings.c_cc[VSTART] = c;

    if (tcsetattr(fd, TCSANOW, &settings) != 0) {
        //TODO EBADF == errno
        throw_ClosedOrNativeException(env, object, "setXONChar tcsetattr");
    }

}

JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_setXOFFChar
(JNIEnv *env, jobject object, jchar c) {
    const int fd = (*env)->GetIntField(env, object, spsw_fd);

    struct termios settings;
    if (tcgetattr(fd, &settings) != 0) {
        throw_ClosedOrNativeException(env, object, "setXOFFChar tcgetattr");
        return;
    }
    settings.c_cc[VSTOP] = c;

    if (tcsetattr(fd, TCSANOW, &settings) != 0) {
        throw_ClosedOrNativeException(env, object, "setXOFFChar tcsetattr");
    }

}

JNIEXPORT jchar JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_getXONChar
(JNIEnv *env, jobject object) {
    const int fd = (*env)->GetIntField(env, object, spsw_fd);

    struct termios settings;
    if (tcgetattr(fd, &settings) != 0) {
        throw_ClosedOrNativeException(env, object, "getXONChar tcgetattr");
        return 0;
    }
    return settings.c_cc[VSTART];

}

JNIEXPORT jchar JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_getXOFFChar
(JNIEnv *env, jobject object) {
    const int fd = (*env)->GetIntField(env, object, spsw_fd);

    struct termios settings;
    if (tcgetattr(fd, &settings) != 0) {
        throw_ClosedOrNativeException(env, object, "getXOFFChar tcgetattr");
        return 0;
    }
    return settings.c_cc[VSTOP];

}

JNIEXPORT jboolean JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_isRTS
(JNIEnv *env, jobject object) {
    return getLineStatus(env, object, TIOCM_RTS);
}

JNIEXPORT jboolean JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_isCTS
(JNIEnv *env, jobject object) {
    return getLineStatus(env, object, TIOCM_CTS);
}

JNIEXPORT jboolean JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_isDSR
(JNIEnv *env, jobject object) {
    return getLineStatus(env, object, TIOCM_DSR);
}

JNIEXPORT jboolean JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_isDTR
(JNIEnv *env, jobject object) {
    return getLineStatus(env, object, TIOCM_DTR);
}

JNIEXPORT jboolean JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_isIncommingRI
(JNIEnv *env, jobject object) {
    return getLineStatus(env, object, TIOCM_RNG);
}

JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_writeSingle
(JNIEnv *env, jobject object, jint b) {
    const int fd = (*env)->GetIntField(env, object, spsw_fd);
    int written = write(fd, &b, 1);

    if (written == 1) {
        return;
    }
    if (written == 0) {
        //No-op do poll
    } else if (written < 0) {
        if (EAGAIN == errno) {
            //No-op do poll
            written = 0;
        } else {
            if ((*env)->GetIntField(env, object, spsw_fd) == INVALID_FD) {
                throw_SerialPortException_Closed(env);
            } else {
                throw_InterruptedIOExceptionWithError(env, 0, "unknown error writeSingle");
            }
            return;
        }
    }

    const int pollTimeout = (*env)->GetIntField(env, object, spsw_pollWriteTimeout);
    struct pollfd fds;
    fds.fd = fd;
    fds.events = POLLOUT;

    int poll_result = poll(&fds, 1, pollTimeout);

    if (poll_result == 0) {
        //Timeout
        throw_TimeoutIOException(env, written);
        return;
    } else if (poll_result < 0) {
        throw_InterruptedIOExceptionWithError(env, written, "writeSingle poll: Error during poll");
        return;
    } else {
        //Happy path just check if its the right event...
        if (fds.revents == POLLOUT) {
            //Happy path all is right...
        } else {
            if ((*env)->GetIntField(env, object, spsw_fd) == INVALID_FD) {
                throw_SerialPortException_Closed(env);
            } else {
                throw_InterruptedIOExceptionWithError(env, written, "writeSingle error during poll");
            }
            return;
        }
    }

    written = write(fd, &b, 1);
    if (written == 0) {
        throw_TimeoutIOException(env, written);
    } else if (written < 0) {
        if ((*env)->GetIntField(env, object, spsw_fd) == INVALID_FD) {
            throw_SerialPortException_Closed(env);
        } else {
            throw_InterruptedIOExceptionWithError(env, 0, "writeSingle too few bytes written");
        }
    }
}

JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_writeBytes
(JNIEnv *env, jobject object, jbyteArray bytes, jint off, jint len) {

    const int fd = (*env)->GetIntField(env, object, spsw_fd);
    jbyte* buf = (jbyte*) malloc(len);
    (*env)->GetByteArrayRegion(env, bytes, off, len, buf);
    if ((*env)->ExceptionOccurred(env)) {
        free(buf);
        return;
    }
    int written = write(fd, buf, len);
    free(buf);

    if (written == len) {
        //all was written
        return;
    }

    if (written < 0) {
        if (EAGAIN == errno) {
            written = 0;
        } else {
            if ((*env)->GetIntField(env, object, spsw_fd) == INVALID_FD) {
                throw_SerialPortException_Closed(env);
            } else {
                throw_InterruptedIOExceptionWithError(env, 0, "unknown port error  writeBytes");
            }
            return;
        }
    }

    const int pollTimeout = (*env)->GetIntField(env, object, spsw_pollWriteTimeout);
    struct pollfd fds;
    fds.fd = fd;
    fds.events = POLLOUT;

    int poll_result = poll(&fds, 1, pollTimeout);

    if (poll_result == 0) {
        //Timeout
        //Filehandle valid -> a timeout occured
        throw_TimeoutIOException(env, written);
        return;
    } else if ((poll_result < 0)) {
        throw_InterruptedIOExceptionWithError(env, written, "poll timeout with error writeBytes");
        return;
    } else {
        //Happy path just check if its the right event...
        if (fds.revents == POLLOUT) {
            //Happy path all is right...
        } else {
            if ((*env)->GetIntField(env, object, spsw_fd) == INVALID_FD) {
                //Filehandle not valid -> closed.
                throw_SerialPortException_Closed(env);
            } else {
                throw_InterruptedIOExceptionWithError(env, written, "poll timeout with poll event writeBytes");
            }
            return;
        }
    }

    len -= written;
    off += written;

    buf = (jbyte*) malloc(len);
    (*env)->GetByteArrayRegion(env, bytes, off, len, buf);
    if ((*env)->ExceptionOccurred(env)) {
        free(buf);
        return;
    }

    int firstWritten = written;
    written = write(fd, buf, len);
    free(buf);

    if (written < 0) {
        if ((*env)->GetIntField(env, object, spsw_fd) == INVALID_FD) {
            //Filehandle not valid -> closed.
            throw_SerialPortException_Closed(env);
        } else {
            throw_InterruptedIOExceptionWithError(env, firstWritten, "writeBytes after poll no bytes written");
        }
    } else if (written < len) {
        throw_TimeoutIOException(env, firstWritten + written);
    }

}

JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_readSingle
(JNIEnv *env, jobject object) {

    struct pollfd fds;
    fds.fd = (*env)->GetIntField(env, object, spsw_fd);
    fds.events = POLLIN;
    const int pollTimeout = (*env)->GetIntField(env, object, spsw_pollReadTimeout);

    int poll_result = poll(&fds, 1, pollTimeout);

    if (poll_result == 0) {
        //Timeout
        throw_TimeoutIOException(env, 0);
        return -1;
    } else if ((poll_result < 0)) {
        throw_InterruptedIOExceptionWithError(env, 0, "readSingle poll: Error during poll");
        return -1;
    } else {
        //Happy path just check if its the right event...
        if (fds.revents == POLLIN) {
            //Happy path all is right...
        } else {
            //Test if closed
            if ((*env)->GetIntField(env, object, spsw_fd) == INVALID_FD) {
                //Filehandle not valid -> closed.
                return -1;
            }
            throw_InterruptedIOExceptionWithError(env, 0, "readSingle poll: received poll event");
            return -1;
        }
    }

    //OK No timeout and no error, we should read the byte without blocking.
    jbyte lpBuffer;
    jint nread = (jint) read(fds.fd, &lpBuffer, 1);
    if (nread == 1) {
        return lpBuffer & 0xFF;
    }

    if ((*env)->GetIntField(env, object, spsw_fd) == INVALID_FD) {
        //Closed no-op    
    } else {
        throw_InterruptedIOExceptionWithError(env, 0, "readSingle read nothing read and no timeout => Should never happen");
    }
    return -1;
}

JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_readBytes
(JNIEnv *env, jobject object, jbyteArray bytes, jint off, jint len) {

    struct pollfd fds;
    fds.fd = (*env)->GetIntField(env, object, spsw_fd);
    fds.events = POLLIN;
    const int pollTimeout = (*env)->GetIntField(env, object, spsw_pollReadTimeout);

    int poll_result = poll(&fds, 1, pollTimeout);

    if (poll_result == 0) {
        //Timeout
        throw_TimeoutIOException(env, 0);
        return -1;
    } else if ((poll_result < 0)) {
        throw_InterruptedIOExceptionWithError(env, 0, "readBytes poll: Error during poll");
        return -1;
    } else {
        //Happy path just check if its the right event...
        if (fds.revents == POLLIN) {
            //Happy path all is right...
        } else {
            //Test if closed
            if ((*env)->GetIntField(env, object, spsw_fd) == INVALID_FD) {
                //Filehandle not valid -> closed.
                return -1;
            }
            throw_InterruptedIOExceptionWithError(env, 0, "readBytes poll: received poll event");
            return -1;
        }
    }

    //OK No timeout and no error, we should read the byte without blocking.
    jbyte *lpBuffer = (jbyte*) malloc(len);

    jint nread = (jint) read(fds.fd, lpBuffer, len);
    if (nread > 0) {
        (*env)->SetByteArrayRegion(env, bytes, off, nread, lpBuffer);
    } else {
        if ((*env)->GetIntField(env, object, spsw_fd) == INVALID_FD) {
            nread = -1;
        } else if (nread == 0) {
            throw_TimeoutIOException(env, 0); //Is this right???
        } else {
            throw_InterruptedIOExceptionWithError(env, 0, "readBytes read error: Should never happen");
            nread = -1;
        }
    }
    // if (nread == 1) {
    //reread 
    //}
    free(lpBuffer);
    return nread;
}

/* OK */

/*
 * Get bytes count in serial port buffers (Input)
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_getInBufferBytesCount
(JNIEnv *env, jobject object) {
    int fd = (*env)->GetIntField(env, object, spsw_fd);
    jint returnValue = -1;
    int result = ioctl(fd, FIONREAD, &returnValue);
    if (result != 0) {
        throw_ClosedOrNativeException(env, object, "Can't read in buffer size");
    }
    return returnValue;
}

/*
 * Get bytes count in serial port buffers (Output)
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_getOutBufferBytesCount
(JNIEnv *env, jobject object) {
    int fd = (*env)->GetIntField(env, object, spsw_fd);
    jint returnValue = -1;
    int result = ioctl(fd, TIOCOUTQ, &returnValue);
    if (result != 0) {
        throw_ClosedOrNativeException(env, object, "Can't read out buffer size");
    }
    return returnValue;
}

/*
 * write the bytes in the output buffer
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_drainOutputBuffer
(JNIEnv *env, jobject object) {

    const int pollTimeout = (*env)->GetIntField(env, object, spsw_pollWriteTimeout);
    struct pollfd fds;
    fds.fd = (*env)->GetIntField(env, object, spsw_fd);
    fds.events = POLLOUT;

    int poll_result = poll(&fds, 1, pollTimeout);

    if (poll_result == 0) {
        //Timeout
        //Filehandle valid -> a timeout occured
        throw_TimeoutIOException(env, 0);
        return;
    } else if ((poll_result < 0)) {
        throw_SerialPortException_NativeError(env, "drainOutputBuffer poll: Error during poll");
        return;
    } else {
        if (fds.revents == POLLOUT) {
            //Happy path all is right... no-op
        } else {
            // closed?
            throw_ClosedOrNativeException(env, object, "drainOutputBuffer poll => : received unexpected event and port not closed");
            return;
        }
    }

    int result = tcdrain(fds.fd);
    if (result != 0) {
        throw_ClosedOrNativeException(env, object, "Can't drain the output buffer");
    }
}

/*
 * Setting flow control mode
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_setFlowControl
(JNIEnv *env, jobject object, jint mask) {

    int fd = (*env)->GetIntField(env, object, spsw_fd);
    struct termios settings;
    if (tcgetattr(fd, &settings) != 0) {
        throw_ClosedOrNativeException(env, object, "setFlowControl tcgetattr");
        return;
    }

    settings.c_cflag &= ~CRTSCTS;
    settings.c_iflag &= ~(IXON | IXOFF);
    if (mask != SPSW_FLOW_CONTROL_NONE) {
        if (((mask & SPSW_FLOW_CONTROL_RTS_CTS_IN) == SPSW_FLOW_CONTROL_RTS_CTS_IN) ||
                ((mask & SPSW_FLOW_CONTROL_RTS_CTS_OUT) == SPSW_FLOW_CONTROL_RTS_CTS_OUT)) {
            settings.c_cflag |= CRTSCTS;
        }
        if ((mask & SPSW_FLOW_CONTROL_XON_XOFF_IN) == SPSW_FLOW_CONTROL_XON_XOFF_IN) {
            settings.c_iflag |= IXOFF;
        }
        if ((mask & SPSW_FLOW_CONTROL_XON_XOFF_OUT) == SPSW_FLOW_CONTROL_XON_XOFF_OUT) {
            settings.c_iflag |= IXON;
        }
    }

    if (tcsetattr(fd, TCSANOW, &settings) != 0) {
        if ((*env)->GetIntField(env, object, spsw_fd) == INVALID_FD) {
            throw_SerialPortException_Closed(env);
        } else {
            throw_SerialPortException_NativeError(env, "setFlowControl tcsetattr");
        }
    }
}

/*
 * Class:     de_ibapl_spsw_provider_GenericTermiosSerialPortSocket
 * Method:    setBaudrate
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_setBaudrate
(JNIEnv *env, jobject object, jint baudRate) {
    speed_t baudRateValue = baudrate2speed_t(env, baudRate);
    if (baudRateValue == -1) {
        return;
    }

    struct termios settings;
    int fd = (*env)->GetIntField(env, object, spsw_fd);
    if (tcgetattr(fd, &settings) != 0) {
        throw_ClosedOrNativeException(env, object, "setBaudrate tcgetattr");
        return;
    }

    //Set standart baudrate from "termios.h"
    if (cfsetspeed(&settings, baudRateValue) < 0) {
        throw_ClosedOrNativeException(env, object, "Baudrate of in and output mismatch");
        return;
    }

    if (tcsetattr(fd, TCSANOW, &settings) != 0) {
        throw_ClosedOrNativeException(env, object, "set Baudrate tcsetattr");
        return;
    }
}

/*
 * Class:     de_ibapl_spsw_provider_GenericTermiosSerialPortSocket
 * Method:    setDataBits
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_setDataBits
(JNIEnv *env, jobject object, jint dataBits) {
    int fd = (*env)->GetIntField(env, object, spsw_fd);
    struct termios settings;
    if (tcgetattr(fd, &settings) != 0) {
        throw_ClosedOrNativeException(env, object, "setDataBits tcgetattr");
        return;
    }

    settings.c_cflag &= ~CSIZE;
    switch (dataBits) {
        case 5:
            settings.c_cflag |= CS5;
            break;
        case 6:
            settings.c_cflag |= CS6;
            break;
        case 7:
            settings.c_cflag |= CS7;
            break;
        case 8:
            settings.c_cflag |= CS8;
            break;
        default:
            throw_Illegal_Argument_Exception(env, "Wrong databits");
            return;
    }

    if (tcsetattr(fd, TCSANOW, &settings) != 0) {
        throw_ClosedOrNativeException(env, object, "setDataBits tcsetattr");
    }
}

/*
 * Class:     de_ibapl_spsw_provider_GenericTermiosSerialPortSocket
 * Method:    setStopBits
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_setStopBits
(JNIEnv *env, jobject object, jint stopBits) {
    int fd = (*env)->GetIntField(env, object, spsw_fd);
    struct termios settings;
    if (tcgetattr(fd, &settings) != 0) {
        throw_ClosedOrNativeException(env, object, "setStopBits tcgetattr");
        return;
    }

    switch (stopBits) {
        case SPSW_STOP_BITS_1:
            //1 stop bit (for info see ->> MSDN)
            settings.c_cflag &= ~CSTOPB;
            break;
        case SPSW_STOP_BITS_1_5:
            throw_Illegal_Argument_Exception(env, "setStopBits 1.5 stop bits are not supported by termios");
            return;
            break;
        case SPSW_STOP_BITS_2:
            settings.c_cflag |= CSTOPB;
            break;
        default:
            throw_SerialPortException_NativeError(env, "Unknown stopbits");
            return;
    }

    if (tcsetattr(fd, TCSANOW, &settings) != 0) {
        throw_ClosedOrNativeException(env, object, "setStopBits tcsetattr");
    }
}

/*
 * Class:     de_ibapl_spsw_provider_GenericTermiosSerialPortSocket
 * Method:    setParity
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_setParity
(JNIEnv *env, jobject object, jint parity) {
    int fd = (*env)->GetIntField(env, object, spsw_fd);
    struct termios settings;
    if (tcgetattr(fd, &settings) != 0) {
        throw_ClosedOrNativeException(env, object, "setParity tcgetattr");
        return;
    }

#ifdef PAREXT
    settings.c_cflag &= ~(PARENB | PARODD | PAREXT); //Clear parity settings
#elif defined CMSPAR
    settings.c_cflag &= ~(PARENB | PARODD | CMSPAR); //Clear parity settings
#else
    settings.c_cflag &= ~(PARENB | PARODD); //Clear parity settings
#endif
    switch (parity) {
        case SPSW_PARITY_NONE:
            //Parity NONE
            settings.c_iflag &= ~INPCK; // switch parity input checking off
            break;
        case SPSW_PARITY_ODD:
            //Parity ODD
            settings.c_cflag |= (PARENB | PARODD);
            settings.c_iflag |= INPCK; // switch parity input checking On 
            break;
        case SPSW_PARITY_EVEN:
            //Parity EVEN
            settings.c_cflag |= PARENB;
            settings.c_iflag |= INPCK;
            break;
        case SPSW_PARITY_MARK:
            //Parity MARK
#ifdef PAREXT
            settings.c_cflag |= (PARENB | PARODD | PAREXT);
            settings.c_iflag |= INPCK;
#elif defined CMSPAR
            settings.c_cflag |= (PARENB | PARODD | CMSPAR);
            settings.c_iflag |= INPCK;
#endif
            break;
        case SPSW_PARITY_SPACE:
            //Parity SPACE
#ifdef PAREXT
            settings.c_cflag |= (PARENB | PAREXT);
            settings.c_iflag |= INPCK;
#elif defined CMSPAR
            settings.c_cflag |= (PARENB | CMSPAR);
            settings.c_iflag |= INPCK;
#endif
            break;
        default:
            throw_Illegal_Argument_Exception(env, "Wrong Parity");
            return;
    }

    if (tcsetattr(fd, TCSANOW, &settings) != 0) {
        throw_ClosedOrNativeException(env, object, "setParity tcsetattr");
    }
}

/*
 * Class:     de_ibapl_spsw_provider_GenericTermiosSerialPortSocket
 * Method:    getBaudrate0
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_getBaudrate0
(JNIEnv *env, jobject object) {
    int fd = (*env)->GetIntField(env, object, spsw_fd);
    struct termios settings;
    if (tcgetattr(fd, &settings) != 0) {
        throw_ClosedOrNativeException(env, object, "getBaudRate tcgetattr");
        return -1;
    }
    //Set standart baudrate from "termios.h"
    speed_t inSpeed = cfgetispeed(&settings);
    speed_t outSpeed = cfgetospeed(&settings);
    if (inSpeed != outSpeed) {
        throw_ClosedOrNativeException(env, object, "In and out speed mismatch");
        return -1;
    }
    return speed_t2Baudrate(env, inSpeed);
}

/*
 * Class:     de_ibapl_spsw_provider_GenericTermiosSerialPortSocket
 * Method:    getDataBits0
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_getDataBits0
(JNIEnv *env, jobject object) {
    int fd = (*env)->GetIntField(env, object, spsw_fd);
    struct termios settings;
    if (tcgetattr(fd, &settings) != 0) {
        throw_ClosedOrNativeException(env, object, "getDataBits tcgetattr");
        return -1;
    }
    switch (settings.c_cflag & CSIZE) {
        case CS5:
            return 5;
        case CS6:
            return 6;
        case CS7:
            return 7;
        case CS8:
            return 8;
        default:
            throw_Illegal_Argument_Exception(env, "Unknown databits");
            return (jint) - 1;
    }
    return -1;
}

/*
 * Class:     de_ibapl_spsw_provider_GenericTermiosSerialPortSocket
 * Method:    getStopBits0
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_getStopBits0
(JNIEnv *env, jobject object) {
    int fd = (*env)->GetIntField(env, object, spsw_fd);
    struct termios settings;
    if (tcgetattr(fd, &settings) != 0) {
        throw_ClosedOrNativeException(env, object, "getStopBits tcgetattr");
        return -1;
    }

    if ((settings.c_cflag & CSTOPB) == 0) {
        return SPSW_STOP_BITS_1;
    }
    if ((settings.c_cflag & CSTOPB) == CSTOPB) {
        return SPSW_STOP_BITS_2;
    }
}

/*
 * Class:     de_ibapl_spsw_provider_GenericTermiosSerialPortSocket
 * Method:    getParity0
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_getParity0
(JNIEnv *env, jobject object) {
    int fd = (*env)->GetIntField(env, object, spsw_fd);
    struct termios settings;
    if (tcgetattr(fd, &settings) != 0) {
        throw_ClosedOrNativeException(env, object, "getParity tcgetattr");
        return -1;
    }

    if ((settings.c_cflag & PARENB) == 0) {
        return SPSW_PARITY_NONE;
    } else if ((settings.c_cflag & PARODD) == 0) {
        // EVEN or SPACE
#ifdef PAREXT
        if ((settings.c_cflag & PAREXT) == 0) {
            return SPSW_PARITY_EVEN;
        } else {
            return SPSW_PARITY_SPACE;
        }
#elif defined CMSPAR
        if ((settings.c_cflag & CMSPAR) == 0) {
            return SPSW_PARITY_EVEN;
        } else {
            return SPSW_PARITY_SPACE;
        }
#endif
    } else {
        // ODD or MARK
#ifdef PAREXT
        if ((settings.c_cflag & PAREXT) == 0) {
            return SPSW_PARITY_ODD;
        } else {
            return SPSW_PARITY_MARK;
        }
#elif defined CMSPAR
        if ((settings.c_cflag & CMSPAR) == 0) {
            return SPSW_PARITY_ODD;
        } else {
            return SPSW_PARITY_MARK;
        }
#endif
    }
    throw_SerialPortException_NativeError(env, "getParity0 Wrong Parity");
    return -1;
}

/*
 * Getting flow control mode
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_getFlowControl0
(JNIEnv *env, jobject object) {
    int fd = (*env)->GetIntField(env, object, spsw_fd);
    jint returnValue = 0;
    struct termios settings;
    if (tcgetattr(fd, &settings) != 0) {
        throw_ClosedOrNativeException(env, object, "getFlowControl tcgetattr");
        return -1;
    }

    if (settings.c_cflag & CRTSCTS) {
        returnValue |= (SPSW_FLOW_CONTROL_RTS_CTS_IN | SPSW_FLOW_CONTROL_RTS_CTS_OUT);
    }
    if (settings.c_iflag & IXOFF) {
        returnValue |= SPSW_FLOW_CONTROL_XON_XOFF_IN;
    }
    if (settings.c_iflag & IXON) {
        returnValue |= SPSW_FLOW_CONTROL_XON_XOFF_OUT;
    }
    return returnValue;
}


// Timeouts

/*
 * Class:     de_ibapl_spsw_provider_GenericTermiosSerialPortSocket
 * Method:    setTimeouts
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_setTimeouts
(JNIEnv *env, jobject object, jint interByteReadTimeout, jint overallReadTimeout, jint overallWriteTimeout) {
    int fd = (*env)->GetIntField(env, object, spsw_fd);
    struct termios settings;

    if (tcgetattr(fd, &settings) != 0) {
        throw_ClosedOrNativeException(env, object, "setTimeouts tcgetattr");
        return;
    }

    if (interByteReadTimeout == 0) {
        settings.c_cc[VMIN] = 1; // this marks VTIME as overall timeout 
        settings.c_cc[VTIME] = 0; // and this do block infinite
    } else if (interByteReadTimeout > 0) {
        //But block if bytes are comming in ...
        settings.c_cc[VMIN] = 1; // this marks VTIME as overall timeout
        if (interByteReadTimeout > 25500) {
            throw_Illegal_Argument_Exception(env, "setTimeouts: interByteReadTimeout must not > 2550ms");
            return;
        }
        settings.c_cc[VTIME] = interByteReadTimeout / 100; // the  inter byte timeout in tenths of seconds
        if (settings.c_cc[VTIME] == 0) {
            // have at least a tenth of a second ...
            settings.c_cc[VTIME] = 1;
        }
    } else {
        throw_Illegal_Argument_Exception(env, "setTimeouts: interByteReadTimeout : timeout must not < 0");
        return;
    }

    if (overallReadTimeout == 0) {
        (*env)->SetIntField(env, object, spsw_pollReadTimeout, -1);
    } else if (overallReadTimeout > 0) {
        (*env)->SetIntField(env, object, spsw_pollReadTimeout, overallReadTimeout);
    } else {
        throw_Illegal_Argument_Exception(env, "setTimeouts: overallReadTimeout must not < 0");
        return;
    }

    if (overallWriteTimeout == 0) {
        (*env)->SetIntField(env, object, spsw_pollWriteTimeout, -1);
    } else if (overallWriteTimeout > 0) {
        (*env)->SetIntField(env, object, spsw_pollWriteTimeout, overallWriteTimeout);
    } else {
        throw_Illegal_Argument_Exception(env, "setTimeouts: overallWriteTimeout must not < 0");
        return;
    }

    if (tcsetattr(fd, TCSANOW, &settings) != 0) {
        throw_ClosedOrNativeException(env, object, "setTimeouts: setInterByteReadTimeout tcsetattr");
    }
}

/*
 * Class:     de_ibapl_spsw_provider_GenericTermiosSerialPortSocket
 * Method:    getInterByteReadTimeout
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_getInterByteReadTimeout
(JNIEnv *env, jobject object) {
    int fd = (*env)->GetIntField(env, object, spsw_fd);
    struct termios settings;
    if (tcgetattr(fd, &settings) != 0) {
        throw_ClosedOrNativeException(env, object, "getInterByteReadTimeout tcgetattr");
        return -1;
    }

    if (settings.c_cc[VMIN] != 1) {
        throw_SerialPortException_NativeError(env, "getInterByteReadTimeout Wrong Timeout settings");
        return -1;

    }
    return settings.c_cc[VTIME] * 100;

}

/*
 * Class:     de_ibapl_spsw_provider_GenericTermiosSerialPortSocket
 * Method:    getOverallReadTimeout
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_getOverallReadTimeout
(JNIEnv *env, jobject object) {
    int pollReadTimeout = (*env)->GetIntField(env, object, spsw_pollReadTimeout);
    if (pollReadTimeout < 0) {
        return 0;
    } else {
        return pollReadTimeout;
    }
}

/*
 * Class:     de_ibapl_spsw_provider_GenericTermiosSerialPortSocket
 * Method:    getOverallWriteTimeout
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_getOverallWriteTimeout
(JNIEnv *env, jobject object) {
    int pollWriteTimeout = (*env)->GetIntField(env, object, spsw_pollWriteTimeout);
    if (pollWriteTimeout < 0) {
        return 0;
    } else {
        return pollWriteTimeout;
    }
}

