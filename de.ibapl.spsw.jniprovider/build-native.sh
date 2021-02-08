#!/bin/sh

mvn clean compile

./configure --prefix=$PWD/target/ || exit 1
make clean || exit 1
make || exit 1

mvn install -PBaselineTests
