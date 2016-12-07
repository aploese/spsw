/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ibapl.spsw.api;

import javax.inject.Named;
import org.osgi.annotation.versioning.ProviderType;

/**
 *
 * @author aploese
 */
@Named
@ProviderType
public interface SerialPortSocketFactory {

    SerialPortSocket createSerialPortSocket(String portName);

    /**
     * Lifecycle start service
     */
    void activate();

    /**
     * Lifecycle stop service
     */
    void deActivate();

}
