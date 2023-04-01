package com.milkstgo.milkStgo.controllers;

import com.milkstgo.milkStgo.entities.ProveedorEntity;
import com.milkstgo.milkStgo.services.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

@Controller
@RequestMapping
public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;
    @GetMapping("/addProveedor")
    public String addProveedor(){return "addProveedor";}

    @PostMapping("/addProveedor")
    public String nuevoProveedor(@RequestParam("nombre") String nombre,
                                    @RequestParam("codigo") String codigo,
                                    @RequestParam("retencion") String retencion,
                                    @RequestParam("categoria") String categoria){
        proveedorService.guardarProveedor(nombre,codigo,retencion,categoria);
        return "redirect:/addProveedor";
    }

    @GetMapping("/viewProveedor")
    public String viewProveedor(Model model){
        ArrayList<ProveedorEntity> proveedores = proveedorService.obtenerProveedores();
        // if length(proveedores) == 0
        //  hacer algo para mostrar en la pantalla que no hay lol
        model.addAttribute("proveedores", proveedores);
        return "viewProveedor";
    }

}
