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

#include "de_ibapl_spsw_jniprovider_SerialPortSocketFactoryImpl.h"

#ifdef __cplusplus
extern "C" {
#endif

    /*
     * Class:     de_ibapl_spsw_jniprovider_SerialPortSocketFactoryImpl
     * Method:    initNative
     * Signature: (Ljava/lang/Class;Ljava/lang/Class;Ljava/lang/Class;)Z
     */
    JNIEXPORT jboolean JNICALL Java_de_ibapl_spsw_jniprovider_SerialPortSocketFactoryImpl_initNative
    (JNIEnv *env, jclass clazz, jclass timeoutIOExceptionClass, jclass genericTermiosSerialPortSocketClass, jclass genericWinSerialPortSocketClass) {
        if (!initExceptions(env, timeoutIOExceptionClass)) {
            return JNI_FALSE;
        }

#ifdef HAVE_TERMIOS_H
        //Get field IDs
        spsw_fd = getFieldIdOfClassRef(env, genericTermiosSerialPortSocketClass, GENERIC_TERMIOS_SERIAL_PORT_SOCKET, "fd", "I");
        if (spsw_fd == NULL) {
            return JNI_FALSE;
        }

        spsw_closeEventReadFd = getFieldIdOfClassRef(env, genericTermiosSerialPortSocketClass, GENERIC_TERMIOS_SERIAL_PORT_SOCKET, "closeEventReadFd", "I");
        if (spsw_closeEventReadFd == NULL) {
            return JNI_FALSE;
        }

        spsw_closeEventWriteFd = getFieldIdOfClassRef(env, genericTermiosSerialPortSocketClass, GENERIC_TERMIOS_SERIAL_PORT_SOCKET, "closeEventWriteFd", "I");
        if (spsw_closeEventWriteFd == NULL) {
            return JNI_FALSE;
        }

        spsw_interByteReadTimeout = getFieldIdOfClassRef(env, genericTermiosSerialPortSocketClass, GENERIC_TERMIOS_SERIAL_PORT_SOCKET, "interByteReadTimeout", "I");
        if (spsw_interByteReadTimeout == NULL) {
            return JNI_FALSE;
        }
        //SIGSEV ???
        spsw_pollReadTimeout = getFieldIdOfClassRef(env, genericTermiosSerialPortSocketClass, GENERIC_TERMIOS_SERIAL_PORT_SOCKET, "pollReadTimeout", "I");
        if (spsw_pollReadTimeout == NULL) {
            return JNI_FALSE;
        }

        spsw_pollWriteTimeout = getFieldIdOfClassRef(env, genericTermiosSerialPortSocketClass, GENERIC_TERMIOS_SERIAL_PORT_SOCKET, "pollWriteTimeout", "I");
        if (spsw_pollWriteTimeout == NULL) {
            return JNI_FALSE;
        }
        //return JNI_VERSION_1_4;
        spsw_portName = getFieldIdOfClassRef(env, genericTermiosSerialPortSocketClass, GENERIC_TERMIOS_SERIAL_PORT_SOCKET, "portName", "Ljava/lang/String;");
        if (spsw_portName == NULL) {
            return JNI_FALSE;
        }

#elif defined HAVE_WINDOWS_H
        //Get field IDs
        spsw_fd = getFieldIdOfClassRef(env, genericWinSerialPortSocketClass, GENERIC_WIN_SERIAL_PORT_SOCKET, "fd", "J");
        if (spsw_fd == NULL) {
            return JNI_FALSE;
        }
        spsw_portName = getFieldIdOfClassRef(env, genericWinSerialPortSocketClass, GENERIC_WIN_SERIAL_PORT_SOCKET, "portName", "Ljava/lang/String;");
        if (spsw_portName == NULL) {
            return JNI_FALSE;
        }

#endif

        return JNI_TRUE;
    }

#ifdef __cplusplus
}
#endif