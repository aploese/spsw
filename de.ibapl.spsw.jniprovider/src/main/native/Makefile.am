AM_CPPFLAGS = @JNI_INCLUDE_FLAGS@ -I @JNI_HEADER_DIR@
AM_CFLAGS = -Wall -Werror
AM_LDFLAGS = -no-undefined -version-info @JNHW_VERSION_INFO@

LIB_SPSW_NAME=@JNHW_LIB_PREFIX@spsw@JNHW_LIB_SUFFIX@
LIB_SPSW_RES_NAME=@JNHW_RESOURCES_DIR@/$(LIB_SPSW_NAME)
 
lib_LTLIBRARIES = libspsw.la
noinst_LTLIBRARIES = libposix.la
libspsw_la_LIBADD = ./libposix.la

libspsw_la_SOURCES = \
 spsw-jni.c

libposix_la_SOURCES = \
 Exceptions.c \
 TermiosCommon.c \
 TermiosSettings.c \
 TermiosIO.c

#hook into make all
all-local: $(LIB_SPSW_RES_NAME)

install-jnhw-resources: $(LIB_SPSW_RES_NAME)

$(LIB_SPSW_RES_NAME): libspsw.la
	$(MKDIR_P) @JNHW_RESOURCES_DIR@
	cp ./.libs/$(LIB_SPSW_NAME) $(LIB_SPSW_RES_NAME)
	$(STRIP) $(JNHW_STRIP_FLAG) $(LIB_SPSW_RES_NAME)
