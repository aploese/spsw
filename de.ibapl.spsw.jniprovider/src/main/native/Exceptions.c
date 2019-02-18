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
    static jclass TimeoutIOExceptionClass = NULL;
    static jmethodID TimeoutIOExceptionInit = NULL;

    void initExceptions(JNIEnv* env) {
        if (ClassNotFoundExceptionClass == NULL) {
            ClassNotFoundExceptionClass = getGlobalClassRef(env, CLASS_NOT_FOUND_EXCEPTION);
        }
        if (NoSuchFieldExceptionClass == NULL) {
            NoSuchFieldExceptionClass = getGlobalClassRef(env, NO_SUCH_FIELD_EXCEPTION);
        }
        if (NoSuchMethodExceptionClass == NULL) {
            NoSuchMethodExceptionClass = getGlobalClassRef(env, NO_SUCH_METHOD_EXCEPTION);
        }
        if (IOExceptionClass == NULL) {
            IOExceptionClass = getGlobalClassRef(env, IO_EXCEPTION);
        }
        if (TimeoutIOExceptionClass == NULL) {
            TimeoutIOExceptionClass = getGlobalClassRef(env, TIMEOUT_IO_EXCEPTION);
            TimeoutIOExceptionInit = getMethodIdOfClassRef(env, TimeoutIOExceptionClass, TIMEOUT_IO_EXCEPTION, "<init>", "(Ljava/lang/String;I)V");
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
        const jclass ioeClass = (*env)->FindClass(env,
                "java/nio/channels/AsynchronousCloseException");

        if (ioeClass != NULL) {

            const jmethodID ioeConstructor = (*env)->GetMethodID(env, ioeClass,
                    "<init>", "()V");
            const jobject ioeEx = (*env)->NewObject(env, ioeClass, ioeConstructor);

            (*env)->Throw(env, ioeEx);

            (*env)->DeleteLocalRef(env, ioeClass);
        }

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
        const jclass spsClass = (*env)->FindClass(env,
                "de/ibapl/spsw/api/SerialPortSocket");
        const jclass ioeClass = (*env)->FindClass(env,
                "java/io/IOException");

        if ((ioeClass != NULL) || (spsClass != NULL)) {

            const jfieldID PORT_IS_OPEN = (*env)->GetStaticFieldID(env, spsClass,
                    "PORT_IS_OPEN", "Ljava/lang/String;");
            const jmethodID ioeConstructor = (*env)->GetMethodID(env, ioeClass,
                    "<init>", "(Ljava/lang/String;)V");
            const jobject ioeEx = (*env)->NewObject(env, ioeClass, ioeConstructor,
                    (*env)->GetStaticObjectField(env, spsClass, PORT_IS_OPEN));

            (*env)->Throw(env, ioeEx);

            (*env)->DeleteLocalRef(env, ioeClass);
            (*env)->DeleteLocalRef(env, spsClass);
        }
    }

    void throw_InterruptedIOExceptionWithError(JNIEnv *env,
            int bytesTransferred, const char *msg) {
        char buf[2048];
        snprintf(buf, 2048, "%s: Unknown port error %d: (%s)", msg, errno,
                strerror(errno));
        const jclass iioeClass = (*env)->FindClass(env,
                "java/io/InterruptedIOException");
        if (iioeClass != NULL) {
            const jmethodID iioeConstructor = (*env)->GetMethodID(env, iioeClass,
                    "<init>", "(Ljava/lang/String;)V");
            const jobject iioeEx = (*env)->NewObject(env, iioeClass,
                    iioeConstructor, (*env)->NewStringUTF(env, buf));
            const jfieldID bytesTransferredId = (*env)->GetFieldID(env, iioeClass,
                    "bytesTransferred", "I");
            (*env)->SetIntField(env, iioeEx, bytesTransferredId, bytesTransferred);
            (*env)->Throw(env, iioeEx);
            (*env)->DeleteLocalRef(env, iioeClass);
        }
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
            const jclass spsClass = (*env)->FindClass(env,
                    "de/ibapl/spsw/api/SerialPortSocket");
            const jclass ioeClass = (*env)->FindClass(env,
                    "java/io/IOException");

            if ((ioeClass != NULL) || (spsClass != NULL)) {

                const jfieldID PORT_IS_CLOSED = (*env)->GetStaticFieldID(env, spsClass,
                        "PORT_IS_CLOSED", "Ljava/lang/String;");
                const jmethodID ioeConstructor = (*env)->GetMethodID(env, ioeClass,
                        "<init>", "(Ljava/lang/String;)V");
                const jobject ioeEx = (*env)->NewObject(env, ioeClass, ioeConstructor,
                        (*env)->GetStaticObjectField(env, spsClass, PORT_IS_CLOSED));

                (*env)->Throw(env, ioeEx);

                (*env)->DeleteLocalRef(env, ioeClass);
                (*env)->DeleteLocalRef(env, spsClass);

            }
        } else {
            throw_IOException_NativeError(env, message);
        }
    }


#ifdef __cplusplus
}
#endif
