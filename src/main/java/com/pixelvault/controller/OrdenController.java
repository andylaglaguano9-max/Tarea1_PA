package com.pixelvault.controller;

import com.pixelvault.dto.CompraRequest;
import com.pixelvault.dto.OrdenResponseDTO;
import com.pixelvault.model.OrdenCompra;
import com.pixelvault.service.OrdenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ordenes")
@Tag(name = "Ordenes de Compra", description = "Endpoints de checkout y consulta de transacciones de coleccion")
public class OrdenController {

    private final OrdenService ordenService;

    public OrdenController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    @PostMapping("/checkout")
    @Operation(summary = "Procesar adquisicion de reliquia", 
               description = "Ejecuta IoC con @Qualifier para calcular comision y activa el Aspecto AOP Anti-Scalper.")
    public ResponseEntity<OrdenResponseDTO> procesarCompra(@Valid @RequestBody CompraRequest request) {
        OrdenCompra orden = ordenService.procesarCompra(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ordenService.mapearADTO(orden));
    }

    @GetMapping("/historial/{coleccionistaId}")
    @Operation(summary = "Consultar historial de adquisiciones de un coleccionista", 
               description = "Requiere header X-Vault-Pass.")
    public ResponseEntity<List<OrdenResponseDTO>> historialPorColeccionista(@PathVariable Long coleccionistaId) {
        return ResponseEntity.ok(ordenService.listarPorColeccionista(coleccionistaId));
    }
}
