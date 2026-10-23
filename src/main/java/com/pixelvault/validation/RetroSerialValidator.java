package com.pixelvault.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class RetroSerialValidator implements ConstraintValidator<ValidRetroSerial, String> {

    // Patron: CONSOLA (NES, SNES, N64, GEN, PS1) - ANIO (4 digitos) - REGION (USA, JPN, EUR)
    private static final Pattern RETRO_SERIAL_PATTERN = 
            Pattern.compile("^(NES|SNES|N64|GEN|PS1)-\\d{4}-(USA|JPN|EUR)$");

    @Override
    public boolean isValid(String valor, ConstraintValidatorContext context) {
        if (valor == null || valor.trim().isEmpty()) {
            return false;
        }
        return RETRO_SERIAL_PATTERN.matcher(valor.trim()).matches();
    }
}
