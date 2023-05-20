
package com.milkstgo.milkStgo;

import com.milkstgo.milkStgo.entities.DatosEntity;
import com.milkstgo.milkStgo.entities.PlanillaEntity;
import com.milkstgo.milkStgo.entities.ProveedorEntity;
import com.milkstgo.milkStgo.repositories.PlanillaRepository;
import com.milkstgo.milkStgo.services.AcopioService;
import com.milkstgo.milkStgo.services.DatosService;
import com.milkstgo.milkStgo.services.PlanillaDescuentosService;
import com.milkstgo.milkStgo.services.PlanillaEntregasService;
import com.milkstgo.milkStgo.services.PlanillaPagosService;
import com.milkstgo.milkStgo.services.PlanillaPorcentajesService;
import com.milkstgo.milkStgo.services.PlanillaService;
import com.milkstgo.milkStgo.services.ProveedorService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.milkstgo.milkStgo.entities.AcopioEntity;
import com.milkstgo.milkStgo.repositories.AcopioRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;

import java.util.ArrayList;

@SpringBootTest
class PagoTests {
    private static final String ID_PROVEEDOR1 = "5000";
    private static final String ID_PROVEEDOR2 = "5001";

    @Autowired
    PlanillaService planillaService;
    @Autowired
    PlanillaPorcentajesService planillaPorcentajesService;
    @Autowired
    PlanillaRepository planillaRepository;
    @Autowired
    AcopioService acopioService;
    @Autowired
    AcopioRepository acopioRepository;

    // Metodos para crear datos utiles para el testeo
    private ProveedorEntity crearProveedor1(){
        ProveedorEntity proveedor = new ProveedorEntity();
        proveedor.setCodigo(ID_PROVEEDOR1);
        proveedor.setNombre("Proveedor1");
        proveedor.setCategoria("A");
        proveedor.setRetencion("Si");
        return proveedor;
    }
    private AcopioEntity crearAcopio1(String id_proveedor){
        AcopioEntity acopio = new AcopioEntity();
        acopio.setFecha("17-03-2023");
        acopio.setId_proveedor(id_proveedor);
        acopio.setKls_leche("100");
        acopio.setTurno("M");
        return acopio;
    }
    private DatosEntity crearDatos1(String id_proveedor){
        DatosEntity datos = new DatosEntity();
        datos.setId_proveedor(id_proveedor);
        datos.setPor_grasa(35);
        datos.setPor_solidos(35);
        return datos;
    }

    // Tests PlanillaService
    @Test
    void autowiredTests(){
        ProveedorEntity proveedor = crearProveedor1(); // Cat A
        PlanillaService planillaService = new PlanillaService();
        PlanillaEntity planilla = new PlanillaEntity();
        planilla.setCodigo("A");
        planillaService.setPlanilla(planilla);

        /* 
        // ProveedorService
        ProveedorService proveedorService = planillaService.getProveedorService();
        // AcopioService
        AcopioService acopioService = planillaService.getAcopioService();
        // DatosService
        DatosService datosService = planillaService.getDatosService();
        // PlanillaRepository
        PlanillaRepository planillaRepository = planillaService.getPlanillaRepository();
        // PlanillaDescuentosService
        PlanillaDescuentosService planillaDescuentosService = planillaService.getPlanillaDescuentosService();
        // PlanillaEntregasService
        PlanillaEntregasService planillaEntregasService = planillaService.getPlanillaEntregasService();
        // PlanillaPagosService
        PlanillaPagosService planillaPagosService = planillaService.getPlanillaPagosService();
        // PlanillaPorcentajesService
        PlanillaPorcentajesService planillaPorcentajesService = planillaService.getPlanillaPorcentajesService();   
        */
    }
    @Test
    void tieneAcopiosTest(){
        ArrayList<AcopioEntity> acopios1 = acopioService.obtenerAcopiosPorProveedor(ID_PROVEEDOR1);
        ArrayList<AcopioEntity> acopios2 = acopioService.obtenerAcopiosPorProveedor(ID_PROVEEDOR2);
        acopios1.add(crearAcopio1(ID_PROVEEDOR1));
        assertEquals(true, planillaService.tieneAcopios(acopios1));
        assertEquals(false, planillaService.tieneAcopios(acopios2));
    }
    @Test
    void guardarPlanillaTest(){
        PlanillaEntity nuevaPlanilla = new PlanillaEntity();

        //int cantidadPlanillasGuardadasAntes = planillaRepository.findAll().size();
        planillaService.guardarPlanilla(nuevaPlanilla);
        int cantidadPlanillasGuardadas = planillaRepository.findAll().size();
        assertEquals(cantidadPlanillasGuardadas, 1);
        planillaRepository.deleteAll();
    }
    @Test
    void actualizarPlanillaAnteriorTest(){
        PlanillaEntity planillaAnterior = new PlanillaEntity();
        
        int comparadoAnterior = 0;
        planillaAnterior.setComparado(comparadoAnterior);
        planillaService.actualizarPlanillaAnterior(planillaAnterior);

        // Caso 1: Se cambia el comparado de 0 a 1 y se guarda en el repositorio
        assertEquals(-1, planillaAnterior.getComparado());
        assertEquals(1, planillaRepository.findAll().size());
        
        // Caso 2: El comparado de la planilla de entrada ya esta en -1, por lo que no es la ultima planilla
        // por lo que no se guarda en el repositorio
        comparadoAnterior = 3;
        planillaAnterior = new PlanillaEntity();
        planillaAnterior.setComparado(comparadoAnterior);
        assertEquals(3, planillaAnterior.getComparado());
        assertEquals(1, planillaRepository.findAll().size());
        
        planillaRepository.deleteAll();
    }
    @Test
    void setInfoRestanteTest(){
        PlanillaService planillaServiceT= new PlanillaService();
        planillaServiceT.setInfoRestante();

        assertEquals(0, planillaServiceT.getPlanilla().getComparado()); 
        // no se testea el atributo de fecha, puesto que esta se genera en el momento
    }
    @Test
    void setInfoProveedorTest(){
        ProveedorEntity proveedor = crearProveedor1();
        PlanillaService planillaServiceT= new PlanillaService();
        planillaServiceT.setInfoProveedor(proveedor);

        String nombreProveedor = proveedor.getNombre();
        String codigoProveedor = proveedor.getCodigo();
        String categoriaProveedor = proveedor.getCategoria();

        assertEquals(nombreProveedor, planillaServiceT.getPlanilla().getNombre());
        assertEquals(codigoProveedor, planillaServiceT.getPlanilla().getCodigo());
        assertEquals(categoriaProveedor, planillaServiceT.getPlanilla().getCategoria());
    }
    @Test
    void reiniciarPlanillaTest(){
        PlanillaService planillaServiceT = new PlanillaService();
        planillaServiceT.reiniciarPlanilla();
        assertEquals(planillaServiceT.getPlanilla(), new PlanillaEntity());
    }
    @Test
    void actualizarPlanillaTest(){
        PlanillaEntity nuevaPlanilla = new PlanillaEntity();
        nuevaPlanilla.setCategoria("A");
        PlanillaService planillaServiceT = new PlanillaService();
        planillaServiceT.actualizarPlanilla(nuevaPlanilla);
        assertEquals(nuevaPlanilla, planillaServiceT.getPlanilla());
    }
    @Test
    void obtenerPlanillasTest(){
        PlanillaEntity nuevaPlanilla = new PlanillaEntity();

        assertEquals(0, planillaService.obtenerPlanillas().size());
        planillaService.guardarPlanilla(nuevaPlanilla);
        assertEquals(1, planillaService.obtenerPlanillas().size());

        planillaRepository.deleteAll();
    }
    @Test
    void obtenerPlanillaAnteriorProveedorTest(){
        // Caso 1: No hay planillas guardadas
        assertEquals(-1, planillaService.obtenerPlanillaAnteriorProveedor(ID_PROVEEDOR1).getComparado());
        // Caso 2: Hay planillas guardadas
        PlanillaEntity nuevaPlanilla = new PlanillaEntity();
        nuevaPlanilla.setCodigo(ID_PROVEEDOR1);
        nuevaPlanilla.setComparado(0); // Es la ultima
        PlanillaEntity nuevaPlanilla2 = new PlanillaEntity();
        nuevaPlanilla2.setCodigo(ID_PROVEEDOR1);
        nuevaPlanilla2.setComparado(-1); 
        planillaService.guardarPlanilla(nuevaPlanilla);
        planillaService.guardarPlanilla(nuevaPlanilla2);

        assertEquals(nuevaPlanilla, planillaService.obtenerPlanillaAnteriorProveedor(ID_PROVEEDOR1));
        planillaRepository.deleteAll();
    }
    @Test
    void esLaPlanillaAnteriorTest(){
        PlanillaEntity planillaAnterior = new PlanillaEntity();
        planillaAnterior.setComparado(0);

        // Caso 1: Es la planilla anterior
        assertEquals(true, planillaService.esLaPlanillaAnterior(planillaAnterior));

        // Caso 2: No es la planilla anterior
        planillaAnterior.setComparado(-1);
        assertEquals(false, planillaService.esLaPlanillaAnterior(planillaAnterior));
    }

