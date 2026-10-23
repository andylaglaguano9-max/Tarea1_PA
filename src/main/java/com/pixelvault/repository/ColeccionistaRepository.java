package com.pixelvault.repository;

import com.pixelvault.model.Coleccionista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ColeccionistaRepository extends JpaRepository<Coleccionista, Long> {

    Optional<Coleccionista> findByAliasGamer(String aliasGamer);

    boolean existsByAliasGamer(String aliasGamer);

    boolean existsByCorreoElectronico(String correoElectronico);
}
