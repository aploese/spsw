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

#include "de_ibapl_spsw_jniprovider_GenericWinSerialPortSocket.h"

#ifdef __cplusplus
extern "C" {
#endif

    int readBuffer(JNIEnv *env, jobject sps, void *buff, int len) {
        HANDLE hFile = (HANDLE) (uintptr_t) (*env)->GetLongField(env, sps, spsw_fd);

        DWORD dwBytesRead = -1;
        OVERLAPPED overlapped;
        overlapped.Offset = 0;
        overlapped.OffsetHigh = 0;
        overlapped.hEvent = CreateEventA(NULL, TRUE, FALSE, NULL);

        if (!ReadFile(hFile, buff, len, NULL, &overlapped)) {

            if (GetLastError() != ERROR_IO_PENDING) {
                if ((HANDLE) (uintptr_t) (*env)->GetLongField(env, sps, spsw_fd) == INVALID_HANDLE_VALUE) {
                    throw_AsynchronousCloseException(env);
                } else {
                    throw_IOException_NativeError(env,
                            "Error readBytes(GetLastError)");
                }
                CloseHandle(overlapped.hEvent);
                return dwBytesRead;
            }

            //overlapped path
            if (WaitForSingleObject(overlapped.hEvent, INFINITE) != WAIT_OBJECT_0) {
                if ((HANDLE) (uintptr_t) (*env)->GetLongField(env, sps, spsw_fd) == INVALID_HANDLE_VALUE) {
                    throw_AsynchronousCloseException(env);
                } else {
                    throw_IOException_NativeError(env,
                            "Error readBytes (WaitForSingleObject)");
                }
                CloseHandle(overlapped.hEvent);
                return dwBytesRead;
            }

        }

        if (!GetOverlappedResult(hFile, &overlapped, &dwBytesRead, FALSE)) {
            CloseHandle(overlapped.hEvent);
            if ((HANDLE) (uintptr_t) (*env)->GetLongField(env, sps, spsw_fd) == INVALID_HANDLE_VALUE) {
                throw_AsynchronousCloseException(env);
            } else {
                throw_InterruptedIOExceptionWithError(env, dwBytesRead,
                        "Error readBytes (GetOverlappedResult)");
            }
            return dwBytesRead;
        }

        CloseHandle(overlapped.hEvent);

        if (dwBytesRead > 0) {
            //Success
            return dwBytesRead;
        } else if (dwBytesRead == 0) {
            if ((HANDLE) (uintptr_t) (*env)->GetLongField(env, sps, spsw_fd) == INVALID_HANDLE_VALUE) {
                throw_AsynchronousCloseException(env);
            } else {
                throw_TimeoutIOException(env, dwBytesRead);
            }
            return -1;
        } else {
            throw_IOException_NativeError(env,
                    "Should never happen! readBytes dwBytes < 0");
            return dwBytesRead;
        }

        throw_IOException_NativeError(env,
                "Should never happen! readBytes fall trough");
        return dwBytesRead;
    }

    /*
     * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
     * Method:    sendBreak0
     * Signature: (I)V
     */
    JNIEXPORT void JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_sendBreak0
    (JNIEnv *env, jobject sps, jint duration) {

        if (duration <= 0) {
            throw_IllegalArgumentException(env, "sendBreak duration must be grater than 0");
            return;
        }

        HANDLE hFile = (HANDLE) (uintptr_t) (*env)->GetLongField(env, sps, spsw_fd);

        if (!SetCommBreak(hFile)) {
            throw_ClosedOrNativeException(env, sps, "sendBreak SetCommBreak");
            return;
        }

        Sleep(duration);

        if (!ClearCommBreak(hFile)) {
            throw_ClosedOrNativeException(env, sps, "sendBreak ClearCommBreak");
            return;
        }
    }

    int writeBuffer(JNIEnv *env, jobject sps, void *buff, int len) {
        HANDLE hFile = (HANDLE) (uintptr_t) (*env)->GetLongField(env, sps, spsw_fd);

        DWORD dwBytesWritten = -1;
        OVERLAPPED overlapped;
        overlapped.Offset = 0;
        overlapped.OffsetHigh = 0;
        overlapped.hEvent = CreateEventA(NULL, TRUE, FALSE, NULL);
        if (!WriteFile(hFile, buff, len, NULL, &overlapped)) {

            if (GetLastError() != ERROR_IO_PENDING) {
                CloseHandle(overlapped.hEvent);
                if ((HANDLE) (uintptr_t) (*env)->GetLongField(env, sps, spsw_fd) == INVALID_HANDLE_VALUE) {
                    throw_AsynchronousCloseException(env);
                } else {
                    throw_InterruptedIOExceptionWithError(env, 0, "unknown port error 1 writeBytes");
                }
                return dwBytesWritten;
            }

            if (WaitForSingleObject(overlapped.hEvent, INFINITE) != WAIT_OBJECT_0) {
                CloseHandle(overlapped.hEvent);
                if ((HANDLE) (uintptr_t) (*env)->GetLongField(env, sps, spsw_fd) == INVALID_HANDLE_VALUE) {
                    throw_AsynchronousCloseException(env);
                } else {
                    throw_InterruptedIOExceptionWithError(env, 0, "Error writeBytes (WaitForSingleObject)");
                }
                return dwBytesWritten;
            }

        }

        if (!GetOverlappedResult(hFile, &overlapped, &dwBytesWritten, FALSE)) {
            CloseHandle(overlapped.hEvent);
            if ((HANDLE) (uintptr_t) (*env)->GetLongField(env, sps, spsw_fd) == INVALID_HANDLE_VALUE) {
                throw_AsynchronousCloseException(env);
            } else {
                throw_InterruptedIOExceptionWithError(env, 0, "Error writeBytes (GetOverlappedResult)");
            }
            return dwBytesWritten;
        }

        CloseHandle(overlapped.hEvent);
        if (dwBytesWritten != len) {
            if ((HANDLE) (uintptr_t) (*env)->GetLongField(env, sps, spsw_fd) == INVALID_HANDLE_VALUE) {
                throw_AsynchronousCloseException(env);
                return dwBytesWritten;
            } else {
                if (GetLastError() == ERROR_IO_PENDING) {
                    throw_TimeoutIOException(env, dwBytesWritten);
                } else {
                    throw_InterruptedIOExceptionWithError(env, dwBytesWritten, "Error writeBytes too view written");
                }
                return dwBytesWritten;
            }
        }
        //Success
        return dwBytesWritten;
    }

    /*
     * Class:     de_ibapl_spsw_jniprovider_GenericWinSerialPortSocket
     * Method:    sendXOFF
     * Signature: ()V
     */
    JNIEXPORT void JNICALL Java_de_ibapl_spsw_jniprovider_GenericWinSerialPortSocket_sendXOFF
    (JNIEnv *env, jobject sps) {
        throw_IOException_NativeError(env, "sendXOFF not implemented yet");
    }

    /*
     * Class:     de_ibapl_spsw_jniprovider_GenericWinSerialPortSocket
     * Method:    sendXON
     * Signature: ()V
     */
    JNIEXPORT void JNICALL Java_de_ibapl_spsw_jniprovider_GenericWinSerialPortSocket_sendXON
    (JNIEnv *env, jobject sps) {
        throw_IOException_NativeError(env, "sendXON not implemented yet");
    }


#ifdef __cplusplus
}
#endif