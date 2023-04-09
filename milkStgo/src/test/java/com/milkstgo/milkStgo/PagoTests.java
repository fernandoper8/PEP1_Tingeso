package com.milkstgo.milkStgo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.milkstgo.milkStgo.entities.AcopioEntity;
import com.milkstgo.milkStgo.entities.PagoEntity;
import com.milkstgo.milkStgo.repositories.AcopioRepository;
import com.milkstgo.milkStgo.repositories.PagoRepository;
import com.milkstgo.milkStgo.services.PagoService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;

@SpringBootTest
class PagoTests {

    @Autowired
    PagoService pagoService;
    @Autowired
    PagoRepository pagoRepository;
    @Autowired
    AcopioRepository acopioRepository;

    @Test
    void obtenerPagosTest1(){
        PagoEntity pago = new PagoEntity();
        pago.setNombre("Armin van Buuren");
        pago.setCodigo("1013");
        pago.setCategoria("A");
        pagoRepository.save(pago);
        assertNotNull(pagoService.obtenerPagos());
        pagoRepository.delete(pago);
    } 

    @Test // No hay pagos registrados
    void obtenerPagosPorProveedorTest1(){
        String codigo = "1013";
        PagoEntity pagoResultado = pagoService.obtenerPagosPorProveedor(codigo);
        assertEquals(-1, pagoResultado.getComparado());
        pagoRepository.delete(pagoResultado);
    }

    @Test // Se encuentra un pago del mismo proveedor
    void obtenerPagosPorProveedorTest2(){
        PagoEntity pago = new PagoEntity();
        pago.setNombre("Armin van Buuren");
        pago.setCodigo("1013");
        pago.setCategoria("A");
        pago.setComparado(0);
        pagoRepository.save(pago); 
        
        PagoEntity pagoResultado = pagoService.obtenerPagosPorProveedor(pago.getCodigo());
        assertEquals(pago, pagoResultado);
        pagoRepository.delete(pago);
    }

    @Test // No se encuentra un pago del mismo proveedor
    void obtenerPagosPorProveedorTest3(){
        // Pago no guardado
        PagoEntity pago = new PagoEntity();
        pago.setNombre("Armin van Buuren");
        pago.setCodigo("1013");
        pago.setCategoria("A");
        pago.setComparado(0);

        // Pago guardado
        PagoEntity pago2 = new PagoEntity();
        pago2.setNombre("Boris Brejcha");
        pago2.setCodigo("1003");
        pago2.setCategoria("B");
        pago2.setComparado(0);
        pagoRepository.save(pago2); 

        PagoEntity pagoResultado = pagoService.obtenerPagosPorProveedor(pago.getCodigo());
        assertEquals(-1, pagoResultado.getComparado());
        pagoRepository.delete(pago2);
    }

    @Test // Entregas en tarde y mañana -> turno = 3
    void setInfoPorEntregaTest1(){
        PagoEntity pago = new PagoEntity();
        pago.setNombre("Armin van Buuren");
        pago.setCodigo("1013");
        pago.setCategoria("A");
        pago.setComparado(0);
        pagoRepository.save(pago);
        
        AcopioEntity acopio = new AcopioEntity();
        acopio.setId_acopio(50);
        acopio.setFecha("09-04-2023");
        acopio.setId_proveedor("1013");
        acopio.setKls_leche("1000");
        acopio.setTurno("M");

        AcopioEntity acopio2 = new AcopioEntity();
        acopio2.setId_acopio(51);
        acopio2.setFecha("09-04-2023");
        acopio2.setId_proveedor("1013");
        acopio2.setKls_leche("200");
        acopio2.setTurno("T");

        ArrayList<AcopioEntity> acopios = new ArrayList<AcopioEntity>();
        acopios.add(acopio);
        acopios.add(acopio2);

        PagoEntity pagoResultado = pagoService.setInfoPorEntrega(acopios, pago);

        assertEquals(1200, pagoResultado.getTotalKlsLeche());
        assertEquals(3, pagoResultado.getQueTurnos());
        assertEquals(2, pagoResultado.getFrecuencia());
        assertEquals(600, pagoResultado.getPromedioDiarioKls());
        pagoRepository.delete(pago);
    }

