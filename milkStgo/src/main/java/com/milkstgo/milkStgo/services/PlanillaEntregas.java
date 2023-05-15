package com.milkstgo.milkStgo.services;

import com.milkstgo.milkStgo.entities.AcopioEntity;
import com.milkstgo.milkStgo.entities.PlanillaEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

// Esta clase se encarga de llenar solamente los datos relacionados a las entregas
// en PlanillaEntity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PlanillaEntregas {
    private static final int ENTREGA_AMBOS_TURNOS = 3;
    private static final int ENTREGA_SOLO_MANANA = 2;
    private static final int ENTREGA_SOLO_TARDE = 1;
    private static final int SI_ENTREGA = 1;
    private static final int NO_ENTREGA = 0;

    private PlanillaEntity planilla;
    private int totalKlsLecheEntregados = 0;
    private int turnoManana = 0;
    private int turnoTarde = 0;
    private int queTurnos= 0;

    public PlanillaEntregas(PlanillaEntity planilla){
        this.planilla = planilla;
    }
    public void analizarAcopios(ArrayList<AcopioEntity> acopiosProveedor){
        int cantidadEntregas = acopiosProveedor.size();

        for(AcopioEntity acopio: acopiosProveedor){
            sumarLecheEntregada(acopio);
            analizarTurno(acopio);
        }
        queTurnosEntrega();
        setDatosPlanilla(cantidadEntregas);
    }
    public void sumarLecheEntregada(AcopioEntity acopio){
        int lecheEntregadaEnAcopio = Integer.parseInt(acopio.getKls_leche());
        totalKlsLecheEntregados += lecheEntregadaEnAcopio;
    }
    public void analizarTurno(AcopioEntity acopio){
        String turnoAcopio = acopio.getTurno();
        if(turnoAcopio.equals("M"))
            turnoManana = SI_ENTREGA;
        else if(turnoAcopio.equals("T"))
            turnoTarde = SI_ENTREGA;
    }
    public void queTurnosEntrega(){
        if(turnoManana == SI_ENTREGA && turnoTarde == SI_ENTREGA)
            queTurnos = ENTREGA_AMBOS_TURNOS;
        else if(turnoManana == SI_ENTREGA && turnoTarde == NO_ENTREGA)
            queTurnos = ENTREGA_SOLO_MANANA;
        else if(turnoManana == NO_ENTREGA && turnoTarde == SI_ENTREGA)
            queTurnos = ENTREGA_SOLO_TARDE;
    }
    public void setDatosPlanilla(int cantidadEntregas){
        planilla.setTotalKlsLeche(totalKlsLecheEntregados);
        planilla.setQueTurnos(queTurnos);
        planilla.setFrecuencia(cantidadEntregas);
        setPromedioKlsEntregados(cantidadEntregas);
    }
    public void setPromedioKlsEntregados(int cantidadEntregas){
        float promedioKlsEntregados = 0;
        int totalKlsLeche = planilla.getTotalKlsLeche();

        if(cantidadEntregas != 0)
            promedioKlsEntregados = (float) totalKlsLeche / cantidadEntregas;
        planilla.setPromedioDiarioKls(promedioKlsEntregados);
    }
}
