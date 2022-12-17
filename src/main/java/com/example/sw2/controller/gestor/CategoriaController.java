package com.example.sw2.controller.gestor;

import com.example.sw2.entity.Categorias;
import com.example.sw2.repository.CategoriasRepository;
import com.example.sw2.utils.UploadObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/gestor/categoria")
public class CategoriaController {

    @Autowired
    CategoriasRepository categoriasRepository;


    @GetMapping(value = {"/"})
    public String redirectAT(){
        return "redirect:/gestor/categoria";
    }

    @GetMapping(value = {""})
    public String listCat(@ModelAttribute("categoria") Categorias cat, Model model) {
        model.addAttribute("lista", categoriasRepository.findAll());
        return "gestor/categorias";
    }


    @PostMapping("/save")
    public String editCat(@ModelAttribute("categoria") @Valid Categorias categorias,
                          BindingResult bindingResult, @RequestParam("type") int type,
                          RedirectAttributes attr, Model model) {

        if(type==1 && categoriasRepository.findById(categorias.getCodigo()).isPresent()){ //if new
            bindingResult.rejectValue("codigo","error.user","Este codigo ya existe");
        }

        if(categoriasRepository.findCategoriasByNombre(categorias.getNombre()).isPresent()){
            bindingResult.rejectValue("nombre","error.user","Este nombre ya está registrado");
        }

        if(bindingResult.hasErrors()){
            model.addAttribute("formtype",Integer.toString(type));
            model.addAttribute("lista", categoriasRepository.findAll());
            model.addAttribute("msgError",   "ERROR");
            return "gestor/categorias";
        }
        else {
            Optional<Categorias> optionalCategorias = categoriasRepository.findById(categorias.getCodigo());
            if (optionalCategorias.isPresent() && type==0) {
                attr.addFlashAttribute("msg", "Categoría actualizada exitosamente");
            }
            else if (type==1){
                attr.addFlashAttribute("msg", "Categoría creada exitosamente");
            }
            else {
                attr.addFlashAttribute("msgError", "Hubo un problema, no se pudo guardar");
                return "redirect:/gestor/categoria";
            }
            categoriasRepository.save(categorias);
            return "redirect:/gestor/categoria";
        }
    }

    @GetMapping("/delete")
    public String deleteCat(Model model,
                                      @RequestParam("codigo") String id,
                                      RedirectAttributes attr) {
        Optional<Categorias> c = categoriasRepository.findById(id);
        if (c.isPresent()) {
            categoriasRepository.deleteById(id);
            attr.addFlashAttribute("msg","Categoría borrada exitosamente");
        }
        return "redirect:/gestor/categoria";
    }

    //Web service
    @ResponseBody
    @GetMapping(value = "/get",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Optional<Categorias>> getCat(@RequestParam(value = "id") String id){

        return new ResponseEntity<>(categoriasRepository.findById(id), HttpStatus.OK);
    }


    //Has items
    @ResponseBody
    @GetMapping(value = "/has", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<HashMap<String,String>>> hasItem(@RequestParam(value = "id") String id){
        return new ResponseEntity<>(new ArrayList<HashMap<String,String>>() {{
            Objects.requireNonNull(categoriasRepository.findById(id).orElse(null)).getInventario().forEach((i)->
            {
                add(new HashMap<String, String>() {
                    {
                        put("codigo", i.getCodigoinventario());
                        put("producto", i.getProductos().getNombre());
                        put("cantidad", Integer.toString(i.getCantidadtotal()));
                    }
                });
            });
        }},
                HttpStatus.OK);
    }


}
