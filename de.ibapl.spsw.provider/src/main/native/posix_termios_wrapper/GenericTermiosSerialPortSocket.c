/*-
 * #%L
 * SPSW Provider
 * %%
 * Copyright (C) 2009 - 2017 Arne Plöse
 * %%
 * SPSW - Drivers for the serial port, https://github.com/aploese/spsw/
 * Copyright (C) 2009, 2017, Arne Plöse and individual contributors as indicated
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

#include <jni.h>

#include "de_ibapl_spsw_provider_GenericTermiosSerialPortSocket.h"



#undef FLOW_CONTROL_NONE
#define FLOW_CONTROL_NONE de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_FLOW_CONTROL_NONE
#undef FLOW_CONTROL_RTS_CTS_IN
#define FLOW_CONTROL_RTS_CTS_IN de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_FLOW_CONTROL_RTS_CTS_IN
#undef FLOW_CONTROL_RTS_CTS_OUT
#define FLOW_CONTROL_RTS_CTS_OUT de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_FLOW_CONTROL_RTS_CTS_OUT
#undef FLOW_CONTROL_XON_XOFF_IN
#define FLOW_CONTROL_XON_XOFF_IN de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_FLOW_CONTROL_XON_XOFF_IN
#undef FLOW_CONTROL_XON_XOFF_OUT
#define FLOW_CONTROL_XON_XOFF_OUT de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_FLOW_CONTROL_XON_XOFF_OUT


jfieldID spsw_portName; /* id for field 'portName'  */
jfieldID spsw_fd; /* id for field 'fd'  */
jfieldID spsw_open; /* id for field 'open'  */

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved) {
    JNIEnv *env;
    jint getEnvResult = ((*jvm)->GetEnv(jvm, (void **) &env, JNI_VERSION_1_2));
    if (getEnvResult != JNI_OK){
        return getEnvResult;
    }
    jclass genericTermiosSerialPortSocket = (*env)->FindClass(env, "Lde/ibapl/spsw/provider/GenericTermiosSerialPortSocket;");
    spsw_fd = (*env)->GetFieldID(env, genericTermiosSerialPortSocket, "fd", "I");
    spsw_open = (*env)->GetFieldID(env, genericTermiosSerialPortSocket, "open", "Z");
    spsw_portName = (*env)->GetFieldID(env, genericTermiosSerialPortSocket, "portName", "Ljava/lang/String;");
    
    // mark that the lib was loaded
    jclass serialPortSocketFactoryImpl = (*env)->FindClass(env, "Lde/ibapl/spsw/provider/SerialPortSocketFactoryImpl;");
    jfieldID spsw_libLoaded = (*env)->GetStaticFieldID(env, serialPortSocketFactoryImpl, "libLoaded", "Z");
    (*env)->SetStaticBooleanField(env, serialPortSocketFactoryImpl, spsw_libLoaded, JNI_TRUE);
    return JNI_VERSION_1_2;
}

JNIEXPORT void JNICALL JNI_OnUnLoad(JavaVM *jvm, void *reserved) {
    JNIEnv *env;
    spsw_fd = 0;
    spsw_open = 0;
    spsw_portName = 0;
    
    // mark that the lib was unloaded
    jclass serialPortSocketFactoryImpl = (*env)->FindClass(env, "Lde/ibapl/spsw/provider/SerialPortSocketFactoryImpl;");
    jfieldID spsw_libLoaded = (*env)->GetStaticFieldID(env, serialPortSocketFactoryImpl, "libLoaded", "Z");
    (*env)->SetStaticBooleanField(env, serialPortSocketFactoryImpl, spsw_libLoaded, JNI_FALSE);
}



// Helper method

static void throw_SerialPortException_With_PortName(JNIEnv *env, const char *msg, jstring portName) {
    char buf[2048];
    const char* port = (*env)->GetStringUTFChars(env, portName, JNI_FALSE);
    snprintf(buf, 2048, "%s (%s) : Unknown port error %d: (%s)", msg, port, errno, strerror(errno));
    (*env)->ReleaseStringUTFChars(env, portName, port);
    (*env)->ThrowNew(env, (*env)->FindClass(env, "de/ibapl/spsw/api/SerialPortException"), buf);
}

