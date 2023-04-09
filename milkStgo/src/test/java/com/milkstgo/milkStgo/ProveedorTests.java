package com.milkstgo.milkStgo;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.milkstgo.milkStgo.entities.ProveedorEntity;
import com.milkstgo.milkStgo.repositories.ProveedorRepository;
import com.milkstgo.milkStgo.services.ProveedorService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ProveedorTests{

    @Autowired
    ProveedorRepository proveedorRepository;
    @Autowired
    ProveedorService proveedorService;

    @Test
    void guardarProveedorTest1(){
        int actual = proveedorService.obtenerProveedores().size();
        proveedorService.guardarProveedor("Armin van Buuren", "2000", "Si", "A");
        assertEquals(actual+1, proveedorService.obtenerProveedores().size());
        proveedorRepository.deleteAll();
    }

    @Test
    void obtenerProveedoresTest1(){
        ArrayList<ProveedorEntity> proveedores = proveedorService.obtenerProveedores();
        int actual = proveedores.size();
        proveedorService.guardarProveedor("Armin van Buuren", "2000", "Si", "A");
        assertEquals(actual+1, proveedorService.obtenerProveedores().size());
        proveedorRepository.deleteAll();
    }
}