    // Tests PlanillaPorcentajeService

    // PPS: PlantillaPorcentajeService
    PlanillaEntity planillaTestPPS;
    PlanillaEntity planillaAnteriorTestPPS; // No tiene datos anteriores
    DatosEntity datosTestPPS;

    private void iniciarObjetosTestPPS(){
        planillaTestPPS = new PlanillaEntity();
        planillaAnteriorTestPPS = new PlanillaEntity();
        datosTestPPS = crearDatos1(ID_PROVEEDOR1); // 5000, 35, 35
    }

    @Test
    void argsTest(){
        //PlanillaEntity planilla = new PlanillaEntity();
        //PlanillaEntity planillaAnterior = new PlanillaEntity();
        //DatosEntity datos = new DatosEntity();
        //PlanillaPorcentajesService planillaPorcentajesService = new PlanillaPorcentajesService(planilla, planillaAnterior, datos);

        //datos = planillaPorcentajesService.getDatosProveedor();
        //planilla = planillaPorcentajesService.getPlanilla();
        //planillaAnterior = planillaPorcentajesService.getPlanillaAnterior();
    }
    @Test
    void setVariacionesSinPagoAnteriorTest(){
        iniciarObjetosTestPPS();
        PlanillaPorcentajesService planillaPorcentajeServiceT = new PlanillaPorcentajesService(planillaTestPPS, planillaAnteriorTestPPS, datosTestPPS);
        planillaPorcentajeServiceT.setVariacionesSinPagoAnterior();

        assertEquals(0, planillaPorcentajeServiceT.getPlanilla().getPorVariacionLeche());
        assertEquals(0, planillaPorcentajeServiceT.getPlanilla().getPorVariacionGrasa());
        assertEquals(0, planillaPorcentajeServiceT.getPlanilla().getPorVariacionSolidos());
    }
    @Test
    void setPorcentajeVariacionesTest(){
        iniciarObjetosTestPPS();
        PlanillaPorcentajesService planillaPorcentajeServiceT = new PlanillaPorcentajesService(planillaTestPPS, planillaAnteriorTestPPS, datosTestPPS);
        planillaPorcentajeServiceT.setPorcentajeVariaciones(10, 10,10);

        assertEquals(10, planillaPorcentajeServiceT.getPlanilla().getPorVariacionLeche());
        assertEquals(10, planillaPorcentajeServiceT.getPlanilla().getPorVariacionGrasa());
        assertEquals(10, planillaPorcentajeServiceT.getPlanilla().getPorVariacionSolidos());
    }
    @Test
    void calculoVariacionDeUnPorcentajeTest(){
        assertEquals(10, planillaPorcentajesService.calculoVariacionDeUnPorcentaje(20, 10));
    }
    @Test
    void calculoVariaciones(){
        PlanillaEntity planillaNueva = new PlanillaEntity();
        PlanillaEntity planillaAnterior = new PlanillaEntity();

        planillaNueva.setTotalKlsLeche(100);
        planillaNueva.setPorGrasa(10);
        planillaNueva.setPorSolidos(10);

        planillaAnterior.setTotalKlsLeche(50);
        planillaAnterior.setPorGrasa(5);
        planillaAnterior.setPorSolidos(5);

        PlanillaPorcentajesService planillaPorcentajesServiceT = new PlanillaPorcentajesService(planillaNueva, planillaAnterior, datosTestPPS);

        float resLeche;
        int resGrasa;
        int resSolido;

        // Caso 1: Variaciones negativas, resultado para todos = 0;
        
        // Primero se testea metodo por metodo
        resLeche = planillaPorcentajesServiceT.calculoVariacionLeche();
        assertEquals(-100.0, resLeche);
        resGrasa = planillaPorcentajesServiceT.calculoVariacionGrasa();
        assertEquals(-5, resGrasa);
        resSolido = planillaPorcentajesServiceT.calculoVariacionSolidos();
        assertEquals(-5, resSolido);

        // Luego se testea el metodo que los llama a todos
        planillaPorcentajesServiceT.setVariaciones();
        assertEquals(-100.0, planillaPorcentajesServiceT.getPlanilla().getPorVariacionLeche());
        assertEquals(-5.0, planillaPorcentajesServiceT.getPlanilla().getPorVariacionGrasa());
        assertEquals(-5.0, planillaPorcentajesServiceT.getPlanilla().getPorVariacionSolidos());

        // Caso 2: Hay variaciones
        PlanillaEntity planillaNueva2 = new PlanillaEntity();
        PlanillaEntity planillaAnterior2 = new PlanillaEntity();

        planillaNueva2.setTotalKlsLeche(100);
        planillaNueva2.setPorGrasa(10);
        planillaNueva2.setPorSolidos(10);

        planillaAnterior2.setTotalKlsLeche(150);
        planillaAnterior2.setPorGrasa(20);
        planillaAnterior2.setPorSolidos(20);

        PlanillaPorcentajesService planillaPorcentajesServiceT2 = new PlanillaPorcentajesService(planillaNueva2, planillaAnterior2, datosTestPPS);
        // Primero se testea metodo por metodo
        resLeche = planillaPorcentajesServiceT2.calculoVariacionLeche();
        assertEquals(33.333335876464844, resLeche);
        resGrasa = planillaPorcentajesServiceT2.calculoVariacionGrasa();
        assertEquals(10.0, resGrasa);
        resSolido = planillaPorcentajesServiceT2.calculoVariacionSolidos();
        assertEquals(10.0, resSolido);

        // Luego se testea el metodo que los llama a todos
        planillaPorcentajesServiceT2.setVariaciones();
        assertEquals(33.333335876464844, planillaPorcentajesServiceT2.getPlanilla().getPorVariacionLeche());
        assertEquals(10.0, planillaPorcentajesServiceT2.getPlanilla().getPorVariacionGrasa());
        assertEquals(10.0, planillaPorcentajesServiceT2.getPlanilla().getPorVariacionSolidos());

    }
    @Test
    void setCaracteristicasTest(){
        iniciarObjetosTestPPS();
        PlanillaPorcentajesService planillaPorcentajeServiceT = new PlanillaPorcentajesService(planillaTestPPS, planillaAnteriorTestPPS, datosTestPPS);
        planillaPorcentajeServiceT.setCaracteristicas();

        assertEquals(35, planillaPorcentajeServiceT.getPlanilla().getPorGrasa());
        assertEquals(35, planillaPorcentajeServiceT.getPlanilla().getPorSolidos());
    }
    @Test
    void analizarDatosTest(){
        PlanillaEntity planillaNueva = new PlanillaEntity();
        PlanillaEntity planillaAnterior = new PlanillaEntity();
        DatosEntity datosTestPPS = crearDatos1(ID_PROVEEDOR1);

        planillaNueva.setTotalKlsLeche(100);
        planillaNueva.setPorGrasa(10);
        planillaNueva.setPorSolidos(10);
        planillaAnterior.setComparado(-1);

        PlanillaPorcentajesService planillaPorcentajesServiceT = new PlanillaPorcentajesService(planillaNueva, planillaAnterior, datosTestPPS);
        planillaPorcentajesServiceT.analizarDatos();

        assertEquals(35, planillaPorcentajesServiceT.getPlanilla().getPorGrasa());
        assertEquals(35, planillaPorcentajesServiceT.getPlanilla().getPorSolidos());
        assertEquals(0, planillaPorcentajesServiceT.getPlanilla().getPorVariacionLeche());
        assertEquals(0, planillaPorcentajesServiceT.getPlanilla().getPorVariacionGrasa());
        assertEquals(0, planillaPorcentajesServiceT.getPlanilla().getPorVariacionSolidos());
    }

