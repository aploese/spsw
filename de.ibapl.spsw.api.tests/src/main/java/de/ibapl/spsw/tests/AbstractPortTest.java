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

import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.TimeoutIOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.logging.Level;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Arne Plöse
 */
public abstract class AbstractPortTest extends AbstractSerialPortSocketTest {

    public class Receiver implements Runnable {

        final SocketIoType socketIoType;
        final SerialPortSocket sps;
        boolean done;
        final Object LOCK = new Object();
        ByteBuffer recBuffer;
        final ByteBuffer sendBuffer;
        int currentRecOffset;

        Exception ex;
        Error err;

        public Receiver(SocketIoType socketIoType, SerialPortSocket sps, ByteBuffer sendBuffer) {
            this(socketIoType, sps, sendBuffer, socketIoType == SocketIoType.CHANNEL ? ByteBuffer.allocateDirect(sendBuffer.capacity()) : ByteBuffer.allocate(sendBuffer.capacity()));
        }

        public Receiver(SocketIoType socketIoType, SerialPortSocket sps, ByteBuffer sendBuffer, ByteBuffer recBuffer) {
            this.socketIoType = socketIoType;
            this.sps = sps;
            this.sendBuffer = sendBuffer;
            this.recBuffer = recBuffer;
        }

        @Override
        public void run() {
            currentRecOffset = 0;
            ex = null;
            err = null;
            done = false;
            try {
                while (sendBuffer.limit() > currentRecOffset) {
                    switch (socketIoType) {
                        case SINGLE_BYTE: {

                            final InputStream is = sps.getInputStream();
                            final int data = is.read();
                            if (data >= 0) {
                                recBuffer.put((byte) data);
                                assertEquals(sendBuffer.get(currentRecOffset), recBuffer.get(currentRecOffset), () -> {
                                    return String.format("Arrays differ @%d expected %02x, but was %02x", currentRecOffset, sendBuffer.get(currentRecOffset), recBuffer.get(currentRecOffset));
                                });
                                currentRecOffset++;
                            } else {
                                throw new RuntimeException("TODO implement me is.read returns: " + data);
                            }
                            if (currentRecOffset == recBuffer.capacity()) {
                                break;
                            }
                        }
                        break;
                        case STREAM: {
                            byte[] buf = new byte[recBuffer.remaining()];
                            final int count = sps.getInputStream().read(buf);
                            recBuffer.put(buf, 0, count);

                            if (count > 0) {
                                for (int i = 0; i < count; i++) {
                                    final int pos = currentRecOffset + i;
                                    assertEquals(sendBuffer.get(currentRecOffset), recBuffer.get(currentRecOffset), () -> {
                                        return String.format("Arrays differ @%d expected %02x, but was %02x", pos,
                                                sendBuffer.get(pos), recBuffer.get(pos));
                                    });
                                }
                                currentRecOffset += count;
                                if (!recBuffer.hasRemaining()) {
                                    break;
                                }
                            } else if (count < 0) {
                                //End of stream ...
                                if (recBuffer.hasRemaining()) {
                                    LOG.log(Level.SEVERE, "Bytes missing: {0}", recBuffer.remaining());
                                }
                                break;
                            } else {
                                fail("Unknow error: Read result count is 0 and no timeout");
                            }

                        }
                        break;
                        case CHANNEL: {
                            final int count = sps.read(recBuffer);

                            if (count > 0) {
                                for (int i = 0; i < count; i++) {
                                    final int pos = currentRecOffset + i;
                                    assertEquals(sendBuffer.get(pos), recBuffer.get(pos), () -> {
                                        return String.format("Arrays differ @%d expected %02x, but was %02x", pos,
                                                sendBuffer.get(pos), recBuffer.get(pos));
                                    });
                                }
                                currentRecOffset += count;
                                if (!recBuffer.hasRemaining()) {
                                    break;
                                }
                            } else if (count <= 0) {
                                if (recBuffer.hasRemaining()) {
                                    LOG.log(Level.SEVERE, "Bytes missing: {0}", recBuffer.remaining());
                                }
                                break;
                            } else {
                                fail("Unknow error: Read result count is 0 and no timeout");
                            }

                        }
                        break;
                    }
                }

                LOG.log(Level.INFO, "Byte total read: {0}", currentRecOffset);
                synchronized (LOCK) {
                    done = true;
                    LOCK.notifyAll();
                    LOG.log(Level.INFO, "Send Thread finished");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                synchronized (LOCK) {
                    done = true;
                    this.ex = ex;
                    LOG.log(Level.SEVERE, "Send Thread Exception offset: " + currentRecOffset, ex);
                    LOCK.notifyAll();
                }
            } catch (Error err) {
                err.printStackTrace();
                synchronized (LOCK) {
                    done = true;
                    this.err = err;
                    // TODO printArrays("Error");
                    LOG.log(Level.SEVERE, "Send Thread Error offset: " + currentRecOffset, err);
                    LOCK.notifyAll();
                }
            }
        }

        /**
         * Try to figure out what exactly hit us...
         *
         * At high speeds sometime a byte gets "lost" and therefore its running
         * in an timeout... The first "missing" byte is where the array
         * differ...? The amount of "missing" bytes is the difference of
         * sendBuffer.length and currentRecOffset.
         *
         */
        public void assertStateAfterExecution() {
            assertNull(err, err != null ? err.getMessage() : "");
            assertAll("Receive Exception", () -> {
                // Where is the missing byte
                assertEquals(sendBuffer.position(), recBuffer.position(), "Position");
                assertEquals(sendBuffer.limit(), recBuffer.limit(), "Limit");
            }, () -> {
                // How much bytes are missing
                assertEquals(sendBuffer.limit(), currentRecOffset, "Received not enough");
            }, () -> {
                if (ex instanceof TimeoutIOException) {
                    // if bytesTransferred == 0 then in the second attempt nothing was read.
                    fail("TimeoutIOException bytes transferred: " + ((TimeoutIOException) ex).bytesTransferred
                            + " MSG: " + ex.getMessage());
                } else if (ex instanceof InterruptedIOException) {
                    fail("InterruptedIOException bytes transferred: " + ((TimeoutIOException) ex).bytesTransferred
                            + " MSG: " + ex.getMessage());
                } else if (ex != null) {
                    fail(ex.getClass().getSimpleName() + " MSG: " + ex.getMessage());

                }
            }, () -> {
                assertTrue(done, "Receiver has not finished");
            });
        }

    }

