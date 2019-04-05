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
package de.ibapl.spsw.demo.ascii;

import de.ibapl.spsw.api.DataBits;
import de.ibapl.spsw.api.FlowControl;
import de.ibapl.spsw.api.Parity;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.SerialPortSocketFactory;
import de.ibapl.spsw.api.Speed;
import de.ibapl.spsw.api.StopBits;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.ServiceLoader;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author aploese
 */
public class AsciiDemoMain {

    static class Reader implements Runnable {

        ReadableByteChannel channel;

        private Reader(ReadableByteChannel readChannel) {
            this.channel = readChannel;
        }

        @Override
        public void run() {
            ByteBuffer buff = ByteBuffer.allocateDirect(2048);
            while (channel.isOpen()) {
                try {
                    buff.clear();
                    int count = channel.read(buff);
                    buff.flip();
                    char[] chars = new char[count];
                    for (int i = 0; i < count; i++) {
                        chars[i] = (char) buff.get();
                    }
                    System.out.append("Received: ").print(chars);
                    System.out.println();
                } catch (AsynchronousCloseException ace) {

                } catch (IOException ioe) {

                }

            }
        }

    }

    static SerialPortSocketFactory spsf;

    private static void printHelp(Options opts) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(300);
        formatter.printHelp("openv4j-memory-image", opts);
    }

    static private SerialPortSocketFactory getSerialPortSocketFactory() {
        ServiceLoader<SerialPortSocketFactory> serviceLoader = ServiceLoader.load(SerialPortSocketFactory.class);
        Iterator<SerialPortSocketFactory> iterator = serviceLoader.iterator();
        if (!iterator.hasNext()) {
            throw new RuntimeException("No implementation of SerialPortSocket found - Please add an SPSW provider to the class path");
        }
        final SerialPortSocketFactory result = iterator.next();
        if (iterator.hasNext()) {
            throw new RuntimeException("More than one implementation of SerialPortSocket found - Please make sure only one SPSW provider is in the class path");
        }
        return result;
    }

    static private CommandLine parseCommandLine(String[] args) {
        Options options = new Options();
        Option opt;

        opt = new Option("h", "help", false, "print this help message");
        options.addOption(opt);

        opt = new Option("n", "port", true, "serial port to use");
        opt.setRequired(true);
        opt.setArgName("port");
        opt.setType(String.class);
        options.addOption(opt);

        opt = new Option("s", "speed", true, "speed to use");
        opt.setRequired(true);
        opt.setArgName("speed");
        opt.setType(Integer.class);
        options.addOption(opt);

        opt = new Option("db", "databits", true, "databits to use");
        opt.setRequired(true);
        opt.setArgName("databits");
        opt.setType(Integer.class);
        options.addOption(opt);

        opt = new Option("sb", "stopbits", true, "stopbits to use");
        opt.setRequired(true);
        opt.setArgName("stopbits");
        opt.setType(Integer.class);
        options.addOption(opt);

        opt = new Option("p", "parity", true, "parity to use");
        opt.setRequired(true);
        opt.setArgName("parity");
        opt.setType(String.class);
        options.addOption(opt);

        opt = new Option("t", "text", true, "text to send, otherwise receive mode");
        opt.setArgName("text");
        opt.setType(String.class);
        options.addOption(opt);

        CommandLineParser cmdParser = new DefaultParser();
        CommandLine cmd = null;

        try {
            cmd = cmdParser.parse(options, args);
        } catch (ParseException ex) {
            printHelp(options);
            return null;
        }

        if (cmd.hasOption("help") || args.length == 0) {
            printHelp(options);
            return null;
        }
        return cmd;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        spsf = getSerialPortSocketFactory();

        CommandLine cmd = parseCommandLine(args);
        if (cmd == null) {
            return;
        }

        final String portName = cmd.getOptionValue("port");
        final Speed speed = Speed.fromNative(Integer.parseInt(cmd.getOptionValue("speed")));
        final StopBits stopBits = StopBits.fromNative(Float.parseFloat(cmd.getOptionValue("stopbits")));
        final DataBits dataBits = DataBits.fromNative(Integer.parseInt(cmd.getOptionValue("databits")));
        final Parity parity = Parity.valueOf(cmd.getOptionValue("parity"));

        SerialPortSocket sps;
        try {
            sps = spsf.open(portName, speed, dataBits, stopBits, parity, FlowControl.getFC_NONE());
        } catch (IOException ioe) {
            System.out.println("de.ibapl.spsw.demo.asciidemo.AsciiDemoMain.main() Ex: " + ioe);
            return;
        }

        if (cmd.hasOption("text")) {
            final String text = cmd.getOptionValue("text");
            final ByteBuffer buffer = ByteBuffer.allocateDirect(2048);
            final WritableByteChannel channel = sps;
            try {
                while (true) {
                    String textToSent = String.format("%s: %s", LocalDateTime.now(), text);
                    buffer.clear();
                    buffer.put(textToSent.getBytes(), 0, textToSent.length());
                    buffer.flip();
                    channel.write(buffer);
                    System.out.append("Send: ").println(textToSent);
                    if (System.in.available() > 0) {
                        switch (System.in.read()) {
                            case 'q':
                                System.out.print("Bye will close down!");
                                sps.close();
                                break;
                            default:
                        }
                    }
                }

            } catch (IOException ex) {
            }

        } else {
            new Thread(new Reader(sps)).start();
            try {

                switch (System.in.read()) {
                    case 'q':
                        System.out.print("Bye will close down!");
                        sps.close();
                        break;
                    default:
                }
            } catch (IOException ioe) {
            }

        }

        System.out.println("CLOSE");

    }

}
