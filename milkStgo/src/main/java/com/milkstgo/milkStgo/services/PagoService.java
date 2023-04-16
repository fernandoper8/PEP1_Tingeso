package com.milkstgo.milkStgo.services;

import com.milkstgo.milkStgo.entities.AcopioEntity;
import com.milkstgo.milkStgo.entities.DatosEntity;
import com.milkstgo.milkStgo.entities.PagoEntity;
import com.milkstgo.milkStgo.entities.ProveedorEntity;
import com.milkstgo.milkStgo.repositories.PagoRepository;
import lombok.Generated;
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
        if(pagos.isEmpty()){
            PagoEntity pagosProveedor = new PagoEntity();
            pagosProveedor.setComparado(-1); // no hay pagos anteriores
            return pagosProveedor;
        }
        PagoEntity pagosProveedor = new PagoEntity();
        for(PagoEntity pago : pagos){
            if(pago.getCodigo().equals(codigo) && pago.getComparado() == 0){
                return pago;
            }
        }
        pagosProveedor.setComparado(-1); // no hay pagos anteriores
        return pagosProveedor;
    }

    @Generated
    public void crearPlanilla(){
        PagoEntity nuevoPago;
        ArrayList<ProveedorEntity> proveedores = proveedorService.obtenerProveedores();
        for(ProveedorEntity proveedor : proveedores){
            ArrayList<AcopioEntity> acopiosProveedor = acopioService.obtenerAcopiosPorProveedor(proveedor.getCodigo());
            if(!acopiosProveedor.isEmpty()) {
                DatosEntity datosProveedor = datosService.obtenerDataPorProveedor(proveedor.getCodigo());
                PagoEntity pagoAnterior = obtenerPagosPorProveedor(proveedor.getCodigo());
                nuevoPago = new PagoEntity();
                nuevoPago.setNombre(proveedor.getNombre());
                nuevoPago.setCodigo(proveedor.getCodigo());
                nuevoPago.setCategoria(proveedor.getCategoria());
                nuevoPago.setComparado(0);
                nuevoPago.setFecha(new Date());
                nuevoPago = setInfoPorEntrega(acopiosProveedor, nuevoPago);
                nuevoPago.setPorGrasa(datosProveedor.getPor_grasa());
                nuevoPago.setPorSolidos(datosProveedor.getPor_solidos());
                nuevoPago = setVariaciones(nuevoPago, pagoAnterior);
                nuevoPago = setAcopio(nuevoPago);
                nuevoPago = setDescuentos(nuevoPago, pagoAnterior);
                nuevoPago = setPagos(nuevoPago);
                guardarPago(nuevoPago);
                actualizarPago(pagoAnterior);
            }
        }
        acopioService.eliminarAcopios();
        datosService.eliminarDatos();
    }

    public PagoEntity setInfoPorEntrega(ArrayList<AcopioEntity> acopiosProveedor, PagoEntity pago){
        int totalLeche = 0;
        int turnoM = 0;
        int turnoT = 0;
        int turno = 0;
        for(AcopioEntity acopio : acopiosProveedor){
            totalLeche += Integer.parseInt(acopio.getKls_leche());
            if(acopio.getTurno().equals("M"))
                turnoM = 1;
            else if(acopio.getTurno().equals("T"))
                turnoT = 1;
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
        pago.setTotalKlsLeche(totalLeche);
        pago.setQueTurnos(turno);
        pago.setFrecuencia(acopiosProveedor.size());
        if(!acopiosProveedor.isEmpty())
            pago.setPromedioDiarioKls((float)totalLeche/acopiosProveedor.size());
        else
            pago.setPromedioDiarioKls(0);
        return pago;
    }

    public PagoEntity setVariaciones(PagoEntity pago, PagoEntity pagoAnterior){
        if(pagoAnterior.getComparado() == 0){
            pago.setPorVariacionLeche(calcularVariacionLeche(pagoAnterior.getTotalKlsLeche(), pago.getTotalKlsLeche()));
            pago.setPorVariacionGrasa(calcularVariacionPorcentaje(pagoAnterior.getPorGrasa(), pago.getPorGrasa()));
            pago.setPorVariacionSolidos(calcularVariacionPorcentaje(pagoAnterior.getPorSolidos(), pago.getPorSolidos()));
        }else if(pagoAnterior.getComparado() == -1){
            pago.setPorVariacionLeche(0);
            pago.setPorVariacionGrasa(0);
            pago.setPorVariacionSolidos(0);
        }
        return pago;
    }

    public PagoEntity setAcopio(PagoEntity pago){
        int pagoAcopio;
        int bono = 0;
        int leche = pago.getTotalKlsLeche();
        int pagoCategoria = pagoPorCategoria(pago.getCategoria());
        int pagoGrasa = pagoPorGrasa(pago.getPorGrasa());
        int pagoSolidos = pagoPorSolidos(pago.getPorSolidos());
        pagoAcopio = leche*pagoCategoria + leche*pagoGrasa + leche*pagoSolidos;

        if(pago.getFrecuencia() > 10){
            bono = bonoFrecuencia(pago, pagoAcopio) - pagoAcopio;
        }
        pago.setPagoAcopio(pagoAcopio+bono);
        pago.setBonoFrecuencia(bono);
        return pago;
    }

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
            default:
                break;
        }
        return pago;
    }
    
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

    public int bonoFrecuencia(PagoEntity pago, int pagoAcopio){
        double bono = 0;
        int turno = pago.getQueTurnos();
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

    public PagoEntity setDescuentos(PagoEntity pago, PagoEntity pagoAnterior){
        if(pagoAnterior.getComparado() == 0){
            pago.setDctoVariacionLeche(calcularDescuentoPorLeche(pago));
            pago.setDctoVariacionGrasa(calcularDescuentoPorGrasa(pago));
            pago.setDctoVariacionSolidos(calcularDescuentoPorSolidos(pago));
        }else if(pagoAnterior.getComparado() == -1){
            pago.setDctoVariacionLeche(0);
            pago.setDctoVariacionGrasa(0);
            pago.setDctoVariacionSolidos(0);
        }
            
        return pago;
    }

    public PagoEntity setPagos(PagoEntity pago){
        pago.setPagoTotal(pagoTotal(pago));
        if(pago.getPagoTotal() > 950000)
            pago.setMontoRetencion(montoRetencion(pago));
        else
            pago.setMontoRetencion(0);
        pago.setPagoFinal(pagoFinal(pago));
        return pago;
    }

    public int pagoTotal(PagoEntity pago){
        int resultado = pago.getPagoAcopio() - pago.getDctoVariacionLeche() - pago.getDctoVariacionGrasa() - pago.getDctoVariacionSolidos();
        if(resultado < 0)
            resultado = 0;
        return resultado;
    }

    public int montoRetencion(PagoEntity pago){
        return (int) (pago.getPagoTotal() * 0.13);
    }

    public int pagoFinal(PagoEntity pago){
        int resultado = pago.getPagoTotal() - pago.getMontoRetencion();
        if(resultado < 0)
            resultado = 0;
        return resultado;
    }

    public int calcularDescuentoPorLeche(PagoEntity pago){
        float porcentajeVariacionLeche = pago.getPorVariacionLeche();
        double porcentajeDescuento = descuentoPorLeche(porcentajeVariacionLeche);
        return (int) (pago.getPagoAcopio() * porcentajeDescuento);
    }

    public int calcularDescuentoPorGrasa(PagoEntity pago){
        float porcentajeVariacionGrasa = pago.getPorVariacionGrasa();
        double porcentajeDescuento = descuentoPorGrasa(porcentajeVariacionGrasa);
        return (int) (pago.getPagoAcopio() * porcentajeDescuento);
    }

    public int calcularDescuentoPorSolidos(PagoEntity pago){
        float porcentajeVariacionSolidos = pago.getPorVariacionSolidos();
        double porcentajeDescuento = descuentoPorSolidos(porcentajeVariacionSolidos);
        return (int) (pago.getPagoAcopio() * porcentajeDescuento);
    }

    public double descuentoPorLeche(float porcentaje){
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

    public double descuentoPorGrasa(float porcentaje){
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

    public double descuentoPorSolidos(float porcentaje){
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

    public float calcularVariacionLeche(int datoAnterior, int datoActual){
        return (((float)datoActual - (float)datoAnterior) / datoAnterior) * -100;
    }

    public int calcularVariacionPorcentaje(int porAnterior, int porActual){
        return porAnterior - porActual;
    }

    public void guardarPago(PagoEntity nuevoPago){
        pagoRepository.save(nuevoPago);
    }

    public void actualizarPago(PagoEntity pago){
        if(pago.getComparado() == 0){
            pago.setComparado(-1);
            guardarPago(pago);
        }
    }
}