package com.example.sw2.controller.gestor;

import com.example.sw2.entity.Artesanos;
import com.example.sw2.entity.Inventario;
import com.example.sw2.repository.ArtesanosRepository;
import com.example.sw2.repository.ComunidadesRepository;
import com.example.sw2.repository.InventarioRepository;
import com.example.sw2.repository.UsuariosRepository;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/gestor/artesano")
public class ArtesanoController {

    @Autowired
    ArtesanosRepository artesanosRepository;

    @Autowired
    ComunidadesRepository comunidadesRepository;

    @Autowired
    UsuariosRepository usuariosRepository;

    @Autowired
    InventarioRepository inventarioRepository;

    @GetMapping(value = {"/"})
    public String redirectAT(){
        return "redirect:/gestor/artesano";
    }

    @GetMapping(value = {""})
    public String listCat(@ModelAttribute("artesano") Artesanos artesanos, Model model) {
        model.addAttribute("lista", artesanosRepository.findAll());
        model.addAttribute("comunidades", comunidadesRepository.findAll());
        return "gestor/artesanos";
    }

    @PostMapping("/save")
    public String editCat(@ModelAttribute("artesano") @Valid Artesanos artesanos,
                          BindingResult bindingResult, @RequestParam("type") int type,
                          RedirectAttributes attr, Model model) {
        if(type==1 && artesanosRepository.findById(artesanos.getCodigo()).isPresent()){ //if new
            bindingResult.rejectValue("codigo","error.user","Este código ya existe");
        }


        if(bindingResult.hasErrors()){
            model.addAttribute("formtype",Integer.toString(type));
            model.addAttribute("lista", artesanosRepository.findAll());
            model.addAttribute("comunidades", comunidadesRepository.findAll());
            model.addAttribute("msgError", "ERROR");
            return "gestor/artesanos";
        }
        else {
            Optional<Artesanos> optionalArtesanos = artesanosRepository.findById(artesanos.getCodigo());
            if (optionalArtesanos.isPresent() && type==0) {
                attr.addFlashAttribute("msg", "Artesano actualizado exitosamente");
            }
            else if (type==1){
                attr.addFlashAttribute("msg", "Artesano creado exitosamente");
            }else{
                attr.addFlashAttribute("msgError", "Hubo un problema, no se pudo guardar");
                return "redirect:/gestor/artesano";
            }
            artesanosRepository.save(artesanos);
            return "redirect:/gestor/artesano";
        }

    }


    @GetMapping("/delete")
    public String deleteCat(Model model,
                            @RequestParam("codigo") String id,
                            RedirectAttributes attr) {
        Optional<Artesanos> c = artesanosRepository.findById(id);

        if (c.isPresent()) {
            artesanosRepository.deleteById(id);
            attr.addFlashAttribute("msg","Artesano borrado exitosamente");
        }
        return "redirect:/gestor/artesano";
    }

    //Web service
    @ResponseBody
    @GetMapping(value = "/get",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Optional<Artesanos>> getCat(@RequestParam(value = "id") String id){

        return new ResponseEntity<>(artesanosRepository.findById(id), HttpStatus.OK);
    }

    //Has items
    @ResponseBody
    @GetMapping(value = "/has", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<HashMap<String,String>>> hasItems(@RequestParam(value = "id") String id){
        return new ResponseEntity<>(new ArrayList<HashMap<String,String>>() {{
            Objects.requireNonNull(artesanosRepository.findById(id).orElse(null)).getInventario().forEach((i)->
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
