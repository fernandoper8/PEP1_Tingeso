package com.milkstgo.milkStgo.controllers;

import com.milkstgo.milkStgo.entities.AcopioEntity;
import com.milkstgo.milkStgo.entities.DatosEntity;
import com.milkstgo.milkStgo.entities.PlanillaEntity;
import com.milkstgo.milkStgo.entities.ProveedorEntity;
import com.milkstgo.milkStgo.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;

@Controller
@RequestMapping
public class HomeController {
    @Autowired
    ProveedorService proveedorService;
    @Autowired
    PlanillaService planillaService;
    @Autowired
    AcopioService acopioService;
    @Autowired
    DatosService datosService;

    @GetMapping("/")
    public String main(Model model){
        ArrayList<ProveedorEntity> proveedores = proveedorService.obtenerProveedores();
        ArrayList<PlanillaEntity> planillas = planillaService.obtenerPlanillas();
        ArrayList<AcopioEntity> acopios = acopioService.obtenerData();
        ArrayList<DatosEntity> datos = datosService.obtenerData();
        model.addAttribute("proveedores", proveedores);
        model.addAttribute("planillas", planillas);
        model.addAttribute("acopios", acopios);
        model.addAttribute("datos", datos);
        return "main";
    }

}