    // Tests PlanillaEntregasService
    @Test
    void allArgsConstructorTest(){
        PlanillaEntity planilla = new PlanillaEntity();
        planilla.setCodigo("A");
        int klsLeche = 10;
        int turnoManana = 1;
        int turnoTarde = 0;
        int queTurnos = 0;
        PlanillaEntregasService planillaEntregasService = new PlanillaEntregasService(planilla,klsLeche,turnoManana,turnoTarde,queTurnos);

        assertEquals("A", planillaEntregasService.getPlanilla().getCodigo());
        assertEquals(10, planillaEntregasService.getTotalKlsLecheEntregados());
        assertEquals(1, planillaEntregasService.getTurnoManana());
        assertEquals(0, planillaEntregasService.getTurnoTarde());
        assertEquals(0, planillaEntregasService.getQueTurnos());

    }
    @Test
    void setPromedioKlsEntregadosTest(){
        PlanillaEntity planilla = new PlanillaEntity();
        planilla.setTotalKlsLeche(1000);
        PlanillaEntregasService planillaEntregasService = new PlanillaEntregasService(planilla);
        // Caso 1: cantidadEntregas != 0
        planillaEntregasService.setPromedioKlsEntregados(10);
        
        assertEquals(100, planillaEntregasService.getPlanilla().getPromedioDiarioKls());

        // Caso 2: cantidadEntregas == 0
        planilla.setTotalKlsLeche(0);
        planillaEntregasService.setPlanilla(planilla);
        planillaEntregasService.setPromedioKlsEntregados(0);

        assertEquals(0, planillaEntregasService.getPlanilla().getPromedioDiarioKls());
    }
    @Test
    void setDatosPlanillaTest(){
        int frecuencia = 10;
        PlanillaEntity planilla = new PlanillaEntity();
        PlanillaEntregasService planillaEntregasService = new PlanillaEntregasService(planilla);
        planillaEntregasService.setTotalKlsLecheEntregados(1000);
        planillaEntregasService.setQueTurnos(2);
        planillaEntregasService.setDatosPlanilla(frecuencia);

        assertEquals(1000, planillaEntregasService.getPlanilla().getTotalKlsLeche());
        assertEquals(2, planillaEntregasService.getPlanilla().getQueTurnos());
        assertEquals(10, planillaEntregasService.getPlanilla().getFrecuencia());
    }
    @Test
    void queTurnosEntregaTest(){
        PlanillaEntity planilla = new PlanillaEntity();
        
        // Caso 1: Ambos turnos se entrega
        PlanillaEntregasService planillaEntregasService = new PlanillaEntregasService(planilla);
        planillaEntregasService.setTurnoManana(1);
        planillaEntregasService.setTurnoTarde(1);
        planillaEntregasService.queTurnosEntrega();
        assertEquals(3, planillaEntregasService.getQueTurnos());

        // Caso 2: Se entrega solo turno mañana
        planillaEntregasService = new PlanillaEntregasService(planilla);
        planillaEntregasService.setTurnoManana(1);
        planillaEntregasService.setTurnoTarde(0);
        planillaEntregasService.queTurnosEntrega();
        assertEquals(2, planillaEntregasService.getQueTurnos());

        // Caso 3: Se entrega solo turno tarde
        planillaEntregasService = new PlanillaEntregasService(planilla);
        planillaEntregasService.setTurnoManana(0);
        planillaEntregasService.setTurnoTarde(1);
        planillaEntregasService.queTurnosEntrega();
        assertEquals(1, planillaEntregasService.getQueTurnos());

        // Caso 4: no entrega
        planillaEntregasService = new PlanillaEntregasService(planilla);
        planillaEntregasService.setTurnoManana(0);
        planillaEntregasService.setTurnoTarde(0);
        planillaEntregasService.queTurnosEntrega();
        assertEquals(0, planillaEntregasService.getQueTurnos());
    }
    @Test
    void analizarTurnosTest(){
        PlanillaEntity planilla = new PlanillaEntity();
        PlanillaEntregasService planillaEntregasService = new PlanillaEntregasService(planilla);
        AcopioEntity acopio = new AcopioEntity();
        String turno;

    
        // Caso 1: Turno mañana
        turno = "M";
        acopio.setTurno(turno);
        planillaEntregasService.analizarTurno(acopio);
        assertEquals(1, planillaEntregasService.getTurnoManana());
        assertEquals(0, planillaEntregasService.getTurnoTarde());

        // Caso 2: Turno tarde
        turno = "T";
        acopio.setTurno(turno);
        planillaEntregasService.analizarTurno(acopio);
        assertEquals(1, planillaEntregasService.getTurnoManana());
        assertEquals(1, planillaEntregasService.getTurnoTarde());

        // 'Reseteo de datos'
        planillaEntregasService.setTurnoManana(0);
        planillaEntregasService.setTurnoTarde(0);

        // Caso 3: Ningun turno
        turno = "N";
        acopio.setTurno(turno);
        planillaEntregasService.analizarTurno(acopio);
        assertEquals(0, planillaEntregasService.getTurnoManana());
        assertEquals(0, planillaEntregasService.getTurnoTarde());
    }
    @Test
    void sumarLecheEntregadaTest(){
        AcopioEntity acopio = new AcopioEntity();
        acopio.setKls_leche("100");
        PlanillaEntity planilla = new PlanillaEntity();
        PlanillaEntregasService planillaEntregasService = new PlanillaEntregasService(planilla);
        planillaEntregasService.sumarLecheEntregada(acopio);
        assertEquals(100, planillaEntregasService.getTotalKlsLecheEntregados());
        planillaEntregasService.sumarLecheEntregada(acopio);
        planillaEntregasService.sumarLecheEntregada(acopio);
        planillaEntregasService.sumarLecheEntregada(acopio);
        assertEquals(400, planillaEntregasService.getTotalKlsLecheEntregados());
    }
    @Test
    void analizarAcopiosTest(){
        ArrayList<AcopioEntity> acopios = new ArrayList<>();
        AcopioEntity acopio1 = new AcopioEntity();
        acopio1.setId_proveedor("1");
        acopio1.setFecha("2021-01-01");
        acopio1.setKls_leche("100");
        acopio1.setTurno("M");
        AcopioEntity acopio2 = new AcopioEntity();
        acopio2.setId_proveedor("1");
        acopio2.setFecha("2021-01-01");
        acopio2.setKls_leche("200");
        acopio2.setTurno("M");
        PlanillaEntregasService planillaEntregasService = new PlanillaEntregasService(new PlanillaEntity());
        acopios.add(acopio1);
        acopios.add(acopio2);
        planillaEntregasService.analizarAcopios(acopios);

        assertEquals(300, planillaEntregasService.getTotalKlsLecheEntregados());
        assertEquals(2, planillaEntregasService.getQueTurnos());
        assertEquals(1, planillaEntregasService.getTurnoManana());
        assertEquals(0, planillaEntregasService.getTurnoTarde());
    }