    @Test // Entregas en mañana -> turno = 2
    void setInfoPorEntregaTest2(){
        PagoEntity pago = new PagoEntity();
        pago.setNombre("Armin van Buuren");
        pago.setCodigo("1013");
        pago.setCategoria("A");
        pago.setComparado(0);
        pagoRepository.save(pago);
        
        AcopioEntity acopio = new AcopioEntity();
        acopio.setId_acopio(50);
        acopio.setFecha("09-04-2023");
        acopio.setId_proveedor("1013");
        acopio.setKls_leche("1000");
        acopio.setTurno("M");

        ArrayList<AcopioEntity> acopios = new ArrayList<AcopioEntity>();
        acopios.add(acopio);

        PagoEntity pagoResultado = pagoService.setInfoPorEntrega(acopios, pago);

        assertEquals(1000, pagoResultado.getTotalKlsLeche());
        assertEquals(2, pagoResultado.getQueTurnos());
        assertEquals(1, pagoResultado.getFrecuencia());
        assertEquals(1000, pagoResultado.getPromedioDiarioKls());
        pagoRepository.delete(pago);
    }

    @Test // Entregas en tarde -> turno = 1
    void setInfoPorEntregaTest3(){
        PagoEntity pago = new PagoEntity();
        pago.setNombre("Armin van Buuren");
        pago.setCodigo("1013");
        pago.setCategoria("A");
        pago.setComparado(0);
        pagoRepository.save(pago);
        
        AcopioEntity acopio = new AcopioEntity();
        acopio.setId_acopio(50);
        acopio.setFecha("09-04-2023");
        acopio.setId_proveedor("1013");
        acopio.setKls_leche("1000");
        acopio.setTurno("T");

        ArrayList<AcopioEntity> acopios = new ArrayList<AcopioEntity>();
        acopios.add(acopio);

        PagoEntity pagoResultado = pagoService.setInfoPorEntrega(acopios, pago);

        assertEquals(1000, pagoResultado.getTotalKlsLeche());
        assertEquals(1, pagoResultado.getQueTurnos());
        assertEquals(1, pagoResultado.getFrecuencia());
        assertEquals(1000, pagoResultado.getPromedioDiarioKls());
        pagoRepository.delete(pago);
    }

    @Test // No hay entregas
    void setInfoPorEntregaTest4(){
        PagoEntity pago = new PagoEntity();
        pago.setNombre("Armin van Buuren");
        pago.setCodigo("1013");
        pago.setCategoria("A");
        pago.setComparado(0);
        pagoRepository.save(pago);

        ArrayList<AcopioEntity> acopios = new ArrayList<AcopioEntity>();

        PagoEntity pagoResultado = pagoService.setInfoPorEntrega(acopios, pago);

        assertEquals(0, pagoResultado.getTotalKlsLeche());
        assertEquals(0, pagoResultado.getQueTurnos());
        assertEquals(0, pagoResultado.getFrecuencia());
        assertEquals(0, pagoResultado.getPromedioDiarioKls());
        pagoRepository.delete(pago);
    }

    @Test // Hay pago anterior
    void setVariacionesTest1(){
        PagoEntity pago1 = new PagoEntity();
        pago1.setNombre("Armin van Buuren");
        pago1.setCodigo("1013");
        pago1.setCategoria("A");
        pago1.setComparado(0);
        pago1.setTotalKlsLeche(1820);
        pago1.setPorGrasa(72);
        pago1.setPorSolidos(70);

        PagoEntity pago2 = new PagoEntity();
        pago2.setNombre("Armin van Buuren");
        pago2.setCodigo("1013");
        pago2.setCategoria("A");
        pago2.setComparado(0);
        pago2.setTotalKlsLeche(1200);
        pago2.setPorGrasa(37);
        pago2.setPorSolidos(31);

        PagoEntity pagoResultado = pagoService.setVariaciones(pago2, pago1);

        assertEquals(34, (int)pagoResultado.getPorVariacionLeche());
        assertEquals(35, pagoResultado.getPorVariacionGrasa());
        assertEquals(39, pagoResultado.getPorVariacionSolidos());
    }

    @Test // No hay pago anterior
    void setVariacionesTest2(){
        PagoEntity pago1 = new PagoEntity();
        pago1.setNombre("Armin van Buuren");
        pago1.setCodigo("1013");
        pago1.setCategoria("A");
        pago1.setComparado(0);
        pago1.setTotalKlsLeche(1820);
        pago1.setPorGrasa(72);
        pago1.setPorSolidos(70);

        PagoEntity pago2 = new PagoEntity();
        pago2.setComparado(-1);

        PagoEntity pagoResultado = pagoService.setVariaciones(pago1, pago2);

        assertEquals(0, pagoResultado.getPorVariacionLeche());
        assertEquals(0, pagoResultado.getPorVariacionGrasa());
        assertEquals(0, pagoResultado.getPorVariacionSolidos());
    }

