package com.pixelvault.exception;

public class StockAgotadoException extends RuntimeException {
    public StockAgotadoException(String titulo) {
        super("¡GAME OVER de inventario! La reliquia '" + titulo + "' se encuentra totalmente agotada en la boveda.");
    }
}
