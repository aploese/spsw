#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ([2.69])
AC_INIT([spsw-jni-provider], [3.0.0], [aploese@gmx.de])
AM_INIT_AUTOMAKE([-Wall -Werror foreign])
AM_MAINTAINER_MODE

AM_PROG_AR

LT_INIT()
AC_CONFIG_SRCDIR([src/main/native/spsw-jni.c])
AC_CONFIG_HEADERS([config.h])
AC_CONFIG_MACRO_DIRS([m4])

# Checks for programs.
AC_PROG_CC
AC_PROG_INSTALL

# Check or manually define jni header files.
AC_ARG_WITH([jni-include-dir],
    [AS_HELP_STRING([--with-jni-include-dir],
    [set the jni include dir manually])],
    [JNI_INCLUDE_DIRS="$with_jni_include_dir"],
    [AX_JNI_INCLUDE_DIR])
AC_SUBST(JNI_INCLUDE_DIRS)
for JNI_INCLUDE_DIR in $JNI_INCLUDE_DIRS
do
    JNI_INCLUDE_FLAGS="$JNI_INCLUDE_FLAGS -I$JNI_INCLUDE_DIR"
done
AC_SUBST(JNI_INCLUDE_FLAGS)

JNHW_VERSION_INFO="1:0:0"
AC_SUBST(JNHW_VERSION_INFO)
JNHW_VERSION="1"
AC_SUBST(JNHW_VERSION)

JW_MULTIARCHTUPEL()
JW_LIB_PREFIX_SUFFIX()

JNHW_RESOURCES_DIR="\$(top_builddir)/src/main/resources/lib/\$(JNHW_MULTIARCH_TUPEL)"
AC_SUBST(JNHW_RESOURCES_DIR)

JNI_HEADER_DIR="\$(top_builddir)/target/generated-sources/include"
AC_SUBST(JNI_HEADER_DIR)

# Checks for libraries.

# Checks for header files.
AC_CHECK_HEADERS([sys/eventfd.h])

AC_CHECK_HEADERS([poll.h termios.h sys/ioctl.h string.h stdlib.h stdint.h errno.h])

AC_CHECK_HEADERS([windows.h], [spsw_use_win=true], [spsw_use_win=false],[])

# Checks for typedefs, structures, and compiler characteristics.

# Checks for library functions.

AC_CONFIG_FILES([Makefile src/Makefile src/main/Makefile src/main/native/Makefile])


AM_CONDITIONAL([USE_WIN_API], [test x$spsw_use_win = xtrue])

AC_OUTPUT
