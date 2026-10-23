package com.pixelvault.dto;

import com.pixelvault.model.NivelMembresia;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ColeccionistaResponseDTO(
    Long id,
    String aliasGamer,
    String correoElectronico,
    NivelMembresia nivelMembresia,
    BigDecimal saldoBoveda,
    Boolean activo,
    LocalDateTime fechaRegistro
) {}
