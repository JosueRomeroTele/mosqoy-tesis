package com.example.sw2.controller.gestor;

import com.example.sw2.Dao.StorageServiceDao;
import com.example.sw2.constantes.AsignadosSedesId;
import com.example.sw2.constantes.CustomConstants;
import com.example.sw2.constantes.VentasId;
import com.example.sw2.entity.*;
import com.example.sw2.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.example.sw2.constantes.CustomConstants.MediosDePago;

@Controller
@RequestMapping("/gestor/confirmacion")
public class ConfirmacionVentaController {

    @Autowired
    VentasRepository ventasRepository;
    @Autowired
    UsuariosRepository usuariosRepository;
    @Autowired
    StorageServiceDao storageServiceDao;
    @Autowired
    InventarioRepository inventarioRepository;
    @Autowired
    AsignadosSedesRepository asignadosSedesRepository;
    @Autowired
    AsignacionTiendasRepository asignacionTiendasRepository;


    @GetMapping(value = {""})
    public String listVen(@ModelAttribute("venta") Ventas ven,
                          Model model,
                          HttpSession session) {
        session.setAttribute("controller","gestor/confirmacion");
        model.addAttribute("lista", ventasRepository.findVentasByConfirmado(false));
        model.addAttribute("documentos", CustomConstants.getTiposDocumento());
        model.addAttribute("mediosDePago",MediosDePago);
        return "gestor/confirmaVentas";
    }

    @PostMapping(value = "save")
    public String confirmar(@ModelAttribute("venta") Ventas v, RedirectAttributes attr,
                            @RequestParam(name = "foto1", required = false) MultipartFile multipartFile){

        StorageServiceResponse s2 = null;

        Optional<Ventas> optV = ventasRepository.findByIdventasAndConfirmadoAndId_Tipodocumento(
                v.getIdventas(), false, v.getId().getTipodocumento()
        );

        if (!v.getId().validateNumeroDocumento()){
            attr.addFlashAttribute("msgError", "El número de documento no es valido (mínimo 5 digitos)");
            return "redirect:/gestor/confirmacion";
        }
        if (!v.validateMedioPago()){
            attr.addFlashAttribute("msgError", "Hubo un error");
            return "redirect:/gestor/confirmacion";
        }

        // Faltaría validar el numero de documento
        System.out.println(v.getIdventas());
        System.out.println(v.getId().getTipodocumento());
        System.out.println(v.getId().getNumerodocumento());

        if (optV.isPresent()){
            if (!ventasRepository.findById(v.getId()).isPresent()){
                Ventas venta = optV.get();
                venta.setMediopago(v.getMediopago());
                venta.getId().setNumerodocumento(v.getId().getNumerodocumento());
                venta.getId().setTipodocumento(v.getId().getTipodocumento());
                venta.setConfirmado(true);

                if(multipartFile!=null && !multipartFile.isEmpty()){
                    HashMap<String,String> file = storageServiceDao.storeVenta(venta,multipartFile);
                    if (!(file.get("estado").equals("exito"))){
                        attr.addFlashAttribute("msgError", "Error subiendo el archivo");
                        return "redirect:/gestor/confirmacion";
                    }

                }

                ventasRepository.save(venta);
                attr.addFlashAttribute("msg", "La venta se ha confirmado");
            }else {
                attr.addFlashAttribute("msgError", "El número "+v.getId().getNumerodocumento() +" ya está registrado como "+v.getId().getNombreTipodocumento());
            }
        }else {
            attr.addFlashAttribute("msgError", "Hubo un error encontrando el registro de venta");
        }



        return "redirect:/gestor/confirmacion";
    }

