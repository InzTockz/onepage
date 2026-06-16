package com.battilana.onepage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tbl_factura_cliente")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FacturaClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFacturaPorCobrar;
    private String ruc;
    private String nombre;
    private Integer documento;
    private String comprobante;
    private String emision;
    private String vencimiento;
    private String moneda;
    private BigDecimal importe;
    private BigDecimal saldo;
    private String vendedor;
    private LocalDate fechaRegistro; //Año del registro de la factura
    private Integer periodo; //Periodo del registro 1-12 los cuales son numeros del mes
    private BigDecimal lc;
}
