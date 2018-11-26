/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ibapl.jnrheader.api.windows;

import de.ibapl.jnrheader.JnrHeader;
import de.ibapl.jnrheader.Wrapper;

/**
 *
 * @author aploese
 */
@Wrapper("ioapiset.h")
public abstract class Ioapiset_H implements JnrHeader {
      public abstract boolean GetOverlappedResult (Minwindef_H.HANDLE hFile, Minwinbase_H.OVERLAPPED lpOverlapped, Minwindef_H.LPDWORD lpNumberOfBytesTransferred, boolean bWait);
}
