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
package de.ibapl.spsw.jnhwprovider;

import de.ibapl.jnhw.common.references.IntRef;
import de.ibapl.jnhw.common.exception.NativeErrorException;
import de.ibapl.jnhw.common.memory.AbstractNativeMemory.SetMem;
import de.ibapl.jnhw.libloader.LoadState;
import de.ibapl.jnhw.util.winapi.LibJnhwWinApiLoader;
import static de.ibapl.jnhw.winapi.Fileapi.CreateFileW;
import static de.ibapl.jnhw.winapi.Fileapi.FlushFileBuffers;
import static de.ibapl.jnhw.winapi.Fileapi.OPEN_EXISTING;
import static de.ibapl.jnhw.winapi.Fileapi.ReadFile;
import static de.ibapl.jnhw.winapi.Fileapi.WriteFile;
import de.ibapl.jnhw.winapi.Handleapi;
import static de.ibapl.jnhw.winapi.Handleapi.CloseHandle;
import de.ibapl.jnhw.winapi.Ioapiset;
import static de.ibapl.jnhw.winapi.Ioapiset.GetOverlappedResult;
import de.ibapl.jnhw.winapi.Minwinbase.OVERLAPPED;
import de.ibapl.jnhw.winapi.WinDef.LPBYTE;
import de.ibapl.jnhw.winapi.WinDef.PHKEY;
import de.ibapl.jnhw.winapi.Synchapi;
import static de.ibapl.jnhw.winapi.Synchapi.CreateEventW;
import static de.ibapl.jnhw.winapi.Synchapi.WaitForSingleObject;
import static de.ibapl.jnhw.winapi.Winbase.CLRBREAK;
import static de.ibapl.jnhw.winapi.Winbase.CLRDTR;
import static de.ibapl.jnhw.winapi.Winbase.CLRRTS;
import de.ibapl.jnhw.winapi.Winbase.COMMTIMEOUTS;
import de.ibapl.jnhw.winapi.Winbase.COMSTAT;
import static de.ibapl.jnhw.winapi.Winbase.ClearCommBreak;
import static de.ibapl.jnhw.winapi.Winbase.ClearCommError;
import de.ibapl.jnhw.winapi.Winbase.DCB;
import static de.ibapl.jnhw.winapi.Winbase.EVENPARITY;
import static de.ibapl.jnhw.winapi.Winbase.EscapeCommFunction;
import static de.ibapl.jnhw.winapi.Winbase.FILE_FLAG_OVERLAPPED;
import static de.ibapl.jnhw.winapi.Winbase.GetCommModemStatus;
import static de.ibapl.jnhw.winapi.Winbase.GetCommState;
import static de.ibapl.jnhw.winapi.Winbase.GetCommTimeouts;
import static de.ibapl.jnhw.winapi.Winbase.INFINITE;
import static de.ibapl.jnhw.winapi.Winbase.MARKPARITY;
import static de.ibapl.jnhw.winapi.Winbase.MS_CTS_ON;
import static de.ibapl.jnhw.winapi.Winbase.MS_DSR_ON;
import static de.ibapl.jnhw.winapi.Winbase.MS_RING_ON;
import static de.ibapl.jnhw.winapi.Winbase.MS_RLSD_ON;
import static de.ibapl.jnhw.winapi.Winbase.NOPARITY;
import static de.ibapl.jnhw.winapi.Winbase.ODDPARITY;
import static de.ibapl.jnhw.winapi.Winbase.ONE5STOPBITS;
import static de.ibapl.jnhw.winapi.Winbase.ONESTOPBIT;
import static de.ibapl.jnhw.winapi.Winbase.RTS_CONTROL_DISABLE;
import static de.ibapl.jnhw.winapi.Winbase.RTS_CONTROL_HANDSHAKE;
import static de.ibapl.jnhw.winapi.Winbase.SETBREAK;
import static de.ibapl.jnhw.winapi.Winbase.SETDTR;
import static de.ibapl.jnhw.winapi.Winbase.SETRTS;
import static de.ibapl.jnhw.winapi.Winbase.SPACEPARITY;
import static de.ibapl.jnhw.winapi.Winbase.SetCommBreak;
import static de.ibapl.jnhw.winapi.Winbase.SetCommState;
import static de.ibapl.jnhw.winapi.Winbase.SetCommTimeouts;
import static de.ibapl.jnhw.winapi.Winbase.TWOSTOPBITS;
import static de.ibapl.jnhw.winapi.Winbase.WAIT_OBJECT_0;
import de.ibapl.jnhw.winapi.Winerror;
import static de.ibapl.jnhw.winapi.Winerror.ERROR_ACCESS_DENIED;
import static de.ibapl.jnhw.winapi.Winerror.ERROR_FILE_NOT_FOUND;
import static de.ibapl.jnhw.winapi.Winerror.ERROR_GEN_FAILURE;
import static de.ibapl.jnhw.winapi.Winerror.ERROR_INVALID_PARAMETER;
import static de.ibapl.jnhw.winapi.Winerror.ERROR_IO_PENDING;
import static de.ibapl.jnhw.winapi.Winnt.GENERIC_READ;
import static de.ibapl.jnhw.winapi.Winnt.GENERIC_WRITE;
import de.ibapl.jnhw.winapi.Winnt.HANDLE;
import static de.ibapl.jnhw.winapi.Winnt.KEY_READ;
import de.ibapl.jnhw.winapi.Winnt.LPWSTR;
import static de.ibapl.jnhw.winapi.Winnt.MAXDWORD;
import de.ibapl.jnhw.winapi.Winreg;
import static de.ibapl.jnhw.winapi.Winreg.HKEY_LOCAL_MACHINE;
import static de.ibapl.jnhw.winapi.Winreg.RegEnumValueW;
import static de.ibapl.jnhw.winapi.Winreg.RegOpenKeyExW;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import static de.ibapl.spsw.api.SerialPortSocket.PORT_IS_OPEN;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;
import de.ibapl.spsw.api.TimeoutIOException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.ref.Cleaner;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GenericWinSerialPortSocket extends StreamSerialPortSocket<GenericWinSerialPortSocket> {

    public final static Cleaner CLEANER = Cleaner.create();

    /**
     * Cleanup whats left over if the port was not closed properly
     */
    static class HFileCleaner implements Runnable {

        HANDLE hFile = Handleapi.INVALID_HANDLE_VALUE;
        HANDLE readEvent = Handleapi.INVALID_HANDLE_VALUE;
        HANDLE writeEvent = Handleapi.INVALID_HANDLE_VALUE;

        @Override
        public void run() {
            if (readEvent.isNot_INVALID_HANDLE_VALUE()) {
                try {
                    CloseHandle(readEvent);
                } catch (NativeErrorException nee) {
                    LOG.log(Level.SEVERE, "can't properly close readEvent " + nee.errno, nee);
                }
            }
            if (writeEvent.isNot_INVALID_HANDLE_VALUE()) {
                try {
                    CloseHandle(writeEvent);
                } catch (NativeErrorException nee) {
                    LOG.log(Level.SEVERE, "can't properly close writeEvent " + nee.errno, nee);
                }
            }
            if (hFile.isNot_INVALID_HANDLE_VALUE()) {
                try {
                    Ioapiset.CancelIo(hFile);
                } catch (NativeErrorException nee) {
                    LOG.log(Level.SEVERE, "can't Cancel IO on fd " + nee.errno, nee);
                }
                try {
                    CloseHandle(hFile);
                } catch (NativeErrorException nee) {
                    LOG.log(Level.SEVERE, "can't properly close fd " + nee.errno, nee);
                }
            }
        }

    }

    private final static Logger LOG = Logger.getLogger(GenericWinSerialPortSocket.class.getCanonicalName());
    private volatile HANDLE hFile = HANDLE.INVALID_HANDLE_VALUE;
    private final HFileCleaner cleaner = new HFileCleaner();
    private final Object readLock = new Object();
    private final Object writeLock = new Object();
    private final OVERLAPPED readOverlapped = new OVERLAPPED();
    private final OVERLAPPED writeOverlapped = new OVERLAPPED();
    private HANDLE readEvent = HANDLE.INVALID_HANDLE_VALUE;
    private HANDLE writeEvent = HANDLE.INVALID_HANDLE_VALUE;
    private ExecutorService executor;

    private void setReadEvent(HANDLE readEvent) {
        this.readEvent = readEvent;
        this.readOverlapped.hEvent(readEvent);
    }

    private void setWriteEvent(HANDLE writeEvent) {
        this.writeEvent = writeEvent;
        this.writeOverlapped.hEvent(writeEvent);
    }

    public static List<String> getWindowsBasedPortNames() {
        if (LoadState.SUCCESS != LibJnhwWinApiLoader.touch()) {
            throw new RuntimeException("Could not load native lib", LibJnhwWinApiLoader.getLoadResult().loadError);
        }
        LinkedList<String> result = new LinkedList<>();

        PHKEY phkResult = new PHKEY();
        String lpSubKey = "HARDWARE\\DEVICEMAP\\SERIALCOMM\\";
        try {
            RegOpenKeyExW(HKEY_LOCAL_MACHINE, lpSubKey, 0, KEY_READ, phkResult);
        } catch (NativeErrorException nee) {
            throw new RuntimeException("Could not open registry errorCode: " + nee.errno, nee);
        }
        int dwIndex = 0;
        LPWSTR lpValueName = new LPWSTR(256, SetMem.DO_NOT_SET);
        LPBYTE lpData = new LPBYTE(256, SetMem.DO_NOT_SET);
        IntRef lpType = new IntRef();
        boolean collecting = true;
        do {
            lpData.clear();
            try {
                long errorCode = RegEnumValueW(phkResult.dereference(), dwIndex, lpValueName, lpType, lpData);
                if (errorCode == Winerror.ERROR_SUCCESS) {
                    result.add(LPBYTE.getUnicodeString(lpData, true));
                    dwIndex++;
                    lpValueName.clear();
                } else if (errorCode == Winerror.ERROR_NO_MORE_ITEMS) {
                    collecting = false;
                } else {
                    throw new RuntimeException("Should never happen! Unknown Error in getWindowsBasedPortNames RegEnumValueW errorCode: " + errorCode);
                }
            } catch (NativeErrorException nee) {
                throw new RuntimeException("Unknown Error in getWindowsBasedPortNames RegEnumValueW errorCode: " + nee.errno);
            }
        } while (collecting);
        try {
            Winreg.RegCloseKey(phkResult.dereference());
        } catch (NativeErrorException nee) {
        }
        return result;
    }

    GenericWinSerialPortSocket(String portName) throws IOException {
        this(portName, null, null, null, null, null);
    }

    GenericWinSerialPortSocket(String portName, Speed speed, DataBits dataBits, StopBits stopBits, Parity parity, Set<FlowControl> flowControls) throws IOException {
        super(portName);
        if (LoadState.SUCCESS != LibJnhwWinApiLoader.touch()) {
            throw new RuntimeException("Could not load native lib", LibJnhwWinApiLoader.getLoadResult().loadError);
        }
        open(speed, dataBits, stopBits, parity, flowControls);
    }

    public GenericWinSerialPortSocket(String portName, ExecutorService executor) throws IOException {
        this(portName);
        this.executor = executor;
    }

    public GenericWinSerialPortSocket(String portName, Speed speed, DataBits dataBits, StopBits stopBits, Parity parity, Set<FlowControl> flowControls, ExecutorService executor) throws IOException {
        this(portName, speed, dataBits, stopBits, parity, flowControls);
        this.executor = executor;
    }

    private IOException createClosedOrNativeException(int errno, String formatString, Object... args) {
        if (hFile.isNot_INVALID_HANDLE_VALUE()) {
            return new IOException(String.format("Native port error on %s, \"%d\" %s", portName, errno,
                    String.format(formatString, args)));
        } else {
            return new IOException(PORT_IS_CLOSED);
        }
    }

    /**
     * Create and fill aDCB structure
     *
     */
    private DCB getDCB() throws IOException {
        DCB result = new DCB(SetMem.TO_0x00);//TODO needed to clear mem?
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
        if (dcb.fRtsControl() == RTS_CONTROL_HANDSHAKE) {
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
        COMSTAT result = new COMSTAT(SetMem.TO_0x00); //TODO need to clear mem?

        try {
            ClearCommError(hFile, lpErrors, result);
            return result;
        } catch (NativeErrorException nee) {
            throw createClosedOrNativeException(nee.errno, "native call ClearCommError lpError 0x%08x", lpErrors.value);
        }
    }

    private COMMTIMEOUTS getCOMMTIMEOUTS() throws IOException {
        COMMTIMEOUTS result = new COMMTIMEOUTS(SetMem.TO_0x00);//TODO need to clear mem?
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
        final COMMTIMEOUTS lpCommTimeouts = getCOMMTIMEOUTS();
        if ((lpCommTimeouts.ReadIntervalTimeout() & lpCommTimeouts.ReadTotalTimeoutMultiplier() & MAXDWORD) == MAXDWORD) {
            return 0;
        } else {
            final int result = (int) lpCommTimeouts.ReadIntervalTimeout();
            if (result < 0) {
                throw new RuntimeException("COMMTIMEOUTS.ReadIntervalTimeout overflow from long to int occured");
            }
            return result;
        }
    }

    @Override
    public int getOutBufferBytesCount() throws IOException {
        return getCOMSTAT().cbOutQue();
    }

    @Override
    public int getOverallReadTimeout() throws IOException {
        final int result = (int) getCOMMTIMEOUTS().ReadTotalTimeoutConstant();
        if (result < 0) {
            throw new RuntimeException("COMMTIMEOUTS.ReadTotalTimeoutConstant overflow from long to int occured");
        }
        return result;
    }

    @Override
    public int getOverallWriteTimeout() throws IOException {
        final int result = (int) getCOMMTIMEOUTS().WriteTotalTimeoutConstant();
        if (result < 0) {
            throw new RuntimeException("COMMTIMEOUTS.WriteTotalTimeoutConstant overflow from long to int occured");
        }
        return result;
    }

    @Override
    public Parity getParity() throws IOException {
        return getParity(getDCB().Parity());
    }

    private Parity getParity(byte parity) throws IOException {
        if (parity == NOPARITY) {
            return Parity.NONE;
        } else if (parity == ODDPARITY) {
            return Parity.ODD;
        } else if (parity == EVENPARITY) {
            return Parity.EVEN;
        } else if (parity == MARKPARITY) {
            return Parity.MARK;
        } else if (parity == SPACEPARITY) {
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
        if (stopBits == ONESTOPBIT) {
            return StopBits.SB_1;
        } else if (stopBits == ONE5STOPBITS) {
            return StopBits.SB_1_5;
        } else if (stopBits == TWOSTOPBITS) {
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
    protected void implCloseChannel() throws IOException {
        super.implCloseChannel();
        final HANDLE _hFile = hFile;
        hFile = HANDLE.INVALID_HANDLE_VALUE;
// if only ReadIntervalTimeout is set and port is closed during pending read the read operation will hang forever...

        try {
            Ioapiset.CancelIo(_hFile);
        } catch (NativeErrorException nee) {
            if (nee.errno != Winerror.ERROR_NOT_FOUND) {
                hFile = _hFile;
                throw new IOException("Can't cancel IO for closing", nee);
            }
            //no-op we dont care
        }

        try {
            CloseHandle(_hFile);
            cleaner.hFile = HANDLE.INVALID_HANDLE_VALUE;
        } catch (NativeErrorException nee) {
            throw new IOException("Can't close port", nee);
        }

        try {
            final HANDLE hEvent = readEvent;
            setReadEvent(HANDLE.INVALID_HANDLE_VALUE);
            CloseHandle(hEvent);
            cleaner.readEvent = HANDLE.INVALID_HANDLE_VALUE;
        } catch (NativeErrorException nee) {
            LOG.log(Level.SEVERE, "can't properly close readEvent " + nee.errno, nee);
        }

        try {
            final HANDLE hEvent = writeEvent;
            setWriteEvent(HANDLE.INVALID_HANDLE_VALUE);
            CloseHandle(hEvent);
            cleaner.writeEvent = HANDLE.INVALID_HANDLE_VALUE;
        } catch (NativeErrorException nee) {
            LOG.log(Level.SEVERE, "can't properly close writeEvent " + nee.errno, nee);
        }
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
        return getCommModemStatus(MS_CTS_ON);
    }

    @Override
    public boolean isDCD() throws IOException {
        return getCommModemStatus(MS_RLSD_ON);
    }

    @Override
    public boolean isDSR() throws IOException {
        return getCommModemStatus(MS_DSR_ON);
    }

    @Override
    public boolean isRI() throws IOException {
        return getCommModemStatus(MS_RING_ON);
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
                    if (dcb.StopBits() == TWOSTOPBITS) {
                        //Fix stopBits
                        dcb.StopBits(ONE5STOPBITS);
                    }
                    break;
                case DB_6: //fall trough
                case DB_7: //fall trough
                case DB_8:
                    if (dcb.StopBits() == ONE5STOPBITS) {
                        //Fix stopBits
                        dcb.StopBits(TWOSTOPBITS);
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
                    dcb.StopBits(ONESTOPBIT);
                    break;
                case SB_1_5:
                    if (dcb.ByteSize() == 5) {
                        dcb.StopBits(ONE5STOPBITS);
                    } else {
                        throw new IllegalArgumentException("setParams setStopBits to 1.5: only for 5 dataBits 1.5 stoppbits are supported");
                    }
                    break;
                case SB_2:
                    if (dcb.ByteSize() == 5) {
                        throw new IllegalArgumentException("setParams setStopBits to 2: 5 dataBits only 1.5 stoppbits are supported");
                    } else {
                        dcb.StopBits(TWOSTOPBITS);
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
                    dcb.Parity(NOPARITY); // switch parity input checking off
                    break;
                case ODD:
                    dcb.Parity(ODDPARITY);
                    break;
                case EVEN:
                    dcb.Parity(EVENPARITY);
                    break;
                case MARK:
                    dcb.Parity(MARKPARITY);
                    break;
                case SPACE:
                    dcb.Parity(SPACEPARITY);
                    break;
                default:
                    throw new IllegalArgumentException("setParams Wrong Parity");
            }

        }

        //FlowControl
        if (flowControls != null) {
            dcb.fRtsControl(RTS_CONTROL_DISABLE);
            dcb.fOutxCtsFlow(false);
            dcb.fOutX(false);
            dcb.fInX(false);
            if (flowControls.contains(FlowControl.RTS_CTS_IN)) {
                dcb.fRtsControl(RTS_CONTROL_HANDSHAKE);
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
            switch (nee.errno) {
                case ERROR_INVALID_PARAMETER:
                    throw new IllegalArgumentException("setParams: Wrong params\n GetLastError() == ERROR_INVALID_PARAMETER");
                case ERROR_GEN_FAILURE:
                    throw new IllegalArgumentException("setParams: Wrong params\n GetLastError() == ERROR_GEN_FAILURE");
                default:
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

    private void open(Speed speed, DataBits dataBits, StopBits stopBits, Parity parity, Set<FlowControl> flowControls)
            throws IOException {

        if (hFile.isNot_INVALID_HANDLE_VALUE()) {
            throw new IOException(PORT_IS_OPEN);
        }

        final String portFullName = "\\\\.\\" + portName;

        try {
            hFile = CreateFileW(portFullName,
                    GENERIC_READ | GENERIC_WRITE,
                    0,
                    null,
                    OPEN_EXISTING,
                    FILE_FLAG_OVERLAPPED,
                    null);

        } catch (NativeErrorException nee) {

            if (nee.errno == ERROR_ACCESS_DENIED) {
                throw new IOException(String.format("Port is busy: \"%s\"", portName));
            } else if (nee.errno == ERROR_FILE_NOT_FOUND) {
                throw new IOException(String.format("Port not found: \"%s\"", portName));
            } else {
                throw new IOException(String.format("Open: Unknown port error %d", nee.errno));
            }
        }
        // The port is open, but maybe not configured ... setParam and getParam needs this to be set for their field access

        DCB dcb = new DCB(SetMem.TO_0x00);//TODO need to clear mem?
        try {
            GetCommState(hFile, dcb);
        } catch (NativeErrorException nee) {
            try {
                CloseHandle(hFile);
            } catch (NativeErrorException nee1) {
            }
            hFile = HANDLE.INVALID_HANDLE_VALUE;
            throw new IOException(String.format("Not a serial port: \"%s\"", portName));
        }

        //set speed etc.
        try {
            setParams(dcb, speed, dataBits, stopBits, parity, flowControls);
        } catch (Throwable t) {
            try {
                CloseHandle(hFile);
            } catch (NativeErrorException nee) {
            }
            hFile = HANDLE.INVALID_HANDLE_VALUE;
            throw t;
        }

        COMMTIMEOUTS lpCommTimeouts = new COMMTIMEOUTS(SetMem.TO_0x00);//TODO need to clear mem?
        try {
            GetCommTimeouts(hFile, lpCommTimeouts);
        } catch (NativeErrorException nee) {
            try {
                CloseHandle(hFile);
            } catch (NativeErrorException nee1) {
            }
            hFile = HANDLE.INVALID_HANDLE_VALUE;
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
            hFile = HANDLE.INVALID_HANDLE_VALUE;
            throw new IOException("Open SetCommTimeouts");
        }

        CLEANER.register(this, cleaner);
        cleaner.hFile = hFile;

        try {
            setReadEvent(CreateEventW(null, true, false, null));
            cleaner.readEvent = readEvent;
        } catch (NativeErrorException nee) {
            throw new RuntimeException("Can't create readEvent error: " + nee.errno, nee);
        }
        try {
            setWriteEvent(CreateEventW(null, true, false, null));
            cleaner.writeEvent = writeEvent;
        } catch (NativeErrorException nee) {
            throw new RuntimeException("Can't create writeEvent error: " + nee.errno, nee);
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

        boolean completed = false;
        try {
            Thread.sleep(duration);
            completed = true;
        } catch (InterruptedException ex) {
            Logger.getLogger(GenericWinSerialPortSocket.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        try {
            ClearCommBreak(hFile);
        } catch (NativeErrorException nee) {
            throw createClosedOrNativeException(nee.errno, "sendBreak ClearCommBreak");
        }
        if (!completed) {
            close();
            throw new AsynchronousCloseException();
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
        int dwFunc = value ? SETBREAK : CLRBREAK;

        try {
            EscapeCommFunction(hFile, dwFunc);
        } catch (NativeErrorException nee) {
            if (nee.errno == ERROR_INVALID_PARAMETER) {
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
        int dwFunc = value ? SETDTR : CLRDTR;

        try {
            EscapeCommFunction(hFile, dwFunc);
        } catch (NativeErrorException nee) {
            if (nee.errno == ERROR_INVALID_PARAMETER) {
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
        int dwFunc = value ? SETRTS : CLRRTS;

        try {
            EscapeCommFunction(hFile, dwFunc);
        } catch (NativeErrorException nee) {
            if (nee.errno == ERROR_INVALID_PARAMETER) {
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
        if (overallWriteTimeout < 0) {
            throw new IllegalArgumentException("setTimeouts: overallWriteTimeout must >= 0");
        }

        if (overallReadTimeout < 0) {
            throw new IllegalArgumentException("setTimeouts: overallReadTimeout must >= 0");
        }

        if (interByteReadTimeout < 0) {
            throw new IllegalArgumentException("setReadTimeouts: interByteReadTimeout must >= 0");
        }

        COMMTIMEOUTS commTimeouts = new COMMTIMEOUTS(SetMem.TO_0x00);

        if ((interByteReadTimeout == 0) && (overallReadTimeout > 0)) {
            //This fits best for wait a timeout and have no interByteReadTimeout see also getInterbyteReadTimeout for reading back
            commTimeouts.ReadIntervalTimeout(MAXDWORD);
            commTimeouts.ReadTotalTimeoutMultiplier(MAXDWORD);
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
            if (nee.errno == ERROR_INVALID_PARAMETER) {
                throw new IllegalArgumentException("setTimeouts: Wrong Timeouts");
            } else {
                throw createClosedOrNativeException(nee.errno, "setTimeouts SetCommTimeouts");
            }
        }
    }

    @Override
    public void setXOFFChar(char c) throws IOException {
        DCB dcb = new DCB(SetMem.TO_0x00);//TODO need to clear mem?

        try {
            GetCommState(hFile, dcb);
        } catch (NativeErrorException nee) {
            throw createClosedOrNativeException(nee.errno, "setXOFFChar GetCommState");
        }

        dcb.XoffChar(c);

        try {
            SetCommState(hFile, dcb);
        } catch (NativeErrorException nee) {
            if (nee.errno == ERROR_INVALID_PARAMETER) {
                throw new IllegalArgumentException("etXOFFChar: Wrong value\n GetLastError() == ERROR_INVALID_PARAMETER");
            } else if (nee.errno == ERROR_GEN_FAILURE) {
                throw new IllegalArgumentException("etXOFFChar: Wrong value\n GetLastError() == ERROR_GEN_FAILURE");
            } else {
                throw createClosedOrNativeException(nee.errno, "setXOFFChar SetCommState");
            }
        }
    }

    @Override
    public void setXONChar(char c) throws IOException {
        DCB dcb = new DCB(SetMem.TO_0x00);//TODO need to clear mem?

        try {
            GetCommState(hFile, dcb);
        } catch (NativeErrorException nee) {
            throw createClosedOrNativeException(nee.errno, "setXONChar GetCommState");
        }

        dcb.XonChar(c);

        try {
            SetCommState(hFile, dcb);
        } catch (NativeErrorException nee) {
            if (nee.errno == ERROR_INVALID_PARAMETER) {
                throw new IllegalArgumentException("setXONChar: Wrong value\n GetLastError() == ERROR_INVALID_PARAMETER");
            } else if (nee.errno == ERROR_GEN_FAILURE) {
                throw new IllegalArgumentException("setXONChar: Wrong value\n GetLastError() == ERROR_GEN_FAILURE");
            } else {
                throw createClosedOrNativeException(nee.errno, "setXONChar SetCommState");
            }
        }
    }

    @Override
    public void drainOutputBuffer() throws IOException {
        //make this blocking IO interruptable
        boolean completed = false;
        try {
            begin();
            try {
                FlushFileBuffers(hFile);
                completed = true;
            } catch (NativeErrorException nee) {
                completed = true;
                throw createClosedOrNativeException(nee.errno, "drainOutputBuffer");
            }
        } finally {
            end(completed);
        }
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        synchronized (readLock) {
            if (!dst.hasRemaining()) {
                return 0;
            }

            try {
                Synchapi.ResetEvent(readEvent);
            } catch (NativeErrorException nee) {
                throw new IOException("Error read => ResetEvent: " + nee.errno, nee);
            }

            try {
                ReadFile(hFile, dst, readOverlapped);
            } catch (NativeErrorException nee) {
                if (nee.errno != ERROR_IO_PENDING) {
                    if (hFile.isNot_INVALID_HANDLE_VALUE()) {
                        throw new InterruptedIOException("Error readBytes(GetLastError):" + nee.errno);
                    } else {
                        throw new AsynchronousCloseException();
                    }
                }
            }
            //make this blocking IO interruptable
            boolean completed = false;
            try {
                begin();

                //overlapped path
                try {
                    final long waitResult = WaitForSingleObject(readEvent, INFINITE);
                    if (waitResult != WAIT_OBJECT_0) {
                        if (hFile.isNot_INVALID_HANDLE_VALUE()) {
                            completed = true;
                            throw new InterruptedIOException("Error readBytes (WaitForSingleObject): " + waitResult);
                        } else {
                            completed = true;
                            throw new AsynchronousCloseException();
                        }
                    }
                } catch (NativeErrorException nee1) {
                    completed = true;
                    throw new RuntimeException("NativeError readBytes (WaitForSingleObject)", nee1);
                }

                int dwBytesRead = 0;

                try {
                    dwBytesRead = GetOverlappedResult(hFile, readOverlapped, dst, false);
                } catch (NativeErrorException nee) {
                    if (hFile.isNot_INVALID_HANDLE_VALUE()) {
                        InterruptedIOException iioe = new InterruptedIOException("Error readBytes (GetOverlappedResult)");
                        completed = true;
                        throw iioe;
                    } else {
                        completed = true;
                        throw new AsynchronousCloseException();
                    }
                }

                if (dwBytesRead > 0) {
                    //Success
                    completed = true;
                    return dwBytesRead;
                } else if (dwBytesRead == 0) {
                    if (hFile.isNot_INVALID_HANDLE_VALUE()) {
                        TimeoutIOException tioe = new TimeoutIOException("read dwBytesRead == 0");
                        tioe.bytesTransferred = dwBytesRead;
                        completed = true;
                        throw tioe;
                    } else {
                        completed = true;
                        throw new AsynchronousCloseException();
                    }
                } else {
                    completed = true;
                    throw new InterruptedIOException("Should never happen! readBytes dwBytes < 0");
                }

            } finally {
                end(completed);
            }
        }
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        synchronized (writeLock) {

            if (!src.hasRemaining()) {
                return 0;
            }

            try {
                Synchapi.ResetEvent(writeEvent);
            } catch (NativeErrorException nee) {
                throw new IOException("Error write => ResetEvent: " + nee.errno, nee);
            }

            try {
                WriteFile(hFile, src, writeOverlapped);
            } catch (NativeErrorException nee) {

                if (nee.errno != ERROR_IO_PENDING) {
                    if (hFile.isNot_INVALID_HANDLE_VALUE()) {
                        throw new InterruptedIOException("Error writeBytes (GetLastError): " + nee.errno);
                    } else {
                        throw new AsynchronousCloseException();
                    }
                }
            }

            //make this blocking IO interruptable
            boolean completed = false;
            try {
                begin();

                try {
                    final long waitResult = WaitForSingleObject(writeEvent, INFINITE);

                    if (waitResult != WAIT_OBJECT_0) {
                        if (hFile.isNot_INVALID_HANDLE_VALUE()) {
                            completed = true;
                            throw new InterruptedIOException("Error writeBytes (WaitForSingleObject): " + waitResult);
                        } else {
                            completed = true;
                            throw new AsynchronousCloseException();
                        }
                    }

                } catch (NativeErrorException nee1) {
                    completed = true;
                    throw new RuntimeException("NativeError writeBytes (WaitForSingleObject)", nee1);
                }

                int dwBytesWritten = 0;
                try {
                    dwBytesWritten = GetOverlappedResult(hFile, writeOverlapped, src, false);
                } catch (NativeErrorException nee) {
                    if (hFile.isNot_INVALID_HANDLE_VALUE()) {
                        InterruptedIOException iioe = new InterruptedIOException("Error writeBytes (GetOverlappedResult) errno: " + nee.errno);
                        completed = true;
                        throw iioe;
                    } else {
                        completed = true;
                        throw new AsynchronousCloseException();
                    }
                }

                if (src.hasRemaining()) {
                    if (hFile.isNot_INVALID_HANDLE_VALUE()) {
//                if (winbase_H.GetLastError() == Winerr_H.ERROR_IO_PENDING) {
//                    TimeoutIOException tioe = new TimeoutIOException();
//                    tioe.bytesTransferred = (int) dwBytesWritten.value;
//                    throw tioe;
//                } else {
                        InterruptedIOException iioe = new InterruptedIOException("Error writeBytes too view written");
                        iioe.bytesTransferred = dwBytesWritten;
                        completed = true;
                        throw iioe;
//                }
                    } else {
                        completed = true;
                        throw new AsynchronousCloseException();
                    }
                }

                //Success
                completed = true;
                return dwBytesWritten;
            } finally {
                end(completed);
            }
        }
    }

}
