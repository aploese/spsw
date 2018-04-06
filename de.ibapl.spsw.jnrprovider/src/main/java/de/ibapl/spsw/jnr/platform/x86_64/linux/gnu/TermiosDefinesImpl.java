package de.ibapl.spsw.jnr.platform.x86_64.linux.gnu;

import java.util.function.BiConsumer;import de.ibapl.spsw.jnr.Termios_H.TermiosDefines;

public class TermiosDefinesImpl implements TermiosDefines {
	// Definition is: INTEGER
	@Override
	public short NCCS() { return 32; }
	 //value in define: 1
	// Definition is: BOOLEAN
	@Override
	public boolean _HAVE_STRUCT_TERMIOS_C_ISPEED() { return true; }
	 //value in define: 1
	// Definition is: BOOLEAN
	@Override
	public boolean _HAVE_STRUCT_TERMIOS_C_OSPEED() { return true; }
	// Definition is: INTEGER
	@Override
	public Short VINTR() { return 0; }
	// Definition is: INTEGER
	@Override
	public Short VQUIT() { return 1; }
	// Definition is: INTEGER
	@Override
	public Short VERASE() { return 2; }
	// Definition is: INTEGER
	@Override
	public Short VKILL() { return 3; }
	// Definition is: INTEGER
	@Override
	public Short VEOF() { return 4; }
	// Definition is: INTEGER
	@Override
	public Short VTIME() { return 5; }
	// Definition is: INTEGER
	@Override
	public Short VMIN() { return 6; }
	// Definition is: INTEGER
	@Override
	public Short VSWTC() { return 7; }
	// Definition is: NOT_DEFINED
	@Override
	public Short VSWTCH() { return null; }
	// Definition is: INTEGER
	@Override
	public Short VSTART() { return 8; }
	// Definition is: INTEGER
	@Override
	public Short VSTOP() { return 9; }
	// Definition is: INTEGER
	@Override
	public Short VSUSP() { return 10; }
	// Definition is: INTEGER
	@Override
	public Short VEOL() { return 11; }
	// Definition is: INTEGER
	@Override
	public Short VREPRINT() { return 12; }
	// Definition is: INTEGER
	@Override
	public Short VDISCARD() { return 13; }
	// Definition is: INTEGER
	@Override
	public Short VWERASE() { return 14; }
	// Definition is: INTEGER
	@Override
	public Short VLNEXT() { return 15; }
	// Definition is: INTEGER
	@Override
	public Short VEOL2() { return 16; }
	// Definition is: OCTAL
	@Override
	public short IGNBRK() { return 0000001; }
	// Definition is: OCTAL
	@Override
	public short BRKINT() { return 0000002; }
	// Definition is: OCTAL
	@Override
	public short IGNPAR() { return 0000004; }
	// Definition is: OCTAL
	@Override
	public short PARMRK() { return 0000010; }
	// Definition is: OCTAL
	@Override
	public short INPCK() { return 0000020; }
	// Definition is: OCTAL
	@Override
	public short ISTRIP() { return 0000040; }
	// Definition is: OCTAL
	@Override
	public short INLCR() { return 0000100; }
	// Definition is: OCTAL
	@Override
	public short IGNCR() { return 0000200; }
	// Definition is: OCTAL
	@Override
	public short ICRNL() { return 0000400; }
	// Definition is: OCTAL
	@Override
	public short IUCLC() { return 0001000; }
	// Definition is: OCTAL
	@Override
	public short IXON() { return 0002000; }
	// Definition is: OCTAL
	@Override
	public short IXANY() { return 0004000; }
	// Definition is: OCTAL
	@Override
	public short IXOFF() { return 0010000; }
	// Definition is: OCTAL
	@Override
	public short IMAXBEL() { return 0020000; }
	// Definition is: OCTAL
	@Override
	public short IUTF8() { return 0040000; }
	// Definition is: OCTAL
	@Override
	public short OPOST() { return 0000001; }
	// Definition is: OCTAL
	@Override
	public short OLCUC() { return 0000002; }
	// Definition is: OCTAL
	@Override
	public short ONLCR() { return 0000004; }
	// Definition is: OCTAL
	@Override
	public short OCRNL() { return 0000010; }
	// Definition is: OCTAL
	@Override
	public short ONOCR() { return 0000020; }
	// Definition is: OCTAL
	@Override
	public short ONLRET() { return 0000040; }
	// Definition is: OCTAL
	@Override
	public short OFILL() { return 0000100; }
	// Definition is: OCTAL
	@Override
	public short OFDEL() { return 0000200; }
	// Definition is: OCTAL
	@Override
	public short NLDLY() { return 0000400; }
	// Definition is: OCTAL
	@Override
	public short NL0() { return 0000000; }
	// Definition is: OCTAL
	@Override
	public short NL1() { return 0000400; }
	// Definition is: OCTAL
	@Override
	public short CRDLY() { return 0003000; }
	// Definition is: OCTAL
	@Override
	public short CR0() { return 0000000; }
	// Definition is: OCTAL
	@Override
	public short CR1() { return 0001000; }
	// Definition is: OCTAL
	@Override
	public short CR2() { return 0002000; }
	// Definition is: OCTAL
	@Override
	public short CR3() { return 0003000; }
	// Definition is: OCTAL
	@Override
	public short TABDLY() { return 0014000; }
	// Definition is: OCTAL
	@Override
	public short TAB0() { return 0000000; }
	// Definition is: OCTAL
	@Override
	public short TAB1() { return 0004000; }
	// Definition is: OCTAL
	@Override
	public short TAB2() { return 0010000; }
	// Definition is: OCTAL
	@Override
	public short TAB3() { return 0014000; }
	// Definition is: OCTAL
	@Override
	public short BSDLY() { return 0020000; }
	// Definition is: OCTAL
	@Override
	public short BS0() { return 0000000; }
	// Definition is: OCTAL
	@Override
	public short BS1() { return 0020000; }
	// Definition is: OCTAL
	@Override
	public short FFDLY() { return (short) 0100000; }
	// Definition is: OCTAL
	@Override
	public short FF0() { return 0000000; }
	// Definition is: OCTAL
	@Override
	public short FF1() { return (short) 0100000; }
	// Definition is: OCTAL
	@Override
	public short VTDLY() { return 0040000; }
	// Definition is: OCTAL
	@Override
	public short VT0() { return 0000000; }
	// Definition is: OCTAL
	@Override
	public short VT1() { return 0040000; }
	// Definition is: OCTAL
	@Override
	public short XTABS() { return 0014000; }
	// Definition is: OCTAL
	@Override
	public short CBAUD() { return 0010017; }
	// Definition is: OCTAL
	@Override
	public short B0() { return 0000000; }
	// Definition is: OCTAL
	@Override
	public short B50() { return 0000001; }
	// Definition is: OCTAL
	@Override
	public short B75() { return 0000002; }
	// Definition is: OCTAL
	@Override
	public short B110() { return 0000003; }
	// Definition is: OCTAL
	@Override
	public short B134() { return 0000004; }
	// Definition is: OCTAL
	@Override
	public short B150() { return 0000005; }
	// Definition is: OCTAL
	@Override
	public short B200() { return 0000006; }
	// Definition is: OCTAL
	@Override
	public short B300() { return 0000007; }
	// Definition is: OCTAL
	@Override
	public short B600() { return 0000010; }
	// Definition is: OCTAL
	@Override
	public short B1200() { return 0000011; }
	// Definition is: OCTAL
	@Override
	public short B1800() { return 0000012; }
	// Definition is: OCTAL
	@Override
	public short B2400() { return 0000013; }
	// Definition is: OCTAL
	@Override
	public short B4800() { return 0000014; }
	// Definition is: OCTAL
	@Override
	public short B9600() { return 0000015; }
	// Definition is: OCTAL
	@Override
	public short B19200() { return 0000016; }
	// Definition is: OCTAL
	@Override
	public short B38400() { return 0000017; }
	 //alias for: B19200
	// Definition is: OCTAL
	@Override
	public short EXTA() { return 0000016; }
	 //alias for: B38400
	// Definition is: OCTAL
	@Override
	public short EXTB() { return 0000017; }
	// Definition is: OCTAL
	@Override
	public short CSIZE() { return 0000060; }
	// Definition is: OCTAL
	@Override
	public short CS5() { return 0000000; }
	// Definition is: OCTAL
	@Override
	public short CS6() { return 0000020; }
	// Definition is: OCTAL
	@Override
	public short CS7() { return 0000040; }
	// Definition is: OCTAL
	@Override
	public short CS8() { return 0000060; }
	// Definition is: OCTAL
	@Override
	public short CSTOPB() { return 0000100; }
	// Definition is: OCTAL
	@Override
	public short CREAD() { return 0000200; }
	// Definition is: OCTAL
	@Override
	public short PARENB() { return 0000400; }
	// Definition is: OCTAL
	@Override
	public short PARODD() { return 0001000; }
	// Definition is: OCTAL
	@Override
	public short HUPCL() { return 0002000; }
	// Definition is: OCTAL
	@Override
	public short CLOCAL() { return 0004000; }
	// Definition is: OCTAL
	@Override
	public short CBAUDEX() { return 0010000; }
	// Definition is: OCTAL
	@Override
	public short B57600() { return 0010001; }
	// Definition is: OCTAL
	@Override
	public short B115200() { return 0010002; }
	// Definition is: OCTAL
	@Override
	public short B230400() { return 0010003; }
	// Definition is: OCTAL
	@Override
	public short B460800() { return 0010004; }
	// Definition is: OCTAL
	@Override
	public short B500000() { return 0010005; }
	// Definition is: OCTAL
	@Override
	public short B576000() { return 0010006; }
	// Definition is: OCTAL
	@Override
	public short B921600() { return 0010007; }
	// Definition is: OCTAL
	@Override
	public short B1000000() { return 0010010; }
	// Definition is: OCTAL
	@Override
	public short B1152000() { return 0010011; }
	// Definition is: OCTAL
	@Override
	public short B1500000() { return 0010012; }
	// Definition is: OCTAL
	@Override
	public short B2000000() { return 0010013; }
	// Definition is: OCTAL
	@Override
	public short B2500000() { return 0010014; }
	// Definition is: OCTAL
	@Override
	public short B3000000() { return 0010015; }
	// Definition is: OCTAL
	@Override
	public short B3500000() { return 0010016; }
	// Definition is: OCTAL
	@Override
	public short B4000000() { return 0010017; }
	 //alias for: B4000000
	// Definition is: OCTAL
	@Override
	public short __MAX_BAUD() { return 0010017; }
	// Definition is: OCTAL
	@Override
	public int CIBAUD() { return 002003600000; }
	// Definition is: OCTAL
	@Override
	public Integer CMSPAR() { return 010000000000; }
	// Definition is: OCTAL
	@Override
	public int CRTSCTS() { return (int) 020000000000; }
	// Definition is: OCTAL
	@Override
	public short ISIG() { return 0000001; }
	// Definition is: OCTAL
	@Override
	public short ICANON() { return 0000002; }
	// Definition is: OCTAL
	@Override
	public short XCASE() { return 0000004; }
	// Definition is: OCTAL
	@Override
	public short ECHO() { return 0000010; }
	// Definition is: OCTAL
	@Override
	public short ECHOE() { return 0000020; }
	// Definition is: OCTAL
	@Override
	public short ECHOK() { return 0000040; }
	// Definition is: OCTAL
	@Override
	public short ECHONL() { return 0000100; }
	// Definition is: OCTAL
	@Override
	public short NOFLSH() { return 0000200; }
	// Definition is: OCTAL
	@Override
	public short TOSTOP() { return 0000400; }
	// Definition is: OCTAL
	@Override
	public short ECHOCTL() { return 0001000; }
	// Definition is: OCTAL
	@Override
	public short ECHOPRT() { return 0002000; }
	// Definition is: OCTAL
	@Override
	public short ECHOKE() { return 0004000; }
	// Definition is: OCTAL
	@Override
	public short FLUSHO() { return 0010000; }
	// Definition is: OCTAL
	@Override
	public short PENDIN() { return 0040000; }
	// Definition is: OCTAL
	@Override
	public short IEXTEN() { return (short) 0100000; }
	// Definition is: OCTAL
	@Override
	public int EXTPROC() { return 0200000; }
	// Definition is: INTEGER
	@Override
	public short TCOOFF() { return 0; }
	// Definition is: INTEGER
	@Override
	public short TCOON() { return 1; }
	// Definition is: INTEGER
	@Override
	public short TCIOFF() { return 2; }
	// Definition is: INTEGER
	@Override
	public short TCION() { return 3; }
	// Definition is: INTEGER
	@Override
	public short TCIFLUSH() { return 0; }
	// Definition is: INTEGER
	@Override
	public short TCOFLUSH() { return 1; }
	// Definition is: INTEGER
	@Override
	public short TCIOFLUSH() { return 2; }
	// Definition is: INTEGER
	@Override
	public short TCSANOW() { return 0; }
	// Definition is: INTEGER
	@Override
	public short TCSADRAIN() { return 1; }
	// Definition is: INTEGER
	@Override
	public short TCSAFLUSH() { return 2; }
	// Definition is: NOT_DEFINED
	@Override
	public Short PAREXT() { return null; }

	@Override
	public void getAliases(BiConsumer<String, String> biConsumer) {
		biConsumer.accept("EXTA", "B19200");
		biConsumer.accept("__MAX_BAUD", "B4000000");
		biConsumer.accept("EXTB", "B38400");
	}
}
