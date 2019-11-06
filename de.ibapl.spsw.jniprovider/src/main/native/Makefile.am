AM_CPPFLAGS = @JNI_INCLUDE_FLAGS@ -I @JNI_HEADER_DIR@
AM_CFLAGS = -Wall -Wextra -Wconversion -Werror
AM_LDFLAGS = -no-undefined -version-info @JNHW_VERSION_INFO@

LIB_SPSW_NAME=@JNHW_LIB_PREFIX@spsw@JNHW_LIB_SUFFIX@
LIB_SPSW_RES_NAME=@JNHW_RESOURCES_DIR@/$(LIB_SPSW_NAME)

lib_LTLIBRARIES = libspsw.la

if USE_WIN_API
noinst_LTLIBRARIES = libwin-api-spsw.la
libspsw_la_LIBADD = ./libwin-api-spsw.la
else
noinst_LTLIBRARIES = libtermios-spsw.la
libspsw_la_LIBADD = ./libtermios-spsw.la
endif

libspsw_la_SOURCES = \
 spsw-jni.c \
 Exceptions.c \
 AbstractSerialPortSocketIO.c

if USE_WIN_API
libwin_api_spsw_la_SOURCES = \
 WinApiCommon.c \
 WinApiSettings.c \
 WinApiIO.c
else
libtermios_spsw_la_SOURCES = \
 TermiosCommon.c \
 TermiosSettings.c \
 TermiosIO.c
endif


#hook into make all
all-local: $(LIB_SPSW_RES_NAME)

install-jnhw-resources: $(LIB_SPSW_RES_NAME)

$(LIB_SPSW_RES_NAME): libspsw.la
	$(MKDIR_P) @JNHW_RESOURCES_DIR@
	cp ./.libs/$(LIB_SPSW_NAME) $(LIB_SPSW_RES_NAME)
	$(STRIP) $(JNHW_STRIP_FLAG) $(LIB_SPSW_RES_NAME)
