package de.ibapl.spsw;

/*
 * #%L
 * SPSW Java
 * %%
 * Copyright (C) 2009 - 2014 atmodem4j
 * %%
 * atmodem4j - A serial port socket wrapper- http://atmodem4j.sourceforge.net/
 * Copyright (C) 2009-2014, atmodem4j.sf.net, and individual contributors as indicated
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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.EnumSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author scream3r
 */
public abstract class AbstractSerialPortSocket implements SerialPortSocket {

    protected final static Logger LOG = Logger.getLogger("de.ibapl.spsw");

    static final int PORT_MODE_UNCHANGED = 0;
    static final int PORT_MODE_RAW = 0x01;

    static final int FLOW_CONTROL_NONE = 0;
    static final int FLOW_CONTROL_RTS_CTS_IN = 0x01;
    static final int FLOW_CONTROL_RTS_CTS_OUT = 0x02;
    static final int FLOW_CONTROL_XON_XOFF_IN = 0x04;
    static final int FLOW_CONTROL_XON_XOFF_OUT = 0x08;

    static final int STOP_BITS_1 = 0;
    static final int STOP_BITS_1_5 = 0x01;
    static final int STOP_BITS_2 = 0x02;

    static final int PARITY_NONE = 0;
    static final int PARITY_ODD = 0x01;
    static final int PARITY_EVEN = 0x02;
    static final int PARITY_MARK = 0x03;
    static final int PARITY_SPACE = 0x04;

    private static boolean libLoaded;
    private static String libName;
    public final static String SPSW_PROPERTIES = "de/ibapl/spsw/spsw.properties";

    public static boolean isLibLoaded() {
        return libLoaded;
    }

    public static String getLibName() {
        return libName;
    }

