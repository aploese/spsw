/**
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
#ifndef _SPSW_JNI_H
#define _SPSW_JNI_H

#include "../../../config.h"

#ifdef __cplusplus
extern "C" {
#endif

#if defined HAVE_TERMIOS_H
#include <jni.h>
#include <termios.h>

#undef INVALID_FD
#define INVALID_FD -1
#define GENERIC_TERMIOS_SERIAL_PORT_SOCKET "de/ibapl/spsw/jniprovider/GenericTermiosSerialPortSocket"

    extern jfieldID spsw_closeEventReadFd; /* id for field 'closeEventReadFd'  */
    extern jfieldID spsw_closeEventWriteFd; /* id for field 'closeEventWriteFd'  */
    extern jfieldID spsw_interByteReadTimeout; // id for field interByteReadTimeout
    extern jfieldID spsw_pollReadTimeout; // id for field overallReadTimeout
    extern jfieldID spsw_pollWriteTimeout; // id for field overallWriteTimeout
    int setParams(JNIEnv *env, jobject sps, struct termios *settings, jint paramBitSet);

#elif defined HAVE_WINDOWS_H
#define GENERIC_WIN_SERIAL_PORT_SOCKET "de/ibapl/spsw/jniprovider/GenericWinSerialPortSocket"
#define _WIN32_WINNT 0x600
#include <jni.h>
#include <windows.h>
#undef INVALID_FD
#define INVALID_FD INVALID_HANDLE_VALUE
    int setParams(JNIEnv *env, jobject sps, DCB *dcb, jint paramBitSet);
#else
    fail
#endif
            //Exception names
#define IO_EXCEPTION "java/io/IOException"
#define TIMEOUT_IO_EXCEPTION "de/ibapl/spsw/api/TimeoutIOException"
#define CLASS_NOT_FOUND_EXCEPTION "java/lang/ClassNotFoundException"
#define NO_SUCH_FIELD_EXCEPTION "java/lang/NoSuchFieldException"
#define NO_SUCH_METHOD_EXCEPTION "java/lang/NoSuchMethodException"
#define ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION "java/lang/ArrayIndexOutOfBoundsException"
#define ASYNCHRONOUS_CLOSE_EXCEPTION "java/nio/channels/AsynchronousCloseException"
#define INTERRUPTED_IO_EXCEPTION "java/io/InterruptedIOException"

            //Cached
            extern jfieldID spsw_portName; /* id for field 'portName'  */

    extern jfieldID spsw_fd; /* id for field 'fd'  */


    //Cached Exceptions
    void throw_IOException_NativeError(JNIEnv *env, const char *msg);

    void throw_ClosedOrNativeException(JNIEnv *env, jobject sps, const char *message);

    void throw_IOException(JNIEnv *env, const char* msg, jstring portName);

    void throw_TimeoutIOException(JNIEnv *env, int bytesTransferred);

    void throw_AsynchronousCloseException(JNIEnv *env);

    void throw_IllegalArgumentException(JNIEnv *env, const char *msg);

    void throw_InterruptedIOExceptionWithError(JNIEnv *env, int bytesTransferred, const char *msg);

    void throw_IOException_Opend(JNIEnv *env);

    void throw_ClassNotFoundException(JNIEnv* env, const char* className);

    void throw_NoSuchFieldException(JNIEnv* env, const char* className, const char* fieldName, const char* fieldType);

    void throw_NoSuchMethodException(JNIEnv* env, const char* className, const char* fieldName, const char* fieldType);

    jclass getGlobalClassRef(JNIEnv *env, const char* className);

    jfieldID getFieldId(JNIEnv *env, const char* className, const char* fieldName, const char* fieldType);

    jfieldID getFieldIdOfClassRef(JNIEnv *env, jclass clazz, const char* className, const char* fieldName, const char* fieldType);

    jmethodID getMethodIdOfClassRef(JNIEnv *env, jclass clazz, const char* className, const char* methodName, const char* methodSignature);

    int readBuffer(JNIEnv *env, jobject sps, void *buff, int len);

    int writeBuffer(JNIEnv *env, jobject sps, void *buff, int len);

