package com.pixelvault.interceptor;

import com.pixelvault.exception.VaultSecurityException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class VaultPassSecurityInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(VaultPassSecurityInterceptor.class);

    @Value("${vault.security.pass-key:RETRO-MASTER-KEY-1989}")
    private String secretPassKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Permitir solicitudes OPTIONS para negociacion CORS preflight
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String passHeader = request.getHeader("X-Vault-Pass");
        log.debug("[INTERCEPTOR SEGURIDAD] Evaluando header X-Vault-Pass en ruta: {}", request.getRequestURI());

        if (passHeader == null || passHeader.isBlank()) {
            throw new VaultSecurityException("¡Acceso denegado a la Boveda! Debes proporcionar el header 'X-Vault-Pass'.");
        }

        if (!passHeader.equals(secretPassKey)) {
            throw new VaultSecurityException("¡Credenciales de Boveda invalidas! La llave 'X-Vault-Pass' no coincide con el codigo maestro.");
        }

        return true;
    }
}
