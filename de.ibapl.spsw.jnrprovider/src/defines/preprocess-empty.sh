#!/bin/sh

GCC=gcc

for i in "$@"
do
 GCC=$i
done
DUMPMACHINE=`$GCC -dumpmachine`

echo $GCC
echo $DUMPMACHINE

echo | $GCC -v -dD -dI -E -xc - 2>$DUMPMACHINE-err.txt 1> $DUMPMACHINE-preprocessed.h

