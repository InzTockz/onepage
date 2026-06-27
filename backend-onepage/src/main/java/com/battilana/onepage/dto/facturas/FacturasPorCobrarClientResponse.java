package com.battilana.onepage.dto.facturas;

import java.math.BigDecimal;

public record FacturasPorCobrarClientResponse(
        String ruc, //RUC DEL CLIENTE
        String nombre, //RAZON SOCIAL
        Integer documento,
        String comprobante,
        String emision,
        String vencimiento,
        String moneda,
        BigDecimal importe,
        BigDecimal saldo,
        String vendedor,
        BigDecimal lc
) {
}
