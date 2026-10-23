package com.pixelvault.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String mensaje,
    String path,
    Map<String, String> detallesValidacion
) {
    public ApiErrorResponse(int status, String error, String mensaje, String path) {
        this(LocalDateTime.now(), status, error, mensaje, path, null);
    }
}
