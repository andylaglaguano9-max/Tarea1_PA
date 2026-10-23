package com.pixelvault.dto;

import com.pixelvault.model.EstadoConservacion;
import com.pixelvault.model.Rareza;
import java.math.BigDecimal;

public record VideojuegoResponseDTO(
    Long id,
    String titulo,
    String plataforma,
    Integer anioLanzamiento,
    BigDecimal precio,
    EstadoConservacion estadoConservacion,
    Rareza rareza,
    String serialRetro,
    Integer stock
) {}