    // Tests PlanillaDescuentosService
    @Test
    void descuentoPorSolidosTest(){
        PlanillaDescuentosService planillaDescuentosService = new PlanillaDescuentosService();
        PlanillaEntity planilla = new PlanillaEntity();
        PlanillaEntity planillaAnterior = new PlanillaEntity();
        planillaAnterior.setComparado(2);
        planillaDescuentosService.setPlanillaAnterior(planillaAnterior);
        assertEquals(2, planillaDescuentosService.getPlanillaAnterior().getComparado());
        double resultado;
        // Caso 1: entre 0 y 6
        planilla.setPorVariacionSolidos(0);
        planillaDescuentosService.setPlanilla(planilla);
        resultado = planillaDescuentosService.descuentoPorSolidos();
        assertEquals(0, resultado);
        planilla.setPorVariacionSolidos(5);
        planillaDescuentosService.setPlanilla(planilla);
        resultado = planillaDescuentosService.descuentoPorSolidos();
        assertEquals(0, resultado);
        planilla.setPorVariacionSolidos(6);
        planillaDescuentosService.setPlanilla(planilla);
        resultado = planillaDescuentosService.descuentoPorSolidos();
        assertEquals(0, resultado);
        // Caso 2: entre 7 y 12
        planilla.setPorVariacionSolidos(7);
        planillaDescuentosService.setPlanilla(planilla);
        resultado = planillaDescuentosService.descuentoPorSolidos();
        assertEquals(0.18, resultado);
        planilla.setPorVariacionSolidos(10);
        planillaDescuentosService.setPlanilla(planilla);
        resultado = planillaDescuentosService.descuentoPorSolidos();
        assertEquals(0.18, resultado);
        planilla.setPorVariacionSolidos(12);
        planillaDescuentosService.setPlanilla(planilla);
        resultado = planillaDescuentosService.descuentoPorSolidos();
        assertEquals(0.18, resultado);
        // Caso 3: entre 13 y 35
        planilla.setPorVariacionSolidos(13);
        planillaDescuentosService.setPlanilla(planilla);
        resultado = planillaDescuentosService.descuentoPorSolidos();
        assertEquals(0.27, resultado);
        planilla.setPorVariacionSolidos(20);
        planillaDescuentosService.setPlanilla(planilla);
        resultado = planillaDescuentosService.descuentoPorSolidos();
        assertEquals(0.27, resultado);
        planilla.setPorVariacionSolidos(35);
        planillaDescuentosService.setPlanilla(planilla);
        resultado = planillaDescuentosService.descuentoPorSolidos();
        assertEquals(0.27, resultado);
        // Caso 4: mayor o igual a 36
        planilla.setPorVariacionSolidos(36);
        planillaDescuentosService.setPlanilla(planilla);
        resultado = planillaDescuentosService.descuentoPorSolidos();
        assertEquals(0.45, resultado);
        planilla.setPorVariacionSolidos(40);
        planillaDescuentosService.setPlanilla(planilla);
        resultado = planillaDescuentosService.descuentoPorSolidos();
        assertEquals(0.45, resultado);
        // Caso 5: menor a 0
        planilla.setPorVariacionSolidos(-1);
        planillaDescuentosService.setPlanilla(planilla);
        resultado = planillaDescuentosService.descuentoPorSolidos();
        assertEquals(0, resultado);
    }   
    @Test
    void descuentoPorGrasaTest(){
        PlanillaDescuentosService planillaDescuentosService = new PlanillaDescuentosService();
        PlanillaEntity planilla = new PlanillaEntity();
        double resultado;
        // Caso 1: entre 0 y 15 = retornar 0
        planilla.setPorVariacionGrasa(0);
        planillaDescuentosService.setPlanilla(planilla);
        resultado = planillaDescuentosService.descuentoPorGrasa();
        assertEquals(0, resultado);
        planilla.setPorVariacionGrasa(10);
        planillaDescuentosService.setPlanilla(planilla);
        resultado = planillaDescuentosService.descuentoPorGrasa();
        assertEquals(0, resultado);
        planilla.setPorVariacionGrasa(15);
        planillaDescuentosService.setPlanilla(planilla);
        resultado = planillaDescuentosService.descuentoPorGrasa();
        assertEquals(0, resultado);
        // Caso 2: entre 16 y 25 = retornar 0.12
        planilla.setPorVariacionGrasa(20);
        planillaDescuentosService.setPlanilla(planilla);
        resultado = planillaDescuentosService.descuentoPorGrasa();
        assertEquals(0.12, resultado);
        // Caso 3: entre 26 y 40 = retornar 0.20
        planilla.setPorVariacionGrasa(30);
        planillaDescuentosService.setPlanilla(planilla);
        resultado = planillaDescuentosService.descuentoPorGrasa();
        assertEquals(0.20, resultado);
        // Caso 4: mayor o igual a 41 = retornar 0.30
        planilla.setPorVariacionGrasa(41);
        planillaDescuentosService.setPlanilla(planilla);
        resultado = planillaDescuentosService.descuentoPorGrasa();
        assertEquals(0.30, resultado);
        // Caso 5: menor a 0
        planilla.setPorVariacionGrasa(-1);
        planillaDescuentosService.setPlanilla(planilla);
        resultado = planillaDescuentosService.descuentoPorGrasa();
        assertEquals(0, resultado);
    }
    @Test
    void descuentoPorLecheTest(){
        PlanillaDescuentosService planillaDescuentosService = new PlanillaDescuentosService();
        PlanillaEntity planilla = new PlanillaEntity();
        double resultado;
        // Caso 1: entre 0 y 8 = retornar 0
        planilla.setPorVariacionLeche(5);
        planillaDescuentosService.setPlanilla(planilla);
        resultado = planillaDescuentosService.descuentoPorLeche();
        assertEquals(0, resultado);
        // Caso 2: entre 9 y 25 = retornar 0.07
        planilla.setPorVariacionLeche(20);
        planillaDescuentosService.setPlanilla(planilla);
        resultado = planillaDescuentosService.descuentoPorLeche();
        assertEquals(0.07, resultado);
        // Caso 3: entre 26 y 45 = retornar 0.15
        planilla.setPorVariacionLeche(30);
        planillaDescuentosService.setPlanilla(planilla);
        resultado = planillaDescuentosService.descuentoPorLeche();
        assertEquals(0.15, resultado);
        // Caso 4: mayor o igual a 46 = retornar 0.30
        planilla.setPorVariacionLeche(46);
        planillaDescuentosService.setPlanilla(planilla);
        resultado = planillaDescuentosService.descuentoPorLeche();
        assertEquals(0.30, resultado);
        // Caso 5: menor a 0
        planilla.setPorVariacionLeche(-1);
        planillaDescuentosService.setPlanilla(planilla);
        resultado = planillaDescuentosService.descuentoPorLeche();
        assertEquals(0, resultado);
    }
    @Test
    void calcularDescuentoPorSolidosTest(){
        PlanillaDescuentosService planillaDescuentosService = new PlanillaDescuentosService();
        PlanillaEntity planilla = new PlanillaEntity();
        planilla.setPagoAcopio(10000);
        planilla.setPorVariacionSolidos(20);
        planillaDescuentosService.setPlanilla(planilla);
        int resultado = planillaDescuentosService.calcularDescuentoPorSolidos();
        int valorEsperado = 2700; // 10000 * 0.27
        assertEquals(valorEsperado, resultado);
    }
    @Test
    void calcularDescuentoPorGrasaTest(){
        PlanillaDescuentosService planillaDescuentosService = new PlanillaDescuentosService();
        PlanillaEntity planilla = new PlanillaEntity();
        planilla.setPagoAcopio(10000);
        planilla.setPorVariacionGrasa(20);
        planillaDescuentosService.setPlanilla(planilla);
        int resultado = planillaDescuentosService.calcularDescuentoPorGrasa();
        int valorEsperado = 1200; // 10000 * 0.12
        assertEquals(valorEsperado, resultado);
    }
    @Test
    void calcularDescuentoPorLecheTest(){
        PlanillaDescuentosService planillaDescuentosService = new PlanillaDescuentosService();
        PlanillaEntity planilla = new PlanillaEntity();
        planilla.setPagoAcopio(10000);
        planilla.setPorVariacionLeche(20);
        planillaDescuentosService.setPlanilla(planilla);
        int resultado = planillaDescuentosService.calcularDescuentoPorLeche();
        int valorEsperado = 700; // 10000 * 0.07
        assertEquals(valorEsperado, resultado);
    }
    @Test
    void setDescuentosSinPagoAnteriorTest(){
        PlanillaEntity planilla = new PlanillaEntity();
        PlanillaDescuentosService planillaDescuentosService = new PlanillaDescuentosService();
        planillaDescuentosService.setPlanilla(planilla);
        planillaDescuentosService.setDescuentosSinPagoAnterior();
        assertEquals(0, planilla.getDctoVariacionLeche());
        assertEquals(0, planilla.getDctoVariacionGrasa());
        assertEquals(0, planilla.getDctoVariacionSolidos());
    }
    @Test
    void obtenerDescuentosConPlanillaAnterior(){
        PlanillaEntity planilla = new PlanillaEntity();
        PlanillaDescuentosService planillaDescuentosService = new PlanillaDescuentosService();
        planilla.setPagoAcopio(10000);
        planilla.setPorVariacionSolidos(20);
        planilla.setPorVariacionGrasa(20);
        planilla.setPorVariacionLeche(20);
        planillaDescuentosService.setPlanilla(planilla);
        planillaDescuentosService.obtenerDescuentosConPlanillaAnterior();

        assertEquals(2700, planilla.getDctoVariacionSolidos());
        assertEquals(1200, planilla.getDctoVariacionGrasa());
        assertEquals(700, planilla.getDctoVariacionLeche());
    }
    @Test
    void setDescuentosTest(){
        // Caso 1: Con planilla anterior
        PlanillaEntity planilla = new PlanillaEntity();
        PlanillaEntity planillaAnterior = new PlanillaEntity();
        planillaAnterior.setComparado(0);
        PlanillaDescuentosService planillaDescuentosService = new PlanillaDescuentosService();
        planilla.setPagoAcopio(10000);
        planilla.setPorVariacionSolidos(20);
        planilla.setPorVariacionGrasa(20);
        planilla.setPorVariacionLeche(20);
        planillaDescuentosService.setPlanilla(planilla);
        planillaDescuentosService.setPlanillaAnterior(planillaAnterior);

        planillaDescuentosService.setDescuentos();
        assertEquals(2700, planilla.getDctoVariacionSolidos());
        assertEquals(1200, planilla.getDctoVariacionGrasa());
        assertEquals(700, planilla.getDctoVariacionLeche());
        
        // Caso 2: Sin planilla anterior
        planilla = new PlanillaEntity();
        planillaAnterior = new PlanillaEntity();
        planillaAnterior.setComparado(-1);
        planillaDescuentosService = new PlanillaDescuentosService(planilla, planillaAnterior);
        planilla.setPagoAcopio(10000);
        planilla.setPorVariacionSolidos(20);
        planilla.setPorVariacionGrasa(20);
        planilla.setPorVariacionLeche(20);

        planillaDescuentosService.setDescuentos();
        assertEquals(0, planilla.getDctoVariacionSolidos());
        assertEquals(0, planilla.getDctoVariacionGrasa());
        assertEquals(0, planilla.getDctoVariacionLeche());
    }
    @Test
    void analizarDescuentosTest(){
        // Caso 1: Con plantilla anterior
        PlanillaEntity planilla = new PlanillaEntity();
        PlanillaEntity planillaAnterior = new PlanillaEntity();
        planillaAnterior.setComparado(0);
        PlanillaDescuentosService planillaDescuentosService = new PlanillaDescuentosService();
        planilla.setPagoAcopio(10000);
        planilla.setPorVariacionSolidos(20);
        planilla.setPorVariacionGrasa(20);
        planilla.setPorVariacionLeche(20);
        planillaDescuentosService.setPlanilla(planilla);
        planillaDescuentosService.setPlanillaAnterior(planillaAnterior);

        planillaDescuentosService.analizarDescuentos();
        assertEquals(2700, planilla.getDctoVariacionSolidos());
        assertEquals(1200, planilla.getDctoVariacionGrasa());
        assertEquals(700, planilla.getDctoVariacionLeche());

        // Caso 2: Sin planilla anterior
        planilla = new PlanillaEntity();
        planillaAnterior = new PlanillaEntity();
        planillaAnterior.setComparado(-1);
        planillaDescuentosService = new PlanillaDescuentosService(planilla, planillaAnterior);
        planilla.setPagoAcopio(10000);
        planilla.setPorVariacionSolidos(20);
        planilla.setPorVariacionGrasa(20);
        planilla.setPorVariacionLeche(20);

        planillaDescuentosService.analizarDescuentos();
        assertEquals(0, planilla.getDctoVariacionSolidos());
        assertEquals(0, planilla.getDctoVariacionGrasa());
        assertEquals(0, planilla.getDctoVariacionLeche());
    }

