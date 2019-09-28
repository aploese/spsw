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

#ifdef __cplusplus
extern "C" {
#endif

    jfieldID spsw_fd; /* id for field 'fd'  */
    jfieldID spsw_portName; /* id for field 'portName'  */

#ifdef HAVE_TERMIOS_H
    jfieldID spsw_interByteReadTimeout; // id for field interByteReadTimeout
    jfieldID spsw_pollReadTimeout; // id for field overallReadTimeout
    jfieldID spsw_pollWriteTimeout; // id for field overallWriteTimeout
    jfieldID spsw_closeEventReadFd; /* id for field 'closeEventReadFd'  */
    jfieldID spsw_closeEventWriteFd; /* id for field 'closeEventWriteFd'  */
#endif

    jclass getGlobalClassRef(JNIEnv *env, const char* className) {
        jclass clazz = (*env)->FindClass(env, className);
        if (clazz == NULL) {
            return NULL;
        }
        jclass result = (*env)->NewGlobalRef(env, clazz);
        (*env)->DeleteLocalRef(env, clazz);
        if (result == NULL) {
            throw_RuntimeException(env, "(*env)->NewGlobalRef(env, clazz) return NULL >> Out of memory??");
        }
        return result;

    }

    jfieldID getFieldId(JNIEnv *env, const char* className, const char* fieldName, const char* fieldType) {

        jclass clazz = (*env)->FindClass(env, className);
        if (clazz == NULL) {
            return NULL;
        }

        jfieldID result = (*env)->GetFieldID(env, clazz, fieldName, fieldType);
        (*env)->DeleteLocalRef(env, clazz);
        if (result == NULL) {
            return NULL;
        }
        return result;
    }

    jfieldID getFieldIdOfClassRef(JNIEnv *env, jclass clazz, const char* className, const char* fieldName, const char* fieldType) {

        jfieldID result = (*env)->GetFieldID(env, clazz, fieldName, fieldType);
        if (result == NULL) {
            return NULL;
        }
        return result;
    }

    jmethodID getMethodIdOfClassRef(JNIEnv *env, jclass clazz, const char* className, const char* methodName, const char* methodSignature) {

        jmethodID result = (*env)->GetMethodID(env, clazz, methodName, methodSignature);
        if (result == NULL) {
            return NULL;
        }
        return result;
    }

    JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved) {
        JNIEnv *env;
        if ((*jvm)->GetEnv(jvm, (void **) &env, JNI_VERSION_1_4)) {
            return JNI_ERR;
        }

        if (!initExceptions(env)) {
            return JNI_ERR;
        }

#ifdef HAVE_TERMIOS_H
        //Get field IDs
        spsw_fd = getFieldId(env, GENERIC_TERMIOS_SERIAL_PORT_SOCKET, "fd", "I");
        if (spsw_fd == NULL) {
            return JNI_ERR;
        }

        spsw_closeEventReadFd = getFieldId(env, GENERIC_TERMIOS_SERIAL_PORT_SOCKET, "closeEventReadFd", "I");
        if (spsw_closeEventReadFd == NULL) {
            return JNI_ERR;
        }

        spsw_closeEventWriteFd = getFieldId(env, GENERIC_TERMIOS_SERIAL_PORT_SOCKET, "closeEventWriteFd", "I");
        if (spsw_closeEventWriteFd == NULL) {
            return JNI_ERR;
        }

        spsw_interByteReadTimeout = getFieldId(env, GENERIC_TERMIOS_SERIAL_PORT_SOCKET, "interByteReadTimeout", "I");
        if (spsw_interByteReadTimeout == NULL) {
            return JNI_ERR;
        }
        //SIGSEV ???
        spsw_pollReadTimeout = getFieldId(env, GENERIC_TERMIOS_SERIAL_PORT_SOCKET, "pollReadTimeout", "I");
        if (spsw_pollReadTimeout == NULL) {
            return JNI_ERR;
        }

        spsw_pollWriteTimeout = getFieldId(env, GENERIC_TERMIOS_SERIAL_PORT_SOCKET, "pollWriteTimeout", "I");
        if (spsw_pollWriteTimeout == NULL) {
            return JNI_ERR;
        }
        //return JNI_VERSION_1_4;
        spsw_portName = getFieldId(env, GENERIC_TERMIOS_SERIAL_PORT_SOCKET, "portName", "Ljava/lang/String;");
        if (spsw_portName == NULL) {
            return JNI_ERR;
        }

#elif defined HAVE_WINDOWS_H
        //Get field IDs
        spsw_fd = getFieldId(env, GENERIC_WIN_SERIAL_PORT_SOCKET, "fd", "J");
        if (spsw_fd == NULL) {
            return JNI_ERR;
        }
        spsw_portName = getFieldId(env, GENERIC_WIN_SERIAL_PORT_SOCKET, "portName", "Ljava/lang/String;");
        if (spsw_portName == NULL) {
            return JNI_ERR;
        }

#endif

        return JNI_VERSION_1_4;
    }

    JNIEXPORT void JNICALL JNI_OnUnLoad(JavaVM *jvm, void *reserved) {
        JNIEnv *env;
        //TODO delete GlobalRefs
        if ((*jvm)->GetEnv(jvm, (void **) &env, JNI_VERSION_1_4)) {
            cleanupExceptions(env);
        }

#ifdef HAVE_TERMIOS_H

        spsw_fd = 0;
        spsw_closeEventReadFd = 0;
        spsw_closeEventWriteFd = 0;
        spsw_interByteReadTimeout = 0;
        spsw_pollReadTimeout = 0;
        spsw_pollWriteTimeout = 0;
        spsw_portName = 0;
#elif defined HAVE_WINDOWS_H
#else
        fail
#endif

    }


#ifdef __cplusplus
}
#endif
