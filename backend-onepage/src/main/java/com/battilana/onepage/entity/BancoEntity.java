package com.battilana.onepage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_banco")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BancoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_banco")
    private int idBanco;
    @Column(name = "codigo")
    private String codigo;
    @Column(name = "nombre")
    private String nombre;
}
