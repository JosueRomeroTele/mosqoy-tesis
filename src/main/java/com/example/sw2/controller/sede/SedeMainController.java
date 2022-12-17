package com.example.sw2.controller.sede;

import com.example.sw2.Dao.StorageServiceDao;
import com.example.sw2.constantes.CustomConstants;
import com.example.sw2.entity.StorageServiceResponse;
import com.example.sw2.entity.Usuarios;
import com.example.sw2.repository.AsignacionTiendasRepository;
import com.example.sw2.repository.AsignadosSedesRepository;
import com.example.sw2.repository.TiendaRepository;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

@Controller
@RequestMapping("/sede")
public class SedeMainController {

    @Autowired
    UsuariosRepository usuariosRepository;
    @Autowired
    StorageServiceDao storageServiceDao;
    @Autowired
    AsignadosSedesRepository asignadosSedesRepository;
    @Autowired
    TiendaRepository tiendaRepository;
    @Autowired
    AsignacionTiendasRepository asignacionTiendasRepository;

    private int estado_enviado = CustomConstants.ESTADO_ENVIADO_A_SEDE;
    private int estado_recibido = CustomConstants.ESTADO_RECIBIDO_POR_SEDE;


    @GetMapping(value = {"/", ""})
    public String init() {
        return "redirect:/sede/perfil";
    }

    @GetMapping("/perfil")
    public String perfilSede(@ModelAttribute("user") Usuarios newUser,
                               Model model,
                               HttpSession session) {
        newUser = (Usuarios) session.getAttribute("usuario");
        model.addAttribute("user", newUser);
        obtenercifras(model,newUser);
        return "sede/perfilSede";
    }

    @PostMapping("/save")
    public String editCom(@ModelAttribute("user") @Valid Usuarios newUser,
                          BindingResult bindingResult,
                          RedirectAttributes attr, Model model, HttpSession session,
                          @RequestParam("deletefoto") String[] dfstr,
                          @RequestParam(name = "photo", required = false) MultipartFile multipartFile) throws IOException {
        StorageServiceResponse s2 = null;
        Boolean df = (dfstr.length == 2);


        Optional<Usuarios> optionalUsuarios = usuariosRepository.findById(newUser.getIdusuarios());
        Usuarios userSession=(Usuarios)session.getAttribute("usuario");

        boolean valid=(userSession.getIdusuarios()==newUser.getIdusuarios())&&optionalUsuarios.isPresent();
        if (valid) {
            Usuarios usuOld = optionalUsuarios.get();
            newUser.setFoto(usuOld.getFoto());
            if (bindingResult.hasFieldErrors("nombre") || bindingResult.hasFieldErrors("apellido") || bindingResult.hasFieldErrors("telefono")) {
                model.addAttribute("msgError", "ERROR");
                obtenercifras(model,newUser);
                return "sede/perfilSede";
            } else {
                if (df){
                    usuOld.setFoto("defaultprofile.png");
                }
                if (!multipartFile.isEmpty()) {
                    HashMap<String, String> file = storageServiceDao.storelocalUsuario(usuOld,multipartFile);
                    if (file.get("estado").equals("exito")){
                        String namefile = file.get("fileName");
                        usuOld.setFoto(namefile);
                    }else{
                        bindingResult.rejectValue("foto", "error.user", "problemas para poder guardar la imagen");
                        model.addAttribute("msgError", "ERROR");
                        obtenercifras(model,newUser);
                        return "sede/perfilSede";
                    }

                }

                usuOld.setNombre(newUser.getNombre());
                usuOld.setApellido(newUser.getApellido());
                usuOld.setTelefono(newUser.getTelefono());
                attr.addFlashAttribute("msg", "Su perfil se actualizó exitosamente");
                session.setAttribute("usuario", usuOld);
                usuariosRepository.save(usuOld);

                return "redirect:/sede/perfil";
            }
        }else{
            model.addAttribute("msgError", "Fatal error de edición");
            return "sede/perfilSede";
        }
    }

    public void obtenercifras(Model model,Usuarios newUser){
        model.addAttribute("prod_porConfirmar", asignadosSedesRepository.cantProductosSegunEstado(estado_enviado,newUser.getSede().getIdsede()));
        model.addAttribute("prod_Confirmado", asignadosSedesRepository.cantProductosSegunEstado(estado_recibido,newUser.getSede().getIdsede()));
        model.addAttribute("stockProductos", asignadosSedesRepository.stockTotalProductos(newUser.getSede().getIdsede()));
        model.addAttribute("cantTiendas", tiendaRepository.cantTiendas());
        model.addAttribute("cantProd_enTienda", asignacionTiendasRepository.cantProductosEnTienda(newUser.getSede().getIdsede()));
    }

    //Web service
    @ResponseBody
    @GetMapping(value = {"/perfil/get", "/save/get"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Optional<Usuarios>> getUsu(@RequestParam(value = "id") int id) {
        return new ResponseEntity<>(usuariosRepository.findById(id), HttpStatus.OK);
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
