package com.pixelvault.repository;

import com.pixelvault.model.VideojuegoRetro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideojuegoRepository extends JpaRepository<VideojuegoRetro, Long> {

    Optional<VideojuegoRetro> findBySerialRetro(String serialRetro);

    boolean existsBySerialRetro(String serialRetro);
}
