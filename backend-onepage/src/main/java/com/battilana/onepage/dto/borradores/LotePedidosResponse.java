package com.battilana.onepage.dto.borradores;

import jakarta.persistence.Column;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record LotePedidosResponse(
        Integer idLotePedido,
        String codCliente,
        String nombre,
        LocalDate fechaCreacion,
        LocalDateTime fechaRecorte,
        BigDecimal montoTotal,
        BigDecimal lineaCredito,
        String condicionPago,
        BigDecimal montoPorCobrar,
        BigDecimal montoVencido,
        BigDecimal lineaCreditoUtilizada,
        BigDecimal mora,
        Long nroFacturasVencidas,
        LocalDateTime fechaFacturaVencidaMasAntigua,
        LocalDateTime fechaUltimaFacturaPagada,
        Boolean estado,
        String facturasVencidas
) {
}
