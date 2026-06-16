package com.battilana.onepage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "tbl_pago",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_pago_banco_trx_fecha_importe",
                        columnNames = {
                                "id_banco",
                                "nro_transaccion",
                                "fecha_operacion",
                                "importe"
                        }
                )
        },
        indexes = {
                @Index(name = "idx_pago_factura", columnList = "nro_factura"),
                @Index(name = "idx_pago_fecha", columnList = "fecha_operacion"),
                @Index(name = "idx_pago_estado", columnList = "estado"),
                @Index(name = "idx_pago_aceptante", columnList = "aceptante")
        }
)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Long idPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "id_banco",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_pago_banco")
    )
    private BancoEntity bancoEntity;

    // IDENTIFICADORES

    @Column(name = "nro_transaccion", nullable = false, length = 30)
    private String nroTransaccion;

    @Column(name = "nro_factura", nullable = false, length = 30)
    private String nroFactura;

    @Column(name = "aceptante", nullable = false, length = 150)
    private String aceptante;

    // FECHAS

    @Column(name = "fecha_operacion", nullable = false)
    private LocalDate fechaOperacion;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "fecha_ingreso_banco")
    private LocalDate fechaIngresoBanco;

    // MONTOS

    @Column(name = "moneda", nullable = false, length = 10)
    private String moneda = "Dolares";

    @Column(name = "importe_ingreso", precision = 12, scale = 2)
    private BigDecimal importeIngreso;

    @Column(name = "importe", nullable = false, precision = 12, scale = 2)
    private BigDecimal importe;

    @Column(name = "interes", precision = 12, scale = 2)
    private BigDecimal interes;

    @Column(name = "comision", precision = 12, scale = 2)
    private BigDecimal comision;

    @Column(name = "gastos", precision = 12, scale = 2)
    private BigDecimal gastos;

    // ESTADOS

    @Column(name = "estado", nullable = false, length = 25)
    private String estado;

    @Column(name = "estado_original", nullable = false, length = 50)
    private String estadoOriginal;

    @Column(name = "estado_operativo", length = 50)
    private String estadoOperativo;

    // TRAZABILIDAD

    @Column(name = "archivo_origen", length = 120)
    private String archivoOrigen;

    @Column(name = "fecha_carga", insertable = false, updatable = false)
    private LocalDateTime fechaCarga;

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id_pago")
//    private Long idPago;
//    @Column(name = "nro_transaccion")
//    private String nroTransaccion;
//    @Column(name = "nro_factura")
//    private String nroFactura;
//    @Column(name = "aceptante")
//    private String aceptante;
//    @Column(name = "fecha_operacion")
//    private LocalDateTime fechaOperacion;
//    @Column(name = "fecha_vencimiento")
//    private LocalDate fechaVencimiento;
//    @Column(name = "moneda")
//    private String moneda;
//    @Column(name = "importe")
//    private Double importe;
//    @Column(name = "interes")
//    private Double interes;
//    @Column(name = "comision")
//    private Double comision;
//    @Column(name = "gastos")
//    private Double gastos;
//    @Column(name = "estado_original")
//    private String estadoOriginal;
//    @Column(name = "producto")
//    private String producto;
//    @Column(name = "fecha_carga")
//    private LocalDateTime fechaCarga;
//    @ManyToOne
//    @JoinColumn(name = "id_banco")
//    private BancoEntity bancoEntity;
}
