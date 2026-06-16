package com.battilana.onepage.repository;

import com.battilana.onepage.entity.BancoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BancoRepository extends JpaRepository<BancoEntity, Short> {

    Optional<BancoEntity> findByCodigo(String codigo);
}
