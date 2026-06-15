package com.battilana.onepage.dto.facturas;

import java.math.BigDecimal;

public record FacturasPorCobrarTopDiezMasVencidasClientResponse(
        String ruc,
        String nombre,
        String comprobante,
        String vencimiento,
        String moneda,
        BigDecimal importe,
        BigDecimal saldo,
        Integer diasVencido
) {
}
