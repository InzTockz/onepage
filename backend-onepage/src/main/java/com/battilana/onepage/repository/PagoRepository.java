package com.battilana.onepage.repository;

import com.battilana.onepage.entity.PagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface PagoRepository extends JpaRepository<PagoEntity, Long> {

    List<PagoEntity> findAllByOrderByIdPagoDesc();

    @Query("SELECT P " +
            "FROM PagoEntity P " +
            "WHERE P.fechaOperacion BETWEEN :fechaInicio AND :fechaFin")
    List<PagoEntity> buscarPorFechas(@RequestParam("fechaInicio") String fechaInicio, @RequestParam("fechaFin") String fechaFin);
}
