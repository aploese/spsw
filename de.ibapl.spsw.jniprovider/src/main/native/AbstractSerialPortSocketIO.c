#include "spsw-jni.h"

#include "de_ibapl_spsw_jniprovider_AbstractSerialPortSocket.h"

#ifndef HAVE_WINDOWS_H
#include <stdlib.h>
#endif

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
 * Method:    readBytes
 * Signature: ([BII)I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_readBytes___3BII
(JNIEnv *env, jobject sps, jbyteArray bytes, jint off, jint len) {

    jbyte *lpBuffer = (jbyte*) malloc(len);

    jint result = readBuffer(env, sps, lpBuffer, len);
    if (result > 0) {
        (*env)->SetByteArrayRegion(env, bytes, off, result, lpBuffer);
    }
    free(lpBuffer);
    return result;
}

/*
 * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
 * Method:    readBytes
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_readBytes___3B
(JNIEnv *env, jobject sps, jbyteArray bytes) {
    jbyte *lpBuffer = (*env)->GetByteArrayElements(env, bytes, NULL);
    jsize len = (*env)->GetArrayLength(env, bytes);
    jint result = readBuffer(env, sps, lpBuffer, len);
    (*env)->ReleaseByteArrayElements(env, bytes, lpBuffer, 0);
    return result;
}

/*
 * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
 * Method:    readSingle
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_readSingle(
        JNIEnv *env, jobject sps) {

    jbyte readBuff;
    int result = readBuffer(env, sps, &readBuff, 1);
    if (result == 1) {
        return readBuff & 0xFF;
    } else {
        return result;
    }
}

/*
 * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
 * Method:    read
 * Signature: (Ljava/nio/ByteBuffer;II)I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_read
(JNIEnv *env, jobject sps, jobject byteBuffer, jint pos, jint len) {
    return readBuffer(env, sps, (*env)->GetDirectBufferAddress(env, byteBuffer) + pos, len);
}


/*
 * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
 * Method:    writeBytes
 * Signature: ([BII)V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_writeBytes___3BII
(JNIEnv *env, jobject sps, jbyteArray bytes, jint off, jint len) {
    void *buff = malloc(len);
    (*env)->GetByteArrayRegion(env, bytes, off, len, buff);
    if ((*env)->ExceptionOccurred(env)) {
        return;
    }
    writeBuffer(env, sps, buff, len);
    free(buff);
}

/*
 * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
 * Method:    write
 * Signature: (Ljava/nio/ByteBuffer;II)I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_write
(JNIEnv *env, jobject sps, jobject byteBuffer, jint pos, jint len) {
    return writeBuffer(env, sps, (*env)->GetDirectBufferAddress(env, byteBuffer) + pos, len);
}

/*
 * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
 * Method:    writeBytes
 * Signature: ([B)V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_writeBytes___3B
(JNIEnv *env, jobject sps, jbyteArray bytes) {
    jbyte *lpBuffer = (*env)->GetByteArrayElements(env, bytes, NULL);
    jsize len = (*env)->GetArrayLength(env, bytes);
    writeBuffer(env, sps, lpBuffer, len);
    //We did not change anything, so there is no need to copy it back
    (*env)->ReleaseByteArrayElements(env, bytes, lpBuffer, JNI_ABORT);
}

/*
 * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
 * Method:    writeSingle
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_writeSingle
(JNIEnv *env, jobject sps, jint b) {
    writeBuffer(env, sps, &b, 1);
}

#ifdef __cplusplus
}
#endif