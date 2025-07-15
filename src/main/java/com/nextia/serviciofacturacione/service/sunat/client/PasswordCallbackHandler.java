package com.nextia.serviciofacturacione.service.sunat.client;


import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;

import org.apache.wss4j.common.ext.WSPasswordCallback;

/**
 * Manejador de contraseñas para WS-Security
 * Implementación para autenticación con usuario y contraseña en servicios web de SUNAT
 */
public class PasswordCallbackHandler implements CallbackHandler {
    private final String username;
    private final String password;
    
    /**
     * Constructor con usuario y contraseña
     * 
     * @param username Nombre de usuario
     * @param password Contraseña
     */
    public PasswordCallbackHandler(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    /**
     * Método que establece la contraseña para el identificador correspondiente
     */
    @Override
    public void handle(Callback[] callbacks) {
        for (Callback callback : callbacks) {
            if (callback instanceof WSPasswordCallback passwordCallback && 
                passwordCallback.getIdentifier().equals(username)) {
                passwordCallback.setPassword(password);
            }
        }
    }
}
