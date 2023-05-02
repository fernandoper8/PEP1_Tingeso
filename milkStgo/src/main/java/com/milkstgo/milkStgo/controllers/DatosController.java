package com.milkstgo.milkStgo.controllers;

import com.milkstgo.milkStgo.entities.DatosEntity;
import com.milkstgo.milkStgo.services.DatosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

@Controller
@RequestMapping
public class DatosController {

    @Autowired
    DatosService datosService;
    @PostMapping("/addDatos")
    public String addAcopio(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes){
        datosService.guardar(file);
        redirectAttributes.addFlashAttribute("mensaje", "Datos guardados correctamente");
        datosService.leerCsv("Datos.csv");
        return "redirect:/archivos";
    }
}
