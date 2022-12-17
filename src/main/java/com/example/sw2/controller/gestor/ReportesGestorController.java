package com.example.sw2.controller.gestor;

import com.example.sw2.entity.*;
import com.example.sw2.repository.ComunidadesRepository;
import com.example.sw2.repository.ProductosRepository;
import com.example.sw2.repository.SedeRepository;
import com.example.sw2.repository.VentasRepository;
import com.example.sw2.service.IReporteGestorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/gestor/reportes")
public class ReportesGestorController {



    @Autowired
    IReporteGestorService IReporteGestorService;
    @Autowired
    VentasRepository ventasRepository;
    @Autowired
    SedeRepository sedeRepository;
    @Autowired
    ProductosRepository productosRepository;
    @Autowired
    ComunidadesRepository comunidadesRepository;

    @GetMapping(value = {"/"})
    public String redirectAT(){
        return "redirect:/gestor/reportes";
    }


    @GetMapping(value = "")
    public String listaReportes(){
        return "gestor/Reportes";
//        return "gestor/en_construccion";
    }

    @GetMapping("excel")
    public String vista(){
        return "gestor/ImportarExcel";
    }


    @PostMapping(value = "/generate")
    public ResponseEntity<InputStreamResource> printExcel(@RequestParam("ordenar") Integer orderBy, @RequestParam("tipo") Integer type,
                                                          @RequestParam("years") Integer anho, @RequestParam("tipoSelect") Integer select,
                                                          @RequestParam("nombreselect") String nombreSelect)
                                                            throws Exception{

        Reportes reportes = new Reportes(orderBy, anho, type,select,nombreSelect);
        HttpHeaders headers = new HttpHeaders();
        ByteArrayInputStream stream = new  ByteArrayInputStream(new byte[]{});
        String filename;
        if (reportes.validateGestor()){
            stream = IReporteGestorService.generarReporte(reportes);
            filename = reportes.createNameGestor();
        }else filename = "error";

        headers.add("Content-Disposition","attachment; filename="+ filename +".xls");
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(stream));
    }


    @ResponseBody
    @GetMapping(value = "/years")
    public ResponseEntity<ArrayList<Integer>> getYears(){
        return new ResponseEntity<>(new ArrayList<Integer>(){
            {
                this.addAll(ventasRepository.getYears());
            }
        }, HttpStatus.OK);
    }


    //REPORTE DE  GESTOR PRINCIPAL
    @ResponseBody
    @GetMapping(value = "/sedeVentas",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Sede>>getSedeConVentas(){
        return new ResponseEntity<>(sedeRepository.listSedeConVentas(),HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/productosVentas",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Productos>>getProductosConVentas(){
        return new ResponseEntity<>(productosRepository.productoConVentas(),HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/comunidadesVentas",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Comunidades>>getComunidadesConVentas(){
        return new ResponseEntity<>(comunidadesRepository.comunidadesConVentas(),HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/clientesVentas",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Ventas>>getClientesConVentas(){
        return new ResponseEntity<>(ventasRepository.clientesConVentas(),HttpStatus.OK);
    }

}
