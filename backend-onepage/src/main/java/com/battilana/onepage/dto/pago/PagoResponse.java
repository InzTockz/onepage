package com.battilana.onepage.dto.pago;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PagoResponse(
        Long idPago,
        Short idBanco,
        String nroTransaccion, // <= SE MUESTRA EN EL TXT
        String nroFactura, // <= SE MUESTRA EN EL TXT
        String aceptante, // <= SE MUESTRA EN EL TXT
        LocalDate fechaOperacion, // <= SE MUESTRA EN EL TXT
        LocalDate fechaVencimiento, // <= SE MUESTRA EN EL TXT
        LocalDate fechaIngresoBanco, // <= SE MUESTRA EN EL TXT
        String moneda, // <= SE MUESTRA EN EL TXT
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
