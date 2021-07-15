AC_DEFUN([JW_MULTIARCHTUPEL],
[case "$host_os" in
    mingw32*)
        case "$host_cpu" in
            x86_64)
                JNHW_MULTIARCH_TUPEL="x86_64-windows-pe32+"
            ;;
            i686)
                JNHW_MULTIARCH_TUPEL="x86-windows-pe32"
            ;;
            *)
                fail Unknown CPU
            ;;
        esac
    ;;
    darwin*)
	case "$host_cpu" in
	x86_64)
        	JNHW_MULTIARCH_TUPEL="x86_64-darwin-bsd"
	;;
	*)
        	fail not supported
	;;
	esac
    ;;
    freebsd*)
	case "$host_cpu" in
	x86_64)
        	JNHW_MULTIARCH_TUPEL="x86_64-freebsd-bsd"
	;;
	*)
        	fail not supported
	;;
	esac
    ;;
    openbsd*)
	case "$host_cpu" in
	x86_64)
        	JNHW_MULTIARCH_TUPEL="x86_64-openbsd-bsd"
	;;
	aarch64)
        	JNHW_MULTIARCH_TUPEL="aarch64-openbsd-bsd"
	;;
	*)
        	fail not supported
	;;
	esac
    ;;
    *)
	case "$host_cpu" in
	i686)
        	JNHW_MULTIARCH_TUPEL="i386-\$(host_os)"
	;;
	arm*)
		JNHW_MULTIARCH_TUPEL="arm-\$(host_os)"
	;;
	*)
        	JNHW_MULTIARCH_TUPEL="\$(host_cpu)-\$(host_os)"
	;;
	esac
    ;;
esac

AC_SUBST(JNHW_MULTIARCH_TUPEL)

])
