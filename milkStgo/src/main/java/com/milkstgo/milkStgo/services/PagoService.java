package com.milkstgo.milkStgo.services;

import com.milkstgo.milkStgo.entities.AcopioEntity;
import com.milkstgo.milkStgo.entities.DatosEntity;
import com.milkstgo.milkStgo.entities.PagoEntity;
import com.milkstgo.milkStgo.entities.ProveedorEntity;
import com.milkstgo.milkStgo.repositories.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;

@Service
public class PagoService {

    @Autowired
    PagoRepository pagoRepository;

    @Autowired
    ProveedorService proveedorService;
    @Autowired
    AcopioService acopioService;
    @Autowired
    DatosService datosService;

    public ArrayList<PagoEntity> obtenerPagos(){
        return (ArrayList<PagoEntity>) pagoRepository.findAll();
    }

    public PagoEntity obtenerPagosPorProveedor(String codigo){
        ArrayList<PagoEntity> pagos = obtenerPagos();
        PagoEntity pagosProveedor = new PagoEntity();
        for(PagoEntity pago : pagos){
            if(pago.getCodigo().equals(codigo) && pago.getComparado() == 0){
                return pago;
            }
        }
        pagosProveedor.setComparado(-1);
        return pagosProveedor;
    }

    public void crearPlanilla(){ 
        ArrayList<ProveedorEntity> proveedores = proveedorService.obtenerProveedores();
        for(ProveedorEntity proveedor : proveedores){
            ArrayList<AcopioEntity> acopiosProveedor = acopioService.obtenerAcopiosPorProveedor(proveedor.getCodigo());
            DatosEntity datosProveedor = datosService.obtenerDataPorProveedor(proveedor.getCodigo());
            PagoEntity pagoAnterior = obtenerPagosPorProveedor(proveedor.getCodigo());

            // infoEntregas = {totalKlsLeche, turno, pagoAcopio, bonoPorFrecuencia}
            ArrayList<Integer> infoEntregas = contarLeche(acopiosProveedor);
            infoEntregas = calcularAcopioFinal(infoEntregas, proveedor, datosProveedor, acopiosProveedor.size());

            // Datos del proveedor
            ArrayList <String> informacionProveedor = new ArrayList<String>();
            informacionProveedor.add(proveedor.getCodigo());
            informacionProveedor.add(proveedor.getNombre());
            
            // Datos relacionados a la leche
            ArrayList <Integer> datosLeche = new ArrayList<Integer>();
            datosLeche.add(infoEntregas.get(0));
            datosLeche.add(acopiosProveedor.size());
            datosLeche.add(infoEntregas.get(0)/acopiosProveedor.size());
            datosLeche.add(infoEntregas.get(3));

            // Datos relacionados a porcentajes de variacion
            ArrayList<Integer> datosVariacion = asignarVariaciones(infoEntregas, pagoAnterior, datosProveedor);

            // Datos relacionados a los pagos
            ArrayList<Integer> datosPago = calcularPagos(infoEntregas, datosVariacion);

            PagoEntity nuevoPago = new PagoEntity();
            nuevoPago.setComparado(0);
            nuevoPago = setInfoProveedor(nuevoPago, informacionProveedor);
            nuevoPago = setInfoLeche(nuevoPago, datosLeche);
            nuevoPago = setInfoVariacion(nuevoPago, datosVariacion);
            nuevoPago = setInfoPagos(nuevoPago, datosPago);
            nuevoPago.setFecha(new Date());

            guardarPago(nuevoPago);
        }
        acopioService.eliminarDatos();
        datosService.eliminarDatos();
    }

    // no hacer prueba unitaria
    public ArrayList<Integer> contarLeche(ArrayList<AcopioEntity> acopiosProveedor){
        ArrayList<Integer> datos = new ArrayList<Integer>();
        int klsLeche = 0;
        int turno;
        int turnoM = 0;
        int turnoT = 0;
        for(AcopioEntity acopio : acopiosProveedor){
            klsLeche = klsLeche + Integer.parseInt(acopio.getKls_leche());
            if(acopio.getTurno().equals("M")){
                turnoM = 1;
            }
            else if(acopio.getTurno().equals("T")){
                turnoT = 1;
            }
        }
        if(turnoM == 1 && turnoT == 1){
            turno = 3; // Ambos turnos
        }
        else if(turnoM == 1 && turnoT == 0){
            turno = 2; // Solo mañana
        }
        else if(turnoM == 0 && turnoT == 1){
            turno = 1; // Solo tarde
        }
        else{
            turno = 0;
        }
        datos.add(klsLeche);
        datos.add(turno);
        return datos;
    }

