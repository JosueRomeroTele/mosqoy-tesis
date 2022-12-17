package com.example.sw2.controller.gestor;


import com.example.sw2.Dao.StorageServiceDao;
import com.example.sw2.entity.*;
import com.example.sw2.repository.AsignadosSedesRepository;
import com.example.sw2.repository.SedeRepository;
import com.example.sw2.repository.UsuariosRepository;
import com.example.sw2.utils.CustomMailService;
import com.example.sw2.utils.ExceptionView;
import com.example.sw2.utils.UploadObject;
import com.sun.istack.Nullable;
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
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/gestor/sede")
public class ListaSedeGestorController {

    private final int ROL_CRUD = 3; // rol al que se le hace el crud

    @Autowired
    UsuariosRepository usuariosRepository;
    @Autowired
    AsignadosSedesRepository asignadosSedesRepository;
    @Autowired
    CustomMailService customMailService;
    @Autowired
    StorageServiceDao storageServiceDao;
    @Autowired
    SedeRepository sedeRepository;
    /*
    @Override
    public ModelAndView resolveException(
            HttpServletRequest request,
            HttpServletResponse response,
            Object object,
            Exception exc) {

        ModelAndView model = new ModelAndView();
        model.setView(new MappingJackson2JsonView());
        model.addObject("msgError", exc.getMessage());
        return model;
    }*/


    @GetMapping(value = {"/"})
    public String redirectAT(){
        return "redirect:/gestor/sede";
    }

    @GetMapping(value = {""})
    public String listaSede(@ModelAttribute("sede") Usuarios usuarios, Model model, HttpSession session){
        session.setAttribute("controller","gestor/sede");
        model.addAttribute("lista", usuariosRepository.findUsuariosByRoles_idroles(ROL_CRUD));
        model.addAttribute("listaSedes",sedeRepository.findAll());
        return "gestor/sedes";
    }

    //@ExceptionView(value = "gestor/sedes", getValue ="gestor/sedes")
    @PostMapping("/save")
    public String editCat(@ModelAttribute("sede") @Valid Usuarios usuarios,
                          BindingResult bindingResult,
                          @RequestParam(name = "photo", required = false) MultipartFile multipartFile,
                          @RequestParam("type") int type,
                          RedirectAttributes attr, Model model) throws Exception {
        //StorageServiceResponse s2 = new StorageServiceResponse();

        if(usuarios.validateUser(bindingResult,type,usuariosRepository).hasErrors()){
            model.addAttribute("formtype",Integer.toString(type))
                .addAttribute("lista", usuariosRepository.findUsuariosByRoles_idroles(ROL_CRUD))
                .addAttribute("msgError", "ERROR");
            model.addAttribute("listaSedes",sedeRepository.findAll());
            return "gestor/sedes";
        }
        else {
            String msg;
            Optional<Usuarios> optionalUsuarios = usuariosRepository.findUsuariosByRoles_idrolesAndIdusuarios(ROL_CRUD, usuarios.getIdusuarios());
            if (optionalUsuarios.isPresent() && (type==0) ) {
                usuarios = optionalUsuarios.get().updateFields(usuarios); // actualizar
                if(!multipartFile.isEmpty()){
                    HashMap<String, String> file = storageServiceDao.storelocalUsuario(usuarios,multipartFile);
                    if (file.get("estado").equals("exito")){
                        String namefile = file.get("fileName");
                        usuarios.setFoto(namefile);
                    }else{
                        bindingResult.rejectValue("foto","error.user","error al subiar la imagen");
                        model.addAttribute("formtype",Integer.toString(type))
                                .addAttribute("lista", usuariosRepository.findUsuariosByRoles_idroles(ROL_CRUD))
                                .addAttribute("msgError", "ERROR");
                        model.addAttribute("listaSedes",sedeRepository.findAll());
                        return "gestor/sedes";
                    }
                }

                msg = "Sede actualizada exitosamente";
            }
            else if (type==1){

                if(!multipartFile.isEmpty()){
                    HashMap<String, String> file = storageServiceDao.storelocalUsuario(usuarios,multipartFile);
                    if (file.get("estado").equals("exito")){
                        String namefile = file.get("fileName");
                        usuarios.setFoto(namefile);
                    }else{
                        bindingResult.rejectValue("foto","error.user","error al subiar la imagen");
                        model.addAttribute("formtype",Integer.toString(type))
                                .addAttribute("lista", usuariosRepository.findUsuariosByRoles_idroles(ROL_CRUD))
                                .addAttribute("msgError", "ERROR");
                        model.addAttribute("listaSedes",sedeRepository.findAll());
                        return "gestor/sedes";
                    }
                }else{
                    usuarios.setFoto("defaultprofile.png");
                }

                try {
                    usuarios.setRoles(new Roles(ROL_CRUD));
                    customMailService.sendEmailPassword(usuarios);
                    msg = "Sede creada exitosamente";
                }
                catch (Exception e){
                    attr.addFlashAttribute("msgError", "Hubo un problema con el envío de credenciales, no se creo el usuario");
                    return "redirect:/gestor/sede";
                }
            }
            else {
                attr.addFlashAttribute("msgError", "Ocurrió un problema, no se pudo guardar");
                return "redirect:/gestor/sede";
            }

            usuariosRepository.save(usuarios);
            attr.addFlashAttribute("msg", msg);
            return "redirect:/gestor/sede";
        }
    }

