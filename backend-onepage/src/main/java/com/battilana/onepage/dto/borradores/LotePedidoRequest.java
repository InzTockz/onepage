package com.battilana.onepage.dto.borradores;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LotePedidoRequest(
        String codCliente,
        String nombres,
        String condicionDePago,
        BigDecimal montoTotalPedido,
        BigDecimal limiteCredito,
        BigDecimal montoVencido,
        BigDecimal montoPorVencer,
        Long facturasVencidas,
        LocalDateTime facturaVencida,
        LocalDateTime facturaPagada
) {
}