    @Test // Frecuencia menor a 10
    void setAcopioTest1(){ 
        PagoEntity pago = new PagoEntity();
        pago.setNombre("Armin van Buuren");
        pago.setCodigo("1013");
        pago.setCategoria("A");
        pago.setComparado(0);
        pago.setTotalKlsLeche(1820);
        pago.setPorGrasa(72);
        pago.setPorSolidos(70);
        pago.setFrecuencia(1);
    
        PagoEntity pagoResultado = pagoService.setAcopio(pago);

        assertEquals(1765400, pagoResultado.getPagoAcopio());
        assertEquals(0, pagoResultado.getBonoFrecuencia());
    }

    @Test // Frecuencia mayor a 10
    void setAcopioTest2(){ 
        PagoEntity pago = new PagoEntity();
        pago.setNombre("Armin van Buuren");
        pago.setCodigo("1013");
        pago.setCategoria("A");
        pago.setComparado(0);
        pago.setTotalKlsLeche(1820);
        pago.setPorGrasa(72);
        pago.setPorSolidos(70);
        pago.setFrecuencia(11);
        pago.setQueTurnos(3);
    
        PagoEntity pagoResultado = pagoService.setAcopio(pago);

        assertEquals(1765400*1.20, pagoResultado.getPagoAcopio());
        assertEquals(1765400*0.20, pagoResultado.getBonoFrecuencia());
    }

    @Test
    void pagoPorCategoriaTest1(){
        String cat1 = "A";	
        String cat2 = "B";
        String cat3 = "C";
        String cat4 = "D";

        assertEquals(700, pagoService.pagoPorCategoria(cat1));
        assertEquals(550, pagoService.pagoPorCategoria(cat2));
        assertEquals(400, pagoService.pagoPorCategoria(cat3));
        assertEquals(250, pagoService.pagoPorCategoria(cat4));
    }

    @Test
    void pagoPorGrasaTest1(){
        int porGrasa1 = 10;
        int porGrasa2 = 25;
        int porGrasa3 = 55;
        int porGrasa4 = -20;

        assertEquals(30, pagoService.pagoPorGrasa(porGrasa1));
        assertEquals(80, pagoService.pagoPorGrasa(porGrasa2));
        assertEquals(120, pagoService.pagoPorGrasa(porGrasa3));
        assertEquals(0, pagoService.pagoPorGrasa(porGrasa4));
    }

    @Test
    void pagoPorGrasaSolidosTest1(){
        int porSolido1 = 5;
        int porSolido2 = 15;
        int porSolido3 = 25;
        int porSolido4 = 45;
        int porSolido5 = -20;

        assertEquals(-130, pagoService.pagoPorSolidos(porSolido1));
        assertEquals(-90, pagoService.pagoPorSolidos(porSolido2));
        assertEquals(95, pagoService.pagoPorSolidos(porSolido3));
        assertEquals(150, pagoService.pagoPorSolidos(porSolido4));
        assertEquals(0, pagoService.pagoPorSolidos(porSolido5));
    }

    @Test // Entregas mañana y tarde -> queTurnos = 3
    void bonoFrecuenciaTest1(){
        PagoEntity pago = new PagoEntity();
        pago.setNombre("Armin van Buuren");
        pago.setCodigo("1013");
        pago.setCategoria("A");
        pago.setComparado(0);
        pago.setTotalKlsLeche(1820);
        pago.setPorGrasa(72);
        pago.setPorSolidos(70);
        pago.setFrecuencia(11);
        pago.setQueTurnos(3);
    
        pago = pagoService.setAcopio(pago);

        int resultado = pagoService.bonoFrecuencia(pago, 1765400);

        assertEquals(1.20*1765400, resultado);
    }

    @Test // Entregas en mañana -> turno = 2
    void bonoFrecuenciaTest2(){
        PagoEntity pago = new PagoEntity();
        pago.setNombre("Armin van Buuren");
        pago.setCodigo("1013");
        pago.setCategoria("A");
        pago.setComparado(0);
        pago.setTotalKlsLeche(1820);
        pago.setPorGrasa(72);
        pago.setPorSolidos(70);
        pago.setFrecuencia(11);
        pago.setQueTurnos(2);
    
        pago = pagoService.setAcopio(pago);

        int resultado = pagoService.bonoFrecuencia(pago, 1765400);

        assertEquals((int)(1.12*1765400), resultado);
    }

