package com.milkstgo.milkStgo.controllers;

import com.milkstgo.milkStgo.services.AcopioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping
public class AcopioController {

    @Autowired
    AcopioService acopioService;

    @GetMapping("/addAcopio")
    public String main(){
        return "addAcopio";
    }

    @PostMapping("/addAcopio")
    public String addAcopio(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes){
        acopioService.guardar(file);
        redirectAttributes.addFlashAttribute("mensaje", "Acopio guardado correctamente");
        acopioService.leerCsv("Acopio.csv");
        return "redirect:/addAcopio";
    }

}
