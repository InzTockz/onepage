package com.battilana.onepage.repository;

import com.battilana.onepage.entity.PagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagoRepository extends JpaRepository<PagoEntity, Long> {

    List<PagoEntity> findAllByOrderByIdPagoDesc();
}
