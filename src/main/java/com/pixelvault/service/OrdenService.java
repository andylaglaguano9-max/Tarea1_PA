package com.pixelvault.service;

import com.pixelvault.dto.CompraRequest;
import com.pixelvault.dto.OrdenResponseDTO;
import com.pixelvault.exception.AntiScalperViolationException;
import com.pixelvault.exception.ColeccionistaNotFoundException;
import com.pixelvault.exception.JuegoRetroNotFoundException;
import com.pixelvault.exception.SaldoInsuficienteException;
import com.pixelvault.exception.StockAgotadoException;
import com.pixelvault.model.*;
import com.pixelvault.repository.ColeccionistaRepository;
import com.pixelvault.repository.OrdenRepository;
import com.pixelvault.repository.VideojuegoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrdenService {

    private static final Logger log = LoggerFactory.getLogger(OrdenService.class);

    private final OrdenRepository ordenRepository;
    private final VideojuegoRepository videojuegoRepository;
    private final ColeccionistaRepository coleccionistaRepository;

    // Inyeccion de dependencias con @Qualifier de las 2 implementaciones de dominio
    private final CalculoComisionService comisionCartuchosService;
    private final CalculoComisionService comisionConsolasGrialService;

    // Inyeccion de Beans de infraestructura configurados con @Bean
    private final DecimalFormat retroCurrencyFormatter;
    private final String transactionPrefix;

    public OrdenService(
            OrdenRepository ordenRepository,
            VideojuegoRepository videojuegoRepository,
            ColeccionistaRepository coleccionistaRepository,
            @Qualifier("comisionCartuchosService") CalculoComisionService comisionCartuchosService,
            @Qualifier("comisionConsolasGrialService") CalculoComisionService comisionConsolasGrialService,
            @Qualifier("retroCurrencyFormatter") DecimalFormat retroCurrencyFormatter,
            @Qualifier("transactionPrefix") String transactionPrefix) {
        this.ordenRepository = ordenRepository;
        this.videojuegoRepository = videojuegoRepository;
        this.coleccionistaRepository = coleccionistaRepository;
        this.comisionCartuchosService = comisionCartuchosService;
        this.comisionConsolasGrialService = comisionConsolasGrialService;
        this.retroCurrencyFormatter = retroCurrencyFormatter;
        this.transactionPrefix = transactionPrefix;
    }

    @Transactional
    public OrdenCompra procesarCompra(CompraRequest request) {
        log.info("[CHECKOUT] Iniciando procesamiento de orden para Coleccionista ID: {} y Juego ID: {}",
                request.coleccionistaId(), request.videojuegoId());

        Coleccionista coleccionista = coleccionistaRepository.findById(request.coleccionistaId())
                .orElseThrow(() -> new ColeccionistaNotFoundException(request.coleccionistaId()));

        VideojuegoRetro juego = videojuegoRepository.findById(request.videojuegoId())
                .orElseThrow(() -> new JuegoRetroNotFoundException(request.videojuegoId()));

        if (juego.getStock() <= 0) {
            throw new StockAgotadoException(juego.getTitulo());
        }

        // Relocalizacion de regla de negocio: Control anti-acaparamiento (Anti-Scalper)
        validarReglaAntiScalper(coleccionista.getId(), juego);

        // Seleccion de estrategia de comision inyectada mediante @Qualifier segun la categoria
        CalculoComisionService estrategia = (juego.getRareza() == Rareza.SANTO_GRIAL || juego.getEstadoConservacion() == EstadoConservacion.MINT_IN_BOX)
                ? comisionConsolasGrialService
                : comisionCartuchosService;

        BigDecimal comision = estrategia.calcularComision(juego.getPrecio());
        BigDecimal total = juego.getPrecio().add(comision);

        log.info("[IoC DI] Aplicando estrategia: {} -> Comision: {} | Total: {}",
                estrategia.obtenerNombreEstrategia(),
                retroCurrencyFormatter.format(comision),
                retroCurrencyFormatter.format(total));

        if (coleccionista.getSaldoBoveda().compareTo(total) < 0) {
            throw new SaldoInsuficienteException(coleccionista.getSaldoBoveda(), total);
        }

        // Efectuar transaccion
        coleccionista.setSaldoBoveda(coleccionista.getSaldoBoveda().subtract(total));
        juego.setStock(juego.getStock() - 1);

        String codigoTransaccion = transactionPrefix + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        OrdenCompra orden = new OrdenCompra(
                null,
                codigoTransaccion,
                coleccionista,
                juego,
                juego.getPrecio(),
                comision,
                total,
                EstadoOrden.COMPLETADA
        );

        return ordenRepository.save(orden);
    }

    /**
     * Regla de Negocio Anti-Scalper:
     * Protege el inventario comunitario impidiendo que un coleccionista adquiera mas de 2
     * piezas criticas (SANTO_GRIAL o MINT_IN_BOX) en un lapso de 24 horas.
     */
    public void validarReglaAntiScalper(Long coleccionistaId, VideojuegoRetro juego) {
        boolean esPiezaCritica = (juego.getRareza() == Rareza.SANTO_GRIAL 
                || juego.getEstadoConservacion() == EstadoConservacion.MINT_IN_BOX);

        if (esPiezaCritica) {
            LocalDateTime ventana24Horas = LocalDateTime.now().minusHours(24);
            long comprasPrevias = ordenRepository.contarComprasCriticasRecientes(coleccionistaId, ventana24Horas);

            log.info("[REGLA NEGOCIO ANTI-SCALPER] Coleccionista ID: {} registra {} adquisiciones de Griales/Mint en las ultimas 24h.",
                    coleccionistaId, comprasPrevias);

            if (comprasPrevias >= 2) {
                log.warn("[REGLA NEGOCIO BLOQUEO SCALPER] Coleccionista ID: {} intento superar el limite de 2 piezas Santo Grial en 24h.",
                        coleccionistaId);
                throw new AntiScalperViolationException(
                        "ALERTA DE ACAPARAMIENTO (ANTI-SCALPER): Limite maximo de 2 piezas criticas en 24h alcanzado."
                );
            }
        }
    }

    @Transactional(readOnly = true)
    public List<OrdenResponseDTO> listarPorColeccionista(Long coleccionistaId) {
        if (!coleccionistaRepository.existsById(coleccionistaId)) {
            throw new ColeccionistaNotFoundException(coleccionistaId);
        }

        return ordenRepository.findByColeccionistaIdOrderByFechaOrdenDesc(coleccionistaId).stream()
                .map(this::mapearADTO)
                .toList();
    }

    public OrdenResponseDTO mapearADTO(OrdenCompra o) {
        return new OrdenResponseDTO(
                o.getId(),
                o.getCodigoTransaccion(),
                o.getColeccionista().getId(),
                o.getColeccionista().getAliasGamer(),
                o.getVideojuego().getId(),
                o.getVideojuego().getTitulo(),
                o.getPrecioBase(),
                o.getComisionPreservacion(),
                o.getPrecioFinal(),
                o.getEstado(),
                o.getFechaOrden()
        );
    }
}
