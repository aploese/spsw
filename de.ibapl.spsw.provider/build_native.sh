#!/bin/bash
mvn clean compile -P linux-amd64
mvn clean compile -P linux-i386
mvn clean compile -P arm-linux-gnueabihf-gcc
mvn clean compile -P mipsel-linux-gnu-gcc
mvn clean compile -P i686-w64-mingw32-gcc
mvn clean compile -P x86_64-w64-mingw32-gcc
