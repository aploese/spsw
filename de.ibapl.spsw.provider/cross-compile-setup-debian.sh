#!/bin/sh
apt install gcc-mingw-w64

dpkg --add-architecture amd64
apt update
apt install libc6-dev:amd64
apt install lib64gcc-6-dev

dpkg --add-architecture i386
apt update
apt install libc6-dev:i386
apt install lib32gcc-6-dev

#dpkg --add-architecture armhf
apt install gcc-arm-linux-gnueabihf

#dpkg --add-architecture mipsel
apt install gcc-mipsel-linux-gnu

