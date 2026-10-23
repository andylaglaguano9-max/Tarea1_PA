package com.pixelvault.exception;

import java.math.BigDecimal;

public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(BigDecimal saldoActual, BigDecimal totalRequerido) {
        super(String.format("¡Fondos insuficientes! Tu saldo en boveda es de %s PX, pero la transaccion requiere %s PX. ¡Inserta mas fichas para continuar!",
                saldoActual, totalRequerido));
    }
}
