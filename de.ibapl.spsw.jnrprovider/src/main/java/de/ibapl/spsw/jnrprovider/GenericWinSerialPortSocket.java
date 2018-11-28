package de.ibapl.spsw.jnrprovider;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.ibapl.jnrheader.JnrHeader;
import de.ibapl.jnrheader.api.windows.Fileapi_H;
import de.ibapl.jnrheader.api.windows.Ioapiset_H;
import de.ibapl.jnrheader.api.windows.Minwinbase_H.OVERLAPPED;
import de.ibapl.jnrheader.api.windows.Minwindef_H.HANDLE;
import de.ibapl.jnrheader.api.windows.Minwindef_H.LPDWORD;
import de.ibapl.jnrheader.api.windows.Minwindef_H.LPWSTR;
import de.ibapl.jnrheader.api.windows.Minwindef_H.PHKEY;
import de.ibapl.jnrheader.api.windows.Synchapi_H;
import de.ibapl.jnrheader.api.windows.Winbase_H;
import de.ibapl.jnrheader.api.windows.Winbase_H.COMMTIMEOUTS;
import de.ibapl.jnrheader.api.windows.Winbase_H.COMSTAT;
import de.ibapl.jnrheader.api.windows.Winbase_H.DCB;
import de.ibapl.jnrheader.api.windows.Winerr_H;
import de.ibapl.jnrheader.api.windows.Winnt_H;
import de.ibapl.jnrheader.api.windows.Winreg_H;
import de.ibapl.jnrheader.api.windows.Winreg_H.REGSAM;
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

public class GenericWinSerialPortSocket extends AbstractSerialPortSocket<GenericWinSerialPortSocket> {

    private HANDLE hFile;
    private final Winbase_H winbase_H;
    private final Winerr_H winerr_H;
    private final Fileapi_H fileapi_H;
    private final Synchapi_H synchapi_H;
    private final Ioapiset_H ioapiset_H;

    public static List<String> getWindowsBasedPortNames() {
        LinkedList<String> result = new LinkedList<>();

        Winreg_H winreg_H = JnrHeader.getInstance(Winreg_H.class);
        Winerr_H winerr_H = JnrHeader.getInstance(Winerr_H.class);
        Winnt_H winnt_H = JnrHeader.getInstance(Winnt_H.class);
        Winbase_H winbase_H = JnrHeader.getInstance(Winbase_H.class);

        PHKEY phkResult = new PHKEY();
        REGSAM samDesired = REGSAM.of(Winnt_H.KEY_READ);
        String lpSubKey = "HARDWARE\\DEVICEMAP\\SERIALCOMM\\";
        if (winreg_H.RegOpenKeyExW(Winreg_H.HKEY_LOCAL_MACHINE, lpSubKey, 0, samDesired, phkResult)
                == Winerr_H.ERROR_SUCCESS) {
            int dwIndex = 0;
            LPWSTR lpValueName = LPWSTR.allocate(256);
            ByteBuffer lpData = ByteBuffer.allocateDirect(256);
            long enumResult;
            do {
                lpValueName.clear();
                lpData.clear();
                enumResult = winreg_H.RegEnumValueW(phkResult.indirection, dwIndex, lpValueName, null, lpData);
                if (enumResult == Winerr_H.ERROR_SUCCESS) {
                    result.add(LPWSTR.buffer2String(lpData, true));
                    dwIndex++;
                }
            } while (enumResult != Winerr_H.ERROR_SUCCESS);

            winbase_H.CloseHandle(phkResult.indirection);
            return result;
        } else {
            throw new RuntimeException("Could not open registry");
        }
    }

    public GenericWinSerialPortSocket(String portName) {
        super(portName);
        winbase_H = JnrHeader.getInstance(Winbase_H.class);
        winerr_H = JnrHeader.getInstance(Winerr_H.class);
        fileapi_H = JnrHeader.getInstance(Fileapi_H.class);
        synchapi_H = JnrHeader.getInstance(Synchapi_H.class);
        ioapiset_H = JnrHeader.getInstance(Ioapiset_H.class);
    }