static void throw_SerialPortException(JNIEnv *env, const char *msg) {
    (*env)->ThrowNew(env, (*env)->FindClass(env, "de/ibapl/spsw/api/SerialPortException"), msg);
}

static void throw_PortBusyException(JNIEnv *env, jstring portName) {
    const char* port = (*env)->GetStringUTFChars(env, portName, JNI_FALSE);
    (*env)->ThrowNew(env, (*env)->FindClass(env, "de/ibapl/spsw/api/PortBusyException"), port);
    (*env)->ReleaseStringUTFChars(env, portName, port);
}

static void throw_PortNotFoundException(JNIEnv *env, jstring portName) {
    const char* port = (*env)->GetStringUTFChars(env, portName, JNI_FALSE);
    (*env)->ThrowNew(env, (*env)->FindClass(env, "de/ibapl/spsw/api/PortNotFoundException"), port);
    (*env)->ReleaseStringUTFChars(env, portName, port);
}

static void throw_PermissionDeniedException(JNIEnv *env, jstring portName) {
    const char* port = (*env)->GetStringUTFChars(env, portName, JNI_FALSE);
    (*env)->ThrowNew(env, (*env)->FindClass(env, "de/ibapl/spsw/api/PermissionDeniedException"), port);
    (*env)->ReleaseStringUTFChars(env, portName, port);
}

static void throw_NotASerialPortException(JNIEnv *env, jstring portName) {
    const char* port = (*env)->GetStringUTFChars(env, portName, JNI_FALSE);
    (*env)->ThrowNew(env, (*env)->FindClass(env, "de/ibapl/spsw/api/NotASerialPortException"), port);
    (*env)->ReleaseStringUTFChars(env, portName, port);
}

