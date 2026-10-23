package com.pixelvault.exception;

public class AntiScalperViolationException extends RuntimeException {
    public AntiScalperViolationException(String mensaje) {
        super(mensaje);
    }
}
