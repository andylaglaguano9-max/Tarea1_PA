package com.pixelvault.exception;

public class JuegoRetroNotFoundException extends RuntimeException {
    public JuegoRetroNotFoundException(String mensaje) {
        super(mensaje);
    }

    public JuegoRetroNotFoundException(Long id) {
        super("¡Cartucho extraviado! No se encontro ningun videojuego con ID " + id + " en la Boveda.");
    }
}
