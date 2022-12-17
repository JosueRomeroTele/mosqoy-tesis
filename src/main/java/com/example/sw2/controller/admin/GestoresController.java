package com.example.sw2.controller.admin;

import com.example.sw2.Dao.StorageServiceDao;
import com.example.sw2.entity.Sede;
import com.example.sw2.entity.StorageServiceResponse;
import com.example.sw2.entity.Roles;
import com.example.sw2.entity.Usuarios;
import com.example.sw2.repository.AsignadosSedesRepository;
import com.example.sw2.repository.SedeRepository;
import com.example.sw2.repository.UsuariosRepository;
import com.example.sw2.utils.CustomMailService;
import com.example.sw2.utils.UploadObject;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/admin/gestor")
public class GestoresController {

    private final int ROL_CRUD = 2;

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

    private String SedePrincipal = "cuzco";
    @GetMapping(value = {"/"})
    public String redirectAT(){
        return "redirect:/admin/gestor";
    }

    @GetMapping(value = {""})
    public String listaSede(@ModelAttribute("gestor")  Usuarios usuarios, Model model, HttpSession session){
        session.setAttribute("controller","admin/gestor");
        model.addAttribute("lista", usuariosRepository.findUsuariosByRoles_idroles(ROL_CRUD));
        return "admin/listaGestor";
    }

    @PostMapping("/save")
    public String editCat(@ModelAttribute("gestor") @Valid Usuarios usuarios,
                          BindingResult bindingResult,
                          @RequestParam(name = "photo", required = false) MultipartFile multipartFile,
                          @RequestParam("type") int type,
                          RedirectAttributes attr, Model model) throws IOException {

       // StorageServiceResponse s2 = null;
        Sede sedeGestor = sedeRepository.findByNombresede(SedePrincipal).get();
        usuarios.setSede(sedeGestor);
        if(usuarios.validateUser(bindingResult,type,usuariosRepository).hasErrors()){
            model.addAttribute("formtype",Integer.toString(type))
                .addAttribute("lista", usuariosRepository.findUsuariosByRoles_idroles(ROL_CRUD))
                .addAttribute("msgError", "ERROR");
            return "admin/listaGestor";

        }
        else {

            String msg;
            Optional<Usuarios> optionalUsuarios = usuariosRepository.findUsuariosByRoles_idrolesAndIdusuarios(ROL_CRUD, usuarios.getIdusuarios());
            if (optionalUsuarios.isPresent() && (type==0) ) {
               usuarios = optionalUsuarios.get().updateFields(usuarios);
                String n1= multipartFile.getOriginalFilename();
                String n2=multipartFile.toString();
                if(usuarios.getFoto()!=multipartFile.getOriginalFilename() && !n1.equals("")){
                HashMap<String,String> map = storageServiceDao.storelocalUsuario(usuarios,multipartFile);
                //usuarios = optionalUsuarios.get().updateFields(usuarios,multipartFile);  actualizar

                if (map.get("estado").equals("default")) {
                    String filename2=(map.get("fileName"));
                    usuarios.setFoto(filename2); }
                if (map.get("estado").equals("exito")) {
                    String filename3=(map.get("fileName"));
                    usuarios.setFoto(filename3);}
               }
                msg = "Gestor actualizado exitosamente";

            } else if (type==1){

                if(!multipartFile.isEmpty()){

                    HashMap<String,String> map = storageServiceDao.storelocalUsuario(usuarios,multipartFile);
                    if (map.get("estado").equals("exito")) {
                        String filename2=(map.get("fileName"));
                        usuarios.setFoto(filename2);}
                }else {
                    usuarios.setFoto("defaultprofile.png");
                }

                try {
                    usuarios.setRoles(new Roles(ROL_CRUD));
                    customMailService.sendEmailPassword(usuarios);
                    msg = "Gestor creado exitosamente";
                } catch (Exception e){
                    attr.addFlashAttribute("msgError", "Hubo un problema con el envío de credenciales, no se creo el usuario");
                    return "redirect:/admin/gestor";
                }
            } else {
                attr.addFlashAttribute("msgError", "Ocurrió un problema, no se pudo guardar");
                return "redirect:/admin/gestor";
            }
            usuariosRepository.save(usuarios);
            attr.addFlashAttribute("msg", msg);
            return "redirect:/admin/gestor";
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
                attr.addFlashAttribute("msg", "Gestor borrado exitosamente");
            }
            catch (Exception ex){
                attr.addFlashAttribute("msgError", "Ocurrió un problema, no se pudo borrar ");
                ex.fillInStackTrace();
            }
        }
        else {
            attr.addFlashAttribute("msgError", "Ocurrió un problema, no se pudo borrar");
        }

        return "redirect:/admin/gestor";
    }

    @ResponseBody
    @GetMapping(value = "/get",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Optional<Usuarios>> getCat(@RequestParam(value = "id") int id){
        return new ResponseEntity<>(usuariosRepository.findUsuariosByRoles_idrolesAndIdusuarios(ROL_CRUD,id), HttpStatus.OK);
    }



    @ResponseBody
    @GetMapping(value = "/has", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<List<HashMap<String,String>>>> hasItems(@RequestParam(value = "id") int id){

        return new ResponseEntity<>(new ArrayList<List<HashMap<String, String>>>() {
            {
                add(new ArrayList<HashMap<String,String>>() {{
                            Objects.requireNonNull(usuariosRepository.findUsuariosByRoles_idrolesAndIdusuarios(ROL_CRUD,id).orElse(null)).getVentas().forEach((i)-> {
                                add(new HashMap<String, String>() {{
                                        put("rucdni", i.getRucdni());
                                        put("cliente", i.getNombrecliente());
                                        put("inventario", i.getInventario().getCodigoinventario());
                                    }});});}});
                add(new ArrayList<HashMap<String,String>>() {{
                            asignadosSedesRepository.findAsignadosSedesById_Sede_Idsede(usuariosRepository.findById(id).get().getSede().getIdsede()).forEach((a)-> {
                                add(new HashMap<String, String>() {{
                                        put("inventario", a.getId().getProductoinventario().getCodigoinventario());
                                        put("stock", Integer.toString(a.getStock()));
                                        put("sede", a.getId().getSede().getNombresede());
                                }});});}});}},
                HttpStatus.OK);
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


}


