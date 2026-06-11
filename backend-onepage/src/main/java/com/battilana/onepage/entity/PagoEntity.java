package com.battilana.onepage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_pago")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Long idPago;
    @Column(name = "nro_transaccion")
    private String nroTransaccion;
    @Column(name = "nro_factura")
    private String nroFactura;
    @Column(name = "aceptante")
    private String aceptante;
    @Column(name = "fecha_operacion")
    private LocalDateTime fechaOperacion;
    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;
    @Column(name = "moneda")
    private String moneda;
    @Column(name = "importe")
    private Double importe;
    @Column(name = "interes")
    private Double interes;
    @Column(name = "comision")
    private Double comision;
    @Column(name = "gastos")
    private Double gastos;
    @Column(name = "estado_original")
    private String estadoOriginal;
    @Column(name = "producto")
    private String producto;
    @Column(name = "fecha_carga")
    private LocalDateTime fechaCarga;
    @ManyToOne
    @JoinColumn(name = "id_banco")
    private BancoEntity bancoEntity;
}
