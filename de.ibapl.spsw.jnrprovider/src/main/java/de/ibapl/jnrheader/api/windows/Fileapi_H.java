/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ibapl.jnrheader.api.windows;

import de.ibapl.jnrheader.JnrHeader;
import de.ibapl.jnrheader.Wrapper;
import de.ibapl.jnrheader.api.windows.Minwinbase_H.SECURITY_ATTRIBUTES;
import de.ibapl.jnrheader.api.windows.Minwindef_H.HANDLE;
import java.nio.ByteBuffer;

/**
 *
 * @author aploese
 */
@Wrapper("fileapi.h")
public abstract class Fileapi_H implements JnrHeader {

    public static final int CREATE_NEW = 1;
    public static final int CREATE_ALWAYS = 2;
    public static final int OPEN_EXISTING = 3;
    public static final int OPEN_ALWAYS = 4;
    public static final int TRUNCATE_EXISTING = 5;

    public static final int INVALID_FILE_SIZE = 0xffffffff;
    public static final int INVALID_SET_FILE_POINTER = -1;
    public static final int INVALID_FILE_ATTRIBUTES = -1;

    public abstract Minwindef_H.HANDLE CreateFileW(String lpFileName, int dwDesiredAccess, int dwShareMode, SECURITY_ATTRIBUTES lpSecurityAttributes, int dwCreationDisposition, int dwFlagsAndAttributes, HANDLE hTemplateFile);

    public abstract boolean FlushFileBuffers(HANDLE hFile);

    public abstract boolean ReadFile(HANDLE hFile, byte[] lpBuffer, int nNumberOfBytesToRead, Minwindef_H.LPDWORD lpNumberOfBytesRead, Minwinbase_H.OVERLAPPED lpOverlapped);
    public abstract boolean ReadFile(HANDLE hFile, ByteBuffer lpBuffer, int nNumberOfBytesToRead, Minwindef_H.LPDWORD lpNumberOfBytesRead, Minwinbase_H.OVERLAPPED lpOverlapped);

    public abstract boolean WriteFile (HANDLE hFile, ByteBuffer lpBuffer, int nNumberOfBytesToWrite, Minwindef_H.LPDWORD lpNumberOfBytesWritten, Minwinbase_H.OVERLAPPED lpOverlapped);
    public abstract boolean WriteFile (HANDLE hFile, byte[] lpBuffer, int nNumberOfBytesToWrite, Minwindef_H.LPDWORD lpNumberOfBytesWritten, Minwinbase_H.OVERLAPPED lpOverlapped);
}
