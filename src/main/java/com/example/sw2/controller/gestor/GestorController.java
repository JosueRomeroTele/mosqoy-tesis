package com.example.sw2.controller.gestor;

import com.example.sw2.Dao.StorageServiceDao;
import com.example.sw2.constantes.VentasId;
import com.example.sw2.entity.Inventario;
import com.example.sw2.entity.StorageServiceResponse;
import com.example.sw2.entity.Usuarios;
import com.example.sw2.entity.Ventas;
import com.example.sw2.repository.AsignadosSedesRepository;
import com.example.sw2.repository.InventarioRepository;
import com.example.sw2.repository.UsuariosRepository;
import com.example.sw2.repository.VentasRepository;
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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

@Controller
@RequestMapping("/gestor")
public class GestorController {

    //private final int ID_USU = ;

    @Autowired
    UsuariosRepository usuariosRepository;
    @Autowired
    StorageServiceDao storageServiceDao;
    @Autowired
    InventarioRepository inventarioRepository;
    @Autowired
    VentasRepository ventasRepository;
    @Autowired
    AsignadosSedesRepository asignadosSedesRepository;


    @GetMapping(value = {"/", ""})
    public String init() {
        return "redirect:/gestor/perfil";
    }


    @GetMapping("/perfil")
    public String perfilGestor(@ModelAttribute("user") Usuarios newUser,
                               Model model,
                               HttpSession session) {
        newUser = (Usuarios) session.getAttribute("usuario");
        model.addAttribute("user", newUser);
        obtenercifras(model,newUser);
        return "gestor/perfilGestor";
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
                return "gestor/perfilGestor";
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
                        bindingResult.rejectValue("foto", "error.user", s2.getMsg());
                        model.addAttribute("msgError", "ERROR");
                        obtenercifras(model,newUser);
                        return "admin/perfilAdmin";
                    }
                }

                usuOld.setNombre(newUser.getNombre());
                usuOld.setApellido(newUser.getApellido());
                usuOld.setTelefono(newUser.getTelefono());
                attr.addFlashAttribute("msg", "Su perfil se actualizó exitosamente");
                session.setAttribute("usuario", usuOld);
                usuariosRepository.save(usuOld);

                return "redirect:/gestor/perfil";
            }
        }else{
            model.addAttribute("msgError", "Fatal error de edición");
            return "gestor/perfilGestor";
        }
    }

    public void obtenercifras(Model model,Usuarios newUser){
        model.addAttribute("cantSedes", usuariosRepository.cantUsuarios(3));
        model.addAttribute("cantInventario", inventarioRepository.cantInventario());
        model.addAttribute("stock_total", inventarioRepository.stockTotal());
        model.addAttribute("cantProdGestor", inventarioRepository.cantPoductosGestor());
        model.addAttribute("stock_gestor", inventarioRepository.stockGestor());
        model.addAttribute("cantProdSede", asignadosSedesRepository.cantProductosEnSede(newUser.getIdusuarios()));
        model.addAttribute("stock_enSede", asignadosSedesRepository.stockProductosEnSede());
        model.addAttribute("cantProdDevueltos", asignadosSedesRepository.cantProductosDevueltos(newUser.getIdusuarios()));
        model.addAttribute("stock_devuelto", asignadosSedesRepository.stockProductosDevueltos(newUser.getIdusuarios()));
        model.addAttribute("cantVentas", ventasRepository.cantVentasPorGestor(newUser.getIdusuarios()));
        model.addAttribute("cantVentasEnTotal", ventasRepository.cantVentasTotalesDeGestores(newUser.getRoles().getIdroles()));
        model.addAttribute("cantProdVendidos", ventasRepository.cantProductosVendidosPorGestor(newUser.getRoles().getIdroles()));
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
