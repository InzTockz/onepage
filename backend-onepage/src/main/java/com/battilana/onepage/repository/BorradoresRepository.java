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
            "WHERE b.estadoBorrador in ('PEDIDO_REGISTRADO','LOTE_GENERADO')")
    List<BorradoresEntity> findByEstadoBorrador();
}
