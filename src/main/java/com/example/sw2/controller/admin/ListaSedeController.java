package com.example.sw2.controller.admin;

import com.example.sw2.entity.AsignadosSedes;
import com.example.sw2.entity.Sede;
import com.example.sw2.entity.Usuarios;
import com.example.sw2.repository.AsignadosSedesRepository;
import com.example.sw2.repository.SedeRepository;
import com.example.sw2.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/admin/sede")
public class ListaSedeController {

    @Autowired
    UsuariosRepository usuariosRepository;
    @Autowired
    SedeRepository sedeRepository;
    @Autowired
    AsignadosSedesRepository asignadosSedesRepository;

    @GetMapping(value = {""})
    public String listaSede(Model model,@ModelAttribute("Sede") Sede
                             sede){
        model.addAttribute("lista", usuariosRepository.findUsuariosByRoles_Nombrerol("sede"));
        return "admin/listaSede";
    }

    @GetMapping("lista")
    public String listaDeSedes(Model m, @ModelAttribute("Sede") Sede sede){
        m.addAttribute("listaDeSedes", sedeRepository.findAll() );
        return "admin/listaLugaresSedes";
    }

    @PostMapping("save")
    public String GuardarSede(@ModelAttribute("Sede")@Valid Sede sede,
                              BindingResult bindingResult,@RequestParam("type") int type,
                              RedirectAttributes attr,Model model){
        if (type==1 && sedeRepository.findByNombresede(sede.getNombresede()).isPresent()){
            bindingResult.rejectValue("nombresede","error.user","El nombre ya está registrado");
        }

        if (bindingResult.hasErrors()){
            model.addAttribute("formtype",Integer.toString(type));
            model.addAttribute("listaDeSedes",sedeRepository.findAll());
            model.addAttribute("errorSede","ERROR");
            return "admin/listaLugaresSedes";
        }else {
            if (type==0 && sedeRepository.findById(sede.getIdsede()).isPresent()){
                attr.addFlashAttribute("msg","La sede fue actualizado");
            }else if(type==1){
                attr.addFlashAttribute("msg","la sede se creó exitosamente");
            }else {
                attr.addFlashAttribute("errorSede","hubo problemas al guardar la sede");
                return "redirect:/admin/sede/lista";
            }
                sedeRepository.save(sede);
            return "redirect:/admin/sede/lista";
        }

    }

    @GetMapping(value = "/lista/delete")
    public String borrarSede(@ModelAttribute("sede") Sede sede,RedirectAttributes attr){
        Optional<Sede> sed = sedeRepository.findById(sede.getIdsede());
        if (sed.isPresent()){
            attr.addFlashAttribute("msg","La sede se eliminó exitosamete");
            sedeRepository.delete(sede);
        }else {
            attr.addFlashAttribute("errorSede","Hubo Problemas para borrar la sede");
        }
        return "redirect:/admin/sede/lista";
    }

    @ResponseBody
    @GetMapping(value = "/fotoUser", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<FileSystemResource> getFile(@RequestParam("id")int id) throws IOException {
        Optional<Usuarios> optProduct = usuariosRepository.findById(id);
        Usuarios user=optProduct.get();
        String path = "C:/FotosProyecto/";
        //String path = "/home/ec2-user/FotosProyecto/";
        File file = new File(path + user.getFoto());
        HttpHeaders respHeaders = new HttpHeaders();
        return new ResponseEntity<FileSystemResource>(
                new FileSystemResource(file), respHeaders, HttpStatus.OK
        );
    }

    @ResponseBody
    @GetMapping(value = "/lista/get",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Optional<Sede>> ObtenerSede(@RequestParam("id") int id){
        return new ResponseEntity<>(sedeRepository.findById(id),HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/lista/productosAsignados",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Optional<AsignadosSedes>> productosAsignado(@RequestParam("id") int id){
        Optional<Sede> sede1 = sedeRepository.findById(id);
        return new ResponseEntity<>(asignadosSedesRepository.findById_Sede(sede1.get()) ,HttpStatus.OK);
    }

}
