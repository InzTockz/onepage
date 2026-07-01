package com.battilana.onepage.dto.pago;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PagoVigenteResponse(
        Long idPagoVigente,
        String nroUnico,
        String nroFactura,
        String aceptante,
        LocalDate fechaIngreso,
        LocalDate fechaVencimiento,
        String moneda,
        BigDecimal importe,
        String estadoOriginal,
        String estado,
        String archivoOrigen,
        LocalDateTime fechaRegistro,
        Long idBanco
) {
}