#include "de_ibapl_spsw_jniprovider_AbstractSerialPortSocket.h"
#undef SPSW_SPEED_0_BPS
#define SPSW_SPEED_0_BPS de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_SPEED_0_BPS
#undef SPSW_SPEED_50_BPS
#define SPSW_SPEED_50_BPS de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_SPEED_50_BPS
#undef SPSW_SPEED_75_BPS
#define SPSW_SPEED_75_BPS de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_SPEED_75_BPS
#undef SPSW_SPEED_110_BPS
#define SPSW_SPEED_110_BPS de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_SPEED_110_BPS
#undef SPSW_SPEED_134_BPS
#define SPSW_SPEED_134_BPS de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_SPEED_134_BPS
#undef SPSW_SPEED_150_BPS
#define SPSW_SPEED_150_BPS de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_SPEED_150_BPS
#undef SPSW_SPEED_200_BPS
#define SPSW_SPEED_200_BPS de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_SPEED_200_BPS
#undef SPSW_SPEED_300_BPS
#define SPSW_SPEED_300_BPS de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_SPEED_300_BPS
#undef SPSW_SPEED_600_BPS
#define SPSW_SPEED_600_BPS de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_SPEED_600_BPS
#undef SPSW_SPEED_1200_BPS
#define SPSW_SPEED_1200_BPS de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_SPEED_1200_BPS
#undef SPSW_SPEED_1800_BPS
#define SPSW_SPEED_1800_BPS de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_SPEED_1800_BPS
#undef SPSW_SPEED_2400_BPS
#define SPSW_SPEED_2400_BPS de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_SPEED_2400_BPS
#undef SPSW_SPEED_4800_BPS
#define SPSW_SPEED_4800_BPS de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_SPEED_4800_BPS
#undef SPSW_SPEED_9600_BPS
#define SPSW_SPEED_9600_BPS de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_SPEED_9600_BPS
#undef SPSW_SPEED_19200_BPS
#define SPSW_SPEED_19200_BPS de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_SPEED_19200_BPS
#undef SPSW_SPEED_38400_BPS
#define SPSW_SPEED_38400_BPS de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_SPEED_38400_BPS
#undef SPSW_SPEED_57600_BPS
#define SPSW_SPEED_57600_BPS de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_SPEED_57600_BPS
#undef SPSW_SPEED_115200_BPS
#define SPSW_SPEED_115200_BPS de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_SPEED_115200_BPS
#undef SPSW_SPEED_230400_BPS
#define SPSW_SPEED_230400_BPS de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_SPEED_230400_BPS
#undef SPSW_SPEED_460800_BPS
#define SPSW_SPEED_460800_BPS de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_SPEED_460800_BPS
#undef SPSW_SPEED_500000_BPS
#define SPSW_SPEED_500000_BPS de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_SPEED_500000_BPS
#undef SPSW_SPEED_576000_BPS
#define SPSW_SPEED_576000_BPS de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_SPEED_576000_BPS
#undef SPSW_SPEED_921600_BPS
#define SPSW_SPEED_921600_BPS de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_SPEED_921600_BPS
#undef SPSW_SPEED_1000000_BPS
#define SPSW_SPEED_1000000_BPS de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_SPEED_1000000_BPS
#undef SPSW_SPEED_1152000_BPS
#define SPSW_SPEED_1152000_BPS de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_SPEED_1152000_BPS
#undef SPSW_SPEED_1500000_BPS
#define SPSW_SPEED_1500000_BPS de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_SPEED_1500000_BPS
#undef SPSW_SPEED_2000000_BPS
#define SPSW_SPEED_2000000_BPS de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_SPEED_2000000_BPS
#undef SPSW_SPEED_2500000_BPS
#define SPSW_SPEED_2500000_BPS de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_SPEED_2500000_BPS
#undef SPSW_SPEED_3000000_BPS
#define SPSW_SPEED_3000000_BPS de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_SPEED_3000000_BPS
#undef SPSW_SPEED_3500000_BPS
#define SPSW_SPEED_3500000_BPS de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_SPEED_3500000_BPS
#undef SPSW_SPEED_4000000_BPS
#define SPSW_SPEED_4000000_BPS de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_SPEED_4000000_BPS
#undef SPSW_SPEED_MASK
#define SPSW_SPEED_MASK de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_SPEED_MASK
#undef SPSW_DATA_BITS_DB5
#define SPSW_DATA_BITS_DB5 de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_DATA_BITS_DB5
#undef SPSW_DATA_BITS_DB6
#define SPSW_DATA_BITS_DB6 de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_DATA_BITS_DB6
#undef SPSW_DATA_BITS_DB7
#define SPSW_DATA_BITS_DB7 de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_DATA_BITS_DB7
#undef SPSW_DATA_BITS_DB8
#define SPSW_DATA_BITS_DB8 de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_DATA_BITS_DB8
#undef SPSW_DATA_BITS_MASK
#define SPSW_DATA_BITS_MASK de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_DATA_BITS_MASK
#undef SPSW_FLOW_CONTROL_NONE
#define SPSW_FLOW_CONTROL_NONE de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_FLOW_CONTROL_NONE
#undef SPSW_FLOW_CONTROL_RTS_CTS_IN
#define SPSW_FLOW_CONTROL_RTS_CTS_IN de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_FLOW_CONTROL_RTS_CTS_IN
#undef SPSW_FLOW_CONTROL_RTS_CTS_OUT
#define SPSW_FLOW_CONTROL_RTS_CTS_OUT de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_FLOW_CONTROL_RTS_CTS_OUT
#undef SPSW_FLOW_CONTROL_XON_XOFF_IN
#define SPSW_FLOW_CONTROL_XON_XOFF_IN de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_FLOW_CONTROL_XON_XOFF_IN
#undef SPSW_FLOW_CONTROL_XON_XOFF_OUT
#define SPSW_FLOW_CONTROL_XON_XOFF_OUT de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_FLOW_CONTROL_XON_XOFF_OUT
#undef SPSW_FLOW_CONTROL_MASK
#define SPSW_FLOW_CONTROL_MASK de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_FLOW_CONTROL_MASK
#undef SPSW_STOP_BITS_1
#define SPSW_STOP_BITS_1 de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_STOP_BITS_1
#undef SPSW_STOP_BITS_1_5
#define SPSW_STOP_BITS_1_5 de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_STOP_BITS_1_5
#undef SPSW_STOP_BITS_2
#define SPSW_STOP_BITS_2 de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_STOP_BITS_2
#undef SPSW_STOP_BITS_MASK
#define SPSW_STOP_BITS_MASK de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_STOP_BITS_MASK
#undef SPSW_PARITY_NONE
#define SPSW_PARITY_NONE de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_PARITY_NONE
#undef SPSW_PARITY_ODD
#define SPSW_PARITY_ODD de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_PARITY_ODD
#undef SPSW_PARITY_EVEN
#define SPSW_PARITY_EVEN de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_PARITY_EVEN
#undef SPSW_PARITY_MARK
#define SPSW_PARITY_MARK de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_PARITY_MARK
#undef SPSW_PARITY_SPACE
#define SPSW_PARITY_SPACE de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_PARITY_SPACE
#undef SPSW_PARITY_MASK
#define SPSW_PARITY_MASK de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_PARITY_MASK
#undef SPSW_NO_PARAMS_TO_SET
#define SPSW_NO_PARAMS_TO_SET de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_NO_PARAMS_TO_SET

    jboolean initExceptions(JNIEnv* env);
    void cleanupExceptions(JNIEnv* env);

#ifdef __cplusplus
}
#endif

#endif
