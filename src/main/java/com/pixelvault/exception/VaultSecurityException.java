package com.pixelvault.exception;

public class VaultSecurityException extends RuntimeException {
    public VaultSecurityException(String mensaje) {
        super(mensaje);
    }
}
