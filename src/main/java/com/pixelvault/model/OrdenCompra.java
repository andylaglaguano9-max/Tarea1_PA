package com.pixelvault.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ordenes_compra")
public class OrdenCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_transaccion", nullable = false, unique = true, length = 60)
    private String codigoTransaccion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "coleccionista_id", nullable = false)
    private Coleccionista coleccionista;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "videojuego_id", nullable = false)
    private VideojuegoRetro videojuego;

    @Column(name = "fecha_orden", nullable = false)
    private LocalDateTime fechaOrden;

    @Column(name = "precio_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioBase;

    @Column(name = "comision_preservacion", nullable = false, precision = 10, scale = 2)
    private BigDecimal comisionPreservacion;

    @Column(name = "precio_final", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioFinal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private EstadoOrden estado;

    public OrdenCompra() {
        this.fechaOrden = LocalDateTime.now();
        this.estado = EstadoOrden.COMPLETADA;
    }

    public OrdenCompra(Long id, String codigoTransaccion, Coleccionista coleccionista, 
                       VideojuegoRetro videojuego, BigDecimal precioBase, 
                       BigDecimal comisionPreservacion, BigDecimal precioFinal, EstadoOrden estado) {
        this.id = id;
        this.codigoTransaccion = codigoTransaccion;
        this.coleccionista = coleccionista;
        this.videojuego = videojuego;
        this.fechaOrden = LocalDateTime.now();
        this.precioBase = precioBase;
        this.comisionPreservacion = comisionPreservacion;
        this.precioFinal = precioFinal;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoTransaccion() {
        return codigoTransaccion;
    }

    public void setCodigoTransaccion(String codigoTransaccion) {
        this.codigoTransaccion = codigoTransaccion;
    }

    public Coleccionista getColeccionista() {
        return coleccionista;
    }

    public void setColeccionista(Coleccionista coleccionista) {
        this.coleccionista = coleccionista;
    }

    public VideojuegoRetro getVideojuego() {
        return videojuego;
    }

    public void setVideojuego(VideojuegoRetro videojuego) {
        this.videojuego = videojuego;
    }

    public LocalDateTime getFechaOrden() {
        return fechaOrden;
    }

    public void setFechaOrden(LocalDateTime fechaOrden) {
        this.fechaOrden = fechaOrden;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(BigDecimal precioBase) {
        this.precioBase = precioBase;
    }

    public BigDecimal getComisionPreservacion() {
        return comisionPreservacion;
    }

    public void setComisionPreservacion(BigDecimal comisionPreservacion) {
        this.comisionPreservacion = comisionPreservacion;
    }

    public BigDecimal getPrecioFinal() {
        return precioFinal;
    }

    public void setPrecioFinal(BigDecimal precioFinal) {
        this.precioFinal = precioFinal;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public void setEstado(EstadoOrden estado) {
        this.estado = estado;
    }
}
