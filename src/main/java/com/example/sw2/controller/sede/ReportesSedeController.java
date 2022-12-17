package com.example.sw2.controller.sede;

import com.example.sw2.entity.*;
import com.example.sw2.repository.ComunidadesRepository;
import com.example.sw2.repository.ProductosRepository;
import com.example.sw2.repository.VentasRepository;
import com.example.sw2.service.IReporteSedeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/sede/reportes")
public class ReportesSedeController {


    @Autowired
    IReporteSedeService IReporteSedeService;
    @Autowired
    VentasRepository ventasRepository;
    @Autowired
    ComunidadesRepository comunidadesRepository;
    @Autowired
    ProductosRepository productosRepository;

    @Autowired

    @GetMapping(value = {"/"})
    public String redirectAT(){
        return "redirect:/sede/reportes";
    }

    @GetMapping(value = "")
    public String listaReportes(){
        return "sede/reportesSede";
//        return "sede/reportesSedeEnConstruccion";
    }



    @PostMapping(value = "/generate")
    public ResponseEntity<InputStreamResource> printExcel(@RequestParam("ordenar") Integer orderBy, @RequestParam("tipo") Integer type,
                                                          @RequestParam("years") Integer anho, @RequestParam("tipoSelect") Integer Select,
                                                          @RequestParam("nombreselect") String nombreSelect,HttpSession session) throws Exception{

        Usuarios sede = (Usuarios) session.getAttribute("usuario");
        if(orderBy>1){
            orderBy += 1;
        }
        Reportes reportes = new Reportes(orderBy,anho,type,Select,nombreSelect);
        HttpHeaders headers = new HttpHeaders();
        ByteArrayInputStream stream = new  ByteArrayInputStream(new byte[]{});
        String filename;
        if (reportes.validateSede()) {
            stream = IReporteSedeService.generarReporte(reportes, sede.getSede().getIdsede());
            filename = reportes.createNameGestor();
        }else filename="error";

        headers.add("Content-Disposition","attachment; filename="+filename+".xls");
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

    @ResponseBody
    @GetMapping("clienteVentasSede")
    public ResponseEntity<List<Ventas>>getArticulosSedeConVentas(HttpSession session){
        Usuarios sede = (Usuarios) session.getAttribute("usuario");
        return new ResponseEntity(ventasRepository.clientesConVentasEnSede(sede.getSede().getIdsede()),HttpStatus.OK);
    }
    @ResponseBody
    @GetMapping("comunidadVentasSede")
    public ResponseEntity<List<Comunidades>>getComunidadesSedeConVentas(HttpSession session){
        Usuarios sede = (Usuarios) session.getAttribute("usuario");
        return new ResponseEntity(comunidadesRepository.comunidadesConVentasEnSede(sede.getSede().getIdsede()),HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping("productoVentasSede")
    public ResponseEntity<List<Ventas>>getsProductosSedeConVentas(HttpSession session){
        Usuarios sede = (Usuarios) session.getAttribute("usuario");
        return new ResponseEntity(productosRepository.productosConVentasEnSede(sede.getSede().getIdsede()),HttpStatus.OK);
    }

}
