package com.battilana.onepage.repository;

import com.battilana.onepage.entity.PagoVigenteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PagoVigenteRepository extends JpaRepository<PagoVigenteEntity, Long> {

    @Query("SELECT PV " +
            "FROM PagoVigenteEntity PV " +
            "WHERE PV.estadoRegistro = true " +
            "ORDER BY PV.idPagoVigente DESC")
    List<PagoVigenteEntity> findAll();

    @Query("SELECT PV " +
            "FROM PagoVigenteEntity PV " +
            "JOIN FETCH PV.bancoEntity")
    List<PagoVigenteEntity> findAllWithBank();

    @Modifying
    @Query("DELETE " +
            "FROM PagoVigenteEntity  PV " +
            "WHERE PV.producto = :nomProducto")
    void deletePagoVigentePorProducto(@Param("nomProducto") String nomProducto);

    @Modifying
    @Query("DELETE " +
            "FROM PagoVigenteEntity PV " +
            "WHERE PV.bancoEntity.codigo = :codigo")
    void deletePagoVigentePorCodigo(@Param("codigo") String codigo);
}
