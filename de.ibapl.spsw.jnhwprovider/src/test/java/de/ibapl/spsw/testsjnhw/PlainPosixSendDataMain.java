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
package de.ibapl.spsw.testsjnhw;

import de.ibapl.jnhw.Define;
import de.ibapl.jnhw.Defined;
import de.ibapl.jnhw.NativeErrorException;
import de.ibapl.jnhw.NotDefinedException;
import de.ibapl.jnhw.OpaqueMemory;
import de.ibapl.jnhw.posix.Errno;
import static de.ibapl.jnhw.posix.Fcntl.O_NOCTTY;
import static de.ibapl.jnhw.posix.Fcntl.O_RDWR;
import static de.ibapl.jnhw.posix.Fcntl.open;
import de.ibapl.jnhw.posix.Termios;
import static de.ibapl.jnhw.posix.Termios.B1500000;
import static de.ibapl.jnhw.posix.Termios.BRKINT;
import static de.ibapl.jnhw.posix.Termios.CLOCAL;
import static de.ibapl.jnhw.posix.Termios.CMSPAR;
import static de.ibapl.jnhw.posix.Termios.CREAD;
import static de.ibapl.jnhw.posix.Termios.CS8;
import static de.ibapl.jnhw.posix.Termios.CSIZE;
import static de.ibapl.jnhw.posix.Termios.ECHO;
import static de.ibapl.jnhw.posix.Termios.ECHONL;
import static de.ibapl.jnhw.posix.Termios.HUPCL;
import static de.ibapl.jnhw.posix.Termios.ICANON;
import static de.ibapl.jnhw.posix.Termios.ICRNL;
import static de.ibapl.jnhw.posix.Termios.IEXTEN;
import static de.ibapl.jnhw.posix.Termios.IGNBRK;
import static de.ibapl.jnhw.posix.Termios.IGNCR;
import static de.ibapl.jnhw.posix.Termios.INLCR;
import static de.ibapl.jnhw.posix.Termios.ISIG;
import static de.ibapl.jnhw.posix.Termios.ISTRIP;
import static de.ibapl.jnhw.posix.Termios.IXON;
import static de.ibapl.jnhw.posix.Termios.OPOST;
import static de.ibapl.jnhw.posix.Termios.PARENB;
import static de.ibapl.jnhw.posix.Termios.PAREXT;
import static de.ibapl.jnhw.posix.Termios.PARMRK;
import de.ibapl.jnhw.posix.Termios.StructTermios;
import static de.ibapl.jnhw.posix.Termios.TCIOFLUSH;
import static de.ibapl.jnhw.posix.Termios.TCSANOW;
import static de.ibapl.jnhw.posix.Termios.VMIN;
import static de.ibapl.jnhw.posix.Termios.VTIME;
import static de.ibapl.jnhw.posix.Termios.cfsetspeed;
import static de.ibapl.jnhw.posix.Termios.tcflush;
import static de.ibapl.jnhw.posix.Termios.tcgetattr;
import static de.ibapl.jnhw.posix.Termios.tcsetattr;
import static de.ibapl.jnhw.posix.Unistd.close;
import static de.ibapl.jnhw.posix.Unistd.read;
import static de.ibapl.jnhw.posix.Unistd.write;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 *
 * @author Arne Plöse
 *
 */
public class PlainPosixSendDataMain {

    final static int BUFFER_SIZE = 512;
    final static int SPEED = Defined.getValueOr(Termios::B1500000, Termios.B0());
    final static int BITS_PER_TRANSFERRED_BYTE = 10; //1 start + 8 data + 0 parity + 1 stopp  
    private static final int CMSPAR_OR_PAREXT;
    static volatile boolean doExit = false;

    static {
        int value = 0;
        try {
            value = CMSPAR();
        } catch (NotDefinedException nde) {
            try {
                value = PAREXT();
            } catch (NotDefinedException nde1) {
                //This is for FreeBSD No Parity SPACE and MARK
                System.err.println("Parites SPACE and MARK will not work!");
            }
        }
        CMSPAR_OR_PAREXT = value;
    }

    public static void main(String[] args) throws Exception {
        args = new String[]{"/dev/ttyUSB0"};
        if (args.length != 1) {
            System.err.println("You must only provide the port name!");
            System.exit(-1);
        }

        final int fd = openSerialPort(args[0], SPEED);
        sendOpaqueMemory(fd);
        //  sendByteBuffer(fd);
        //  sendByteArray(fd);
    }

