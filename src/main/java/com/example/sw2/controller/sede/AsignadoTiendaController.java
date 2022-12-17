package com.example.sw2.controller.sede;

import com.example.sw2.Dao.StorageServiceDao;
import com.example.sw2.constantes.AsignadosSedesId;
import com.example.sw2.constantes.CustomConstants;
import com.example.sw2.constantes.VentasId;
import com.example.sw2.dto.DatosAsignadosTiendaDto;
import com.example.sw2.entity.*;
import com.example.sw2.repository.*;
import com.example.sw2.utils.CustomMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.example.sw2.constantes.CustomConstants.MediosDePago;

@Controller
@RequestMapping("sede/AsignadoTienda")
public class AsignadoTiendaController {

    @Autowired
    AsignacionTiendasRepository asignacionTiendasRepository;
    @Autowired
    VentasRepository ventasRepository;
    @Autowired
    AsignadosSedesRepository asignadosSedesRepository;
    @Autowired
    InventarioRepository inventarioRepository;
    @Autowired
    UsuariosRepository usuariosRepository;
    @Autowired
    StorageServiceDao storageServiceDao;
    @Autowired
    CustomMailService customMailService;

    @GetMapping(value = {"/"})
    public String redirectAT(){
        return "redirect:/sede/AsignadoTienda";
    }

    @GetMapping(value = {""})
    public String ListaAsignacionTiendas(@ModelAttribute("venta") Ventas v,
                                         HttpSession session,
                                         Model model){
        Usuarios sede = (Usuarios) session.getAttribute("usuario");
        session.setAttribute("controller","sede/AsignadoTienda");
        v = new Ventas(); v.setConfirmado(false);
        model.addAttribute("venta", v);
        model.addAttribute("asignados", asignacionTiendasRepository.findAsignacionTiendasByStockGreaterThanAndAsignadosSedes_Id_Sede(0,sede.getSede()));
        model.addAttribute("tipodoc", CustomConstants.getTiposDocumento());
        model.addAttribute("mediosDePago",MediosDePago);
        return "sede/asignadoTiendas";
    }

    @PostMapping("/registrar")
    public String RegistrarVentas(@ModelAttribute("venta") @Valid Ventas venta,
                                  BindingResult bindingResult, HttpSession session,
                                  @RequestParam("id1") int idAstiendas,
                                  Model model, RedirectAttributes attr,
                                  @RequestParam(name = "foto1", required = false) MultipartFile multipartFile) throws Exception {

        StorageServiceResponse s2;
        Usuarios sedeuser = (Usuarios) session.getAttribute("usuario");
        venta.setVendedor(sedeuser);
        Optional<AsignacionTiendas> optaTienda = asignacionTiendasRepository.findById(idAstiendas);
        Optional<Ventas> optVentas = ventasRepository.findById(venta.getId());
        AsignacionTiendas aTienda = optaTienda.get();

        System.out.println(venta.getId().getNombreTipodocumento());
        System.out.println(venta.getId().getNumerodocumento());
        System.out.println(venta.getId().getTipodocumento());
        System.out.println(venta.getConfirmado());



        if(venta.getCantidad() > aTienda.getStock()){
            bindingResult.rejectValue("cantidad", "error.user","La cantidad no puede ser mayor al stock de la tienda");
        }
        if(venta.getFecha()!= null && venta.getFecha().isBefore(aTienda.getFechaasignacion())){
                bindingResult.rejectValue("fecha", "error.user","La fecha de venta no puede ser antes de la fecha de asignación ("+ aTienda.getFechaasignacion()+")");
        }

        //Verificar que se haya ingresado un numero correcto de documento si la venta es confirmada

        if (venta.getConfirmado()!=null && venta.getConfirmado() && venta.getId()!=null && !venta.getId().validateNumeroDocumento()){
            bindingResult.rejectValue("id.numerodocumento","error.user","Ingrese un numero de documento válido");
            if (!(venta.getMediopago()!=null && venta.getMediopago()>0 && venta.getMediopago()<(MediosDePago.size()+1))){
                bindingResult.rejectValue("mediopago","error.user","Ingrese un medio de pago correcto");
            }
        }


        if(optVentas.isPresent() && venta.getId().validateNumeroDocumento()){
            bindingResult.rejectValue("id.numerodocumento", "error.user", "El número de documento ya está registrado");
        }


        if(bindingResult.hasErrors()){
            Usuarios sede = (Usuarios) session.getAttribute("usuario");
                model.addAttribute("cantAsign", aTienda.getStock());
            model.addAttribute("asignados", asignacionTiendasRepository.findAsignacionTiendasByStockGreaterThanAndAsignadosSedes_Id_Sede(0,sede.getSede()));
            model.addAttribute("id1", idAstiendas);
            model.addAttribute("tipodoc", CustomConstants.getTiposDocumento());
            model.addAttribute("msgErrorRegistrar", "ERROR");
            model.addAttribute("mediosDePago",MediosDePago);
            return "sede/asignadoTiendas";
        }else{
            if(multipartFile!=null && !multipartFile.isEmpty()){
                HashMap<String,String> file = storageServiceDao.storeVenta(venta,multipartFile);
                if (!(file.get("estado").equals("exito"))){
                    bindingResult.rejectValue("media", "error.user", "error subiendo el archivo");
                    Usuarios sede = (Usuarios) session.getAttribute("usuario");
                    model.addAttribute("asignados", asignacionTiendasRepository.findAsignacionTiendasByStockGreaterThanAndAsignadosSedes_Id_Sede(0,sede.getSede()));
                    model.addAttribute("id1", idAstiendas);
                    model.addAttribute("tipodoc", CustomConstants.getTiposDocumento());
                    model.addAttribute("msgErrorRegistrar", "ERROR");
                    model.addAttribute("mediosDePago",MediosDePago);
                    return "sede/asignadoTiendas";
                }

            }
            //actualizar stock(Asignados_sedes) cant_total(inventario)
            // gestor, sede, codigo, estado int, in precio decimal(10,2), in cant int, in aTienda int)
            asignacionTiendasRepository.tienda_registra(venta.getVendedor().getSede().getIdsede(), venta.getInventario().getCodigoinventario(),
                    2,venta.getPrecioventa(), venta.getCantidad(), idAstiendas);
            venta.setVendedor((Usuarios) session.getAttribute("usuario"));
            ventasRepository.save(venta);
            try {
                if (aTienda.getStock()==venta.getCantidad()){
                    customMailService.sendStockAlert(aTienda.getAsignadosSedes());
                }
                //customMailService.sendSaleConfirmation(venta, aTienda.getAsignadosSedes().getId().getGestor().getCorreo());
            } catch (MessagingException | IOException  e) {
                e.printStackTrace();
            }
            attr.addFlashAttribute("msg", "Venta registrada exitosamente");
            return "redirect:/sede/AsignadoTienda";
        }
    }

