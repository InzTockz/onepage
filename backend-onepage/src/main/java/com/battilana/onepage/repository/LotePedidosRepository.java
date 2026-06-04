package com.battilana.onepage.repository;

import com.battilana.onepage.entity.LotePedidosEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LotePedidosRepository extends JpaRepository<LotePedidosEntity, Integer> {

    List<LotePedidosEntity> findByEstadoTrue();
}
