package com.battilana.onepage.entity;

import com.battilana.onepage.dto.facturas.FacturasPorCobrarClientResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tbl_lote_pedidos")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LotePedidosEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idLotePedidos;
    private String codCliente;
    private String nombre;
    @CreationTimestamp
    private LocalDate fechaCreacion;
    @CreationTimestamp
    private LocalDateTime fechaRecorte;
    private BigDecimal montoTotal;
    private BigDecimal lineaCredito;
    private String condicionPago;
    private BigDecimal montoPorCobrar;
    private BigDecimal montoVencido;
    private BigDecimal lineaCreditoUtilizada;
    private BigDecimal mora;
    private Long nroFacturasVencidas;
    private LocalDateTime fechaFacturaVencidaMasAntigua;
    private LocalDateTime fechaUltimaFacturaPagada;
    private Boolean estado;
    @Column(columnDefinition = "TEXT")
    private String facturasVencidas;
}