    public static String getArch() {
        String osArch = System.getProperty("os.arch");
        switch (getOsName()) {
            case "linux":
                if ("arm".equals(osArch)) {
                    String floatStr = "sf";
                    if (System.getProperty("java.library.path").contains("gnueabihf") || System.getProperty("java.library.path").contains("armhf")) {
                        floatStr = "hf";
                    } else {
                        LOG.log(Level.WARNING, "Can't find hardware|software floating point in libpath try readelf");
                        try {
                            Process readelfProcess = Runtime.getRuntime().exec("readelf -A /proc/self/exe");
                            try (BufferedReader reader = new BufferedReader(new InputStreamReader(readelfProcess.getInputStream()))) {
                                String buffer;
                                while ((buffer = reader.readLine()) != null && !buffer.isEmpty()) {
                                    if (buffer.toLowerCase().contains("Tag_ABI_VFP_args".toLowerCase())) {
                                        floatStr = "hf";
                                        break;
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            LOG.severe("Please install binutils to detect architecture ... use hf as default");
                            floatStr = "hf";
                            //Do nothing
                        }
                    }
                    return osArch + floatStr;
                } else {
                    return osArch;
                }
            default:
                return osArch;
        }

    }

    public static String getOsName() {
        switch (System.getProperty("os.name")) {
            case "Linux":
                return "linux";
            case "Mac OS":
                throw new UnsupportedOperationException("Mac OS is currently not supported yet");
            case "Mac OS X":
                throw new UnsupportedOperationException("Mac OS X is currently not supported yet");
            case "Windows 95":
                return "windows";
            case "Windows 98":
                return "windows";
            case "Windows Me":
                return "windows";
            case "Windows NT":
                return "windows";
            case "Windows 2000":
                return "windows";
            case "Windows XP":
                return "windows";
            case "Windows 2003":
                return "windows";
            case "Windows Vista":
                return "windows";
            case "Windows 2008":
                return "windows";
            case "Windows 7":
                return "windows";
            case "Windows 8":
                return "windows";
            case "Windows 2012":
                return "windows";
            case "Windows CE":
                throw new UnsupportedOperationException("Windows CE is currently not supported yet");
            case "OS/2":
                throw new UnsupportedOperationException("OS/2 is currently not supported yet");
            case "MPE/iX":
                throw new UnsupportedOperationException("MPE/iX is currently not supported yet");
            case "HP-UX":
                throw new UnsupportedOperationException("HP-UX is currently not supported yet");
            case "AIX":
                throw new UnsupportedOperationException("AIX is currently not supported yet");
            case "OS/390":
                throw new UnsupportedOperationException("OS/390 is currently not supported yet");
            case "FreeBSD":
                throw new UnsupportedOperationException("FreeBSD is currently not supported yet");
            case "Irix":
                throw new UnsupportedOperationException("Irix is currently not supported yet");
            case "Digital Unix":
                throw new UnsupportedOperationException("Digital Unix is currently not supported yet");
            case "NetWare 4.11":
                throw new UnsupportedOperationException("NetWare 4.11 is currently not supported yet");
            case "OSF1":
                throw new UnsupportedOperationException("OSF1 is currently not supported yet");
            case "OpenVMS":
                throw new UnsupportedOperationException("OpenVMS is currently not supported yet");
            default:
                throw new RuntimeException("Can't figure out OS: " + System.getProperty("os.name"));
        }

    }

    /**
     * Someone else has loaded the correct native lib somplace else, Or know
     * what she is doingt...
     *
     * @param libName the name of the lib
     * @return false if the lib was loaded before, false otherwise.
     */
    public static synchronized boolean yesIhaveLoadedTheNativeLibMyself(String libName) {

        if (libLoaded) {
            return false;
        }
        libLoaded = true;
        AbstractSerialPortSocket.libName = libName;
        return true;
    }

    //TODO usable LOG INFOS ...
    public static synchronized boolean loadNativeLib() {
        if (libLoaded) {
            LOG.log(Level.INFO, "Lib was Loaded");
            return false;
        }
        Properties p = new Properties();
        try {
            p.load(AbstractSerialPortSocket.class.getClassLoader().getResourceAsStream(SPSW_PROPERTIES));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Can't find spsw.properties");
            throw new RuntimeException("Can't load version information", ex);
        }

        final String rawLibName = String.format("spsw-%s-%s-%s", getOsName(), getArch(), p.getProperty("version"));
        LOG.log(Level.INFO, "Raw Libname: {0}", rawLibName);
        libName = System.mapLibraryName(rawLibName);
        LOG.log(Level.INFO, "Libname: {0}", libName);

        try {
            System.loadLibrary(libName);
            LOG.log(Level.INFO, "Lib Loaded via System.loadLibrary(\"{0}\")", libName);
            libLoaded = true;
            return true;
        } catch (Throwable t) {

        }
        try {
            String file = AbstractSerialPortSocket.class.getClassLoader().getResource(libName).getFile();
            System.load(file);
            libLoaded = true;
            libName = file;
            LOG.log(Level.INFO, "Lib Loaded via System.load(\"{0}\")", file);
            return true;
        } catch (Throwable t) {

        }

        File tmpLib = null;
        try (InputStream is = AbstractSerialPortSocket.class.getClassLoader().getResourceAsStream(libName)) {

            int splitPos = libName.indexOf(rawLibName);
            if (splitPos <= 0) {
                //ERROR
            }
            splitPos += rawLibName.length();

            tmpLib = File.createTempFile(libName.substring(0, splitPos), libName.substring(splitPos));
            tmpLib.deleteOnExit();
            try (FileOutputStream fos = new FileOutputStream(tmpLib)) {
                byte[] buff = new byte[1024];
                int i;
                while ((i = is.read(buff)) > 0) {
                    fos.write(buff, 0, i);
                }
                fos.flush();
            }

            System.load(tmpLib.getAbsolutePath());
            libLoaded = true;
            libName = tmpLib.getAbsolutePath();
            LOG.log(Level.INFO, "Lib Loaded via System.load(\"{0}\")", tmpLib.getAbsolutePath());
            return true;
        } catch (Throwable t) {
            LOG.log(Level.SEVERE, "Giving up cant load the lib \"{0}\" List System Properties", tmpLib.getAbsolutePath());
            StringBuilder sb = new StringBuilder();
            for (String name : System.getProperties().stringPropertyNames()) {
                sb.append("\t").append(name).append(" = ").append(System.getProperty(name)).append("\n");
            }
            LOG.log(Level.SEVERE, "System.properties\n{0}", new Object[]{sb.toString()});
            LOG.log(Level.SEVERE, "Giving up cant load the lib \"" + tmpLib.getAbsolutePath() + "\" ", t);
            throw new RuntimeException("Can't load spsw native lib, giving up!", t);
        }
    }

    protected SerialInputStream is;
    protected SerialOutputStream os;

    private String portName;
    private boolean open = false;

    public AbstractSerialPortSocket(String portName) {
        if (portName == null) {
            throw new IllegalArgumentException("portname must not null!");
        }
        if (!libLoaded) {
            loadNativeLib();
        }
        this.portName = portName;
    }

    @Override
    public boolean isClosed() {
        return !open;
    }

    @Override
    public synchronized void close() throws IOException {
        open = false;
        //Make streams closed, so that they can not be reopend.
        if (is != null) {
            is.open = false;
            is = null;
        }
        if (os != null) {
            os.open = false;
            os = null;
        }
        close0();
    }

    @Override
    public synchronized InputStream getInputStream() throws IOException {
        if (!isOpen()) {
            throw new SerialPortException(portName, "Port is not opend");
        }
        InputStream result;
        try {
            result = AccessController.doPrivileged(
                    new PrivilegedExceptionAction<InputStream>() {
                        @Override
                        public InputStream run() throws IOException {
                            if (is == null) {
                                is = new SerialInputStream();
                                is.open = isOpen();
                            }
                            return is;
                        }
                    });
        } catch (java.security.PrivilegedActionException e) {
            throw (IOException) e.getException();
        }
        return result;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (!isOpen()) {
            throw new SerialPortException(portName, "Port is not opend");
        }
        OutputStream result;
        try {
            result = AccessController.doPrivileged(
                    new PrivilegedExceptionAction<OutputStream>() {
                        @Override
                        public OutputStream run() throws IOException {
                            if (os == null) {
                                os = new SerialOutputStream();
                                os.open = isOpen();
                            }
                            return os;
                        }
                    });
        } catch (java.security.PrivilegedActionException e) {
            throw (IOException) e.getException();
        }
        return result;
    }

    @Override
    public String getPortName() {
        return portName;
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public synchronized void openAsIs() throws IOException {
        open(portName, PORT_MODE_UNCHANGED);
        open = true;
    }

    @Override
    public synchronized void openRaw() throws IOException {
        open(portName, PORT_MODE_RAW);
        open = true;
    }

    @Override
    public synchronized void openTerminal() throws IOException {
        throw new UnsupportedOperationException("Terminal mode not yet supported");
    }

    @Override
    public synchronized void openModem() throws IOException {
        throw new UnsupportedOperationException("Modem mode not yet supported");
    }

    @Override
    public void openRaw(Baudrate baudRate, DataBits dataBits, StopBits stopBits, Parity parity, Set<FlowControl> flowControls) throws IOException {
        openRaw();
        setBaudrate(baudRate);
        setDataBits(dataBits);
        setStopBits(stopBits);
        setParity(parity);
        setFlowControl(flowControls);
    }

    @Override
    public void setFlowControl(Set<FlowControl> flowControls) throws IOException {
        int nativeValue = 0;
        for (FlowControl fc : flowControls) {
            switch (fc) {
                case RTS_CTS_IN:
                    nativeValue |= FLOW_CONTROL_RTS_CTS_IN;
                    break;
                case RTS_CTS_OUT:
                    nativeValue |= FLOW_CONTROL_RTS_CTS_OUT;
                    break;
                case XON_XOFF_IN:
                    nativeValue |= FLOW_CONTROL_XON_XOFF_IN;
                    break;
                case XON_XOFF_OUT:
                    nativeValue |= FLOW_CONTROL_XON_XOFF_OUT;
                    break;
                default:
                    throw new RuntimeException("Cant handle Flowcontrol");
            }
        }
        setFlowControl(nativeValue);
    }

    @Override
    public Set<FlowControl> getFlowControl() throws IOException {
        final int flowControl = getFlowControl0();
        Set<FlowControl> s = EnumSet.noneOf(FlowControl.class);
        if ((flowControl & FLOW_CONTROL_RTS_CTS_IN) == FLOW_CONTROL_RTS_CTS_IN) {
            s.add(FlowControl.RTS_CTS_IN);
        }
        if ((flowControl & FLOW_CONTROL_RTS_CTS_OUT) == FLOW_CONTROL_RTS_CTS_OUT) {
            s.add(FlowControl.RTS_CTS_OUT);
        }
        if ((flowControl & FLOW_CONTROL_XON_XOFF_IN) == FLOW_CONTROL_XON_XOFF_IN) {
            s.add(FlowControl.XON_XOFF_IN);
        }
        if ((flowControl & FLOW_CONTROL_XON_XOFF_OUT) == FLOW_CONTROL_XON_XOFF_OUT) {
            s.add(FlowControl.XON_XOFF_OUT);
        }
        return s;
    }

    /**
     * Open port
     *
     * @param portName name of port for opening
     * @param type
     * @throws java.io.IOException
     *
     */
    protected abstract void open(String portName, int type) throws IOException;

    /**
     * Close port
     * @throws java.io.IOException
     */
    protected abstract void close0() throws IOException;

    @Override
    @SuppressWarnings("FinalizeDeclaration")
    protected void finalize() throws Throwable {
        try {
            if (isOpen()) {
                close();
            }
        } catch (IOException e) {

        } finally {
            super.finalize();
        }
    }

    @Override
    public void setBaudrate(Baudrate baudrate) throws IOException {
        setBaudrate(baudrate.value);
    }

    @Override
    public void setDataBits(DataBits dataBits) throws IOException {
        setDataBits(dataBits.value);
    }

    @Override
    public void setStopBits(StopBits stopBits) throws IOException {
        switch (stopBits) {
            case SB_1:
                setStopBits(STOP_BITS_1);
                break;
            case SB_1_5:
                setStopBits(STOP_BITS_1_5);
                break;
            case SB_2:
                setStopBits(STOP_BITS_2);
                break;
            default:
                throw new IllegalArgumentException("Cant handle Stopbits");

        }
    }

    @Override
    public void setParity(Parity parity) throws IOException {
        switch (parity) {
            case EVEN:
                setParity(PARITY_EVEN);
                break;
            case MARK:
                setParity(PARITY_MARK);
                break;
            case NONE:
                setParity(PARITY_NONE);
                break;
            case ODD:
                setParity(PARITY_ODD);
                break;
            case SPACE:
                setParity(PARITY_SPACE);
                break;
            default:
                throw new RuntimeException("cant convert parity");
        }
    }

    @Override
    public Baudrate getBaudrate() throws IOException {
        return Baudrate.fromNative(getBaudrate0());
    }

    @Override
    public DataBits getDatatBits() throws IOException {
        return DataBits.fromNative(getDataBits0());
    }

    @Override
    public StopBits getStopBits() throws IOException {
        switch (getStopBits0()) {
            case STOP_BITS_1:
                return StopBits.SB_1;
            case STOP_BITS_1_5:
                return StopBits.SB_1_5;
            case STOP_BITS_2:
                return StopBits.SB_2;
            default:
                throw new RuntimeException("Cant handle native value: " + getStopBits0());
        }
    }

    @Override
    public Parity getParity() throws IOException {
        switch (getParity0()) {
            case PARITY_NONE:
                return Parity.NONE;
            case PARITY_ODD:
                return Parity.ODD;
            case PARITY_EVEN:
                return Parity.EVEN;
            case PARITY_MARK:
                return Parity.MARK;
            case PARITY_SPACE:
                return Parity.SPACE;
            default:
                throw new RuntimeException("Can't convert native value to parity: " + getParity0());
        }
    }

    protected abstract int readSingle() throws IOException;

    /**
     * Read data from port
     *
     * @param b the data to be written
     * @param off the start offset in the data
     * @param len the number of bytes that are written
     * @return the readed bytes
     * @exception IOException If an I/O error has occurred.
     */
    protected abstract int readBytes(byte[] b, int off, int len) throws IOException;

    protected abstract void writeSingle(int b) throws IOException;

    /**
     * Write data to port
     *
     * @param b
     * @param off the start offset in the data.
     * @param len the number of bytes to write.
     * @throws java.io.IOException
     *
     */
    protected abstract void writeBytes(byte[] b, int off, int len) throws IOException;

    protected abstract void setFlowControl(int mask) throws IOException;

    protected abstract int getFlowControl0() throws IOException;

    protected abstract void setBaudrate(int baudRate) throws IOException;

    protected abstract void setDataBits(int value) throws IOException;

    protected abstract void setStopBits(int value) throws IOException;

    protected abstract void setParity(int parity) throws IOException;

    protected abstract int getBaudrate0() throws IOException;

    protected abstract int getDataBits0() throws IOException;

    protected abstract int getStopBits0() throws IOException;

    protected abstract int getParity0() throws IOException;

    protected class SerialInputStream extends InputStream {

        private boolean open = false;

        @Override
        public void close() throws IOException {
            AbstractSerialPortSocket.this.close();
        }

        @Override
        public int read() throws IOException {
            if (open) {
                return AbstractSerialPortSocket.this.readSingle();
            } else {
                return -1;
            }
        }

        @Override
        public int read(byte b[]) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            } else if (b.length == 0) {
                return 0;
            }
            
            if (open) {
                return AbstractSerialPortSocket.this.readBytes(b, 0, b.length);
            } else {
                return -1;
            }
        }

        @Override
        public int read(byte b[], int off, int len) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            } else if (off < 0 || len < 0 || len > b.length - off) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return 0;
            }
            
            if (open) {
                return AbstractSerialPortSocket.this.readBytes(b, off, len);
            } else {
                return -1;
            }
        }

