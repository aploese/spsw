#!/bin/sh

GCC=gcc
MULTIARCH=`$GCC --print-multiarch`

for i in "$@" 
do
  MULTIARCH=$i
  GCC="$MULTIARCH-gcc"
done

echo $GCC
echo $MULTIARCH

#create all subfolders
mkdir -p linux/$MULTIARCH/sys/

for d in "sys/eventfd"
do
  echo "#include <$d.h>" > c.c
  $GCC -dD -dI -E c.c > linux/$MULTIARCH/$d-prepocessed.h
#  $GCC -dM -E c.c > $MULTIARCH/$d.defines
#  $GCC -fdump-translation-unit c.c
#  mv c.c.001t.tu $MULTIARCH/$d.c.001t.tu
#  castxml --castxml-cc-gnu-c $GCC --castxml-output=1 -o $MULTIARCH/$d.xml c.c
  rm c.c
done




