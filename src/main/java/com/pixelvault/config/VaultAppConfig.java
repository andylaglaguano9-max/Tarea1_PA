package com.pixelvault.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Configuration
public class VaultAppConfig {

    /**
     * Bean 1 de infraestructura: Formateador monetario para moneda retro 'PIXELS'.
     */
    @Bean(name = "retroCurrencyFormatter")
    public DecimalFormat retroCurrencyFormatter() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setCurrencySymbol("PX$");
        return new DecimalFormat("PX$ #,##0.00", symbols);
    }

    /**
     * Bean 2 de infraestructura: Prefijo formal para trazabilidad y auditoria de la boveda.
     */
    @Bean(name = "vaultAuditPrefix")
    public String vaultAuditPrefix() {
        return "[PIXELVAULT-ARCHIVE-SECURE]";
    }

    /**
     * Bean 3 adicional: Generador del prefijo para codigos de transaccion de arcade.
     */
    @Bean(name = "transactionPrefix")
    public String transactionPrefix() {
        return "PV-ARCADE-";
    }
}
