package com.milkstgo.milkStgo.entities;

import com.sun.istack.NotNull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "datos")
@NoArgsConstructor
@AllArgsConstructor
public class DatosEntity {
    @Id
    @NotNull
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id_datos;
    private String id_proveedor;
    private int por_grasa;
    private int por_solidos;
}
