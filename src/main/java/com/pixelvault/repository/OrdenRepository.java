package com.pixelvault.repository;

import com.pixelvault.model.OrdenCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrdenRepository extends JpaRepository<OrdenCompra, Long> {

    List<OrdenCompra> findByColeccionistaIdOrderByFechaOrdenDesc(Long coleccionistaId);

    @Query("SELECT COUNT(o) FROM OrdenCompra o " +
           "WHERE o.coleccionista.id = :coleccionistaId " +
           "AND o.fechaOrden >= :desde " +
           "AND (o.videojuego.rareza = com.pixelvault.model.Rareza.SANTO_GRIAL " +
           "     OR o.videojuego.estadoConservacion = com.pixelvault.model.EstadoConservacion.MINT_IN_BOX) " +
           "AND o.estado = com.pixelvault.model.EstadoOrden.COMPLETADA")
    long contarComprasCriticasRecientes(@Param("coleccionistaId") Long coleccionistaId, 
                                        @Param("desde") LocalDateTime desde);
}
