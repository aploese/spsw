/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ibapl.jnrheader.api.windows;

import de.ibapl.jnrheader.JnrHeader;
import de.ibapl.jnrheader.Wrapper;
import de.ibapl.jnrheader.api.windows.Minwinbase_H.SECURITY_ATTRIBUTES;

/**
 *
 * @author aploese
 */
@Wrapper("synchapi.h")
public abstract class Synchapi_H implements JnrHeader {
 
    
  public abstract int  WaitForSingleObject (Minwindef_H.HANDLE hHandle, long dwMilliseconds);
    
  public abstract Minwindef_H.HANDLE CreateEventW(SECURITY_ATTRIBUTES lpEventAttributes, boolean bManualReset, boolean bInitialState, String lpName);
}
