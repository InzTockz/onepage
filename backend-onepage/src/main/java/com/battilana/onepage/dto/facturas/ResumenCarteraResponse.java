package com.battilana.onepage.dto.facturas;

import java.math.BigDecimal;

public record ResumenCarteraResponse(
        Integer periodo,
        BigDecimal noVencido,
        BigDecimal vencido0a30,
        BigDecimal vencido31a45,
        BigDecimal vencido46a60,
        BigDecimal vencido61a90,
        BigDecimal vencido91a180,
        BigDecimal vencido180aMas
) {
}
