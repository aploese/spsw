/*-
 * #%L
 * SPSW Provider
 * %%
 * Copyright (C) 2009 - 2017 Arne Plöse
 * %%
 * SPSW - Drivers for the serial port, https://github.com/aploese/spsw/
 * Copyright (C) 2009-2018, Arne Plöse and individual contributors as indicated
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

#define _WIN32_WINNT 0x600

#include <jni.h>
#include <stdlib.h>
#include <windows.h>
#include "de_ibapl_spsw_provider_GenericWinSerialPortSocket.h"

//#include <iostream>

#include "de_ibapl_spsw_provider_AbstractSerialPortSocket.h"

#undef SPSW_BAUDRATE_B0
#define SPSW_BAUDRATE_B0 de_ibapl_spsw_provider_AbstractSerialPortSocket_BAUDRATE_B0
#undef SPSW_BAUDRATE_B50
#define SPSW_BAUDRATE_B50 de_ibapl_spsw_provider_AbstractSerialPortSocket_BAUDRATE_B50
#undef SPSW_BAUDRATE_B75
#define SPSW_BAUDRATE_B75 de_ibapl_spsw_provider_AbstractSerialPortSocket_BAUDRATE_B75
#undef SPSW_BAUDRATE_B110
#define SPSW_BAUDRATE_B110 de_ibapl_spsw_provider_AbstractSerialPortSocket_BAUDRATE_B110
#undef SPSW_BAUDRATE_B134
#define SPSW_BAUDRATE_B134 de_ibapl_spsw_provider_AbstractSerialPortSocket_BAUDRATE_B134
#undef SPSW_BAUDRATE_B150
#define SPSW_BAUDRATE_B150 de_ibapl_spsw_provider_AbstractSerialPortSocket_BAUDRATE_B150
#undef SPSW_BAUDRATE_B200
#define SPSW_BAUDRATE_B200 de_ibapl_spsw_provider_AbstractSerialPortSocket_BAUDRATE_B200
#undef SPSW_BAUDRATE_B300
#define SPSW_BAUDRATE_B300 de_ibapl_spsw_provider_AbstractSerialPortSocket_BAUDRATE_B300
#undef SPSW_BAUDRATE_B600
#define SPSW_BAUDRATE_B600 de_ibapl_spsw_provider_AbstractSerialPortSocket_BAUDRATE_B600
#undef SPSW_BAUDRATE_B1200
#define SPSW_BAUDRATE_B1200 de_ibapl_spsw_provider_AbstractSerialPortSocket_BAUDRATE_B1200
#undef SPSW_BAUDRATE_B1800
#define SPSW_BAUDRATE_B1800 de_ibapl_spsw_provider_AbstractSerialPortSocket_BAUDRATE_B1800
#undef SPSW_BAUDRATE_B2400
#define SPSW_BAUDRATE_B2400 de_ibapl_spsw_provider_AbstractSerialPortSocket_BAUDRATE_B2400
#undef SPSW_BAUDRATE_B4800
#define SPSW_BAUDRATE_B4800 de_ibapl_spsw_provider_AbstractSerialPortSocket_BAUDRATE_B4800
#undef SPSW_BAUDRATE_B9600
#define SPSW_BAUDRATE_B9600 de_ibapl_spsw_provider_AbstractSerialPortSocket_BAUDRATE_B9600
#undef SPSW_BAUDRATE_B19200
#define SPSW_BAUDRATE_B19200 de_ibapl_spsw_provider_AbstractSerialPortSocket_BAUDRATE_B19200
#undef SPSW_BAUDRATE_B38400
#define SPSW_BAUDRATE_B38400 de_ibapl_spsw_provider_AbstractSerialPortSocket_BAUDRATE_B38400
#undef SPSW_BAUDRATE_B57600
#define SPSW_BAUDRATE_B57600 de_ibapl_spsw_provider_AbstractSerialPortSocket_BAUDRATE_B57600
#undef SPSW_BAUDRATE_B115200
#define SPSW_BAUDRATE_B115200 de_ibapl_spsw_provider_AbstractSerialPortSocket_BAUDRATE_B115200
#undef SPSW_BAUDRATE_B230400
#define SPSW_BAUDRATE_B230400 de_ibapl_spsw_provider_AbstractSerialPortSocket_BAUDRATE_B230400
#undef SPSW_BAUDRATE_B460800
#define SPSW_BAUDRATE_B460800 de_ibapl_spsw_provider_AbstractSerialPortSocket_BAUDRATE_B460800
#undef SPSW_BAUDRATE_B500000
#define SPSW_BAUDRATE_B500000 de_ibapl_spsw_provider_AbstractSerialPortSocket_BAUDRATE_B500000
#undef SPSW_BAUDRATE_B576000
#define SPSW_BAUDRATE_B576000 de_ibapl_spsw_provider_AbstractSerialPortSocket_BAUDRATE_B576000
#undef SPSW_BAUDRATE_B921600
#define SPSW_BAUDRATE_B921600 de_ibapl_spsw_provider_AbstractSerialPortSocket_BAUDRATE_B921600
#undef SPSW_BAUDRATE_B1000000
#define SPSW_BAUDRATE_B1000000 de_ibapl_spsw_provider_AbstractSerialPortSocket_BAUDRATE_B1000000
#undef SPSW_BAUDRATE_B1152000
#define SPSW_BAUDRATE_B1152000 de_ibapl_spsw_provider_AbstractSerialPortSocket_BAUDRATE_B1152000
#undef SPSW_BAUDRATE_B1500000
#define SPSW_BAUDRATE_B1500000 de_ibapl_spsw_provider_AbstractSerialPortSocket_BAUDRATE_B1500000
#undef SPSW_BAUDRATE_B2000000
#define SPSW_BAUDRATE_B2000000 de_ibapl_spsw_provider_AbstractSerialPortSocket_BAUDRATE_B2000000
#undef SPSW_BAUDRATE_B2500000
#define SPSW_BAUDRATE_B2500000 de_ibapl_spsw_provider_AbstractSerialPortSocket_BAUDRATE_B2500000
#undef SPSW_BAUDRATE_B3000000
#define SPSW_BAUDRATE_B3000000 de_ibapl_spsw_provider_AbstractSerialPortSocket_BAUDRATE_B3000000
#undef SPSW_BAUDRATE_B3500000
#define SPSW_BAUDRATE_B3500000 de_ibapl_spsw_provider_AbstractSerialPortSocket_BAUDRATE_B3500000
#undef SPSW_BAUDRATE_B4000000
#define SPSW_BAUDRATE_B4000000 de_ibapl_spsw_provider_AbstractSerialPortSocket_BAUDRATE_B4000000
#undef SPSW_BAUDRATE_MASK
#define SPSW_BAUDRATE_MASK de_ibapl_spsw_provider_AbstractSerialPortSocket_BAUDRATE_MASK
#undef SPSW_DATA_BITS_DB5
#define SPSW_DATA_BITS_DB5 de_ibapl_spsw_provider_AbstractSerialPortSocket_DATA_BITS_DB5
#undef SPSW_DATA_BITS_DB6
#define SPSW_DATA_BITS_DB6 de_ibapl_spsw_provider_AbstractSerialPortSocket_DATA_BITS_DB6
#undef SPSW_DATA_BITS_DB7
#define SPSW_DATA_BITS_DB7 de_ibapl_spsw_provider_AbstractSerialPortSocket_DATA_BITS_DB7
#undef SPSW_DATA_BITS_DB8
#define SPSW_DATA_BITS_DB8 de_ibapl_spsw_provider_AbstractSerialPortSocket_DATA_BITS_DB8
#undef SPSW_DATA_BITS_MASK
#define SPSW_DATA_BITS_MASK de_ibapl_spsw_provider_AbstractSerialPortSocket_DATA_BITS_MASK
#undef SPSW_FLOW_CONTROL_NONE
#define SPSW_FLOW_CONTROL_NONE de_ibapl_spsw_provider_AbstractSerialPortSocket_FLOW_CONTROL_NONE
#undef SPSW_FLOW_CONTROL_RTS_CTS_IN
#define SPSW_FLOW_CONTROL_RTS_CTS_IN de_ibapl_spsw_provider_AbstractSerialPortSocket_FLOW_CONTROL_RTS_CTS_IN
#undef SPSW_FLOW_CONTROL_RTS_CTS_OUT
#define SPSW_FLOW_CONTROL_RTS_CTS_OUT de_ibapl_spsw_provider_AbstractSerialPortSocket_FLOW_CONTROL_RTS_CTS_OUT
#undef SPSW_FLOW_CONTROL_XON_XOFF_IN
#define SPSW_FLOW_CONTROL_XON_XOFF_IN de_ibapl_spsw_provider_AbstractSerialPortSocket_FLOW_CONTROL_XON_XOFF_IN
#undef SPSW_FLOW_CONTROL_XON_XOFF_OUT
#define SPSW_FLOW_CONTROL_XON_XOFF_OUT de_ibapl_spsw_provider_AbstractSerialPortSocket_FLOW_CONTROL_XON_XOFF_OUT
#undef SPSW_FLOW_CONTROL_MASK
#define SPSW_FLOW_CONTROL_MASK de_ibapl_spsw_provider_AbstractSerialPortSocket_FLOW_CONTROL_MASK
#undef SPSW_STOP_BITS_1
#define SPSW_STOP_BITS_1 de_ibapl_spsw_provider_AbstractSerialPortSocket_STOP_BITS_1
#undef SPSW_STOP_BITS_1_5
#define SPSW_STOP_BITS_1_5 de_ibapl_spsw_provider_AbstractSerialPortSocket_STOP_BITS_1_5
#undef SPSW_STOP_BITS_2
#define SPSW_STOP_BITS_2 de_ibapl_spsw_provider_AbstractSerialPortSocket_STOP_BITS_2
#undef SPSW_STOP_BITS_MASK
#define SPSW_STOP_BITS_MASK de_ibapl_spsw_provider_AbstractSerialPortSocket_STOP_BITS_MASK
#undef SPSW_PARITY_NONE
#define SPSW_PARITY_NONE de_ibapl_spsw_provider_AbstractSerialPortSocket_PARITY_NONE
#undef SPSW_PARITY_ODD
#define SPSW_PARITY_ODD de_ibapl_spsw_provider_AbstractSerialPortSocket_PARITY_ODD
#undef SPSW_PARITY_EVEN
#define SPSW_PARITY_EVEN de_ibapl_spsw_provider_AbstractSerialPortSocket_PARITY_EVEN
#undef SPSW_PARITY_MARK
#define SPSW_PARITY_MARK de_ibapl_spsw_provider_AbstractSerialPortSocket_PARITY_MARK
#undef SPSW_PARITY_SPACE
#define SPSW_PARITY_SPACE de_ibapl_spsw_provider_AbstractSerialPortSocket_PARITY_SPACE
#undef SPSW_PARITY_MASK
#define SPSW_PARITY_MASK de_ibapl_spsw_provider_AbstractSerialPortSocket_PARITY_MASK
#undef SPSW_NO_PARAMS_TO_SET
#define SPSW_NO_PARAMS_TO_SET de_ibapl_spsw_provider_AbstractSerialPortSocket_NO_PARAMS_TO_SET

jfieldID spsw_portName; /* id for field 'portName'  */
jfieldID spsw_fd; /* id for field 'fd'  */

