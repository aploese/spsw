package de.ibapl.spsw.jnhwprovider;

import de.ibapl.jnhw.IntRef;
import de.ibapl.jnhw.NativeErrorException;
import de.ibapl.jnhw.winapi.Fileapi;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import static de.ibapl.spsw.api.SerialPortSocket.PORT_IS_OPEN;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.api.TimeoutIOException;
import java.io.InterruptedIOException;
import java.util.EnumSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.ibapl.jnhw.winapi.Winnt.KEY_READ;
import static de.ibapl.jnhw.winapi.Winnt.MAXDWORD;
import static de.ibapl.jnhw.winapi.Winnt.GENERIC_READ;
import static de.ibapl.jnhw.winapi.Winnt.GENERIC_WRITE;
import static de.ibapl.jnhw.winapi.Winnt.HANDLE;

import static de.ibapl.jnhw.winapi.Winreg.HKEY_LOCAL_MACHINE;
import static de.ibapl.jnhw.winapi.Winreg.RegEnumValueW;
import static de.ibapl.jnhw.winapi.Winreg.RegOpenKeyExW;

import static de.ibapl.jnhw.winapi.Winerror.ERROR_GEN_FAILURE;
import static de.ibapl.jnhw.winapi.Winerror.ERROR_INVALID_PARAMETER;
import static de.ibapl.jnhw.winapi.Winerror.ERROR_ACCESS_DENIED;
import static de.ibapl.jnhw.winapi.Winerror.ERROR_FILE_NOT_FOUND;
import static de.ibapl.jnhw.winapi.Winerror.ERROR_IO_PENDING;

import static de.ibapl.jnhw.winapi.Winbase.NOPARITY;
import static de.ibapl.jnhw.winapi.Winbase.EVENPARITY;
import static de.ibapl.jnhw.winapi.Winbase.MARKPARITY;
import static de.ibapl.jnhw.winapi.Winbase.ODDPARITY;
import static de.ibapl.jnhw.winapi.Winbase.SPACEPARITY;
import static de.ibapl.jnhw.winapi.Winbase.ONESTOPBIT;
import static de.ibapl.jnhw.winapi.Winbase.ONE5STOPBITS;
import static de.ibapl.jnhw.winapi.Winbase.TWOSTOPBITS;
import static de.ibapl.jnhw.winapi.Winbase.DCB;
import static de.ibapl.jnhw.winapi.Winbase.COMSTAT;
import static de.ibapl.jnhw.winapi.Winbase.COMMTIMEOUTS;
import static de.ibapl.jnhw.winapi.Winbase.CloseHandle;
import static de.ibapl.jnhw.winapi.Winbase.GetCommState;
import static de.ibapl.jnhw.winapi.Winbase.SetCommState;
import static de.ibapl.jnhw.winapi.Winbase.RTS_CONTROL_HANDSHAKE;
import static de.ibapl.jnhw.winapi.Winbase.ClearCommError;
import static de.ibapl.jnhw.winapi.Winbase.GetCommTimeouts;
import static de.ibapl.jnhw.winapi.Winbase.SetCommTimeouts;
import static de.ibapl.jnhw.winapi.Winbase.GetCommModemStatus;
import static de.ibapl.jnhw.winapi.Winbase.MS_CTS_ON;
import static de.ibapl.jnhw.winapi.Winbase.MS_DSR_ON;
import static de.ibapl.jnhw.winapi.Winbase.MS_RING_ON;
import static de.ibapl.jnhw.winapi.Winbase.MS_RLSD_ON;
import static de.ibapl.jnhw.winapi.Winbase.FILE_FLAG_OVERLAPPED;
import static de.ibapl.jnhw.winapi.Winbase.ClearCommBreak;
import static de.ibapl.jnhw.winapi.Winbase.SetCommBreak;
import static de.ibapl.jnhw.winapi.Winbase.EscapeCommFunction;
import static de.ibapl.jnhw.winapi.Winbase.SETBREAK;
import static de.ibapl.jnhw.winapi.Winbase.CLRBREAK;
import static de.ibapl.jnhw.winapi.Winbase.SETDTR;
import static de.ibapl.jnhw.winapi.Winbase.CLRDTR;
import static de.ibapl.jnhw.winapi.Winbase.SETRTS;
import static de.ibapl.jnhw.winapi.Winbase.CLRRTS;
import static de.ibapl.jnhw.winapi.Winbase.RTS_CONTROL_DISABLE;
import static de.ibapl.jnhw.winapi.Winbase.INFINITE;
import static de.ibapl.jnhw.winapi.Winbase.WAIT_OBJECT_0;

