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

    // Busca entidades de PagoEntity que tengan el mismo codigo de proveedor
    // y la variable de comparado = 0 mediante un ciclo for
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
        int bono_frecuencia = 0;
        int turnoM = 0;
        int turnoT = 0;
        int retencionTotal = 0;
        int descuentos = 0;

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
                                                  // pasar estos datos en un arraylist
            pagoAcopio = pagoAcopioLeche(klsLeche, proveedor.getCategoria(), datosProveedor.getPor_grasa(), datosProveedor.getPor_solidos());
            if(acopiosProveedor.size() > 10){
                bono_frecuencia = bonoFrecuencia(turnoM, turnoT, pagoAcopio);
                pagoAcopio += bono_frecuencia;
            }
            // si hay pago anterior
            if(pagoAnterior.getComparado() == 0){                                   // pasar estos datos en un arraylist
                descuentos = calcularDescuentos(pagoAnterior, pagoAcopio, klsLeche, datosProveedor.getPor_grasa(), datosProveedor.getPor_solidos());
            }
                                                                    
            // Datos del proveedor
            ArrayList <String> informacionProveedor = new ArrayList<String>();
            informacionProveedor.add(proveedor.getCodigo());
            informacionProveedor.add(proveedor.getNombre());
            
            // Datos relacionados a la leche
            // totalKlsLeche, frecuencia, promedioDiario y bonofrecuencia
            ArrayList <Integer> datosLeche = new ArrayList<Integer>();
            datosLeche.add(klsLeche);
            datosLeche.add(acopiosProveedor.size());
            datosLeche.add(klsLeche/acopiosProveedor.size());
            datosLeche.add(bono_frecuencia);

            // Datos relacionados a porcentajes de variacion
            // porcentajeVariacionLeche -
            // dctoVariacionLeche -
            // porcentajeGrasa, porcentajeVariacionGrasa -
            // descuentoVariacionGrasa -
            // porcentajeSolidos, porcentajeVariacionSolidos -
            // descuentoVariacionSolidos -
            System.out.println("Nombre: " + proveedor.getNombre());
            ArrayList <Integer> datosVariacion = new ArrayList<Integer>();
            if(pagoAnterior.getComparado() == 0){
                datosVariacion.add(descuentoVariacionNegKls(pagoAnterior.getTotalKlsLeche(), klsLeche, pagoAcopio));
                System.out.println("totalKlsLeche anterior: " + pagoAnterior.getTotalKlsLeche());
                System.out.println("klsLeche: " + klsLeche);
                System.out.println("porcentajeVariacionLeche: " + datosVariacion.get(0));

                datosVariacion.add(descuentoVariacionNegKls(pagoAnterior.getTotalKlsLeche(), klsLeche, pagoAcopio));
                System.out.println("dctoVariacionLeche: " + datosVariacion.get(1));
                datosVariacion.add(datosProveedor.getPor_grasa());
                datosVariacion.add((int) ((pagoAnterior.getPorGrasa() - datosProveedor.getPor_grasa()) * 100 / pagoAnterior.getPorGrasa()));
                System.out.println("porcentajeVariacionGrasa: " + datosVariacion.get(2));
                datosVariacion.add(descuentoVariacionNegGrasa(pagoAnterior.getPorGrasa(), datosProveedor.getPor_grasa(), pagoAcopio));
                System.out.println("descuentoVariacionGrasa: " + datosVariacion.get(3));
                datosVariacion.add(datosProveedor.getPor_solidos());
                datosVariacion.add((int) ((pagoAnterior.getPorSolidos() - datosProveedor.getPor_solidos()) * 100 / pagoAnterior.getPorSolidos()));
                datosVariacion.add(descuentoVariacionNegSolidos(pagoAnterior.getPorSolidos(), datosProveedor.getPor_solidos(), pagoAcopio));

                // el pago anterior fue usado para comparar con el actual
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
            System.out.println("------------------------");
            // Datos relacionados a los pagos
            // pagoTotal, montoRetencion, pagoFinal
            ArrayList <Integer> datosPago = new ArrayList<Integer>();
            
            //System.out.println("Pago Acopio: " + pagoAcopio);
            //System.out.println("Pago Total: " + pagoTotal(pagoAcopio, descuentos));
            
            datosPago.add(pagoTotal(pagoAcopio, descuentos));

            retencionTotal = retencion(pagoTotal(pagoAcopio, descuentos), pagoAcopio);
            //System.out.println("Pago Final: " + pagoFinal(pagoTotal(pagoAcopio, descuentos), retencionTotal));
            //System.out.println("Descuentos: " + descuentos);
            //System.out.println("Retencion: " + retencionTotal);
            datosPago.add(retencionTotal);
            datosPago.add(pagoFinal(pagoTotal(pagoAcopio, descuentos), retencionTotal));
            // Comparado = 0 significa que el nuevo pago no ha sido comparado con otro

            PagoEntity nuevoPago = new PagoEntity();
            nuevoPago.setComparado(0);
            nuevoPago = setInfoProveedor(nuevoPago, informacionProveedor);
            nuevoPago = setInfoLeche(nuevoPago, datosLeche);
            nuevoPago = setInfoVariacion(nuevoPago, datosVariacion);
            nuevoPago = setInfoPagos(nuevoPago, datosPago);


            // colocar fecha actual en fecha de nuevo pago
            nuevoPago.setFecha(new Date());
            guardarPago(nuevoPago);

            // reinicio variables para el resto de proveedores
            klsLeche = 0;
            turnoM = 0;
            turnoT = 0;
            retencionTotal = 0;
            descuentos = 0; 
            bono_frecuencia = 0;
        }
        acopioService.eliminarDatos();
        datosService.eliminarDatos();
    }

    // usar repositorio para guardar el pago
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

    // hacer prueba unitaria
    public int bonoFrecuencia(int turnoM, int turnoT, int pago){
        int bono = 0;
        if(turnoM == 1 && turnoT == 1){
            bono = (int) (pago * 0.20);
        }
        else if(turnoM == 1 && turnoT == 0){
            bono = (int) (pago * 0.12);
        }
        else if(turnoM == 0 && turnoT == 1){
            bono = (int) (pago * 0.8);
        }
        return bono;
    }


    // hacer prueba unitaria
    public int pagoAcopioLeche(int kls_leche, String categoria, int grasa, int solidos){
        return pagoPorLeche(kls_leche, categoria) + calculoGrasa(kls_leche, grasa) + calculoSolidos(kls_leche, solidos);
    }

    // hacer prueba unitaria
    public int pagoTotal(int pagoAcopio, int descuentos){
        return pagoAcopio - descuentos;
    }

    // hacer prueba unitaria
    public int pagoFinal(int pagoTotal, int retencionTotal){
        return pagoTotal - retencionTotal;
    }

    // hacer prueba unitaria                                               pasar kls_leche, por_grasa y por_solidos dentro de un vector
    public int calcularDescuentos(PagoEntity pagoAnterior, int pagoAcopio, int kls_leche, int por_grasa, int por_solidos){
        int descuento = 0;
        int kls_leche_anterior = 0;
        int por_grasa_anterior = 0;
        int por_solidos_anterior = 0;
        if(pagoAnterior.getComparado() == 0){
            kls_leche_anterior = pagoAnterior.getTotalKlsLeche();
            por_grasa_anterior = pagoAnterior.getPorGrasa();
            por_solidos_anterior = pagoAnterior.getPorSolidos();
            descuento += descuentoVariacionNegKls(kls_leche_anterior, kls_leche, pagoAcopio) ;
            descuento += descuentoVariacionNegGrasa(por_grasa_anterior, por_grasa, pagoAcopio) ;
            descuento += descuentoVariacionNegSolidos(por_solidos_anterior, por_solidos, pagoAcopio) ;
        }
        return descuento;
    }

    // hacer prueba unitaria
    public int porcentajeVariacionLeche(int klsLecheAnterior, int klsLeche){
        int diferencia = klsLecheAnterior - klsLeche;
        int porcentaje = 0;
        if(diferencia > 0){
            porcentaje = (int) ((diferencia*100)/klsLecheAnterior);
        }
        return porcentaje;
    }

    // hacer prueba unitaria
    public int descuentoVariacionNegKls(int klsLecheAnterior, int klsLeche, int pagoAcopio){
        int porcentaje = porcentajeVariacionLeche(klsLecheAnterior, klsLeche);
        //int porcentaje = (int) ((klsLecheAnterior - klsLeche) * 100 / klsLecheAnterior);
        if(porcentaje >= 0 && porcentaje <= 8){
            return 0;
        }
        else if(porcentaje >= 9 && porcentaje <= 25){
            return (int) (pagoAcopio * 0.07);
        }
        else if(porcentaje >= 26 && porcentaje <= 45){
            return (int) (pagoAcopio * 0.15);
        }
        else if(porcentaje >= 46){
            return (int) (pagoAcopio * 0.30);
        }
        else{
            return 0;
        }
    }
    
    // hacer prueba unitaria
    public int descuentoVariacionNegGrasa(int por_grasa_anterior, int por_grasa, int pagoAcopio){
        int porcentaje = (int) ((por_grasa_anterior - por_grasa) * 100 / por_grasa_anterior);
        if(porcentaje >= 0 && porcentaje <= 15){
            return 0;
        }
        else if(porcentaje >= 16 && porcentaje <= 25){
            return (int) (pagoAcopio * 0.12);
        }
        else if(porcentaje >= 26 && porcentaje <= 40){
            return (int) (pagoAcopio * 0.20);
        }
        else if(porcentaje >= 41){
            return (int) (pagoAcopio * 0.30);
        }
        else{
            return 0;
        }
    }

    // hacer prueba unitaria
    public int descuentoVariacionNegSolidos(int solidos_anterior, int solidos_actual, int pagoAcopio){
        int porcentaje = (int) ((solidos_anterior - solidos_actual) * 100 / solidos_anterior);
        if(porcentaje >= 0 && porcentaje <= 6){
            return 0;
        }
        else if(porcentaje >= 7 && porcentaje <= 12){
            return (int) (pagoAcopio * 0.18);
        }
        else if(porcentaje >= 13 && porcentaje <= 35){
            return (int) (pagoAcopio * 0.27);
        }
        else if(porcentaje >= 36){
            return (int) (pagoAcopio * 0.45);
        }
        else{
            return 0;
        }
    }

    // Recibe un vector con valores a los que calcular la retencion mediante un ciclo
    public int retencion(int pagoTotal, int pagoAcopio){
        ArrayList<Integer> valores = new ArrayList<Integer>();
        int retencion = 0;
        valores.add(pagoTotal);
        valores.add(pagoAcopio);
        for(int i = 0; i < valores.size(); i++){
            if(valores.get(i) > 950000){
                retencion += calculoRetencion(valores.get(i));
            }
        }
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

    // hacer prueba unitaria
    public int pagoPorLeche(int kls_leche, String categoria){
        return kls_leche * pagoPorCategoria(categoria);
    }

    // hacer prueba unitaria
    public int calculoGrasa(int kls_leche, int grasa){
        return kls_leche * pagoPorGrasa(grasa);
    }

    // hacer prueba unitaria
    public int calculoSolidos(int kls_leche, int solidos){
        return kls_leche * pagoPorSolidos(solidos);
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
    public int pagoPorGrasa(int grasa){
        int pago = 0;
        if(grasa >= 0 && grasa <= 20){
            pago = 30;
        }
        else if(grasa >= 21 && grasa <= 45){
            pago = 80;
        }
        else if(grasa >= 46){
            pago = 120;
        }
        return pago;
    }

    // hacer prueba unitaria
    public int pagoPorSolidos(int solidos){
        int pago = 0;
        if(solidos >= 0 && solidos <= 7){
            pago = -130;
        }
        else if(solidos >= 8 && solidos <= 18){
            pago = -90;
        }
        else if(solidos >= 19 && solidos <= 35){
            pago = 95;
        }
        else if(solidos >= 36){
            pago = 150;
        }
        return pago;
    }

}
