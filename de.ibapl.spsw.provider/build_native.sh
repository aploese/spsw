#!/bin/bash
mvn clean && mvn compile -P linux-amd64
mvn clean && mvn compile -P linux-i386
mvn clean && mvn compile -P arm-linux-gnueabihf-gcc
mvn clean && mvn compile -P i686-w64-mingw32-gcc
mvn clean && mvn compile -P x86_64-w64-mingw32-gcc