import static de.ibapl.jnhw.winapi.Fileapi.CreateFileW;
import static de.ibapl.jnhw.winapi.Fileapi.OPEN_EXISTING;
import static de.ibapl.jnhw.winapi.Fileapi.FlushFileBuffers;
import static de.ibapl.jnhw.winapi.Fileapi.ReadFile;
import static de.ibapl.jnhw.winapi.Fileapi.WriteFile;
import de.ibapl.jnhw.winapi.Ioapiset;

import static de.ibapl.jnhw.winapi.Synchapi.CreateEventW;
import static de.ibapl.jnhw.winapi.Synchapi.WaitForSingleObject;
import static de.ibapl.jnhw.winapi.Ioapiset.GetOverlappedResult;
import static de.ibapl.jnhw.winapi.Minwinbase.OVERLAPPED;
import static de.ibapl.jnhw.winapi.Minwindef.PHKEY;
import static de.ibapl.jnhw.winapi.Minwindef.LPBYTE;
import de.ibapl.jnhw.winapi.Winbase;
import de.ibapl.jnhw.winapi.Winerror;
import static de.ibapl.jnhw.winapi.Winnt.LPWSTR;
import de.ibapl.jnhw.winapi.Winreg;
import java.nio.channels.AsynchronousCloseException;

public class GenericWinSerialPortSocket extends AbstractSerialPortSocket<GenericWinSerialPortSocket> {

    private final static HANDLE INVALID_HANDLE_VALUE = Winbase.INVALID_HANDLE_VALUE();
    private HANDLE hFile = INVALID_HANDLE_VALUE;

    public static List<String> getWindowsBasedPortNames() {
        LinkedList<String> result = new LinkedList<>();

        PHKEY phkResult = new PHKEY();
        String lpSubKey = "HARDWARE\\DEVICEMAP\\SERIALCOMM\\";
        long errorCode = RegOpenKeyExW(HKEY_LOCAL_MACHINE(), lpSubKey, 0, KEY_READ(), phkResult);
        if (errorCode != Winerror.ERROR_SUCCESS()) {
            throw new RuntimeException("Could not open registry errorCode: " + errorCode);
        }
        int dwIndex = 0;
        LPWSTR lpValueName = new LPWSTR(256, true);
        LPBYTE lpData = new LPBYTE(256, false);
        IntRef lpType = new IntRef();
        boolean collecting = true;
        do {
            lpData.clear();
            errorCode = RegEnumValueW(phkResult, dwIndex, lpValueName, lpType, lpData);
            if (errorCode == Winerror.ERROR_SUCCESS()) {
                result.add(LPWSTR.stringValueOfNullTerminated(lpData));
                dwIndex++;
                lpValueName.clear();
            } else if (errorCode == Winerror.ERROR_NO_MORE_ITEMS()) {
                collecting = false;
            } else {
                throw new RuntimeException("Unknown Error in getWindowsBasedPortNames RegEnumValueW errorCode: " + errorCode);
            }
        } while (collecting);
        try {
            Winreg.RegCloseKey(phkResult);
        } catch (NativeErrorException nee) {
        }
        return result;
    }

    public GenericWinSerialPortSocket(String portName) {
        super(portName);
    }

    private IOException createNativeException(int errno, String formatString, Object... args) {
        return new IOException(String.format("Native port error on %s, \"%d\" %s", portName, errno,
                String.format(formatString, args)));
    }

    private IOException createClosedOrNativeException(int errno, String formatString, Object... args) {
        if (INVALID_HANDLE_VALUE.value == hFile.value) {
            return new IOException(PORT_IS_CLOSED);
        } else {
            return new IOException(String.format("Native port error on %s, \"%d\" %s", portName, errno,
                    String.format(formatString, args)));
        }
    }

    /**
     * Create and fill aDCB structure
     *
     */
    private DCB getDCB() throws IOException {
        DCB result = new DCB(true);//TODO needed to clear mem?
        try {
            GetCommState(hFile, result);
            return result;
        } catch (NativeErrorException nee) {
            throw createClosedOrNativeException(nee.errno, "GetCommState (%s)", portName);
        }
    }

    private DataBits getDatatBits(DCB dcb) throws IOException {
        switch (dcb.ByteSize()) {
            case 5:
                return DataBits.DB_5;
            case 6:
                return DataBits.DB_6;
            case 7:
                return DataBits.DB_7;
            case 8:
                return DataBits.DB_8;
            default:
                //TODO throw something other than a IllegalArgumentException
                throw new IllegalArgumentException("getDataBits Unknown databits");
        }
    }

    @Override
    public DataBits getDatatBits() throws IOException {
        return getDatatBits(getDCB());
    }

    @Override
    public Set<FlowControl> getFlowControl() throws IOException {
        return getFlowControl(getDCB());
    }

