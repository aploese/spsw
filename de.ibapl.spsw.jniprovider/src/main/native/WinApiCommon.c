#include "spsw-jni.h"

#include "de_ibapl_spsw_jniprovider_GenericWinSerialPortSocket.h"

#ifdef __cplusplus
extern "C" {
#endif

    
static jboolean getCommModemStatus(JNIEnv *env, jobject sps, DWORD bitMask) {
	DWORD lpModemStat;

	HANDLE hFile = (HANDLE)(uintptr_t)(*env)->GetLongField(env, sps, spsw_fd);

	if (!GetCommModemStatus(hFile, &lpModemStat)) {
		throw_ClosedOrNativeException(env, sps, "Can't get GetCommModemStatus");
		return JNI_FALSE;
	}
	if ((lpModemStat & bitMask) == bitMask) {
		return JNI_TRUE;
	} else {
		return JNI_FALSE;
	}
}


/*
 * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
 * Method:    isCTS
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_isCTS(
		JNIEnv *env, jobject sps) {
	return getCommModemStatus(env, sps, MS_CTS_ON);
}

/*
 * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
 * Method:    isDSR
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_isDSR(
		JNIEnv *env, jobject sps) {
	return getCommModemStatus(env, sps, MS_DSR_ON);
}

/*
 * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
 * Method:    isDCD
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_isDCD(
		JNIEnv *env, jobject sps) {
	return getCommModemStatus(env, sps, MS_RLSD_ON);
}

/*
 * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
 * Method:    isIncommingRI
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_isRI(
		JNIEnv *env, jobject sps) {
	return getCommModemStatus(env, sps, MS_RING_ON);
}


/*
 * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
 * Method:    close0
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_close0
(JNIEnv *env, jobject sps) {

	HANDLE hFile = (HANDLE)(uintptr_t)(*env)->GetLongField(env, sps, spsw_fd);
	(*env)->SetLongField(env, sps, spsw_fd, (uintptr_t)INVALID_HANDLE_VALUE);
// if only ReadIntervalTimeout is set and port is closed during pending read the read operation will hang forever...
	if (!CancelIo(hFile)) {
		//no-op we dont care
	}
	if (!CancelIoEx(hFile, NULL)) {
		//no-op we dont care
	}

	if (!CloseHandle(hFile)) {
		throw_IOException_NativeError(env, "Can't close port");
	}
}

/*
 * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
 * Method:    getInBufferBytesCount
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_getInBufferBytesCount(
		JNIEnv *env, jobject sps) {
	DWORD lpErrors;
	COMSTAT comstat;

	HANDLE hFile = (HANDLE)(uintptr_t)(*env)->GetLongField(env, sps, spsw_fd);

	if (ClearCommError(hFile, &lpErrors, &comstat)) {
		return (jint) comstat.cbInQue;
	} else {
		throw_ClosedOrNativeException(env, sps,
				"getInBufferBytesCount ClearCommError");
		return -1;
	}
}

/*
 * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
 * Method:    getOutBufferBytesCount
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_getOutBufferBytesCount(
		JNIEnv *env, jobject sps) {
	DWORD lpErrors;
	COMSTAT comstat;

	HANDLE hFile = (HANDLE)(uintptr_t)(*env)->GetLongField(env, sps, spsw_fd);

	if (ClearCommError(hFile, &lpErrors, &comstat)) {
		return (jint) comstat.cbOutQue;
	} else {
		throw_ClosedOrNativeException(env, sps,
				"getOutBufferBytesCount ClearCommError");
		return -1;
	}
}

/*
 * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
 * Method:    open
 * Signature: (Ljava/lang/String;I)V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_open
(JNIEnv *env, jobject sps, jstring portName, jint paramBitSet) {
//Do not try to reopen port and therefore failing and overriding the file descriptor
	if ((HANDLE)(uintptr_t)(*env)->GetLongField(env, sps, spsw_fd) != INVALID_HANDLE_VALUE) {
		throw_IOException_Opend(env);
		return;
	}

	char prefix[] = "\\\\.\\";
	const char* port = (*env)->GetStringUTFChars(env, portName, JNI_FALSE);

//string concat fix
	char portFullName[strlen(prefix) + strlen(port) + 1];
	strcpy(portFullName, prefix);
	strcat(portFullName, port);
	(*env)->ReleaseStringUTFChars(env, portName, port);

	HANDLE hFile = CreateFile(portFullName,
			GENERIC_READ | GENERIC_WRITE,
			0,
			NULL,
			OPEN_EXISTING,
			FILE_FLAG_OVERLAPPED,
			NULL);

	if (hFile == INVALID_HANDLE_VALUE) {

		(*env)->SetLongField(env, sps, spsw_fd, (uintptr_t)INVALID_HANDLE_VALUE);

		switch (GetLastError()) {
			case ERROR_ACCESS_DENIED:
			throw_IOException(env, "Port is busy: (%s)", portName);
			break;
			case ERROR_FILE_NOT_FOUND:
			throw_IOException(env, "Port not found: (%s)", portName);
			break;
			default:
			throw_IOException_NativeError(env, "Open");
		}
		return;
	}
	// The port is open, but maybe not configured ... setParam and getParam needs this to be set for their field access
	(*env)->SetLongField(env, sps, spsw_fd, (uintptr_t)hFile);

	DCB dcb;
        dcb.DCBlength = sizeof(DCB);
	if (!GetCommState(hFile, &dcb)) {
		CloseHandle(hFile);

		(*env)->SetLongField(env, sps, spsw_fd, (uintptr_t)INVALID_HANDLE_VALUE);

		throw_IOException(env, "Not a serial port: (%s)", portName);
		return;
	}

	if (paramBitSet != SPSW_NO_PARAMS_TO_SET) {
		//set speed etc.
		if (setParams(env, sps, &dcb, paramBitSet)) {
			CloseHandle(hFile);
			(*env)->SetLongField(env, sps, spsw_fd, (uintptr_t)INVALID_HANDLE_VALUE);
			return;
		}
	}

	COMMTIMEOUTS lpCommTimeouts;
	if (!GetCommTimeouts(hFile, &lpCommTimeouts)) {
		CloseHandle(hFile);

		(*env)->SetLongField(env, sps, spsw_fd, (uintptr_t)INVALID_HANDLE_VALUE);

		throw_IOException_NativeError(env, "Open GetCommTimeouts");
		return;
	}

	lpCommTimeouts.ReadIntervalTimeout = 100;
	lpCommTimeouts.ReadTotalTimeoutConstant = 0;
	lpCommTimeouts.ReadTotalTimeoutMultiplier = 0;
	lpCommTimeouts.WriteTotalTimeoutConstant = 0;
	lpCommTimeouts.WriteTotalTimeoutMultiplier = 0;

	if (!SetCommTimeouts(hFile, &lpCommTimeouts)) {
		CloseHandle(hFile);

		(*env)->SetLongField(env, sps, spsw_fd, (uintptr_t)INVALID_HANDLE_VALUE);

		throw_IOException_NativeError(env, "Open SetCommTimeouts");
		return;
	}

}

/*
 * Class:     de_ibapl_spsw_jniprovider_GenericWinSerialPortSocket
 * Method:    getWindowsBasedPortNames
 * Signature: ()[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_de_ibapl_spsw_jniprovider_GenericWinSerialPortSocket_getWindowsBasedPortNames(
		JNIEnv *env, jclass clazz) {
	HKEY phkResult;
	LPCSTR lpSubKey = "HARDWARE\\DEVICEMAP\\SERIALCOMM\\";
	jobjectArray returnArray = NULL;
	if (RegOpenKeyExA(HKEY_LOCAL_MACHINE, lpSubKey, 0, KEY_READ, &phkResult)
			== ERROR_SUCCESS) {
		boolean hasMoreElements = TRUE;
		DWORD keysCount = 0;
		char valueName[256];
		DWORD valueNameSize;
		DWORD enumResult;
		while (hasMoreElements) {
			valueNameSize = 256;
			enumResult = RegEnumValueA(phkResult, keysCount, valueName,
					&valueNameSize, NULL, NULL, NULL, NULL);
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
			returnArray = (*env)->NewObjectArray(env, (jsize) keysCount,
					stringClass, NULL);
			char lpValueName[256];
			DWORD lpcchValueName;
			byte lpData[256];
			DWORD lpcbData;
			DWORD result;
			for (DWORD i = 0; i < keysCount; i++) {
				lpcchValueName = 256;
				lpcbData = 256;
				result = RegEnumValueA(phkResult, i, lpValueName,
						&lpcchValueName, NULL, NULL, lpData, &lpcbData);
				if (result == ERROR_SUCCESS) {
					(*env)->SetObjectArrayElement(env, returnArray, i,
							(*env)->NewStringUTF(env, (char*) lpData));
				}
			}
		}
		CloseHandle(phkResult);
	}
	return returnArray;
}



#ifdef __cplusplus
}
#endif
