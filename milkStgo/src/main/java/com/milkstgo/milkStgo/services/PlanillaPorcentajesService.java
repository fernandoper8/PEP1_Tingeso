package com.milkstgo.milkStgo.services;

import com.milkstgo.milkStgo.entities.DatosEntity;
import com.milkstgo.milkStgo.entities.PlanillaEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@NoArgsConstructor
@Getter
@Service
public class PlanillaPorcentajesService {
    private PlanillaEntity planilla;
    private PlanillaEntity planillaAnterior;
    private DatosEntity datosProveedor;

    public PlanillaPorcentajesService(PlanillaEntity planilla, PlanillaEntity planillaAnterior, DatosEntity datosProveedor){
        this.planilla = planilla;
        this.planillaAnterior = planillaAnterior;
        this.datosProveedor = datosProveedor;
    }
    public void analizarDatos(){
        setCaracteristicas();
        setVariaciones();
    }
    public void setCaracteristicas(){
        int porcentajeGrasa = this.datosProveedor.getPor_grasa();
        int porcentajeSolidos = this.datosProveedor.getPor_solidos();
        planilla.setPorGrasa(porcentajeGrasa);
        planilla.setPorSolidos(porcentajeSolidos);
    }
    public void setVariaciones(){
        PlanillaService planillaService = new PlanillaService();
        if(planillaService.esLaPlanillaAnterior(planillaAnterior))
            obtenerVariacionesConPagoAnterior();
        else
            setVariacionesSinPagoAnterior();
    }
    public void obtenerVariacionesConPagoAnterior(){
        float porcentajeVariacionLeche = calculoVariacionLeche();
        int porcentajeVariacionGrasa = calculoVariacionGrasa();
        int porcentajeVariacionSolidos = calculoVariacionSolidos();
        setPorcentajeVariaciones(porcentajeVariacionLeche, porcentajeVariacionGrasa, porcentajeVariacionSolidos);
    }
    public int calculoVariacionGrasa(){
        int porcentajeGrasaPlanillaAnterior = planillaAnterior.getPorGrasa();
        int porcentajeGrasaPlanillaActual = planilla.getPorGrasa();
        return calculoVariacionDeUnPorcentaje(porcentajeGrasaPlanillaAnterior, porcentajeGrasaPlanillaActual);
    }
    public int calculoVariacionSolidos(){
        int porcentajeSolidosPlanillaAnterior = planillaAnterior.getPorSolidos();
        int porcentajeSolidosPlanillaActual = planilla.getPorSolidos();
        return calculoVariacionDeUnPorcentaje(porcentajeSolidosPlanillaAnterior, porcentajeSolidosPlanillaActual);
    }
    public float calculoVariacionLeche(){
        int klsLechePlanillaAnterior = planillaAnterior.getTotalKlsLeche();
        int klsLechePlanillaActual = planilla.getTotalKlsLeche();
        return (((float)klsLechePlanillaActual - (float)klsLechePlanillaAnterior) / klsLechePlanillaAnterior)*-100;
    }
    public int calculoVariacionDeUnPorcentaje(int porcentajeAnterior, int porcentajeNuevo){
        return porcentajeAnterior - porcentajeNuevo;
    }
    public void setPorcentajeVariaciones(float porcentajeVariacionLeche, int porcentajeVariacionGrasa, int porcentajeVariacionSolidos){
        planilla.setPorVariacionLeche(porcentajeVariacionLeche);
        planilla.setPorVariacionGrasa(porcentajeVariacionGrasa);
        planilla.setPorVariacionSolidos(porcentajeVariacionSolidos);
    }
    public void setVariacionesSinPagoAnterior(){
        planilla.setPorVariacionLeche(0);
        planilla.setPorVariacionGrasa(0);
        planilla.setPorVariacionSolidos(0);
    }
}