    private Set<FlowControl> getFlowControl(DCB dcb) throws IOException {
        Set<FlowControl> result = EnumSet.noneOf(FlowControl.class);
        if (dcb.fRtsControl() == RTS_CONTROL_HANDSHAKE()) {
            result.add(FlowControl.RTS_CTS_IN);
        }
        if (dcb.fOutxCtsFlow()) {
            result.add(FlowControl.RTS_CTS_OUT);
        }
        if (dcb.fInX()) {
            result.add(FlowControl.XON_XOFF_IN);
        }
        if (dcb.fOutX()) {
            result.add(FlowControl.XON_XOFF_OUT);
        }
        return result;
    }

    private COMSTAT getCOMSTAT() throws IOException {
        IntRef lpErrors = new IntRef(0);
        COMSTAT result = new COMSTAT(true); //TODO need to clear mem?

        try {
            ClearCommError(hFile, lpErrors, result);
            return result;
        } catch (NativeErrorException nee) {
            throw createClosedOrNativeException(nee.errno, "native call ClearCommError lpError %0x", lpErrors.value);
        }
    }

    private COMMTIMEOUTS getCOMMTIMEOUTS() throws IOException {
        COMMTIMEOUTS result = new COMMTIMEOUTS(true);//TODO need to clear mem?
        try {
            GetCommTimeouts(hFile, result);
            return result;
        } catch (NativeErrorException nee) {
            throw createClosedOrNativeException(nee.errno, "native call to GetCommTimeouts");
        }
    }

    @Override
    public int getInBufferBytesCount() throws IOException {
        return getCOMSTAT().cbInQue();
    }

    @Override
    public int getInterByteReadTimeout() throws IOException {
        COMMTIMEOUTS lpCommTimeouts = getCOMMTIMEOUTS();
        if ((lpCommTimeouts.ReadIntervalTimeout() & lpCommTimeouts.ReadTotalTimeoutMultiplier() & MAXDWORD()) == MAXDWORD()) {
            return 0;
        } else {
            return lpCommTimeouts.ReadIntervalTimeout();
        }
    }

    @Override
    public int getOutBufferBytesCount() throws IOException {
        return getCOMSTAT().cbOutQue();
    }

    @Override
    public int getOverallReadTimeout() throws IOException {
        return getCOMMTIMEOUTS().ReadTotalTimeoutConstant();
    }

    @Override
    public int getOverallWriteTimeout() throws IOException {
        return getCOMMTIMEOUTS().WriteTotalTimeoutConstant();
    }

    @Override
    public Parity getParity() throws IOException {
        return getParity(getDCB().Parity());
    }

    private Parity getParity(byte parity) throws IOException {
        if (parity == NOPARITY()) {
            return Parity.NONE;
        } else if (parity == ODDPARITY()) {
            return Parity.ODD;
        } else if (parity == EVENPARITY()) {
            return Parity.EVEN;
        } else if (parity == MARKPARITY()) {
            return Parity.MARK;
        } else if (parity == SPACEPARITY()) {
            return Parity.SPACE;
        } else {
            throw new IllegalArgumentException("getParity unknown Parity");
        }
    }

    @Override
    public Speed getSpeed() throws IOException {
        return getSpeed(getDCB());
    }

    private Speed getSpeed(DCB dcb) throws IOException {
        switch (dcb.BaudRate()) {
            case 0:
                return Speed._0_BPS;
            case 50:
                return Speed._50_BPS;
            case 75:
                return Speed._75_BPS;
            case 110:
                return Speed._110_BPS;
            case 134:
                return Speed._134_BPS;
            case 150:
                return Speed._150_BPS;
            case 200:
                return Speed._200_BPS;
            case 300:
                return Speed._300_BPS;
            case 600:
                return Speed._600_BPS;
            case 1200:
                return Speed._1200_BPS;
            case 1800:
                return Speed._1800_BPS;
            case 2400:
                return Speed._2400_BPS;
            case 4800:
                return Speed._4800_BPS;
            case 9600:
                return Speed._9600_BPS;
            case 19200:
                return Speed._19200_BPS;
            case 38400:
                return Speed._38400_BPS;
            case 57600:
                return Speed._57600_BPS;
            case 115200:
                return Speed._115200_BPS;
            case 230400:
                return Speed._230400_BPS;
            case 460800:
                return Speed._460800_BPS;
            case 500000:
                return Speed._500000_BPS;
            case 576000:
                return Speed._576000_BPS;
            case 921600:
                return Speed._921600_BPS;
            case 1000000:
                return Speed._1000000_BPS;
            case 1152000:
                return Speed._1152000_BPS;
            case 1500000:
                return Speed._1500000_BPS;
            case 2000000:
                return Speed._2000000_BPS;
            case 2500000:
                return Speed._2500000_BPS;
            case 3000000:
                return Speed._3000000_BPS;
            case 3500000:
                return Speed._3500000_BPS;
            case 4000000:
                return Speed._4000000_BPS;
            default:
                throw new IllegalArgumentException("getSpeed Speed not supported");
        }
    }

