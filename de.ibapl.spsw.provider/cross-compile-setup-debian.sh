#!/bin/sh
apt install gcc-mingw-w64

dpkg --add-architecture amd64
apt install libc6-dev:amd64 libgcc-6-dev:amd64
apt install lib64gcc-6-dev

dpkg --add-architecture i386
apt install libc6-dev:i386 libgcc-6-dev:i386
apt install lib32gcc-6-dev

dpkg --add-architecture armhf
apt install gcc-arm-linux-gnueabihf libc6-dev:armhf libgcc-6-dev:armhf

dpkg --add-architecture mipsel
apt install gcc-mipsel-linux-gnu libc6-dev:mipsel libgcc-6-dev:mipsel

