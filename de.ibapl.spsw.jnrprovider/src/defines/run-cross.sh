#!/bin/sh

for d in "aarch64-linux-gnu-gcc" "arm-linux-gnueabi-gcc" "arm-linux-gnueabihf-gcc" "mips-linux-gnu-gcc" "mips64-linux-gnuabi64-gcc" "mips64el-linux-gnuabi64-gcc" "mipsel-linux-gnu-gcc" "x86_64-linux-gnu-gcc" "i686-linux-gnu-gcc"
do
 ./preprocess-empty.sh $d;
 ./preprocess-isoc.sh $d;
 ./preprocess-posix.sh $d;
 ./preprocess-linux.sh $d;
 ./preprocess-unix.sh $d;
done

for d in "x86_64-w64-mingw32-gcc" "i686-w64-mingw32-gcc"
do
 ./preprocess-empty.sh $d;
 ./preprocess-isoc.sh $d;
 ./preprocess-windows.sh $d;
done



