#!/bin/sh

autoreconf -i

mvn clean compile

# "x86_64-linux-gnu"
# "i386-linux-gnu"

for d in\
 "x86_64-w64-mingw32"\
 "i686-w64-mingw32"\
 "aarch64-linux-gnu"\
 "arm-linux-gnueabi"\
 "arm-linux-gnueabihf"\
 "mips-linux-gnu"\
 "mips64-linux-gnuabi64"\
 "mips64el-linux-gnuabi64"\
 "mipsel-linux-gnu"
do
  ./configure --host=$d --prefix=`pwd`/target/$d || exit 1
  make clean || exit 1
  make || exit 1
  make install || exit 1
done

./configure --prefix=`pwd`/target || exit 1
make clean || exit 1
make || exit 1
make install || exit 1

mvn install || exit 1


