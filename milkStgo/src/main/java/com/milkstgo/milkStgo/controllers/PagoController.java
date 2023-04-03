package com.milkstgo.milkStgo.controllers;

import com.milkstgo.milkStgo.services.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class PagoController {

    @Autowired
    PagoService pagoService;

    @GetMapping("/crearPlanilla")
    public String main(){ return "crearPlanilla"; }

    @PostMapping("/crearPlanilla")
    public String calcularPlanilla(){
        //pagoService.crearPlanilla();
        return "redirect:/verPlanilla";
    }

    @GetMapping("/viewPlanilla")
    public String verPlanilla(){ return "viewPlanilla";}
}
