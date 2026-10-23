package com.pixelvault.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RetroSerialValidatorTest {

    private RetroSerialValidator validator;

    @BeforeEach
    void setUp() {
        validator = new RetroSerialValidator();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SNES-1994-USA",
            "NES-1985-JPN",
            "N64-1996-USA",
            "GEN-1991-EUR",
            "PS1-1997-USA"
    })
    @DisplayName("Debe validar exitosamente codigos de serie retro con formato estandar")
    void testSerialesValidos(String serialValido) {
        assertTrue(validator.isValid(serialValido, null));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "GAMEBOY-1990-USA", // Consola no soportada
            "SNES-94-USA",       // Anio sin 4 digitos
            "N64-1996-MEX",      // Region no estandar
            "PS1_1997_USA",      // Guiones bajos en vez de medios
            "",                  // Cadena vacia
            "   ",               // Espacios en blanco
            "INVALID_CODE"
    })
    @DisplayName("Debe rechazar codigos de serie retro malformados o no autorizados")
    void testSerialesInvalidos(String serialInvalido) {
        assertFalse(validator.isValid(serialInvalido, null));
    }

    @Test
    @DisplayName("Debe retornar falso cuando el serial es nulo")
    void testSerialNulo() {
        assertFalse(validator.isValid(null, null));
    }
}