    @GetMapping("/delete")
    public String deleteCat(Model model,
                            @RequestParam("idusuarios") int id,
                            RedirectAttributes attr) {
        Optional<Usuarios> c = usuariosRepository.findUsuariosByRoles_idrolesAndIdusuarios(ROL_CRUD,id);

        if (c.isPresent()) {
            try {
                usuariosRepository.deleteById(c.get().getIdusuarios());
                attr.addFlashAttribute("msg", "Sede borrado exitosamente");
            }
            catch (Exception ex){
                attr.addFlashAttribute("msgError", "Ocurrió un problema, nno se pudo borrar a la sede");
                ex.fillInStackTrace();
            }
        }
        else {
            attr.addFlashAttribute("msgError", "Ocurrió un problema, nno se pudo borrar a la sede");
        }

        return "redirect:/gestor/sede";
    }


    //Web service
    @ResponseBody
    @GetMapping(value = "/get",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Optional<Usuarios>> getCat(@RequestParam(value = "id") int id){
        return new ResponseEntity<>(usuariosRepository.findUsuariosByRoles_idrolesAndIdusuarios(ROL_CRUD,id), HttpStatus.OK);
    }


    @ResponseBody
    @GetMapping(value = "/has", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<List<HashMap<String,String>>>> hasItems(@RequestParam(value = "id") int id){
        return new ResponseEntity<>(new ArrayList<List<HashMap<String, String>>>() {{
                add(new ArrayList<HashMap<String,String>>() {{
                            Objects.requireNonNull(usuariosRepository.findUsuariosByRoles_idrolesAndIdusuarios(ROL_CRUD,id).orElse(null)).getVentas().forEach((i)-> {
                                add(new HashMap<String, String>() {{
                                        put("rucdni", i.getRucdni());
                                        put("cliente", i.getNombrecliente());
                                        put("vendedor", i.getVendedor().getNombre());
                                    }});});}});
                add(new ArrayList<HashMap<String,String>>() {{
                            asignadosSedesRepository.findAsignadosSedesById_Sede_Idsede(id).forEach((a)-> {
                                add(new HashMap<String, String>() {{
                                        put("sede", a.getId().getSede().getNombresede());
                                        put("stock", Integer.toString(a.getStock()));
                                        put("vendedor", Integer.toString(a.getCantidadactual()));
                                    }});});}}); }},
                HttpStatus.OK);
    }

    //WEB SERVICES
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



}