#if defined(__i386)
#define GET_FILEDESCRIPTOR(env, sps) ((HANDLE) (INT32) (*env)->GetLongField(env, sps, spsw_fd))
#elif defined(__x86_64)
#define GET_FILEDESCRIPTOR(env, sps) ((HANDLE) (*env)->GetLongField(env, sps, spsw_fd))
#endif

#if defined(__i386)
#define SET_FILEDESCRIPTOR(env, sps, fd) (*env)->SetLongField(env, sps, spsw_fd, (jlong) (INT32) fd);
#elif defined(__x86_64)
#define SET_FILEDESCRIPTOR(env, sps, fd)  (*env)->SetLongField(env, sps, spsw_fd, (jlong) fd);
#endif

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved) {
	JNIEnv *env;
	jint getEnvResult = ((*jvm)->GetEnv(jvm, (void **) &env, JNI_VERSION_1_2));
	if (getEnvResult != JNI_OK) {
		return getEnvResult;
	}

	jclass spswClass = (*env)->FindClass(env,
			"Lde/ibapl/spsw/provider/GenericWinSerialPortSocket;");
	if (spswClass == NULL) {
		return JNI_ERR;
	}

	spsw_fd = (*env)->GetFieldID(env, spswClass, "fd", "J");
	if (spsw_fd == NULL) {
		return JNI_ERR;
	}

	spsw_portName = (*env)->GetFieldID(env, spswClass, "portName",
			"Ljava/lang/String;");
	if (spsw_portName == NULL) {
		return JNI_ERR;
	}

	(*env)->DeleteLocalRef(env, spswClass);

	// mark that the lib was loaded
	jclass serialPortSocketFactoryImpl = (*env)->FindClass(env,
			"Lde/ibapl/spsw/provider/SerialPortSocketFactoryImpl;");
	if (serialPortSocketFactoryImpl == NULL) {
		return JNI_ERR;
	}
	jfieldID spsw_libLoaded = (*env)->GetStaticFieldID(env,
			serialPortSocketFactoryImpl, "libLoaded", "Z");
	if (spsw_libLoaded == NULL) {
		return JNI_ERR;
	}
	(*env)->SetStaticBooleanField(env, serialPortSocketFactoryImpl,
			spsw_libLoaded, JNI_TRUE);

	(*env)->DeleteLocalRef(env, serialPortSocketFactoryImpl);

	return JNI_VERSION_1_2;
}

JNIEXPORT void JNICALL JNI_OnUnLoad(JavaVM *jvm, void *reserved) {
	JNIEnv *env;
	spsw_fd = 0;
	spsw_portName = 0;

	jint getEnvResult = ((*jvm)->GetEnv(jvm, (void **) &env, JNI_VERSION_1_2));
	if (getEnvResult != JNI_OK) {
		return;
	}

	// mark that the lib was unloaded
	jclass serialPortSocketFactoryImpl = (*env)->FindClass(env, "de/ibapl/spsw/provider/SerialPortSocketFactoryImpl");
	if (serialPortSocketFactoryImpl == NULL) {
		return;
	}
	jfieldID spsw_libLoaded = (*env)->GetStaticFieldID(env, serialPortSocketFactoryImpl, "libLoaded", "Z");
	if (spsw_libLoaded == NULL) {
		return;
	}
	(*env)->SetStaticBooleanField(env, serialPortSocketFactoryImpl, spsw_libLoaded, JNI_FALSE);
	(*env)->DeleteLocalRef(env, serialPortSocketFactoryImpl);
}

// Helper method

static void throw_IOException_NativeError(JNIEnv *env, const char *msg) {
	char buf[2048];
	snprintf(buf, 2048, "%s: Unknown port error %d", msg, (int) GetLastError());
	const jclass ioeClass = (*env)->FindClass(env,
			"java/io/IOException");
	if (ioeClass != NULL) {
		(*env)->ThrowNew(env, ioeClass, buf);
		(*env)->DeleteLocalRef(env, ioeClass);
	}
}

static void throw_Illegal_Argument_Exception(JNIEnv *env, const char *msg) {
	const jclass iaeClass = (*env)->FindClass(env,
			"java/lang/IllegalArgumentException");
	if (iaeClass != NULL) {
		(*env)->ThrowNew(env, iaeClass, msg);
		(*env)->DeleteLocalRef(env, iaeClass);
	}
}

static void throw_IOException(JNIEnv *env, const char* msg, jstring portName) {
	const char* port = (*env)->GetStringUTFChars(env, portName, JNI_FALSE);
	char buf[2048];
	snprintf(buf, sizeof(buf), msg, port);
	(*env)->ReleaseStringUTFChars(env, portName, port);

	const jclass ioeClass = (*env)->FindClass(env,
			"java/io/IOException");
	if (ioeClass != NULL) {
		(*env)->ThrowNew(env, ioeClass, buf);
		(*env)->DeleteLocalRef(env, ioeClass);
	}
}