    // Tests PlanillaPagosService
    @Test
    void argsTest2(){
        PlanillaEntity planilla = new PlanillaEntity();
        PlanillaEntity planillaAnterior = new PlanillaEntity();
        PlanillaPagosService planillaPagosService = new PlanillaPagosService(planilla, planillaAnterior);
        assertEquals(planilla, planillaPagosService.getPlanilla());
        assertEquals(planillaAnterior, planillaPagosService.getPlanillaAnterior());

        //PlanillaDescuentosService planillaDescuentosService = planillaPagosService.getPlanillaDescuentosService();
    }
    @Test
    void actualizarPlanillaTest2(){
        PlanillaEntity planilla = new PlanillaEntity();
        PlanillaEntity planillaAnterior = new PlanillaEntity();
        PlanillaEntity planillaNueva = new PlanillaEntity();
        planilla.setComparado(0);
        planillaNueva.setComparado(-1);

        PlanillaPagosService planillaPagosService = new PlanillaPagosService(planilla, planillaAnterior);
        planillaPagosService.actualizarPlanilla(planillaNueva);

        assertEquals(-1, planillaPagosService.getPlanilla().getComparado());
    }
    @Test
    void pagoPorSolidosTest(){
        PlanillaEntity planilla = new PlanillaEntity();
        PlanillaPagosService planillaPagosService = new PlanillaPagosService();
        int resultado;
        planilla.setPorSolidos(5);
        planillaPagosService.setPlanilla(planilla);
        resultado = planillaPagosService.pagoPorSolidos();
        assertEquals(-130, resultado);

        planilla.setPorSolidos(15);
        planillaPagosService.setPlanilla(planilla);
        resultado = planillaPagosService.pagoPorSolidos();
        assertEquals(-90, resultado);

        planilla.setPorSolidos(30);
        planillaPagosService.setPlanilla(planilla);
        resultado = planillaPagosService.pagoPorSolidos();
        assertEquals(95, resultado);

        planilla.setPorSolidos(36);
        planillaPagosService.setPlanilla(planilla);
        resultado = planillaPagosService.pagoPorSolidos();
        assertEquals(150, resultado);

        planilla.setPorSolidos(-1);
        planillaPagosService.setPlanilla(planilla);
        resultado = planillaPagosService.pagoPorSolidos();
        assertEquals(0, resultado);
    }
    @Test
    void pagoPorGrasaTest(){
        PlanillaEntity planilla = new PlanillaEntity();
        PlanillaPagosService planillaPagosService = new PlanillaPagosService();
        int resultado;

        planilla.setPorGrasa(10);
        planillaPagosService.setPlanilla(planilla);
        resultado = planillaPagosService.pagoPorGrasa();
        assertEquals(30, resultado);

        planilla.setPorGrasa(30);
        planillaPagosService.setPlanilla(planilla);
        resultado = planillaPagosService.pagoPorGrasa();
        assertEquals(80, resultado);

        planilla.setPorGrasa(46);
        planillaPagosService.setPlanilla(planilla);
        resultado = planillaPagosService.pagoPorGrasa();
        assertEquals(120, resultado);

        planilla.setPorGrasa(-1);
        planillaPagosService.setPlanilla(planilla);
        resultado = planillaPagosService.pagoPorGrasa();
        assertEquals(0, resultado);
    }
    @Test
    void pagoPorCategoriaTest(){
        PlanillaEntity planilla = new PlanillaEntity();
        PlanillaPagosService planillaPagosService = new PlanillaPagosService();
        int resultado;
        planilla.setCategoria("A");
        planillaPagosService.setPlanilla(planilla);
        resultado = planillaPagosService.pagoPorCategoria();
        assertEquals(700, resultado);

        planilla.setCategoria("B");
        planillaPagosService.setPlanilla(planilla);
        resultado = planillaPagosService.pagoPorCategoria();
        assertEquals(550, resultado);

        planilla.setCategoria("C");
        planillaPagosService.setPlanilla(planilla);
        resultado = planillaPagosService.pagoPorCategoria();
        assertEquals(400, resultado);

        planilla.setCategoria("D");
        planillaPagosService.setPlanilla(planilla);
        resultado = planillaPagosService.pagoPorCategoria();
        planilla.setCategoria("");

        planillaPagosService.setPlanilla(planilla);
        resultado = planillaPagosService.pagoPorCategoria();
        assertEquals(0, resultado);
    }
    @Test
    void multiplicadorBonoFrecuencia(){
        PlanillaEntity planilla = new PlanillaEntity();
        PlanillaPagosService planillaPagosService = new PlanillaPagosService();
        double resultado;
        
        planilla.setQueTurnos(3);
        planillaPagosService.setPlanilla(planilla);
        resultado = planillaPagosService.multiplicadorBonoFrecuencia();
        assertEquals(1.20, resultado);
        
        planilla.setQueTurnos(2);
        planillaPagosService.setPlanilla(planilla);
        resultado = planillaPagosService.multiplicadorBonoFrecuencia();
        assertEquals(1.12, resultado);
        
        planilla.setQueTurnos(1);
        planillaPagosService.setPlanilla(planilla);
        resultado = planillaPagosService.multiplicadorBonoFrecuencia();
        assertEquals(1.08, resultado);

        planilla.setQueTurnos(4);
        planillaPagosService.setPlanilla(planilla);
        resultado = planillaPagosService.multiplicadorBonoFrecuencia();
        assertEquals(0, resultado);
    }
    @Test
    void calcularBonoFrecuenciaTest(){
        int pagoPorAcopio = 10000;
        int frecuencia = 3;
        PlanillaEntity planilla = new PlanillaEntity();
        PlanillaEntity planillaAnterior = new PlanillaEntity();
        planilla.setQueTurnos(frecuencia);
        PlanillaPagosService planillaPagosService = new PlanillaPagosService(planilla, planillaAnterior);
        
        planillaPagosService.calcularBonoFrecuencia(pagoPorAcopio);

        assertEquals(2000, planilla.getBonoFrecuencia());
    }
    @Test
    void setBonoFrecuienciaTest(){
        int bono = 20000;
        PlanillaEntity planilla = new PlanillaEntity();
        PlanillaEntity planillaAnterior = new PlanillaEntity();
        PlanillaPagosService planillaPagosService = new PlanillaPagosService(planilla, planillaAnterior);

        planillaPagosService.setBonoFrecuencia(bono);

        assertEquals(bono, planillaPagosService.getPlanilla().getBonoFrecuencia());
    }
    @Test
    void setPagoAcopioTest(){
        int pago = 20000;
        PlanillaEntity planilla = new PlanillaEntity();
        PlanillaEntity planillaAnterior = new PlanillaEntity();
        PlanillaPagosService planillaPagosService = new PlanillaPagosService(planilla, planillaAnterior);

        planillaPagosService.setPagoAcopio(pago);

        assertEquals(pago, planillaPagosService.getPlanilla().getPagoAcopio());
    }
    @Test
    void calcularPagoAcopio(){
        PlanillaEntity planilla = new PlanillaEntity();
        PlanillaEntity planillaAnterior = new PlanillaEntity();
        int totalKlsLeche = 1000;
        String categoria = "A";
        int porGrasa = 10;
        int porSolidos = 10;
        int queTurnos = 3;
        planilla.setTotalKlsLeche(totalKlsLeche);
        planilla.setCategoria(categoria);
        planilla.setPorGrasa(porGrasa);
        planilla.setPorSolidos(porSolidos);
        planilla.setQueTurnos(queTurnos);

        PlanillaPagosService planillaPagosService = new PlanillaPagosService(planilla, planillaAnterior);
        planillaPagosService.calcularPagoAcopio();

        assertEquals(768000, planillaPagosService.getPlanilla().getPagoAcopio());
    }
    @Test
    void calculoPagoTotalTest(){
        PlanillaEntity planilla = new PlanillaEntity();
        PlanillaPagosService planillaPagosService = new PlanillaPagosService();
        int pagoAcopio;
        int dctoLeche;
        int dctoGrasa;
        int dctoSolidos;

        // Caso 1: resultado mayor a 0
        pagoAcopio = 1000;
        dctoLeche = 100;
        dctoGrasa = 100;
        dctoSolidos = 100;
        planilla.setPagoAcopio(pagoAcopio);
        planilla.setDctoVariacionLeche(dctoLeche);
        planilla.setDctoVariacionGrasa(dctoGrasa);
        planilla.setDctoVariacionSolidos(dctoSolidos);
        planillaPagosService.setPlanilla(planilla);
        assertEquals(700, planillaPagosService.calculoPagoTotal());

        // Caso 2: resultado menor a 0
        pagoAcopio = 1000;
        dctoLeche = 1000;
        dctoGrasa = 1000;
        dctoSolidos = 1000;
        planilla.setPagoAcopio(pagoAcopio);
        planilla.setDctoVariacionLeche(dctoLeche);
        planilla.setDctoVariacionGrasa(dctoGrasa);
        planilla.setDctoVariacionSolidos(dctoSolidos);
        planillaPagosService.setPlanilla(planilla);
        assertEquals(0, planillaPagosService.calculoPagoTotal());
    }
    @Test
    void setPagoTotalTest(){
        PlanillaEntity planilla = new PlanillaEntity();
        PlanillaPagosService planillaPagosService = new PlanillaPagosService();
        int pagoAcopio = 1000;
        int dctoLeche = 100;
        int dctoGrasa = 100;
        int dctoSolidos = 100;

        planilla.setPagoAcopio(pagoAcopio);
        planilla.setDctoVariacionLeche(dctoLeche);
        planilla.setDctoVariacionGrasa(dctoGrasa);
        planilla.setDctoVariacionSolidos(dctoSolidos);
        planillaPagosService.setPlanilla(planilla);

        planillaPagosService.setPagoTotal();
        assertEquals(700, planillaPagosService.getPlanilla().getPagoTotal());
    }
    @Test
    void calculoMontoRetencionTest(){
        PlanillaEntity planilla = new PlanillaEntity();
        PlanillaPagosService planillaPagosService = new PlanillaPagosService();
        int pagoTotal = 10000;
        planilla.setPagoTotal(pagoTotal);

        planillaPagosService.setPlanilla(planilla);
        int retencion = planillaPagosService.calculoMontoRetencion();

        assertEquals(pagoTotal*0.13, retencion);
    }
    @Test
    void setMontoRetencionTest(){
        PlanillaEntity planilla = new PlanillaEntity();
        PlanillaPagosService planillaPagosService = new PlanillaPagosService();
        int pagoTotal;

        // Caso 1: pagoTotal mayor a 950000
        pagoTotal = 960000;
        planilla.setPagoTotal(pagoTotal);
        planillaPagosService.setPlanilla(planilla);
        planillaPagosService.setMontoRetencion();

        assertEquals(pagoTotal*0.13, planillaPagosService.getPlanilla().getMontoRetencion());
        
        // Caso 2: pagoTotal menor a 950000
        pagoTotal = 940000;
        planilla.setPagoTotal(pagoTotal);
        planillaPagosService.setPlanilla(planilla);
        planillaPagosService.setMontoRetencion();

        assertEquals(0, planillaPagosService.getPlanilla().getMontoRetencion());
    }
    @Test
    void calculoPagoFinalTest(){
        PlanillaEntity planilla = new PlanillaEntity();
        PlanillaPagosService planillaPagosService = new PlanillaPagosService();
        int pagoTotal = 10000;
        int montoRetencion = 1000;
        planilla.setPagoTotal(pagoTotal);
        planilla.setMontoRetencion(montoRetencion);

        planillaPagosService.setPlanilla(planilla);
        int pagoFinal = planillaPagosService.calculoPagoFinal();

        assertEquals(pagoTotal-montoRetencion, pagoFinal);
    }
    @Test
    void setPagoFinalTest(){
        PlanillaEntity planilla = new PlanillaEntity();
        PlanillaPagosService planillaPagosService = new PlanillaPagosService();
        int pagoTotal = 10000;
        int montoRetencion = 1000;
        planilla.setPagoTotal(pagoTotal);
        planilla.setMontoRetencion(montoRetencion);

        planillaPagosService.setPlanilla(planilla);
        planillaPagosService.setPagoFinal();

        assertEquals(pagoTotal-montoRetencion, planillaPagosService.getPlanilla().getPagoFinal());
    }
    @Test
    void analizarPagosTest(){
        PlanillaEntity planilla = new PlanillaEntity();
        PlanillaEntity planillaAnterior = new PlanillaEntity();
        int totalKlsLeche = 1000;
        String categoria = "A";
        int porGrasa = 10;
        int porSolidos = 10;
        int queTurnos = 3;
        planilla.setTotalKlsLeche(totalKlsLeche);
        planilla.setCategoria(categoria);
        planilla.setPorGrasa(porGrasa);
        planilla.setPorSolidos(porSolidos);
        planilla.setQueTurnos(queTurnos);

        PlanillaPagosService planillaPagosService = new PlanillaPagosService(planilla, planillaAnterior);
        planillaPagosService.analizarPagos();

        assertEquals(768000, planillaPagosService.getPlanilla().getPagoAcopio());
        assertEquals(768000, planillaPagosService.getPlanilla().getPagoTotal());
        assertEquals(0, planillaPagosService.getPlanilla().getMontoRetencion());
        assertEquals(768000, planillaPagosService.getPlanilla().getPagoFinal());
    }
}