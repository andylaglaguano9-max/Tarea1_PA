package com.pixelvault.service;

import com.pixelvault.dto.VideojuegoRequestDTO;
import com.pixelvault.dto.VideojuegoResponseDTO;
import com.pixelvault.exception.JuegoRetroNotFoundException;
import com.pixelvault.model.VideojuegoRetro;
import com.pixelvault.repository.VideojuegoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VideojuegoService {

    private final VideojuegoRepository videojuegoRepository;

    public VideojuegoService(VideojuegoRepository videojuegoRepository) {
        this.videojuegoRepository = videojuegoRepository;
    }

    @Transactional(readOnly = true)
    public List<VideojuegoResponseDTO> obtenerCatalogoPublico() {
        return videojuegoRepository.findAll().stream()
                .map(this::mapearADTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public VideojuegoResponseDTO buscarPorId(Long id) {
        VideojuegoRetro juego = videojuegoRepository.findById(id)
                .orElseThrow(() -> new JuegoRetroNotFoundException(id));
        return mapearADTO(juego);
    }

    @Transactional
    public VideojuegoResponseDTO registrarJuego(VideojuegoRequestDTO dto) {
        if (videojuegoRepository.existsBySerialRetro(dto.serialRetro())) {
            throw new IllegalArgumentException("¡Serial duplicado! Ya existe un cartucho registrado con el serial: " + dto.serialRetro());
        }

        VideojuegoRetro nuevoJuego = new VideojuegoRetro(
                null,
                dto.titulo(),
                dto.plataforma().toUpperCase(),
                dto.anioLanzamiento(),
                dto.precio(),
                dto.estadoConservacion(),
                dto.rareza(),
                dto.serialRetro().toUpperCase(),
                dto.stock()
        );

        VideojuegoRetro guardado = videojuegoRepository.save(nuevoJuego);
        return mapearADTO(guardado);
    }

    private VideojuegoResponseDTO mapearADTO(VideojuegoRetro j) {
        return new VideojuegoResponseDTO(
                j.getId(),
                j.getTitulo(),
                j.getPlataforma(),
                j.getAnioLanzamiento(),
                j.getPrecio(),
                j.getEstadoConservacion(),
                j.getRareza(),
                j.getSerialRetro(),
                j.getStock()
        );
    }
}
