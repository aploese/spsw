/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ibapl.spsw.provider;

import de.ibapl.spsw.api.AbstractSerialPortSocketFactory;
import de.ibapl.spsw.api.SerialPortSocket;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.ServiceScope;

/**
 *
 * @author aploese
 */
@Singleton
@Component(name = "SerialPortSocketFactory", scope = ServiceScope.SINGLETON)
public class SerialPortSocketFactoryImpl extends AbstractSerialPortSocketFactory {

    protected final static Logger LOG = Logger.getLogger("de.ibapl.spsw.provider");

    private static SerialPortSocketFactoryImpl singleton;

    /**
     * Creates and activates @see #activate a singleton instance for use in non
     * framework environments
     *
     * @return
     */
    public synchronized static SerialPortSocketFactoryImpl singleton() {
        if (singleton == null) {
            singleton = new SerialPortSocketFactoryImpl();
            singleton.activate();
        }
        return singleton;
    }

    private static boolean libLoaded;
    private static String libName;
    public final static String SPSW_PROPERTIES = "de/ibapl/spsw/provider/spsw.properties";

    @Override
    public boolean isLibLoaded() {
        return libLoaded;
    }

    public String getLibName() {
        return libName;
    }

    //TODO usable LOG INFOS ...
    @Override
    public synchronized boolean loadNativeLib() {
        if (libLoaded) {
            LOG.log(Level.INFO, "Lib was Loaded");
            return false;
        }
        LOG.log(Level.INFO, "java.library.path: \"{0}\"", System.getProperty("java.library.path"));
        Properties p = new Properties();
        try {
            p.load(this.getClass().getClassLoader().getResourceAsStream(SPSW_PROPERTIES));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Can't find spsw.properties");
            throw new RuntimeException("Can't load version information", ex);
        }

        libName = String.format("spsw-%s-%s-%s", getOsName(), getArch(), p.getProperty("version"));

        //Try it plain - OSGi will load with the bundle classloader - or if there are in the "java.library.path"
        LOG.log(Level.INFO, "Try plain with libName: {0}", libName);
        try {
            System.loadLibrary(libName);
            LOG.log(Level.INFO, "Lib loaded via System.loadLibrary(\"{0}\")", libName);
            return true;
        } catch (Throwable t) {
            LOG.log(Level.INFO, "Native lib not loaded.", t);
            libLoaded = false;
        }

        final String libResourceName = "lib/" + System.mapLibraryName(libName);
        //Try from resource like the tests do
        libName = getClass().getClassLoader().getResource(libResourceName).getFile();
        LOG.log(Level.INFO, "Try from resource with libName: {0}", libName);
        try {
            System.load(libName);
            LOG.log(Level.INFO, "Lib loaded via System.load(\"{0}\")", libName);
            return true;
        } catch (Throwable t) {
            LOG.log(Level.INFO, "Native lib not loaded.", t);
            libLoaded = false;
        }

        //If nothing helps do it the hard way: unpack to temp and load that.
        File tmpLib = null;
        try (InputStream is = AbstractSerialPortSocket.class.getClassLoader().getResourceAsStream(libResourceName)) {
            if (is == null) {
                throw new RuntimeException("Cant find lib: " + libName + "in resources");
            }
            int splitPos = libName.lastIndexOf('.');
            if (splitPos <= 0) {
                //ERROR
            }
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
            LOG.log(Level.INFO, "Try temp copy with libName: {0}", libName);
            libName = tmpLib.getAbsolutePath();
            try {
                System.load(libName);
            } catch (Throwable t) {
                LOG.log(Level.INFO, "Native lib not loaded.", t);
                libLoaded = false;
                throw t;
            }
            libName = tmpLib.getAbsolutePath();
            LOG.log(Level.INFO, "Lib loaded via System.load(\"{0}\")", tmpLib.getAbsolutePath());
            return true;
        } catch (Throwable t) {
            LOG.log(Level.SEVERE, "Giving up can't load the lib \"" + tmpLib.getAbsolutePath() + "\" List System Properties", t);
            StringBuilder sb = new StringBuilder();
            for (String name : System.getProperties().stringPropertyNames()) {
                sb.append("\t").append(name).append(" = ").append(System.getProperty(name)).append("\n");
            }
            LOG.log(Level.SEVERE, "System.properties\n{0}", new Object[]{sb.toString()});
            throw new RuntimeException("Can't load spsw native lib, giving up!", t);
        }
    }

    @Override
    protected String[] getWindowsBasedPortNames(boolean hideBusyPorts) throws IOException {
        return GenericWinSerialPortSocket.getWindowsBasedPortNames(hideBusyPorts);
    }

    @Override
    public SerialPortSocket createSerialPortSocket(String portName) {
        switch (System.getProperty("os.name")) {
            case "Linux":
                return new GenericTermiosSerialPortSocket(portName);
            case "Mac OS":
                throw new UnsupportedOperationException("Mac OS is currently not supported yet");
            case "Mac OS X":
                throw new UnsupportedOperationException("Mac OS X is currently not supported yet");
            case "Windows 95":
                return new GenericWinSerialPortSocket(portName);
            case "Windows 98":
                return new GenericWinSerialPortSocket(portName);
            case "Windows Me":
                return new GenericWinSerialPortSocket(portName);
            case "Windows NT":
                return new GenericWinSerialPortSocket(portName);
            case "Windows 2000":
                return new GenericWinSerialPortSocket(portName);
            case "Windows XP":
                return new GenericWinSerialPortSocket(portName);
            case "Windows 2003":
                return new GenericWinSerialPortSocket(portName);
            case "Windows Vista":
                return new GenericWinSerialPortSocket(portName);
            case "Windows 2008":
                return new GenericWinSerialPortSocket(portName);
            case "Windows 7":
                return new GenericWinSerialPortSocket(portName);
            case "Windows 8":
                return new GenericWinSerialPortSocket(portName);
            case "Windows 2012":
                return new GenericWinSerialPortSocket(portName);
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

    @Override
    @PostConstruct
    @Activate
    public void activate() {
        if (!libLoaded) {
            loadNativeLib();
        }
    }

    @Override
    @PreDestroy
    @Deactivate
    public void deActivate() {
    }
}
