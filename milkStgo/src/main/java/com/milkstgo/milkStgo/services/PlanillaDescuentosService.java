package com.milkstgo.milkStgo.services;

import com.milkstgo.milkStgo.entities.PlanillaEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@NoArgsConstructor
@Getter
@Setter
@Service
public class PlanillaDescuentosService {
    private static final double VARIACION_INSIGNIFICANTE = 0;
    private static final double DCTO_VARIACION_LECHE_BAJA = 0.07;
    private static final double DCTO_VARIACION_LECHE_MEDIA = 0.15;
    private static final double DCTO_VARIACION_LECHE_ALTA = 0.30;
    private static final double DCTO_VARIACION_GRASA_BAJA = 0.12;
    private static final double DCTO_VARIACION_GRASA_MEDIA = 0.20;
    private static final double DCTO_VARIACION_GRASA_ALTA = 0.30;
    private static final double DCTO_VARIACION_SOLIDOS_BAJA = 0.18;
    private static final double DCTO_VARIACION_SOLIDOS_MEDIA = 0.27;
    private static final double DCTO_VARIACION_SOLIDOS_ALTA = 0.45;

    //@Autowired
    //PlanillaService planillaService;
    private PlanillaEntity planilla;
    private PlanillaEntity planillaAnterior;

    public PlanillaDescuentosService(PlanillaEntity planilla, PlanillaEntity planillaAnterior){
        this.planilla = planilla;
        this.planillaAnterior = planillaAnterior;
    }
    public void analizarDescuentos(){
        setDescuentos();
    }

    public void setDescuentos(){
        PlanillaService planillaService = new PlanillaService();
        if(planillaService.esLaPlanillaAnterior(planillaAnterior))
            obtenerDescuentosConPlanillaAnterior();
        else
            setDescuentosSinPagoAnterior();
    }
    public void obtenerDescuentosConPlanillaAnterior(){
        int montoDctoPorLeche = calcularDescuentoPorLeche();
        int montoDctoPorGrasa = calcularDescuentoPorGrasa();
        int montoDctoPorSolidos = calcularDescuentoPorSolidos();

        planilla.setDctoVariacionLeche(montoDctoPorLeche);
        planilla.setDctoVariacionGrasa(montoDctoPorGrasa);
        planilla.setDctoVariacionSolidos(montoDctoPorSolidos);
    }
    public void setDescuentosSinPagoAnterior(){
        planilla.setDctoVariacionLeche(0);
        planilla.setDctoVariacionGrasa(0);
        planilla.setDctoVariacionSolidos(0);
    }
    public int calcularDescuentoPorLeche(){
        double porcentajeDescuento = descuentoPorLeche();
        return (int) (planilla.getPagoAcopio() * porcentajeDescuento);
    }
    public int calcularDescuentoPorGrasa(){
        double porcentajeDescuento = descuentoPorGrasa();
        return (int) (planilla.getPagoAcopio() * porcentajeDescuento);
    }
    public int calcularDescuentoPorSolidos(){
        double porcentajeDescuento = descuentoPorSolidos();
        return (int) (planilla.getPagoAcopio() * porcentajeDescuento);
    }
    public double descuentoPorLeche(){
        float porcentajeVariacion = planilla.getPorVariacionLeche();
        if(porcentajeVariacion >= 0 && porcentajeVariacion <= 8)
            return VARIACION_INSIGNIFICANTE;
        else if(porcentajeVariacion >= 9 && porcentajeVariacion <= 25)
            return DCTO_VARIACION_LECHE_BAJA;
        else if(porcentajeVariacion >= 26 && porcentajeVariacion <= 45)
            return DCTO_VARIACION_LECHE_MEDIA;
        else if(porcentajeVariacion >= 46)
            return DCTO_VARIACION_LECHE_ALTA;
        else
            return 0;
    }
    public double descuentoPorGrasa(){
        int porcentajeVariacion = planilla.getPorVariacionGrasa();
        if(porcentajeVariacion >= 0 && porcentajeVariacion <= 15)
            return VARIACION_INSIGNIFICANTE;
        else if(porcentajeVariacion >= 16 && porcentajeVariacion <= 25)
            return DCTO_VARIACION_GRASA_BAJA;
        else if(porcentajeVariacion >= 26 && porcentajeVariacion <= 40)
            return DCTO_VARIACION_GRASA_MEDIA;
        else if(porcentajeVariacion >= 41)
            return DCTO_VARIACION_GRASA_ALTA;
        else
            return 0;
    }
    public double descuentoPorSolidos(){
        int porcentajeVariacion = planilla.getPorVariacionSolidos();
        if(porcentajeVariacion >= 0 && porcentajeVariacion <= 6)
            return VARIACION_INSIGNIFICANTE;
        else if(porcentajeVariacion >= 7 && porcentajeVariacion <= 12)
            return DCTO_VARIACION_SOLIDOS_BAJA;
        else if(porcentajeVariacion >= 13 && porcentajeVariacion <= 35)
            return DCTO_VARIACION_SOLIDOS_MEDIA;
        else if(porcentajeVariacion >= 36)
            return DCTO_VARIACION_SOLIDOS_ALTA;
        else
            return 0;
    }
}
