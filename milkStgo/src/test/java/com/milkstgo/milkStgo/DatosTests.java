package com.milkstgo.milkStgo;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.milkstgo.milkStgo.entities.DatosEntity;
import com.milkstgo.milkStgo.repositories.DatosRepository;
import com.milkstgo.milkStgo.services.DatosService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class DatosTests {

    @Autowired
    DatosRepository datosRepository;
    @Autowired
    DatosService datosService;

    @Test
    void obtenerDataTest1(){
        datosRepository.deleteAll();
        DatosEntity dato = new DatosEntity();
        dato.setId_proveedor("1020");
        dato.setPor_grasa(3);
        dato.setPor_solidos(4);
        datosRepository.save(dato);

        ArrayList<DatosEntity> datos = datosService.obtenerData();
        assertEquals(1, datos.size());
        datosRepository.deleteAll();
    }

    @Test
    void eliminarDatosTest1(){
        datosRepository.deleteAll();
        DatosEntity dato = new DatosEntity();
        dato.setId_datos(1000);
        dato.setId_proveedor("1030");
        dato.setPor_grasa(3);
        dato.setPor_solidos(4);
        datosService.guardarData(dato);
        
        datosService.eliminarDatos();
        ArrayList<DatosEntity> datos = datosService.obtenerData();
        assertEquals(0, datos.size());
    }

    @Test
    void guardarDataTest1(){
        datosRepository.deleteAll();
        DatosEntity dato = new DatosEntity();
        dato.setId_proveedor("1030");
        dato.setPor_grasa(3);
        dato.setPor_solidos(4);
        datosService.guardarData(dato);
        
        ArrayList<DatosEntity> datos = datosService.obtenerData();
        assertEquals(1, datos.size());
        datosRepository.deleteAll();
    }

    @Test
    void guardarDataDBTest1(){
        datosRepository.deleteAll();
        datosService.guardarDataDB("1030", "3", "4");
        ArrayList<DatosEntity> datos = datosService.obtenerData();
        assertEquals(1, datos.size());
        datosRepository.deleteAll();
    }

    @Test // Hay datas del proveedor 1030
    void obtenerDataPorProveedorTest1(){
        datosRepository.deleteAll();
        DatosEntity dato = new DatosEntity();
        dato.setId_proveedor("1030");
        dato.setPor_grasa(3);
        dato.setPor_solidos(4);
        datosService.guardarData(dato);
        
        DatosEntity datosResultado = datosService.obtenerDataPorProveedor("1030");
        assertEquals("1030", datosResultado.getId_proveedor());
        datosRepository.deleteAll();
    }

    @Test // No hay datas del proveedor 1030
    void obtenerDataPorProveedorTest2(){
        datosRepository.deleteAll();
        DatosEntity datosResultado = datosService.obtenerDataPorProveedor("1030");
        assertNull(datosResultado);
        datosRepository.deleteAll();
    }
}