    // hacer prueba unitaria
    public double calcularVariacion(int datoAnterior, int datoActual){
        double porcentaje = (((double)datoActual - (double)datoAnterior) / (double)datoAnterior) * -100;
        return porcentaje;
    }
    
    // hacer prueba unitaria
    public int calcularVariacionPorcentaje(int porAnterior, int porActual){
        return porAnterior - porActual;
    }

    // hacer prueba unitaria
    public int calcularDescuentos(ArrayList<Integer> infoEntregas, PagoEntity pagoAnterior, DatosEntity datosProveedor){
        int descuentos = 0;
        descuentos += calcularDescuentoLeche(infoEntregas.get(2), infoEntregas.get(0), pagoAnterior);
        descuentos += calcularDescuentoGrasa(infoEntregas.get(2), datosProveedor.getPor_grasa(), pagoAnterior);
        descuentos += calcularDescuentoSolidos(infoEntregas.get(2), datosProveedor.getPor_solidos(), pagoAnterior);
        return descuentos;
    }

    // no hacer prueba unitaria
    public ArrayList<Integer> asignarVariaciones(ArrayList<Integer> infoEntregas, PagoEntity pagoAnterior, DatosEntity datosProveedor){
        ArrayList<Integer> resultado = new ArrayList<Integer>();
        if(pagoAnterior.getComparado() == 0){ // hay un pago anterior
            resultado = variacionesPagoAnterior(infoEntregas, pagoAnterior, datosProveedor); 
            pagoAnterior.setComparado(-1);
            actualizarPago(pagoAnterior);
        }else if(pagoAnterior.getComparado() == -1){ // no hay pago anterior
            resultado = variacionesNoPagoAnterior(datosProveedor);
        }
        return resultado;
    }

    // no hacer prueba unitaria
    public ArrayList<Integer> variacionesPagoAnterior(ArrayList<Integer> infoEntregas, PagoEntity pagoAnterior, DatosEntity datosProveedor){
        ArrayList<Integer> resultado = new ArrayList<Integer>();
        resultado.add( (int) calcularVariacion(pagoAnterior.getTotalKlsLeche(), infoEntregas.get(0)) ); // leche
        resultado.add(calcularDescuentoLeche(infoEntregas.get(2), infoEntregas.get(0), pagoAnterior)); 
        resultado.add(datosProveedor.getPor_grasa()); // grasa
        resultado.add(calcularVariacionPorcentaje(pagoAnterior.getPorGrasa(), datosProveedor.getPor_grasa())); 
        resultado.add(calcularDescuentoGrasa(infoEntregas.get(2), datosProveedor.getPor_grasa(), pagoAnterior)); 
        resultado.add(datosProveedor.getPor_solidos()); // solidos
        resultado.add(calcularVariacionPorcentaje(pagoAnterior.getPorSolidos(), datosProveedor.getPor_solidos())); 
        resultado.add(calcularDescuentoSolidos(infoEntregas.get(2), datosProveedor.getPor_solidos(), pagoAnterior)); 
        resultado.add(calcularDescuentos(infoEntregas, pagoAnterior, datosProveedor)); // descuentos
        return resultado;
    }

    // no hacer prueba unitaria
    public ArrayList<Integer> variacionesNoPagoAnterior(DatosEntity datosProveedor){
        ArrayList<Integer> resultado = new ArrayList<Integer>();
        
        resultado.add(0);
        resultado.add(0);
        resultado.add(datosProveedor.getPor_grasa());
        resultado.add(0);
        resultado.add(0);
        resultado.add(datosProveedor.getPor_solidos());
        resultado.add(0);
        resultado.add(0);
        resultado.add(0);
        return resultado;
    }

    // hacer prueba unitaria
    public int bonoFrecuencia(int turno, int pagoAcopio){
        double bono = 0;
        if(turno == 3){
            bono = 1.20;
        }
        else if(turno == 2){
            bono = 1.12;
        }
        else if(turno == 1){
            bono = 1.08;
        }
        return (int)(pagoAcopio * bono);
    }

