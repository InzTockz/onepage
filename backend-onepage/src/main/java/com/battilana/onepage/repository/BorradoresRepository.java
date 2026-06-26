package com.battilana.onepage.repository;

import com.battilana.onepage.entity.BorradoresEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BorradoresRepository extends JpaRepository<BorradoresEntity, Integer> {

    @Query("SELECT b " +
            "FROM BorradoresEntity b " +
            "WHERE b.docEntry=:docEntry")
    BorradoresEntity findByDocEntry(@Param("docEntry") Integer docEntry);

    @Query("SELECT b " +
            "FROM BorradoresEntity  b " +
            "WHERE b.estadoBorrador in ('PEDIDO_REGISTRADO','LOTE_GENERADO') " +
            "AND b.estado = true")
    List<BorradoresEntity> findByEstadoBorrador();

    @Query("SELECT  b " +
            "FROM BorradoresEntity b " +
            "WHERE b.estadoBorrador = 'PEDIDO_REGISTRADO' " +
            "AND b.estado = true")
    List<BorradoresEntity> findByEstadoBorradorRegistrado();

    @Query("SELECT b " +
            "FROM BorradoresEntity b " +
            "WHERE b.estadoBorrador = 'LOTE_ENVIADO' " +
            "AND b.estado = true")
    List<BorradoresEntity> findByEstadoBorradorEnviado();

    @Query("SELECT b " +
            "FROM BorradoresEntity b " +
            "WHERE b.estadoBorrador = 'LOTE_GENERADO' " +
            "AND b.estado = true")
    List<BorradoresEntity> findByEstadoBorradorLoteGenerado();
}
