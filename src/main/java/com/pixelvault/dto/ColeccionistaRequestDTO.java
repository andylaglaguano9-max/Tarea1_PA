package com.pixelvault.dto;

import com.pixelvault.model.NivelMembresia;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ColeccionistaRequestDTO(

    @NotBlank(message = "El alias gamer es obligatorio para ingresar a la Boveda.")
    @Size(min = 3, max = 30, message = "El alias gamer debe tener entre 3 y 30 caracteres.")
    String aliasGamer,

    @NotBlank(message = "El correo electronico es requerido.")
    @Email(message = "El formato del correo electronico no es valido.")
    String correoElectronico,

    @NotNull(message = "Debes indicar el nivel de membresia inicial (NOOB, ENTUSIASTA, CURADOR_LEGENDARIO).")
    NivelMembresia nivelMembresia,

    @NotNull(message = "Debes asignar un saldo inicial de apertura en la boveda.")
    @PositiveOrZero(message = "El saldo inicial no puede ser negativo.")
    BigDecimal saldoBoveda
) {}