    @Override
    public StopBits getStopBits() throws IOException {
        return getStopBits(getDCB().StopBits());
    }

    private StopBits getStopBits(byte stopBits) throws IOException {
        if (stopBits == ONESTOPBIT()) {
            return StopBits.SB_1;
        } else if (stopBits == ONE5STOPBITS()) {
            return StopBits.SB_1_5;
        } else if (stopBits == TWOSTOPBITS()) {
            return StopBits.SB_2;
        } else {
            throw new IllegalArgumentException("getStopBits Unknown stopbits");
        }
    }

    @Override
    public char getXOFFChar() throws IOException {
        return getDCB().XoffChar();
    }

    @Override
    public char getXONChar() throws IOException {
        return getDCB().XonChar();
    }

    @Override
    public synchronized void close() throws IOException {
        super.close();

        final HANDLE _hFile = hFile;
        hFile = INVALID_HANDLE_VALUE;
// if only ReadIntervalTimeout is set and port is closed during pending read the read operation will hang forever...
        try {
            Ioapiset.CancelIo(_hFile);
        } catch (NativeErrorException nee) {
                 if (nee.errno != Winerror.ERROR_NOT_FOUND()) {
                hFile = _hFile;
		throw new IOException("Can't cancel io for closing", nee);
            }
       //no-op we dont care
        }

        try {
            CloseHandle(_hFile);
        } catch (NativeErrorException nee) {
            throw new IOException("Can't close port", nee);
        }
    }

    @Override
    public boolean isClosed() {
        return INVALID_HANDLE_VALUE.value == hFile.value;
    }

    private boolean getCommModemStatus(int bitMask) throws IOException {
        IntRef lpModemStat = new IntRef(0);

        try {
            GetCommModemStatus(hFile, lpModemStat);
            return (lpModemStat.value & bitMask) == bitMask;
        } catch (NativeErrorException nee) {
            throw createClosedOrNativeException(nee.errno, "Can't get GetCommModemStatus");
        }
    }

    @Override
    public boolean isCTS() throws IOException {
        return getCommModemStatus(MS_CTS_ON());
    }

    @Override
    public boolean isDCD() throws IOException {
        return getCommModemStatus(MS_RLSD_ON());
    }

    @Override
    public boolean isDSR() throws IOException {
        return getCommModemStatus(MS_DSR_ON());
    }

    @Override
    public boolean isOpen() {
        return INVALID_HANDLE_VALUE.value != hFile.value;
    }

    @Override
    public boolean isRI() throws IOException {
        return getCommModemStatus(MS_RING_ON());
    }

    @Override
    public void open() throws IOException {
        open(null, null, null, null, null);
    }