    @Test // Entregas en tarde -> turno = 1
    void bonoFrecuenciaTest3(){
        PagoEntity pago = new PagoEntity();
        pago.setNombre("Armin van Buuren");
        pago.setCodigo("1013");
        pago.setCategoria("A");
        pago.setComparado(0);
        pago.setTotalKlsLeche(1820);
        pago.setPorGrasa(72);
        pago.setPorSolidos(70);
        pago.setFrecuencia(11);
        pago.setQueTurnos(1);
    
        pago = pagoService.setAcopio(pago);

        int resultado = pagoService.bonoFrecuencia(pago, 1765400);

        assertEquals((int)(1.08*1765400), resultado);
    }

    @Test // Con pago anterior
    void setDescuentosTest1(){
        PagoEntity pago1 = new PagoEntity();
        pago1.setNombre("Armin van Buuren");
        pago1.setCodigo("1013");
        pago1.setCategoria("A");
        pago1.setComparado(0);
        pago1.setTotalKlsLeche(1820);
        pago1.setPorGrasa(72);
        pago1.setPorSolidos(70);

        PagoEntity pago2 = new PagoEntity();
        pago2.setNombre("Armin van Buuren");
        pago2.setCodigo("1013");
        pago2.setCategoria("A");
        pago2.setComparado(0);
        pago2.setTotalKlsLeche(1200);
        pago2.setPorGrasa(37);
        pago2.setPorSolidos(31);
        pago2.setPagoAcopio(1134000);

        pago1 = pagoService.setVariaciones(pago2, pago1);
        pago1 = pagoService.setDescuentos(pago2, pago1);

        assertEquals(170100, pago1.getDctoVariacionLeche());
        assertEquals(226800, pago1.getDctoVariacionGrasa());
        assertEquals(510300, pago1.getDctoVariacionSolidos());
    }

    @Test // Sin pago anterior
    void setDescuentosTest2(){
        PagoEntity pago1 = new PagoEntity();
        pago1.setComparado(-1);
        
        PagoEntity pago2 = new PagoEntity();
        pago2.setNombre("Armin van Buuren");
        pago2.setCodigo("1013");
        pago2.setCategoria("A");
        pago2.setComparado(0);
        pago2.setTotalKlsLeche(1820);
        pago2.setPorGrasa(72);
        pago2.setPorSolidos(70);
        pago2.setPagoAcopio(1765400);

        

        pago1 = pagoService.setVariaciones(pago2, pago1);
        pago1 = pagoService.setDescuentos(pago2, pago1);

        assertEquals(0, pago1.getDctoVariacionLeche());
        assertEquals(0, pago1.getDctoVariacionGrasa());
        assertEquals(0, pago1.getDctoVariacionSolidos());
    }

    @Test // Sin retencion
    void setPagosTest1(){
        PagoEntity pago1 = new PagoEntity();
        pago1.setNombre("Armin van Buuren");
        pago1.setCodigo("1013");
        pago1.setCategoria("A");
        pago1.setComparado(0);
        pago1.setTotalKlsLeche(1820);
        pago1.setPorGrasa(72);
        pago1.setPorSolidos(70);

        PagoEntity pago2 = new PagoEntity();
        pago2.setNombre("Armin van Buuren");
        pago2.setCodigo("1013");
        pago2.setCategoria("A");
        pago2.setComparado(0);
        pago2.setTotalKlsLeche(1200);
        pago2.setPorGrasa(37);
        pago2.setPorSolidos(31);
        pago2.setPagoAcopio(1134000);

        pago2 = pagoService.setVariaciones(pago2, pago1);
        pago2 = pagoService.setDescuentos(pago2, pago1);

        pago2 = pagoService.setPagos(pago2);
        
        assertEquals(226800, pago2.getPagoTotal());
        assertEquals(226800, pago2.getPagoFinal());
    }

