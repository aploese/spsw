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
package de.ibapl.spsw.wrapper;

import de.ibapl.spsw.api.AsyncSerialPortSocket;
import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 *
 * @author aploese
 */
public class AsyncSerialPortSocketThreadPoolWrapper implements AsyncSerialPortSocket {

    final private ExecutorService es;
    final private SerialPortSocket serialPortSocket;

    public AsyncSerialPortSocketThreadPoolWrapper(SerialPortSocket serialPortSocket) {
        this.es = Executors.newCachedThreadPool();
        this.serialPortSocket = serialPortSocket;
    }

    public AsyncSerialPortSocketThreadPoolWrapper(SerialPortSocket serialPortSocket, ExecutorService es) {
        this.es = es;
        this.serialPortSocket = serialPortSocket;
    }

    /**
     *
     * @param dst
     * @param callbackOnSuccess
     * @param callbackOnFailure
     * @throws IOException
     *
     * Param mayInterruptIfRunning of Future.cancel is ignored TODO remove this
     * default before final must be cancelable
     */
    @Override
    public void readAsync(ByteBuffer dst, Consumer<ByteBuffer> callbackOnSuccess, Consumer<IOException> callbackOnFailure) {
        if (dst == null) {
            throw new NullPointerException("dst is null");
        }
        es.execute(() -> {
            try {
                serialPortSocket.read(dst);
                if (callbackOnSuccess != null) {
                    callbackOnSuccess.accept(dst);
                }
            } catch (IOException e) {
                if (callbackOnFailure != null) {
                    callbackOnFailure.accept(e);
                }
            } catch (Throwable t) {
                if (callbackOnFailure != null) {
                    callbackOnFailure.accept(new IOException(t));
                }
            }
        });
    }

    @Override
    public void readAsync(ByteBuffer dst, BiConsumer<ByteBuffer, IOException> callback) {
        if (dst == null) {
            throw new NullPointerException("dst is null");
        }
        es.execute(() -> {
            try {
                serialPortSocket.read(dst);
                if (callback != null) {
                    callback.accept(dst, null);
                }
            } catch (IOException e) {
                if (callback != null) {
                    callback.accept(dst, e);
                }
            } catch (Throwable t) {
                if (callback != null) {
                    callback.accept(dst, new IOException(t));
                }
            }
        });
    }

    @Override
    public Future<ByteBuffer> readAsync(ByteBuffer dst) {
        if (dst == null) {
            throw new NullPointerException("dst is null");
        }
        return (Future<ByteBuffer>) es.submit(() -> {
            try {
                serialPortSocket.read(dst);
                return dst;
            } catch (Exception e) {
                throw e;
            } catch (Throwable t) {
                throw new Exception(t);
            }
        });
    }

    /**
     *
     * @param src
     * @param callbackOnSuccess
     * @param callbackOnFailure
     * @return
     * @throws IOException
     *
     * TODO remove this default before final must be cancelable * Param
     * mayInterruptIfRunning of Future.cancel is ignored
     */
    @Override
    public Future<ByteBuffer> writeAsync(ByteBuffer src) {
        if (src == null) {
            throw new NullPointerException("src is null");
        }
        return es.submit(() -> {
            try {
                serialPortSocket.write(src);
                return src;
            } catch (Exception e) {
                throw e;
            } catch (Throwable t) {
                throw new Exception(t);
            }
        });
    }

    @Override
    public void writeAsync(ByteBuffer src, Consumer<ByteBuffer> callbackOnSuccess, Consumer<IOException> callbackOnFailure) {
        if (src == null) {
            throw new NullPointerException("src is null");
        }
        new Thread(() -> {
            try {
                serialPortSocket.write(src);
                if (callbackOnSuccess != null) {
                    callbackOnSuccess.accept(src);
                }
            } catch (IOException ioe) {
                if (callbackOnFailure != null) {
                    callbackOnFailure.accept(ioe);
                }
            } catch (Throwable t) {
                if (callbackOnFailure != null) {
                    callbackOnFailure.accept(new IOException(t));
                }
            }
        }).start();
    }

