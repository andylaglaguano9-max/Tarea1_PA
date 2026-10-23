package com.pixelvault.service;

import com.pixelvault.dto.ColeccionistaRequestDTO;
import com.pixelvault.dto.ColeccionistaResponseDTO;
import com.pixelvault.exception.ColeccionistaNotFoundException;
import com.pixelvault.model.Coleccionista;
import com.pixelvault.repository.ColeccionistaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ColeccionistaService {

    private final ColeccionistaRepository coleccionistaRepository;

    public ColeccionistaService(ColeccionistaRepository coleccionistaRepository) {
        this.coleccionistaRepository = coleccionistaRepository;
    }

    @Transactional(readOnly = true)
    public List<ColeccionistaResponseDTO> listarTodos() {
        return coleccionistaRepository.findAll().stream()
                .map(this::mapearADTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ColeccionistaResponseDTO buscarPorId(Long id) {
        Coleccionista coleccionista = coleccionistaRepository.findById(id)
                .orElseThrow(() -> new ColeccionistaNotFoundException(id));
        return mapearADTO(coleccionista);
    }

    @Transactional(readOnly = true)
    public BigDecimal consultarSaldo(Long id) {
        Coleccionista c = coleccionistaRepository.findById(id)
                .orElseThrow(() -> new ColeccionistaNotFoundException(id));
        return c.getSaldoBoveda();
    }

    @Transactional
    public ColeccionistaResponseDTO registrar(ColeccionistaRequestDTO dto) {
        if (coleccionistaRepository.existsByAliasGamer(dto.aliasGamer())) {
            throw new IllegalArgumentException("El alias gamer '" + dto.aliasGamer() + "' ya esta tomado por otro jugador.");
        }
        if (coleccionistaRepository.existsByCorreoElectronico(dto.correoElectronico())) {
            throw new IllegalArgumentException("Ya existe una cuenta con el correo electronico proporcionado.");
        }

        Coleccionista nuevo = new Coleccionista(
                null,
                dto.aliasGamer(),
                dto.correoElectronico(),
                dto.nivelMembresia(),
                dto.saldoBoveda()
        );

        Coleccionista guardado = coleccionistaRepository.save(nuevo);
        return mapearADTO(guardado);
    }

    private ColeccionistaResponseDTO mapearADTO(Coleccionista c) {
        return new ColeccionistaResponseDTO(
                c.getId(),
                c.getAliasGamer(),
                c.getCorreoElectronico(),
                c.getNivelMembresia(),
                c.getSaldoBoveda(),
                c.getActivo(),
                c.getFechaRegistro()
        );
    }
}
