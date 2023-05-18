package com.milkstgo.milkStgo.entities;

import com.sun.istack.NotNull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@Table(name="acopio")
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class AcopioEntity {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_acopio;
    private String id_proveedor;
    private String fecha;
    private String kls_leche;
    private String turno;
}
