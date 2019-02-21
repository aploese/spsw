#!/bin/sh
./configure --host=x86_64-apple-darwin --with-jni-include-dir="$PWD/jni-includes/openjdk11 $PWD/jni-includes/openjdk11/darwin" \
&& make clean \
&& make 