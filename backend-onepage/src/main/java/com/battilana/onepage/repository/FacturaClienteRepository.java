package com.battilana.onepage.repository;

import com.battilana.onepage.dto.facturas.FacturasPorCobrarResponse;
import com.battilana.onepage.dto.facturas.ResumenCarteraResponse;
import com.battilana.onepage.entity.FacturaClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FacturaClienteRepository extends JpaRepository<FacturaClienteEntity, Long> {

    @Query("SELECT FC " +
            "FROM FacturaClienteEntity FC " +
            "WHERE YEAR(FC.fechaRegistro)=:anio " +
            "AND FC.periodo=:periodo")
    List<FacturaClienteEntity> buscarFacturasPorAnioYPeriodo(@Param("anio") Integer anio, @Param("periodo") Integer periodo);

    @Query("SELECT FC " +
            "FROM FacturaClienteEntity FC " +
            "WHERE YEAR(FC.fechaRegistro)=:anio ")
    List<FacturaClienteEntity> buscarFacturasPorAnio(@Param("anio") Integer anio);

    @Query(value = "SELECT periodo, " +
            "SUM(CASE WHEN dias <= 0 THEN saldo ELSE 0 END) as no_vencidos, " +
            "SUM(CASE WHEN dias BETWEEN 1 AND 30 THEN saldo ELSE 0 END) as vencido_0_30, " +
            "SUM(CASE WHEN dias BETWEEN 31 AND 45 THEN saldo ELSE 0 END) as vencido_31_45, " +
            "SUM(CASE WHEN dias BETWEEN 46 AND 60 THEN saldo ELSE 0 END) as vencido_46_60, " +
            "SUM(CASE WHEN dias BETWEEN 61 AND 90 THEN saldo ELSE 0 END) as vencido_61_90, " +
            "SUM(CASE WHEN dias BETWEEN 91 AND 180 THEN saldo ELSE 0 END) as vencido_91_180, " +
            "SUM(CASE WHEN dias > 180 THEN saldo ELSE 0 END) as vencido_180_mas " +
            "FROM (" +
            "SELECT periodo, saldo, " +
            "DATEDIFF(fecha_registro, vencimiento) as dias " +
            "FROM tbl_factura_cliente " +
            "WHERE saldo>0) t " +
            "GROUP BY periodo " +
            "ORDER BY periodo"
            , nativeQuery = true)
    List<ResumenCarteraResponse> resumenCartera();

    @Query(value = "SELECT periodo, " +
            "SUM(CASE WHEN dias <= 0 THEN saldo ELSE 0 END) as no_vencidos, " +
            "SUM(CASE WHEN dias BETWEEN 1 AND 30 THEN saldo ELSE 0 END) as vencido_0_30, " +
            "SUM(CASE WHEN dias BETWEEN 31 AND 45 THEN saldo ELSE 0 END) as vencido_31_45, " +
            "SUM(CASE WHEN dias BETWEEN 46 AND 60 THEN saldo ELSE 0 END) as vencido_46_60, " +
            "SUM(CASE WHEN dias BETWEEN 61 AND 90 THEN saldo ELSE 0 END) as vencido_61_90, " +
            "SUM(CASE WHEN dias BETWEEN 91 AND 180 THEN saldo ELSE 0 END) as vencido_91_180, " +
            "SUM(CASE WHEN dias > 180 THEN saldo ELSE 0 END) as vencido_180_mas " +
            "FROM (" +
            "SELECT periodo, saldo, " +
            "DATEDIFF(fecha_registro, vencimiento) as dias " +
            "FROM tbl_factura_cliente " +
            "WHERE saldo>0 AND periodo BETWEEN 1 AND :periodo) t " +
            "GROUP BY periodo " +
            "ORDER BY periodo"
            , nativeQuery = true)
    List<ResumenCarteraResponse> resumenCarteraPorPeriodo(@Param("periodo") Integer periodo);

    @Query(value = "SELECT periodo, " +
            "SUM(CASE WHEN dias <= 0 THEN saldo ELSE 0 END) as no_vencidos, " +
            "SUM(CASE WHEN dias BETWEEN 1 AND 30 THEN saldo ELSE 0 END) as vencido_0_30, " +
            "SUM(CASE WHEN dias BETWEEN 31 AND 45 THEN saldo ELSE 0 END) as vencido_31_45, " +
            "SUM(CASE WHEN dias BETWEEN 46 AND 60 THEN saldo ELSE 0 END) as vencido_46_60, " +
            "SUM(CASE WHEN dias BETWEEN 61 AND 90 THEN saldo ELSE 0 END) as vencido_61_90, " +
            "SUM(CASE WHEN dias BETWEEN 91 AND 180 THEN saldo ELSE 0 END) as vencido_91_180, " +
            "SUM(CASE WHEN dias > 180 THEN saldo ELSE 0 END) as vencido_180_mas " +
            "FROM (" +
            "SELECT periodo, saldo, " +
            "DATEDIFF(fecha_registro, vencimiento) as dias " +
            "FROM tbl_factura_cliente " +
            "WHERE saldo>0 " +
            "AND periodo BETWEEN 1 AND :periodo " +
            "AND vendedor=:vendedor) t " +
            "GROUP BY periodo " +
            "ORDER BY periodo"
            , nativeQuery = true)
    List<ResumenCarteraResponse> resumenCarteraPorPeriodoYVendedor(@Param("periodo") Integer periodo, @Param("vendedor") String vendedor);

    @Query(value = "SELECT periodo, " +
            "SUM(CASE WHEN dias <= 0 THEN saldo ELSE 0 END) as no_vencidos, " +
            "SUM(CASE WHEN dias BETWEEN 1 AND 30 THEN saldo ELSE 0 END) as vencido_0_30, " +
            "SUM(CASE WHEN dias BETWEEN 31 AND 45 THEN saldo ELSE 0 END) as vencido_31_45, " +
            "SUM(CASE WHEN dias BETWEEN 46 AND 60 THEN saldo ELSE 0 END) as vencido_46_60, " +
            "SUM(CASE WHEN dias BETWEEN 61 AND 90 THEN saldo ELSE 0 END) as vencido_61_90, " +
            "SUM(CASE WHEN dias BETWEEN 91 AND 180 THEN saldo ELSE 0 END) as vencido_91_180, " +
            "SUM(CASE WHEN dias > 180 THEN saldo ELSE 0 END) as vencido_180_mas " +
            "FROM (" +
            "SELECT periodo, saldo, " +
            "DATEDIFF(fecha_registro, vencimiento) as dias " +
            "FROM tbl_factura_cliente " +
            "WHERE saldo>0 AND vendedor=:vendedor) t " +
            "GROUP BY periodo " +
            "ORDER BY periodo"
            , nativeQuery = true)
    List<ResumenCarteraResponse> resumenCarteraPorVendedor(@Param("vendedor") String vendedor);

    @Query("SELECT MAX(F.periodo) " +
            "FROM FacturaClienteEntity F " +
            "WHERE YEAR(F.fechaRegistro)=:anio")
    Integer obtenerUltimoPeriodo(@Param("anio") Integer anio);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END " +
            "FROM FacturaClienteEntity f " +
            "WHERE f.periodo = :periodo AND YEAR(f.fechaRegistro) =:anio")
    boolean existePeriodoEnAnio(@Param("periodo") int periodo, @Param("anio") int anio);

    @Query("SELECT f " +
            "FROM FacturaClienteEntity f " +
            "WHERE f.periodo=:periodo AND YEAR(f.fechaRegistro)=:anio " +
            "AND f.comprobante =:comprobante")
    FacturaClienteEntity buscarFacturaPorComprobantePorPeriodoPorAnio(@Param("comprobante") String comprobante, @Param("periodo") int periodo, @Param("anio") int anio);
}
