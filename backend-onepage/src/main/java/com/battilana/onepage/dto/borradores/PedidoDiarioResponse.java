package com.battilana.onepage.dto.borradores;

import com.battilana.onepage.enums.EstadoBorrador;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PedidoDiarioResponse(
        Integer idLotePedidos,
        Integer docEntry, //NRO UNICO DE PEDIDO GESTIONADO
        String codCliente,
        String nombre,
        BigDecimal montoTotalPedido,
        String condicionPago,
        BigDecimal lineaCredito,
        BigDecimal montoTotalDeuda,
        BigDecimal montoPorCobrar,
        BigDecimal montoVencido,
        BigDecimal lineaCreditoUtilizada,
        BigDecimal mora,
        Long nroFacturasVencidas,
        LocalDateTime fechaFacturaVencidaMasAntigua,
        LocalDateTime fechaUltimaFacturaPagada,
        //AUDITORIA
        LocalDateTime fechaCreacionPedido,
        LocalDateTime fechaRegistro,
        LocalDateTime fechaLoteGenerado,
        EstadoBorrador estadoBorrador,
        String facturasVencidas,
        Short docTime,
        String comentario
) {
}
