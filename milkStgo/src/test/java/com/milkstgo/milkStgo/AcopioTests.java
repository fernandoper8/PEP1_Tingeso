package com.milkstgo.milkStgo;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.milkstgo.milkStgo.entities.AcopioEntity;
import com.milkstgo.milkStgo.repositories.AcopioRepository;
import com.milkstgo.milkStgo.services.AcopioService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class AcopioTests {
    
    @Autowired
    AcopioRepository acopioRepository;
    @Autowired
    AcopioService acopioService;

    @Test
    void obtenerDataTest1(){
        int actual = acopioService.obtenerData().size();
        AcopioEntity acopio = new AcopioEntity();
        acopio.setId_proveedor("1020");
        acopio.setFecha("09-04-2023");
        acopio.setKls_leche("1000");
        acopio.setTurno("M");
        acopioRepository.save(acopio);

        ArrayList<AcopioEntity> acopios = acopioService.obtenerData();
        assertEquals(actual+1, acopios.size());
        acopioRepository.deleteAll();
    }

    @Test
    void eliminarAcopiosTest1(){
        acopioRepository.deleteAll();
        AcopioEntity acopio = new AcopioEntity();
        acopio.setId_acopio(1000);
        acopio.setId_proveedor("1030");
        acopio.setFecha("09-04-2023");
        acopio.setKls_leche("1000");
        acopio.setTurno("M");
        acopioService.guardarData(acopio);
        
        acopioService.eliminarAcopios();
        ArrayList<AcopioEntity> acopios = acopioService.obtenerData();
        assertEquals(0, acopios.size());
    }

    @Test
    void guardarDataTest1(){
        int actual = acopioService.obtenerData().size();
        AcopioEntity acopio = new AcopioEntity();
        acopio.setId_proveedor("1040");
        acopio.setFecha("09-04-2023");
        acopio.setKls_leche("1000");
        acopio.setTurno("M");
        
        acopioService.guardarData(acopio);

        ArrayList<AcopioEntity> acopios = acopioService.obtenerData();
        assertEquals(actual+1, acopios.size());
        acopioRepository.deleteAll();
    }

    @Test
    void guardarDataDBTest1(){
        acopioRepository.deleteAll();
        acopioService.guardarDataDB("09-04-2023", "M", "1050", "1000");
        ArrayList<AcopioEntity> acopios = acopioService.obtenerData();
        assertEquals(1, acopios.size());
        acopioRepository.deleteAll();
    }

    @Test // Hay minimo 1 acopio registrado del proveedor 1050
    void obtenerAcopiosPorProveedorTest1(){
        int actual = acopioService.obtenerData().size();
        AcopioEntity acopio = new AcopioEntity();
        acopio.setId_proveedor("1060");
        acopio.setFecha("09-04-2023");
        acopio.setKls_leche("1000");
        acopio.setTurno("M");
        acopioService.guardarData(acopio);
        ArrayList<AcopioEntity> acopios = acopioService.obtenerAcopiosPorProveedor("1060");
        assertEquals(actual+1, acopios.size());
        acopioRepository.deleteAll();
    }   

    @Test // No hay acopios del proveedor
    void obtenerAcopiosPorProveedorTest2(){
        ArrayList<AcopioEntity> acopios = acopioService.obtenerAcopiosPorProveedor("1070");
        assertEquals(0, acopios.size());
    }
}