    @GetMapping("/devolucion")
    public String DevolTienda(@ModelAttribute("venta") Ventas v,
                              @RequestParam("id2") int idAstiendas,
                              BindingResult bindingResult,
                              HttpSession session,
                              Model model, RedirectAttributes attr,
                              @RequestParam(name = "foto1", required = false) MultipartFile multipartFile){


        Optional<AsignacionTiendas> optAt = asignacionTiendasRepository.findById(idAstiendas);
        AsignacionTiendas at = optAt.get();


        if(v.getCantDevol() > at.getStock()){
            bindingResult.rejectValue("cantDevol", "error.user","La cantidad no puede ser mayor al stock actual de la tienda");
        }
        if(v.getCantDevol() == 0){
            bindingResult.rejectValue("cantDevol", "error.user","La cantidad tiene que ser mayor a 0");
        }
        if(v.getFecha() == null){
            bindingResult.rejectValue("fecha", "error.user","Tiene que asignar una fecha");
        }else{
            if(v.getFecha().isBefore(at.getFechaasignacion())){
                bindingResult.rejectValue("fecha", "error.user","La fecha de devolución no puede ser antes de la fecha de asignación");
            }
        }


        if(bindingResult.hasErrors()){
            Usuarios sede = (Usuarios) session.getAttribute("usuario");
            model.addAttribute("asignados", asignacionTiendasRepository.findAsignacionTiendasByStockGreaterThanAndAsignadosSedes_Id_Sede(0,sede.getSede()));
            model.addAttribute("id2", idAstiendas);
            model.addAttribute("tipodoc", CustomConstants.getTiposDocumento());
            model.addAttribute("msgErrorDevolucion", "ERROR");
            model.addAttribute("mediosDePago",MediosDePago);
            return "sede/asignadoTiendas";
        }else{
            AsignadosSedes as = at.getAsignadosSedes();
            asignacionTiendasRepository.tienda_devolucion(as.getId().getSede().getIdsede(),
                    as.getId().getProductoinventario().getCodigoinventario(), as.getId().getEstadoasignacion(),
                    as.getId().getPrecioventa(),v.getCantDevol(), at.getIdtiendas());

            //AsignadosID = gestor - sede - inventario - estado - precio
            //restar stock(Asignado_Tienda) y sumar cantidadactual(Asigandos_sed    es)
            //sumar lo devuelto a cant_gestor(inventario)
    

            attr.addFlashAttribute("msg","Producto devuelto exitosamente");

            return "redirect:/sede/AsignadoTienda";

        }
    }

    //Web service
    @ResponseBody
    @GetMapping(value = "/get",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Optional<AsignacionTiendas>> getAsignacion(@RequestParam(value = "id1") int id){
        return new ResponseEntity<>(asignacionTiendasRepository.findById(id), HttpStatus.OK);
    }

}
