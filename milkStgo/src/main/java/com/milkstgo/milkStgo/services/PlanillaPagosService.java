package com.milkstgo.milkStgo.services;

import com.milkstgo.milkStgo.entities.PlanillaEntity;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@NoArgsConstructor
@Getter
@Setter
@Service
public class PlanillaPagosService {
    private static final int PAGO_PORCENTAJE_INICIAL = 0;
    private static final int FRECUENCIA_MINIMA_BONO = 10;
    private static final int ENTREGA_AMBOS_TURNOS = 3;
    private static final int ENTREGA_SOLO_MANANA = 2;
    private static final int ENTREGA_SOLO_TARDE = 1;
    private static final double MULTIPLICADOR_AMBOS_TURNOS = 1.20;
    private static final double MULTIPLICADOR_TURNO_MANANA = 1.12;
    private static final double MULTIPLICADOR_TURNO_TARDE = 1.08;
    private static final int PAGO_CATEGORIA_A = 700;
    private static final int PAGO_CATEGORIA_B = 550;
    private static final int PAGO_CATEGORIA_C = 400;
    private static final int PAGO_CATEGORIA_D = 250;
    private static final int PAGO_GRASA_BAJA = 30;
    private static final int PAGO_GRASA_MEDIA = 80;
    private static final int PAGO_GRASA_ALTA = 120;
    private static final int PAGO_SOLIDOS_MUY_BAJOS = -130;
    private static final int PAGO_SOLIDOS_BAJOS = -90;
    private static final int PAGO_SOLIDOS_NORMALES = 95;
    private static final int PAGO_SOLIDOS_ALTOS = 150;
    private static final double MONTO_MAXIMO_RETENCION = 950000;
    private static final double RETENCION = 0.13;

    @Autowired
    PlanillaDescuentosService planillaDescuentosService;

    private PlanillaEntity planilla;
    private PlanillaEntity planillaAnterior;

    public PlanillaPagosService(PlanillaEntity planilla, PlanillaEntity planillaAnterior) {
        this.planilla = planilla;
        this.planillaAnterior = planillaAnterior;
    }

