package com.battilana.onepage.dto.pago;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PagoVigenteNormalizadoDto (
        String nroUnico,
        String nroFactura,
        String aceptante,
        LocalDate fechaIngreso,
        LocalDate fechaVencimiento,
        String moneda,            // "USD" / "PEN"
        BigDecimal importe,
        String estadoOriginal     // null en Scotiabank; el service homologa a "estado"
){
}
