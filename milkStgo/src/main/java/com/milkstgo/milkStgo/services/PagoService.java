package com.milkstgo.milkStgo.services;

import com.milkstgo.milkStgo.entities.AcopioEntity;
import com.milkstgo.milkStgo.entities.DatosEntity;
import com.milkstgo.milkStgo.entities.ProveedorEntity;
import com.milkstgo.milkStgo.repositories.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class PagoService {

    @Autowired
    PagoRepository pagoRepository;

    ProveedorService proveedorService;
    AcopioService acopioService;
    DatosService datosService;

    public void crearPlanilla(){
        // Se obtienen todos los proveedores registrados
        ArrayList<ProveedorEntity> proveedores = proveedorService.obtenerProveedores();
        // Se obtienen todos los acopios
        ArrayList<AcopioEntity> acopios = acopioService.obtenerData();
        // Se obtienen todos los datos de leche
        ArrayList<DatosEntity> datos = datosService.obtenerData();
        // Para saber si un proveedor entrego en tarde o mañana
        int turnoM = 0;
        int turnoT = 0;
        // Para contar la frecuencia de entregas de un proveedor
        int frecuencia = 0;
        // Para calcular la bonificacion por categoria
        int bono_categoria = 0;

        for(ProveedorEntity proveedor : proveedores){
            //ArrayList<AcopioEntity> acopiosTest = acopioService.obtenerDataProveedor(proveedor.getCodigo());
            //for(AcopioEntity acopio : acopiosTest){
            //    System.out.println(acopio.getKls_leche());
            //}
        }
    }

}
