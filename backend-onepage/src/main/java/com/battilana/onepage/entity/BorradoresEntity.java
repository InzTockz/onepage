package com.battilana.onepage.entity;

import com.battilana.onepage.enums.EstadoBorrador;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BorradoresEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idLotePedidos;
    private Integer docEntry; //NRO UNICO DE PEDIDO GESTIONADO
    private String codCliente;
    private String nombre;
    private BigDecimal montoTotalPedido;
    private String condicionPago;
    private BigDecimal lineaCredito;
    private BigDecimal montoTotalDeuda;
    private BigDecimal montoPorCobrar;
    private BigDecimal montoVencido;
    private BigDecimal lineaCreditoUtilizada;
    private BigDecimal mora;
    private Long nroFacturasVencidas;
    private LocalDateTime fechaFacturaVencidaMasAntigua;
    private LocalDateTime fechaUltimaFacturaPagada;
    //AUDITORIA
    private LocalDateTime fechaCreacionPedido; //LA FECHA EN LA QUE FUE CREADA EL PEDIDO
    private LocalDateTime fechaRegistro; //Fecha en la que se realizan los registros
    private LocalDateTime fechaLoteGenerado; //fecha que indica cuando se genero el lote - inicialmente se registrara como nulo
    @Enumerated(EnumType.STRING)
    private EstadoBorrador estadoBorrador;
    private Integer docTime; //LA HORA EN LA QUE FUE CREADO EL PEDIDO
    private String comentario;
}
