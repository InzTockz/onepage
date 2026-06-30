package com.battilana.onepage.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "tbl_pago_vigente",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_pago_vig_banco_unico_factura_venc_importe",
                columnNames = {"id_banco", "nro_unico", "nro_factura", "fecha_vencimiento", "importe"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class PagoVigenteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago_vigente")
    private Long idPagoVigente;

    @Column(name = "nro_unico", nullable = false, length = 100)
    private String nroUnico;

    @Column(name = "nro_factura", nullable = false, length = 100)
    private String nroFactura;

    @Column(name = "aceptante", nullable = false, length = 1000)
    private String aceptante;

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;          // nullable: solo algunos bancos lo traen

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "moneda", length = 50)
    private String moneda;                   // nullable: solo un formato lo trae

    @Column(name = "importe", nullable = false, precision = 15, scale = 2)
    private BigDecimal importe;

    @Column(name = "estado_original", length = 50)
    private String estadoOriginal;           // nullable: "Situación"/"Estado" o "No tiene"

    @Column(name = "estado", nullable = false, length = 50)
    private String estado;

    @Column(name = "archivo_origen", length = 255)
    private String archivoOrigen;            // nombre del Excel del que se cargó (lo asigna el service)

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;     // se auto-asigna al insertar; no cambia en updates

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_banco", nullable = false)
    private BancoEntity bancoEntity;

}
