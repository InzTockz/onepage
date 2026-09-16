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
        BigDecimal lc,
        String nroUnico,
        String banco
) {

    // DEvuelve una copia con el vinculo bancario lleno (los records son inmutables)
    public FacturasPorCobrarClientResponse conVinculacion(String nroUnico, String banco){
        return new FacturasPorCobrarClientResponse(
                ruc, nombre, documento, comprobante, emision, vencimiento, moneda, importe, saldo,
                vendedor, lc, nroUnico, banco
        );
    }
}
