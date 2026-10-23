package com.pixelvault.aspect;

import com.pixelvault.model.OrdenCompra;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class VaultAuditAspect {

    private static final Logger log = LoggerFactory.getLogger(VaultAuditAspect.class);

    private final String vaultAuditPrefix;

    public VaultAuditAspect(@Qualifier("vaultAuditPrefix") String vaultAuditPrefix) {
        this.vaultAuditPrefix = vaultAuditPrefix;
    }

    @AfterReturning(
            pointcut = "execution(* com.pixelvault.service.OrdenService.procesarCompra(..))",
            returning = "resultado"
    )
    public void auditarTransaccionExitosa(JoinPoint joinPoint, Object resultado) {
        if (resultado instanceof OrdenCompra orden) {
            log.info("{} AUDITORIA EXITOSA: Coleccionista '{}' (ID: {}) adquirio la reliquia '{}' por PX$ {} [Tx: {}]",
                    vaultAuditPrefix,
                    orden.getColeccionista().getAliasGamer(),
                    orden.getColeccionista().getId(),
                    orden.getVideojuego().getTitulo(),
                    orden.getPrecioFinal(),
                    orden.getCodigoTransaccion());
        }
    }
}