    // no hacer prueba unitaria
    public ArrayList<Integer> calcularAcopioFinal(ArrayList<Integer> infoEntregas, ProveedorEntity proveedor, DatosEntity datosProveedor, int entregas){
        int pagoAcopio = pagoAcopio(infoEntregas.get(0), proveedor.getCategoria(), datosProveedor.getPor_grasa(), datosProveedor.getPor_solidos());
        int bono = 0;
        if(entregas > 10){
            bono = bonoFrecuencia(infoEntregas.get(1), pagoAcopio) - pagoAcopio;
        }
        infoEntregas.add(pagoAcopio+bono);
        infoEntregas.add(bono);
        return infoEntregas;
    }

    // no hacer prueba unitaria
    public ArrayList<Integer> calcularPagos(ArrayList<Integer> infoEntregas, ArrayList<Integer> datosVariacion){
        ArrayList<Integer> pagos = new ArrayList<Integer>();
        
        int montoPagoTotal = pagoTotal(infoEntregas.get(2), datosVariacion.get(8));
        int montoRetencionTotal = retencion(montoPagoTotal);
        int montoPagoFinal = pagoFinal(montoPagoTotal, montoRetencionTotal);

        //ArrayList <Integer> datosPago = new ArrayList<Integer>();
        pagos.add(montoPagoTotal);
        pagos.add(montoRetencionTotal);
        pagos.add(montoPagoFinal);
        return pagos;
    }

    // hacer prueba unitaria
    int pagoFinal(int pagoTotal, int retencion){
        int resultado = 0;
        resultado = pagoTotal - retencion;
        if(resultado < 0){
            resultado = 0;
        }
        return resultado;
    }

    // hacer prueba unitaria
    int pagoTotal(int pagoAcopio, int descuentos){
        int resultado = 0;
        resultado = pagoAcopio - descuentos;
        if(resultado < 0){
            resultado = 0;
        }
        return resultado;
    }

    // hacer prueba unitaria
    public int calcularDescuentoLeche(int pagoAcopio, int klsLecheActual, PagoEntity pagoAnterior){
        double porcentajeLeche = porcentajeVariacionLeche(pagoAnterior.getTotalKlsLeche(), klsLecheActual);
        return (int)(pagoAcopio * porcentajeLeche);
    }

    // hacer prueba unitaria
    public double porcentajeVariacionLeche(int klsLecheAnterior, int klsLecheActual){
        if(klsLecheAnterior-klsLecheActual <= 0) // variacion positiva o igual a 0
            return 0;
        double porcentaje = calcularVariacion(klsLecheAnterior, klsLecheActual);    
        //double porcentaje = (((double)klsLecheActual - (double)klsLecheAnterior) / (double)klsLecheAnterior) * -100;
        if(porcentaje >= 0 && porcentaje <= 8){
            return 0;
        }else if(porcentaje >= 9 && porcentaje <= 25){
            return 0.07;
        }else if(porcentaje >= 26 && porcentaje <= 45){
            return 0.15;
        }else if(porcentaje >= 46){
            return 0.30;
        }
        return 0;
    }

    // hacer prueba unitaria
    public int calcularDescuentoGrasa(int pagoAcopio, int porGrasa, PagoEntity pagoAnterior){
        double porcentajeGrasa = porcentajeVariacionGrasa(pagoAnterior.getPorGrasa(), porGrasa);
        return (int)(pagoAcopio * porcentajeGrasa);
    }

    // hacer prueba unitaria
    public double porcentajeVariacionGrasa(int porAnterior, int porActual){
        int porcentaje = calcularVariacionPorcentaje(porAnterior, porActual);
        if(porcentaje >= 0 && porcentaje <= 15){
            return 0;
        }else if(porcentaje >= 16 && porcentaje <= 25){
            return 0.12;
        }else if(porcentaje >= 26 && porcentaje <= 40){
            return 0.20;
        }else if(porcentaje >= 41){
            return 0.30;
        }
        return 0;
    }

    // hacer prueba unitaria
    public int calcularDescuentoSolidos(int pagoAcopio, int porSolidos, PagoEntity pagoAnterior){
        double porcentajeSolidos = porcentajeVariacionSolidos(pagoAnterior.getPorSolidos(), porSolidos);
        return (int) (pagoAcopio * porcentajeSolidos);
    }
    
