#!/bin/sh

GCC=gcc
MULTIARCH=`$GCC --print-multiarch`

for i in "$@"
do
MULTIARCH=$i
GCC="$MULTIARCH-gcc"
done;

echo $GCC
echo $MULTIARCH

echo "" | $GCC -dD -dI -E -xc - > $MULTIARCH/empty.txt

for d in "termios" "sys/ioctl" "poll" "fcntl" "stdio" "sys/eventfd" "elf"; do
  echo "#include <$d.h>" | $GCC -dD -dI -E -xc - > $MULTIARCH/$d-prepocessor.txt
  echo "#include <$d.h>" | $GCC -dM -E -xc - > $MULTIARCH/$d.defines
  echo "#include <$d.h>" | castxml --castxml-cc-gnu-c $GCC --castxml-output=1 -x c -o $MULTIARCH/$d.xml -
done




