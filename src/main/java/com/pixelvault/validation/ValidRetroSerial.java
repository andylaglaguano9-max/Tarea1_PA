package com.pixelvault.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RetroSerialValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRetroSerial {

    String message() default "¡Serial de cartucho invalido! Formato requerido: [CONSOLA]-[ANIO]-[REGION] (Ej: SNES-1994-USA, N64-1996-JPN). ¡Sopla el cartucho y verifica el codigo!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
