package com.pixelvault.controller;

import com.pixelvault.dto.ColeccionistaRequestDTO;
import com.pixelvault.dto.ColeccionistaResponseDTO;
import com.pixelvault.service.ColeccionistaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/coleccionistas")
@Tag(name = "Coleccionistas", description = "Endpoints de gestion de perfiles y balances de gamer")
public class ColeccionistaController {

    private final ColeccionistaService coleccionistaService;

    public ColeccionistaController(ColeccionistaService coleccionistaService) {
        this.coleccionistaService = coleccionistaService;
    }

    @GetMapping
    @Operation(summary = "Listar coleccionistas registrados", description = "Requiere header X-Vault-Pass.")
    public ResponseEntity<List<ColeccionistaResponseDTO>> listarColeccionistas() {
        return ResponseEntity.ok(coleccionistaService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar coleccionista por ID", description = "Requiere header X-Vault-Pass.")
    public ResponseEntity<ColeccionistaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(coleccionistaService.buscarPorId(id));
    }

    @GetMapping("/{id}/balance")
    @Operation(summary = "Consultar saldo disponible en boveda", description = "Requiere header X-Vault-Pass.")
    public ResponseEntity<Map<String, Object>> consultarSaldo(@PathVariable Long id) {
        BigDecimal saldo = coleccionistaService.consultarSaldo(id);
        return ResponseEntity.ok(Map.of(
                "coleccionistaId", id,
                "saldoBoveda", saldo,
                "moneda", "PIXELS (PX$)"
        ));
    }

    @PostMapping
    @Operation(summary = "Registrar nuevo coleccionista", description = "Valida formato de email, longitud de alias y saldo positivo.")
    public ResponseEntity<ColeccionistaResponseDTO> registrar(@Valid @RequestBody ColeccionistaRequestDTO dto) {
        ColeccionistaResponseDTO nuevo = coleccionistaService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }
}