        @Override
        public int available() throws IOException {
            if (!open) {
                throw new SerialPortException(portName, "Port is closed");
            } else {
                return AbstractSerialPortSocket.this.getInBufferBytesCount();
            }
        }

    }

    protected class SerialOutputStream extends OutputStream {

        private boolean open;

        @Override
        public void close() throws IOException {
            AbstractSerialPortSocket.this.close();
        }

        @Override
        public void write(int b) throws IOException {
            if (open) {
                AbstractSerialPortSocket.this.writeSingle(b);
            } else {
                throw new SerialPortException(portName, "port is closed");
            }
        }

        @Override
        public void write(byte b[]) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            } else if (b.length == 0) {
                return;
            }

            if (open) {
                AbstractSerialPortSocket.this.writeBytes(b, 0, b.length);
            } else {
                throw new SerialPortException(portName, "port is closed");
            }
        }

        @Override
        public void write(byte b[], int off, int len) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            } else if ((off < 0) || (off > b.length) || (len < 0)
                    || ((off + len) > b.length) || ((off + len) < 0)) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return;
            }

            if (open) {
                AbstractSerialPortSocket.this.writeBytes(b, off, len);
            } else {
                throw new SerialPortException(portName, "port is closed");
            }
        }
    }

    @Override
    public String toString() {
        try {
            return String.format("[portname=%s, baudrate= %s, dataBits= %s, stopBits= %s, parity= %s, flowControl= %s]", getPortName(), getBaudrate(), getDatatBits(), getStopBits(), getParity(), getFlowControl());
        } catch (IOException e) {
            return "Internal Error " + e;
        }
    }
}
