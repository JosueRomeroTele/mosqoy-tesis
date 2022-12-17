package com.example.sw2.controller;

import com.example.sw2.entity.Inventario;
import com.example.sw2.message.ResponseMessage;
import com.example.sw2.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/api/excel")
public class excelController {
    @Autowired
    ExcelService excelService;


    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes attr) {
        String message="";
        if (ExcelHelper.hasExcelFormat(file)){
            try {
                excelService.save(file);
                message = "Subió el archivo con éxito: " + file.getOriginalFilename();
                //return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
                attr.addFlashAttribute("msg",message);
                return "redirect:/gestor/reportes/excel";
            }catch (Exception e){
                message = "No se pudo cargar el archivo: " + file.getOriginalFilename() + "! - Consulte con servicio tecnico";
               //return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
                attr.addFlashAttribute("msg",message);
                return "redirect:/gestor/reportes/excel";
            }
        }
        message = "Suba un archivo de Excel!";
        attr.addFlashAttribute("msg",message);
        //return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new  ResponseMessage(message));
        return "redirect:/gestor/reportes/excel";
    }

    @GetMapping("/invetarioList")
    public ResponseEntity<List<Inventario>> getAllTutorials() {
        try {
            List<Inventario> tutorials = excelService.getAllInventario();

            if (tutorials.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(tutorials, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
