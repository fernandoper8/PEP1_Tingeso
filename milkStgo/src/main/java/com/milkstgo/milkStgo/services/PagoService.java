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
        // el proveedor no ha recibido un pago anteriormente
        pagosProveedor.setComparado(-1);
        return pagosProveedor;
    }


    public void crearPlanilla(){
        ArrayList<ProveedorEntity> proveedores = proveedorService.obtenerProveedores();
        ArrayList<AcopioEntity> acopios = acopioService.obtenerData();
        ArrayList<DatosEntity> datos = datosService.obtenerData();
        int pagoAcopio = 0;
        int klsLeche = 0;
        float bonoFrecuencia = 0;
        int bonoFreq = 0;
        int turnoM = 0;
        int turnoT = 0;
        int descuentos = 0;
        int montoPagoTotal = 0;
        int montoRetencionTotal = 0;
        int montoPagoFinal = 0;
        int porcentajeVariacionLeche = 0;
        int porcentajeVariacionGrasa = 0;
        int porcentajeVariacionSolidos = 0;

        for(ProveedorEntity proveedor : proveedores){
            ArrayList<AcopioEntity> acopiosProveedor = acopioService.obtenerAcopiosPorProveedor(proveedor.getCodigo());
            DatosEntity datosProveedor = datosService.obtenerDataPorProveedor(proveedor.getCodigo());
            PagoEntity pagoAnterior = obtenerPagosPorProveedor(proveedor.getCodigo());
            for(AcopioEntity acopio : acopiosProveedor){
                klsLeche = klsLeche + Integer.parseInt(acopio.getKls_leche());
                if(acopio.getTurno().equals("M")){
                    turnoM = 1;
                }
                else if(acopio.getTurno().equals("T")){
                    turnoT = 1;
                }
            }

            pagoAcopio = pagoAcopio(klsLeche, proveedor.getCategoria(), datosProveedor.getPor_grasa(), datosProveedor.getPor_solidos());
            if(acopiosProveedor.size() > 10){
                pagoAcopio = bonoFrecuencia(turnoM, turnoT, pagoAcopio);
            }

            // Datos del proveedor
            ArrayList <String> informacionProveedor = new ArrayList<String>();
            informacionProveedor.add(proveedor.getCodigo());
            informacionProveedor.add(proveedor.getNombre());
            
            // Datos relacionados a la leche
            ArrayList <Integer> datosLeche = new ArrayList<Integer>();
            datosLeche.add(klsLeche);
            datosLeche.add(acopiosProveedor.size());
            datosLeche.add(klsLeche/acopiosProveedor.size());
            datosLeche.add(bonoFreq);

            // Datos relacionados a porcentajes de variacion
            ArrayList <Integer> datosVariacion = new ArrayList<Integer>();
            if(pagoAnterior.getComparado() == 0){
                porcentajeVariacionLeche = (int) ((porcentajeVariacionLeche(pagoAnterior.getTotalKlsLeche(), klsLeche))*100);
                porcentajeVariacionGrasa = (int) ((porcentajeVariacionGrasa(pagoAnterior.getPorGrasa(), datosProveedor.getPor_grasa())*100)); //ARREGLAR
                porcentajeVariacionSolidos = (int) ((porcentajeVariacionSolidos(pagoAnterior.getPorSolidos(), datosProveedor.getPor_solidos())*100));
                datosVariacion.add(porcentajeVariacionLeche); // porcentaje leche
                datosVariacion.add(calcularDescuentoLeche(pagoAcopio, klsLeche, pagoAnterior)); // descuento leche
                datosVariacion.add(datosProveedor.getPor_grasa());
                datosVariacion.add(porcentajeVariacionGrasa); // porcentaje variacion grasa
                datosVariacion.add(calcularDescuentoGrasa(pagoAcopio, datosProveedor.getPor_grasa(), pagoAnterior)); // descuento grasa
                datosVariacion.add(datosProveedor.getPor_solidos());
                datosVariacion.add(porcentajeVariacionSolidos); // porcentaje variacion solidos
                datosVariacion.add(calcularDescuentoSolidos(pagoAcopio, datosProveedor.getPor_solidos(), pagoAnterior)); // descuento solidos
                descuentos = calcularDescuentos(pagoAcopio, calcularDescuentoLeche(pagoAcopio, klsLeche, pagoAnterior),
                                    calcularDescuentoGrasa(pagoAcopio, datosProveedor.getPor_grasa(), pagoAnterior),
                                    calcularDescuentoSolidos(pagoAcopio, datosProveedor.getPor_solidos(), pagoAnterior));
                pagoAnterior.setComparado(-1);
                actualizarPago(pagoAnterior);
            }else if(pagoAnterior.getComparado() == -1){ // No hay pagos anteriores, los datos de variacion cambian
                datosVariacion.add(0);
                datosVariacion.add(0);
                datosVariacion.add(datosProveedor.getPor_grasa());
                datosVariacion.add(0);
                datosVariacion.add(0);
                datosVariacion.add(datosProveedor.getPor_solidos());
                datosVariacion.add(0);
                datosVariacion.add(0);
            }

            // Datos relacionados a los pagos
            montoPagoTotal = pagoTotal(pagoAcopio, descuentos);
            montoRetencionTotal = retencion(montoPagoTotal);
            montoPagoFinal = pagoFinal(montoPagoTotal, montoRetencionTotal);

            ArrayList <Integer> datosPago = new ArrayList<Integer>();
            datosPago.add(montoPagoTotal);
            datosPago.add(montoRetencionTotal);
            datosPago.add(montoPagoFinal);

            PagoEntity nuevoPago = new PagoEntity();
            nuevoPago.setComparado(0);
            nuevoPago = setInfoProveedor(nuevoPago, informacionProveedor);
            nuevoPago = setInfoLeche(nuevoPago, datosLeche);
            nuevoPago = setInfoVariacion(nuevoPago, datosVariacion);
            nuevoPago = setInfoPagos(nuevoPago, datosPago);
            nuevoPago.setFecha(new Date());
            guardarPago(nuevoPago);

            // IMPRIMIR NOMBRE Y PAGOACOPIO
            System.out.println("Nombre: " + proveedor.getNombre());
            System.out.println("Pago Acopio: " + pagoAcopio);

            // reinicio variables para el resto de proveedores
            klsLeche = 0;
            turnoM = 0;
            turnoT = 0;
            montoRetencionTotal = 0;
            descuentos = 0;
        }
        acopioService.eliminarDatos();
        datosService.eliminarDatos();
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
    int calcularDescuentos(int pagoAcopio, int dctoLeche, int dctoGrasa, int dctoSolidos){
        int descuentos = 0;
        System.out.println("Pago acopio: " + pagoAcopio + " dcto leche: " + dctoLeche + " dcto grasa: " + dctoGrasa + " dcto solidos: " + dctoSolidos);
        descuentos += dctoLeche;
        descuentos += dctoGrasa;
        descuentos += dctoSolidos;
        System.out.println("Descuentos: " + descuentos);
        return descuentos;
    }

    // hacer prueba unitaria
    public int bonoFrecuencia(int turnoM, int turnoT, int pagoAcopio){
        double bono = 0;
        if(turnoM == 1 && turnoT == 1){
            bono = 1.20;
        }
        else if(turnoM == 1 && turnoT == 0){
            bono = 1.12;
        }
        else if(turnoM == 0 && turnoT == 1){
            bono = 1.08;
        }
        return (int)(pagoAcopio * bono);
    }

    // hacer prueba unitaria
    public int calcularDescuentoLeche(int pagoAcopio, int klsLecheActual, PagoEntity pagoAnterior){
        double porcentajeLeche = porcentajeVariacionLeche(pagoAnterior.getTotalKlsLeche(), klsLecheActual);
        System.out.println("Porcentaje leche: " + porcentajeLeche + "%" + "PagoAcopio: " + pagoAcopio);
        System.out.println("Dcto leche: " + pagoAcopio * porcentajeLeche);
        return (int)(pagoAcopio * porcentajeLeche);
    }

    // hacer prueba unitaria
    public double porcentajeVariacionLeche(int klsLecheAnterior, int klsLecheActual){
        int porcentaje = Math.round((klsLecheActual * 100)/klsLecheAnterior);
        System.out.println("Porcentaje variacion leche: " + porcentaje);
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
        System.out.println("Dcto grasa: " + pagoAcopio * porcentajeGrasa);
        return (int)(pagoAcopio * porcentajeGrasa);
    }

    // hacer prueba unitaria
    public double porcentajeVariacionGrasa(int porAnterior, int porActual){
        int porcentaje = porAnterior - porActual;
        System.out.println("Porcentaje variacion grasa: " + porcentaje);
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
        System.out.println("Dcto solidos: " + pagoAcopio * porcentajeSolidos);
        return (int) (pagoAcopio * porcentajeSolidos);
    }
    
    // hacer prueba unitaria
    public double porcentajeVariacionSolidos(int porAnterior, int porActual){
        int porcentaje = porAnterior - porActual;
        System.out.println("Porcentaje variacion solidos: " + porcentaje);
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
        System.out.println(((klsLecheActual * pagoCat) + (klsLecheActual * pagoGrasa) + (klsLecheActual * pagoSolidos)));
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

    // Recibe un vector con valores a los que calcular la retencion mediante un ciclo
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

    public void guardarPago(PagoEntity nuevoPago){
        pagoRepository.save(nuevoPago);
    }

    public void actualizarPago(PagoEntity pago){
        pagoRepository.save(pago);
    }

    public PagoEntity setInfoProveedor(PagoEntity pago, ArrayList<String> datosProveedor){
        pago.setCodigo(datosProveedor.get(0));
        pago.setNombre(datosProveedor.get(1));
        return pago;
    }

    public PagoEntity setInfoLeche(PagoEntity pago, ArrayList<Integer> datosLeche){
        pago.setTotalKlsLeche(datosLeche.get(0));
        pago.setFrecuencia(datosLeche.get(1));
        pago.setPromedioDiarioKls(datosLeche.get(2));
        pago.setBonoFrecuencia(datosLeche.get(3));
        return pago;
    }

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

    public PagoEntity setInfoPagos(PagoEntity pago, ArrayList<Integer> datosPago){
        pago.setPagoTotal(datosPago.get(0));
        pago.setMontoRetencion(datosPago.get(1));
        pago.setPagoFinal(datosPago.get(2));
        return pago;
    }

}
