#!/bin/sh

GCC=gcc

for i in "$@"
do
 GCC=$i
done
DUMPMACHINE=`$GCC -dumpmachine`

echo $GCC
echo $DUMPMACHINE

#create all subfolders
mkdir -p unix/$DUMPMACHINE/sys/

for d in "sys/ioctl"
do
  echo "#include <$d.h>" > c.c
  $GCC -dD -dI -E c.c > unix/$DUMPMACHINE/$d-prepocessed.h
#  $GCC -dM -E c.c > $MULTIARCH/$d.defines
#  $GCC -fdump-translation-unit c.c
#  mv c.c.001t.tu $MULTIARCH/$d.c.001t.tu
#  castxml --castxml-cc-gnu-c $GCC --castxml-output=1 -o $MULTIARCH/$d.xml c.c
  rm c.c
done




