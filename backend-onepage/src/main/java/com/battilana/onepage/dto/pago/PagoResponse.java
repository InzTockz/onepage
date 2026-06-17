package com.battilana.onepage.dto.pago;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PagoResponse(
        Long idPago,
        Short idBanco,
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
        String estado,
        String estadoOriginal,
        String estadoOperativo,
        String archivoOrigen,
        String fechaCarga
) {
}
