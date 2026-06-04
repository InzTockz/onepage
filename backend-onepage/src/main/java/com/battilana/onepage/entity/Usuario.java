package com.battilana.onepage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_usuario")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;
    @Column(length = 150)
    private String nombre;
    @Column(length = 150)
    private String apellido;
    @Column(length = 100)
    private String correo;
    @Column(length = 100)
    private String username;
    private String password;
    private Boolean estado;
    @Enumerated(EnumType.STRING)
    private Roles roles;
}