    public class Sender implements Runnable {

        final SocketIoType socketIoType;
        boolean done;
        final Object LOCK = new Object();
        final ByteBuffer sendBuffer;
        Exception ex;
        Error err;
        final SerialPortSocket sps;

        public Sender(SocketIoType socketIoType, SerialPortSocket sps, int bufferSize) {
            this(socketIoType, sps, socketIoType == SocketIoType.CHANNEL ? ByteBuffer.allocateDirect(bufferSize) : ByteBuffer.allocate(bufferSize));
        }

        public Sender(SocketIoType socketIoType, SerialPortSocket sps, ByteBuffer sendBuffer) {
            this.socketIoType = socketIoType;
            this.sps = sps;
            this.sendBuffer = sendBuffer;
            for (int i = 0; i < sendBuffer.capacity(); i++) {
                sendBuffer.put((byte) i);
            }
            sendBuffer.flip();
        }

        @Override
        public void run() {
            done = false;
            ex = null;
            err = null;
            int bytesWritten = 0;
            try {
                switch (socketIoType) {
                    case SINGLE_BYTE: {
                        final OutputStream os = sps.getOutputStream();
                        while (sendBuffer.hasRemaining()) {
                            os.write(sendBuffer.get());
                            bytesWritten = 1;
                        }
                    }
                    break;
                    case STREAM:
                        byte[] b = new byte[sendBuffer.remaining()];
                        sendBuffer.get(b);
                        sps.getOutputStream().write(b);
                        bytesWritten = b.length;
                        break;
                    case CHANNEL:
                        bytesWritten = sps.write(sendBuffer);
                        break;

                }
                LOG.log(Level.INFO, "Bytes written: {0}", bytesWritten);

                synchronized (LOCK) {
                    done = true;
                    LOCK.notifyAll();
                    LOG.log(Level.INFO, "Send Thread finished");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                synchronized (LOCK) {
                    done = true;
                    this.ex = ex;
                    LOG.log(Level.SEVERE, "Send Thread Exception", ex);
                    LOCK.notifyAll();
                }
            } catch (Error err) {
                err.printStackTrace();
                synchronized (LOCK) {
                    done = true;
                    this.err = err;
                    LOG.log(Level.SEVERE, "Send Thread Error", err);
                    LOCK.notifyAll();
                }
            }
        }

        public void assertStateAfterExecution() {
            assertNull(err);
            assertAll("Sender Exception", () -> {
                assertTrue(done, "Not all data was sent");
            }, () -> {
                if (ex instanceof TimeoutIOException) {
                    // if bytesTransferred == 0 then in the second attempt nothing was read.
                    fail("TimeoutIOException bytes transferred: " + ((TimeoutIOException) ex).bytesTransferred
                            + " MSG: " + ex.getMessage());
                } else if (ex instanceof InterruptedIOException) {
                    fail("InterruptedIOException bytes transferred: " + ((TimeoutIOException) ex).bytesTransferred
                            + " MSG: " + ex.getMessage());
                } else if (ex != null) {
                    fail(ex.getClass().getSimpleName() + " MSG: " + ex.getMessage());
                }
            });
        }
    }

