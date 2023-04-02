package com.milkstgo.milkStgo.services;

import com.milkstgo.milkStgo.entities.AcopioEntity;
import com.milkstgo.milkStgo.repositories.AcopioRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
public class AcopioService {
    @Autowired
    AcopioRepository acopioRepository;


    public ArrayList<AcopioEntity> obtenerData(){
        return (ArrayList<AcopioEntity>) acopioRepository.findAll();
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
        acopioRepository.deleteAll();

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
                    guardarDataDB(bfRead.split(";")[0], bfRead.split(";")[1], bfRead.split(";")[2], bfRead.split(";")[3]);
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

    public void guardarData(AcopioEntity data){
        acopioRepository.save(data);
    }

    public void guardarDataDB(String fecha, String turno, String proveedor, String kls_leche) {
        AcopioEntity newData = new AcopioEntity();
        newData.setFecha(fecha);
        System.out.println(fecha);
        System.out.println(turno);
        System.out.println(proveedor);
        System.out.println(kls_leche);
        //newData.setTurno(turno);
        //if("M".equals(turno)){
        // cambiar en entidad del id proveedor
        //} else if ("T".equals(turno)) {
        // cambiar en entidad del id proveedor
        //}
        newData.setId_proveedor(proveedor);
        newData.setKls_leche(kls_leche);
        newData.setPor_grasa(0);
        newData.setPor_solido(0);
        guardarData(newData);
        // Sumar frecuencia en id del proveedor
    }
}
