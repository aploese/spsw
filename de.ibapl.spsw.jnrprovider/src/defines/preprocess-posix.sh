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
mkdir -p posix/$DUMPMACHINE/sys/
mkdir -p posix/$DUMPMACHINE/arpa/
mkdir -p posix/$DUMPMACHINE/net/
mkdir -p posix/$DUMPMACHINE/netinet/

#obsolete "ulimit" 

for d in "aio" "arpa/inet" "cpio" "dirent" "dlfcn" "fcntl" "fmtmsg" "fnmatch" "ftw" "glob" "grp" "iconv" "langinfo" "libgen" "monetary" "mqueue" "ndbm" "net/if" "netdb" "netinet/in" "netinet/tcp" "nl_types" "poll" "pthread" "pwd" "regex" "sched" "search" "semaphore" "spawn" "strings" "stropts" "sys/ipc" "sys/mman" "sys/msg" "sys/resource" "sys/select" "sys/sem" "sys/shm" "sys/socket" "sys/stat" "sys/statvfs" "sys/time" "sys/times" "sys/types" "sys/uio" "sys/un" "sys/utsname" "sys/wait" "syslog" "tar" "termios" "trace" "ulimit" "unistd" "utime" "utmpx" "wordexp"
do
  echo "#include <$d.h>" > c.c
  $GCC -dD -dI -E c.c > posix/$DUMPMACHINE/$d-prepocessed.h
#  $GCC -dM -E c.c > $MULTIARCH/$d.defines
#  $GCC -fdump-translation-unit c.c
#  mv c.c.001t.tu $MULTIARCH/$d.c.001t.tu
#  castxml --castxml-cc-gnu-c $GCC --castxml-output=1 -o $MULTIARCH/$d.xml c.c
  rm c.c
done




