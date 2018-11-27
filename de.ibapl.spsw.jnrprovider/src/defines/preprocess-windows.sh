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
mkdir -p windows/$DUMPMACHINE

#obsolete "ulimit" 

for d in "windows" "sdkddkver" "synchapi" "ioapiset" "winresrc" "fileapi" "excpt" "stdarg" "windef" "basetsd" "ntstatus" "minwinbase" "winbase" "wingdi" "winuser" "winnls" "wincon" "winver" "winreg" "winnetwk" "virtdisk" "cderr" "dde" "ddeml" "dlgs" "lzexpand" "mmsystem" "nb30" "rpc" "shellapi" "winperf" "winsock" "wincrypt" "winefs" "winscard" "winspool" "ole" "commdlg" "stralign" "ole2" "winsvc" "mcx" "imm"
do
  echo "#include <$d.h>" > c.c
  $GCC -dD -dI -E c.c > windows/$DUMPMACHINE/$d-prepocessed.h
#  $GCC -dM -E c.c > $MULTIARCH/$d.defines
#  $GCC -fdump-translation-unit c.c
#  mv c.c.001t.tu $MULTIARCH/$d.c.001t.tu
#  castxml --castxml-cc-gnu-c $GCC --castxml-output=1 -o $MULTIARCH/$d.xml c.c
  rm c.c
done