    private static int openSerialPort(String portName, int bitrate) {
        try {
            int fd = open(portName, O_RDWR() | O_NOCTTY());
            final StructTermios termios = new StructTermios();

            tcgetattr(fd, termios);
            System.err.println("Termios: " + termios.toString());

            //Make RAW like cfmakeraw
            if (Defined.defined(Termios::PARMRK)) {
            try {
                termios.c_iflag(termios.c_iflag() & ~(IGNBRK() | BRKINT() | PARMRK() | ISTRIP() | INLCR() | IGNCR() | ICRNL() | IXON()));
            } catch (NotDefinedException nde) {
                //PARMRK not defined
                throw new RuntimeException(nde);
            }
            } else {
                                termios.c_iflag(termios.c_iflag() & ~(IGNBRK() | BRKINT() | ISTRIP() | INLCR() | IGNCR() | ICRNL() | IXON()));
            }
            termios.c_oflag(termios.c_oflag() & ~OPOST());
            termios.c_lflag(termios.c_lflag() & ~(ECHO() | ECHONL() | ICANON() | ISIG() | IEXTEN()));
            //Make sure CLOCAL is set otherwise opening the port later won't work without Fcntl.O_NONBLOCK()
            termios.c_cflag(termios.c_cflag() & ~(CSIZE() | PARENB()) | CS8() | CREAD() | CLOCAL() | HUPCL());
            termios.c_cc(VMIN(), (byte) 1); // read at least 1 char
            termios.c_cc(VTIME(), (byte) 100);// 1 s inter char timeout ;-)
            cfsetspeed(termios, bitrate);

            /*
        //Throw away all settings and start from scratch ...
        termios.c_cflag(Termios.CREAD() | Termios.CLOCAL());
        termios.c_lflag(0);
        termios.c_iflag(0);
        termios.c_oflag(0);
        termios.c_cc(VMIN(), (byte) 1); // read at least 1 char
        termios.c_cc(VTIME(), (byte) 10);// 1 s inter char timeout ;-)
        //bitrate
        cfsetspeed(termios, bitrate);
        // 8 data bits    
        termios.c_cflag(termios.c_cflag() & ~CSIZE() | Termios.CS8());
        //1 stop bit        
        termios.c_cflag(termios.c_cflag() & ~CSTOPB());
        //no parity checking
        termios.c_cflag(termios.c_cflag() & ~(PARENB() | PARODD() | CMSPAR_OR_PAREXT)); // Clear parity settings
        termios.c_iflag(termios.c_iflag() & ~INPCK()); // switch parity input checking off
        //no flow control
        termios.c_cflag(termios.c_cflag() & ~CRTSCTS());
        termios.c_iflag(termios.c_iflag() & ~(IXON() | IXOFF()));
             */
            System.err.println("Termios: " + termios.toString());
            tcsetattr(fd, TCSANOW(), termios);
            tcflush(fd, TCIOFLUSH());
            return fd;
        } catch (NativeErrorException nee) {
            nee.printStackTrace();
            System.err.println("Native error " + Errno.getErrnoSymbol(nee.errno));
            System.err.flush();
            throw new RuntimeException(nee);
        }
    }

