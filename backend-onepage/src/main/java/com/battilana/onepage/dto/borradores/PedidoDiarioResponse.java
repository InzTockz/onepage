package com.battilana.onepage.dto.borradores;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PedidoDiarioResponse (
        Integer docEntry,
        String cardCode,
        String cardName,
        String pymntGroup, //CONDICION DE PAGO
        BigDecimal docTotalFC, //MONTO TOTAL DEL PEDIDO
        BigDecimal creditLine, //LIMITE DE CREDITO
        LocalDateTime docDate, // ULTIMO PAGO RECIBIDO
        Long facturasVencidas, //NUMERO DE FACTURAS VENCIDAS
        BigDecimal montoVencido, //MONTO TOTAL VENCIDO
        //Double cuentaTotal,
        BigDecimal montoPorVencer, //MONTO POR VENCER
        LocalDateTime fechaVencida //FECHA DEL DOCUMENTO MAS VENCIDO
        //String comentario
){
}
