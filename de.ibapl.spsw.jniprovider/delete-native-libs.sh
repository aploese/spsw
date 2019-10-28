#!/bin/sh

find -name "libjnhw*.dll" -exec rm {} \;
find -name "libjnhw*.so.?" -exec rm {} \;
find -name "libjnhw*.dylib" -exec rm {} \;
