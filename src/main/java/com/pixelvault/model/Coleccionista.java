package com.pixelvault.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coleccionistas")
public class Coleccionista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alias_gamer", nullable = false, unique = true, length = 100)
    private String aliasGamer;

    @Column(name = "correo_electronico", nullable = false, unique = true, length = 150)
    private String correoElectronico;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_membresia", nullable = false, length = 30)
    private NivelMembresia nivelMembresia;

    @Column(name = "saldo_boveda", nullable = false, precision = 12, scale = 2)
    private BigDecimal saldoBoveda;

    @Column(nullable = false)
    private Boolean activo;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    public Coleccionista() {
        this.activo = true;
        this.fechaRegistro = LocalDateTime.now();
    }

    public Coleccionista(Long id, String aliasGamer, String correoElectronico, 
                         NivelMembresia nivelMembresia, BigDecimal saldoBoveda) {
        this.id = id;
        this.aliasGamer = aliasGamer;
        this.correoElectronico = correoElectronico;
        this.nivelMembresia = nivelMembresia;
        this.saldoBoveda = saldoBoveda;
        this.activo = true;
        this.fechaRegistro = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAliasGamer() {
        return aliasGamer;
    }

    public void setAliasGamer(String aliasGamer) {
        this.aliasGamer = aliasGamer;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public NivelMembresia getNivelMembresia() {
        return nivelMembresia;
    }

    public void setNivelMembresia(NivelMembresia nivelMembresia) {
        this.nivelMembresia = nivelMembresia;
    }

    public BigDecimal getSaldoBoveda() {
        return saldoBoveda;
    }

    public void setSaldoBoveda(BigDecimal saldoBoveda) {
        this.saldoBoveda = saldoBoveda;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
