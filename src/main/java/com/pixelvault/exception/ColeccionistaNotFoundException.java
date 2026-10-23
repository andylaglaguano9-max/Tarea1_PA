package com.pixelvault.exception;

public class ColeccionistaNotFoundException extends RuntimeException {
    public ColeccionistaNotFoundException(String mensaje) {
        super(mensaje);
    }

    public ColeccionistaNotFoundException(Long id) {
        super("¡Gamer no registrado! El coleccionista con ID " + id + " no posee ficha activa en PixelVault.");
    }
}
