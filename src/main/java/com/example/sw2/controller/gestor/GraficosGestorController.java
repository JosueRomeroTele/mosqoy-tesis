package com.example.sw2.controller.gestor;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/gestor/graficos")
public class GraficosGestorController {

    @GetMapping(value = {"/"})
    public String redirectAT(){
        return "redirect:/gestor/graficos";
    }


    @GetMapping(value = "")
    public String listDashboard(){
        return "gestor/graficos";

    }
}
