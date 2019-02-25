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

#ifdef HAVE_WINDOWS_H
#else 
#include <stdio.h>
#include <stdarg.h>
#include <errno.h>
#include <string.h>
#endif

#ifdef __cplusplus
extern "C" {
#endif

    static jclass ClassNotFoundExceptionClass = NULL;
    static jclass NoSuchFieldExceptionClass = NULL;
    static jclass NoSuchMethodExceptionClass = NULL;
    static jclass IOExceptionClass = NULL;

    static jclass InterruptedIOExceptionClass = NULL;
    static jmethodID InterruptedIOExceptionInit = NULL;
    static jfieldID InterruptedIOException_bytesTransferred = NULL;

    static jclass AsynchronousCloseExceptionClass = NULL;
    static jmethodID AsynchronousCloseExceptionInit = NULL;

    static jclass TimeoutIOExceptionClass = NULL;
    static jmethodID TimeoutIOExceptionInit = NULL;

    jboolean initExceptions(JNIEnv* env) {
        if (ClassNotFoundExceptionClass == NULL) {
            ClassNotFoundExceptionClass = getGlobalClassRef(env, CLASS_NOT_FOUND_EXCEPTION);
            if (ClassNotFoundExceptionClass == NULL) {
                return JNI_FALSE;
            }
        }
        if (NoSuchFieldExceptionClass == NULL) {
            NoSuchFieldExceptionClass = getGlobalClassRef(env, NO_SUCH_FIELD_EXCEPTION);
            if (NoSuchFieldExceptionClass == NULL) {
                return JNI_FALSE;
            }
        }
        if (NoSuchMethodExceptionClass == NULL) {
            NoSuchMethodExceptionClass = getGlobalClassRef(env, NO_SUCH_METHOD_EXCEPTION);
            if (NoSuchMethodExceptionClass == NULL) {
                return JNI_FALSE;
            }
        }
        if (IOExceptionClass == NULL) {
            IOExceptionClass = getGlobalClassRef(env, IO_EXCEPTION);
            if (IOExceptionClass == NULL) {
                return JNI_FALSE;
            }
        }
        if (InterruptedIOExceptionClass == NULL) {
            InterruptedIOExceptionClass = getGlobalClassRef(env, INTERRUPTED_IO_EXCEPTION);
            if (InterruptedIOExceptionClass == NULL) {
                return JNI_FALSE;
            }
            InterruptedIOExceptionInit = getMethodIdOfClassRef(env, InterruptedIOExceptionClass, INTERRUPTED_IO_EXCEPTION, "<init>", "(Ljava/lang/String;)V");
            if (InterruptedIOExceptionInit == NULL) {
                return JNI_FALSE;
            }
            InterruptedIOException_bytesTransferred = getFieldIdOfClassRef(env, InterruptedIOExceptionClass, INTERRUPTED_IO_EXCEPTION, "bytesTransferred", "I");
            if (InterruptedIOException_bytesTransferred == NULL) {
                return JNI_FALSE;
            }
        }
        if (AsynchronousCloseExceptionClass == NULL) {
            AsynchronousCloseExceptionClass = getGlobalClassRef(env, ASYNCHRONOUS_CLOSE_EXCEPTION);
            if (AsynchronousCloseExceptionClass == NULL) {
                return JNI_FALSE;
            }
            AsynchronousCloseExceptionInit = getMethodIdOfClassRef(env, AsynchronousCloseExceptionClass, ASYNCHRONOUS_CLOSE_EXCEPTION, "<init>", "()V");
            if (AsynchronousCloseExceptionInit == NULL) {
                return JNI_FALSE;
            }
        }
        if (TimeoutIOExceptionClass == NULL) {
            TimeoutIOExceptionClass = getGlobalClassRef(env, TIMEOUT_IO_EXCEPTION);
            TimeoutIOExceptionInit = getMethodIdOfClassRef(env, TimeoutIOExceptionClass, TIMEOUT_IO_EXCEPTION, "<init>", "(Ljava/lang/String;I)V");
            if (TimeoutIOExceptionInit == NULL) {
                return JNI_FALSE;
            }
        }
        return JNI_TRUE;
    }

    void cleanupExceptions(JNIEnv* env) {
        if (ClassNotFoundExceptionClass != NULL) {
            (*env)->DeleteLocalRef(env, ClassNotFoundExceptionClass);
            ClassNotFoundExceptionClass = NULL;
        }
        if (NoSuchFieldExceptionClass != NULL) {
            (*env)->DeleteLocalRef(env, NoSuchFieldExceptionClass);
            NoSuchFieldExceptionClass = NULL;
        }
        if (NoSuchMethodExceptionClass != NULL) {
            (*env)->DeleteLocalRef(env, NoSuchMethodExceptionClass);
            NoSuchMethodExceptionClass = NULL;
        }
        if (IOExceptionClass != NULL) {
            (*env)->DeleteLocalRef(env, IOExceptionClass);
            IOExceptionClass = NULL;
        }
        if (InterruptedIOExceptionClass != NULL) {
            (*env)->DeleteLocalRef(env, InterruptedIOExceptionClass);
            InterruptedIOExceptionClass = NULL;
            InterruptedIOExceptionInit = NULL;
            InterruptedIOException_bytesTransferred = NULL;
        }
        if (AsynchronousCloseExceptionClass != NULL) {
            (*env)->DeleteLocalRef(env, AsynchronousCloseExceptionClass);
            AsynchronousCloseExceptionClass = NULL;
            AsynchronousCloseExceptionInit = NULL;
        }
        if (TimeoutIOExceptionClass != NULL) {
            (*env)->DeleteLocalRef(env, TimeoutIOExceptionClass);
            TimeoutIOExceptionClass = NULL;
            TimeoutIOExceptionInit = NULL;
        }
    }

    void throw_ClassNotFoundException(JNIEnv* env, const char* className) {
        (*env)->ThrowNew(env, ClassNotFoundExceptionClass, className);
    }

    void throw_NoSuchFieldException(JNIEnv* env, const char* className, const char* fieldName, const char* fieldType) {
        char buf[1024] = {0};
        snprintf(buf, sizeof (buf) - 1, "Get FieldID of (%s) %s.%s", fieldType, className, fieldName);
        (*env)->ThrowNew(env, NoSuchFieldExceptionClass, buf);
    }

    void throw_NoSuchMethodException(JNIEnv* env, const char* className, const char* methodName, const char* methodSignature) {
        char buf[1024] = {0};
        snprintf(buf, sizeof (buf) - 1, "Get FieldID of (%s) %s.%s", methodSignature, className, methodName);
        (*env)->ThrowNew(env, NoSuchFieldExceptionClass, buf);
    }

    void throw_IOException(JNIEnv *env, const char* msg, jstring portName) {
        const char* port = (*env)->GetStringUTFChars(env, portName, JNI_FALSE);
        char buf[2048];
        snprintf(buf, sizeof (buf), msg, port);
        (*env)->ReleaseStringUTFChars(env, portName, port);
        (*env)->ThrowNew(env, IOExceptionClass, buf);
    }

    void throw_IOException_NativeError(JNIEnv *env, const char *msg) {
        char buf[2048] = {0};
#ifdef HAVE_WINDOWS_H
        snprintf(buf, 2048, "%s: Unknown port error %ld", msg, GetLastError());
#else
        snprintf(buf, 2048, "%s: Unknown port error %d: (%s)", msg, errno,
                strerror(errno));
#endif
        (*env)->ThrowNew(env, IOExceptionClass, buf);
    }

    void throw_AsynchronousCloseException(JNIEnv * env) {
        const jobject ioeEx = (*env)->NewObject(env, AsynchronousCloseExceptionClass, AsynchronousCloseExceptionInit);
        (*env)->Throw(env, ioeEx);
    }

    void throw_IllegalArgumentException(JNIEnv *env, const char *msg) {
        const jclass iaeClass = (*env)->FindClass(env,
                "java/lang/IllegalArgumentException");
        if (iaeClass != NULL) {
            (*env)->ThrowNew(env, iaeClass, msg);
            (*env)->DeleteLocalRef(env, iaeClass);
        }
    }

    void throw_IOException_Opend(JNIEnv * env) {
        (*env)->ThrowNew(env, IOExceptionClass, "Port is open");
    }

    void throw_InterruptedIOExceptionWithError(JNIEnv *env,
            int bytesTransferred, const char *msg) {
        char buf[2048];
        snprintf(buf, 2048, "%s: Unknown port error %d: (%s)", msg, errno,
                strerror(errno));
        const jobject iioeEx = (*env)->NewObject(env, InterruptedIOExceptionClass,
                InterruptedIOExceptionInit, (*env)->NewStringUTF(env, buf));
        (*env)->SetIntField(env, iioeEx, InterruptedIOException_bytesTransferred, bytesTransferred);
        (*env)->Throw(env, iioeEx);
    }

    void throw_TimeoutIOException(JNIEnv *env, int bytesTransferred) {
        const jobject tioeEx = (*env)->NewObject(env, TimeoutIOExceptionClass, TimeoutIOExceptionInit, (*env)->NewStringUTF(env, "Timeout"), bytesTransferred);
        (*env)->Throw(env, tioeEx);
    }

    void throw_ClosedOrNativeException(JNIEnv *env, jobject sps,
            const char *message) {
#ifdef HAVE_WINDOWS_H 
        if ((HANDLE) (uintptr_t) (*env)->GetIntField(env, sps, spsw_fd) == INVALID_FD) {
#else
        if ((*env)->GetIntField(env, sps, spsw_fd) == INVALID_FD) {
#endif
            (*env)->ThrowNew(env, IOExceptionClass, "Port is closed");
        } else {
            throw_IOException_NativeError(env, message);
        }
    }


#ifdef __cplusplus
}
#endif
