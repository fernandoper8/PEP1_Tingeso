package com.milkstgo.milkStgo.entities;

import com.sun.istack.NotNull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Data
@Table(name = "planilla")
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class PlanillaEntity {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPlanilla;

    // Datos proveedor
    private String nombre; //
    private String codigo; //
    private String categoria; //
       
    // Datos relacionados a leche
    private int totalKlsLeche; //
    private float promedioDiarioKls; //  
    private int frecuencia; //     
    private int queTurnos; //       

    // Porcentajes de variacion
    private float porVariacionLeche; //   
    private int dctoVariacionLeche; //  
    private int porGrasa; //             
    private int porVariacionGrasa; //   
    private int dctoVariacionGrasa; //
    private int porSolidos; //           
    private int porVariacionSolidos; // 
    private int dctoVariacionSolidos; //

    // Datos respecto a pagos
    private int pagoAcopio; //
    private int pagoTotal;               
    private int pagoFinal;
    private int montoRetencion;           
    private int bonoFrecuencia; //        

    // Es el ultimo pago del proveedor?
    private int comparado; //
    private Date fecha; //
}
