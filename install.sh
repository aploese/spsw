#!/bin/sh
mvn clean -P all-cross-compile
mvn install -P all-cross-compile
cd spsw
mvn install -P bundle 
cp target/spsw-1.0.0-SNAPSHOT-bundle.jar ~/.m2/repository/de/ibapl/spsw/spsw/1.0.0-SNAPSHOT/spsw-1.0.0-SNAPSHOT.jar
cd ..
