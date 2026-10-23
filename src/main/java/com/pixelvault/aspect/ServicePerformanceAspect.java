package com.pixelvault.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServicePerformanceAspect {

    private static final Logger log = LoggerFactory.getLogger(ServicePerformanceAspect.class);

    @Around("execution(* com.pixelvault.service..*(..))")
    public Object medirTiempoEjecucion(ProceedingJoinPoint joinPoint) throws Throwable {
        long inicio = System.currentTimeMillis();
        String metodo = joinPoint.getSignature().toShortString();

        try {
            return joinPoint.proceed();
        } finally {
            long duracion = System.currentTimeMillis() - inicio;
            if (duracion > 300) {
                log.warn("[AOP WARP-SPEED WARNING] El metodo de servicio {} tomo {} ms (Umbral alto de latencia)", metodo, duracion);
            } else {
                log.info("[AOP BENCHMARK] Ejecucion de {} finalizada en {} ms", metodo, duracion);
            }
        }
    }
}
