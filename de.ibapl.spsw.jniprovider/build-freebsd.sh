export JAVA_HOME=/usr/local/openjdk15/
mvn clean compile  test-compile  || exit 1
./configure --with-jni-include-dir="$JAVA_HOME/include/ $JAVA_HOME/include/freebsd/"  || exit 1
gmake clean  || exit 1
gmake  || exit 1
mvn install -PBaselineTests