    public static void sendByteArray(int fd) throws Exception {
        final byte[] sendBuffer = new byte[BUFFER_SIZE];
        for (int i = 0; i < BUFFER_SIZE; i++) {
            sendBuffer[i] = (byte) i;
        }
        try {

            Thread thread = new Thread(() -> {
                final byte[] recBuffer = new byte[BUFFER_SIZE];
                byte currentData = 0;
                try {
                    int overallRead = 0;
                    int currBytePos = 0;
                    final long start = System.currentTimeMillis() - 1;
                    while (!doExit) {
                        final int currentRead = read(fd, recBuffer);
                        overallRead += currentRead;
                        for (int i = 0; i < currentRead; i++) {
                            final byte data = recBuffer[i];
//                            System.err.format("%d: 0x%02x 0x%02x\n", currBytePos++, currentData, data);
                            if (currentData != data) {
                                throw new RuntimeException("REC wrong");
                            }
                            currentData++;
                        }
//                        System.err.println("XXXXXXXXXXXXXXXXXXX");

                        Arrays.fill(recBuffer, (byte) 0);
                        System.out.format("rec: %d bps @%d\n", ((overallRead * 1000L * BITS_PER_TRANSFERRED_BYTE) / (System.currentTimeMillis() - start)), currentRead);
                        System.out.flush();
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    System.err.println(throwable);
                    System.err.flush();
                    doExit = true;
                }
            });
            thread.start();
            long i = 0;
            final long start = System.currentTimeMillis() - 1;
            while (!doExit) {
                try {
                    write(fd, sendBuffer);
                    i++;
                    System.out.format("send: %d bps\n", ((i * BUFFER_SIZE * 1000L * BITS_PER_TRANSFERRED_BYTE) / (System.currentTimeMillis() - start)));
                    System.out.flush();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    System.err.println(throwable);
                    System.err.flush();
                    System.exit(-1);
                }
            }
        } finally {
            tcflush(fd, TCIOFLUSH());
            close(fd);
            System.err.println("end doExit: " + doExit);
            System.err.flush();
        }
    }

    public static void sendByteBuffer(int fd) throws Exception {
        final ByteBuffer sendBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
        while (sendBuffer.hasRemaining()) {
            sendBuffer.put((byte) sendBuffer.position());
        }
        sendBuffer.flip();
        try {

            Thread thread = new Thread(() -> {
                final ByteBuffer recBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
                byte currentData = 0;
                try {
                    int overallRead = 0;
                    int currBytePos = 0;
                    final long start = System.currentTimeMillis() - 1;
                    while (!doExit) {
                        final int currentRead = read(fd, recBuffer);
                        overallRead += currentRead;
                        recBuffer.flip();
                        while (recBuffer.hasRemaining()) {
                            final byte data = recBuffer.get();
//                            System.err.format("%d: 0x%02x 0x%02x\n", currBytePos++, currentData, data);
                            if (currentData != data) {
                                throw new RuntimeException("REC wrong");
                            }
                            currentData++;
                        }
//                        System.err.println("XXXXXXXXXXXXXXXXXXX");

                        recBuffer.clear();
                        System.out.format("rec: %d bps @%d\n", ((overallRead * 1000L * BITS_PER_TRANSFERRED_BYTE) / (System.currentTimeMillis() - start)), currentRead);
                        System.out.flush();
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    System.err.println(throwable);
                    System.err.flush();
                    doExit = true;
                }
            });
            thread.start();
            long i = 0;
            final long start = System.currentTimeMillis() - 1;
            while (!doExit) {
                try {
                    write(fd, sendBuffer);
                    sendBuffer.flip();
                    i++;
                    System.out.format("send: %d bps\n", ((i * BUFFER_SIZE * 1000L * BITS_PER_TRANSFERRED_BYTE) / (System.currentTimeMillis() - start)));
                    System.out.flush();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    System.err.println(throwable);
                    System.err.flush();
                    System.exit(-1);
                }
            }
        } finally {
            tcflush(fd, TCIOFLUSH());
            close(fd);
            System.err.println("end doExit: " + doExit);
            System.err.flush();
        }
    }

    public static void sendOpaqueMemory(int fd) throws Exception {
        final OpaqueMemory sendBuffer = new OpaqueMemory(BUFFER_SIZE, false);
        for (int i = 0; i < BUFFER_SIZE; i++) {
            OpaqueMemory.setByte(sendBuffer, i, (byte) i);
        }
        try {

            Thread thread = new Thread(() -> {
                final OpaqueMemory recBuffer = new OpaqueMemory(BUFFER_SIZE, true);
                byte currentData = 0;
                try {
                    int overallRead = 0;
                    int currBytePos = 0;
                    final long start = System.currentTimeMillis() - 1;
                    while (!doExit) {
                        final int currentRead = read(fd, recBuffer);
                        overallRead += currentRead;
                        for (int i = 0; i < currentRead; i++) {
                            final byte data = OpaqueMemory.getByte(recBuffer, i);
//                            System.err.format("%d: 0x%02x 0x%02x\n", currBytePos++, currentData, data);
                            if (currentData != data) {
                                throw new RuntimeException("REC wrong");
                            }
                            currentData++;
                        }
//                        System.err.println("XXXXXXXXXXXXXXXXXXX");

                        OpaqueMemory.clear(recBuffer);
                        System.out.format("rec: %d bps @%d\n", ((overallRead * 1000L * BITS_PER_TRANSFERRED_BYTE) / (System.currentTimeMillis() - start)), currentRead);
                        System.out.flush();
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    System.err.println(throwable);
                    System.err.flush();
                    doExit = true;
                }
            });
            thread.start();
            long i = 0;
            final long start = System.currentTimeMillis() - 1;
            while (!doExit) {
                try {
                    write(fd, sendBuffer);
                    i++;
                    System.out.format("send: %d bps\n", ((i * BUFFER_SIZE * 1000L * BITS_PER_TRANSFERRED_BYTE) / (System.currentTimeMillis() - start)));
                    System.out.flush();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    System.err.println(throwable);
                    System.err.flush();
                    System.exit(-1);
                }
            }
        } finally {
            tcflush(fd, TCIOFLUSH());
            close(fd);
            System.err.println("end doExit: " + doExit);
            System.err.flush();
        }
    }

}
