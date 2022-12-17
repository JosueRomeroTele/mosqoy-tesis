package com.example.sw2.controller.admin;

import com.example.sw2.repository.VentasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

@Controller
@RequestMapping("/admin/reportes")
public class AdminReportesController {

    @Autowired
    VentasRepository ventasRepository;

    @GetMapping(value = "")
    public String listaReportes(){

//        return "reportes";
          return "admin/reportesEnConstruccion";
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
}
