/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ibapl.jnrheader.api.windows;

/**
 *
 * @author aploese
 */
public interface Pointer<T> {
    
    T getIndirection();
    void setIndirection(T indirection);
}
