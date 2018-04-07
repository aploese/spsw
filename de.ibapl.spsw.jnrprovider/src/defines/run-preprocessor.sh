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
for d in "termios.h" "sys/ioctl.h" "poll.h" "fcntl.h" "stdio.h" "sys/eventfd.h"; do
echo "#include <$d>" | $GCC -dD -dI -E -xc - > $MULTIARCH/$d.txt
done