static void throw_PortBusyException(JNIEnv *env, jstring portName) {
	throw_IOException(env, "Port is busy: (%s)", portName);
}

static void throw_PortNotFoundException(JNIEnv *env, jstring portName) {
	throw_IOException(env, "Port not found: (%s)", portName);
}

static void throw_NotASerialPortException(JNIEnv *env, jstring portName) {
	throw_IOException(env, "Not a serial port: (%s)", portName);
}

static void throw_IOException_Opend(JNIEnv *env) {
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

static void throw_IOException_Closed(JNIEnv *env) {
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
}

static void throw_IO_ClosedException(JNIEnv *env, int bytesTransferred) {
	const jclass spsClass = (*env)->FindClass(env,
			"de/ibapl/spsw/api/SerialPortSocket");
	const jclass iioeClass = (*env)->FindClass(env,
			"java/io/InterruptedIOException");

	if ((iioeClass != NULL) && (spsClass != NULL)) {

		const jfieldID PORT_IS_CLOSED = (*env)->GetStaticFieldID(env, spsClass,
				"PORT_IS_CLOSED", "Ljava/lang/String;");
		const jmethodID iioeConstructor = (*env)->GetMethodID(env, iioeClass,
				"<init>", "(Ljava/lang/String;)V");
		const jobject iioeEx = (*env)->NewObject(env, iioeClass,
				iioeConstructor,
				(*env)->GetStaticObjectField(env, spsClass, PORT_IS_CLOSED));
		const jfieldID bytesTransferredId = (*env)->GetFieldID(env, iioeClass,
				"bytesTransferred", "I");
		(*env)->SetIntField(env, iioeEx, bytesTransferredId, bytesTransferred);
		(*env)->Throw(env, iioeEx);
		(*env)->DeleteLocalRef(env, iioeClass);
	}
}

static void throw_InterruptedIOExceptionWithError(JNIEnv *env,
		int bytesTransferred, const char *msg) {
	char buf[2048];
	snprintf(buf, 2048, "%s: Unknown port native win error: %d", msg,
			(int) GetLastError());
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

static void throw_TimeoutIOException(JNIEnv *env, int bytesTransferred) {
	const jclass tioeClass = (*env)->FindClass(env,
			"de/ibapl/spsw/api/TimeoutIOException");
	if (tioeClass != NULL) {
		const jmethodID tioeConstructor = (*env)->GetMethodID(env, tioeClass,
				"<init>", "(Ljava/lang/String;)V");
		const jobject tioeEx = (*env)->NewObject(env, tioeClass,
				tioeConstructor, (*env)->NewStringUTF(env, "Timeout"));
		const jfieldID bytesTransferredId = (*env)->GetFieldID(env, tioeClass,
				"bytesTransferred", "I");
		(*env)->SetIntField(env, tioeEx, bytesTransferredId, bytesTransferred);
		(*env)->Throw(env, tioeEx);
		(*env)->DeleteLocalRef(env, tioeClass);
	}
}

static void throw_IO_ClosedOrInterruptedException(JNIEnv *env, jobject sps,
		int bytesTransferred, const char *message) {
	if (GET_FILEDESCRIPTOR(env, sps) == INVALID_HANDLE_VALUE) {
		throw_IO_ClosedException(env, bytesTransferred);
	} else {
		throw_InterruptedIOExceptionWithError(env, bytesTransferred, message);
	}
}

static void throw_ClosedOrNativeException(JNIEnv *env, jobject sps,
		const char *message) {
	if (GET_FILEDESCRIPTOR(env, sps) == INVALID_HANDLE_VALUE) {
		throw_IOException_Closed(env);
	} else {
		throw_IOException_NativeError(env, message);
	}
}

static jboolean getCommModemStatus(JNIEnv *env, jobject sps, DWORD bitMask) {
	DWORD lpModemStat;

	HANDLE hFile = GET_FILEDESCRIPTOR(env, sps);

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

static jint nativeToSpswBaudRate(JNIEnv *env, DWORD baudRate) {
	switch (baudRate) {
	case 0:
		return SPSW_BAUDRATE_B0;
	case 50:
		return SPSW_BAUDRATE_B50;
	case 75:
		return SPSW_BAUDRATE_B75;
	case 110:
		return SPSW_BAUDRATE_B110;
	case 134:
		return SPSW_BAUDRATE_B134;
	case 150:
		return SPSW_BAUDRATE_B150;
	case 200:
		return SPSW_BAUDRATE_B200;
	case 300:
		return SPSW_BAUDRATE_B300;
	case 600:
		return SPSW_BAUDRATE_B600;
	case 1200:
		return SPSW_BAUDRATE_B1200;
	case 1800:
		return SPSW_BAUDRATE_B1800;
	case 2400:
		return SPSW_BAUDRATE_B2400;
	case 4800:
		return SPSW_BAUDRATE_B4800;
	case 9600:
		return SPSW_BAUDRATE_B9600;
	case 19200:
		return SPSW_BAUDRATE_B19200;
	case 38400:
		return SPSW_BAUDRATE_B38400;
	case 57600:
		return SPSW_BAUDRATE_B57600;
	case 115200:
		return SPSW_BAUDRATE_B115200;
	case 230400:
		return SPSW_BAUDRATE_B230400;
	case 460800:
		return SPSW_BAUDRATE_B460800;
	case 500000:
		return SPSW_BAUDRATE_B500000;
	case 576000:
		return SPSW_BAUDRATE_B576000;
	case 921600:
		return SPSW_BAUDRATE_B921600;
	case 1000000:
		return SPSW_BAUDRATE_B1000000;
	case 1152000:
		return SPSW_BAUDRATE_B1152000;
	case 1500000:
		return SPSW_BAUDRATE_B1500000;
	case 2000000:
		return SPSW_BAUDRATE_B2000000;
	case 2500000:
		return SPSW_BAUDRATE_B2500000;
	case 3000000:
		return SPSW_BAUDRATE_B3000000;
	case 3500000:
		return SPSW_BAUDRATE_B3500000;
	case 4000000:
		return SPSW_BAUDRATE_B4000000;
	default:
		throw_Illegal_Argument_Exception(env, "Baudrate not supported");
		return -1;
	}
}

static DWORD spswBaudrateToNative(JNIEnv *env, jint baudRate) {
	switch (baudRate) {
	case SPSW_BAUDRATE_B0:
		return 0;
	case SPSW_BAUDRATE_B50:
		return 50;
	case SPSW_BAUDRATE_B75:
		return 75;
	case SPSW_BAUDRATE_B110:
		return 110;
	case SPSW_BAUDRATE_B134:
		return 134;
	case SPSW_BAUDRATE_B150:
		return 150;
	case SPSW_BAUDRATE_B200:
		return 200;
	case SPSW_BAUDRATE_B300:
		return 300;
	case SPSW_BAUDRATE_B600:
		return 600;
	case SPSW_BAUDRATE_B1200:
		return 1200;
	case SPSW_BAUDRATE_B1800:
		return 1800;
	case SPSW_BAUDRATE_B2400:
		return 2400;
	case SPSW_BAUDRATE_B4800:
		return 4800;
	case SPSW_BAUDRATE_B9600:
		return 9600;
	case SPSW_BAUDRATE_B19200:
		return 19200;
	case SPSW_BAUDRATE_B38400:
		return 38400;
	case SPSW_BAUDRATE_B57600:
		return 57600;
	case SPSW_BAUDRATE_B115200:
		return 115200;
	case SPSW_BAUDRATE_B230400:
		return 230400;
	case SPSW_BAUDRATE_B460800:
		return 460800;
	case SPSW_BAUDRATE_B500000:
		return 500000;
	case SPSW_BAUDRATE_B576000:
		return 576000;
	case SPSW_BAUDRATE_B921600:
		return 921600;
	case SPSW_BAUDRATE_B1000000:
		return 1000000;
	case SPSW_BAUDRATE_B1152000:
		return 1152000;
	case SPSW_BAUDRATE_B1500000:
		return 1500000;
	case SPSW_BAUDRATE_B2000000:
		return 2000000;
	case SPSW_BAUDRATE_B2500000:
		return 2500000;
	case SPSW_BAUDRATE_B3000000:
		return 3000000;
	case SPSW_BAUDRATE_B3500000:
		return 3500000;
	case SPSW_BAUDRATE_B4000000:
		return 4000000;
	default:
		throw_Illegal_Argument_Exception(env, "Baudrate not supported");
		return -1;
	}
}

static int getParams(JNIEnv *env, jobject sps, jint* paramBitSet) {

	jint result = 0;
	DCB dcb;

	HANDLE hFile = GET_FILEDESCRIPTOR(env, sps);

	if (!GetCommState(hFile, &dcb)) {
		throw_ClosedOrNativeException(env, sps, "getParams GetCommState");
		return -1;
	}

	//Baudrate
	if (*paramBitSet & SPSW_BAUDRATE_MASK) {
		result |= nativeToSpswBaudRate(env, dcb.BaudRate);
	}

	//DataBits
	if (*paramBitSet & SPSW_DATA_BITS_MASK) {
		switch (dcb.ByteSize) {
		case 5:
			result |= SPSW_DATA_BITS_DB5;
			break;
		case 6:
			result |= SPSW_DATA_BITS_DB6;
			break;
		case 7:
			result |= SPSW_DATA_BITS_DB7;
			break;
		case 8:
			result |= SPSW_DATA_BITS_DB8;
			break;
		default:
			throw_Illegal_Argument_Exception(env, "getParams Unknown databits");
			return -1;
		}

	}

	//StopBits
	if (*paramBitSet & SPSW_STOP_BITS_MASK) {
		switch (dcb.StopBits) {
		case ONESTOPBIT:
			result |= SPSW_STOP_BITS_1;
			break;
		case ONE5STOPBITS:
			result |= SPSW_STOP_BITS_1_5;
			break;
		case TWOSTOPBITS:
			result |= SPSW_STOP_BITS_2;
			break;
		default:
			throw_Illegal_Argument_Exception(env, "getParams Unknown stopbits");
			return -1;
		}
	}

	//Parity
	if (*paramBitSet & SPSW_PARITY_MASK) {
		switch (dcb.Parity) {
		case NOPARITY:
			result |= SPSW_PARITY_NONE;
			break;
		case ODDPARITY:
			result |= SPSW_PARITY_ODD;
			break;
		case EVENPARITY:
			result |= SPSW_PARITY_EVEN;
			break;
		case MARKPARITY:
			result |= SPSW_PARITY_MARK;
			break;
		case SPACEPARITY:
			result |= SPSW_PARITY_SPACE;
			break;
		default:
			throw_Illegal_Argument_Exception(env, "getParams unknown Parity");
			return -1;
		}
	}

	//FlowControl
	if (*paramBitSet & SPSW_FLOW_CONTROL_MASK) {
		result |= SPSW_FLOW_CONTROL_NONE;
		if (dcb.fRtsControl == RTS_CONTROL_HANDSHAKE) {
			result |= SPSW_FLOW_CONTROL_RTS_CTS_IN;
			result &= ~SPSW_FLOW_CONTROL_NONE; //clean NONE
		}
		if (dcb.fOutxCtsFlow == TRUE) {
			result |= SPSW_FLOW_CONTROL_RTS_CTS_OUT;
			result &= ~SPSW_FLOW_CONTROL_NONE; //clean NONE
		}
		if (dcb.fInX == TRUE) {
			result |= SPSW_FLOW_CONTROL_XON_XOFF_IN;
			result &= ~SPSW_FLOW_CONTROL_NONE; //clean NONE
		}
		if (dcb.fOutX == TRUE) {
			result |= SPSW_FLOW_CONTROL_XON_XOFF_OUT;
			result &= ~SPSW_FLOW_CONTROL_NONE; //clean NONE
		}
	}

	*paramBitSet = result;
	return 0;

}

static int setParams(JNIEnv *env, jobject sps, DCB *dcb, jint paramBitSet) {

	//Baudrate
	if (paramBitSet & SPSW_BAUDRATE_MASK) {

		dcb->BaudRate = spswBaudrateToNative(env,
				paramBitSet & SPSW_BAUDRATE_MASK);
		if (dcb->BaudRate == -1) {
			return -1;
		}

	}

	//DataBits
	if (paramBitSet & SPSW_DATA_BITS_MASK) {
		switch (paramBitSet & SPSW_DATA_BITS_MASK) {
		case SPSW_DATA_BITS_DB5:
			dcb->ByteSize = 5;
			if (dcb->StopBits == TWOSTOPBITS) {
				//Fix stopBits
				dcb->StopBits = ONE5STOPBITS;
			}
			break;
		case SPSW_DATA_BITS_DB6:
			dcb->ByteSize = 6;
			if (dcb->StopBits == ONE5STOPBITS) {
				//Fix stopBits
				dcb->StopBits = TWOSTOPBITS;
			}
			break;
		case SPSW_DATA_BITS_DB7:
			dcb->ByteSize = 7;
			if (dcb->StopBits == ONE5STOPBITS) {
				//Fix stopBits
				dcb->StopBits = TWOSTOPBITS;
			}
			break;
		case SPSW_DATA_BITS_DB8:
			dcb->ByteSize = 8;
			if (dcb->StopBits == ONE5STOPBITS) {
				//Fix stopBits
				dcb->StopBits = TWOSTOPBITS;
			}
			break;
		default:
			throw_Illegal_Argument_Exception(env, "setParams Wrong databits");
			return -1;
		}
	}

	//StopBits
	if (paramBitSet & SPSW_STOP_BITS_MASK) {

		switch (paramBitSet & SPSW_STOP_BITS_MASK) {
		case SPSW_STOP_BITS_1:
			dcb->StopBits = ONESTOPBIT;
			break;
		case SPSW_STOP_BITS_1_5:
			if (dcb->ByteSize == 5) {
				dcb->StopBits = ONE5STOPBITS;
			} else {
				throw_Illegal_Argument_Exception(env,
						"setParams setStopBits to 1.5: only for 5 dataBits 1.5 stoppbits are supported");
				return -1;
			}
			break;
		case SPSW_STOP_BITS_2:
			if (dcb->ByteSize == 5) {
				throw_Illegal_Argument_Exception(env,
						"setParams setStopBits to 2: 5 dataBits only 1.5 stoppbits are supported");
				return -1;
			} else {
				dcb->StopBits = TWOSTOPBITS;
			}
			break;
		default:
			throw_Illegal_Argument_Exception(env, "setParams Unknown stopbits");
			return -1;
		}

	}

	//Parity
	if (paramBitSet & SPSW_PARITY_MASK) {

		switch (paramBitSet & SPSW_PARITY_MASK) {
		case SPSW_PARITY_NONE:
			dcb->Parity = NOPARITY; // switch parity input checking off
			break;
		case SPSW_PARITY_ODD:
			dcb->Parity = ODDPARITY;
			break;
		case SPSW_PARITY_EVEN:
			dcb->Parity = EVENPARITY;
			break;
		case SPSW_PARITY_MARK:
			dcb->Parity = MARKPARITY;
			break;
		case SPSW_PARITY_SPACE:
			dcb->Parity = SPACEPARITY;
			break;
		default:
			throw_IOException_NativeError(env,
					"setParams Wrong Parity");
			return -1;
		}

	}

	//FlowControl
	if (paramBitSet & SPSW_FLOW_CONTROL_MASK) {
		dcb->fRtsControl = RTS_CONTROL_DISABLE;
		dcb->fOutxCtsFlow = FALSE;
		dcb->fOutX = FALSE;
		dcb->fInX = FALSE;
		if (paramBitSet & SPSW_FLOW_CONTROL_NONE) {
		} else {
			if ((paramBitSet & SPSW_FLOW_CONTROL_RTS_CTS_IN)
					== SPSW_FLOW_CONTROL_RTS_CTS_IN) {
				dcb->fRtsControl = RTS_CONTROL_HANDSHAKE;
			}
			if ((paramBitSet & SPSW_FLOW_CONTROL_RTS_CTS_OUT)
					== SPSW_FLOW_CONTROL_RTS_CTS_OUT) {
				dcb->fOutxCtsFlow = TRUE;
			}
			if ((paramBitSet & SPSW_FLOW_CONTROL_XON_XOFF_IN)
					== SPSW_FLOW_CONTROL_XON_XOFF_IN) {
				dcb->fInX = TRUE;
			}
			if ((paramBitSet & SPSW_FLOW_CONTROL_XON_XOFF_OUT)
					== SPSW_FLOW_CONTROL_XON_XOFF_OUT) {
				dcb->fOutX = TRUE;
			}
		}
	}

	HANDLE hFile = GET_FILEDESCRIPTOR(env, sps);

	if (!SetCommState(hFile, dcb)) {
		switch (GetLastError()) {
		case ERROR_INVALID_PARAMETER:
			throw_Illegal_Argument_Exception(env,
					"setParams: Wrong FlowControl\n GetLastError() == ERROR_INVALID_PARAMETER");
			break;
		case ERROR_GEN_FAILURE:
			throw_Illegal_Argument_Exception(env,
					"setParams: Wrong FlowControl\n GetLastError() == ERROR_GEN_FAILURE");
			break;
		default:
			throw_ClosedOrNativeException(env, sps, "setParams SetCommState");
		}
	}

	jint paramsRead =
	SPSW_BAUDRATE_MASK & paramBitSet ? SPSW_BAUDRATE_MASK : 0;
	paramsRead |= SPSW_DATA_BITS_MASK & paramBitSet ? SPSW_DATA_BITS_MASK : 0;
	paramsRead |= SPSW_STOP_BITS_MASK & paramBitSet ? SPSW_STOP_BITS_MASK : 0;
	paramsRead |= SPSW_PARITY_MASK & paramBitSet ? SPSW_PARITY_MASK : 0;
	paramsRead |=
	SPSW_FLOW_CONTROL_MASK & paramBitSet ? SPSW_FLOW_CONTROL_MASK : 0;

	if (getParams(env, sps, &paramsRead)) {
		return -1;
	}

	if (paramsRead != paramBitSet) {
		char buf[512];
		if ((paramsRead & SPSW_BAUDRATE_MASK)
				!= (paramBitSet & SPSW_BAUDRATE_MASK)) {
			snprintf(buf, sizeof(buf),
					"Could not set Baudrate! NATIVE: paramsRead(0x%08lx) != paramBitSet(0x%08lx)",
					paramsRead, paramBitSet);
		} else if ((paramsRead & SPSW_DATA_BITS_MASK)
				!= (paramBitSet & SPSW_DATA_BITS_MASK)) {
			snprintf(buf, sizeof(buf),
					"Could not set DataBits! NATIVE: paramsRead(0x%08lx) != paramBitSet(0x%08lx)",
					paramsRead, paramBitSet);
		} else if ((paramsRead & SPSW_STOP_BITS_MASK)
				!= (paramBitSet & SPSW_STOP_BITS_MASK)) {
			snprintf(buf, sizeof(buf),
					"Could not set StopBits! NATIVE: paramsRead(0x%08lx) != paramBitSet(0x%08lx)",
					paramsRead, paramBitSet);
		} else if ((paramsRead & SPSW_PARITY_MASK)
				!= (paramBitSet & SPSW_PARITY_MASK)) {
			snprintf(buf, sizeof(buf),
					"Could not set Parity! NATIVE: paramsRead(0x%08lx) != paramBitSet(0x%08lx)",
					paramsRead, paramBitSet);
		} else if ((paramsRead & SPSW_FLOW_CONTROL_MASK)
				!= (paramBitSet & SPSW_FLOW_CONTROL_MASK)) {
			snprintf(buf, sizeof(buf),
					"Could not set FlowControl! NATIVE: paramsRead(0x%08lx) != paramBitSet(0x%08lx)",
					paramsRead, paramBitSet);
		} else {
			snprintf(buf, sizeof(buf),
					"Could not set unknown parameter! NATIVE: paramsRead(0x%08lx) != paramBitSet(0x%08lx)",
					paramsRead, paramBitSet);

		}
		throw_Illegal_Argument_Exception(env, buf);
		return -1;
	}
	return 0;
}

/*
 * Class:     de_ibapl_spsw_provider_AbstractSerialPortSocket
 * Method:    close0
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_AbstractSerialPortSocket_close0
(JNIEnv *env, jobject sps) {

	HANDLE hFile = GET_FILEDESCRIPTOR(env, sps);
	SET_FILEDESCRIPTOR(env, sps, INVALID_HANDLE_VALUE);
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
 * Class:     de_ibapl_spsw_provider_AbstractSerialPortSocket
 * Method:    getInBufferBytesCount
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_AbstractSerialPortSocket_getInBufferBytesCount(
		JNIEnv *env, jobject sps) {
	DWORD lpErrors;
	COMSTAT comstat;

	HANDLE hFile = GET_FILEDESCRIPTOR(env, sps);

	if (ClearCommError(hFile, &lpErrors, &comstat)) {
		return (jint) comstat.cbInQue;
	} else {
		throw_ClosedOrNativeException(env, sps,
				"getInBufferBytesCount ClearCommError");
		return -1;
	}
}

/*
 * Class:     de_ibapl_spsw_provider_AbstractSerialPortSocket
 * Method:    getOutBufferBytesCount
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_AbstractSerialPortSocket_getOutBufferBytesCount(
		JNIEnv *env, jobject sps) {
	DWORD lpErrors;
	COMSTAT comstat;

	HANDLE hFile = GET_FILEDESCRIPTOR(env, sps);

	if (ClearCommError(hFile, &lpErrors, &comstat)) {
		return (jint) comstat.cbOutQue;
	} else {
		throw_ClosedOrNativeException(env, sps,
				"getOutBufferBytesCount ClearCommError");
		return -1;
	}
}

/*
 * Class:     de_ibapl_spsw_provider_AbstractSerialPortSocket
 * Method:    getParameters
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_AbstractSerialPortSocket_getParameters(
		JNIEnv *env, jobject sps, jint parameterBitSetMask) {
	if (getParams(env, sps, &parameterBitSetMask)) {
		return 0;
	}
	return parameterBitSetMask;
}

/*
 * Class:     de_ibapl_spsw_provider_AbstractSerialPortSocket
 * Method:    getXOFFChar
 * Signature: ()C
 */
JNIEXPORT jchar JNICALL Java_de_ibapl_spsw_provider_AbstractSerialPortSocket_getXOFFChar(
		JNIEnv *env, jobject sps) {
	DCB dcb;

	HANDLE hFile = GET_FILEDESCRIPTOR(env, sps);

	if (!GetCommState(hFile, &dcb)) {
		throw_ClosedOrNativeException(env, sps, "getXOFFChar GetCommState");
		return 0;
	}
	return dcb.XoffChar;
}

/*
 * Class:     de_ibapl_spsw_provider_AbstractSerialPortSocket
 * Method:    getXONChar
 * Signature: ()C
 */
JNIEXPORT jchar JNICALL Java_de_ibapl_spsw_provider_AbstractSerialPortSocket_getXONChar(
		JNIEnv *env, jobject sps) {
	DCB dcb;

	HANDLE hFile = GET_FILEDESCRIPTOR(env, sps);

	if (!GetCommState(hFile, &dcb)) {
		throw_ClosedOrNativeException(env, sps, "getXONChar GetCommState");
		return 0;
	}
	return dcb.XonChar;
}

/*
 * Class:     de_ibapl_spsw_provider_AbstractSerialPortSocket
 * Method:    isCTS
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_de_ibapl_spsw_provider_AbstractSerialPortSocket_isCTS(
		JNIEnv *env, jobject sps) {
	return getCommModemStatus(env, sps, MS_CTS_ON);
}

/*
 * Class:     de_ibapl_spsw_provider_AbstractSerialPortSocket
 * Method:    isDSR
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_de_ibapl_spsw_provider_AbstractSerialPortSocket_isDSR(
		JNIEnv *env, jobject sps) {
	return getCommModemStatus(env, sps, MS_DSR_ON);
}

/*
 * Class:     de_ibapl_spsw_provider_AbstractSerialPortSocket
 * Method:    isIncommingRI
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_de_ibapl_spsw_provider_AbstractSerialPortSocket_isIncommingRI(
		JNIEnv *env, jobject sps) {
	return getCommModemStatus(env, sps, MS_RING_ON);
}

/*
 * Class:     de_ibapl_spsw_provider_AbstractSerialPortSocket
 * Method:    open
 * Signature: (Ljava/lang/String;I)V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_AbstractSerialPortSocket_open
(JNIEnv *env, jobject sps, jstring portName, jint paramBitSet) {
//Do not try to reopen port and therefore failing and overriding the file descriptor
	if (GET_FILEDESCRIPTOR(env, sps) != INVALID_HANDLE_VALUE) {
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

		SET_FILEDESCRIPTOR(env, sps, INVALID_HANDLE_VALUE);

		switch (GetLastError()) {
			case ERROR_ACCESS_DENIED:
			throw_PortBusyException(env, portName);
			break;
			case ERROR_FILE_NOT_FOUND:
			throw_PortNotFoundException(env, portName);
			break;
			default:
			throw_IOException_NativeError(env, "Open");
		}
		return;
	}
	// The port is open, but maybe not configured ... setParam and getParam needs this to be set for their field access
	SET_FILEDESCRIPTOR(env, sps, hFile);

	DCB dcb;
	if (!GetCommState(hFile, &dcb)) {
		CloseHandle(hFile);

		SET_FILEDESCRIPTOR(env, sps, INVALID_HANDLE_VALUE);

		throw_NotASerialPortException(env, portName);
		return;
	}

	if (paramBitSet != SPSW_NO_PARAMS_TO_SET) {
		//set baudrate etc.
		if (setParams(env, sps, &dcb, paramBitSet)) {
			CloseHandle(hFile);
			SET_FILEDESCRIPTOR(env, sps, INVALID_HANDLE_VALUE);
			return;
		}
	}

	COMMTIMEOUTS lpCommTimeouts;
	if (!GetCommTimeouts(hFile, &lpCommTimeouts)) {
		CloseHandle(hFile);

		SET_FILEDESCRIPTOR(env, sps, INVALID_HANDLE_VALUE);

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

		SET_FILEDESCRIPTOR(env, sps, INVALID_HANDLE_VALUE);

		throw_IOException_NativeError(env, "Open SetCommTimeouts");
		return;
	}

}

/*
 * Class:     de_ibapl_spsw_provider_AbstractSerialPortSocket
 * Method:    readBytes
 * Signature: ([BII)I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_AbstractSerialPortSocket_readBytes(
		JNIEnv *env, jobject sps, jbyteArray bytes, jint off, jint len) {

	jbyte *lpBuffer = (jbyte*) malloc(len);

	HANDLE hFile = GET_FILEDESCRIPTOR(env, sps);

	DWORD dwBytesRead;
	OVERLAPPED overlapped;
	overlapped.Offset = 0;
	overlapped.OffsetHigh = 0;
	overlapped.hEvent = CreateEventA(NULL, TRUE, FALSE, NULL);

	if (!ReadFile(hFile, lpBuffer, len, NULL, &overlapped)) {

		if (GetLastError() != ERROR_IO_PENDING) {
			if (GET_FILEDESCRIPTOR(env, sps) == INVALID_HANDLE_VALUE) {
				//closed no-op
			} else {
				throw_IOException_NativeError(env,
						"Error readBytes(GetLastError)");
			}
			free(lpBuffer);
			CloseHandle(overlapped.hEvent);
			return -1;
		}

		//overlapped path
		if (WaitForSingleObject(overlapped.hEvent, INFINITE) != WAIT_OBJECT_0) {
			if (GET_FILEDESCRIPTOR(env, sps) == INVALID_HANDLE_VALUE) {
				//closed no-op
			} else {
				throw_IOException_NativeError(env,
						"Error readBytes (WaitForSingleObject)");
			}
			free(lpBuffer);
			CloseHandle(overlapped.hEvent);
			return -1;
		}

	}

	if (!GetOverlappedResult(hFile, &overlapped, &dwBytesRead, FALSE)) {
		if (GET_FILEDESCRIPTOR(env, sps) == INVALID_HANDLE_VALUE) {
			//closed no-op
		} else {
			throw_InterruptedIOExceptionWithError(env, dwBytesRead,
					"Error readBytes (GetOverlappedResult)");
		}
		free(lpBuffer);
		CloseHandle(overlapped.hEvent);
		return -1;
	}

	CloseHandle(overlapped.hEvent);

	(*env)->SetByteArrayRegion(env, bytes, off, dwBytesRead, lpBuffer);

	free(lpBuffer);

	if (dwBytesRead > 0) {
		//Success
		return dwBytesRead;
	} else if (dwBytesRead == 0) {
		if (GET_FILEDESCRIPTOR(env, sps) == INVALID_HANDLE_VALUE) {
			//closed no-op
		} else {
			throw_TimeoutIOException(env, dwBytesRead);
		}
		return -1;
	} else {
		throw_IOException_NativeError(env,
				"Should never happen! readBytes dwBytes < 0");
		return -1;
	}

	throw_IOException_NativeError(env,
			"Should never happen! readBytes fall trough");
	return -1;
}

/*
 * Class:     de_ibapl_spsw_provider_AbstractSerialPortSocket
 * Method:    readSingle
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_AbstractSerialPortSocket_readSingle(
		JNIEnv *env, jobject sps) {

	jbyte lpBuffer;

	HANDLE hFile = GET_FILEDESCRIPTOR(env, sps);

	DWORD dwBytesRead;
	OVERLAPPED overlapped;
	overlapped.Offset = 0;
	overlapped.OffsetHigh = 0;
	overlapped.hEvent = CreateEventA(NULL, TRUE, FALSE, NULL);

	if (!ReadFile(hFile, &lpBuffer, 1, NULL, &overlapped)) {

		if (GetLastError() != ERROR_IO_PENDING) {
			if (GET_FILEDESCRIPTOR(env, sps) == INVALID_HANDLE_VALUE) {
				//closed no-op
			} else {
				throw_IOException_NativeError(env,
						"Error readSingle (GetLastError)");
			}
			CloseHandle(overlapped.hEvent);
			return -1;
		}

		if (WaitForSingleObject(overlapped.hEvent, INFINITE) != WAIT_OBJECT_0) {
			if (GET_FILEDESCRIPTOR(env, sps) == INVALID_HANDLE_VALUE) {
				//closed no-op
			} else {
				throw_IOException_NativeError(env,
						"Error readSingle (WaitForSingleObject)");
			}
			CloseHandle(overlapped.hEvent);
			return -1;
		}

	}

	if (!GetOverlappedResult(hFile, &overlapped, &dwBytesRead, FALSE)) {
		if (GET_FILEDESCRIPTOR(env, sps) == INVALID_HANDLE_VALUE) {
			//closed no-op
		} else {
			throw_InterruptedIOExceptionWithError(env, dwBytesRead,
					"Error readSingle (GetOverlappedResult)");
		}
		CloseHandle(overlapped.hEvent);
		return -1;
	}

	CloseHandle(overlapped.hEvent);
	if (dwBytesRead == 1) {
		//Success
		return lpBuffer & 0xFF;
	} else if (dwBytesRead == 0) {
		if (GET_FILEDESCRIPTOR(env, sps) == INVALID_HANDLE_VALUE) {
			//closed no-op
		} else {
			throw_TimeoutIOException(env, dwBytesRead);
		}
		return -1;
	} else {
		throw_InterruptedIOExceptionWithError(env, dwBytesRead,
				"Should never happen! readSingle dwBytes < 0");
		return -1;
	}

	throw_IOException_NativeError(env,
			"Should never happen! readSingle fall trough");
	return -1;
}

/*
 * Class:     de_ibapl_spsw_provider_AbstractSerialPortSocket
 * Method:    sendBreak
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_AbstractSerialPortSocket_sendBreak
(JNIEnv *env, jobject sps, jint duration) {
	HANDLE hFile = GET_FILEDESCRIPTOR(env, sps);

	if (duration < 0) {
		throw_IOException_NativeError(env, "sendBreak duration must be grater than 0");
		return;
	}

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

/*
 * Class:     de_ibapl_spsw_provider_AbstractSerialPortSocket
 * Method:    setBreak
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_AbstractSerialPortSocket_setBreak
(JNIEnv *env, jobject sps, jboolean enabled) {
	DWORD dwFunc = (enabled == JNI_TRUE) ? SETBREAK : CLRBREAK;

	HANDLE hFile = GET_FILEDESCRIPTOR(env, sps);

	if (!EscapeCommFunction(hFile, dwFunc)) {
		if (GetLastError() == ERROR_INVALID_PARAMETER) {
			throw_Illegal_Argument_Exception(env, "setBreak: Wrong value");
		} else {
			throw_ClosedOrNativeException(env, sps, "Can't set/clear BREAK");
		}
	}
}

/*
 * Class:     de_ibapl_spsw_provider_AbstractSerialPortSocket
 * Method:    setDTR
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_AbstractSerialPortSocket_setDTR
(JNIEnv *env, jobject sps, jboolean enabled) {
	DWORD dwFunc = (enabled == JNI_TRUE) ? SETDTR : CLRDTR;

	HANDLE hFile = GET_FILEDESCRIPTOR(env, sps);

	if (!EscapeCommFunction(hFile, dwFunc)) {
		if (GetLastError() == ERROR_INVALID_PARAMETER) {
			throw_Illegal_Argument_Exception(env, "setDTR: Wrong value");
		} else {
			throw_ClosedOrNativeException(env, sps, "Can't set/clear DTR");
		}
	}
}

/*
 * Class:     de_ibapl_spsw_provider_AbstractSerialPortSocket
 * Method:    setParameters
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_AbstractSerialPortSocket_setParameters(
		JNIEnv *env, jobject sps, jint parameterBitSet) {

	HANDLE hFile = GET_FILEDESCRIPTOR(env, sps);

	DCB dcb;

	if (!GetCommState(hFile, &dcb)) {
		throw_ClosedOrNativeException(env, sps, "setXOFFChar GetCommState");
		return;
	}

	setParams(env, sps, &dcb, parameterBitSet);
}

/*
 * Class:     de_ibapl_spsw_provider_AbstractSerialPortSocket
 * Method:    setRTS
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_AbstractSerialPortSocket_setRTS
(JNIEnv *env, jobject sps, jboolean enabled) {
	DWORD dwFunc = (enabled == JNI_TRUE) ? SETRTS : CLRRTS;

	HANDLE hFile = GET_FILEDESCRIPTOR(env, sps);

	if (!EscapeCommFunction(hFile, dwFunc)) {
		if (GetLastError() == ERROR_INVALID_PARAMETER) {
			throw_Illegal_Argument_Exception(env, "setRTS: Wrong value");
		} else {
			throw_ClosedOrNativeException(env, sps, "Can't set/clear RTS");
		}
	}
}

/*
 * Class:     de_ibapl_spsw_provider_AbstractSerialPortSocket
 * Method:    setXOFFChar
 * Signature: (C)V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_AbstractSerialPortSocket_setXOFFChar
(JNIEnv *env, jobject sps, jchar c) {
	DCB dcb;

	HANDLE hFile = GET_FILEDESCRIPTOR(env, sps);

	if (!GetCommState(hFile, &dcb)) {
		throw_ClosedOrNativeException(env, sps, "setXOFFChar GetCommState");
		return;
	}

	dcb.XoffChar = c;

	if (!SetCommState(hFile, &dcb)) {
		switch (GetLastError()) {
			case ERROR_INVALID_PARAMETER:
			throw_Illegal_Argument_Exception(env, "etXOFFChar: Wrong value\n GetLastError() == ERROR_INVALID_PARAMETER");
			break;
			case ERROR_GEN_FAILURE:
			throw_Illegal_Argument_Exception(env, "etXOFFChar: Wrong value\n GetLastError() == ERROR_GEN_FAILURE");
			break;
			default:
			throw_ClosedOrNativeException(env, sps, "setXOFFChar SetCommState");
		}
	}
}

/*
 * Class:     de_ibapl_spsw_provider_AbstractSerialPortSocket
 * Method:    setXONChar
 * Signature: (C)V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_AbstractSerialPortSocket_setXONChar
(JNIEnv *env, jobject sps, jchar c) {
	DCB dcb;

	HANDLE hFile = GET_FILEDESCRIPTOR(env, sps);

	if (!GetCommState(hFile, &dcb)) {
		throw_ClosedOrNativeException(env, sps, "setXONChar GetCommState");
		return;
	}

	dcb.XonChar = c;

	if (!SetCommState(hFile, &dcb)) {
		switch (GetLastError()) {
			case ERROR_INVALID_PARAMETER:
			throw_Illegal_Argument_Exception(env, "setXONChar: Wrong value\n GetLastError() == ERROR_INVALID_PARAMETER");
			break;
			case ERROR_GEN_FAILURE:
			throw_Illegal_Argument_Exception(env, "setXONChar: Wrong value\n GetLastError() == ERROR_GEN_FAILURE");
			break;
			default:
			throw_ClosedOrNativeException(env, sps, "setXONChar SetCommState");
		}
	}
}

/*
 * Class:     de_ibapl_spsw_provider_AbstractSerialPortSocket
 * Method:    writeBytes
 * Signature: ([BII)V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_AbstractSerialPortSocket_writeBytes
(JNIEnv *env, jobject sps, jbyteArray bytes, jint off, jint len) {

	jbyte *buf = (jbyte*) malloc(len);
	(*env)->GetByteArrayRegion(env, bytes, off, len, buf);

	HANDLE hFile = GET_FILEDESCRIPTOR(env, sps);

	DWORD dwBytesWritten = 0;
	OVERLAPPED overlapped;
	overlapped.Offset = 0;
	overlapped.OffsetHigh = 0;
	overlapped.hEvent = CreateEventA(NULL, TRUE, FALSE, NULL);
	if (!WriteFile(hFile, buf, len, NULL, &overlapped)) {

		if (GetLastError() != ERROR_IO_PENDING) {
			CloseHandle(overlapped.hEvent);
			free(buf);
			throw_IO_ClosedOrInterruptedException(env, sps, dwBytesWritten, "Error writeBytes (GetLastError)");
			return;
		}

		if (WaitForSingleObject(overlapped.hEvent, INFINITE) != WAIT_OBJECT_0) {
			CloseHandle(overlapped.hEvent);
			free(buf);
			throw_IO_ClosedOrInterruptedException(env, sps, dwBytesWritten, "Error writeBytes (WaitForSingleObject)");
			return;
		}

	}

	if (!GetOverlappedResult(hFile, &overlapped, &dwBytesWritten, FALSE)) {
		CloseHandle(overlapped.hEvent);
		free(buf);
		throw_IO_ClosedOrInterruptedException(env, sps, dwBytesWritten, "Error writeBytes (GetOverlappedResult)");
		return;
	}

	CloseHandle(overlapped.hEvent);
	free(buf);
	if (dwBytesWritten != len) {
		if (GET_FILEDESCRIPTOR(env, sps) == INVALID_HANDLE_VALUE) {
			throw_IO_ClosedException(env, dwBytesWritten);
			return;
		} else {
			if (GetLastError() == ERROR_IO_PENDING) {
				throw_TimeoutIOException(env, dwBytesWritten);
			} else {
				throw_InterruptedIOExceptionWithError(env, dwBytesWritten, "Error writeBytes too view written");
			}
			return;
		}
	}

//Success
	return;
}

/*
 * Class:     de_ibapl_spsw_provider_AbstractSerialPortSocket
 * Method:    writeSingle
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_AbstractSerialPortSocket_writeSingle
(JNIEnv *env, jobject sps, jint b) {

	HANDLE hFile = GET_FILEDESCRIPTOR(env, sps);

	DWORD dwBytesWritten;
	OVERLAPPED overlapped;
	overlapped.Offset = 0;
	overlapped.OffsetHigh = 0;
	overlapped.hEvent = CreateEventA(NULL, TRUE, FALSE, NULL);

	if (!WriteFile(hFile, &b, 1, NULL, &overlapped)) {

		if (GetLastError() != ERROR_IO_PENDING) {
			CloseHandle(overlapped.hEvent);
			throw_IO_ClosedOrInterruptedException(env, sps, dwBytesWritten, "Error writeSingle (GetLastError)");
			return;
		}

		if (WaitForSingleObject(overlapped.hEvent, INFINITE) != WAIT_OBJECT_0) {
			CloseHandle(overlapped.hEvent);
			throw_IO_ClosedOrInterruptedException(env, sps, dwBytesWritten, "Error writeSingle (WaitForSingleObject)");
			return;
		}

	}

	if (!GetOverlappedResult(hFile, &overlapped, &dwBytesWritten, FALSE)) {
		CloseHandle(overlapped.hEvent);
		throw_IO_ClosedOrInterruptedException(env, sps, dwBytesWritten, "Error writeSingle (GetOverlappedResult)");
		return;
	}

	CloseHandle(overlapped.hEvent);
	if (dwBytesWritten != 1) {
		if (GET_FILEDESCRIPTOR(env, sps) == INVALID_HANDLE_VALUE) {
			throw_IO_ClosedException(env, dwBytesWritten);
			return;
		} else {
			if (GetLastError() == ERROR_IO_PENDING) {
				throw_TimeoutIOException(env, dwBytesWritten);
			} else {
				throw_InterruptedIOExceptionWithError(env, dwBytesWritten, "Error writeSingle");
			}
			return;
		}
	}

//Success
	return;
}

/*
 * Class:     de_ibapl_spsw_provider_GenericWinSerialPortSocket
 * Method:    getWindowsBasedPortNames
 * Signature: ()[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_getWindowsBasedPortNames(
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

/*
 * Class:     de_ibapl_spsw_provider_GenericWinSerialPortSocket
 * Method:    getInterByteReadTimeout
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_getInterByteReadTimeout(
		JNIEnv *env, jobject sps) {

	HANDLE hFile = GET_FILEDESCRIPTOR(env, sps);

	COMMTIMEOUTS lpCommTimeouts;
	if (!GetCommTimeouts(hFile, &lpCommTimeouts)) {
		throw_ClosedOrNativeException(env, sps, "getInterByteReadTimeout");
		return -1;
	}
	if ((lpCommTimeouts.ReadIntervalTimeout == MAXDWORD)
			&& (lpCommTimeouts.ReadTotalTimeoutMultiplier == MAXDWORD)) {
		return 0;
	} else {
		return lpCommTimeouts.ReadIntervalTimeout;
	}
}

/*
 * Class:     de_ibapl_spsw_provider_GenericWinSerialPortSocket
 * Method:    getOverallReadTimeout
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_getOverallReadTimeout(
		JNIEnv *env, jobject sps) {

	HANDLE hFile = GET_FILEDESCRIPTOR(env, sps);

	COMMTIMEOUTS lpCommTimeouts;
	if (!GetCommTimeouts(hFile, &lpCommTimeouts)) {
		throw_ClosedOrNativeException(env, sps, "getOverallReadTimeout");
		return -1;
	}
	return lpCommTimeouts.ReadTotalTimeoutConstant;
}

/*
 * Class:     de_ibapl_spsw_provider_GenericWinSerialPortSocket
 * Method:    getOverallWriteTimeout
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_getOverallWriteTimeout(
		JNIEnv *env, jobject sps) {

	HANDLE hFile = GET_FILEDESCRIPTOR(env, sps);

	COMMTIMEOUTS lpCommTimeouts;
	if (!GetCommTimeouts(hFile, &lpCommTimeouts)) {
		throw_ClosedOrNativeException(env, sps, "getOverallWriteTimeout");
		return -1;
	}
	return lpCommTimeouts.WriteTotalTimeoutConstant;
}
/*
 * Class:     de_ibapl_spsw_provider_GenericWinSerialPortSocket
 * Method:    sendXOFF
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_sendXOFF
(JNIEnv *env, jobject sps) {
	throw_IOException_NativeError(env, "setXOFF not implemented yet");
}

/*
 * Class:     de_ibapl_spsw_provider_GenericWinSerialPortSocket
 * Method:    sendXON
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_sendXON
(JNIEnv *env, jobject sps) {
	throw_IOException_NativeError(env, "setXON not implemented yet");
}

/*
 * Class:     de_ibapl_spsw_provider_GenericWinSerialPortSocket
 * Method:    setTimeouts
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_provider_GenericWinSerialPortSocket_setTimeouts
(JNIEnv *env, jobject sps, jint interByteReadTimeout, jint overallReadTimeout, jint overallWriteTimeout) {

	HANDLE hFile = GET_FILEDESCRIPTOR(env, sps);

	COMMTIMEOUTS lpCommTimeouts;
	if (!GetCommTimeouts(hFile, &lpCommTimeouts)) {
		throw_ClosedOrNativeException(env, sps, "setTimeouts");
		return;
	}

	if (overallWriteTimeout < 0) {
		throw_Illegal_Argument_Exception(env, "setTimeouts: overallWriteTimeout must >= 0");
		return;
	} else if (overallWriteTimeout == MAXDWORD) {
		//MAXDWORD has a special meaning...
		overallWriteTimeout = MAXDWORD - 1;
	}

	if (overallReadTimeout < 0) {
		throw_Illegal_Argument_Exception(env, "setTimeouts: overallReadTimeout must >= 0");
		return;
	} else if (overallReadTimeout == MAXDWORD) {
		//MAXDWORD has a special meaning...
		overallReadTimeout = MAXDWORD - 1;
	}

	if (interByteReadTimeout < 0) {
		throw_Illegal_Argument_Exception(env, "setReadTimeouts: interByteReadTimeout must >= 0");
		return;
	} else if (interByteReadTimeout == MAXDWORD) {
		//MAXDWORD has a special meaning...
		interByteReadTimeout = MAXDWORD - 1;
	}

	if ((interByteReadTimeout == 0) && (overallReadTimeout > 0)) {
		//This fits best for wait a timeout and have no interByteReadTimeout see also getInterbyteReadTimeout for reading back
		lpCommTimeouts.ReadIntervalTimeout = MAXDWORD;
		lpCommTimeouts.ReadTotalTimeoutMultiplier = MAXDWORD;
		lpCommTimeouts.ReadTotalTimeoutConstant = overallReadTimeout;
	} else {
		lpCommTimeouts.ReadIntervalTimeout = interByteReadTimeout;
		lpCommTimeouts.ReadTotalTimeoutMultiplier = 0;
		lpCommTimeouts.ReadTotalTimeoutConstant = overallReadTimeout;
	}

	lpCommTimeouts.WriteTotalTimeoutMultiplier = 0;
	lpCommTimeouts.WriteTotalTimeoutConstant = overallWriteTimeout;

	if (!SetCommTimeouts(hFile, &lpCommTimeouts)) {
		if (GetLastError() == ERROR_INVALID_PARAMETER) {
			throw_Illegal_Argument_Exception(env, "setTimeouts: Wrong Timeouts");
		} else {
			throw_ClosedOrNativeException(env, sps, "setTimeouts SetCommTimeouts");
		}
		return;
	}
}