    @Test // Con retencion
    void setPagosTest2(){
        PagoEntity pago1 = new PagoEntity();
        pago1.setNombre("Armin van Buuren");
        pago1.setCodigo("1013");
        pago1.setCategoria("A");
        pago1.setComparado(0);
        pago1.setTotalKlsLeche(1820);
        pago1.setPorGrasa(72);
        pago1.setPorSolidos(70);
        pago1.setPagoAcopio(1765400);

        PagoEntity pago2 = new PagoEntity();
        pago2.setComparado(-1);

        pago1 = pagoService.setVariaciones(pago1, pago2);
        pago1 = pagoService.setDescuentos(pago1, pago2);

        pago1 = pagoService.setPagos(pago1);

        assertEquals(1765400, pago1.getPagoTotal());
        assertEquals(229502, pago1.getMontoRetencion());
        assertEquals(1535898, pago1.getPagoFinal());
    }

    @Test // Pago correspondiente mayor a 0
    void pagoTotalTest1(){
        PagoEntity pago1 = new PagoEntity();
        pago1.setNombre("Armin van Buuren");
        pago1.setCodigo("1013");
        pago1.setCategoria("A");
        pago1.setComparado(0);
        pago1.setTotalKlsLeche(1820);
        pago1.setPorGrasa(72);
        pago1.setPorSolidos(70);

        PagoEntity pago2 = new PagoEntity();
        pago2.setNombre("Armin van Buuren");
        pago2.setCodigo("1013");
        pago2.setCategoria("A");
        pago2.setComparado(0);
        pago2.setTotalKlsLeche(1200);
        pago2.setPorGrasa(37);
        pago2.setPorSolidos(31);
        pago2.setPagoAcopio(1134000);

        pago1 = pagoService.setVariaciones(pago2, pago1);
        pago1 = pagoService.setDescuentos(pago2, pago1);

        int resultado = pagoService.pagoTotal(pago1);

        assertEquals(226800, resultado);
    }

    @Test // Pago correspondiente menor a 0
    void pagoTotalTest2(){
        PagoEntity pago = new PagoEntity();
        pago.setNombre("Armin van Buuren");
        pago.setCodigo("1013");
        pago.setCategoria("A");
        pago.setComparado(0);
        pago.setTotalKlsLeche(1820);
        pago.setPorGrasa(72);
        pago.setPorSolidos(70);

        pago.setDctoVariacionLeche(210100);
        pago.setDctoVariacionGrasa(276800);
        pago.setDctoVariacionSolidos(560300);

        int resultado = pagoService.pagoTotal(pago);

        assertEquals(0, resultado);
    }

    @Test
    void montoRetencionTest1(){
        PagoEntity pago = new PagoEntity();
        pago.setNombre("Armin van Buuren");
        pago.setCodigo("1013");
        pago.setCategoria("A");
        pago.setComparado(0);
        pago.setTotalKlsLeche(1820);
        pago.setPorGrasa(72);
        pago.setPorSolidos(70);
        
        pago.setPagoTotal(1765400);
        int resultado = pagoService.montoRetencion(pago);

        assertEquals(229502, resultado);
    }

    @Test // Pago final mayor a 0
    void pagoFinalTest1(){
        PagoEntity pago = new PagoEntity();
        pago.setNombre("Armin van Buuren");
        pago.setCodigo("1013");
        pago.setCategoria("A");
        pago.setComparado(0);
        pago.setTotalKlsLeche(1820);
        pago.setPorGrasa(72);
        pago.setPorSolidos(70);
        
        pago.setPagoTotal(1765400);
        pago.setMontoRetencion(229502);
        int resultado = pagoService.pagoFinal(pago);

        assertEquals(1535898, resultado);
    }

    @Test // Pago final menor a 0
    void pagoFinalTest2(){
        PagoEntity pago = new PagoEntity();
        pago.setNombre("Armin van Buuren");
        pago.setCodigo("1013");
        pago.setCategoria("A");
        pago.setComparado(0);
        pago.setTotalKlsLeche(1820);
        pago.setPorGrasa(72);
        pago.setPorSolidos(70);
        
        pago.setPagoTotal(1765400);
        pago.setMontoRetencion(1775400);
        int resultado = pagoService.pagoFinal(pago);

        assertEquals(0, resultado);
    }

    @Test
    void calcularDescuentoPorLecheTest1(){
        PagoEntity pago = new PagoEntity();
        pago.setNombre("Armin van Buuren");
        pago.setCodigo("1013");
        pago.setCategoria("A");
        pago.setComparado(0);
        pago.setTotalKlsLeche(1200);
        pago.setPorGrasa(37);
        pago.setPorSolidos(31);
        pago.setPagoAcopio(1134000);

        pago.setPorVariacionLeche(34);
        int resultado = pagoService.calcularDescuentoPorLeche(pago);
        assertEquals(170100, resultado);
    }

