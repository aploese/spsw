#!/bin/sh

find -name "libspsw*.dll" -exec rm {} \;
find -name "libspsw*.so.?" -exec rm {} \;
find -name "libspsw*.dylib" -exec rm {} \;
