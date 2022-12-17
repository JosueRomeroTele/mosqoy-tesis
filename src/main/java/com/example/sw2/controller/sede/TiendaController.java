package com.example.sw2.controller.sede;

import com.example.sw2.entity.Tienda;
import com.example.sw2.entity.Usuarios;
import com.example.sw2.repository.TiendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.*;

@Controller
@RequestMapping("/sede/tienda")
public class TiendaController {

    @Autowired
    TiendaRepository tiendaRepository;

    @GetMapping(value = {"/"})
    public String redirectAT(){
        return "redirect:/sede/tienda";
    }

    @GetMapping(value = {""})
    public String listCat(@ModelAttribute("tienda") Tienda cat, Model model, HttpSession session) {
        Usuarios sede = (Usuarios) session.getAttribute("usuario");
        model.addAttribute("lista", tiendaRepository.listaTiendaEnSedes(sede.getSede().getIdsede()));
        return "sede/tiendas";
    }

    @PostMapping("/save")
    public String editCat(@ModelAttribute("tienda") @Valid Tienda tienda,
                          BindingResult bindingResult,
                          @RequestParam("type") int type,
                          RedirectAttributes attr, Model model, HttpSession session) {
        Usuarios sede = (Usuarios) session.getAttribute("usuario");
        tienda.setSede(sede.getSede());
        if(bindingResult.hasErrors()){
            model.addAttribute("formtype",Integer.toString(type));
            model.addAttribute("lista", tiendaRepository.listaTiendaEnSedes(sede.getSede().getIdsede()));
            model.addAttribute("msgError", "ERROR");
            return "sede/tiendas";
        }
        else {
            Optional<Tienda> optionalTienda = tiendaRepository.findById(tienda.getIdtienda());
            if (optionalTienda.isPresent()) {
                attr.addFlashAttribute("msg", "Tienda actualizada exitosamente");
            }
            else {
                attr.addFlashAttribute("msg", "Tienda creada exitosamente");
            }
            tiendaRepository.save(tienda);
            return "redirect:/sede/tienda";
        }

    }

    @GetMapping("/delete")
    public String deleteCat(Model model,
                            @RequestParam("idtienda") int id,
                            RedirectAttributes attr) {

        Optional<Tienda> c = tiendaRepository.findById(id);

        if (c.isPresent()) {
            tiendaRepository.deleteById(id);
            attr.addFlashAttribute("msg","Producto borrado exitosamente");
        }
        return "redirect:/sede/tienda";
    }

    //Web service
    @ResponseBody
    @GetMapping(value = "/get",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Optional<Tienda>> getTienda(@RequestParam(value = "id") int id){
        return new ResponseEntity<>(tiendaRepository.findById(id), HttpStatus.OK);
    }

    //Has items
    @ResponseBody
    @GetMapping(value = "/has", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<HashMap<String,String>>> hasItem(@RequestParam(value = "id") int id){
        return new ResponseEntity<>(new ArrayList<HashMap<String,String>>() {{
            Objects.requireNonNull(tiendaRepository.findById(id).orElse(null)).getAsignacionTiendas().forEach((i)->
            {
                add(new HashMap<String, String>() {
                    {
                       //put("gestor", i.getAsignadosSedes().getId().getGestor().getFullname());
                        put("codigo", i.getAsignadosSedes().getId().getProductoinventario().getCodigoinventario());
                        put("stock", Integer.toString(i.getStock()));
                    }
                });
            });
        }},
                HttpStatus.OK);
    }


}
