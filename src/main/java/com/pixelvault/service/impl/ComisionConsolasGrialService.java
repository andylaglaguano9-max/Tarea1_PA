package com.pixelvault.service.impl;

import com.pixelvault.service.CalculoComisionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service("comisionConsolasGrialService")
public class ComisionConsolasGrialService implements CalculoComisionService {

    // Comision con seguro blindado y preservacion de grado museo (15%)
    private static final BigDecimal TASA_GRIAL = new BigDecimal("0.15");

    @Override
    public BigDecimal calcularComision(BigDecimal precioBase) {
        if (precioBase == null) {
            return BigDecimal.ZERO;
        }
        return precioBase.multiply(TASA_GRIAL).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String obtenerNombreEstrategia() {
        return "TASA_MUSEO_GRIAL_15%";
    }
}
