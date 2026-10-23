package com.pixelvault.service;

import com.pixelvault.dto.CompraRequest;
import com.pixelvault.exception.AntiScalperViolationException;
import com.pixelvault.exception.SaldoInsuficienteException;
import com.pixelvault.exception.StockAgotadoException;
import com.pixelvault.model.*;
import com.pixelvault.repository.ColeccionistaRepository;
import com.pixelvault.repository.OrdenRepository;
import com.pixelvault.repository.VideojuegoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdenServiceTest {

    @Mock
    private OrdenRepository ordenRepository;

    @Mock
    private VideojuegoRepository videojuegoRepository;

    @Mock
    private ColeccionistaRepository coleccionistaRepository;

    @Mock
    private CalculoComisionService comisionCartuchosService;

    @Mock
    private CalculoComisionService comisionConsolasGrialService;

    private DecimalFormat retroCurrencyFormatter;
    private String transactionPrefix;

    private OrdenService ordenService;

    @BeforeEach
    void setUp() {
        retroCurrencyFormatter = new DecimalFormat("$#,##0.00");
        transactionPrefix = "TEST-PV-";
        ordenService = new OrdenService(
                ordenRepository,
                videojuegoRepository,
                coleccionistaRepository,
                comisionCartuchosService,
                comisionConsolasGrialService,
                retroCurrencyFormatter,
                transactionPrefix
        );
    }

    @Test
    @DisplayName("Debe procesar compra regular exitosamente y descontar saldo y stock")
    void testProcesarCompraExitosa() {
        // GIVEN
        Long coleccionistaId = 1L;
        Long juegoId = 5L;
        CompraRequest request = new CompraRequest(coleccionistaId, juegoId);

        VideojuegoRetro juego = new VideojuegoRetro(
                juegoId, "Sonic 2", "GEN", 1992,
                new BigDecimal("100.00"), EstadoConservacion.CIB, Rareza.COMUN, "GEN-1992-USA", 3
        );

        Coleccionista coleccionista = new Coleccionista(
                coleccionistaId, "GamerTest", "test@pixelvault.com",
                NivelMembresia.ENTUSIASTA, new BigDecimal("200.00")
        );

        when(coleccionistaRepository.findById(coleccionistaId)).thenReturn(Optional.of(coleccionista));
        when(videojuegoRepository.findById(juegoId)).thenReturn(Optional.of(juego));
        when(comisionCartuchosService.calcularComision(any())).thenReturn(new BigDecimal("5.00"));
        when(comisionCartuchosService.obtenerNombreEstrategia()).thenReturn("ESTRATEGIA_TEST_5%");
        when(ordenRepository.save(any(OrdenCompra.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        OrdenCompra orden = ordenService.procesarCompra(request);

        // THEN
        assertNotNull(orden);
        assertEquals(2, juego.getStock()); // Stock redujo de 3 a 2
        assertEquals(new BigDecimal("95.00"), coleccionista.getSaldoBoveda()); // 200 - (100 + 5) = 95
        assertEquals(new BigDecimal("105.00"), orden.getPrecioFinal());
        verify(ordenRepository, times(1)).save(any(OrdenCompra.class));
    }

    @Test
    @DisplayName("Debe lanzar StockAgotadoException si el juego no tiene unidades disponibles")
    void testLanzarStockAgotadoException() {
        Long coleccionistaId = 1L;
        Long juegoId = 5L;
        CompraRequest request = new CompraRequest(coleccionistaId, juegoId);

        VideojuegoRetro juegoAgotado = new VideojuegoRetro(
                juegoId, "EarthBound", "SNES", 1994,
                new BigDecimal("1000.00"), EstadoConservacion.CIB, Rareza.SANTO_GRIAL, "SNES-1994-USA", 0
        );

        Coleccionista coleccionista = new Coleccionista(
                coleccionistaId, "GamerTest", "test@pixelvault.com",
                NivelMembresia.CURADOR_LEGENDARIO, new BigDecimal("5000.00")
        );

        when(coleccionistaRepository.findById(coleccionistaId)).thenReturn(Optional.of(coleccionista));
        when(videojuegoRepository.findById(juegoId)).thenReturn(Optional.of(juegoAgotado));

        assertThrows(StockAgotadoException.class, () -> ordenService.procesarCompra(request));
        verify(ordenRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar SaldoInsuficienteException si el coleccionista no tiene fondos suficientes")
    void testLanzarSaldoInsuficienteException() {
        Long coleccionistaId = 1L;
        Long juegoId = 5L;
        CompraRequest request = new CompraRequest(coleccionistaId, juegoId);

        VideojuegoRetro juegoCaro = new VideojuegoRetro(
                juegoId, "Chrono Trigger", "SNES", 1995,
                new BigDecimal("800.00"), EstadoConservacion.MINT_IN_BOX, Rareza.SANTO_GRIAL, "SNES-1995-USA", 1
        );

        Coleccionista coleccionistaPobre = new Coleccionista(
                coleccionistaId, "Noob", "noob@pixelvault.com",
                NivelMembresia.NOOB, new BigDecimal("100.00")
        );

        when(coleccionistaRepository.findById(coleccionistaId)).thenReturn(Optional.of(coleccionistaPobre));
        when(videojuegoRepository.findById(juegoId)).thenReturn(Optional.of(juegoCaro));
        when(comisionConsolasGrialService.calcularComision(any())).thenReturn(new BigDecimal("120.00"));
        when(comisionConsolasGrialService.obtenerNombreEstrategia()).thenReturn("ESTRATEGIA_GRIAL_15%");

        assertThrows(SaldoInsuficienteException.class, () -> ordenService.procesarCompra(request));
        verify(ordenRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar AntiScalperViolationException en OrdenService si el coleccionista ya compro 2 piezas Grial en 24h")
    void testBloqueoAntiScalperCuandoSuperaLimiteEnServicio() {
        Long coleccionistaId = 1L;
        Long juegoId = 10L;
        CompraRequest request = new CompraRequest(coleccionistaId, juegoId);

        VideojuegoRetro grial = new VideojuegoRetro(
                juegoId, "EarthBound MINT", "SNES", 1994,
                new BigDecimal("1200.00"), EstadoConservacion.MINT_IN_BOX, Rareza.SANTO_GRIAL, "SNES-1994-USA", 1
        );

        Coleccionista coleccionista = new Coleccionista(
                coleccionistaId, "GamerTest", "test@pixelvault.com",
                NivelMembresia.CURADOR_LEGENDARIO, new BigDecimal("5000.00")
        );

        when(coleccionistaRepository.findById(coleccionistaId)).thenReturn(Optional.of(coleccionista));
        when(videojuegoRepository.findById(juegoId)).thenReturn(Optional.of(grial));
        when(ordenRepository.contarComprasCriticasRecientes(eq(coleccionistaId), any(LocalDateTime.class))).thenReturn(2L);

        assertThrows(AntiScalperViolationException.class, () -> ordenService.procesarCompra(request));
        verify(ordenRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe permitir compra de pieza Grial en OrdenService si el coleccionista solo tiene 1 compra previa en 24h")
    void testPermitirCompraPiezaCriticaBajoLimiteEnServicio() {
        Long coleccionistaId = 1L;
        Long juegoId = 10L;
        CompraRequest request = new CompraRequest(coleccionistaId, juegoId);

        VideojuegoRetro grial = new VideojuegoRetro(
                juegoId, "Zelda Gold", "N64", 1998,
                new BigDecimal("450.00"), EstadoConservacion.MINT_IN_BOX, Rareza.SANTO_GRIAL, "N64-1998-USA", 1
        );

        Coleccionista coleccionista = new Coleccionista(
                coleccionistaId, "GamerTest", "test@pixelvault.com",
                NivelMembresia.CURADOR_LEGENDARIO, new BigDecimal("1000.00")
        );

        when(coleccionistaRepository.findById(coleccionistaId)).thenReturn(Optional.of(coleccionista));
        when(videojuegoRepository.findById(juegoId)).thenReturn(Optional.of(grial));
        when(ordenRepository.contarComprasCriticasRecientes(eq(coleccionistaId), any(LocalDateTime.class))).thenReturn(1L);
        when(comisionConsolasGrialService.calcularComision(any())).thenReturn(new BigDecimal("67.50"));
        when(comisionConsolasGrialService.obtenerNombreEstrategia()).thenReturn("ESTRATEGIA_GRIAL_15%");
        when(ordenRepository.save(any(OrdenCompra.class))).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> ordenService.procesarCompra(request));
        verify(ordenRepository, times(1)).save(any(OrdenCompra.class));
    }
}
