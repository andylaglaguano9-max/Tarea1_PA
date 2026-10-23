package com.pixelvault.aspect;

import com.pixelvault.dto.CompraRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Aspecto AOP refactorizado enfocado exclusivamente en un concern transversal genuino (Telemetria / Auditoria).
 * La logica de negocio de validacion y las dependencias directas a repositorios fueron relocalizadas
 * al servicio de dominio (OrdenService) para evitar consultas redundantes a base de datos y acoplamiento inadecuado.
 */
@Aspect
@Component
@Order(1) // Prioridad previa al inicio del procesamiento
public class AntiScalperGrailRuleAspect {

    private static final Logger log = LoggerFactory.getLogger(AntiScalperGrailRuleAspect.class);

    @Before("execution(* com.pixelvault.service.OrdenService.procesarCompra(..)) && args(request)")
    public void registrarIntentoAdquisicion(JoinPoint joinPoint, CompraRequest request) {
        log.info("[AOP TELEMETRIA DE ADQUISICION] Interceptando solicitud de checkout para Coleccionista ID: {} y Videojuego ID: {}",
                request.coleccionistaId(), request.videojuegoId());
    }
}