    private void setParams(DCB dcb, Speed speed, DataBits dataBits, StopBits stopBits, Parity parity,
            Set<FlowControl> flowControls) throws IOException {

        //Speed
        if (speed != null) {
            dcb.BaudRate(speed.value);
        }

        //DataBits
        if (dataBits != null) {
            dcb.ByteSize((byte) dataBits.value);
            switch (dataBits) {
                case DB_5:
                    if (dcb.StopBits() == TWOSTOPBITS()) {
                        //Fix stopBits
                        dcb.StopBits(ONE5STOPBITS());
                    }
                    break;
                case DB_6: //fall trough
                case DB_7: //fall trough
                case DB_8:
                    if (dcb.StopBits() == ONE5STOPBITS()) {
                        //Fix stopBits
                        dcb.StopBits(TWOSTOPBITS());
                    }
                    break;
                default:
                    throw new IllegalArgumentException("setParams Wrong databits: " + dataBits);
            }
        }

        //StopBits
        if (stopBits != null) {

            switch (stopBits) {
                case SB_1:
                    dcb.StopBits(ONESTOPBIT());
                    break;
                case SB_1_5:
                    if (dcb.ByteSize() == 5) {
                        dcb.StopBits(ONE5STOPBITS());
                    } else {
                        throw new IllegalArgumentException("setParams setStopBits to 1.5: only for 5 dataBits 1.5 stoppbits are supported");
                    }
                    break;
                case SB_2:
                    if (dcb.ByteSize() == 5) {
                        throw new IllegalArgumentException("setParams setStopBits to 2: 5 dataBits only 1.5 stoppbits are supported");
                    } else {
                        dcb.StopBits(TWOSTOPBITS());
                    }
                    break;
                default:
                    throw new IllegalArgumentException("setParams Unknown stopbits");
            }

        }

        //Parity
        if (parity != null) {

            switch (parity) {
                case NONE:
                    dcb.Parity(NOPARITY()); // switch parity input checking off
                    break;
                case ODD:
                    dcb.Parity(ODDPARITY());
                    break;
                case EVEN:
                    dcb.Parity(EVENPARITY());
                    break;
                case MARK:
                    dcb.Parity(MARKPARITY());
                    break;
                case SPACE:
                    dcb.Parity(SPACEPARITY());
                    break;
                default:
                    throw new IOException("setParams Wrong Parity");
            }

        }

        //FlowControl
        if (flowControls != null) {
            dcb.fRtsControl(RTS_CONTROL_DISABLE());
            dcb.fOutxCtsFlow(false);
            dcb.fOutX(false);
            dcb.fInX(false);
            if (flowControls.contains(FlowControl.RTS_CTS_IN)) {
                dcb.fRtsControl(RTS_CONTROL_HANDSHAKE());
            }
            if (flowControls.contains(FlowControl.RTS_CTS_OUT)) {
                dcb.fOutxCtsFlow(true);
            }
            if (flowControls.contains(FlowControl.XON_XOFF_IN)) {
                dcb.fInX(true);
            }
            if (flowControls.contains(FlowControl.XON_XOFF_OUT)) {
                dcb.fOutX(true);
            }
        }

        try {
            SetCommState(hFile, dcb);
        } catch (NativeErrorException nee) {
            if (nee.errno == ERROR_INVALID_PARAMETER()) {
                throw new IllegalArgumentException("setParams: Wrong params\n GetLastError() == ERROR_INVALID_PARAMETER");
            } else if (nee.errno == ERROR_GEN_FAILURE()) {
                throw new IllegalArgumentException("setParams: Wrong params\n GetLastError() == ERROR_GEN_FAILURE");
            } else {
                throw createClosedOrNativeException(nee.errno, "setParams SetCommState");
            }
        }

        StringBuilder errorMsgs = new StringBuilder();
        DCB readed = getDCB();
        if ((speed != null) && (speed
                != getSpeed(dcb))) {
            errorMsgs.append("Could not set speed!\n");
        }
        if ((dataBits != null) && (dataBits
                != getDatatBits(dcb))) {
            errorMsgs.append("Could not set dataBits!\n");
        }
        if ((stopBits != null) && (stopBits
                != getStopBits(dcb.StopBits()))) {
            errorMsgs.append("Could not set stopBits!\n");
        }
        if ((parity != null) && (parity
                != getParity(dcb.Parity()))) {
            errorMsgs.append("Could not set parity!\n");
        }
        if ((flowControls != null) && (!flowControls.equals(getFlowControl(dcb)))) {
            errorMsgs.append("Could not set flowControl!");
        }

        if (errorMsgs.length()
                > 0) {
            throw new IllegalArgumentException(errorMsgs.toString());
        }
    }

    @Override
    public void open(Speed speed, DataBits dataBits, StopBits stopBits, Parity parity, Set<FlowControl> flowControls)
            throws IOException {

        if (INVALID_HANDLE_VALUE.value != hFile.value) {
            throw new IOException(PORT_IS_OPEN);
        }

        final String portFullName = "\\\\.\\" + portName;

        try {
            hFile = CreateFileW(portFullName,
                    GENERIC_READ() | GENERIC_WRITE(),
                    0,
                    null,
                    OPEN_EXISTING(),
                    FILE_FLAG_OVERLAPPED(),
                    null);

        } catch (NativeErrorException nee) {

            if (nee.errno == ERROR_ACCESS_DENIED()) {
                throw new IOException(String.format("Port is busy: (%s)", portName));
            } else if (nee.errno == ERROR_FILE_NOT_FOUND()) {
                throw new IOException(String.format("Port not found: (%s)", portName));
            } else {
                throw new IOException(String.format("Open: Unknown port error %d", nee.errno));
            }
        }
        // The port is open, but maybe not configured ... setParam and getParam needs this to be set for their field access

        DCB dcb = new DCB(true);//TODO need to clear mem?
        try {
            GetCommState(hFile, dcb);
        } catch (NativeErrorException nee) {
            try {
                CloseHandle(hFile);
            } catch (NativeErrorException nee1) {
            }
            hFile.value = INVALID_HANDLE_VALUE.value;
            throw new IOException(String.format("Not a serial port: (%s)", portName));
        }

        //set speed etc.
        try {
            setParams(dcb, speed, dataBits, stopBits, parity, flowControls);
        } catch (Throwable t) {
            try {
                CloseHandle(hFile);
            } catch (NativeErrorException nee) {
            }
            hFile.value = INVALID_HANDLE_VALUE.value;
            throw t;
        }

        COMMTIMEOUTS lpCommTimeouts = new COMMTIMEOUTS(true);//TODO need to clear mem?
        try {
            GetCommTimeouts(hFile, lpCommTimeouts);
        } catch (NativeErrorException nee) {
            try {
                CloseHandle(hFile);
            } catch (NativeErrorException nee1) {
            }
            hFile.value = INVALID_HANDLE_VALUE.value;
            throw new IOException("Open GetCommTimeouts");
        }

        lpCommTimeouts.ReadIntervalTimeout(100);
        lpCommTimeouts.ReadTotalTimeoutConstant(0);
        lpCommTimeouts.ReadTotalTimeoutMultiplier(0);
        lpCommTimeouts.WriteTotalTimeoutConstant(0);
        lpCommTimeouts.WriteTotalTimeoutMultiplier(0);

        try {
            SetCommTimeouts(hFile, lpCommTimeouts);
        } catch (NativeErrorException nee) {
            try {
                CloseHandle(hFile);
            } catch (NativeErrorException nee1) {
            }
            hFile.value = INVALID_HANDLE_VALUE.value;

            throw new IOException("Open SetCommTimeouts");
        }
    }

