package com.milkstgo.milkStgo.entities;

import com.sun.istack.NotNull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@Table(name = "pago")
@NoArgsConstructor
@AllArgsConstructor
public class PagoEntity {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPago;

    // Datos proveedor
    private String nombre; 
    private String codigo;        
       
    // Datos relacionados a leche
    private int totalKlsLeche;        
    private int frecuencia;             
    private float promedioDiarioKls;  
    private int bonoFrecuencia;        

    // Porcentajes de variacion
    private int porVariacionLeche;    
    private int dctoVariacionLeche;   
    private int porGrasa;              
    private int porVariacionGrasa;    
    private int dctoVariacionGrasa;   
    private int porSolidos;            
    private int porVariacionSolidos;  
    private int dctoVariacionSolidos; 

    // Datos respecto a pagos
    private int pagoTotal;             
    private int montoRetencion;        
    private int pagoFinal;             

    // Es el ultimo pago del proveedor?
    private int comparado;
    private Date fecha;
}
