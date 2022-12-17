package com.example.sw2.controller.gestor;

import com.example.sw2.constantes.AsignadosSedesId;
import com.example.sw2.constantes.CustomConstants;
import com.example.sw2.entity.*;
import com.example.sw2.repository.AsignadosSedesRepository;
import com.example.sw2.repository.InventarioRepository;
import com.example.sw2.repository.SedeRepository;
import com.example.sw2.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/gestor/devoluciones")
public class    DevolucionesController {

    @Autowired
    AsignadosSedesRepository asignadosSedesRepository;
    @Autowired
    UsuariosRepository usuariosRepository;
    @Autowired
    InventarioRepository inventarioRepository;
    @Autowired
    SedeRepository sedeRepository;

    private int estado_devol = CustomConstants.ESTADO_DEVUELTO_POR_SEDE;
    private int estado_recibido = CustomConstants.ESTADO_RECIBIDO_POR_SEDE;
    private  int estado_devuelto_artesano = CustomConstants.ESTADO_DEVUELTO_AL_ARTESANO;

    @GetMapping(value = {"", "/"})
    public String ListaDevoluciones(@ModelAttribute("sede") Usuarios u,
                                    @RequestParam(value = "id", required = false) Integer dni,
                                    HttpSession session,
                                    Model model, Authentication auth){

        int estado = estado_devol;

        model.addAttribute("devueltos", asignadosSedesRepository.findById_Estadoasignacion(estado));

        return "gestor/devoluciones";
    }

    @GetMapping("/confirmar")
    public String Confirmar(@RequestParam("id1") int sede_id,
                            @RequestParam("id2") String codigo,
                            @RequestParam("id3") Float precio,
                            @RequestParam("id4") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                            @RequestParam("id5") int encargado,
                            HttpSession session,
                            RedirectAttributes attr) {



        //AsignadosID = gestor - sede - inventario - estado - precio
        //sede - producto - estado
        Usuarios Encargado = usuariosRepository.findByIdusuarios(encargado);
        Sede sede = sedeRepository.findById(Encargado.getSede().getIdsede()).get();
        Inventario inv = inventarioRepository.findByCodigoinventario(codigo);
        AsignadosSedesId devol_id = new AsignadosSedesId(sede, inv, estado_devol, precio);
        AsignadosSedesId Prod_en_sede= new AsignadosSedesId(sede,inv,estado_recibido,precio);

        Optional<AsignadosSedes> optAsig1 = asignadosSedesRepository.findById(devol_id);
        Optional<AsignadosSedes> optProEnSede = asignadosSedesRepository.findById(Prod_en_sede);

        if (optAsig1.isPresent()) {
            AsignadosSedes as_devol = optAsig1.get();

            //Aumenta la cant_gestor(inventario) y disminuye el stock(Asignados_sedes)
            asignadosSedesRepository.aceptar_devol_sede(as_devol.getStock(),sede_id,codigo,estado_recibido,precio);
            //crear un asignado a sede con estado 5 devuelto al artesano

            /*
            AsignadosSedesId idDevArt =  new AsignadosSedesId(sede,inv,estado_devuelto_artesano,precio);
            Optional<AsignadosSedes> byId = asignadosSedesRepository.findById(idDevArt);
            if (byId.isPresent()){
                int cantOld = byId.get().getCantidadactual();
                int cantNew = as_devol.getCantidadactual();
                byId.get().setCantidadactual(cantNew+cantOld);
                asignadosSedesRepository.save(byId.get());
            }else{
                AsignadosSedes asigDevArte = new AsignadosSedes();
                asigDevArte.setId(idDevArt);
                asigDevArte.setEncargado(Encargado);
                asigDevArte.setCantidadactual(as_devol.getCantidadactual());
                asigDevArte.setStock(asigDevArte.getCantidadactual());
                asigDevArte.setFechaenvio(as_devol.getFechaenvio());
                asignadosSedesRepository.save(asigDevArte);
            }*/
            if (optProEnSede.isPresent()){
                if (optProEnSede.get().isDevuelto_artesano() == false) {
                    optProEnSede.get().setDevuelto_artesano(true);
                    asignadosSedesRepository.save(optProEnSede.get());
                }
            }

            asignadosSedesRepository.deleteById(as_devol.getId());
            //sumar lo devuelto a cant_gestor del inventario
            attr.addFlashAttribute("msg","Producto devuelto al inventario exitosamente");
        }

        return "redirect:/gestor/devoluciones/";
    }


    @GetMapping("/rechazar")
    public String Rechazar(@RequestParam("dni") int sede_id,
                            @RequestParam("codigo") String codigo,
                            @RequestParam("precio") Float precio,
                            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                            @RequestParam("enncargado") int encargado,
                            HttpSession session,
                            RedirectAttributes attr) {

        //AsignadosID = gestor - sede - inventario - estado - precio
        //sede - producto - estado
        Usuarios Encargado = usuariosRepository.findByIdusuarios(encargado);
        Sede sede = sedeRepository.findById(Encargado.getSede().getIdsede()).get();
        Inventario inv = inventarioRepository.findByCodigoinventario(codigo);
        AsignadosSedesId aid = new AsignadosSedesId(sede, inv, estado_devol, precio);
        Optional<AsignadosSedes> optAsig = asignadosSedesRepository.findById(aid);

        if (optAsig.isPresent()) {
            AsignadosSedes as = optAsig.get();
            asignadosSedesRepository.rechazar_devol_sede(as.getStock(),
                    sede_id,codigo,estado_recibido,precio);
            asignadosSedesRepository.deleteById(as.getId());
            attr.addFlashAttribute("msg","El producto ha sido regresado a la sede ");
        }

        return "redirect:/gestor/devoluciones/";
    }

    //Web service
    @ResponseBody
    @GetMapping(value = "/get",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Usuarios> getTienda(@RequestParam(value = "id") int sede){
        return new ResponseEntity<>(usuariosRepository.findById(sede).get(), HttpStatus.OK);
    }
}
