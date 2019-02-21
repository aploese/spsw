#!/bin/sh

autoreconf -i

mvn clean compile

# "x86_64-linux-gnu"
# "i386-linux-gnu"

for d in\
 "aarch64-linux-gnu"\
 "arm-linux-gnueabi"\
 "arm-linux-gnueabihf"\
 "mips-linux-gnu"\
 "mipsel-linux-gnu"\
 "mips64-linux-gnuabi64"\
 "mips64-linux-gnuabin32"\
 "mips64el-linux-gnuabi64"\
 "mips64el-linux-gnuabin32"
do
  ./configure --host=$d --prefix=$PWD/target/$d
  make clean
  make
  make install
done


./configure --prefix=$PWD/target
make clean
make
make install

mvn install