    @PostMapping(value = "delete")
    public String cancelar(@ModelAttribute("venta") Ventas v, RedirectAttributes attr){

        Optional<Ventas> optV = ventasRepository.findByIdventasAndConfirmado(v.getIdventas(), false);

        // Faltaría validar el numero
        System.out.println(v.getIdventas());

        if (optV.isPresent()){
            Ventas venta = optV.get();
            switch (venta.getVendedor().getRoles().getIdroles()){
                case 2: // Gestor
                    Optional<Inventario> optionalInventario = inventarioRepository.findById(venta.getInventario().getCodigoinventario());
                    if (optionalInventario.isPresent()){
                        Inventario inv = optionalInventario.get();
                        //Se aumenta cantidad total y cantidad gestor
                        inv.addCantidad(venta.getCantidad());
                        inventarioRepository.save(inv);
                    }
                    break;
                case 3: //Sede
                    Optional<AsignacionTiendas> optionalAsignacionTiendas =
                            asignacionTiendasRepository.findByAsignadosSedes_Id_SedeAndAsignadosSedes_Id_Productoinventario_CodigoinventarioAndTienda_Ruc(
                                    venta.getVendedor().getSede(), venta.getInventario().getCodigoinventario(),venta.getRucdni());
                    Optional<AsignadosSedes> optionalAsignadosSedes =
                            asignadosSedesRepository.findById_SedeAndId_ProductoinventarioAndId_Precioventa(venta.getVendedor().getSede(),venta.getInventario(),venta.getPrecioventa().floatValue());

                    if (optionalAsignacionTiendas.isPresent()){
                        AsignacionTiendas asignacionTiendas = optionalAsignacionTiendas.get();
                        // Si pertenece a asignacionTiendas
                        //  -   Se suma la cantidad de asignacion a tiendas,
                        //  -   Se suma el stock (total) de asignados sedes
                        //  -   Se suma la cantidad total en el inventario.
                        asignacionTiendas.setStock(asignacionTiendas.getStock()+venta.getCantidad());
                        asignacionTiendas.getAsignadosSedes().setStock(
                                asignacionTiendas.getAsignadosSedes().getStock()+venta.getCantidad()
                        );
                        asignacionTiendas.getAsignadosSedes().getId().getProductoinventario().addCantidadTotal(venta.getCantidad());
                        asignacionTiendasRepository.save(asignacionTiendas);
                        asignadosSedesRepository.save(asignacionTiendas.getAsignadosSedes());
                        inventarioRepository.save(asignacionTiendas.getAsignadosSedes().getId().getProductoinventario());
                    }else if (optionalAsignadosSedes.isPresent()){
                        AsignadosSedes asignadosSedes = optionalAsignadosSedes.get();
                        // Si pertenece a asignadosSedes
                        //  -   Se suma el stock (total) de asignados sedes
                        //  -   Se suma la cantidad total en el inventario.
                           asignadosSedes.setStock(asignadosSedes.getStock()+venta.getCantidad());
                           asignadosSedes.setCantidadactual(asignadosSedes.getCantidadactual()+venta.getCantidad());
                           asignadosSedes.getId().getProductoinventario().addCantidadTotal(venta.getCantidad());
                           asignadosSedesRepository.save(asignadosSedes);
                           inventarioRepository.save(asignadosSedes.getId().getProductoinventario());
                    }else {
                        attr.addFlashAttribute("msgError","Hubo un error al momento de cancelar este registro");
                        return "redirect:/gestor/confirmacion";
                    }
                    break;

                default:
                    attr.addFlashAttribute("msgError","Hubo un error al momento de cancelar este registro");
                    return "redirect:/gestor/confirmacion";
            }
            ventasRepository.delete(venta);
            attr.addFlashAttribute("msg", "La venta se ha cancelado");
        }else {
            attr.addFlashAttribute("msgError", "Hubo un error encontrando el registro de venta");
        }
        return "redirect:/gestor/confirmacion";
    }

    //Web service
    @ResponseBody
    @GetMapping(value = "/get",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getCat(@RequestParam(value = "id") Integer id){
        Map<String,Object> map =  new HashMap<>();

        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        Optional<Ventas> optionalVentas = ventasRepository.findByIdventasAndConfirmado(id,false);
        if (optionalVentas.isPresent()){
            Ventas v = optionalVentas.get();
            httpStatus = HttpStatus.OK;
            map.put("id", new VentasId(v.getId().getTipodocumento(), ""));
            map.put("producto",v.getInventario().getCodigoinventario());
            map.put("cantidad",v.getCantidad());
            map.put("preciounitario",v.getPrecioventa());
            map.put("suma",v.getSumaParcial());
        }
        else {

            map.put("status","error");
            map.put("msg","not found");
        }

        return new ResponseEntity<>(map, httpStatus);
    }

}
