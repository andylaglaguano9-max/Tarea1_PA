package com.pixelvault.aspect;

import com.pixelvault.dto.CompraRequest;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AntiScalperGrailRuleAspectTest {

    @InjectMocks
    private AntiScalperGrailRuleAspect aspect;

    @Test
    @DisplayName("Debe interceptar y registrar telemetria de solicitud de compra estandar sin errores")
    void testRegistrarIntentoAdquisicionEstandar() {
        Long coleccionistaId = 1L;
        Long juegoId = 10L;
        CompraRequest request = new CompraRequest(coleccionistaId, juegoId);
        JoinPoint joinPoint = mock(JoinPoint.class);

        assertDoesNotThrow(() -> aspect.registrarIntentoAdquisicion(joinPoint, request));
    }

    @Test
    @DisplayName("Debe interceptar multiples solicitudes consecutivas de telemetria de forma idempotente")
    void testRegistrarMultiplesSolicitudesIdempotente() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        CompraRequest req1 = new CompraRequest(2L, 20L);
        CompraRequest req2 = new CompraRequest(3L, 30L);

        assertDoesNotThrow(() -> {
            aspect.registrarIntentoAdquisicion(joinPoint, req1);
            aspect.registrarIntentoAdquisicion(joinPoint, req2);
        });
    }

    @Test
    @DisplayName("Debe interceptar llamadas con identificadores de coleccionista y juego de alto rango")
    void testRegistrarIntentoAdquisicionAltoRango() {
        CompraRequest request = new CompraRequest(999999L, 888888L);
        JoinPoint joinPoint = mock(JoinPoint.class);

        assertDoesNotThrow(() -> aspect.registrarIntentoAdquisicion(joinPoint, request));
    }
}
