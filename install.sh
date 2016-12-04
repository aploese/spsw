#!/bin/sh
mvn clean -P all-cross-compile
mvn install -P all-cross-compile
cd spsw-java
mvn install -P bundle 
cd ..
