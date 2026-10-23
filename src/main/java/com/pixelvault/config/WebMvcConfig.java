package com.pixelvault.config;

import com.pixelvault.interceptor.RetroLatencyAuditInterceptor;
import com.pixelvault.interceptor.VaultPassSecurityInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final VaultPassSecurityInterceptor securityInterceptor;
    private final RetroLatencyAuditInterceptor auditInterceptor;

    public WebMvcConfig(VaultPassSecurityInterceptor securityInterceptor,
                        RetroLatencyAuditInterceptor auditInterceptor) {
        this.securityInterceptor = securityInterceptor;
        this.auditInterceptor = auditInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Interceptor 1: Auditoria y latencia aplicada a todos los endpoints
        registry.addInterceptor(auditInterceptor);

        // Interceptor 2: Seguridad de cabecera X-Vault-Pass con rutas excluidas
        registry.addInterceptor(securityInterceptor)
                .addPathPatterns("/api/v1/**")
                .excludePathPatterns(
                        "/api/v1/juegos/catalogo-publico",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/h2-console/**",
                        "/error"
                );
    }

    @Override
    public void addViewControllers(org.springframework.web.servlet.config.annotation.ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
    }
}
