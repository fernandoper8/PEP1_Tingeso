package com.milkstgo.milkStgo.controllers;

import com.milkstgo.milkStgo.entities.PagoEntity;
import com.milkstgo.milkStgo.services.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;

@Controller
@RequestMapping
public class PagoController {

    @Autowired
    PagoService pagoService;

    @GetMapping("/crearPlanilla")
    public String main(){ return "crearPlanilla"; }

    @PostMapping("/crearPlanilla")
    public String calcularPlanilla(){
        pagoService.crearPlanilla();
        return "redirect:/viewPlanilla";
    }

    @GetMapping("/viewPlanilla")
    public String verPlanilla(Model model){
        ArrayList<PagoEntity> pagos = pagoService.obtenerPagos();
        model.addAttribute("pagos", pagos);
        return "viewPlanilla";
    }
}
