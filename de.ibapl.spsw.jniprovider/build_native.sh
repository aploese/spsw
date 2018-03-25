#!/bin/bash
mvn clean compile -P x86_64-linux-gnu-gcc && \
mvn clean compile -P i386-linux-gnu-gcc && \
mvn clean compile -P arm-linux-gnueabihf-gcc && \
mvn clean compile -P arm-linux-gnueabi-gcc && \
mvn clean compile -P aarch64-linux-gnu-gcc && \
mvn clean compile -P mips-linux-gnu-gcc && \
mvn clean compile -P mipsel-linux-gnu-gcc && \
mvn clean compile -P mips64-linux-gnuabi64-gcc && \
mvn clean compile -P mips64el-linux-gnuabi64-gcc && \
mvn clean compile -P i686-w64-mingw32-gcc && \
mvn clean compile -P x86_64-w64-mingw32-gcc && \
mvn clean install -DskipTests
