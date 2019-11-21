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
#include "spsw-jni.h"

#include <termios.h>
#include <stdint.h>
#include <sys/ioctl.h>
#include <errno.h>
#include "de_ibapl_spsw_jniprovider_GenericTermiosSerialPortSocket.h"

#ifdef __cplusplus
extern "C" {
#endif

    static jint speed_t2Speed(JNIEnv *env, speed_t speed) {
        switch (speed) {
            case B0:
                return SPSW_SPEED_0_BPS;
            case B50:
                return SPSW_SPEED_50_BPS;
            case B75:
                return SPSW_SPEED_75_BPS;
            case B110:
                return SPSW_SPEED_110_BPS;
            case B134:
                return SPSW_SPEED_134_BPS;
            case B150:
                return SPSW_SPEED_150_BPS;
            case B200:
                return SPSW_SPEED_200_BPS;
            case B300:
                return SPSW_SPEED_300_BPS;
            case B600:
                return SPSW_SPEED_600_BPS;
            case B1200:
                return SPSW_SPEED_1200_BPS;
            case B1800:
                return SPSW_SPEED_1800_BPS;
            case B2400:
                return SPSW_SPEED_2400_BPS;
            case B4800:
                return SPSW_SPEED_4800_BPS;
            case B9600:
                return SPSW_SPEED_9600_BPS;
            case B19200:
                return SPSW_SPEED_19200_BPS;
            case B38400:
                return SPSW_SPEED_38400_BPS;
#ifdef B57600
            case B57600:
                return SPSW_SPEED_57600_BPS;
#endif
#ifdef B115200
            case B115200:
                return SPSW_SPEED_115200_BPS;
#endif
#ifdef B230400
            case B230400:
                return SPSW_SPEED_230400_BPS;
#endif
#ifdef B460800
            case B460800:
                return SPSW_SPEED_460800_BPS;
#endif
#ifdef B500000
            case B500000:
                return SPSW_SPEED_500000_BPS;
#endif
#ifdef B576000
            case B576000:
                return SPSW_SPEED_576000_BPS;
#endif
#ifdef B921600
            case B921600:
                return SPSW_SPEED_921600_BPS;
#endif
#ifdef B1000000
            case B1000000:
                return SPSW_SPEED_1000000_BPS;
#endif
#ifdef B1152000
            case B1152000:
                return SPSW_SPEED_1152000_BPS;
#endif
#ifdef B1500000
            case B1500000:
                return SPSW_SPEED_1500000_BPS;
#endif
#ifdef B2000000
            case B2000000:
                return SPSW_SPEED_2000000_BPS;
#endif
#ifdef B2500000
            case B2500000:
                return SPSW_SPEED_2500000_BPS;
#endif
#ifdef B3000000
            case B3000000:
                return SPSW_SPEED_3000000_BPS;
#endif
#ifdef B3500000
            case B3500000:
                return SPSW_SPEED_3500000_BPS;
#endif
#ifdef B4000000
            case B4000000:
                return SPSW_SPEED_4000000_BPS;
#endif
            default:
                throw_IllegalArgumentException(env, "speed not supported");
                return -1;
        }
    }

    static jint speed2speed_t(JNIEnv *env, jint speed, speed_t *speedValue) {
        switch (speed) {
            case SPSW_SPEED_0_BPS:
                *speedValue = B0;
                break;
            case SPSW_SPEED_50_BPS:
                *speedValue = B50;
                break;
            case SPSW_SPEED_75_BPS:
                *speedValue = B75;
                break;
            case SPSW_SPEED_110_BPS:
                *speedValue = B110;
                break;
            case SPSW_SPEED_134_BPS:
                *speedValue = B134;
                break;
            case SPSW_SPEED_150_BPS:
                *speedValue = B150;
                break;
            case SPSW_SPEED_200_BPS:
                *speedValue = B200;
                break;
            case SPSW_SPEED_300_BPS:
                *speedValue = B300;
                break;
            case SPSW_SPEED_600_BPS:
                *speedValue = B600;
                break;
            case SPSW_SPEED_1200_BPS:
                *speedValue = B1200;
                break;
            case SPSW_SPEED_1800_BPS:
                *speedValue = B1800;
                break;
            case SPSW_SPEED_2400_BPS:
                *speedValue = B2400;
                break;
            case SPSW_SPEED_4800_BPS:
                *speedValue = B4800;
                break;
            case SPSW_SPEED_9600_BPS:
                *speedValue = B9600;
                break;
            case SPSW_SPEED_19200_BPS:
                *speedValue = B19200;
                break;
            case SPSW_SPEED_38400_BPS:
                *speedValue = B38400;
                break;
#ifdef B57600
            case SPSW_SPEED_57600_BPS:
                *speedValue = B57600;
                break;
#endif
#ifdef B115200
            case SPSW_SPEED_115200_BPS:
                *speedValue = B115200;
                break;
#endif
#ifdef B230400
            case SPSW_SPEED_230400_BPS:
                *speedValue = B230400;
                break;
#endif
#ifdef B460800
            case SPSW_SPEED_460800_BPS:
                *speedValue = B460800;
                break;
#endif
#ifdef B500000
            case SPSW_SPEED_500000_BPS:
                *speedValue = B500000;
                break;
#endif
#ifdef B576000
            case SPSW_SPEED_576000_BPS:
                *speedValue = B576000;
                break;
#endif
#ifdef B921600
            case SPSW_SPEED_921600_BPS:
                *speedValue = B921600;
                break;
#endif
#ifdef B1000000
            case SPSW_SPEED_1000000_BPS:
                *speedValue = B1000000;
                break;
#endif
#ifdef B1152000
            case SPSW_SPEED_1152000_BPS:
                *speedValue = B1152000;
                break;
#endif
#ifdef B1500000
            case SPSW_SPEED_1500000_BPS:
                *speedValue = B1500000;
                break;
#endif
#ifdef B2000000
            case SPSW_SPEED_2000000_BPS:
                *speedValue = B2000000;
                break;
#endif
#ifdef B2500000
            case SPSW_SPEED_2500000_BPS:
                *speedValue = B2500000;
                break;
#endif
#ifdef B3000000
            case SPSW_SPEED_3000000_BPS:
                *speedValue = B3000000;
                break;
#endif
#ifdef B3500000
            case SPSW_SPEED_3500000_BPS:
                *speedValue = B3500000;
                break;
#endif
#ifdef B4000000
            case SPSW_SPEED_4000000_BPS:
                *speedValue = B4000000;
                break;
#endif
            default:
                throw_IllegalArgumentException(env, "Speed not supported");
                return -1;
        }
        return 0;
    }

    static int getParams(JNIEnv *env, jobject sps, jint* paramBitSet) {
        int fd = (*env)->GetIntField(env, sps, spsw_fd);
        struct termios settings;
        if (tcgetattr(fd, &settings)) {
            throw_ClosedOrNativeException(env, sps, "setParams tcsetattr");
            return -1;
        }

        jint result = 0;
        //Speed
        if (*paramBitSet & SPSW_SPEED_MASK) {
            //Get standard speed from "termios.h"
            speed_t inSpeed = cfgetispeed(&settings);
            speed_t outSpeed = cfgetospeed(&settings);
            if (inSpeed != outSpeed) {
                throw_IOException_NativeError(env,
                        "In and out speed mismatch");
                return -1;
            }
            jint speed = speed_t2Speed(env, inSpeed);
            if (speed == -1) {
                //IAE is already thrown...
                return -1;
            } else {
                result |= speed;
            }

        }

        //DataBits
        if (*paramBitSet & SPSW_DATA_BITS_MASK) {
            switch (settings.c_cflag & CSIZE) {
                case CS5:
                    result |= SPSW_DATA_BITS_DB5;
                    break;
                case CS6:
                    result |= SPSW_DATA_BITS_DB6;
                    break;
                case CS7:
                    result |= SPSW_DATA_BITS_DB7;
                    break;
                case CS8:
                    result |= SPSW_DATA_BITS_DB8;
                    break;
                default:
                    throw_IllegalArgumentException(env, "Unknown databits");
                    return -1;
            }
        }

        //StopBits
        if (*paramBitSet & SPSW_STOP_BITS_MASK) {
            if ((settings.c_cflag & CSTOPB) == 0) {
                result |= SPSW_STOP_BITS_1;
            } else if ((settings.c_cflag & CSTOPB) == CSTOPB) {
                if ((settings.c_cflag & CSIZE) == CS5) {
                    result |= SPSW_STOP_BITS_1_5;
                } else {
                    result |= SPSW_STOP_BITS_2;
                }
            }
        }

        //Parity
        if (*paramBitSet & SPSW_PARITY_MASK) {
            if ((settings.c_cflag & PARENB) == 0) {
                result |= SPSW_PARITY_NONE;

            } else if ((settings.c_cflag & PARODD) == 0) {
                // EVEN or SPACE
#ifdef PAREXT
                if ((settings.c_cflag & PAREXT) == 0) {
                    result |= SPSW_PARITY_EVEN;
                } else {
                    result |= SPSW_PARITY_SPACE;
                }
#elif defined CMSPAR
                if ((settings.c_cflag & CMSPAR) == 0) {
                    result |= SPSW_PARITY_EVEN;
                } else {
                    result |= SPSW_PARITY_SPACE;
                }
#else
                result |= SPSW_PARITY_EVEN;
#endif
            } else {
                // ODD or MARK
#ifdef PAREXT
                if ((settings.c_cflag & PAREXT) == 0) {
                    result |= SPSW_PARITY_ODD;
                } else {
                    result |= SPSW_PARITY_MARK;
                }
#elif defined CMSPAR
                if ((settings.c_cflag & CMSPAR) == 0) {
                    result |= SPSW_PARITY_ODD;
                } else {
                    result |= SPSW_PARITY_MARK;
                }
#else
                result |= SPSW_PARITY_ODD;
#endif
            }
            if (!(result & SPSW_PARITY_MASK)) {
                //Parity not found
                throw_IOException_NativeError(env,
                        "getParam could not figure out Parity");
                return -1;
            }
        }

        //FlowControl
        if (*paramBitSet & SPSW_FLOW_CONTROL_MASK) {
            result |= SPSW_FLOW_CONTROL_NONE;
            if (settings.c_cflag & CRTSCTS) {
                result |= (SPSW_FLOW_CONTROL_RTS_CTS_IN
                        | SPSW_FLOW_CONTROL_RTS_CTS_OUT);
                result &= ~SPSW_FLOW_CONTROL_NONE; //clean NONE
            }
            if (settings.c_iflag & IXOFF) {
                result |= SPSW_FLOW_CONTROL_XON_XOFF_IN;
                result &= ~SPSW_FLOW_CONTROL_NONE; //clean NONE
            }
            if (settings.c_iflag & IXON) {
                result |= SPSW_FLOW_CONTROL_XON_XOFF_OUT;
                result &= ~SPSW_FLOW_CONTROL_NONE; //clean NONE
            }
        }
        *paramBitSet = result;
        return 0;
    }

    int setParams(JNIEnv *env, jobject sps, struct termios *settings, jint paramBitSet) {

        //Speed
        if (paramBitSet & SPSW_SPEED_MASK) {
            speed_t speedValue;
            if (speed2speed_t(env, paramBitSet & SPSW_SPEED_MASK, &speedValue) == -1) {
                //IAE is already thrown...
                return -1;
            }

            //Set standard speed from "termios.h"
            if (cfsetspeed(settings, speedValue) < 0) {
                throw_ClosedOrNativeException(env, sps,
                        "Can't set Speed cfsetspeed(settings, speedValue)");
                return -1;
            }

        }

        //DataBits
        if (paramBitSet & SPSW_DATA_BITS_MASK) {
            settings->c_cflag &= (tcflag_t)~CSIZE;
            switch (paramBitSet & SPSW_DATA_BITS_MASK) {
                case SPSW_DATA_BITS_DB5:
                    settings->c_cflag |= CS5;
                    break;
                case SPSW_DATA_BITS_DB6:
                    settings->c_cflag |= CS6;
                    break;
                case SPSW_DATA_BITS_DB7:
                    settings->c_cflag |= CS7;
                    break;
                case SPSW_DATA_BITS_DB8:
                    settings->c_cflag |= CS8;
                    break;
                default:
                    throw_IllegalArgumentException(env, "Wrong databits");
                    return -1;
            }
        }

        //StopBits
        if (paramBitSet & SPSW_STOP_BITS_MASK) {
            switch (paramBitSet & SPSW_STOP_BITS_MASK) {
                case SPSW_STOP_BITS_1:
                    //1 stop bit (for info see ->> MSDN)
                    settings->c_cflag &= (tcflag_t)~CSTOPB;
                    break;
                case SPSW_STOP_BITS_1_5:
                    if ((settings->c_cflag & CSIZE) == CS5) {
                        settings->c_cflag |= CSTOPB;
                    } else {
                        throw_IllegalArgumentException(env,
                                "setStopBits 1.5 stop bits are only valid for 5 DataBits");
                        return -1;
                    }
                    break;
                case SPSW_STOP_BITS_2:
                    if ((settings->c_cflag & CSIZE) == CS5) {
                        throw_IllegalArgumentException(env,
                                "setStopBits 2 stop bits are only valid for 6,7,8 DataBits");
                        return -1;
                    } else {
                        settings->c_cflag |= CSTOPB;
                    }
                    break;
                default:
                    throw_IllegalArgumentException(env, "Unknown stopbits");
                    return -1;
            }
        }

        //Parity
        if (paramBitSet & SPSW_PARITY_MASK) {
#ifdef PAREXT
            settings->c_cflag &= (tcflag_t)~(PARENB | PARODD | PAREXT); //Clear parity settings
#elif defined CMSPAR
            settings->c_cflag &= (tcflag_t)~(PARENB | PARODD | CMSPAR); //Clear parity settings
#else
            settings->c_cflag &= (tcflag_t)~(PARENB | PARODD); //Clear parity settings
#endif
            switch (paramBitSet & SPSW_PARITY_MASK) {
                case SPSW_PARITY_NONE:
                    //Parity NONE
                    settings->c_iflag &= (tcflag_t)~INPCK; // switch parity input checking off
                    break;
                case SPSW_PARITY_ODD:
                    //Parity ODD
                    settings->c_cflag |= (PARENB | PARODD);
                    settings->c_iflag |= INPCK; // switch parity input checking On
                    break;
                case SPSW_PARITY_EVEN:
                    //Parity EVEN
                    settings->c_cflag |= PARENB;
                    settings->c_iflag |= INPCK;
                    break;
                case SPSW_PARITY_MARK:
                    //Parity MARK
                    settings->c_iflag |= INPCK;
#ifdef PAREXT
                    settings->c_cflag |= (PARENB | PARODD | PAREXT);
#elif defined CMSPAR
                    settings->c_cflag |= (PARENB | PARODD | CMSPAR);
#else
                    //We can't set mark parity getParams will fail therefore
                    settings->c_cflag |= (PARENB | PARODD);
#endif
                    break;
                case SPSW_PARITY_SPACE:
                    //Parity SPACE
                    settings->c_iflag |= INPCK;
#ifdef PAREXT
                    settings->c_cflag |= (PARENB | PAREXT);
#elif defined CMSPAR
                    settings->c_cflag |= (PARENB | CMSPAR);
#else
                    //We can't set space parity getParams will fail therefore
                    settings->c_cflag |= (PARENB);
#endif
                    break;
                default:
                    throw_IllegalArgumentException(env, "Wrong parity");
                    return -1;
            }
        }

        //FlowControl
        if (paramBitSet & SPSW_FLOW_CONTROL_MASK) {
            jint mask = paramBitSet & SPSW_FLOW_CONTROL_MASK;
            settings->c_cflag &= (tcflag_t)~CRTSCTS;
            settings->c_iflag &= (tcflag_t)~(IXON | IXOFF);
            if (mask != SPSW_FLOW_CONTROL_NONE) {
                if (((mask & SPSW_FLOW_CONTROL_RTS_CTS_IN)
                        == SPSW_FLOW_CONTROL_RTS_CTS_IN)
                        || ((mask & SPSW_FLOW_CONTROL_RTS_CTS_OUT)
                        == SPSW_FLOW_CONTROL_RTS_CTS_OUT)) {
                    if (((mask & SPSW_FLOW_CONTROL_RTS_CTS_IN)
                            == SPSW_FLOW_CONTROL_RTS_CTS_IN)
                            && ((mask & SPSW_FLOW_CONTROL_RTS_CTS_OUT)
                            == SPSW_FLOW_CONTROL_RTS_CTS_OUT)) {
                        settings->c_cflag |= CRTSCTS;
                    } else {
                        throw_IllegalArgumentException(env, "Can only set RTS/CTS for both in and out");
                        return -1;
                    }
                }
                if ((mask & SPSW_FLOW_CONTROL_XON_XOFF_IN)
                        == SPSW_FLOW_CONTROL_XON_XOFF_IN) {
                    settings->c_iflag |= IXOFF;
                }
                if ((mask & SPSW_FLOW_CONTROL_XON_XOFF_OUT)
                        == SPSW_FLOW_CONTROL_XON_XOFF_OUT) {
                    settings->c_iflag |= IXON;
                }
            }
        }

        int fd = (*env)->GetIntField(env, sps, spsw_fd);
        //Write the changes...
        if (tcsetattr(fd, TCSANOW, settings)) {
            if (errno == ERANGE) {
                throw_IllegalArgumentException(env, "setParams tcsetattr Native Error \"ERANGE\"");
            } else {
                throw_ClosedOrNativeException(env, sps, "setParams tcsetattr");
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
                snprintf(buf, sizeof (buf),
                        "Could not set Speed! NATIVE: paramsRead(0x%08x) != paramBitSet(0x%08x)",
                        paramsRead, paramBitSet);
            } else if ((paramsRead & SPSW_DATA_BITS_MASK)
                    != (paramBitSet & SPSW_DATA_BITS_MASK)) {
                snprintf(buf, sizeof (buf),
                        "Could not set DataBits! NATIVE: paramsRead(0x%08x) != paramBitSet(0x%08x)",
                        paramsRead, paramBitSet);
            } else if ((paramsRead & SPSW_STOP_BITS_MASK)
                    != (paramBitSet & SPSW_STOP_BITS_MASK)) {
                snprintf(buf, sizeof (buf),
                        "Could not set StopBits! NATIVE: paramsRead(0x%08x) != paramBitSet(0x%08x)",
                        paramsRead, paramBitSet);
            } else if ((paramsRead & SPSW_PARITY_MASK)
                    != (paramBitSet & SPSW_PARITY_MASK)) {
                snprintf(buf, sizeof (buf),
                        "Could not set Parity! NATIVE: paramsRead(0x%08x) != paramBitSet(0x%08x)",
                        paramsRead, paramBitSet);
            } else if ((paramsRead & SPSW_FLOW_CONTROL_MASK)
                    != (paramBitSet & SPSW_FLOW_CONTROL_MASK)) {
                snprintf(buf, sizeof (buf),
                        "Could not set FlowControl! NATIVE: paramsRead(0x%08x) != paramBitSet(0x%08x)",
                        paramsRead, paramBitSet);
            } else {
                snprintf(buf, sizeof (buf),
                        "Could not set unknown parameter! NATIVE: paramsRead(0x%08x) != paramBitSet(0x%08x)",
                        paramsRead, paramBitSet);

            }
            throw_IllegalArgumentException(env, buf);
            return -1;
        }
        return 0;
    }

    static jboolean getLineStatus(JNIEnv *env, jobject sps, int bitMask) {
        int fd = (*env)->GetIntField(env, sps, spsw_fd);
        int lineStatus;
        if (ioctl(fd, TIOCMGET, &lineStatus)) {
            throw_ClosedOrNativeException(env, sps, "Can't get line status");
            return JNI_FALSE;
        }
        if ((lineStatus & bitMask) == bitMask) {
            return JNI_TRUE;
        } else {
            return JNI_FALSE;
        }
    }

    static void setLineStatus(JNIEnv *env, jobject sps, jboolean enabled,
            int bitMask) {
        int fd = (*env)->GetIntField(env, sps, spsw_fd);
        int lineStatus;
        if (ioctl(fd, TIOCMGET, &lineStatus)) {
            throw_ClosedOrNativeException(env, sps, "Can't get line status");
            return;
        }
        if (enabled == JNI_TRUE) {
            lineStatus |= bitMask;
        } else {
            lineStatus &= ~bitMask;
        }
        if (ioctl(fd, TIOCMSET, &lineStatus)) {
            throw_ClosedOrNativeException(env, sps, "Can't set line status");
        }
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
        const int fd = (*env)->GetIntField(env, sps, spsw_fd);

        struct termios settings;
        if (tcgetattr(fd, &settings)) {
            throw_ClosedOrNativeException(env, sps, "getXOFFChar tcgetattr");
            return 0;
        }
        return settings.c_cc[VSTOP];

    }

    /*
     * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
     * Method:    getXONChar
     * Signature: ()C
     */
    JNIEXPORT jchar JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_getXONChar(
            JNIEnv *env, jobject sps) {
        const int fd = (*env)->GetIntField(env, sps, spsw_fd);

        struct termios settings;
        if (tcgetattr(fd, &settings)) {
            throw_ClosedOrNativeException(env, sps, "getXONChar tcgetattr");
            return 0;
        }
        return settings.c_cc[VSTART];

    }

    /*
     * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
     * Method:    isCTS
     * Signature: ()Z
     */
    JNIEXPORT jboolean JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_isCTS(
            JNIEnv *env, jobject sps) {
        return getLineStatus(env, sps, TIOCM_CTS);
    }

    /*
     * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
     * Method:    isDSR
     * Signature: ()Z
     */
    JNIEXPORT jboolean JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_isDSR
    (JNIEnv *env, jobject sps) {
        return getLineStatus(env, sps, TIOCM_DSR);
    }

    /*
     * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
     * Method:    isDCD
     * Signature: ()Z
     */
    JNIEXPORT jboolean JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_isDCD
    (JNIEnv *env, jobject sps) {
        return getLineStatus(env, sps, TIOCM_CAR);
    }

    /*
     * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
     * Method:    isRI
     * Signature: ()Z
     */
    JNIEXPORT jboolean JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_isRI
    (JNIEnv *env, jobject sps) {
        return getLineStatus(env, sps, TIOCM_RNG);
    }

    /*
     * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
     * Method:    setBreak0
     * Signature: (Z)V
     */
    JNIEXPORT void JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_setBreak0
    (JNIEnv *env, jobject sps, jboolean enabled) {
        const int fd = (*env)->GetIntField(env, sps, spsw_fd);
        unsigned long int arg;
        if (enabled == JNI_TRUE) {
            arg = TIOCSBRK;
        } else {
            arg = TIOCCBRK;
        }
        if (ioctl(fd, arg)) {
            throw_ClosedOrNativeException(env, sps, "Can't set Break");
        }
    }

    /*
     * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
     * Method:    setDTR
     * Signature: (Z)V
     */
    JNIEXPORT void JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_setDTR
    (JNIEnv *env, jobject sps, jboolean enabled) {
        setLineStatus(env, sps, enabled, TIOCM_DTR);
    }

    /*
     * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
     * Method:    setParameters
     * Signature: (I)V
     */
    JNIEXPORT void JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_setParameters(
            JNIEnv *env, jobject sps, jint parameterBitSet) {
        const int fd = (*env)->GetIntField(env, sps, spsw_fd);

        struct termios settings;
        if (tcgetattr(fd, &settings)) {
            throw_ClosedOrNativeException(env, sps, "setParameters tcgetattr");
            return;
        }

        setParams(env, sps, &settings, parameterBitSet);
    }

    /*
     * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
     * Method:    setRTS
     * Signature: (Z)V
     */
    JNIEXPORT void JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_setRTS
    (JNIEnv *env, jobject sps, jboolean enabled) {
        setLineStatus(env, sps, enabled, TIOCM_RTS);
    }

    /*
     * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
     * Method:    setXOFFChar
     * Signature: (C)V
     */
    JNIEXPORT void JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_setXOFFChar
    (JNIEnv *env, jobject sps, jchar c) {
        const int fd = (*env)->GetIntField(env, sps, spsw_fd);

        struct termios settings;
        if (tcgetattr(fd, &settings)) {
            throw_ClosedOrNativeException(env, sps, "setXOFFChar tcgetattr");
            return;
        }
        settings.c_cc[VSTOP] = (uint8_t)c;

        if (tcsetattr(fd, TCSANOW, &settings)) {
            throw_ClosedOrNativeException(env, sps, "setXOFFChar tcsetattr");
        }

    }

    /*
     * Class:     de_ibapl_spsw_jniprovider_AbstractSerialPortSocket
     * Method:    setXONChar
     * Signature: (C)V
     */
    JNIEXPORT void JNICALL Java_de_ibapl_spsw_jniprovider_AbstractSerialPortSocket_setXONChar
    (JNIEnv *env, jobject sps, jchar c) {
        const int fd = (*env)->GetIntField(env, sps, spsw_fd);

        struct termios settings;
        if (tcgetattr(fd, &settings)) {
            throw_ClosedOrNativeException(env, sps, "setXONChar tcgetattr");
            return;
        }
        settings.c_cc[VSTART] = (uint8_t)c;

        if (tcsetattr(fd, TCSANOW, &settings)) {
            //TODO EBADF == errno
            throw_ClosedOrNativeException(env, sps, "setXONChar tcsetattr");
        }

    }

    /*
     * Class:     de_ibapl_spsw_jniprovider_GenericTermiosSerialPortSocket
     * Method:    isDTR
     * Signature: ()Z
     */
    JNIEXPORT jboolean JNICALL Java_de_ibapl_spsw_jniprovider_GenericTermiosSerialPortSocket_isDTR(
            JNIEnv *env, jobject sps) {
        return getLineStatus(env, sps, TIOCM_DTR);
    }

    /*
     * Class:     de_ibapl_spsw_jniprovider_GenericTermiosSerialPortSocket
     * Method:    isRTS
     * Signature: ()Z
     */
    JNIEXPORT jboolean JNICALL Java_de_ibapl_spsw_jniprovider_GenericTermiosSerialPortSocket_isRTS(
            JNIEnv *env, jobject sps) {
        return getLineStatus(env, sps, TIOCM_RTS);
    }

#ifdef __cplusplus
}
#endif
