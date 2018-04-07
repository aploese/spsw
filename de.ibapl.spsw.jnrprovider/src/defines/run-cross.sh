#!/bin/sh

for d in "aarch64-linux-gnu" "arm-linux-gnueabi" "arm-linux-gnueabihf" "mips-linux-gnu" "mips64-linux-gnuabi64" "mips64el-linux-gnuabi64" "mipsel-linux-gnu" "x86_64-linux-gnu"; do
./run-preprocessor.sh $d;
done




