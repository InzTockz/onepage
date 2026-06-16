package com.battilana.onepage.dto.pago;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PagoNormalizadoDto(
        String nroTransaccion,
        String nroFactura,
        String aceptante,
        LocalDate fechaOperacion,
        LocalDate fechaVencimiento,
        LocalDate fechaIngresoBanco,
        String moneda,
        BigDecimal importeIngreso,
        BigDecimal importe,
        BigDecimal interes,
        BigDecimal comision,
        BigDecimal gastos,
        String estadoOriginal
) {
}