    @Test
    void calcularDescuentoPorGrasaTest1(){
        PagoEntity pago = new PagoEntity();
        pago.setNombre("Armin van Buuren");
        pago.setCodigo("1013");
        pago.setCategoria("A");
        pago.setComparado(0);
        pago.setTotalKlsLeche(1200);
        pago.setPorGrasa(37);
        pago.setPorSolidos(31);
        pago.setPagoAcopio(1134000);

        pago.setPorVariacionGrasa(35);
        int resultado = pagoService.calcularDescuentoPorGrasa(pago);
        assertEquals(226800, resultado);
    }

    @Test
    void calcularDescuentoPorSolidosTest1(){
        PagoEntity pago = new PagoEntity();
        pago.setNombre("Armin van Buuren");
        pago.setCodigo("1013");
        pago.setCategoria("A");
        pago.setComparado(0);
        pago.setTotalKlsLeche(1200);
        pago.setPorGrasa(37);
        pago.setPorSolidos(31);
        pago.setPagoAcopio(1134000);

        pago.setPorVariacionSolidos(39);
        int resultado = pagoService.calcularDescuentoPorSolidos(pago);
        assertEquals(510300, resultado);
    }

    @Test
    void descuentoPorLecheTest1(){
        float porc1 = 5;
        float porc2 = 15;
        float porc3 = 35;
        float porc4 = 55;
        float porc5 = -20;

        assertEquals(0, pagoService.descuentoPorLeche(porc1));
        assertEquals(0.07, pagoService.descuentoPorLeche(porc2));
        assertEquals(0.15, pagoService.descuentoPorLeche(porc3));
        assertEquals(0.30, pagoService.descuentoPorLeche(porc4));
        assertEquals(0, pagoService.descuentoPorLeche(porc5));
    }

    @Test
    void descuentoPorGrasaTest1(){
        int porc1 = 10;
        int porc2 = 20;
        int porc3 = 30;
        int porc4 = 50;
        int porc5 = -20;

        assertEquals(0, pagoService.descuentoPorGrasa(porc1));
        assertEquals(0.12, pagoService.descuentoPorGrasa(porc2));
        assertEquals(0.20, pagoService.descuentoPorGrasa(porc3));
        assertEquals(0.30, pagoService.descuentoPorGrasa(porc4));
        assertEquals(0, pagoService.descuentoPorGrasa(porc5));
    }
    
    @Test
    void descuentoPorSolidosTest1(){
        int porc1 = 5;
        int porc2 = 10;
        int porc3 = 20;
        int porc4 = 45;
        int porc5 = -20;

        assertEquals(0, pagoService.descuentoPorSolidos(porc1));
        assertEquals(0.18, pagoService.descuentoPorSolidos(porc2));
        assertEquals(0.27, pagoService.descuentoPorSolidos(porc3));
        assertEquals(0.45, pagoService.descuentoPorSolidos(porc4));
        assertEquals(0, pagoService.descuentoPorSolidos(porc5));
    }

    @Test
    void calcularVariacionLecheTest1(){
        int datoAnterior = 1820;
        int datoActual = 1200;

        int resultado = (int)pagoService.calcularVariacionLeche(datoAnterior, datoActual);
        assertEquals(34, resultado);
    }

    @Test 
    void calcularVariacionPorcentajeTest1(){
        int datoAnterior = 72;
        int datoActual = 37;

        int resultado = pagoService.calcularVariacionPorcentaje(datoAnterior, datoActual);
        assertEquals(35, resultado);
    }
    
    @Test
    void guardarPagoTest1(){
        PagoEntity pago = new PagoEntity();
        pagoService.guardarPago(pago);
        ArrayList<PagoEntity> pagos = pagoService.obtenerPagos();
        assertEquals(1, pagos.size());
        pagoRepository.delete(pago);
    }

    @Test
    void actualizarPagoTest1(){
        PagoEntity pago = new PagoEntity();
        pago.setNombre("Armin van Buuren");
        pago.setCodigo("10");
        pago.setComparado(0);
        pagoService.guardarPago(pago);

        pagoService.actualizarPago(pago);

        PagoEntity pagoTest = pagoService.obtenerPagosPorProveedor("10");
        assertEquals(-1, pagoTest.getComparado());
        pagoRepository.delete(pago);
    }
}