    // hacer prueba unitaria
    public double porcentajeVariacionSolidos(int porAnterior, int porActual){
        int porcentaje = calcularVariacionPorcentaje(porAnterior, porActual);
        if(porcentaje >= 0 && porcentaje <= 6){
            return 0;
        }else if(porcentaje >= 7 && porcentaje <= 12){
            return 0.18;
        }else if(porcentaje >= 13 && porcentaje <= 35){
            return 0.27;
        }else if(porcentaje >= 36){
            return 0.45;
        }
        return 0;
    }

    // hacer prueba unitaria
    public int pagoAcopio(int klsLecheActual, String cat, int porGrasa, int porSolidos){
        int pagoCat = pagoPorCategoria(cat);
        int pagoGrasa = pagoPorGrasa(porGrasa);
        int pagoSolidos = pagoPorSolidos(porSolidos);
        return ((klsLecheActual * pagoCat) + (klsLecheActual * pagoGrasa) + (klsLecheActual * pagoSolidos));
    }
    
    // hacer prueba unitaria
    public int pagoPorCategoria(String categoria){
        int pago = 0;
        switch (categoria){
            case "A":
                pago = 700;
                break;
            case "B":
                pago = 550;
                break;
            case "C":
                pago = 400;
                break;
            case "D":
                pago = 250;
                break;
        }
        return pago;
    }

    // hacer prueba unitaria
    public int pagoPorGrasa(int porGrasa){
        int pago = 0;
        if(porGrasa >= 0 && porGrasa <= 20){
            pago = 30;
        }
        else if(porGrasa >= 21 && porGrasa <= 45){
            pago = 80;
        }
        else if(porGrasa >= 46){
            pago = 120;
        }
        return pago;
    }
    
    // hacer prueba unitaria
    public int pagoPorSolidos(int porSolidos){
        int pago = 0;
        if(porSolidos >= 0 && porSolidos <= 7){
            pago = -130;
        }
        else if(porSolidos >= 8 && porSolidos <= 18){
            pago = -90;
        }
        else if(porSolidos >= 19 && porSolidos <= 35){
            pago = 95;
        }
        else if(porSolidos >= 36){
            pago = 150;
        }
        return pago;
    }

    // hacer prueba unitaria
    public int retencion(int pagoTotal){
        int retencion = 0;
        if(pagoTotal > 950000)
            retencion = (int) (pagoTotal * 0.13);
        return retencion;
    }

    // hacer prueba unitaria
    public int calculoRetencion(int valor){
        if(valor > 950000){
            return (int) (valor * 0.13);
        }
        else{
            return 0;
        }
    }

    // no hacer prueba unitaria
    public void guardarPago(PagoEntity nuevoPago){
        pagoRepository.save(nuevoPago);
    }

    // no hacer prueba unitaria
    public void actualizarPago(PagoEntity pago){
        pagoRepository.save(pago);
    }

    // no hacer prueba unitaria
    public PagoEntity setInfoProveedor(PagoEntity pago, ArrayList<String> datosProveedor){
        pago.setCodigo(datosProveedor.get(0));
        pago.setNombre(datosProveedor.get(1));
        return pago;
    }

    // no hacer prueba unitaria
    public PagoEntity setInfoLeche(PagoEntity pago, ArrayList<Integer> datosLeche){
        pago.setTotalKlsLeche(datosLeche.get(0));
        pago.setFrecuencia(datosLeche.get(1));
        pago.setPromedioDiarioKls(datosLeche.get(2));
        pago.setBonoFrecuencia(datosLeche.get(3));
        return pago;
    }

    // no hacer prueba unitaria
    public PagoEntity setInfoVariacion(PagoEntity pago, ArrayList<Integer> datosVariacion){
        pago.setPorVariacionLeche(datosVariacion.get(0));
        pago.setDctoVariacionLeche(datosVariacion.get(1));
        pago.setPorGrasa(datosVariacion.get(2));
        pago.setPorVariacionGrasa(datosVariacion.get(3));
        pago.setDctoVariacionGrasa(datosVariacion.get(4));
        pago.setPorSolidos(datosVariacion.get(5));
        pago.setPorVariacionSolidos(datosVariacion.get(6));
        pago.setDctoVariacionSolidos(datosVariacion.get(7));
        return pago;
    }

    // no hacer prueba unitaria
    public PagoEntity setInfoPagos(PagoEntity pago, ArrayList<Integer> datosPago){
        pago.setPagoTotal(datosPago.get(0));
        pago.setMontoRetencion(datosPago.get(1));
        pago.setPagoFinal(datosPago.get(2));
        return pago;
    }
}
