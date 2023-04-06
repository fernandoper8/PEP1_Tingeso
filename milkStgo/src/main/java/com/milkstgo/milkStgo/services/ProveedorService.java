package com.milkstgo.milkStgo.services;

import com.milkstgo.milkStgo.entities.ProveedorEntity;
import com.milkstgo.milkStgo.repositories.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ProveedorService {

    @Autowired
    ProveedorRepository proveedorRepository;

    public void guardarProveedor(String nombre, String codigo, String retencion, String categoria){
        ProveedorEntity proveedor = new ProveedorEntity();
        proveedor.setNombre(nombre);
        proveedor.setCodigo(codigo);
        proveedor.setRetencion(retencion);
        proveedor.setCategoria(categoria);
        // Proveedor nuevo, no se registra una entrega de leche hasta que se carge un acopio
        proveedorRepository.save(proveedor);
    }

    public ArrayList<ProveedorEntity> obtenerProveedores(){
        return (ArrayList<ProveedorEntity>) proveedorRepository.findAll();
    }

}
