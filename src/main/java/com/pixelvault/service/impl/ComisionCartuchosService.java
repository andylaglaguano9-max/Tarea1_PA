package com.pixelvault.service.impl;

import com.pixelvault.service.CalculoComisionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service("comisionCartuchosService")
public class ComisionCartuchosService implements CalculoComisionService {

    // Comision estandar de preservacion para cartuchos retro (5%)
    private static final BigDecimal TASA_PRESERVACION = new BigDecimal("0.05");

    @Override
    public BigDecimal calcularComision(BigDecimal precioBase) {
        if (precioBase == null) {
            return BigDecimal.ZERO;
        }
        return precioBase.multiply(TASA_PRESERVACION).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String obtenerNombreEstrategia() {
        return "TASA_ESTANDAR_CARTUCHOS_5%";
    }
}
