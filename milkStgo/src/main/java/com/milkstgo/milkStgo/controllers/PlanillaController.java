package com.milkstgo.milkStgo.controllers;

import com.milkstgo.milkStgo.entities.PlanillaEntity;
import com.milkstgo.milkStgo.services.PlanillaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;

@Controller
@RequestMapping
public class PlanillaController {

    @Autowired
    PlanillaService planillaService;

    @PostMapping("/crearPlanilla")
    public String calcularPlanilla(){
        //revisar
        planillaService.crearPlanilla();
        return "redirect:/";
    }

    @GetMapping("/planilla")
    public String verPlanilla(Model model){
        ArrayList<PlanillaEntity> planillas = planillaService.obtenerPlanillas();
        model.addAttribute("planillas", planillas);
        return "planilla";
    }
}
