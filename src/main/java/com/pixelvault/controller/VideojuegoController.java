package com.pixelvault.controller;

import com.pixelvault.dto.VideojuegoRequestDTO;
import com.pixelvault.dto.VideojuegoResponseDTO;
import com.pixelvault.service.VideojuegoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/juegos")
@Tag(name = "Videojuegos Retro", description = "Endpoints para catalogacion y consulta de piezas historicas")
public class VideojuegoController {

    private final VideojuegoService videojuegoService;

    public VideojuegoController(VideojuegoService videojuegoService) {
        this.videojuegoService = videojuegoService;
    }

    @GetMapping("/catalogo-publico")
    @Operation(summary = "Catalogo publico de reliquias", description = "Permite consultar los juegos sin necesidad de credenciales X-Vault-Pass.")
    public ResponseEntity<List<VideojuegoResponseDTO>> catalogoPublico() {
        return ResponseEntity.ok(videojuegoService.obtenerCatalogoPublico());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle de una pieza historica", description = "Requiere header X-Vault-Pass para autorizar el acceso.")
    public ResponseEntity<VideojuegoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(videojuegoService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Registrar nueva reliquia en boveda", description = "Aplica Bean Validation y validacion de serial retro.")
    public ResponseEntity<VideojuegoResponseDTO> registrarJuego(@Valid @RequestBody VideojuegoRequestDTO dto) {
        VideojuegoResponseDTO registrado = videojuegoService.registrarJuego(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registrado);
    }
}
