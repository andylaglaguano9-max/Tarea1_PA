package com.pixelvault.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "videojuegos_retro")
public class VideojuegoRetro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String titulo;

    @Column(nullable = false, length = 30)
    private String plataforma;

    @Column(name = "anio_lanzamiento", nullable = false)
    private Integer anioLanzamiento;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_conservacion", nullable = false, length = 30)
    private EstadoConservacion estadoConservacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Rareza rareza;

    @Column(name = "serial_retro", nullable = false, unique = true, length = 40)
    private String serialRetro;

    @Column(nullable = false)
    private Integer stock;

    public VideojuegoRetro() {
    }

    public VideojuegoRetro(Long id, String titulo, String plataforma, Integer anioLanzamiento, 
                           BigDecimal precio, EstadoConservacion estadoConservacion, 
                           Rareza rareza, String serialRetro, Integer stock) {
        this.id = id;
        this.titulo = titulo;
        this.plataforma = plataforma;
        this.anioLanzamiento = anioLanzamiento;
        this.precio = precio;
        this.estadoConservacion = estadoConservacion;
        this.rareza = rareza;
        this.serialRetro = serialRetro;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }

    public Integer getAnioLanzamiento() {
        return anioLanzamiento;
    }

    public void setAnioLanzamiento(Integer anioLanzamiento) {
        this.anioLanzamiento = anioLanzamiento;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public EstadoConservacion getEstadoConservacion() {
        return estadoConservacion;
    }

    public void setEstadoConservacion(EstadoConservacion estadoConservacion) {
        this.estadoConservacion = estadoConservacion;
    }

    public Rareza getRareza() {
        return rareza;
    }

    public void setRareza(Rareza rareza) {
        this.rareza = rareza;
    }

    public String getSerialRetro() {
        return serialRetro;
    }

    public void setSerialRetro(String serialRetro) {
        this.serialRetro = serialRetro;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
