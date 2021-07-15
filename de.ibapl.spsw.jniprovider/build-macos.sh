export CC=/usr/bin/cc
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-16.0.1.jdk/Contents/Home/
/Users/user/maven/bin/mvn clean compile  test-compile  || exit 1
./configure --with-jni-include-dir="$JAVA_HOME/include/ $JAVA_HOME/include/darwin/"  || exit 1
 make clean  || exit 1
make  || exit 1
/Users/user/maven/bin/mvn install -PBaselineTests
