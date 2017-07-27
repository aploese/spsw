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
#include <jni.h>
#include <stdlib.h>
#include <windows.h>
#include "de_ibapl_spsw_provider_GenericWinSerialPortSocket.h"

//#include <iostream>

#undef FLOW_CONTROL_NONE
#define FLOW_CONTROL_NONE de_ibapl_spsw_provider_GenericWinSerialPortSocket_FLOW_CONTROL_NONE
#undef FLOW_CONTROL_RTS_CTS_IN
#define FLOW_CONTROL_RTS_CTS_IN de_ibapl_spsw_provider_GenericWinSerialPortSocket_FLOW_CONTROL_RTS_CTS_IN
#undef FLOW_CONTROL_RTS_CTS_OUT
#define FLOW_CONTROL_RTS_CTS_OUT de_ibapl_spsw_provider_GenericWinSerialPortSocket_FLOW_CONTROL_RTS_CTS_OUT
#undef FLOW_CONTROL_XON_XOFF_IN
#define FLOW_CONTROL_XON_XOFF_IN de_ibapl_spsw_provider_GenericWinSerialPortSocket_FLOW_CONTROL_XON_XOFF_IN
#undef FLOW_CONTROL_XON_XOFF_OUT
#define FLOW_CONTROL_XON_XOFF_OUT de_ibapl_spsw_provider_GenericWinSerialPortSocket_FLOW_CONTROL_XON_XOFF_OUT

jfieldID spsw_portName; /* id for field 'portName'  */
jfieldID spsw_fd; /* id for field 'fd'  */
jfieldID spsw_open; /* id for field 'open'  */

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved) {
    JNIEnv *env;
    jint getEnvResult = ((*jvm)->GetEnv(jvm, (void **) &env, JNI_VERSION_1_2));
    if (getEnvResult != JNI_OK) {
        return getEnvResult;
    }
    jclass spswClass = (*env)->FindClass(env, "Lde/ibapl/spsw/provider/GenericWinSerialPortSocket;");
    spsw_fd = (*env)->GetFieldID(env, spswClass, "fd", "J");
    spsw_open = (*env)->GetFieldID(env, spswClass, "open", "Z");
    spsw_portName = (*env)->GetFieldID(env, spswClass, "portName", "Ljava/lang/String;");

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

    jint getEnvResult = ((*jvm)->GetEnv(jvm, (void **) &env, JNI_VERSION_1_2));
    if (getEnvResult != JNI_OK) {
        return;
    }

    // mark that the lib was unloaded
    jclass serialPortSocketFactoryImpl = (*env)->FindClass(env, "Lde/ibapl/spsw/provider/SerialPortSocketFactoryImpl;");
    jfieldID spsw_libLoaded = (*env)->GetStaticFieldID(env, serialPortSocketFactoryImpl, "libLoaded", "Z");
    (*env)->SetStaticBooleanField(env, serialPortSocketFactoryImpl, spsw_libLoaded, JNI_FALSE);
}

/*
 * Class:     de_ibapl_spsw_provider_GenericWinSerialPortSocket
 * Method:    getWindowsBasedPortNames
 * Signature: (Z)[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_getWindowsBasedPortNames
(JNIEnv *env, jclass clazz, jboolean value) {
    HKEY phkResult;
    LPCSTR lpSubKey = "HARDWARE\\DEVICEMAP\\SERIALCOMM\\";
    jobjectArray returnArray = NULL;
    if (RegOpenKeyExA(HKEY_LOCAL_MACHINE, lpSubKey, 0, KEY_READ, &phkResult) == ERROR_SUCCESS) {
        boolean hasMoreElements = TRUE;
        DWORD keysCount = 0;
        char valueName[256];
        DWORD valueNameSize;
        DWORD enumResult;
        while (hasMoreElements) {
            valueNameSize = 256;
            enumResult = RegEnumValueA(phkResult, keysCount, valueName, &valueNameSize, NULL, NULL, NULL, NULL);
            if (enumResult == ERROR_SUCCESS) {
                keysCount++;
            } else if (enumResult == ERROR_NO_MORE_ITEMS) {
                hasMoreElements = FALSE;
            } else {
                hasMoreElements = FALSE;
            }
        }
        if (keysCount > 0) {
            jclass stringClass = (*env)->FindClass(env, "java/lang/String");
            returnArray = (*env)->NewObjectArray(env, (jsize) keysCount, stringClass, NULL);
            char lpValueName[256];
            DWORD lpcchValueName;
            byte lpData[256];
            DWORD lpcbData;
            DWORD result;
            for (DWORD i = 0; i < keysCount; i++) {
                lpcchValueName = 256;
                lpcbData = 256;
                result = RegEnumValueA(phkResult, i, lpValueName, &lpcchValueName, NULL, NULL, lpData, &lpcbData);
                if (result == ERROR_SUCCESS) {
                    (*env)->SetObjectArrayElement(env, returnArray, i, (*env)->NewStringUTF(env, (char*) lpData));
                }
            }
        }
        CloseHandle(phkResult);
    }
    return returnArray;
}


// Helper method

static void throw_SerialPortException_With_PortName(JNIEnv *env, const char *msg, jstring portName) {
    const char* port = (*env)->GetStringUTFChars(env, portName, JNI_FALSE);
    char buf[2048];
    snprintf(buf, 2048, "%s (%s) : Unknown port error %d", msg, port, (int) GetLastError());
    (*env)->ReleaseStringUTFChars(env, portName, port);
    jclass speClass = (*env)->FindClass(env, "de/ibapl/spsw/api/SerialPortException");
    jmethodID speConstructor = (*env)->GetMethodID(env, speClass, "<init>", "(Ljava/lang/String;Ljava/lang/String;)V");
    jobject speEx = (*env)->NewObject(env, speClass, speConstructor, portName, (*env)->NewStringUTF(env, buf));
    (*env)->Throw(env, speEx);
    (*env)->DeleteLocalRef(env, speClass);
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

/*REMOVE? - NOT USED
static void throw_PermissionDeniedException(JNIEnv *env, jstring portName) {
    const char* port = (*env)->GetStringUTFChars(env, portName, JNI_FALSE);
    (*env)->ThrowNew(env, (*env)->FindClass(env, "de/ibapl/spsw/api/PermissionDeniedException"), port);
    (*env)->ReleaseStringUTFChars(env, portName, port);
}
 */

