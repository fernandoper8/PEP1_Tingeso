package com.milkstgo.milkStgo.services;

import com.milkstgo.milkStgo.entities.AcopioEntity;
import com.milkstgo.milkStgo.entities.DatosEntity;
import com.milkstgo.milkStgo.entities.PlanillaEntity;
import com.milkstgo.milkStgo.entities.ProveedorEntity;
import com.milkstgo.milkStgo.repositories.PlanillaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;

@Service
public class PlanillaService {
    private static final int PLANILLA_ANTERIOR = 0;
    private PlanillaEntity planilla;

    @Autowired
    ProveedorService proveedorService;
    @Autowired
    AcopioService acopioService;
    @Autowired
    DatosService datosService;
    @Autowired
    PlanillaRepository planillaRepository;

    public PlanillaService(){
        this.planilla = new PlanillaEntity();
    }

    public void crearPlanilla(){
        ArrayList<ProveedorEntity> proveedoresExistentes;
        ArrayList<AcopioEntity> acopiosProveedor;
        DatosEntity datosProveedor;

        proveedoresExistentes = proveedorService.obtenerProveedores();
        for(ProveedorEntity proveedor: proveedoresExistentes){
            String codigoProveedor = proveedor.getCodigo();

            acopiosProveedor = acopioService.obtenerAcopiosPorProveedor(codigoProveedor);
            datosProveedor = datosService.obtenerDataPorProveedor(codigoProveedor);
            if(tieneAcopios(acopiosProveedor))
                crearPagoPorProveedor(proveedor, acopiosProveedor, datosProveedor);
            reiniciarPlanilla();
        }
        acopioService.eliminarAcopios();
        datosService.eliminarDatos();
    }

    public boolean tieneAcopios(ArrayList<AcopioEntity> acopios){
        return (acopios.size() != 0);
    }
    public void crearPagoPorProveedor(ProveedorEntity proveedor, ArrayList<AcopioEntity> acopios, DatosEntity datos){
        String codigoProveedor = proveedor.getCodigo();
        PlanillaEntity planillaAnterior = obtenerPlanillaAnteriorProveedor(codigoProveedor);
        setInfoProveedor(proveedor);
        setInfoEntregas(acopios);
        setInfoPorcentajes(datos);
        setInfoPagos();
        setInfoRestante();
        guardarPlanilla(this.planilla);
        actualizarPlanillaAnterior(planillaAnterior);
    }
    public void guardarPlanilla(PlanillaEntity planillaNueva){
        planillaRepository.save(planillaNueva);
    }
    public void actualizarPlanillaAnterior(PlanillaEntity planillaAnterior){
        if(esLaPlanillaAnterior(planillaAnterior)) {
            planillaAnterior.setComparado(-1);
            guardarPlanilla(planillaAnterior);
        }
    }
    public void setInfoRestante(){
        int valorNoComparado = 0;
        Date fechaActual = new Date();

        planilla.setComparado(valorNoComparado);
        planilla.setFecha(fechaActual);
    }
    public void setInfoPagos(){
        PlanillaEntity planillaAnterior = obtenerPlanillaAnteriorProveedor(this.planilla.getCodigo());
        PlanillaPagos planillaPagos = new PlanillaPagos(planilla, planillaAnterior);
        planillaPagos.analizarPagos();
        actualizarPlanilla(planillaPagos.getPlanilla());
    }
    public void setInfoPorcentajes(DatosEntity datosProveedor){
        PlanillaEntity planillaAnterior = obtenerPlanillaAnteriorProveedor(this.planilla.getCodigo());
        PlanillaPorcentajes planillaPorcentajes = new PlanillaPorcentajes(this.planilla, planillaAnterior, datosProveedor);
        planillaPorcentajes.analizarDatos();
        actualizarPlanilla(planillaPorcentajes.getPlanilla());
    }
    public void setInfoEntregas(ArrayList<AcopioEntity> acopiosProveedor){
        PlanillaEntregas planillaEntregas = new PlanillaEntregas(this.planilla);
        planillaEntregas.analizarAcopios(acopiosProveedor);
        actualizarPlanilla(planillaEntregas.getPlanilla());
    }
    public void setInfoProveedor(ProveedorEntity proveedor){
        planilla.setNombre(proveedor.getNombre());
        planilla.setCodigo(proveedor.getCodigo());
        planilla.setCategoria(proveedor.getCategoria());
    }
    public void reiniciarPlanilla(){
        this.planilla = new PlanillaEntity();
    }
    public void actualizarPlanilla(PlanillaEntity planillaActualizada){
        this.planilla = planillaActualizada;
    }

    public ArrayList<PlanillaEntity> obtenerPlanillas(){
        return (ArrayList<PlanillaEntity>) planillaRepository.findAll();
    }
    public ArrayList<PlanillaEntity> obtenerPlanillasPorProveedor(String codigoProveedor){
        return planillaRepository.obtenerPlanillasPorProveedor(codigoProveedor);
    }
    public PlanillaEntity obtenerPlanillaAnteriorProveedor(String codigoProveedor){
        ArrayList<PlanillaEntity> planillaAnteriorTemp = planillaRepository.obtenerPlanillaAnteriorProveedor(codigoProveedor);
        PlanillaEntity planillaAnterior;
        if(planillaAnteriorTemp.size() == 0) { // No hay planillas anteriores para el proveedor
            planillaAnterior = new PlanillaEntity();
            planillaAnterior.setComparado(-1); // Se crea una vacia, con el indicador de que no tiene pagos anteriores
        }else{
            planillaAnterior = planillaAnteriorTemp.get(0); // Se obtiene la planilla anterior. Tiene el indicador de que
        }                                                  // corresponde a la planilla anterior del proveedor
        return planillaAnterior;
    }
    public boolean esLaPlanillaAnterior(PlanillaEntity planilla){
        return (planilla.getComparado() == PLANILLA_ANTERIOR);
    }
}
