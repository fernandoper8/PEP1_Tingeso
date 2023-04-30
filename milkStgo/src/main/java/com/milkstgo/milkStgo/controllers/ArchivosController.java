package com.milkstgo.milkStgo.controllers;

import com.milkstgo.milkStgo.entities.AcopioEntity;
import com.milkstgo.milkStgo.entities.DatosEntity;
import com.milkstgo.milkStgo.services.AcopioService;
import com.milkstgo.milkStgo.services.DatosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;

@Controller
@RequestMapping
public class ArchivosController {

    @Autowired
    AcopioService acopioService;
    @Autowired
    DatosService datosService;

    @GetMapping("/archivos")
    public String archivos(Model model){
        ArrayList<AcopioEntity> acopios = acopioService.obtenerData();
        ArrayList<DatosEntity> datos = datosService.obtenerData();
        model.addAttribute("acopios", acopios);
        model.addAttribute("datos", datos);
        return "archivos";
    }
}