static void throw_NotASerialPortException(JNIEnv *env, jstring portName) {
    const char* port = (*env)->GetStringUTFChars(env, portName, JNI_FALSE);
    (*env)->ThrowNew(env, (*env)->FindClass(env, "de/ibapl/spsw/api/NotASerialPortException"), port);
    (*env)->ReleaseStringUTFChars(env, portName, port);
}

static jboolean getCommModemStatus(JNIEnv *env, jobject object, DWORD bitMask) {
    DWORD lpModemStat;

#if defined(__i386)
    HANDLE hFile = (HANDLE) (DWORD) (*env)->GetLongField(env, object, spsw_fd);
#elif defined(__x86_64)
    HANDLE hFile = (HANDLE) (*env)->GetLongField(env, object, spsw_fd);
#endif

    if (!GetCommModemStatus(hFile, &lpModemStat)) {
        GetLastError();
        throw_SerialPortException_With_PortName(env, "Can't get GetCommModemStatus", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return JNI_FALSE;
    }
    if ((lpModemStat & bitMask) == bitMask) {
        return JNI_TRUE;
    } else {
        return JNI_FALSE;
    }
}

/*
 * Port opening.
 *
 * In 2.2.0 added useTIOCEXCL (not used in Windows, only for compatibility with _nix version)
 * 
 *  
 * Use overlapped otherwise any blocked ReadFile will block a WriteFile.
 * 
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_open(JNIEnv *env, jobject object, jstring portName, jint portMode) {
    char prefix[] = "\\\\.\\";
    const char* port = (*env)->GetStringUTFChars(env, portName, JNI_FALSE);

    //string concat fix
    char portFullName[strlen(prefix) + strlen(port) + 1];
    strcpy(portFullName, prefix);
    strcat(portFullName, port);
    (*env)->ReleaseStringUTFChars(env, portName, port);

    HANDLE hComm = CreateFile(portFullName,
            GENERIC_READ | GENERIC_WRITE,
            0,
            NULL,
            OPEN_EXISTING,
            FILE_FLAG_OVERLAPPED,
            NULL);

    if (hComm == INVALID_HANDLE_VALUE) {

#if defined(__i386)
        (*env)->SetLongField(env, object, spsw_fd, (jlong) (DWORD) INVALID_HANDLE_VALUE);
#elif defined(__x86_64)
        (*env)->SetLongField(env, object, spsw_fd, (jlong) INVALID_HANDLE_VALUE);
#endif

        switch (GetLastError()) {
            case ERROR_ACCESS_DENIED:
                throw_PortBusyException(env, portName);
                break;
            case ERROR_FILE_NOT_FOUND:
                throw_PortNotFoundException(env, portName);
                break;
            default:
                throw_SerialPortException_With_PortName(env, "Open", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        }
        return;
    }

    DCB dcb;
    if (!GetCommState(hComm, &dcb)) {
        CloseHandle(hComm); //since 2.7.0

#if defined(__i386)
        (*env)->SetLongField(env, object, spsw_fd, (jlong) (DWORD) INVALID_HANDLE_VALUE);
#elif defined(__x86_64)
        (*env)->SetLongField(env, object, spsw_fd, (jlong) INVALID_HANDLE_VALUE);
#endif

        throw_NotASerialPortException(env, portName);
        return;
    }

    COMMTIMEOUTS lpCommTimeouts;
    if (!GetCommTimeouts(hComm, &lpCommTimeouts)) {
        CloseHandle(hComm);

#if defined(__i386)
        (*env)->SetLongField(env, object, spsw_fd, (jlong) (DWORD) INVALID_HANDLE_VALUE);
#elif defined(__x86_64)
        (*env)->SetLongField(env, object, spsw_fd, (jlong) INVALID_HANDLE_VALUE);
#endif

        throw_SerialPortException_With_PortName(env, "Open GetCommTimeouts", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return;
    }

    switch (portMode) {
        case de_ibapl_spsw_provider_GenericWinSerialPortSocket_PORT_MODE_UNCHANGED:
            break;
        case de_ibapl_spsw_provider_GenericWinSerialPortSocket_PORT_MODE_RAW:
            lpCommTimeouts.ReadIntervalTimeout = 100; // wait max a 1/10 sec for new chars after receiving the last char
            lpCommTimeouts.ReadTotalTimeoutConstant = 0;
            lpCommTimeouts.ReadTotalTimeoutMultiplier = 0;
            lpCommTimeouts.WriteTotalTimeoutConstant = 0;
            lpCommTimeouts.WriteTotalTimeoutMultiplier = 0;
            break;
        default:
            CloseHandle(hComm);

#if defined(__i386)
            (*env)->SetLongField(env, object, spsw_fd, (jlong) (DWORD) INVALID_HANDLE_VALUE);
#elif defined(__x86_64)
            (*env)->SetLongField(env, object, spsw_fd, (jlong) INVALID_HANDLE_VALUE);
#endif

            throw_SerialPortException_With_PortName(env, "Unknown terminal mode giving up", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
            return;
    }

    if (!SetCommTimeouts(hComm, &lpCommTimeouts)) {
        CloseHandle(hComm);

#if defined(__i386)
        (*env)->SetLongField(env, object, spsw_fd, (jlong) (DWORD) INVALID_HANDLE_VALUE);
#elif defined(__x86_64)
        (*env)->SetLongField(env, object, spsw_fd, (jlong) INVALID_HANDLE_VALUE);
#endif

        throw_SerialPortException_With_PortName(env, "Open SetCommTimeouts", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return;
    }

#if defined(__i386)
    (*env)->SetLongField(env, object, spsw_fd, (jlong) (DWORD) hComm);
#elif defined(__x86_64)
    (*env)->SetLongField(env, object, spsw_fd, (jlong) hComm);
#endif
}

/*
 * Port closing
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_close0
(JNIEnv *env, jobject object) {

#if defined(__i386)
    HANDLE hFile = (HANDLE) (DWORD) (*env)->GetLongField(env, object, spsw_fd);
    (*env)->SetLongField(env, object, spsw_fd, (jlong) (DWORD) INVALID_HANDLE_VALUE);
#elif defined(__x86_64)
    HANDLE hFile = (HANDLE) (*env)->GetLongField(env, object, spsw_fd);
    (*env)->SetLongField(env, object, spsw_fd, (jlong) INVALID_HANDLE_VALUE);
#endif

    if (!CloseHandle(hFile)) {
        throw_SerialPortException_With_PortName(env, "Can't close port", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
    }
}

/*
 * Change RTS line state (ON || OFF)
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_setRTS
(JNIEnv *env, jobject object, jboolean enabled) {
    DWORD dwFunc = (enabled == JNI_TRUE) ? SETRTS : CLRRTS;

#if defined(__i386)
    HANDLE hFile = (HANDLE) (DWORD) (*env)->GetLongField(env, object, spsw_fd);
#elif defined(__x86_64)
    HANDLE hFile = (HANDLE) (*env)->GetLongField(env, object, spsw_fd);
#endif

    if (!EscapeCommFunction(hFile, dwFunc)) {
        throw_SerialPortException_With_PortName(env, "Can't set/clear RTS", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
    }
}

JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_sendXON
(JNIEnv *env, jobject object) {
    throw_SerialPortException_With_PortName(env, "setXON not implementred yet", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
}

JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_sendXOFF
(JNIEnv *env, jobject object) {
    throw_SerialPortException_With_PortName(env, "setXOFF not implementred yet", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
}

/*
 * Change DTR line state (ON || OFF)
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_setDTR
(JNIEnv *env, jobject object, jboolean enabled) {
    DWORD dwFunc = (enabled == JNI_TRUE) ? SETDTR : CLRDTR;

#if defined(__i386)
    HANDLE hFile = (HANDLE) (DWORD) (*env)->GetLongField(env, object, spsw_fd);
#elif defined(__x86_64)
    HANDLE hFile = (HANDLE) (*env)->GetLongField(env, object, spsw_fd);
#endif

    if (!EscapeCommFunction(hFile, dwFunc)) {
        throw_SerialPortException_With_PortName(env, "Can't set/clear DTR", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
    }
}

/* 
 * BRK line status changing (ON || OFF)
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_setBreak
(JNIEnv *env, jobject object, jboolean enabled) {
    DWORD dwFunc = (enabled == JNI_TRUE) ? SETBREAK : CLRBREAK;

#if defined(__i386)
    HANDLE hFile = (HANDLE) (DWORD) (*env)->GetLongField(env, object, spsw_fd);
#elif defined(__x86_64)
    HANDLE hFile = (HANDLE) (*env)->GetLongField(env, object, spsw_fd);
#endif

    if (!EscapeCommFunction(hFile, dwFunc)) {
        throw_SerialPortException_With_PortName(env, "Can't set/clear BREAK", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
    }
}

JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_setXONChar
(JNIEnv *env, jobject object, jchar c) {
    DCB dcb;

#if defined(__i386)
    HANDLE hFile = (HANDLE) (DWORD) (*env)->GetLongField(env, object, spsw_fd);
#elif defined(__x86_64)
    HANDLE hFile = (HANDLE) (*env)->GetLongField(env, object, spsw_fd);
#endif

    if (!GetCommState(hFile, &dcb)) {
        throw_SerialPortException_With_PortName(env, "setXONChar GetCommState", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return;
    }
    dcb.XonChar = c;
    if (!SetCommState(hFile, &dcb)) {
        throw_SerialPortException_With_PortName(env, "setXONChar SetCommState", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
    }
}

JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_setXOFFChar
(JNIEnv *env, jobject object, jchar c) {
    DCB dcb;

#if defined(__i386)
    HANDLE hFile = (HANDLE) (DWORD) (*env)->GetLongField(env, object, spsw_fd);
#elif defined(__x86_64)
    HANDLE hFile = (HANDLE) (*env)->GetLongField(env, object, spsw_fd);
#endif

    if (!GetCommState(hFile, &dcb)) {
        throw_SerialPortException_With_PortName(env, "setXOFFChar GetCommState", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return;
    }
    dcb.XoffChar = c;
    if (!SetCommState(hFile, &dcb)) {
        throw_SerialPortException_With_PortName(env, "setXOFFChar SetCommState", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
    }
}

JNIEXPORT jchar JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_getXONChar
(JNIEnv *env, jobject object) {
    DCB dcb;

#if defined(__i386)
    HANDLE hFile = (HANDLE) (DWORD) (*env)->GetLongField(env, object, spsw_fd);
#elif defined(__x86_64)
    HANDLE hFile = (HANDLE) (*env)->GetLongField(env, object, spsw_fd);
#endif

    if (!GetCommState(hFile, &dcb)) {
        throw_SerialPortException_With_PortName(env, "getXONChar GetCommState", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return 0;
    }
    return dcb.XonChar;
}

JNIEXPORT jchar JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_getXOFFChar
(JNIEnv *env, jobject object) {
    DCB dcb;

#if defined(__i386)
    HANDLE hFile = (HANDLE) (DWORD) (*env)->GetLongField(env, object, spsw_fd);
#elif defined(__x86_64)
    HANDLE hFile = (HANDLE) (*env)->GetLongField(env, object, spsw_fd);
#endif

    if (!GetCommState(hFile, &dcb)) {
        throw_SerialPortException_With_PortName(env, "getXOFFChar GetCommState", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return 0;
    }
    return dcb.XoffChar;
}

JNIEXPORT jboolean JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_isCTS
(JNIEnv *env, jobject object) {
    return getCommModemStatus(env, object, MS_CTS_ON);
}

JNIEXPORT jboolean JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_isDSR
(JNIEnv *env, jobject object) {
    return getCommModemStatus(env, object, MS_DSR_ON);
}

JNIEXPORT jboolean JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_isIncommingRI
(JNIEnv *env, jobject object) {
    return getCommModemStatus(env, object, MS_RING_ON);
}

JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_writeSingle
(JNIEnv *env, jobject object, jint b) {

#if defined(__i386)
    HANDLE hFile = (HANDLE) (DWORD) (*env)->GetLongField(env, object, spsw_fd);
#elif defined(__x86_64)
    HANDLE hFile = (HANDLE) (*env)->GetLongField(env, object, spsw_fd);
#endif

    OVERLAPPED overlapped;
    DWORD dwBytesWritten;
    overlapped.hEvent = CreateEventA(NULL, TRUE, FALSE, NULL);

    if (!WriteFile(hFile, &b, 1, NULL, &overlapped)) {

        if (GetLastError() != ERROR_IO_PENDING) {
            CloseHandle(overlapped.hEvent);
            throw_SerialPortException_With_PortName(env, "Error writeSingle (GetLastError)", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
            return;
        }

        if (WaitForSingleObject(overlapped.hEvent, INFINITE) != WAIT_OBJECT_0) {
            CloseHandle(overlapped.hEvent);
            throw_SerialPortException_With_PortName(env, "Error writeSingle (WaitForSingleObject)", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
            return;
        }

    }

    if (!GetOverlappedResult(hFile, &overlapped, &dwBytesWritten, FALSE)) {
        CloseHandle(overlapped.hEvent);
        throw_SerialPortException_With_PortName(env, "Error writeSingle (GetOverlappedResult)", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return;
    }

    CloseHandle(overlapped.hEvent);
    if (dwBytesWritten != 1) {
        throw_SerialPortException_With_PortName(env, "Error writeSingle", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return;
    }

    //Success
    return;
}

/*
 * Write data to port
 * portHandle - port handle
 * buffer - byte array for sending
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_writeBytes
(JNIEnv *env, jobject object, jbyteArray bytes, jint off, jint len) {

    jbyte *buf = (jbyte*) malloc(len);
    (*env)->GetByteArrayRegion(env, bytes, off, len, buf);

#if defined(__i386)
    HANDLE hFile = (HANDLE) (DWORD) (*env)->GetLongField(env, object, spsw_fd);
#elif defined(__x86_64)
    HANDLE hFile = (HANDLE) (*env)->GetLongField(env, object, spsw_fd);
#endif

    DWORD dwBytesWritten;
    OVERLAPPED overlapped;
    overlapped.hEvent = CreateEventA(NULL, TRUE, FALSE, NULL);

    if (!WriteFile(hFile, buf, len, NULL, &overlapped)) {

        if (GetLastError() != ERROR_IO_PENDING) {
            CloseHandle(overlapped.hEvent);
            free(buf);
            throw_SerialPortException_With_PortName(env, "Error writeBytes (GetLastError)", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
            return;
        }

        if (WaitForSingleObject(overlapped.hEvent, INFINITE) != WAIT_OBJECT_0) {
            CloseHandle(overlapped.hEvent);
            free(buf);
            throw_SerialPortException_With_PortName(env, "Error writeBytes (WaitForSingleObject)", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
            return;
        }

    }

    if (!GetOverlappedResult(hFile, &overlapped, &dwBytesWritten, FALSE)) {
        CloseHandle(overlapped.hEvent);
        free(buf);
        throw_SerialPortException_With_PortName(env, "Error writeBytes (GetOverlappedResult)", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return;
    }

    CloseHandle(overlapped.hEvent);
    free(buf);
    if (dwBytesWritten != len) {
        throw_SerialPortException_With_PortName(env, "Error writeBytes too view written", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return;
    }

    //Success
    return;
}

JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_readSingle
(JNIEnv *env, jobject object) {

    jbyte lpBuffer;

#if defined(__i386)
    HANDLE hFile = (HANDLE) (DWORD) (*env)->GetLongField(env, object, spsw_fd);
#elif defined(__x86_64)
    HANDLE hFile = (HANDLE) (*env)->GetLongField(env, object, spsw_fd);
#endif

    OVERLAPPED overlapped;
    DWORD dwBytesRead;
    overlapped.hEvent = CreateEventA(NULL, TRUE, FALSE, NULL);

    if (!ReadFile(hFile, &lpBuffer, 1, NULL, &overlapped)) {

        if (GetLastError() != ERROR_IO_PENDING) {
            CloseHandle(overlapped.hEvent);
            throw_SerialPortException_With_PortName(env, "Error readSingle (GetLastError)", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
            return -1;
        }

        if (WaitForSingleObject(overlapped.hEvent, INFINITE) != WAIT_OBJECT_0) {
            CloseHandle(overlapped.hEvent);
            throw_SerialPortException_With_PortName(env, "Error readSingle (WaitForSingleObject)", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
            return -1;
        }

    }

    if (!GetOverlappedResult(hFile, &overlapped, &dwBytesRead, FALSE)) {
        CloseHandle(overlapped.hEvent);
        if ((*env)->GetBooleanField(env, object, spsw_open)) {
            throw_SerialPortException_With_PortName(env, "Error readSingle (GetOverlappedResult)", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
            return -1;
        } else {
            //closed
            return -1;
        }
        return -1;
    }

    CloseHandle(overlapped.hEvent);
    if (dwBytesRead == 1) {
        //Success
        return lpBuffer & 0xFF;
    } else if (dwBytesRead == 0) {
        return -1;
    }

    throw_SerialPortException_With_PortName(env, "Should never happen! readSingle falltrough", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
    return -1;
}

/*
 * Read data from port
 * portHandle - port handle
 * byteCount - count of bytes for reading
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_readBytes
(JNIEnv *env, jobject object, jbyteArray bytes, jint off, jint len) {

    jbyte *lpBuffer = (jbyte*) malloc(len);

#if defined(__i386)
    HANDLE hFile = (HANDLE) (DWORD) (*env)->GetLongField(env, object, spsw_fd);
#elif defined(__x86_64)
    HANDLE hFile = (HANDLE) (*env)->GetLongField(env, object, spsw_fd);
#endif

    DWORD dwBytesRead;
    OVERLAPPED overlapped;
    overlapped.hEvent = CreateEventA(NULL, TRUE, FALSE, NULL);

    if (!ReadFile(hFile, lpBuffer, len, NULL, &overlapped)) {

        if (GetLastError() != ERROR_IO_PENDING) {
            CloseHandle(overlapped.hEvent);
            free(lpBuffer);
            throw_SerialPortException_With_PortName(env, "Error readBytes (GetLastError)", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
            return -1;
        }

        //overlapped path
        if (WaitForSingleObject(overlapped.hEvent, INFINITE) != WAIT_OBJECT_0) {
            CloseHandle(overlapped.hEvent);
            free(lpBuffer);
            throw_SerialPortException_With_PortName(env, "Error readBytes (WaitForSingleObject)", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
            return -1;
        }

    }

    if (!GetOverlappedResult(hFile, &overlapped, &dwBytesRead, FALSE)) {
        CloseHandle(overlapped.hEvent);
        free(lpBuffer);
        if ((*env)->GetBooleanField(env, object, spsw_open)) {
            throw_SerialPortException_With_PortName(env, "Error readBytes (GetOverlappedResult)", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
            return -1;
        } else {
            //closed
            return -1;
        }
    }

    CloseHandle(overlapped.hEvent);

    (*env)->SetByteArrayRegion(env, bytes, off, dwBytesRead, lpBuffer);

    free(lpBuffer);
    return dwBytesRead;
}

JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_getInBufferBytesCount
(JNIEnv *env, jobject object) {
    DWORD lpErrors;
    COMSTAT comstat;

#if defined(__i386)
    HANDLE hFile = (HANDLE) (DWORD) (*env)->GetLongField(env, object, spsw_fd);
#elif defined(__x86_64)
    HANDLE hFile = (HANDLE) (*env)->GetLongField(env, object, spsw_fd);
#endif

    if (ClearCommError(hFile, &lpErrors, &comstat)) {
        return (jint) comstat.cbInQue;
    } else {
        throw_SerialPortException_With_PortName(env, "getInBufferBytesCount ClearCommError", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return -1;
    }
}

JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_getOutBufferBytesCount
(JNIEnv *env, jobject object) {
    DWORD lpErrors;
    COMSTAT comstat;

#if defined(__i386)
    HANDLE hFile = (HANDLE) (DWORD) (*env)->GetLongField(env, object, spsw_fd);
#elif defined(__x86_64)
    HANDLE hFile = (HANDLE) (*env)->GetLongField(env, object, spsw_fd);
#endif

    if (ClearCommError(hFile, &lpErrors, &comstat)) {
        return (jint) comstat.cbOutQue;
    } else {
        throw_SerialPortException_With_PortName(env, "getOutBufferBytesCount ClearCommError", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return -1;
    }
}

/*
 * Setting flow control mode
 *
 * since 0.8
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_setFlowControl
(JNIEnv *env, jobject object, jint mask) {
    DCB dcb;

#if defined(__i386)
    HANDLE hFile = (HANDLE) (DWORD) (*env)->GetLongField(env, object, spsw_fd);
#elif defined(__x86_64)
    HANDLE hFile = (HANDLE) (*env)->GetLongField(env, object, spsw_fd);
#endif

    if (!GetCommState(hFile, &dcb)) {
        throw_SerialPortException_With_PortName(env, "setFlowControl GetCommState", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return;
    }
    dcb.fRtsControl = RTS_CONTROL_ENABLE;
    dcb.fOutxCtsFlow = FALSE;
    dcb.fOutX = FALSE;
    dcb.fInX = FALSE;
    if (mask != FLOW_CONTROL_NONE) {
        if ((mask & FLOW_CONTROL_RTS_CTS_IN) == FLOW_CONTROL_RTS_CTS_IN) {
            dcb.fRtsControl = RTS_CONTROL_HANDSHAKE;
        }
        if ((mask & FLOW_CONTROL_RTS_CTS_OUT) == FLOW_CONTROL_RTS_CTS_OUT) {
            dcb.fOutxCtsFlow = TRUE;
        }
        if ((mask & FLOW_CONTROL_XON_XOFF_IN) == FLOW_CONTROL_XON_XOFF_IN) {
            dcb.fInX = TRUE;
        }
        if ((mask & FLOW_CONTROL_XON_XOFF_OUT) == FLOW_CONTROL_XON_XOFF_OUT) {
            dcb.fOutX = TRUE;
        }
    }
    if (!SetCommState(hFile, &dcb)) {
        throw_SerialPortException_With_PortName(env, "setFlowControl SetCommState", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
    }

}

/*
 * Class:     de_ibapl_spsw_provider_GenericWinSerialPortSocket
 * Method:    setBaudrate
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_setBaudrate
(JNIEnv *env, jobject object, jint baudRate) {
    DCB dcb;

#if defined(__i386)
    HANDLE hFile = (HANDLE) (DWORD) (*env)->GetLongField(env, object, spsw_fd);
#elif defined(__x86_64)
    HANDLE hFile = (HANDLE) (*env)->GetLongField(env, object, spsw_fd);
#endif

    if (!GetCommState(hFile, &dcb)) {
        throw_SerialPortException_With_PortName(env, "setBaudrate GetCommState", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return;
    }

    dcb.BaudRate = baudRate;

    if (!SetCommState(hFile, &dcb)) {
        throw_SerialPortException_With_PortName(env, "setBaudrate SetCommState", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
    }
}

/*
 * Class:     de_ibapl_spsw_provider_GenericWinSerialPortSocket
 * Method:    setDataBits
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_setDataBits
(JNIEnv *env, jobject object, jint dataBits) {
    DCB dcb;

#if defined(__i386)
    HANDLE hFile = (HANDLE) (DWORD) (*env)->GetLongField(env, object, spsw_fd);
#elif defined(__x86_64)
    HANDLE hFile = (HANDLE) (*env)->GetLongField(env, object, spsw_fd);
#endif

    if (!GetCommState(hFile, &dcb)) {
        throw_SerialPortException_With_PortName(env, "setDataBits GetCommState", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return;
    }

    dcb.ByteSize = dataBits;

    if (!SetCommState(hFile, &dcb)) {
        throw_SerialPortException_With_PortName(env, "setDataBits SetCommState", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
    }
}

/*
 * Class:     de_ibapl_spsw_provider_GenericWinSerialPortSocket
 * Method:    setStopBits
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_setStopBits
(JNIEnv *env, jobject object, jint stopBits) {
    DCB dcb;

#if defined(__i386)
    HANDLE hFile = (HANDLE) (DWORD) (*env)->GetLongField(env, object, spsw_fd);
#elif defined(__x86_64)
    HANDLE hFile = (HANDLE) (*env)->GetLongField(env, object, spsw_fd);
#endif

    if (!GetCommState(hFile, &dcb)) {
        throw_SerialPortException_With_PortName(env, "setStopBits GetCommState", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return;
    }

    switch (stopBits) {
        case de_ibapl_spsw_provider_GenericWinSerialPortSocket_STOP_BITS_1:
            dcb.StopBits = ONESTOPBIT;
            break;
        case de_ibapl_spsw_provider_GenericWinSerialPortSocket_STOP_BITS_1_5:
            dcb.StopBits = ONE5STOPBITS;
            break;
        case de_ibapl_spsw_provider_GenericWinSerialPortSocket_STOP_BITS_2:
            dcb.StopBits = TWOSTOPBITS;
            break;
        default:
            throw_SerialPortException_With_PortName(env, "setStopBits Unknown stopbits", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
            return;
    }

    if (!SetCommState(hFile, &dcb)) {
        throw_SerialPortException_With_PortName(env, "setStopBits SetCommState", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
    }
}

/*
 * Class:     de_ibapl_spsw_provider_GenericWinSerialPortSocket
 * Method:    setParity
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_setParity
(JNIEnv *env, jobject object, jint parity) {
    DCB dcb;

#if defined(__i386)
    HANDLE hFile = (HANDLE) (DWORD) (*env)->GetLongField(env, object, spsw_fd);
#elif defined(__x86_64)
    HANDLE hFile = (HANDLE) (*env)->GetLongField(env, object, spsw_fd);
#endif

    if (!GetCommState(hFile, &dcb)) {
        throw_SerialPortException_With_PortName(env, "setParity GetCommState", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return;
    }

    switch (parity) {
        case de_ibapl_spsw_provider_GenericWinSerialPortSocket_PARITY_NONE:
            dcb.Parity = NOPARITY; // switch parity input checking off
            break;
        case de_ibapl_spsw_provider_GenericWinSerialPortSocket_PARITY_ODD:
            dcb.Parity = ODDPARITY;
            break;
        case de_ibapl_spsw_provider_GenericWinSerialPortSocket_PARITY_EVEN:
            dcb.Parity = EVENPARITY;
            break;
        case de_ibapl_spsw_provider_GenericWinSerialPortSocket_PARITY_MARK:
            dcb.Parity = MARKPARITY;
            break;
        case de_ibapl_spsw_provider_GenericWinSerialPortSocket_PARITY_SPACE:
            dcb.Parity = SPACEPARITY;
            break;
        default:
            throw_SerialPortException_With_PortName(env, "setParity Wrong Parity", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
            return;
    }

    if (!SetCommState(hFile, &dcb)) {
        throw_SerialPortException_With_PortName(env, "setParity SetCommState", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
    }
}

/*
 * Class:     de_ibapl_spsw_provider_GenericWinSerialPortSocket
 * Method:    getBaudrate
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_getBaudrate0
(JNIEnv *env, jobject object) {
    DCB dcb;

#if defined(__i386)
    HANDLE hFile = (HANDLE) (DWORD) (*env)->GetLongField(env, object, spsw_fd);
#elif defined(__x86_64)
    HANDLE hFile = (HANDLE) (*env)->GetLongField(env, object, spsw_fd);
#endif

    if (!GetCommState(hFile, &dcb)) {
        throw_SerialPortException_With_PortName(env, "getBaudrate0 GetCommState", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return -1;
    }
    return dcb.BaudRate;
}

/*
 * Class:     de_ibapl_spsw_provider_GenericWinSerialPortSocket
 * Method:    getDataBits
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_getDataBits0
(JNIEnv *env, jobject object) {
    DCB dcb;

#if defined(__i386)
    HANDLE hFile = (HANDLE) (DWORD) (*env)->GetLongField(env, object, spsw_fd);
#elif defined(__x86_64)
    HANDLE hFile = (HANDLE) (*env)->GetLongField(env, object, spsw_fd);
#endif

    if (!GetCommState(hFile, &dcb)) {
        throw_SerialPortException_With_PortName(env, "getDataBits0 GetCommState", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return -1;
    }
    return dcb.ByteSize;
}

/*
 * Class:     de_ibapl_spsw_provider_GenericWinSerialPortSocket
 * Method:    getStopBits
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_getStopBits0
(JNIEnv *env, jobject object) {
    DCB dcb;

#if defined(__i386)
    HANDLE hFile = (HANDLE) (DWORD) (*env)->GetLongField(env, object, spsw_fd);
#elif defined(__x86_64)
    HANDLE hFile = (HANDLE) (*env)->GetLongField(env, object, spsw_fd);
#endif

    if (!GetCommState(hFile, &dcb)) {
        throw_SerialPortException_With_PortName(env, "getStopBits0 GetCommState", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return -1;
    }

    switch (dcb.StopBits) {
        case ONESTOPBIT:
            return de_ibapl_spsw_provider_GenericWinSerialPortSocket_STOP_BITS_1;
        case ONE5STOPBITS:
            return de_ibapl_spsw_provider_GenericWinSerialPortSocket_STOP_BITS_1_5;
        case TWOSTOPBITS:
            return de_ibapl_spsw_provider_GenericWinSerialPortSocket_STOP_BITS_2;
        default:
            throw_SerialPortException_With_PortName(env, "getStopBits0 Unknown stopbits", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
            return -1;
    }
}

/*
 * Class:     de_ibapl_spsw_provider_GenericWinSerialPortSocket
 * Method:    getParity
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_getParity0
(JNIEnv *env, jobject object) {
    DCB dcb;

#if defined(__i386)
    HANDLE hFile = (HANDLE) (DWORD) (*env)->GetLongField(env, object, spsw_fd);
#elif defined(__x86_64)
    HANDLE hFile = (HANDLE) (*env)->GetLongField(env, object, spsw_fd);
#endif

    if (!GetCommState(hFile, &dcb)) {
        throw_SerialPortException_With_PortName(env, "getParity0 GetCommState", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return -1;
    }

    switch (dcb.Parity) {
        case NOPARITY:
            return de_ibapl_spsw_provider_GenericWinSerialPortSocket_PARITY_NONE;
        case ODDPARITY:
            return de_ibapl_spsw_provider_GenericWinSerialPortSocket_PARITY_ODD;
        case EVENPARITY:
            return de_ibapl_spsw_provider_GenericWinSerialPortSocket_PARITY_EVEN;
        case MARKPARITY:
            return de_ibapl_spsw_provider_GenericWinSerialPortSocket_PARITY_MARK;
        case SPACEPARITY:
            return de_ibapl_spsw_provider_GenericWinSerialPortSocket_PARITY_SPACE;
        default:
            throw_SerialPortException_With_PortName(env, "getParity0 Wrong Parity", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
            return -1;
    }
}

/*
 * Getting flow control mode
 *
 * since 0.8
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_getFlowControl0
(JNIEnv *env, jobject object) {
    jint returnValue = 0;
    DCB dcb;

#if defined(__i386)
    HANDLE hFile = (HANDLE) (DWORD) (*env)->GetLongField(env, object, spsw_fd);
#elif defined(__x86_64)
    HANDLE hFile = (HANDLE) (*env)->GetLongField(env, object, spsw_fd);
#endif

    if (!GetCommState(hFile, &dcb)) {
        throw_SerialPortException_With_PortName(env, "getFlowControl0 GetCommState", (jstring) (*env)->GetObjectField(env, object, spsw_portName));
        return -1;
    }

    if (dcb.fRtsControl == RTS_CONTROL_HANDSHAKE) {
        returnValue |= FLOW_CONTROL_RTS_CTS_IN;
    }
    if (dcb.fOutxCtsFlow == TRUE) {
        returnValue |= FLOW_CONTROL_RTS_CTS_OUT;
    }
    if (dcb.fInX == TRUE) {
        returnValue |= FLOW_CONTROL_XON_XOFF_IN;
    }
    if (dcb.fOutX == TRUE) {
        returnValue |= FLOW_CONTROL_XON_XOFF_OUT;
    }
    return returnValue;
}

JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_printTermios
(JNIEnv *env, jobject object) {
    //TODO Print
}
