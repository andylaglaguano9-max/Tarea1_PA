package com.pixelvault.dto;

import jakarta.validation.constraints.NotNull;

public record CompraRequest(

    @NotNull(message = "El ID del coleccionista comprador es obligatorio.")
    Long coleccionistaId,

    @NotNull(message = "El ID del videojuego retro a adquirir es obligatorio.")
    Long videojuegoId
) {}
