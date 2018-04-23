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
mkdir -p isoc/$DUMPMACHINE/

#obsolete "ulimit" 

for d in "assert" "complex" "ctype" "errno" "fenv" "float" "inttypes" "iso646" "limits" "locale" "math" "setjmp" "signal" "stdarg" "stdbool" "stddef" "stdint" "stdio" "stdlib" "string" "tgmath" "time" "wchar" "wctype" 
do
  echo "#include <$d.h>" > c.c
  $GCC -dD -dI -E c.c > isoc/$DUMPMACHINE/$d-prepocessed.h
#  $GCC -dM -E c.c > $MULTIARCH/$d.defines
#  $GCC -fdump-translation-unit c.c
#  mv c.c.001t.tu $MULTIARCH/$d.c.001t.tu
#  castxml --castxml-cc-gnu-c $GCC --castxml-output=1 -o $MULTIARCH/$d.xml c.c
  rm c.c
done




