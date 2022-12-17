package com.example.sw2.controller.gestor;

import com.example.sw2.entity.Comunidades;
import com.example.sw2.repository.ComunidadesRepository;
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
@RequestMapping("/gestor/comunidad")
public class ComunidadController {

    @Autowired
    ComunidadesRepository comunidadesRepository;

    @GetMapping(value = {"/"})
    public String redirectAT(){
        return "redirect:/gestor/comunidad";
    }

    @GetMapping(value = {""})
    public String listCom(@ModelAttribute("comunidad") Comunidades com, Model model) {
        model.addAttribute("lista", comunidadesRepository.findAll());
        return "gestor/comunidades";
    }

    @PostMapping("/save")
    public String editCom(@ModelAttribute("comunidad") @Valid Comunidades comunidades,
                          BindingResult bindingResult, @RequestParam("type") int type,
                          RedirectAttributes attr, Model model) {

        if(type==1 && comunidadesRepository.findById(comunidades.getCodigo()).isPresent()){ //if new
            bindingResult.rejectValue("codigo","error.user","Este código ya existe");
        }

        if(comunidadesRepository.findComunidadesByNombre(comunidades.getNombre()).isPresent())
            bindingResult.rejectValue("nombre","error.user","Este nombre ya está registrado");


        if(bindingResult.hasErrors()){
            model.addAttribute("formtype",Integer.toString(type));
            model.addAttribute("lista", comunidadesRepository.findAll());
            model.addAttribute("msgError", "ERROR");
            return "gestor/comunidades";
        }
        else {
            Optional<Comunidades> optionalComunidades = comunidadesRepository.findById(comunidades.getCodigo());
            if (optionalComunidades.isPresent() && type==0) {
                Comunidades com = optionalComunidades.get();
                System.out.println(com.getCodigo());
                attr.addFlashAttribute("msg", "Comunidad actualizada exitosamente");
            }
            else if (type==1){
                attr.addFlashAttribute("msg", "Comunidad creada exitosamente");
            }
            else{
                attr.addFlashAttribute("msgError", "Hubo un problema, no se pudo guardar");
                return "redirect:/gestor/comunidad";
            }
            comunidadesRepository.save(comunidades);
            return "redirect:/gestor/comunidad";
        }
    }

    @GetMapping("/delete")
    public String deleteCom(Model model,
                            @RequestParam("codigo") String id,
                            RedirectAttributes attr) {
        Optional<Comunidades> c = comunidadesRepository.findById(id);
        if (c.isPresent()) {
            /* :(try{
                comunidadesRepository.deleteById(id);
                attr.addFlashAttribute("msg","Comunidad borrada exitosamente");
            }
            catch (Exception e){
                attr.addFlashAttribute("msg", "Esta comunidad no se puede borrar, se está usando");
            }*/
            comunidadesRepository.deleteById(id);
            attr.addFlashAttribute("msg","Comunidad borrada exitosamente");

        }
        return "redirect:/gestor/comunidad";
    }

    //Web service
    @ResponseBody
    @GetMapping(value = "/get",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Optional<Comunidades>> getCom(@RequestParam(value = "id") String id){
        return new ResponseEntity<>(comunidadesRepository.findById(id), HttpStatus.OK);
    }

    //Has items
    @ResponseBody
    @GetMapping(value = "/has", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<HashMap<String,String>>> hasItems(@RequestParam(value = "id") String id){
        return new ResponseEntity<>(new ArrayList<HashMap<String,String>>() {{
            Objects.requireNonNull(comunidadesRepository.findById(id).orElse(null)).getArtesanos().forEach((i)->
            {
                add(new HashMap<String, String>() {
                    {
                        put("codigo", i.getCodigo());
                        put("nombre", i.getNombre());
                        put("apellido", i.getApellidopaterno());
                    }
                });
            });
        }},
                HttpStatus.OK);
    }
}
