package com.milkstgo.milkStgo.entities;

import com.sun.istack.NotNull;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "proveedor")
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorEntity {

    @NotNull
    @Id
    private String codigo;
    private String nombre;
    private String categoria;
    private String retencion;
}
