#include "spsw-jni.h"

#include "de_ibapl_spsw_jniprovider_GenericWinSerialPortSocket.h"

#ifdef __cplusplus
extern "C" {
#endif

static jint DCB_BaudrateToSpswSpeed(JNIEnv *env, DWORD speed) {
	switch (speed) {
	case 0:
		return SPSW_SPEED_0_BPS;
	case 50:
		return SPSW_SPEED_50_BPS;
	case 75:
		return SPSW_SPEED_75_BPS;
	case 110:
		return SPSW_SPEED_110_BPS;
	case 134:
		return SPSW_SPEED_134_BPS;
	case 150:
		return SPSW_SPEED_150_BPS;
	case 200:
		return SPSW_SPEED_200_BPS;
	case 300:
		return SPSW_SPEED_300_BPS;
	case 600:
		return SPSW_SPEED_600_BPS;
	case 1200:
		return SPSW_SPEED_1200_BPS;
	case 1800:
		return SPSW_SPEED_1800_BPS;
	case 2400:
		return SPSW_SPEED_2400_BPS;
	case 4800:
		return SPSW_SPEED_4800_BPS;
	case 9600:
		return SPSW_SPEED_9600_BPS;
	case 19200:
		return SPSW_SPEED_19200_BPS;
	case 38400:
		return SPSW_SPEED_38400_BPS;
	case 57600:
		return SPSW_SPEED_57600_BPS;
	case 115200:
		return SPSW_SPEED_115200_BPS;
	case 230400:
		return SPSW_SPEED_230400_BPS;
	case 460800:
		return SPSW_SPEED_460800_BPS;
	case 500000:
		return SPSW_SPEED_500000_BPS;
	case 576000:
		return SPSW_SPEED_576000_BPS;
	case 921600:
		return SPSW_SPEED_921600_BPS;
	case 1000000:
		return SPSW_SPEED_1000000_BPS;
	case 1152000:
		return SPSW_SPEED_1152000_BPS;
	case 1500000:
		return SPSW_SPEED_1500000_BPS;
	case 2000000:
		return SPSW_SPEED_2000000_BPS;
	case 2500000:
		return SPSW_SPEED_2500000_BPS;
	case 3000000:
		return SPSW_SPEED_3000000_BPS;
	case 3500000:
		return SPSW_SPEED_3500000_BPS;
	case 4000000:
		return SPSW_SPEED_4000000_BPS;
	default:
		throw_IllegalArgumentException(env, "Speed not supported");
		return -1;
	}
}

static jint setDCB_Baudrate(JNIEnv *env, jint speed, DCB *dcb) {
	switch (speed) {
	case SPSW_SPEED_0_BPS:
		dcb->BaudRate = 0;
		break;
	case SPSW_SPEED_50_BPS:
		dcb->BaudRate = 50;
		break;
	case SPSW_SPEED_75_BPS:
		dcb->BaudRate = 75;
		break;
	case SPSW_SPEED_110_BPS:
		dcb->BaudRate = 110;
		break;
	case SPSW_SPEED_134_BPS:
		dcb->BaudRate = 134;
		break;
	case SPSW_SPEED_150_BPS:
		dcb->BaudRate = 150;
		break;
	case SPSW_SPEED_200_BPS:
		dcb->BaudRate = 200;
		break;
	case SPSW_SPEED_300_BPS:
		dcb->BaudRate = 300;
		break;
	case SPSW_SPEED_600_BPS:
		dcb->BaudRate = 600;
		break;
	case SPSW_SPEED_1200_BPS:
		dcb->BaudRate = 1200;
		break;
	case SPSW_SPEED_1800_BPS:
		dcb->BaudRate = 1800;
		break;
	case SPSW_SPEED_2400_BPS:
		dcb->BaudRate = 2400;
		break;
	case SPSW_SPEED_4800_BPS:
		dcb->BaudRate = 4800;
		break;
	case SPSW_SPEED_9600_BPS:
		dcb->BaudRate = 9600;
		break;
	case SPSW_SPEED_19200_BPS:
		dcb->BaudRate = 19200;
		break;
	case SPSW_SPEED_38400_BPS:
		dcb->BaudRate = 38400;
		break;
	case SPSW_SPEED_57600_BPS:
		dcb->BaudRate = 57600;
		break;
	case SPSW_SPEED_115200_BPS:
		dcb->BaudRate = 115200;
		break;
	case SPSW_SPEED_230400_BPS:
		dcb->BaudRate = 230400;
		break;
	case SPSW_SPEED_460800_BPS:
		dcb->BaudRate = 460800;
		break;
	case SPSW_SPEED_500000_BPS:
		dcb->BaudRate = 500000;
		break;
	case SPSW_SPEED_576000_BPS:
		dcb->BaudRate = 576000;
		break;
	case SPSW_SPEED_921600_BPS:
		dcb->BaudRate = 921600;
		break;
	case SPSW_SPEED_1000000_BPS:
		dcb->BaudRate = 1000000;
		break;
	case SPSW_SPEED_1152000_BPS:
		dcb->BaudRate = 1152000;
		break;
	case SPSW_SPEED_1500000_BPS:
		dcb->BaudRate = 1500000;
		break;
	case SPSW_SPEED_2000000_BPS:
		dcb->BaudRate = 2000000;
		break;
	case SPSW_SPEED_2500000_BPS:
		dcb->BaudRate = 2500000;
		break;
	case SPSW_SPEED_3000000_BPS:
		dcb->BaudRate = 3000000;
		break;
	case SPSW_SPEED_3500000_BPS:
		dcb->BaudRate = 3500000;
		break;
	case SPSW_SPEED_4000000_BPS:
		dcb->BaudRate = 4000000;
		break;
	default:
		throw_IllegalArgumentException(env, "Speed not supported");
		return -1;
	}
	return 0;
}

static int getParams(JNIEnv *env, jobject sps, jint* paramBitSet) {

	jint result = 0;
	DCB dcb;
        dcb.DCBlength = sizeof(DCB);
        
	HANDLE hFile = (HANDLE)(uintptr_t)(*env)->GetLongField(env, sps, spsw_fd);

	if (!GetCommState(hFile, &dcb)) {
		throw_ClosedOrNativeException(env, sps, "getParams GetCommState");
		return -1;
	}

	//Speed
	if (*paramBitSet & SPSW_SPEED_MASK) {
		jint speed = DCB_BaudrateToSpswSpeed(env, dcb.BaudRate);
		if (speed == -1) {
			//IAE is already thrown...
			return -1;
		} else {
			result |= speed;
		}
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
			throw_IllegalArgumentException(env, "getParams Unknown databits");
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
			throw_IllegalArgumentException(env, "getParams Unknown stopbits");
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
			throw_IllegalArgumentException(env, "getParams unknown Parity");
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

int setParams(JNIEnv *env, jobject sps, DCB *dcb, jint paramBitSet) {

	//Speed
	if (paramBitSet & SPSW_SPEED_MASK) {

		if (setDCB_Baudrate(env, paramBitSet & SPSW_SPEED_MASK, dcb) == -1) {
			//IAE is already thrown...
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
			throw_IllegalArgumentException(env, "setParams Wrong databits");
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
				throw_IllegalArgumentException(env,
						"setParams setStopBits to 1.5: only for 5 dataBits 1.5 stoppbits are supported");
				return -1;
			}
			break;
		case SPSW_STOP_BITS_2:
			if (dcb->ByteSize == 5) {
				throw_IllegalArgumentException(env,
						"setParams setStopBits to 2: 5 dataBits only 1.5 stoppbits are supported");
				return -1;
			} else {
				dcb->StopBits = TWOSTOPBITS;
			}
			break;
		default:
			throw_IllegalArgumentException(env, "setParams Unknown stopbits");
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

	HANDLE hFile = (HANDLE)(uintptr_t)(*env)->GetLongField(env, sps, spsw_fd);

	if (!SetCommState(hFile, dcb)) {
		switch (GetLastError()) {
		case ERROR_INVALID_PARAMETER:
			throw_IllegalArgumentException(env,
					"setParams: Wrong FlowControl\n GetLastError() == ERROR_INVALID_PARAMETER");
			break;
		case ERROR_GEN_FAILURE:
			throw_IllegalArgumentException(env,
					"setParams: Wrong FlowControl\n GetLastError() == ERROR_GEN_FAILURE");
			break;
		default:
			throw_ClosedOrNativeException(env, sps, "setParams SetCommState");
		}
		return -1;
	}

	jint paramsRead =
	SPSW_SPEED_MASK & paramBitSet ? SPSW_SPEED_MASK : 0;
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
		if ((paramsRead & SPSW_SPEED_MASK)
				!= (paramBitSet & SPSW_SPEED_MASK)) {
			snprintf(buf, sizeof(buf),
					"Could not set Speed! NATIVE: paramsRead(0x%08x) != paramBitSet(0x%08x)",
					paramsRead, paramBitSet);
		} else if ((paramsRead & SPSW_DATA_BITS_MASK)
				!= (paramBitSet & SPSW_DATA_BITS_MASK)) {
			snprintf(buf, sizeof(buf),
					"Could not set DataBits! NATIVE: paramsRead(0x%08x) != paramBitSet(0x%08x)",
					paramsRead, paramBitSet);
		} else if ((paramsRead & SPSW_STOP_BITS_MASK)
				!= (paramBitSet & SPSW_STOP_BITS_MASK)) {
			snprintf(buf, sizeof(buf),
					"Could not set StopBits! NATIVE: paramsRead(0x%08x) != paramBitSet(0x%08x)",
					paramsRead, paramBitSet);
		} else if ((paramsRead & SPSW_PARITY_MASK)
				!= (paramBitSet & SPSW_PARITY_MASK)) {
			snprintf(buf, sizeof(buf),
					"Could not set Parity! NATIVE: paramsRead(0x%08x) != paramBitSet(0x%08x)",
					paramsRead, paramBitSet);
		} else if ((paramsRead & SPSW_FLOW_CONTROL_MASK)
				!= (paramBitSet & SPSW_FLOW_CONTROL_MASK)) {
			snprintf(buf, sizeof(buf),
					"Could not set FlowControl! NATIVE: paramsRead(0x%08x) != paramBitSet(0x%08x)",
					paramsRead, paramBitSet);
		} else {
			snprintf(buf, sizeof(buf),
					"Could not set unknown parameter! NATIVE: paramsRead(0x%08x) != paramBitSet(0x%08x)",
					paramsRead, paramBitSet);

		}
		throw_IllegalArgumentException(env, buf);
		return -1;
	}
	return 0;
}
/*
 * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
 * Method:    getParameters
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_getParameters(
		JNIEnv *env, jobject sps, jint parameterBitSetMask) {
	if (getParams(env, sps, &parameterBitSetMask)) {
		return 0;
	}
	return parameterBitSetMask;
}

/*
 * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
 * Method:    getXOFFChar
 * Signature: ()C
 */
JNIEXPORT jchar JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_getXOFFChar(
		JNIEnv *env, jobject sps) {
	DCB dcb;
        dcb.DCBlength = sizeof(DCB);
        
	HANDLE hFile = (HANDLE)(uintptr_t)(*env)->GetLongField(env, sps, spsw_fd);

	if (!GetCommState(hFile, &dcb)) {
		throw_ClosedOrNativeException(env, sps, "getXOFFChar GetCommState");
		return 0;
	}
	return dcb.XoffChar;
}

/*
 * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
 * Method:    getXONChar
 * Signature: ()C
 */
JNIEXPORT jchar JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_getXONChar(
		JNIEnv *env, jobject sps) {
	DCB dcb;
        dcb.DCBlength = sizeof(DCB);

	HANDLE hFile = (HANDLE)(uintptr_t)(*env)->GetLongField(env, sps, spsw_fd);

	if (!GetCommState(hFile, &dcb)) {
		throw_ClosedOrNativeException(env, sps, "getXONChar GetCommState");
		return 0;
	}
	return dcb.XonChar;
}

/*
 * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
 * Method:    setBreak
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_setBreak
(JNIEnv *env, jobject sps, jboolean enabled) {
	DWORD dwFunc = (enabled == JNI_TRUE) ? SETBREAK : CLRBREAK;

	HANDLE hFile = (HANDLE)(uintptr_t)(*env)->GetLongField(env, sps, spsw_fd);

	if (!EscapeCommFunction(hFile, dwFunc)) {
		if (GetLastError() == ERROR_INVALID_PARAMETER) {
			throw_IllegalArgumentException(env, "setBreak: Wrong value");
		} else {
			throw_ClosedOrNativeException(env, sps, "Can't set/clear BREAK");
		}
	}
}

/*
 * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
 * Method:    setDTR
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_setDTR
(JNIEnv *env, jobject sps, jboolean enabled) {
	DWORD dwFunc = (enabled == JNI_TRUE) ? SETDTR : CLRDTR;

	HANDLE hFile = (HANDLE)(uintptr_t)(*env)->GetLongField(env, sps, spsw_fd);

	if (!EscapeCommFunction(hFile, dwFunc)) {
		if (GetLastError() == ERROR_INVALID_PARAMETER) {
			throw_IllegalArgumentException(env, "setDTR: Wrong value");
		} else {
			throw_ClosedOrNativeException(env, sps, "Can't set/clear DTR");
		}
	}
}

/*
 * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
 * Method:    setParameters
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_setParameters(
		JNIEnv *env, jobject sps, jint parameterBitSet) {

	HANDLE hFile = (HANDLE)(uintptr_t)(*env)->GetLongField(env, sps, spsw_fd);

	DCB dcb;
        dcb.DCBlength = sizeof(DCB);

	if (!GetCommState(hFile, &dcb)) {
		throw_ClosedOrNativeException(env, sps, "setXOFFChar GetCommState");
		return;
	}

	setParams(env, sps, &dcb, parameterBitSet);
}

/*
 * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
 * Method:    setRTS
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_setRTS
(JNIEnv *env, jobject sps, jboolean enabled) {
	DWORD dwFunc = (enabled == JNI_TRUE) ? SETRTS : CLRRTS;

	HANDLE hFile = (HANDLE)(uintptr_t)(*env)->GetLongField(env, sps, spsw_fd);

	if (!EscapeCommFunction(hFile, dwFunc)) {
		if (GetLastError() == ERROR_INVALID_PARAMETER) {
			throw_IllegalArgumentException(env, "setRTS: Wrong value");
		} else {
			throw_ClosedOrNativeException(env, sps, "Can't set/clear RTS");
		}
	}
}

/*
 * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
 * Method:    setXOFFChar
 * Signature: (C)V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_setXOFFChar
(JNIEnv *env, jobject sps, jchar c) {
	DCB dcb;
        dcb.DCBlength = sizeof(DCB);

	HANDLE hFile = (HANDLE)(uintptr_t)(*env)->GetLongField(env, sps, spsw_fd);

	if (!GetCommState(hFile, &dcb)) {
		throw_ClosedOrNativeException(env, sps, "setXOFFChar GetCommState");
		return;
	}

	dcb.XoffChar = c;

	if (!SetCommState(hFile, &dcb)) {
		switch (GetLastError()) {
			case ERROR_INVALID_PARAMETER:
			throw_IllegalArgumentException(env, "etXOFFChar: Wrong value\n GetLastError() == ERROR_INVALID_PARAMETER");
			break;
			case ERROR_GEN_FAILURE:
			throw_IllegalArgumentException(env, "etXOFFChar: Wrong value\n GetLastError() == ERROR_GEN_FAILURE");
			break;
			default:
			throw_ClosedOrNativeException(env, sps, "setXOFFChar SetCommState");
		}
	}
}

/*
 * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
 * Method:    setXONChar
 * Signature: (C)V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_setXONChar
(JNIEnv *env, jobject sps, jchar c) {
	DCB dcb;
        dcb.DCBlength = sizeof(DCB);

	HANDLE hFile = (HANDLE)(uintptr_t)(*env)->GetLongField(env, sps, spsw_fd);

	if (!GetCommState(hFile, &dcb)) {
		throw_ClosedOrNativeException(env, sps, "setXONChar GetCommState");
		return;
	}

	dcb.XonChar = c;

	if (!SetCommState(hFile, &dcb)) {
		switch (GetLastError()) {
			case ERROR_INVALID_PARAMETER:
			throw_IllegalArgumentException(env, "setXONChar: Wrong value\n GetLastError() == ERROR_INVALID_PARAMETER");
			break;
			case ERROR_GEN_FAILURE:
			throw_IllegalArgumentException(env, "setXONChar: Wrong value\n GetLastError() == ERROR_GEN_FAILURE");
			break;
			default:
			throw_ClosedOrNativeException(env, sps, "setXONChar SetCommState");
		}
	}
}

/*
 * Class:     de_ibapl_spsw_jniprovider_GenericWinSerialPortSocket
 * Method:    getInterByteReadTimeout
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_jniprovider_GenericWinSerialPortSocket_getInterByteReadTimeout(
		JNIEnv *env, jobject sps) {

	HANDLE hFile = (HANDLE)(uintptr_t)(*env)->GetLongField(env, sps, spsw_fd);

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
 * Class:     de_ibapl_spsw_jniprovider_GenericWinSerialPortSocket
 * Method:    getOverallReadTimeout
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_jniprovider_GenericWinSerialPortSocket_getOverallReadTimeout(
		JNIEnv *env, jobject sps) {

	HANDLE hFile = (HANDLE)(uintptr_t)(*env)->GetLongField(env, sps, spsw_fd);

	COMMTIMEOUTS lpCommTimeouts;
	if (!GetCommTimeouts(hFile, &lpCommTimeouts)) {
		throw_ClosedOrNativeException(env, sps, "getOverallReadTimeout");
		return -1;
	}
	return lpCommTimeouts.ReadTotalTimeoutConstant;
}

/*
 * Class:     de_ibapl_spsw_jniprovider_GenericWinSerialPortSocket
 * Method:    getOverallWriteTimeout
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_de_ibapl_spsw_jniprovider_GenericWinSerialPortSocket_getOverallWriteTimeout(
		JNIEnv *env, jobject sps) {

	HANDLE hFile = (HANDLE)(uintptr_t)(*env)->GetLongField(env, sps, spsw_fd);

	COMMTIMEOUTS lpCommTimeouts;
	if (!GetCommTimeouts(hFile, &lpCommTimeouts)) {
		throw_ClosedOrNativeException(env, sps, "getOverallWriteTimeout");
		return -1;
	}
	return lpCommTimeouts.WriteTotalTimeoutConstant;
}
/*
 * Class:     de_ibapl_spsw_jniprovider_GenericWinSerialPortSocket
 * Method:    setTimeouts
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_de_ibapl_spsw_jniprovider_GenericWinSerialPortSocket_setTimeouts
(JNIEnv *env, jobject sps, jint interByteReadTimeout, jint overallReadTimeout, jint overallWriteTimeout) {

	HANDLE hFile = (HANDLE)(uintptr_t)(*env)->GetLongField(env, sps, spsw_fd);

	COMMTIMEOUTS lpCommTimeouts;
	if (!GetCommTimeouts(hFile, &lpCommTimeouts)) {
		throw_ClosedOrNativeException(env, sps, "setTimeouts");
		return;
	}

	if (overallWriteTimeout < 0) {
		throw_IllegalArgumentException(env, "setTimeouts: overallWriteTimeout must >= 0");
		return;
	} else if (overallWriteTimeout == MAXDWORD) {
		//MAXDWORD has a special meaning...
		overallWriteTimeout = MAXDWORD - 1;
	}

	if (overallReadTimeout < 0) {
		throw_IllegalArgumentException(env, "setTimeouts: overallReadTimeout must >= 0");
		return;
	} else if (overallReadTimeout == MAXDWORD) {
		//MAXDWORD has a special meaning...
		overallReadTimeout = MAXDWORD - 1;
	}

	if (interByteReadTimeout < 0) {
		throw_IllegalArgumentException(env, "setReadTimeouts: interByteReadTimeout must >= 0");
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
			throw_IllegalArgumentException(env, "setTimeouts: Wrong Timeouts");
		} else {
			throw_ClosedOrNativeException(env, sps, "setTimeouts SetCommTimeouts");
		}
		return;
	}
}

#ifdef __cplusplus
}
#endif