/*
 * JNHW - Java Native header Wrapper, https://github.com/aploese/jnhw/
 * Copyright (C) 2019-2019, Arne Plöse and individual contributors as indicated
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
package de.ibapl.spsw.jniprovider;

import de.ibapl.jnhw.libloader.MultiarchInfo;
import de.ibapl.jnhw.libloader.MultiarchTupelBuilder;
import de.ibapl.jnhw.libloader.OS;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aploese
 */
public class SpswNativeLibLoader {

    protected final static Logger LOG = Logger.getLogger("de.ibapl.libjnhw");

    protected final static MultiarchTupelBuilder MULTIARCH_TUPEL_BUILDER;
    protected final static Set<MultiarchInfo> MULTIARCH_INFO;
    protected final static OS OS;
    protected final static File NATIVE_TEMP_DIR;

    /**
     * Setup native lib Sometimes arm-linux-gnueabihf and arm-linux-gnuabihf
     * cant be distinguished
     */
    static {
        MULTIARCH_TUPEL_BUILDER = new MultiarchTupelBuilder();
        Set<MultiarchInfo> multiarchInfo = EnumSet.noneOf(MultiarchInfo.class);
        OS os = null;
        try {
            multiarchInfo = MULTIARCH_TUPEL_BUILDER.guessMultiarch();
            Iterator<MultiarchInfo> iter = multiarchInfo.iterator();
            if (iter.hasNext()) {
                os = iter.next().getOS();
            } else {
                os = null;
            }
        } catch (Throwable t) {
            LOG.log(Level.SEVERE, "Unknown exception sys propereties: \n" + MULTIARCH_TUPEL_BUILDER.listSystemProperties(), t);
        }
        OS = os;
        MULTIARCH_INFO = multiarchInfo;

        File nativeTemDir = null;
        try {
            nativeTemDir = File.createTempFile("jnhw-native-loader", "lib-dir");
            nativeTemDir.delete();
            nativeTemDir.mkdir();
            nativeTemDir.deleteOnExit();
        } catch (IOException ioe) {
            LOG.log(Level.SEVERE, "Can't create tempDir sys propereties: \n" + MULTIARCH_TUPEL_BUILDER.listSystemProperties(), ioe);
        }
        NATIVE_TEMP_DIR = nativeTemDir;
    }

    public SpswNativeLibLoader() {
        
    }

    public synchronized String loadNativeLib(final String libName, int libToolInterfaceVersion) throws IOException {
            String[] javaLibraryPath = System.getProperty("java.library.path").split(":");
            String formattedLibName = OS.formatLibName(libName, libToolInterfaceVersion);
            for (int i = 0; i < javaLibraryPath.length; i++) {
                final String absLibName = javaLibraryPath[i] + "/" + formattedLibName;
                if (new File(absLibName).exists()) {
                    try {
                        System.load(absLibName);
                        LOG.log(Level.WARNING, "TODO INFOLib {0} loaded via System.load(\"{1}\")", new Object[]{libName, absLibName});
                        return absLibName;
                    } catch (UnsatisfiedLinkError ule) {
                        LOG.log(Level.INFO, ule, () -> {
                            return String.format("Could not load %s via System.load(\"%s\")", libName, absLibName);
                        });
                    }
                }
            }
            return loadFromResource(libName, formattedLibName);
    }

