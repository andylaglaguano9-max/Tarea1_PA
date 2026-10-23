package com.pixelvault.service;

import java.math.BigDecimal;

public interface CalculoComisionService {

    /**
     * Calcula el monto adicional por conservacion, certificacion y envio seguro.
     */
    BigDecimal calcularComision(BigDecimal precioBase);

    /**
     * Retorna el nombre de la estrategia aplicada.
     */
    String obtenerNombreEstrategia();
}
