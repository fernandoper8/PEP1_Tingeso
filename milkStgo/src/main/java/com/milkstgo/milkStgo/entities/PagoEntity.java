package com.milkstgo.milkStgo.entities;

import com.sun.istack.NotNull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "pago")
@NoArgsConstructor
@AllArgsConstructor
public class PagoEntity {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_pago;
    private String id_proveedor;
    private String nombre_proveedor;
    private int total_kls_leche;
    private int frecuencia;
    private float promedio_diario_kls;
    private int por_variacion_leche;
    private int por_grasa;
    private int por_variacion_grasa;
    private int por_solidos;
    private int por_variacion_solidos;
    private int bono_frecuencia;
    private int dcto_variacion_leche;
    private int dcto_variacion_grasa;
    private int dcto_variacion_solidos;
    private int pago_total;
    private int monto_retencion;
    private int pago_final;
}