    private String loadFromResource(final String libName, final String formattedLibName) throws IOException {
        for (MultiarchInfo mi : MULTIARCH_INFO) {
            // Figure out os and arch
            final String libResourceName = String.format("/lib/%s/%s", mi.getTupelName(), formattedLibName);
            // Try from classpath like the tests or extracted jars do
            URL classPathLibURL = getClass().getResource(libResourceName);
            if (classPathLibURL == null) {
                LOG.log(Level.SEVERE, "Lib \"{0}\" is not in classpath with resourcename: \"{1}\"", new Object[]{libName, libResourceName});
                throw new FileNotFoundException("Lib " + libName + " is not in classpath with resourcename: " + libResourceName);
            }
            String classPathLibName = classPathLibURL.getFile();

            // Unbundled aka not within a jar
            LOG.log(Level.WARNING, "TODO INFO Try load \"{0}\" from filesystem with libName: \"{1}\"", new Object[]{libName, classPathLibURL.toString()});
            //On MacOS we cant load the lib directly, we must fix first the internal id and lib path ... only copy to tmp and fix the path solves this
            if (getOS() != OS.MAC_OS_X) {
                try {
                    System.load(classPathLibName);
                    LOG.log(Level.WARNING, "TODO INFO \"{0}\" loaded via System.load(\"{1}\")", new Object[]{libName, classPathLibName});
                    return classPathLibName;
                } catch (UnsatisfiedLinkError ule) {
                    LOG.log(Level.WARNING, "TODO INFO  lib {0} for {1} not loaded: {2}", new Object[]{classPathLibName, libName, ule.getMessage()});
                } catch (Throwable t) {
                    LOG.log(Level.WARNING, "TODO INFONative lib not loaded.", t);
                }
            }
            // Copy to tmp
            try {
                File tmpLib = copyToNativeLibDir(classPathLibURL, formattedLibName);
                classPathLibName = tmpLib.getAbsolutePath();
                
                if (getOS() == OS.MAC_OS_X) {
                    URL classPathLibHelperURL = getClass().getResource(libResourceName + ".sh");
                    if (classPathLibHelperURL != null) {
                        String helperName = formattedLibName + ".sh";
                        File libLocationFixScript = copyToNativeLibDir(classPathLibHelperURL, helperName);
                        Process child = new ProcessBuilder("/bin/sh", helperName).directory(NATIVE_TEMP_DIR).start();
                        child.waitFor();
                        if (child.exitValue() != 0) {
                            //Todo collect error stream
                            LOG.log(Level.SEVERE, "executing Script failed (\"{0}\")", libLocationFixScript);
                        }
                    }
                }
                System.load(classPathLibName);
                LOG.log(Level.WARNING, "TODO INFO Lib loaded via System.load(\"{0}\")", classPathLibName);
                tmpLib.delete();
                return classPathLibURL.toString();
            } catch (Throwable t) {
                //do not delete tmpLib so it can be examined to figure out what went wrong...
                // do not stop here.. maybe it is only the wron abi like arm spftfloat and hardfloat ... 
                LOG.log(Level.SEVERE, "Can't load the lib \"" + classPathLibName + "\" List System Properties\n " + MULTIARCH_TUPEL_BUILDER.listSystemProperties() + "\n", t);
            }
        }
        LOG.log(Level.SEVERE, "Giving up; Can't load the lib {0}!\nList System Properties\n {1} \n",
                new Object[]{libName, MULTIARCH_TUPEL_BUILDER.listSystemProperties()});
                throw new IOException("Can't load the lib \"" + libName + "\" foramtted as: \"" + formattedLibName + "\" for any Architectures");
    }

    private static File copyToNativeLibDir(URL sourceURL, final String targetName) throws IOException {
        // If nothing helps, do it the hard way: unpack to temp and load that.
        try (InputStream is = sourceURL.openStream()) {
            File result = new File(NATIVE_TEMP_DIR, targetName);
            LOG.log(Level.INFO, "Try temp copy\nfrom:\t{0}\nto:\t{1}",
                    new Object[]{sourceURL.getFile(), result.getAbsolutePath()});

            try (FileOutputStream fos = new FileOutputStream(result)) {
                byte[] buff = new byte[1024];
                int i;
                while ((i = is.read(buff)) > 0) {
                    fos.write(buff, 0, i);
                }
                fos.flush();
            }
            return result;
        }
    }

    public synchronized String loadClassicalNativeLib(final String libName) throws IOException {
            LOG.log(Level.INFO, "java.library.path: \"{0}\"", System.getProperty("java.library.path"));
            return loadFromResource(libName, System.mapLibraryName(libName));
    }

    public static OS getOS() {
        return OS;
    }
}