    @Override
    public void writeAsync(ByteBuffer src, BiConsumer<ByteBuffer, IOException> callback) {
        if (src == null) {
            throw new NullPointerException("src is null");
        }
        es.execute(() -> {
            try {
                serialPortSocket.write(src);
                if (callback != null) {
                    callback.accept(src, null);
                }
            } catch (IOException ioe) {
                if (callback != null) {
                    callback.accept(src, ioe);
                }
            } catch (Throwable t) {
                if (callback != null) {
                    callback.accept(src, new IOException(t));
                }
            }
        });
    }

    @Override
    public DataBits getDatatBits() throws IOException {
        return serialPortSocket.getDatatBits();
    }

    @Override
    public Set<FlowControl> getFlowControl() throws IOException {
        return serialPortSocket.getFlowControl();
    }

    @Override
    public int getInBufferBytesCount() throws IOException {
        return serialPortSocket.getInBufferBytesCount();
    }

    @Override
    public int getInterByteReadTimeout() throws IOException {
        return serialPortSocket.getInterByteReadTimeout();
    }

    @Override
    public int getOutBufferBytesCount() throws IOException {
        return serialPortSocket.getOutBufferBytesCount();
    }

    @Override
    public int getOverallReadTimeout() throws IOException {
        return serialPortSocket.getOverallReadTimeout();
    }

    @Override
    public int getOverallWriteTimeout() throws IOException {
        return serialPortSocket.getOverallWriteTimeout();
    }

    @Override
    public Parity getParity() throws IOException {
        return serialPortSocket.getParity();
    }

    @Override
    public String getPortName() {
        return serialPortSocket.getPortName();
    }

    @Override
    public Speed getSpeed() throws IOException {
        return serialPortSocket.getSpeed();
    }

    @Override
    public StopBits getStopBits() throws IOException {
        return serialPortSocket.getStopBits();
    }

    @Override
    public char getXOFFChar() throws IOException {
        return serialPortSocket.getXOFFChar();
    }

    @Override
    public char getXONChar() throws IOException {
        return serialPortSocket.getXONChar();
    }

    @Override
    public boolean isCTS() throws IOException {
        return serialPortSocket.isCTS();
    }

    @Override
    public boolean isDCD() throws IOException {
        return serialPortSocket.isDCD();
    }

    @Override
    public boolean isDSR() throws IOException {
        return serialPortSocket.isDSR();
    }

    @Override
    public boolean isOpen() {
        return serialPortSocket.isOpen();
    }

    @Override
    public boolean isRI() throws IOException {
        return serialPortSocket.isRI();
    }

    @Override
    public void sendBreak(int duration) throws IOException {
        serialPortSocket.sendBreak(duration);
    }

    @Override
    public void sendXOFF() throws IOException {
        serialPortSocket.sendXOFF();
    }

    @Override
    public void sendXON() throws IOException {
        serialPortSocket.sendXON();
    }

    @Override
    public void setBreak(boolean value) throws IOException {
        serialPortSocket.setBreak(value);
    }

    @Override
    public void setDataBits(DataBits dataBits) throws IOException {
        serialPortSocket.setDataBits(dataBits);
    }

    @Override
    public void setDTR(boolean value) throws IOException {
        serialPortSocket.setDTR(value);
    }

    @Override
    public void setFlowControl(Set<FlowControl> flowControls) throws IOException {
        serialPortSocket.setFlowControl(flowControls);
    }

    @Override
    public void setParity(Parity parity) throws IOException {
        serialPortSocket.setParity(parity);
    }

    @Override
    public void setRTS(boolean value) throws IOException {
        serialPortSocket.setRTS(value);
    }

    @Override
    public void setSpeed(Speed speed) throws IOException {
        serialPortSocket.setSpeed(speed);
    }

    @Override
    public void setStopBits(StopBits stopBits) throws IOException {
        serialPortSocket.setStopBits(stopBits);
    }

    @Override
    public void setTimeouts(int interByteReadTimeout, int overallReadTimeout, int overallWriteTimeout) throws IOException {
        serialPortSocket.setTimeouts(interByteReadTimeout, overallReadTimeout, overallWriteTimeout);
    }

    @Override
    public void setXOFFChar(char c) throws IOException {
        serialPortSocket.setXOFFChar(c);
    }

    @Override
    public void setXONChar(char c) throws IOException {
        serialPortSocket.setXONChar(c);
    }

    @Override
    public void close() throws IOException {
        serialPortSocket.close();
    }

    @Override
    public void drainOutputBuffer() throws IOException {
        serialPortSocket.drainOutputBuffer();
    }

}
