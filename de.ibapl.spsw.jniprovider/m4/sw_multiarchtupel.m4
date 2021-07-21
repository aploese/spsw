AC_DEFUN([SW_MULTIARCHTUPEL],
[case "$host_os" in
    mingw32*)
        case "$host_cpu" in
            x86_64)
                SPSW_MULTIARCH_TUPEL="x86_64-windows-pe32+"
            ;;
            i686)
                SPSW_MULTIARCH_TUPEL="x86-windows-pe32"
            ;;
            *)
                fail Unknown CPU
            ;;
        esac
    ;;
    darwin*)
	case "$host_cpu" in
	x86_64)
        	SPSW_MULTIARCH_TUPEL="x86_64-darwin-bsd"
	;;
	*)
        	fail not supported
	;;
	esac
    ;;
    freebsd*)
	case "$host_cpu" in
	x86_64)
        	SPSW_MULTIARCH_TUPEL="x86_64-freebsd-bsd"
	;;
	*)
        	fail not supported
	;;
	esac
    ;;
    openbsd*)
	case "$host_cpu" in
	x86_64)
        	SPSW_MULTIARCH_TUPEL="x86_64-openbsd-bsd"
	;;
	aarch64)
        	SPSW_MULTIARCH_TUPEL="aarch64-openbsd-bsd"
	;;
	*)
        	fail not supported
	;;
	esac
    ;;
    *)
	case "$host_cpu" in
	i686)
        	SPSW_MULTIARCH_TUPEL="i386-\$(host_os)"
	;;
	arm*)
		SPSW_MULTIARCH_TUPEL="arm-\$(host_os)"
	;;
	*)
        	SPSW_MULTIARCH_TUPEL="\$(host_cpu)-\$(host_os)"
	;;
	esac
    ;;
esac

AC_SUBST(SPSW_MULTIARCH_TUPEL)

])
