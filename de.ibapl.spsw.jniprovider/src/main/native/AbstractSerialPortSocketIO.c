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

#include "de_ibapl_spsw_jniprovider_AbstractSerialPortSocket.h"

#include <stdint.h>

#ifndef HAVE_WINDOWS_H
#include <stdlib.h>
#include <unistd.h>
#endif
/* The maximum size of a stack-allocated buffer.
 */
#define MAX_STACK_BUF_SIZE 8192


#ifdef __cplusplus
extern "C" {
#endif

    /*
     * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
     * Method:    readBytes_ArgsOK
     * Signature: ([BII)I
     */
    JNIEXPORT jint JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_readBytes_1ArgsOK___3BII
    (JNIEnv *env, jobject sps, jbyteArray bytes, jint off, jint len) {
        jbyte stackBuf[len > MAX_STACK_BUF_SIZE ? 0 : len];
        jbyte *_buf = NULL;
        if (len > MAX_STACK_BUF_SIZE) {
            _buf = malloc((uint32_t)len);
            if (_buf == NULL) {
                throw_OutOfMemoryError(env, "Can`t aquire native memory");
                return 0;
            }
        } else {
            _buf = stackBuf;
        }

        int32_t result = readBuffer(env, sps, _buf, len);
        if (result > 0) {
            (*env)->SetByteArrayRegion(env, bytes, off, result, _buf);
        }
        if (_buf != stackBuf) {
            free(_buf);
        }
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
        int32_t result = readBuffer(env, sps, lpBuffer, len);
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
        int32_t result = readBuffer(env, sps, &readBuff, 1);
        switch (result) {
            case -1:
                return -1;
            case 0:
                return -1;
            case 1:
                return readBuff & 0xFF;
            default:
                throw_IllegalArgumentException(env, "Saveguard for Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_readSingle read single returnd unexpected result.");
                return (int32_t)result;
        }
    }

    /*
     * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
     * Method:    read_ArgsOK
     * Signature: (Ljava/nio/ByteBuffer;II)I
     */
    JNIEXPORT jint JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_read_1ArgsOK
    (JNIEnv *env, jobject sps, jobject byteBuffer, jint pos, jint len) {
        return readBuffer(env, sps, (*env)->GetDirectBufferAddress(env, byteBuffer) + pos, len);
    }

    /*
     * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
     * Method:    writeBytes_ArgsOK
     * Signature: ([BII)V
     */
    JNIEXPORT void JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_writeBytes_1ArgsOK___3BII
    (JNIEnv *env, jobject sps, jbyteArray bytes, jint off, jint len) {
        jbyte stackBuf[len > MAX_STACK_BUF_SIZE ? 0 : len]; 
        jbyte *_buf = NULL;
        if (len > MAX_STACK_BUF_SIZE) {
            _buf = malloc((uint32_t)len);
            if (_buf == NULL) {
                throw_OutOfMemoryError(env, "Can`t aquire native memory");
                return;
            }
        } else {
            _buf = stackBuf;
        }
        (*env)->GetByteArrayRegion(env, bytes, off, len, _buf);
        if ((*env)->ExceptionOccurred(env)) {
            return;
        }
        writeBuffer(env, sps, _buf, len);
        if (_buf != stackBuf) {
            free(_buf);
        }
    }

    /*
     * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
     * Method:    write_ArgsOK
     * Signature: (Ljava/nio/ByteBuffer;II)I
     */
    JNIEXPORT jint JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_write_1ArgsOK
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
        //conversation from jsize to unsigned size_t
        //How to check if sizes match at compile time ???
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