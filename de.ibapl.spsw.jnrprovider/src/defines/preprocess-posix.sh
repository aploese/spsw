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

echo "" | $GCC -dD -dI -E -xc - > $MULTIARCH/empty.txt

#create all subfolders
mkdir -p posix/$MULTIARCH/sys/

#obsolete "ulimit" 

for d in "aio" "arpa/inet" "assert" "complex" "cpio" "ctype" "dirent" "dlfcn" "errno" "fcntl" "fenv" "float" "fmtmsg" "fnmatch" "ftw" "glob" "grp" "iconv" "inttypes" "iso646" "langinfo" "libgen" "limits" "locale" "math" "monetary" "mqueue" "ndbm" "net/if" "netdb" "netinet/in" "netinet/tcp" "nl_types" "poll" "pthread" "pwd" "regex" "sched" "search" "semaphore" "setjmp" "signal" "spawn" "stdarg" "stdbool" "stddef" "stdint" "stdio" "stdlib" "string" "strings" "stropts" "sys/ipc" "sys/mman" "sys/msg" "sys/resource" "sys/select" "sys/sem" "sys/shm" "sys/socket" "sys/stat" "sys/statvfs" "sys/time" "sys/times" "sys/types" "sys/uio" "sys/un" "sys/utsname" "sys/wait" "syslog" "tar" "termios" "tgmath" "time" "trace" "ulimit" "unistd" "utime" "utmpx" "wchar" "wctype" "wordexp"
do
  echo "#include <$d.h>" > c.c
  $GCC -dD -dI -E c.c > posix/$MULTIARCH/$d-prepocessed.h
#  $GCC -dM -E c.c > $MULTIARCH/$d.defines
#  $GCC -fdump-translation-unit c.c
#  mv c.c.001t.tu $MULTIARCH/$d.c.001t.tu
#  castxml --castxml-cc-gnu-c $GCC --castxml-output=1 -o $MULTIARCH/$d.xml c.c
  rm c.c
done




