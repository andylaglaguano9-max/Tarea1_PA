package com.pixelvault.dto;

import com.pixelvault.model.EstadoConservacion;
import com.pixelvault.model.Rareza;
import com.pixelvault.validation.ValidRetroSerial;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record VideojuegoRequestDTO(

    @NotBlank(message = "El titulo de la obra de arte pixelada no puede estar vacio.")
    @Size(min = 2, max = 80, message = "El titulo debe contener entre 2 y 80 caracteres.")
    String titulo,

    @NotBlank(message = "Debes especificar la plataforma de origen (ej. SNES, NES, N64, GEN, PS1).")
    String plataforma,

    @NotNull(message = "El anio de lanzamiento es obligatorio.")
    @Min(value = 1970, message = "¡Demasiado antiguo! Los videojuegos comerciales comenzaron en los 70s.")
    @Max(value = 2005, message = "¡Eso no es retro! PixelVault solo preserva titulos lanzados hasta el anio 2005.")
    Integer anioLanzamiento,

    @NotNull(message = "El precio de tasacion es obligatorio.")
    @Positive(message = "¡Ni en venta de garaje es gratis! El precio debe ser estrictamente mayor a 0.")
    BigDecimal precio,

    @NotNull(message = "Debes indicar el estado de conservacion (LOOSE, CIB, MINT_IN_BOX).")
    EstadoConservacion estadoConservacion,

    @NotNull(message = "La rareza es obligatoria (COMUN, RARO, EPICO, SANTO_GRIAL).")
    Rareza rareza,

    @NotBlank(message = "El serial retro del cartucho es obligatorio.")
    @ValidRetroSerial
    String serialRetro,

    @NotNull(message = "El stock inicial debe ser definido.")
    @Min(value = 0, message = "El stock en boveda no puede ser un valor negativo.")
    Integer stock
) {}