    public void analizarPagos() {
        calcularPagoAcopio();
        setInfoDescuentos();
        setPagoTotal();
        setMontoRetencion();
        setPagoFinal();
    }
    public void setPagoFinal(){
        int pagoFinal = calculoPagoFinal();
        planilla.setPagoFinal(pagoFinal);
    }
    public int calculoPagoFinal(){
        int pagoTotal = planilla.getPagoTotal();
        int montoRetencion = planilla.getMontoRetencion();
        return pagoTotal - montoRetencion;
    }
    public void setMontoRetencion() {
        int montoRetencion = 0;
        int pagoTotal = planilla.getPagoTotal();
        if(pagoTotal > MONTO_MAXIMO_RETENCION)
            montoRetencion = calculoMontoRetencion();
        planilla.setMontoRetencion(montoRetencion);
    }
    public int calculoMontoRetencion(){
        int pagoTotal = planilla.getPagoTotal();
        return (int)(pagoTotal * RETENCION);
    }
    public void setPagoTotal(){
        int pagoTotal = calculoPagoTotal();
        planilla.setPagoTotal(pagoTotal);
    }
    public int calculoPagoTotal(){
        int pagoAcopio = planilla.getPagoAcopio();
        int montoDctoLeche = planilla.getDctoVariacionLeche();
        int montoDctoGrasa = planilla.getDctoVariacionGrasa();
        int montoDctoSolidos = planilla.getDctoVariacionSolidos();
        int resultado = pagoAcopio - montoDctoLeche - montoDctoGrasa - montoDctoSolidos;
        if(resultado < 0)
            resultado = 0;
        return resultado;
    }
    @Generated // Metodos ya testeados
    public void setInfoDescuentos(){
        planillaDescuentosService = new PlanillaDescuentosService(this.planilla, this.planillaAnterior);
        planillaDescuentosService.analizarDescuentos();
        actualizarPlanilla(planillaDescuentosService.getPlanilla());
    }
    public void calcularPagoAcopio(){
        int klsLecheTotales = planilla.getTotalKlsLeche();
        int montoPorCategoria = pagoPorCategoria();
        int montoPorGrasa = pagoPorGrasa();
        int montoPorSolido = pagoPorSolidos();

        int pagoAcopio = klsLecheTotales*montoPorCategoria +
                            klsLecheTotales*montoPorGrasa +
                            klsLecheTotales*montoPorSolido;
        calcularBonoFrecuencia(pagoAcopio);
        pagoAcopio += planilla.getBonoFrecuencia();
        setPagoAcopio(pagoAcopio);
    }
    public void setPagoAcopio(int pagoAcopio){
        planilla.setPagoAcopio(pagoAcopio);
    }
    public void setBonoFrecuencia(int bonoFrecuencia){
        planilla.setBonoFrecuencia(bonoFrecuencia);
    }
    public void calcularBonoFrecuencia(int pagoPorAcopio){
        double multiplicadorFrecuencia = multiplicadorBonoFrecuencia();
        int bono = 0;
        if(planilla.getFrecuencia() > 10)
            bono = (int)((pagoPorAcopio*multiplicadorFrecuencia)-pagoPorAcopio);
        setBonoFrecuencia(bono);
    }
    public double multiplicadorBonoFrecuencia(){
        int queTurnos = planilla.getQueTurnos();
        return switch (queTurnos) {
            case ENTREGA_AMBOS_TURNOS -> MULTIPLICADOR_AMBOS_TURNOS;
            case ENTREGA_SOLO_MANANA -> MULTIPLICADOR_TURNO_MANANA;
            case ENTREGA_SOLO_TARDE -> MULTIPLICADOR_TURNO_TARDE;
            default -> 0;
        };
    }
    public int pagoPorCategoria(){
        String categoriaProveedor = planilla.getCategoria();
        return switch (categoriaProveedor) {
            case "A" -> PAGO_CATEGORIA_A;
            case "B" -> PAGO_CATEGORIA_B;
            case "C" -> PAGO_CATEGORIA_C;
            case "D" -> PAGO_CATEGORIA_D;
            default -> 0;
        };
    }
    public int pagoPorGrasa(){
        int pagoCorrespondiente;
        int porcentajeGrasa = planilla.getPorGrasa();

        if(porcentajeGrasa >= 0 && porcentajeGrasa <= 20)
            pagoCorrespondiente = PAGO_GRASA_BAJA;
        else if(porcentajeGrasa >= 21 && porcentajeGrasa <= 45)
            pagoCorrespondiente = PAGO_GRASA_MEDIA;
        else if(porcentajeGrasa >= 46)
            pagoCorrespondiente = PAGO_GRASA_ALTA;
        else
            pagoCorrespondiente = PAGO_PORCENTAJE_INICIAL;
        return pagoCorrespondiente;
    }
    public int pagoPorSolidos(){
        int pagoCorrespondiente;
        int porcentajeSolidos = planilla.getPorSolidos();

        if(porcentajeSolidos >= 0 && porcentajeSolidos <= 7)
            pagoCorrespondiente = PAGO_SOLIDOS_MUY_BAJOS;
        else if(porcentajeSolidos >= 8 && porcentajeSolidos <= 18)
            pagoCorrespondiente = PAGO_SOLIDOS_BAJOS;
        else if(porcentajeSolidos >= 19 && porcentajeSolidos <= 35)
            pagoCorrespondiente = PAGO_SOLIDOS_NORMALES;
        else if(porcentajeSolidos >= 36)
            pagoCorrespondiente = PAGO_SOLIDOS_ALTOS;
        else
            pagoCorrespondiente = PAGO_PORCENTAJE_INICIAL;
        return pagoCorrespondiente;
    }
    public void actualizarPlanilla(PlanillaEntity planillaActualizada){
        this.planilla = planillaActualizada;
    }
}
