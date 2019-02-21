#!/bin/sh

mvn clean compile

# "x86_64-linux-gnu"
# "i386-linux-gnu"

for d in\
 "x86_64-w64-mingw32"\
 "i686-w64-mingw32"
do
  ./configure --host=$d --prefix=$PWD/target/$d --with-jni-include-dir="$PWD/jni-includes/openjdk11 $PWD/jni-includes/openjdk11/win32" || exit 1
  make clean || exit 1
  make || exit 1
  make install || exit 1
done