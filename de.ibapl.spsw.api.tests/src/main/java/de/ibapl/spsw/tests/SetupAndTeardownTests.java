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
package de.ibapl.spsw.tests;

import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortConfiguration;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.SerialPortSocketFactory;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 *
 * @author aploese
 */
@ExtendWith(SetupAndTeardownTests.AfterTestExecution.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class SetupAndTeardownTests<T extends SerialPortConfiguration> {

    protected final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    protected static final int PORT_RECOVERY_TIME_MS = 200;

    protected static final Logger LOG = Logger.getLogger("SpswTests");
    protected final static String readSerialPortName;
    protected final static String writeSerialPortName;

    static {
        String readName = null;
        String writeName = null;
        try (InputStream is = SetupAndTeardownTests.class.getClassLoader()
                .getResourceAsStream("junit-spsw-config.properties")) {
            if (is != null) {
                Properties p = new Properties();
                p.load(is);
                readName = p.getProperty("readPort", null);
                writeName = p.getProperty("writePort", null);
            }
        } catch (IOException ex) {
            Logger.getLogger(SetupAndTeardownTests.class.getName()).log(Level.SEVERE, null, ex);
        }
        readSerialPortName = readName;
        writeSerialPortName = writeName;
    }
    
    protected T readSpc;
    protected T writeSpc;

    protected boolean currentTestFailed;

    protected SerialPortSocketFactory getSerialPortSocketFactory() {
        ServiceLoader<SerialPortSocketFactory> serviceLoader = ServiceLoader.load(SerialPortSocketFactory.class);
        Iterator<SerialPortSocketFactory> iterator = serviceLoader.iterator();
        assertTrue(iterator.hasNext(), "No implementation of SerialPortSocket found - Please fix test setup");
        final SerialPortSocketFactory result = iterator.next();
        assertFalse(iterator.hasNext(),
                "More than one implementation of SerialPortSocket found - Please fix test setup");
        return result;
    }

    protected T wrapReadSpc(T opend) {
        return opend;
    }

    protected T wrapWriteSpc(T opend) {
        return opend;
    }

    public static class AfterTestExecution implements AfterTestExecutionCallback {

        @Override
        public void afterTestExecution(ExtensionContext context) throws Exception {
            ((SetupAndTeardownTests) context.getRequiredTestInstance()).currentTestFailed = context.getExecutionException()
                    .isPresent();
        }
    }

    protected void setSpeed(Speed speed) throws IOException {
        if (readSpc != null) {
            readSpc.setSpeed(speed);
        }
        if (writeSpc != null && writeSpc != readSpc) {
            writeSpc.setSpeed(speed);
        }
    }

    protected void assumeRTest() {
        assumeTrue(readSerialPortName != null);
    }

    protected void assumeWTest() {
        assumeTrue(writeSerialPortName != null);
    }

    protected void assumeRWTest() {
        assumeRTest();
        assumeWTest();
    }

    protected void setTimeouts(int interByteReadTimeout, int overallReadTimeout, int overallWriteTimeout)
            throws Exception {
        if (readSpc != null) {
            readSpc.setTimeouts(interByteReadTimeout, overallReadTimeout, overallWriteTimeout);
        }
        if (writeSpc != null && writeSpc != readSpc) {
            writeSpc.setTimeouts(interByteReadTimeout, overallReadTimeout, overallWriteTimeout);
        }
    }

    protected void printPorts() throws IOException {
        if (readSpc != null && writeSpc != null && readSpc != writeSpc) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("\n\tName:        %-20s %-20s\n", readSpc.getPortName(), writeSpc.getPortName()));
            sb.append(String.format("\tSpeed:    %-20d %-20d\n", readSpc.getSpeed().value, writeSpc.getSpeed().value));
            sb.append(String.format("\tDataBits:    %-20d %-20d\n", readSpc.getDatatBits().value,
                    writeSpc.getDatatBits().value));
            sb.append(String.format("\tStopBits:    %-20f %-20f\n", readSpc.getStopBits().value,
                    writeSpc.getStopBits().value));
            sb.append(String.format("\tParity:      %-20s %-20s\n", readSpc.getParity().name(),
                    writeSpc.getParity().name()));
            sb.append(String.format("\tFlowControl: %-20s %-20s\n", readSpc.getFlowControl().toString(),
                    writeSpc.getFlowControl().toString()));
            sb.append(String.format("\tIntebyteReadTimeout:    %-20d %-20d\n", readSpc.getInterByteReadTimeout(),
                    writeSpc.getInterByteReadTimeout()));
            sb.append(String.format("\tOverallReadTimeout:    %-20d %-20d\n", readSpc.getOverallReadTimeout(),
                    writeSpc.getOverallReadTimeout()));
            sb.append(String.format("\tOverallWriteTimeout:    %-20d %-20d\n", readSpc.getOverallWriteTimeout(),
                    writeSpc.getOverallWriteTimeout()));

            LOG.log(Level.INFO, sb.toString());
        } else {
            @SuppressWarnings("resource")
            SerialPortConfiguration spc = readSpc != null ? readSpc : writeSpc;
            if (spc != null) {
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("\n\tName:        %-20s\n", spc.getPortName()));
                sb.append(String.format("\tSpeed:    %-20d\n", spc.getSpeed().value));
                sb.append(String.format("\tDataBits:    %-20d\n", spc.getDatatBits().value));
                sb.append(String.format("\tStopBits:    %-20f\n", spc.getStopBits().value));
                sb.append(String.format("\tParity:      %-20s\n", spc.getParity().name()));
                sb.append(String.format("\tFlowControl: %-20s\n", spc.getFlowControl().toString()));
                sb.append(String.format("\tIntebyteReadTimeout:    %-20d\n", spc.getInterByteReadTimeout()));
                sb.append(String.format("\tOverallReadTimeout:    %-20d\n", spc.getOverallReadTimeout()));
                sb.append(String.format("\tOverallWriteTimeout:    %-20d\n", spc.getOverallWriteTimeout()));

                LOG.log(Level.INFO, sb.toString());
            }
        }
    }

    @BeforeEach
    public void setUp(TestInfo testInfo) throws Exception {
        LOG.info(MessageFormat.format("do run test : {0}", testInfo.getDisplayName()));
        readSpc = null;
        writeSpc = null;
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (writeSpc != null) {
            if (writeSpc != readSpc) {
                if (writeSpc.isOpen()) {
                    writeSpc.close();
                    assert !writeSpc.isOpen();
                }
            }
            writeSpc = null;
        }
        if (readSpc != null) {
            if (readSpc.isOpen()) {
                readSpc.close();
                assert !readSpc.isOpen();
            }
            readSpc = null;
        }

        Runtime.getRuntime().gc();
        Runtime.getRuntime().runFinalization();
        if (currentTestFailed) {
            Thread.sleep(PORT_RECOVERY_TIME_MS);
            Runtime.getRuntime().gc();
            Runtime.getRuntime().runFinalization();
        }
    }

}
