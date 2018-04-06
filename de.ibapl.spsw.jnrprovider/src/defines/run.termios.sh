#!/bin/sh
aarch64-linux-gnu-gcc -dM -E termios_flags.c > termios_aarch64-linux-gnu.txt
arm-linux-gnueabi-gcc -dM -E termios_flags.c > termios_arm-linux-gnueabi.txt
arm-linux-gnueabihf-gcc -dM -E termios_flags.c > termios_arm-linux-gnueabihf.txt
mips-linux-gnu-gcc -dM -E termios_flags.c > termios_mips-linux-gnu.txt
mips64-linux-gnuabi64-gcc -dM -E termios_flags.c > termios_mips64-linux-gnuabi64.txt
mips64el-linux-gnuabi64-gcc -dM -E termios_flags.c > termios_mips64el-linux-gnuabi64.txt
mipsel-linux-gnu-gcc -dM -E termios_flags.c > termios_mipsel-linux-gnu.txt
#x86_64-linux-gnu-gcc -dM -E termios_flags.c > termios_x86_64-linux-gnu.txt
#i386-linux-gnu-gcc -dM -E termios_flags.c > termios_i386-linux-gnu-gcc.txt
gcc -dM -E -m64 termios_flags.c > termios_x86_64-linux-gnu.txt
gcc -dM -E -m32 termios_flags.c > termios_i386-linux-gnu-gcc.txt
