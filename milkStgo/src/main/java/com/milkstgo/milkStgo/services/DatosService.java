package com.milkstgo.milkStgo.services;

import com.milkstgo.milkStgo.entities.DatosEntity;
import com.milkstgo.milkStgo.repositories.DatosRepository;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@Service
public class DatosService {

    @Autowired
    DatosRepository datosRepository;

    // no hacer prueba unitaria
    public ArrayList<DatosEntity> obtenerData(){
        return (ArrayList<DatosEntity>) datosRepository.findAll();
    }

    // no hacer prueba unitaria
    public void eliminarDatos(){
        datosRepository.deleteAll();
    }

    private final Logger logg = LoggerFactory.getLogger(AcopioService.class);
    @Generated
    public String guardar(MultipartFile file){
        String filename = file.getOriginalFilename();
        if(filename != null){
            if(!file.isEmpty()){
                try{
                    byte [] bytes = file.getBytes();
                    Path path  = Paths.get(file.getOriginalFilename());
                    Files.write(path, bytes);
                    logg.info("Archivo guardado");
                }
                catch (IOException e){
                    logg.error("ERROR", e);
                }
            }
            return "Archivo guardado con exito!";
        }
        else{
            return "No se pudo guardar el archivo";
        }
    }

    @Generated
    public void leerCsv(String direccion){
        String texto = "";
        BufferedReader bf = null;
        datosRepository.deleteAll();

        try{

            bf = new BufferedReader(new FileReader(direccion));
            String temp = "";
            String bfRead;
            int count = 1;
            while((bfRead = bf.readLine()) != null){
                if (count == 1){
                    count = 0;
                }
                else{
                    guardarDataDB(bfRead.split(";")[0], bfRead.split(";")[1], bfRead.split(";")[2]);
                    temp = temp + "\n" + bfRead;
                }
            }
            texto = temp;
            System.out.println("Archivo leido exitosamente");
        }catch(Exception e){
            System.err.println("No se encontro el archivo");
        }finally{
            if(bf != null){
                try{
                    bf.close();
                }catch(IOException e){
                    logg.error("ERROR", e);
                }
            }
        }
    }

    // no hacer prueba unitaria
    public void guardarData(DatosEntity data){
        datosRepository.save(data);
    }

    // no hacer prueba unitaria
    public void guardarDataDB(String proveedor, String grasa, String solidos) {
        DatosEntity newDatos = new DatosEntity();
        newDatos.setId_proveedor(proveedor);
        newDatos.setPor_grasa(Integer.parseInt(grasa));
        newDatos.setPor_solidos(Integer.parseInt(solidos));
        guardarData(newDatos);
    }

    // no hacer prueba unitaria
    public DatosEntity obtenerDataPorProveedor(String proveedor){
        for (DatosEntity data : datosRepository.findAll()){
            if (data.getId_proveedor().equals(proveedor)){
                return data;
            }
        }
        return null;
    }
}