static jboolean getLineStatus(JNIEnv *env, jobject object, int bitMask) {
    int fd = (*env)->GetIntField(env, object, spsw_fd);
    int lineStatus;
    int result = ioctl(fd, TIOCMGET, &lineStatus);
    if (result != 0) {
        throw_SerialPortException_With_PortName(env, "Can't get line status", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
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
    int result = ioctl(fd, TIOCMGET, &lineStatus);
    if (result != 0) {
        throw_SerialPortException_With_PortName(env, "Can't get line status", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return;
    }
    if (enabled == JNI_TRUE) {
        lineStatus |= bitMask;
    } else {
        lineStatus &= ~bitMask;
    }
    result = ioctl(fd, TIOCMSET, &lineStatus);
    if (result != 0) {
        throw_SerialPortException_With_PortName(env, "Can't set line status", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
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
            throw_SerialPortException(env, "Baudrate not supported");
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
            throw_SerialPortException(env, "Baudrate not supported");
            return -1;
    }
}

/*
 * Port opening
 * 
 * In 2.2.0 added useTIOCEXCL
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_open(JNIEnv *env, jobject object, jstring portName, jint portMode) {
    const char* port = (*env)->GetStringUTFChars(env, portName, JNI_FALSE);

    int fd = open(port, O_RDWR | O_NOCTTY | O_NDELAY);

    (*env)->ReleaseStringUTFChars(env, portName, port);

    if (fd == -1) {
        (*env)->SetIntField(env, object, spsw_fd, -1);
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
                throw_SerialPortException_With_PortName(env, "open", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        }
        return;
    }

    //check termios structure for separating real serial devices from others
    struct termios settings;
    if (tcgetattr(fd, &settings) != 0) {
        close(fd); //since 2.7.0
        (*env)->SetIntField(env, object, spsw_fd, -1);
        switch (errno) {
            case EIO:
                throw_NotASerialPortException(env, portName);
                break;
            default:
                throw_SerialPortException_With_PortName(env, "open tcgetattr", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        }
        return;
    }

    // Yes we use this port exclusively    
    if (ioctl(fd, TIOCEXCL) != 0) {
        close(fd);
        (*env)->SetIntField(env, object, spsw_fd, -1);
        throw_SerialPortException_With_PortName(env, "Can't set exclusive access", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return;
    }

    int flags = fcntl(fd, F_GETFL, 0);
    flags &= ~O_NDELAY; // set blocking IO 
    if (fcntl(fd, F_SETFL, flags) != 0) {
        close(fd);
        (*env)->SetIntField(env, object, spsw_fd, -1);
        throw_SerialPortException_With_PortName(env, "Can't call fcntl F_SETFL", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return;
    };

    switch (portMode) {
        case de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_PORT_MODE_UNCHANGED:
            break;
        case de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_PORT_MODE_RAW:
            settings.c_cflag |= (CREAD | CLOCAL);

            settings.c_lflag = 0;
            /* Raw input*/
            settings.c_iflag = 0;
            /* Raw output */
            settings.c_oflag = 0;
            settings.c_cc[VMIN] = 1; // min 1 char to receive
            settings.c_cc[VTIME] = 0;
            //settings.c_cc[VTIME] = 0;
            if (tcsetattr(fd, TCSANOW, &settings) != 0) {
                close(fd);
                (*env)->SetIntField(env, object, spsw_fd, -1);
                throw_SerialPortException_With_PortName(env, "Can't call tcsetattr TCSANOW", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
                return;
            }
            break;
        default:
            close(fd);
            (*env)->SetIntField(env, object, spsw_fd, -1);
            throw_SerialPortException_With_PortName(env, "Unknown terminal mode giving up", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
            return;
    }
    // flush the device
    if (tcflush(fd, TCIOFLUSH) != 0) {
        close(fd);
        (*env)->SetIntField(env, object, spsw_fd, -1);
        throw_SerialPortException_With_PortName(env, "Can't flush device", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return;
    }


    (*env)->SetIntField(env, object, spsw_fd, fd);
}

/* Closing the port */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_close0
(JNIEnv *env, jobject object) {

    int fd = (*env)->GetIntField(env, object, spsw_fd);
    (*env)->SetIntField(env, object, spsw_fd, -1);

    struct termios settings;
    if (tcgetattr(fd, &settings) != 0) {
        //        perror("NATIVE Error Close - tcgetattr");
    }

    int flags = fcntl(fd, F_GETFL, 0);
    flags |= O_NDELAY;
    if (fcntl(fd, F_SETFL, flags) != 0) {
        //        perror("NATIVE Error Close - fcntl F_SETFL");
    }

    settings.c_cc[VMIN] = 0;
    settings.c_cc[VTIME] = 1;
    if (tcsetattr(fd, TCSANOW, &settings) != 0) {
        //        perror("NATIVE Error Close - tcsetattr");
    }

    if (tcflush(fd, TCIOFLUSH) != 0) {
        //        perror("NATIVE Error Close - tcflush");
    }

    if (close(fd) != 0) {
        throw_SerialPortException_With_PortName(env, "close0 closing port", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
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
    throw_SerialPortException(env, "setXON not implementred yet");
}

JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_sendXOFF
(JNIEnv *env, jobject object) {
    throw_SerialPortException(env, "setXOFF not implementred yet");
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
    int fd = (*env)->GetIntField(env, object, spsw_fd);
    int arg;
    if (enabled == JNI_TRUE) {
        arg = TIOCSBRK;
    } else {
        arg = TIOCCBRK;
    }
    int result = ioctl(fd, arg);
    if (result != 0) {
        throw_SerialPortException_With_PortName(env, "Can't set Break", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
    }
}

JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_setXONChar
(JNIEnv *env, jobject object, jchar c) {
    int fd = (*env)->GetIntField(env, object, spsw_fd);

    struct termios settings;
    if (tcgetattr(fd, &settings) != 0) {
        throw_SerialPortException_With_PortName(env, "setXONChar tcgetattr", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return;
    }
    settings.c_cc[VSTART] = c;

    if (tcsetattr(fd, TCSANOW, &settings) != 0) {
        throw_SerialPortException_With_PortName(env, "setXONChar tcsetattr", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return;
    }

}

JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_setXOFFChar
(JNIEnv *env, jobject object, jchar c) {
    int fd = (*env)->GetIntField(env, object, spsw_fd);

    struct termios settings;
    if (tcgetattr(fd, &settings) != 0) {
        throw_SerialPortException_With_PortName(env, "setXOFFChar tcgetattr", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return;
    }
    settings.c_cc[VSTOP] = c;

    if (tcsetattr(fd, TCSANOW, &settings) != 0) {
        throw_SerialPortException_With_PortName(env, "setXOFFChar tcsetattr", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return;
    }

}

JNIEXPORT jchar JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_getXONChar
(JNIEnv *env, jobject object) {
    int fd = (*env)->GetIntField(env, object, spsw_fd);

    struct termios settings;
    if (tcgetattr(fd, &settings) != 0) {
        throw_SerialPortException_With_PortName(env, "getXONChar tcgetattr", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return 0;
    }
    return settings.c_cc[VSTART];

}

JNIEXPORT jchar JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_getXOFFChar
(JNIEnv *env, jobject object) {
    int fd = (*env)->GetIntField(env, object, spsw_fd);

    struct termios settings;
    if (tcgetattr(fd, &settings) != 0) {
        throw_SerialPortException_With_PortName(env, "getXOFFChar tcgetattr", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
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
    int fd = (*env)->GetIntField(env, object, spsw_fd);
    if (write(fd, &b, 1) < 0) {
        if (fd == -1) {
            throw_SerialPortException_With_PortName(env, "Invalid fileDescriptor", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        } else {
            throw_SerialPortException_With_PortName(env, "writeSingle", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        }
    }
}

JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_writeBytes
(JNIEnv *env, jobject object, jbyteArray bytes, jint off, jint len) {

    jbyte *buf = (jbyte*) malloc(len);

    (*env)->GetByteArrayRegion(env, bytes, off, len, buf);
    if (!(*env)->ExceptionOccurred(env)) {
        off = 0;
        int fd = (*env)->GetIntField(env, object, spsw_fd);
        int written = write(fd, buf, len);
        if (written < 0) {
            if (fd == -1) {
                throw_SerialPortException_With_PortName(env, "writeBytes Invalid fileDescriptor", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
            } else {
                throw_SerialPortException_With_PortName(env, "writeBytes", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
            }
        } else if (written < len) {
            throw_SerialPortException_With_PortName(env, "writeBytes too few bytes written", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        }
    }
    free(buf);
}

JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_readSingle
(JNIEnv *env, jobject object) {
    jbyte lpBuffer;

    jint nread = (jint) read((*env)->GetIntField(env, object, spsw_fd), &lpBuffer, 1);
    if (nread == 1) {
        return lpBuffer & 0xFF;
    } else if (nread <= 0) {
        // read blocked but no data, probaply closing down.
        return -1;
    } else if (nread < 0) {
        //TODO use isOpen ???
        if ((*env)->GetBooleanField(env, object, spsw_open)) {
            throw_SerialPortException_With_PortName(env, "readSingle read error", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
            return -1;
        } else {
            //closed
            return -1;
        }
    }
    
    // we should never reach this ...
    throw_SerialPortException_With_PortName(env, "readSingle: Should never happen", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
    return -1;
}

JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_readBytes
(JNIEnv *env, jobject object, jbyteArray bytes, jint off, jint len) {
    jbyte *lpBuffer = (jbyte*) malloc(len);

    jint nread = (jint) read((*env)->GetIntField(env, object, spsw_fd), lpBuffer, len);
    if (nread > 0) {
        (*env)->SetByteArrayRegion(env, bytes, off, nread, lpBuffer);
    } else if (nread == 0) {
        // read unblocked but no data probaply closing down.
        nread = -1;
    } else {
        if ((*env)->GetIntField(env, object, spsw_fd) == -1) {
            //closed
            nread = -1;
        } else {
            nread = -1;
        }
    }
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
        throw_SerialPortException_With_PortName(env, "Can't read in buffer size", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
    }
    return returnValue;
}

/*
 * write the bytes in the output buffer
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_drainOutputBuffer
(JNIEnv *env, jobject object) {
    int fd = (*env)->GetIntField(env, object, spsw_fd);
    int result = tcdrain(fd);
    if (result != 0) {
        throw_SerialPortException_With_PortName(env, "Can't drain the output buffer", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return;
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
        throw_SerialPortException_With_PortName(env, "setFlowControl tcgetattr", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return;
    }

    settings.c_cflag &= ~CRTSCTS;
    settings.c_iflag &= ~(IXON | IXOFF);
    if (mask != FLOW_CONTROL_NONE) {
        if (((mask & FLOW_CONTROL_RTS_CTS_IN) == FLOW_CONTROL_RTS_CTS_IN) ||
                ((mask & FLOW_CONTROL_RTS_CTS_OUT) == FLOW_CONTROL_RTS_CTS_OUT)) {
            settings.c_cflag |= CRTSCTS;
        }
        if ((mask & FLOW_CONTROL_XON_XOFF_IN) == FLOW_CONTROL_XON_XOFF_IN) {
            settings.c_iflag |= IXOFF;
        }
        if ((mask & FLOW_CONTROL_XON_XOFF_OUT) == FLOW_CONTROL_XON_XOFF_OUT) {
            settings.c_iflag |= IXON;
        }
    }

    if (tcsetattr(fd, TCSANOW, &settings) != 0) {
        throw_SerialPortException_With_PortName(env, "setFlowControl tcsetattr", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
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
        throw_SerialPortException_With_PortName(env, "setBaudrate tcgetattr", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return;
    }

    //Set standart baudrate from "termios.h"
    if (cfsetspeed(&settings, baudRateValue) < 0) {
        throw_SerialPortException_With_PortName(env, "Baudrate of in and output mismatch", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return;
    }

    if (tcsetattr(fd, TCSANOW, &settings) != 0) {
        throw_SerialPortException_With_PortName(env, "set Baudrate tcsetattr", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
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
        throw_SerialPortException_With_PortName(env, "setDataBits tcgetattr", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
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
            throw_SerialPortException(env, "Wrong databits");
            return;
    }

    if (tcsetattr(fd, TCSANOW, &settings) != 0) {
        throw_SerialPortException_With_PortName(env, "setDataBits tcsetattr", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
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
        throw_SerialPortException_With_PortName(env, "setStopBits tcgetattr", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return;
    }

    switch (stopBits) {
        case de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_STOP_BITS_1:
            //1 stop bit (for info see ->> MSDN)
            settings.c_cflag &= ~CSTOPB;
            break;
        case de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_STOP_BITS_1_5:
            throw_SerialPortException(env, "setStopBits 1.5 stop bits are not supported by termios");
            return;
            break;
        case de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_STOP_BITS_2:
            settings.c_cflag |= CSTOPB;
            break;
        default:
            throw_SerialPortException_With_PortName(env, "Unknown stopbits", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
            return;
    }

    if (tcsetattr(fd, TCSANOW, &settings) != 0) {
        throw_SerialPortException_With_PortName(env, "setStopBits tcsetattr", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
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
        throw_SerialPortException_With_PortName(env, "setStopBits tcgetattr", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
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
        case de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_PARITY_NONE:
            //Parity NONE
            settings.c_iflag &= ~INPCK; // switch parity input checking off
            break;
        case de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_PARITY_ODD:
            //Parity ODD
            settings.c_cflag |= (PARENB | PARODD);
            settings.c_iflag |= INPCK; // switch parity input checking On 
            break;
        case de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_PARITY_EVEN:
            //Parity EVEN
            settings.c_cflag |= PARENB;
            settings.c_iflag |= INPCK;
            break;
        case de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_PARITY_MARK:
            //Parity MARK
#ifdef PAREXT
            settings.c_cflag |= (PARENB | PARODD | PAREXT);
            settings.c_iflag |= INPCK;
#elif defined CMSPAR
            settings.c_cflag |= (PARENB | PARODD | CMSPAR);
            settings.c_iflag |= INPCK;
#endif
            break;
        case de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_PARITY_SPACE:
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
            throw_SerialPortException(env, "Wrong Parity");
            return;
    }

    if (tcsetattr(fd, TCSANOW, &settings) != 0) {
        throw_SerialPortException_With_PortName(env, "setParity tcsetattr", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
    }
}

/*
 * Class:     de_ibapl_spsw_provider_GenericTermiosSerialPortSocket
 * Method:    getBaudrate
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_getBaudrate0
(JNIEnv *env, jobject object) {
    int fd = (*env)->GetIntField(env, object, spsw_fd);
    struct termios settings;
    if (tcgetattr(fd, &settings) != 0) {
        throw_SerialPortException_With_PortName(env, "getBaudRate tcgetattr", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return (jint) - 1;
    }
    //Set standart baudrate from "termios.h"
    speed_t inSpeed = cfgetispeed(&settings);
    speed_t outSpeed = cfgetospeed(&settings);
    if (inSpeed != outSpeed) {
        throw_SerialPortException_With_PortName(env, "In and out speed mismatch", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return -1;
    }
    return speed_t2Baudrate(env, inSpeed);
}

/*
 * Class:     de_ibapl_spsw_provider_GenericTermiosSerialPortSocket
 * Method:    getDataBits
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_getDataBits0
(JNIEnv *env, jobject object) {
    int fd = (*env)->GetIntField(env, object, spsw_fd);
    struct termios settings;
    if (tcgetattr(fd, &settings) != 0) {
        throw_SerialPortException_With_PortName(env, "getDataBits tcgetattr", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return (jint) - 1;
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
            throw_SerialPortException(env, "Unknown databits");
            return (jint) - 1;
    }
    return -1;
}

/*
 * Class:     de_ibapl_spsw_provider_GenericTermiosSerialPortSocket
 * Method:    getStopBits
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_getStopBits0
(JNIEnv *env, jobject object) {
    int fd = (*env)->GetIntField(env, object, spsw_fd);
    struct termios settings;
    if (tcgetattr(fd, &settings) != 0) {
        throw_SerialPortException_With_PortName(env, "getStopBits tcgetattr", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return (jint) - 1;
    }

    if ((settings.c_cflag & CSTOPB) == 0) {
        return de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_STOP_BITS_1;
    }
    if ((settings.c_cflag & CSTOPB) == CSTOPB) {
        return de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_STOP_BITS_2;
    }
}

/*
 * Class:     de_ibapl_spsw_provider_GenericTermiosSerialPortSocket
 * Method:    getParity
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_getParity0
(JNIEnv *env, jobject object) {
    int fd = (*env)->GetIntField(env, object, spsw_fd);
    struct termios settings;
    if (tcgetattr(fd, &settings) != 0) {
        throw_SerialPortException_With_PortName(env, "getParity tcgetattr", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return (jint) - 1;
    }

    if ((settings.c_cflag & PARENB) == 0) {
        return de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_PARITY_NONE;
    } else if ((settings.c_cflag & PARODD) == 0) {
        // EVEN or SPACE
#ifdef PAREXT
        if ((settings.c_cflag & PAREXT) == 0) {
            return de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_PARITY_EVEN;
        } else {
            return de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_PARITY_SPACE;
        }
#elif defined CMSPAR
        if ((settings.c_cflag & CMSPAR) == 0) {
            return de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_PARITY_EVEN;
        } else {
            return de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_PARITY_SPACE;
        }
#endif
    } else {
        // ODD or MARK
#ifdef PAREXT
        if ((settings.c_cflag & PAREXT) == 0) {
            return de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_PARITY_ODD;
        } else {
            return de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_PARITY_MARK;
        }
#elif defined CMSPAR
        if ((settings.c_cflag & CMSPAR) == 0) {
            return de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_PARITY_ODD;
        } else {
            return de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_PARITY_MARK;
        }
#endif
    }
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
        throw_SerialPortException_With_PortName(env, "getFlowControl tcgetattr", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return (jint) - 1;
    }

    if (settings.c_cflag & CRTSCTS) {
        returnValue |= (FLOW_CONTROL_RTS_CTS_IN | FLOW_CONTROL_RTS_CTS_OUT);
    }
    if (settings.c_iflag & IXOFF) {
        returnValue |= FLOW_CONTROL_XON_XOFF_IN;
    }
    if (settings.c_iflag & IXON) {
        returnValue |= FLOW_CONTROL_XON_XOFF_OUT;
    }
    return returnValue;
}

JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericTermiosSerialPortSocket_printTermios
(JNIEnv *env, jobject object) {

    int fd = (*env)->GetIntField(env, object, spsw_fd);
    struct termios settings;
    if (tcgetattr(fd, &settings) != 0) {
        throw_SerialPortException_With_PortName(env, "Error closing port", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        throw_SerialPortException(env, "setFlowControl tcgetattr");
        return;
    }
    printf("Termios\n");
    printf("c_cc[VINTR] : %x\n", settings.c_cc[VINTR]);
    printf("c_cc[VQUIT] : %x\n", settings.c_cc[VQUIT]);
    printf("c_cc[VERASE] : %x\n", settings.c_cc[VERASE]);
    printf("c_cc[VKILL] : %x\n", settings.c_cc[VKILL]);
    printf("c_cc[VEOF] : %x\n", settings.c_cc[VEOF]);
    printf("c_cc[VTIME] : %x\n", settings.c_cc[VTIME]);
    printf("c_cc[VMIN] : %x\n", settings.c_cc[VMIN]);
    printf("c_cc[VSWTC] : %x\n", settings.c_cc[VSWTC]);
    printf("c_cc[VSTART] : %x\n", settings.c_cc[VSTART]);
    printf("c_cc[VSTOP] : %x\n", settings.c_cc[VSTOP]);
    printf("c_cc[VSUSP] : %x\n", settings.c_cc[VSUSP]);
    printf("c_cc[VEOL] : %x\n", settings.c_cc[VEOL]);
    printf("c_cc[VREPRINT] : %x\n", settings.c_cc[VREPRINT]);
    printf("c_cc[VDISCARD] : %x\n", settings.c_cc[VDISCARD]);
    printf("c_cc[VWERASE] : %x\n", settings.c_cc[VWERASE]);
    printf("c_cc[VLNEXT] : %x\n", settings.c_cc[VLNEXT]);
    printf("c_cc[VEOL2] : %x\n", settings.c_cc[VEOL2]);
    printf("c_cflag : %x\n", settings.c_cflag);
    printf("c_iflag : %x\n", settings.c_iflag);
    printf("c_ispeed : %x\n", settings.c_ispeed);
    printf("c_lflag : %x\n", settings.c_lflag);
    printf("c_line : %x\n", settings.c_line);
    printf("c_oflag : %x\n", settings.c_oflag);
    printf("c_ospeed : %x\n", settings.c_ospeed);
}



//#include <sys/time.h>
//      #include <sys/types.h>
//      #include <unistd.h>
//        
//      main
//      {
//        int    fd1, fd2;  /* input sources 1 and 2 */
//        fd_set readfs;    /* file descriptor set */
//        int    maxfd;     /* maximum file desciptor used */
//        int    loop=1;    /* loop while TRUE */ 
//        
//        /* open_input_source opens a device, sets the port correctly, and
//           returns a file descriptor */
//        fd1 = open_input_source("/dev/ttyS1");   /* COM2 */
//        if (fd1<0) exit(0);
//        fd2 = open_input_source("/dev/ttyS2");   /* COM3 */
//        if (fd2<0) exit(0);
//        maxfd = MAX (fd1, fd2)+1;  /* maximum bit entry (fd) to test */
//        
//        /* loop for input */
//        while (loop) {
//          FD_SET(fd1, &readfs);  /* set testing for source 1 */
//          FD_SET(fd2, &readfs);  /* set testing for source 2 */
//          /* block until input becomes available */
//          select(maxfd, &readfs, NULL, NULL, NULL);
//          if (FD_ISSET(fd1))         /* input from source 1 available */
//            handle_input_from_source1();
//          if (FD_ISSET(fd2))         /* input from source 2 available */
//            handle_input_from_source2();
//        }
//      }   
