package com.pixelvault.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "VaultPassHeader";

        return new OpenAPI()
                .info(new Info()
                        .title("PixelVault REST API - The Retro Gaming Engine")
                        .version("1.0.0-RELEASE")
                        .description("API REST para la gestion, tasacion y adquisicion de videojuegos clasicos de coleccion. " +
                                     "Integra IoC/DI con @Qualifier, Interceptores Web con header personalizado, " +
                                     "AOP con regla Anti-Scalper, Bean Validation y @RestControllerAdvice.")
                        .contact(new Contact()
                                .name("Comite de Preservacion PixelVault")
                                .email("docente@pixelvault.edu"))
                        .license(new License().name("Apache 2.0").url("https://spring.io")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name("X-Vault-Pass")
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .description("Introduce 'RETRO-MASTER-KEY-1989' para autorizar las solicitudes a rutas protegidas.")));
    }
}
