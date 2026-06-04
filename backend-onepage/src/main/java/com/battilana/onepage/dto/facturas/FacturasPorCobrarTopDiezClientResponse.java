package com.battilana.onepage.dto.facturas;

import java.math.BigDecimal;

public record FacturasPorCobrarTopDiezClientResponse(
        String nombre,
        BigDecimal saldo
) {
}