    @Override
    public void sendBreak(int duration) throws IOException {
        if (duration <= 0) {
            throw new IllegalArgumentException("sendBreak duration must be grater than 0");
        }

        try {
            SetCommBreak(hFile);
        } catch (NativeErrorException nee) {
            throw createClosedOrNativeException(nee.errno, "sendBreak SetCommBreak");
        }

        try {
            Thread.sleep(duration);

        } catch (InterruptedException ex) {
            Logger.getLogger(GenericWinSerialPortSocket.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        try {
            ClearCommBreak(hFile);
        } catch (NativeErrorException nee) {
            throw createClosedOrNativeException(nee.errno, "sendBreak ClearCommBreak");
        }
    }

    @Override
    public void sendXOFF() throws IOException {
        throw new IOException("sendXOFF not implemented yet");
    }

    @Override
    public void sendXON() throws IOException {
        throw new IOException("sendXON not implemented yet");
    }

    @Override
    public void setBreak(boolean value) throws IOException {
        int dwFunc = value ? SETBREAK() : CLRBREAK();

        try {
            EscapeCommFunction(hFile, dwFunc);
        } catch (NativeErrorException nee) {
            if (nee.errno == ERROR_INVALID_PARAMETER()) {
                throw new IllegalArgumentException("setBreak: Wrong value");
            } else {
                throw createClosedOrNativeException(nee.errno, "Can't set/clear BREAK");
            }
        }
    }

    @Override
    public void setDataBits(DataBits dataBits) throws IOException {
        setParams(getDCB(), null, dataBits, null, null, null);
    }

    @Override
    public void setDTR(boolean value) throws IOException {
        int dwFunc = value ? SETDTR() : CLRDTR();

        try {
            EscapeCommFunction(hFile, dwFunc);
        } catch (NativeErrorException nee) {
            if (nee.errno == ERROR_INVALID_PARAMETER()) {
                throw new IllegalArgumentException("setDTR: Wrong value");
            } else {
                throw createClosedOrNativeException(nee.errno, "Can't set/clear DTR");
            }
        }
    }

    @Override
    public void setFlowControl(Set<FlowControl> flowControls) throws IOException {
        setParams(getDCB(), null, null, null, null, flowControls);
    }

    @Override
    public void setParity(Parity parity) throws IOException {
        setParams(getDCB(), null, null, null, parity, null);
    }

    @Override
    public void setRTS(boolean value) throws IOException {
        int dwFunc = value ? SETRTS() : CLRRTS();

        try {
            EscapeCommFunction(hFile, dwFunc);
        } catch (NativeErrorException nee) {
            if (nee.errno == ERROR_INVALID_PARAMETER()) {
                throw new IllegalArgumentException("setRTS: Wrong value");
            } else {
                throw createClosedOrNativeException(nee.errno, "Can't set/clear RTS");
            }
        }
    }

    @Override
    public void setSpeed(Speed speed) throws IOException {
        setParams(getDCB(), speed, null, null, null, null);
    }

    @Override
    public void setStopBits(StopBits stopBits) throws IOException {
        setParams(getDCB(), null, null, stopBits, null, null);
    }

    @Override
    public void setTimeouts(int interByteReadTimeout, int overallReadTimeout, int overallWriteTimeout)
            throws IOException {
        COMMTIMEOUTS commTimeouts = new COMMTIMEOUTS(true);

        if (overallWriteTimeout < 0) {
            throw new IllegalArgumentException("setTimeouts: overallWriteTimeout must >= 0");
        } else if (overallWriteTimeout == MAXDWORD()) {
            //MAXDWORD has a special meaning...
            overallWriteTimeout = MAXDWORD() - 1;
        }

        if (overallReadTimeout < 0) {
            throw new IllegalArgumentException("setTimeouts: overallReadTimeout must >= 0");
        } else if (overallReadTimeout == MAXDWORD()) {
            //MAXDWORD has a special meaning...
            overallReadTimeout = MAXDWORD() - 1;
        }

        if (interByteReadTimeout < 0) {
            throw new IllegalArgumentException("setReadTimeouts: interByteReadTimeout must >= 0");
        } else if (interByteReadTimeout == MAXDWORD()) {
            //MAXDWORD has a special meaning...
            interByteReadTimeout = MAXDWORD() - 1;
        }

        if ((interByteReadTimeout == 0) && (overallReadTimeout > 0)) {
            //This fits best for wait a timeout and have no interByteReadTimeout see also getInterbyteReadTimeout for reading back
            commTimeouts.ReadIntervalTimeout(MAXDWORD());
            commTimeouts.ReadTotalTimeoutMultiplier(MAXDWORD());
            commTimeouts.ReadTotalTimeoutConstant(overallReadTimeout);
        } else {
            commTimeouts.ReadIntervalTimeout(interByteReadTimeout);
            commTimeouts.ReadTotalTimeoutMultiplier(0);
            commTimeouts.ReadTotalTimeoutConstant(overallReadTimeout);
        }

        commTimeouts.WriteTotalTimeoutMultiplier(0);
        commTimeouts.WriteTotalTimeoutConstant(overallWriteTimeout);

        try {
            SetCommTimeouts(hFile, commTimeouts);
        } catch (NativeErrorException nee) {
            if (nee.errno == ERROR_INVALID_PARAMETER()) {
                throw new IllegalArgumentException("setTimeouts: Wrong Timeouts");
            } else {
                throw createClosedOrNativeException(nee.errno, "setTimeouts SetCommTimeouts");
            }
        }
    }

    @Override
    public void setXOFFChar(char c) throws IOException {
        DCB dcb = new DCB(true);//TODO need to clear mem?

        try {
            GetCommState(hFile, dcb);
        } catch (NativeErrorException nee) {
            throw createClosedOrNativeException(nee.errno, "setXOFFChar GetCommState");
        }

        dcb.XoffChar(c);

        try {
            SetCommState(hFile, dcb);
        } catch (NativeErrorException nee) {
            if (nee.errno == ERROR_INVALID_PARAMETER()) {
                throw new IllegalArgumentException("etXOFFChar: Wrong value\n GetLastError() == ERROR_INVALID_PARAMETER");
            } else if (nee.errno == ERROR_GEN_FAILURE()) {
                throw new IllegalArgumentException("etXOFFChar: Wrong value\n GetLastError() == ERROR_GEN_FAILURE");
            } else {
                throw createClosedOrNativeException(nee.errno, "setXOFFChar SetCommState");
            }
        }
    }

    @Override
    public void setXONChar(char c) throws IOException {
        DCB dcb = new DCB(true);//TODO need to clear mem?

        try {
            GetCommState(hFile, dcb);
        } catch (NativeErrorException nee) {
            throw createClosedOrNativeException(nee.errno, "setXONChar GetCommState");
        }

        dcb.XonChar(c);

        try {
            SetCommState(hFile, dcb);
        } catch (NativeErrorException nee) {
            if (nee.errno == ERROR_INVALID_PARAMETER()) {
                throw new IllegalArgumentException("setXONChar: Wrong value\n GetLastError() == ERROR_INVALID_PARAMETER");
            } else if (nee.errno == ERROR_GEN_FAILURE()) {
                throw new IllegalArgumentException("setXONChar: Wrong value\n GetLastError() == ERROR_GEN_FAILURE");
            } else {
                throw createClosedOrNativeException(nee.errno, "setXONChar SetCommState");
            }
        }
    }

    @Override
    protected void drainOutputBuffer() throws IOException {
        try {
            FlushFileBuffers(hFile);
        } catch (NativeErrorException nee) {
            throw createClosedOrNativeException(nee.errno, "drainOutputBuffer");
        }
    }

    @Override
    public int read(ByteBuffer b) throws IOException {

        OVERLAPPED overlapped = new OVERLAPPED(true);

        try {
            overlapped.hEvent(CreateEventW(null, true, false, null));
        } catch (NativeErrorException nee) {
            throw new IOException("Error readBytes => CreateEventW: " + nee.errno);
        }

        try {
            ReadFile(hFile, b, overlapped);
        } catch (NativeErrorException nee) {
            if (nee.errno != ERROR_IO_PENDING()) {
                try {
                    CloseHandle(overlapped.hEvent());
                } catch (NativeErrorException nee1) {
                }
                if (INVALID_HANDLE_VALUE.value == hFile.value) {
                    throw new AsynchronousCloseException();
                } else {
                    throw new InterruptedIOException("Error readBytes(GetLastError):" + nee.errno);
                }
            }
        }
        //overlapped path
        final long waitResult = WaitForSingleObject(overlapped.hEvent(), INFINITE());
        if (waitResult != WAIT_OBJECT_0()) {
            try {
                CloseHandle(overlapped.hEvent());
            } catch (NativeErrorException nee2) {
            }
            if (INVALID_HANDLE_VALUE.value == hFile.value) {
                throw new AsynchronousCloseException();
            } else {
                throw new InterruptedIOException("Error readBytes (WaitForSingleObject)");
            }
        }

        IntRef dwBytesRead = new IntRef(0);

        try {
            GetOverlappedResult(hFile, overlapped, dwBytesRead, false, b);
        } catch (NativeErrorException nee) {
            try {
                CloseHandle(overlapped.hEvent());
            } catch (NativeErrorException nee1) {
            }
            if (INVALID_HANDLE_VALUE.value == hFile.value) {
                throw new AsynchronousCloseException();
            } else {
                InterruptedIOException iioe = new InterruptedIOException("Error readBytes (GetOverlappedResult)");
                iioe.bytesTransferred = (int) dwBytesRead.value;
                throw iioe;
            }
        }

        try {
            CloseHandle(overlapped.hEvent());
        } catch (NativeErrorException nee) {
        }

        if (dwBytesRead.value > 0) {
            //Success
            return (int) dwBytesRead.value;
        } else if (dwBytesRead.value == 0) {
            if (INVALID_HANDLE_VALUE.value == hFile.value) {
                throw new AsynchronousCloseException();
            } else {
                TimeoutIOException tioe = new TimeoutIOException();
                tioe.bytesTransferred = (int) dwBytesRead.value;
                throw tioe;
            }
        } else {
            throw new InterruptedIOException("Should never happen! readBytes dwBytes < 0");
        }

    }

    @Override
    public int write(ByteBuffer b) throws IOException {

        OVERLAPPED overlapped = new OVERLAPPED(true);
        try {
            overlapped.hEvent(CreateEventW(null, true, false, null));
        } catch (NativeErrorException nee) {
            throw new IOException("Error readBytes => CreateEventW: " + nee.errno);
        }

        try {
            WriteFile(hFile, b, overlapped);
        } catch (NativeErrorException nee) {

            if (nee.errno != ERROR_IO_PENDING()) {
                try {
                    CloseHandle(overlapped.hEvent());
                } catch (NativeErrorException nee1) {
                };
                if (INVALID_HANDLE_VALUE.value == hFile.value) {
                    throw new AsynchronousCloseException();
                } else {
                    throw new InterruptedIOException("Error writeBytes (GetLastError): " + nee.errno);
                }
            }
        }
        final long waitResult = WaitForSingleObject(overlapped.hEvent(), INFINITE());

        if (waitResult != WAIT_OBJECT_0()) {
            try {
                CloseHandle(overlapped.hEvent());
            } catch (NativeErrorException nee1) {
            }
            if (INVALID_HANDLE_VALUE.value == hFile.value) {
                throw new AsynchronousCloseException();
            } else {
                throw new InterruptedIOException("Error writeBytes (WaitForSingleObject): " + waitResult);
            }
        }

        IntRef dwBytesWritten = new IntRef(0);
        try {
            GetOverlappedResult(hFile, overlapped, dwBytesWritten, false, b);
        } catch (NativeErrorException nee) {
            try {
                CloseHandle(overlapped.hEvent());
            } catch (NativeErrorException nee1) {
            }
            if (INVALID_HANDLE_VALUE.value == hFile.value) {
                throw new AsynchronousCloseException();
            } else {
                InterruptedIOException iioe = new InterruptedIOException("Error writeBytes (GetOverlappedResult) errno: " + nee.errno);
                iioe.bytesTransferred = (int) dwBytesWritten.value;
                throw iioe;
            }
        }

        try {
            CloseHandle(overlapped.hEvent());
        } catch (NativeErrorException nee) {
        }

        if (b.hasRemaining()) {
            if (INVALID_HANDLE_VALUE.value == hFile.value) {
                throw new AsynchronousCloseException();
            } else {
//                if (winbase_H.GetLastError() == Winerr_H.ERROR_IO_PENDING) {
//                    TimeoutIOException tioe = new TimeoutIOException();
//                    tioe.bytesTransferred = (int) dwBytesWritten.value;
//                    throw tioe;
//                } else {
                InterruptedIOException iioe = new InterruptedIOException("Error writeBytes too view written");
                iioe.bytesTransferred = (int) dwBytesWritten.value;
                throw iioe;
//                }
            }
        }

        //Success
        return (int) dwBytesWritten.value;
    }

}
