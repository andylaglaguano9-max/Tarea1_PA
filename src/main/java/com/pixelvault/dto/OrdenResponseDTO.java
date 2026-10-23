package com.pixelvault.dto;

import com.pixelvault.model.EstadoOrden;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrdenResponseDTO(
    Long id,
    String codigoTransaccion,
    Long coleccionistaId,
    String aliasGamer,
    Long videojuegoId,
    String tituloVideojuego,
    BigDecimal precioBase,
    BigDecimal comisionPreservacion,
    BigDecimal precioFinal,
    EstadoOrden estado,
    LocalDateTime fechaOrden
) {}
