#include "spsw-jni.h"

#ifdef __cplusplus
extern "C" {
#endif

jfieldID spsw_portName; /* id for field 'portName'  */

#ifdef HAVE_TERMIOS_H    
 jfieldID spsw_interByteReadTimeout; // id for field interByteReadTimeout
 jfieldID spsw_pollReadTimeout; // id for field overallReadTimeout
 jfieldID spsw_pollWriteTimeout; // id for field overallWriteTimeout
 jfieldID spsw_fd; /* id for field 'fd'  */
 jfieldID spsw_closeEventReadFd; /* id for field 'closeEventReadFd'  */
 jfieldID spsw_closeEventWriteFd; /* id for field 'closeEventWriteFd'  */
#endif


jclass getGlobalClassRef(JNIEnv *env, const char* className) {
        jclass clazz = (*env)->FindClass(env, className);
        if (clazz == NULL) {
            throw_ClassNotFoundException(env, className);
            return NULL;
        }
        jclass result = (*env)->NewGlobalRef(env, clazz);
        (*env)->DeleteLocalRef(env, clazz);
        if (result == NULL) {
            //TODO what ex to throw???
            throw_ClassNotFoundException(env, className);
        }
        return result;

    }

jfieldID getFieldId(JNIEnv *env, const char* className, const char* fieldName, const char* fieldType) {

        jclass clazz = (*env)->FindClass(env, className);
        if (clazz == NULL) {
            throw_ClassNotFoundException(env, className);
            return NULL;
        }

        jfieldID result = (*env)->GetFieldID(env, clazz, fieldName, fieldType);
        (*env)->DeleteLocalRef(env, clazz);
        if (result == NULL) {
            throw_NoSuchFieldException(env, className, fieldName, fieldType);
            return NULL;
        }
        return result;
    }

jfieldID getFieldIdOfClassRef(JNIEnv *env, jclass clazz, const char* className, const char* fieldName, const char* fieldType) {

        jfieldID result = (*env)->GetFieldID(env, clazz, fieldName, fieldType);
        if (result == NULL) {
            throw_NoSuchFieldException(env, className, fieldName, fieldName);
            return NULL;
        }
        return result;
    }

jmethodID getMethodIdOfClassRef(JNIEnv *env, jclass clazz, const char* className, const char* methodName, const char* methodSignature) {

        jmethodID result = (*env)->GetMethodID(env, clazz, methodName, methodSignature);
        if (result == NULL) {
            throw_NoSuchMethodException(env, className, methodName, methodSignature);
            return NULL;
        }
        return result;
    }

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved) {
    JNIEnv *env;
    if ((*jvm)->GetEnv(jvm, (void **) &env, JNI_VERSION_1_4)) {
        return JNI_ERR;
    }

    initExceptions(env);
    
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
    if (spsw_closeEventReadFd == NULL) {
        return JNI_ERR;
    }

    spsw_interByteReadTimeout = getFieldId(env, GENERIC_TERMIOS_SERIAL_PORT_SOCKET, "interByteReadTimeout", "I");
    if (spsw_interByteReadTimeout == NULL) {
        return JNI_ERR;
    }
    spsw_pollReadTimeout = getFieldId(env, GENERIC_TERMIOS_SERIAL_PORT_SOCKET, "pollReadTimeout", "I");
    if (spsw_pollReadTimeout == NULL) {
        return JNI_ERR;
    }
    spsw_pollWriteTimeout = getFieldId(env, GENERIC_TERMIOS_SERIAL_PORT_SOCKET, "pollWriteTimeout", "I");
    if (spsw_pollWriteTimeout == NULL) {
        return JNI_ERR;
    }
    spsw_portName = getFieldId(env, GENERIC_TERMIOS_SERIAL_PORT_SOCKET, "portName", "Ljava/lang/String;");
    if (spsw_portName == NULL) {
        return JNI_ERR;
    }
#elif defined HAVE_WINDOWS_H
//#else
fail
#endif
            
    return JNI_VERSION_1_4;
}

JNIEXPORT void JNICALL JNI_OnUnLoad(JavaVM *jvm, void *reserved) {
    JNIEnv *env;
#ifdef HAVE_TERMIOS_H    
   
    spsw_fd = 0;
    spsw_closeEventReadFd = 0;
    spsw_closeEventWriteFd = 0;
    spsw_interByteReadTimeout = 0;
    spsw_pollReadTimeout = 0;
    spsw_pollWriteTimeout = 0;
    spsw_portName = 0;
#elif defined HAVE_WINDOWS_H
//#else
fail
#endif

    if ((*jvm)->GetEnv(jvm, (void **) &env, JNI_VERSION_1_2)) {
        return;
    }
}


#ifdef __cplusplus
}
#endif