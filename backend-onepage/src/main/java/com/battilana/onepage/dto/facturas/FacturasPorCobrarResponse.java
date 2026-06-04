package com.battilana.onepage.dto.facturas;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FacturasPorCobrarResponse(
        Long idFacturaPorCobrar,
        String ruc,
        String nombre,
        Integer documento,
        String comprobante,
        String emision,
        String vencimiento,
        String moneda,
        BigDecimal importe,
        BigDecimal saldo,
        String vendedor,
        LocalDate fechaRegistro,
        Integer periodo,
        BigDecimal lc
) {
}