    private void throwNativeException(String formatString, Object... args) throws IOException {
        throw new IOException(String.format("Native port error on %s, \"%d\" %s", portName, winbase_H.GetLastError(),
                String.format(formatString, args)));
    }

    private void throwClosedOrNativeException(String formatString, Object... args) throws IOException {
        if (Winbase_H.INVALID_HANDLE_VALUE.equals(hFile)) {
            throw new IOException(PORT_IS_CLOSED);
        } else {
            throw new IOException(String.format("Native port error on %s, \"%d\" %s", portName, winbase_H.GetLastError(),
                    String.format(formatString, args)));
        }
    }

    /**
     * Create and fill aDCB structure
     *
     */
    private DCB getDCB() throws IOException {
        DCB result = winbase_H.createDCB();
        if (!winbase_H.GetCommState(hFile, result)) {
            throwClosedOrNativeException("Native port error \"%d\" => GetCommState (%s)", winbase_H.GetLastError(), portName);
        }
        return result;
    }

    private DataBits getDatatBits(DCB dcb) throws IOException {
        switch (dcb.ByteSize) {
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
        if (dcb.fRtsControl == winbase_H.RTS_CONTROL_HANDSHAKE) {
            result.add(FlowControl.RTS_CTS_IN);
        }
        if (dcb.fOutxCtsFlow) {
            result.add(FlowControl.RTS_CTS_OUT);
        }
        if (dcb.fInX) {
            result.add(FlowControl.XON_XOFF_IN);
        }
        if (dcb.fOutX) {
            result.add(FlowControl.XON_XOFF_OUT);
        }
        return result;
    }

    private COMSTAT getCOMSTAT() throws IOException {
        LPDWORD lpErrors = LPDWORD.ofValue(0);
        COMSTAT result = new COMSTAT();

        if (!winbase_H.ClearCommError(hFile, lpErrors, result)) {
            throwClosedOrNativeException("native call ClearCommError lpError %0x", lpErrors.value);
        }
        return result;
    }

    private COMMTIMEOUTS getCOMMTIMEOUTS() throws IOException {
        COMMTIMEOUTS result = new COMMTIMEOUTS();
        if (!winbase_H.GetCommTimeouts(hFile, result)) {
            throwClosedOrNativeException("native call to GetCommTimeouts");
        }
        return result;
    }

    @Override
    public int getInBufferBytesCount() throws IOException {
        return getCOMSTAT().cbInQue;
    }

    @Override
    public int getInterByteReadTimeout() throws IOException {
        COMMTIMEOUTS lpCommTimeouts = getCOMMTIMEOUTS();
        if ((lpCommTimeouts.ReadIntervalTimeout & lpCommTimeouts.ReadTotalTimeoutMultiplier & Winnt_H.MAXDWORD) == Winnt_H.MAXDWORD) {
            return 0;
        } else {
            return lpCommTimeouts.ReadIntervalTimeout;
        }
    }

    @Override
    public int getOutBufferBytesCount() throws IOException {
        return getCOMSTAT().cbOutQue;
    }

    @Override
    public int getOverallReadTimeout() throws IOException {
        return getCOMMTIMEOUTS().ReadTotalTimeoutConstant;
    }

    @Override
    public int getOverallWriteTimeout() throws IOException {
        return getCOMMTIMEOUTS().WriteTotalTimeoutConstant;
    }

    @Override
    public Parity getParity() throws IOException {
        return getParity(getDCB());
    }

    private Parity getParity(DCB dcb) throws IOException {
        switch (dcb.Parity) {
            case Winbase_H.NOPARITY:
                return Parity.NONE;
            case Winbase_H.ODDPARITY:
                return Parity.ODD;
            case Winbase_H.EVENPARITY:
                return Parity.EVEN;
            case Winbase_H.MARKPARITY:
                return Parity.MARK;
            case Winbase_H.SPACEPARITY:
                return Parity.SPACE;
            default:
                throw new IllegalArgumentException("getParity unknown Parity");
        }
    }

    @Override
    public Speed getSpeed() throws IOException {
        return getSpeed(getDCB());
    }

    private Speed getSpeed(DCB dcb) throws IOException {
        switch (dcb.BaudRate) {
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
        return getStopBits(getDCB());
    }

    private StopBits getStopBits(DCB dcb) throws IOException {
        switch (dcb.StopBits) {
            case Winbase_H.ONESTOPBIT:
                return StopBits.SB_1;
            case Winbase_H.ONE5STOPBITS:
                return StopBits.SB_1_5;
            case Winbase_H.TWOSTOPBITS:
                return StopBits.SB_2;
            default:
                throw new IllegalArgumentException("getStopBits Unknown stopbits");
        }
    }

    @Override
    public char getXOFFChar() throws IOException {
        return getDCB().XoffChar;
    }

    @Override
    public char getXONChar() throws IOException {
        return getDCB().XonChar;
    }

    @Override
    public boolean isClosed() {
        // TODO Auto-generated method stub
        return false;
    }

    private boolean getCommModemStatus(int bitMask) throws IOException {
        LPDWORD lpModemStat = LPDWORD.ofValue(0);

        if (!winbase_H.GetCommModemStatus(hFile, lpModemStat)) {
            throwClosedOrNativeException("Can't get GetCommModemStatus");
        }
        return (lpModemStat.value & bitMask) == bitMask;
    }

    @Override
    public boolean isCTS() throws IOException {
        return getCommModemStatus(Winbase_H.MS_CTS_ON);
    }

    @Override
    public boolean isDCD() throws IOException {
        return getCommModemStatus(Winbase_H.MS_RLSD_ON);
    }

    @Override
    public boolean isDSR() throws IOException {
        return getCommModemStatus(Winbase_H.MS_DSR_ON);
    }

    @Override
    public boolean isOpen() {
        return !winbase_H.INVALID_HANDLE_VALUE.equals(hFile);
    }

    @Override
    public boolean isRI() throws IOException {
        return getCommModemStatus(Winbase_H.MS_RING_ON);
    }

    @Override
    public void open() throws IOException {
        open(null, null, null, null, null);
    }

    private void setParams(DCB dcb, Speed speed, DataBits dataBits, StopBits stopBits, Parity parity,
            Set<FlowControl> flowControls) throws IOException {

        //Speed
        if (speed != null) {
            dcb.BaudRate = speed.value;
        }

        //DataBits
        if (dataBits != null) {
            dcb.ByteSize = (byte) dataBits.value;
            switch (dataBits) {
                case DB_5:
                    if (dcb.StopBits == Winbase_H.TWOSTOPBITS) {
                        //Fix stopBits
                        dcb.StopBits = Winbase_H.ONE5STOPBITS;
                    }
                    break;
                case DB_6: //fall trough
                case DB_7: //fall trough
                case DB_8:
                    if (dcb.StopBits == Winbase_H.ONE5STOPBITS) {
                        //Fix stopBits
                        dcb.StopBits = Winbase_H.TWOSTOPBITS;
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
                    dcb.StopBits = Winbase_H.ONESTOPBIT;
                    break;
                case SB_1_5:
                    if (dcb.ByteSize == 5) {
                        dcb.StopBits = Winbase_H.ONE5STOPBITS;
                    } else {
                        throw new IllegalArgumentException("setParams setStopBits to 1.5: only for 5 dataBits 1.5 stoppbits are supported");
                    }
                    break;
                case SB_2:
                    if (dcb.ByteSize == 5) {
                        throw new IllegalArgumentException("setParams setStopBits to 2: 5 dataBits only 1.5 stoppbits are supported");
                    } else {
                        dcb.StopBits = Winbase_H.TWOSTOPBITS;
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
                    dcb.Parity = Winbase_H.NOPARITY; // switch parity input checking off
                    break;
                case ODD:
                    dcb.Parity = Winbase_H.ODDPARITY;
                    break;
                case EVEN:
                    dcb.Parity = Winbase_H.EVENPARITY;
                    break;
                case MARK:
                    dcb.Parity = Winbase_H.MARKPARITY;
                    break;
                case SPACE:
                    dcb.Parity = Winbase_H.SPACEPARITY;
                    break;
                default:
                    throw new IOException("setParams Wrong Parity");
            }

        }

        //FlowControl
        if (flowControls != null) {
            dcb.fRtsControl = Winbase_H.RTS_CONTROL_DISABLE;
            dcb.fOutxCtsFlow = false;
            dcb.fOutX = false;
            dcb.fInX = false;
            if (flowControls.contains(FlowControl.RTS_CTS_IN)) {
                dcb.fRtsControl = Winbase_H.RTS_CONTROL_HANDSHAKE;
            }
            if (flowControls.contains(FlowControl.RTS_CTS_OUT)) {
                dcb.fOutxCtsFlow = true;
            }
            if (flowControls.contains(FlowControl.XON_XOFF_IN)) {
                dcb.fInX = true;
            }
            if (flowControls.contains(FlowControl.XON_XOFF_OUT)) {
                dcb.fOutX = true;
            }
        }

        if (!winbase_H.SetCommState(hFile, dcb)) {
            switch (winbase_H.GetLastError()) {
                case Winerr_H.ERROR_INVALID_PARAMETER:
                    throw new IllegalArgumentException("setParams: Wrong FlowControl\n GetLastError() == ERROR_INVALID_PARAMETER");
                case Winerr_H.ERROR_GEN_FAILURE:
                    throw new IllegalArgumentException("setParams: Wrong FlowControl\n GetLastError() == ERROR_GEN_FAILURE");
                default:
                    throwClosedOrNativeException("setParams SetCommState");
            }
        }

        StringBuilder errorMsgs = new StringBuilder();
        DCB readed = getDCB();
        if ((speed != null) && (speed != getSpeed(dcb))) {
            errorMsgs.append("Could not set speed!\n");
        };
        if ((dataBits != null) && (dataBits != getDatatBits(dcb))) {
            errorMsgs.append("Could not set dataBits!\n");
        };
        if ((stopBits != null) && (stopBits != getStopBits(dcb))) {
            errorMsgs.append("Could not set stopBits!\n");
        };
        if ((parity != null) && (parity != getParity(dcb))) {
            errorMsgs.append("Could not set stopBits!\n");
        };
        if ((flowControls != null) && (flowControls != getFlowControl(dcb))) {
            errorMsgs.append("Could not set stopBits!");
        };

        if (errorMsgs.length() > 0) {
            throw new IllegalArgumentException(errorMsgs.toString());
        }
    }

    @Override
    public void open(Speed speed, DataBits dataBits, StopBits stopBits, Parity parity, Set<FlowControl> flowControls)
            throws IOException {

        if (!Winbase_H.INVALID_HANDLE_VALUE.equals(hFile)) {
            throw new IOException(PORT_IS_OPEN);
        }

        final String portFullName = "\\\\.\\" + portName;

        hFile = fileapi_H.CreateFileW(portFullName,
                Winnt_H.GENERIC_READ | Winnt_H.GENERIC_WRITE,
                0,
                null,
                Fileapi_H.OPEN_EXISTING,
                Winbase_H.FILE_FLAG_OVERLAPPED,
                null);

        if (Winbase_H.INVALID_HANDLE_VALUE.equals(hFile)) {

            switch (winbase_H.GetLastError()) {
                case Winerr_H.ERROR_ACCESS_DENIED:
                    throw new IOException(String.format("Port is busy: (%s)", portName));
                case Winerr_H.ERROR_FILE_NOT_FOUND:
                    throw new IOException(String.format("Port not found: (%s)", portName));
                default:
                    throw new IOException(String.format("Open: Unknown port error %d", winbase_H.GetLastError()));
            }
        }
        // The port is open, but maybe not configured ... setParam and getParam needs this to be set for their field access

        DCB dcb = new DCB();
        if (!winbase_H.GetCommState(hFile, dcb)) {
            winbase_H.CloseHandle(hFile);
            hFile.value = Winbase_H.INVALID_HANDLE_VALUE.value;
            throw new IOException(String.format("Not a serial port: (%s)", portName));
        }

        //set speed etc.
        try {
            setParams(dcb, speed, dataBits, stopBits, parity, flowControls);
        } catch (Throwable t) {
            winbase_H.CloseHandle(hFile);
            hFile.value = Winbase_H.INVALID_HANDLE_VALUE.value;
            throw t;
        }

        COMMTIMEOUTS lpCommTimeouts = new COMMTIMEOUTS();
        if (!winbase_H.GetCommTimeouts(hFile, lpCommTimeouts)) {
            winbase_H.CloseHandle(hFile);
            hFile.value = Winbase_H.INVALID_HANDLE_VALUE.value;
            throw new IOException("Open GetCommTimeouts");
        }

        lpCommTimeouts.ReadIntervalTimeout = 100;
        lpCommTimeouts.ReadTotalTimeoutConstant = 0;
        lpCommTimeouts.ReadTotalTimeoutMultiplier = 0;
        lpCommTimeouts.WriteTotalTimeoutConstant = 0;
        lpCommTimeouts.WriteTotalTimeoutMultiplier = 0;

        if (!winbase_H.SetCommTimeouts(hFile, lpCommTimeouts)) {
            winbase_H.CloseHandle(hFile);
            hFile.value = Winbase_H.INVALID_HANDLE_VALUE.value;

            throw new IOException("Open SetCommTimeouts");
        }
    }

    @Override
    public void sendBreak(int duration) throws IOException {
        if (duration <= 0) {
            throw new IllegalArgumentException("sendBreak duration must be grater than 0");
        }

        if (!winbase_H.SetCommBreak(hFile)) {
            throwClosedOrNativeException("sendBreak SetCommBreak");
            return;
        }

        try {
            Thread.sleep(duration);
        } catch (InterruptedException ex) {
            Logger.getLogger(GenericWinSerialPortSocket.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (!winbase_H.ClearCommBreak(hFile)) {
            throwClosedOrNativeException("sendBreak ClearCommBreak");
            return;
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
        int dwFunc = value ? Winbase_H.SETBREAK : Winbase_H.CLRBREAK;

        if (!winbase_H.EscapeCommFunction(hFile, dwFunc)) {
            if (winbase_H.GetLastError() == Winerr_H.ERROR_INVALID_PARAMETER) {
                throw new IllegalArgumentException("setBreak: Wrong value");
            } else {
                throwClosedOrNativeException("Can't set/clear BREAK");
            }
        }
    }

    @Override
    public void setDataBits(DataBits dataBits) throws IOException {
        setParams(getDCB(), null, dataBits, null, null, null);
    }

    @Override
    public void setDTR(boolean value) throws IOException {
        int dwFunc = value ? Winbase_H.SETDTR : Winbase_H.CLRDTR;

        if (!winbase_H.EscapeCommFunction(hFile, dwFunc)) {
            if (winbase_H.GetLastError() == Winerr_H.ERROR_INVALID_PARAMETER) {
                throw new IllegalArgumentException("setDTR: Wrong value");
            } else {
                throwClosedOrNativeException("Can't set/clear DTR");
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
        int dwFunc = value ? Winbase_H.SETRTS : Winbase_H.CLRRTS;

        if (!winbase_H.EscapeCommFunction(hFile, dwFunc)) {
            if (winbase_H.GetLastError() == Winerr_H.ERROR_INVALID_PARAMETER) {
                throw new IllegalArgumentException("setRTS: Wrong value");
            } else {
                throwClosedOrNativeException("Can't set/clear RTS");
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
        // TODO Auto-generated method stub

    }

    @Override
    public void setXOFFChar(char c) throws IOException {
        DCB dcb = new DCB();
//TODO        dcb.DCBlength = sizeof(DCB);

        if (!winbase_H.GetCommState(hFile, dcb)) {
            throwClosedOrNativeException("setXOFFChar GetCommState");
            return;
        }

        dcb.XoffChar = c;

        if (!winbase_H.SetCommState(hFile, dcb)) {
            switch (winbase_H.GetLastError()) {
                case Winerr_H.ERROR_INVALID_PARAMETER:
                    throw new IllegalArgumentException("etXOFFChar: Wrong value\n GetLastError() == ERROR_INVALID_PARAMETER");
                case Winerr_H.ERROR_GEN_FAILURE:
                    throw new IllegalArgumentException("etXOFFChar: Wrong value\n GetLastError() == ERROR_GEN_FAILURE");
                default:
                    throwClosedOrNativeException("setXOFFChar SetCommState");
            }
        }
    }

    @Override
    public void setXONChar(char c) throws IOException {
        DCB dcb = new DCB();
        //TODO dcb.DCBlength = sizeof(DCB);

        if (!winbase_H.GetCommState(hFile, dcb)) {
            throwClosedOrNativeException("setXONChar GetCommState");
            return;
        }

        dcb.XonChar = c;

        if (!winbase_H.SetCommState(hFile, dcb)) {
            switch (winbase_H.GetLastError()) {
                case Winerr_H.ERROR_INVALID_PARAMETER:
                    throw new IllegalArgumentException("setXONChar: Wrong value\n GetLastError() == ERROR_INVALID_PARAMETER");
                case Winerr_H.ERROR_GEN_FAILURE:
                    throw new IllegalArgumentException("setXONChar: Wrong value\n GetLastError() == ERROR_GEN_FAILURE");
                default:
                    throwClosedOrNativeException("setXONChar SetCommState");
            }
        }
    }

    @Override
    protected void drainOutputBuffer() throws IOException {
        if (!fileapi_H.FlushFileBuffers(hFile)) {
            throwClosedOrNativeException("drainOutputBuffer");
        }
    }

    @Override
    public int read(ByteBuffer b) throws IOException {

        OVERLAPPED overlapped = OVERLAPPED.newOLWithUnionPointer();

        overlapped.hEvent = synchapi_H.CreateEventW(null, true, false, null);

        if (!fileapi_H.ReadFile(hFile, b, b.remaining(), null, overlapped)) {

            if (winbase_H.GetLastError() != Winerr_H.ERROR_IO_PENDING) {
                winbase_H.CloseHandle(overlapped.hEvent);
                if (Winbase_H.INVALID_HANDLE_VALUE.equals(hFile)) {
                    return -1;
                } else {
                    throw new InterruptedIOException("Error readBytes(GetLastError):" + winbase_H.GetLastError());
                }
            }

            //overlapped path
            if (synchapi_H.WaitForSingleObject(overlapped.hEvent, Winbase_H.INFINITE) != Winbase_H.WAIT_OBJECT_0) {
                winbase_H.CloseHandle(overlapped.hEvent);
                if (Winbase_H.INVALID_HANDLE_VALUE.equals(hFile)) {
                    return -1;
                } else {
                    throw new InterruptedIOException("Error readBytes (WaitForSingleObject)");
                }
            }

        }

        LPDWORD dwBytesRead = LPDWORD.ofValue(0);

        if (!ioapiset_H.GetOverlappedResult(hFile, overlapped, dwBytesRead, false)) {
            winbase_H.CloseHandle(overlapped.hEvent);
            if (Winbase_H.INVALID_HANDLE_VALUE.equals(hFile)) {
                return -1;
            } else {
                InterruptedIOException iioe = new InterruptedIOException("Error readBytes (GetOverlappedResult)");
                iioe.bytesTransferred = (int) dwBytesRead.value;
                throw iioe;
            }
        }

        winbase_H.CloseHandle(overlapped.hEvent);

        b.limit(b.position() + (int) dwBytesRead.value);

        if (dwBytesRead.value > 0) {
            //Success
            return (int) dwBytesRead.value;
        } else if (dwBytesRead.value == 0) {
            if (Winbase_H.INVALID_HANDLE_VALUE.equals(hFile)) {
                return -1;
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

        OVERLAPPED overlapped = OVERLAPPED.newOLWithUnionPointer();
        overlapped.hEvent = synchapi_H.CreateEventW(null, true, false, null);
        if (!fileapi_H.WriteFile(hFile, b, b.remaining(), null, overlapped)) {

            if (winbase_H.GetLastError() != Winerr_H.ERROR_IO_PENDING) {
                winbase_H.CloseHandle(overlapped.hEvent);
                if (Winbase_H.INVALID_HANDLE_VALUE.equals(hFile)) {
                    throw new InterruptedIOException(PORT_IS_CLOSED);
                } else {
                    throw new InterruptedIOException("Error writeBytes (GetLastError): " + winbase_H.GetLastError());
                }
            }

            if (synchapi_H.WaitForSingleObject(overlapped.hEvent, Winbase_H.INFINITE) != Winbase_H.WAIT_OBJECT_0) {
                winbase_H.CloseHandle(overlapped.hEvent);
                if (Winbase_H.INVALID_HANDLE_VALUE.equals(hFile)) {
                    throw new InterruptedIOException(PORT_IS_CLOSED);
                } else {
                    throw new InterruptedIOException("Error writeBytes (WaitForSingleObject): " + winbase_H.GetLastError());
                }
            }

        }

        LPDWORD dwBytesWritten = LPDWORD.ofValue(0);
        if (!ioapiset_H.GetOverlappedResult(hFile, overlapped, dwBytesWritten, false)) {
            winbase_H.CloseHandle(overlapped.hEvent);
            if (Winbase_H.INVALID_HANDLE_VALUE.equals(hFile)) {
                InterruptedIOException iioe = new InterruptedIOException(PORT_IS_CLOSED);
                iioe.bytesTransferred = (int) dwBytesWritten.value;
                throw iioe;
            } else {
                InterruptedIOException iioe = new InterruptedIOException("Error writeBytes (GetOverlappedResult)");
                iioe.bytesTransferred = (int) dwBytesWritten.value;
                throw iioe;
            }
        }

        winbase_H.CloseHandle(overlapped.hEvent);
        if (dwBytesWritten.value != b.remaining()) {
            if (Winbase_H.INVALID_HANDLE_VALUE.equals(hFile)) {
                InterruptedIOException iioe = new InterruptedIOException(PORT_IS_CLOSED);
                iioe.bytesTransferred = (int) dwBytesWritten.value;
                throw iioe;
            } else {
                if (winbase_H.GetLastError() == Winerr_H.ERROR_IO_PENDING) {
                    TimeoutIOException tioe = new TimeoutIOException();
                    tioe.bytesTransferred = (int) dwBytesWritten.value;
                    throw tioe;
                } else {
                    InterruptedIOException iioe = new InterruptedIOException("Error writeBytes too view written");
                    iioe.bytesTransferred = (int) dwBytesWritten.value;
                    throw iioe;
                }
            }
        }

    //Success
        //TODO fix this for errors
        b.limit(b.position() + (int) dwBytesWritten.value);
        return (int) dwBytesWritten.value;
    }

}