    public void runNonThreaded(Sender sender, Receiver receiver, long timeout) throws Exception {
        assertAll("After ", () -> {
            assertTimeoutPreemptively(Duration.ofMillis(timeout), () -> {
                sender.run();
                receiver.run();
            });
        }, () -> {
            sender.assertStateAfterExecution();
        }, () -> {
            receiver.assertStateAfterExecution();
        });
    }

    public void write_Read_nonThreaded(SocketIoType sendType, SocketIoType receiveType, PortConfiguration pc) throws Exception {
        open(pc);
        final Sender sender = new Sender(sendType, writeSpc, pc.getBufferSize());
        final Receiver receiver = new Receiver(receiveType, readSpc, sender.sendBuffer);

        runNonThreaded(sender, receiver, pc.getTestTimeout());
    }

    private void runThreaded(Sender sender, Receiver receiver, long timeout) throws Exception {
        EXECUTOR_SERVICE.submit(receiver);
        EXECUTOR_SERVICE.submit(sender);
        assertAll("Treaded Test Run ", () -> {
            assertTimeoutPreemptively(Duration.ofMillis(timeout), () -> {
                // Make sure all was sent
                synchronized (sender.LOCK) {
                    while (!sender.done) {
                        sender.LOCK.wait(timeout / 10);
                    }
                }
            }, "Send Timeout");
        }, () -> {
            assertTimeoutPreemptively(Duration.ofMillis(timeout), () -> {
                // Make sure all was received
                synchronized (receiver.LOCK) {
                    while (!receiver.done) {
                        receiver.LOCK.wait(timeout / 10);
                    }
                }
            }, "Reveive Timeout");
        }, () -> {
            sender.assertStateAfterExecution();
        }, () -> {
            receiver.assertStateAfterExecution();
        });

    }

    public void write_Read_Threaded(SocketIoType sendType, SocketIoType receiveType, PortConfiguration pc) throws Exception {
        open(pc);
        final Sender sender = new Sender(sendType, writeSpc, pc.getBufferSize());
        final Receiver receiver = new Receiver(receiveType, readSpc, sender.sendBuffer);

        runThreaded(sender, receiver, pc.getTestTimeout());
    }
}
