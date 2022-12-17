package com.example.sw2.controller.sede;

import com.example.sw2.Dao.StorageServiceDao;
import com.example.sw2.constantes.AsignadosSedesId;
import com.example.sw2.constantes.CustomConstants;
import com.example.sw2.constantes.VentasId;
import com.example.sw2.entity.*;
import com.example.sw2.repository.*;
import com.example.sw2.utils.CustomMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.format.annotation.DateTimeFormat;
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

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import static com.example.sw2.constantes.CustomConstants.MediosDePago;

@Controller
@RequestMapping("/sede")
public class SedeController {

    @Autowired
    AsignadosSedesRepository asignadosSedesRepository;

    @Autowired
    UsuariosRepository usuariosRepository;

    @Autowired
    InventarioRepository inventarioRepository;
    @Autowired
    VentasRepository ventasRepository;
    @Autowired
    AsignacionTiendasRepository asignacionTiendasRepository;
    @Autowired
    TiendaRepository tiendaRepository;
    @Autowired
    StorageServiceDao storageServiceDao;
    @Autowired
    CustomMailService customMailService;
    @Autowired
    SedeRepository sedeRepository;


    @GetMapping("productosPorConfirmar")
    public String productosPorConfirmar(HttpSession session, Model model) {

        Usuarios sede = (Usuarios) session.getAttribute("usuario");
        session.setAttribute("controller","sede/productosPorConfirmar");
        session.setAttribute("user",sede);
        model.addAttribute("listaProductosPorConfirmar", asignadosSedesRepository.buscarPorSede(sede.getSede().getIdsede()));
        return "sede/ListaProductosPorConfirmar";

    }

    @GetMapping("productosConfirmados")
    public String productosConfirmados(@ModelAttribute("venta") Ventas ventas,
                                       @ModelAttribute("asignaciontiendas") AsignacionTiendas asignacionTiendas, HttpSession session, Model model) {

        Usuarios sede = (Usuarios) session.getAttribute("usuario");

        model.addAttribute("listaProductosConfirmados", asignadosSedesRepository.buscarPorSede(sede.getSede().getIdsede()));
        model.addAttribute("listaTiendas", tiendaRepository.findAll());
        model.addAttribute("mediosDePago",MediosDePago);
        return "sede/ListaProductosConfirmados";
    }

    @PostMapping("registrarVenta")
    public String registrarVenta(@ModelAttribute("venta") @Valid Ventas ventas,
                                 BindingResult bindingResult,
                                 @ModelAttribute("asignaciontiendas") AsignacionTiendas asignacionTiendas,
                                 @RequestParam(value = "idsede") String idsedestr,
                                 @RequestParam(value = "idestadoasign") String idestadoasignstr,
                                 RedirectAttributes attr, HttpSession session, Model model,
                                 @RequestParam(name = "foto1", required = false) MultipartFile multipartFile) {
        StorageServiceResponse s2;
        int idsede;
        int idestadoasign;
        try{
            idsede= Integer.parseInt(idsedestr);
            idestadoasign = Integer.parseInt(idestadoasignstr);
        }catch(Exception e){
            attr.addFlashAttribute("msgNoVenta", "Error al encontrar el producto");
            return "redirect:/sede/productosConfirmados";
        }

        AsignadosSedes asignadosSedes = null;
        /*
        if(!usuariosRepository.findById(idgestor).isPresent()){
            attr.addFlashAttribute("msgNoVenta", "Error al encontrar el producto");
            return "redirect:/sede/productosConfirmados";
        }
         */

        if(!sedeRepository.findById(idsede).isPresent()){
            attr.addFlashAttribute("msgNoVenta", "Error al encontrar el producto");
            return "redirect:/sede/productosConfirmados";
        }


        if (!(bindingResult.hasFieldErrors("precioventa") || bindingResult.hasFieldErrors("inventario") || bindingResult.hasFieldErrors("vendedor"))) {

            AsignadosSedesId id = new AsignadosSedesId(sedeRepository.findById(idsede).get(),ventas.getInventario(),
                    idestadoasign, ventas.getPrecioventa().floatValue());

            Optional<AsignadosSedes> asignadosSedesOptional = asignadosSedesRepository.findById(id);
            if (asignadosSedesOptional.isPresent()) {
                asignadosSedes = asignadosSedesOptional.get();
                if (asignadosSedes.getCantidadactual() == 0) {
                    attr.addFlashAttribute("msgNoVenta", "No se puede registrar la venta de este producto, dado que la cantidad actual es 0.");
                    return "redirect:/sede/productosConfirmados";
                }
                if (!bindingResult.hasFieldErrors("cantidad")) {
                    if (ventas.getCantidad() > asignadosSedes.getCantidadactual()) {
                        bindingResult.rejectValue("cantidad", "error.user", "La cantidad vendida no puede ser mayor a la cantidad actual de la sede");
                    }
                }
            } else {
                attr.addFlashAttribute("msgNoVenta", "Error al encontrar el producto");
                return "redirect:/sede/productosConfirmados";
            }
        }
        if (!ventas.getRucdni().isEmpty() && !bindingResult.hasFieldErrors("rucdni")) {
            if (!(ventas.getRucdni().length() == 8 || ventas.getRucdni().length() == 11)) {
                bindingResult.rejectValue("rucdni", "error.user", "Ingrese un Ruc(11 dígitos) / DNI(8 dígitos) válido.");
            }
        }


        Usuarios sede = (Usuarios) session.getAttribute("usuario");

        if (ventas.getConfirmado() && (!ventas.getId().validateNumeroDocumento())){
                bindingResult.rejectValue("id.numerodocumento","error.user","Ingrese un numero de documento valido");
            if (!(ventas.getMediopago()!=null && ventas.getMediopago()>0 && ventas.getMediopago()<(MediosDePago.size()+1))){
                bindingResult.rejectValue("mediopago","error.user","Ingrese un medio de pago correcto");
            }
        }

        if (!bindingResult.hasFieldErrors("id") && ventas.getConfirmado() && !ventas.getId().validateNumeroDocumento()) {
            Optional<Ventas> optVenta = ventasRepository.findById(ventas.getId());

            if (optVenta.isPresent()) {
                model.addAttribute("msgBoleta", "El numero y tipo de documento de esta venta ya ha sido registrada anteriormente");
                bindingResult.rejectValue("id", "error.user", "");
            }

        }
        if (bindingResult.hasFieldErrors("id") || bindingResult.hasFieldErrors("rucdni") || bindingResult.hasFieldErrors("nombrecliente") || bindingResult.hasFieldErrors("lugarventa") || bindingResult.hasFieldErrors("fecha") || bindingResult.hasFieldErrors("cantidad")) {

            AsignadosSedesId id = new AsignadosSedesId(sedeRepository.findById(idsede).get(),
                    ventas.getInventario(),
                    idestadoasign, ventas.getPrecioventa().floatValue());

            Optional<AsignadosSedes> asignadosSedesOptional = asignadosSedesRepository.findById(id);
            asignadosSedes = asignadosSedesOptional.get();
            model.addAttribute("cantAsignV", asignadosSedes.getCantidadactual());
            model.addAttribute("idsede)", idsede);
            model.addAttribute("idestadoasign", idestadoasign);
            model.addAttribute("listaTiendas", tiendaRepository.findAll());
            model.addAttribute("msgError_V", "ERROR");
            model.addAttribute("listaProductosConfirmados", asignadosSedesRepository.buscarPorSede(sede.getIdusuarios()));
            model.addAttribute("mediosDePago",MediosDePago);
            return "sede/ListaProductosConfirmados";
        } else {
            if(multipartFile!=null && !multipartFile.isEmpty()){
                HashMap<String,String> file = storageServiceDao.storeVenta(ventas,multipartFile);

                if ( !(file.get("estado").equals("exito")) ) {

                    model.addAttribute("idsede", idsede);
                    model.addAttribute("idestadoasign", idestadoasign);
                    model.addAttribute("listaTiendas", tiendaRepository.findAll());
                    model.addAttribute("msgError_V", "ERROR");
                    model.addAttribute("listaProductosConfirmados", asignadosSedesRepository.buscarPorSede(sede.getIdusuarios()));
                    model.addAttribute("mediosDePago",MediosDePago);
                    return "sede/ListaProductosConfirmados";
                }
            }
            ventasRepository.save(ventas);
            int StockActual = asignadosSedes.getStock() - ventas.getCantidad();
            asignadosSedes.setCantidadactual(asignadosSedes.getCantidadactual() - ventas.getCantidad());
            asignadosSedes.setStock(StockActual);
            Inventario inventario = inventarioRepository.findByCodigoinventario(asignadosSedes.getId().getProductoinventario().getCodigoinventario());
            inventario.setCantidadtotal(inventario.getCantidadtotal() - ventas.getCantidad());
            asignadosSedesRepository.save(asignadosSedes);
            inventarioRepository.save(inventario);
                Usuarios user = (Usuarios)session.getAttribute("usuario");
            try {
                if (asignadosSedes.getStock() ==0){
                    customMailService.sendStockAlert(asignadosSedes);
                }
                customMailService.sendSaleConfirmation(ventas, user);
            } catch (MessagingException | IOException  e) {
                e.printStackTrace();
            }
            attr.addFlashAttribute("msgExito", "Venta registrada exitosamente");
            return "redirect:/sede/productosConfirmados";
        }

    }


    @PostMapping("asignar")
    public String asignarProducto(@ModelAttribute("asignaciontiendas") @Valid AsignacionTiendas asignacionTiendas,
                                  BindingResult bindingResult,
                                  @ModelAttribute("venta") Ventas venta,
                                  RedirectAttributes attr, HttpSession session, Model model) {

        Optional<AsignadosSedes> asignadosSedesOptional = asignadosSedesRepository.findById(asignacionTiendas.getAsignadosSedes().getId());

        if (asignadosSedesOptional.isPresent()) {
            AsignadosSedes asignadosSedes = asignadosSedesOptional.get();

            if (asignadosSedes.getCantidadactual() == 0) {
                attr.addFlashAttribute("msgNoAsignar", "No se puede asignar este producto, dado que la cantidad actual es 0.");
                return "redirect:/sede/productosConfirmados";
            }

            if (asignacionTiendas.getStock() < 0) {
                bindingResult.rejectValue("stock", "error.user", "Ingrese una cantidad valida");
            } else {
                if (asignacionTiendas.getStock() > asignadosSedes.getCantidadactual()) {
                    bindingResult.rejectValue("stock", "error.user", "La cantidad asignada no puede ser mayor a la cantidad actual de la sede");
                }
            }

            if (bindingResult.hasErrors() ) {
                model.addAttribute("cantAsign", asignadosSedes.getCantidadactual());
                Usuarios sede = (Usuarios) session.getAttribute("usuario");
                model.addAttribute("listaProductosConfirmados", asignadosSedesRepository.buscarPorSede(sede.getIdusuarios()));
                model.addAttribute("listaTiendas", tiendaRepository.findAll());
                model.addAttribute("msgError_A", "ERROR");
                model.addAttribute("mediosDePago",MediosDePago);
                return "sede/ListaProductosConfirmados";

            } else {

                List<AsignacionTiendas> asignacionTiendasListOld = asignacionTiendasRepository.findAsignacionTiendasByTiendaAndAsignadosSedes(asignacionTiendas.getTienda(), asignacionTiendas.getAsignadosSedes());
                if (asignacionTiendasListOld.isEmpty()) {
                    asignacionTiendas.setAsignadosSedes(asignadosSedes);
                    asignacionTiendasRepository.save(asignacionTiendas);
                } else {
                    AsignacionTiendas asigTiendaOld = asignacionTiendasListOld.get(0);
                    asigTiendaOld.setStock(asignacionTiendas.getStock() + asigTiendaOld.getStock());
                    asigTiendaOld.setFechaasignacion(asignacionTiendas.getFechaasignacion());
                    //asigTiendaOld.setAsignadosSedes(asignadosSedes);
                    asignacionTiendasRepository.save(asigTiendaOld);
                }
                asignadosSedes.setCantidadactual(asignadosSedes.getCantidadactual() - asignacionTiendas.getStock());
                asignadosSedesRepository.save(asignadosSedes);
                attr.addFlashAttribute("msgExito", "Producto asignado exitosamente");
                return "redirect:/sede/productosConfirmados";
            }

        }
        attr.addFlashAttribute("msgE", "El producto no existe");
        return "redirect:/sede/productosConfirmados";
    }

    @PostMapping("/confirmarRecepcion")
    public String confirmarRecepcion( AsignadosSedesId id, RedirectAttributes attr, HttpSession session) {

        Optional<AsignadosSedes> asignadosSedesOptional = asignadosSedesRepository.findById(id);
        if (asignadosSedesOptional.isPresent()) {

            AsignadosSedesId idNew = new AsignadosSedesId(id.getSede(),
                    id.getProductoinventario(), CustomConstants.ESTADO_RECIBIDO_POR_SEDE, id.getPrecioventa());

            Optional<AsignadosSedes> asignadosSedesOldOpt = asignadosSedesRepository.findById(idNew);
            AsignadosSedes asignadosSedesNew = asignadosSedesOptional.get();

            if (asignadosSedesOldOpt.isPresent()) {

                AsignadosSedes asignadosSedesOld = asignadosSedesOldOpt.get();
                asignadosSedesOld.setStock(asignadosSedesOld.getStock() + asignadosSedesNew.getStock());
                asignadosSedesOld.setCantidadactual(asignadosSedesOld.getCantidadactual() + asignadosSedesNew.getStock());
                asignadosSedesOld.setFechaenvio(asignadosSedesNew.getFechaenvio());

                asignadosSedesRepository.save(asignadosSedesOld);
                asignadosSedesRepository.deleteById(id);

            } else {
                AsignadosSedes newAsignadosSedes = new AsignadosSedes(idNew, asignadosSedesNew);
                Usuarios userSede = (Usuarios) session.getAttribute("usuario");
                newAsignadosSedes.setEncargado(userSede);
                attr.addFlashAttribute("msgExito", "Se ha registrado la recepcion correctamente");
                asignadosSedesRepository.save(newAsignadosSedes);
                asignadosSedesRepository.deleteById(id);
            }
        }else {
            attr.addFlashAttribute("msgErrorD", "Hubo un problema, no se encontro el producto");
        }
        return "redirect:/sede/productosPorConfirmar";
    }

    @PostMapping("registrarProblema")
    public String registrarProblema(@RequestParam(value = "mensaje") String mensaje,

                                    @RequestParam(value = "idsede1") int idsede,
                                    @RequestParam(value = "idproductoinv1") String idproductoinv,
                                    @RequestParam(value = "idestadoasign1") int idestadoasign,
                                    @RequestParam(value = "idprecioventa1") Float idprecioventa, RedirectAttributes attr, HttpSession session) {


        AsignadosSedesId id = new AsignadosSedesId(sedeRepository.findById(idsede).get(),
                inventarioRepository.findById(idproductoinv).get(),
                idestadoasign, idprecioventa);

        Optional<AsignadosSedes> asignadosSedesOptional = asignadosSedesRepository.findById(id);

        if (asignadosSedesOptional.isPresent()) {

            if (mensaje==null || mensaje.trim().isEmpty()){
                attr.addFlashAttribute("msgError", "Debe ingresar un mensaje");
            }
            else if (mensaje.trim().length()>256){
                attr.addFlashAttribute("msgError", "Mensaje muy largo, máximo 256 caracteres");
            }
            else{
                AsignadosSedesId idNew = new AsignadosSedesId(id.getSede(),
                        id.getProductoinventario(), 3, id.getPrecioventa());

                AsignadosSedes asignadosSedes = asignadosSedesOptional.get();
                Usuarios sede = (Usuarios) session.getAttribute("usuario");
                asignadosSedesRepository.deleteById(id);
                asignadosSedes.setId(idNew);
                asignadosSedes.setMensaje(mensaje.trim());
                asignadosSedes.setEncargado(sede);
                asignadosSedesRepository.save(asignadosSedes);

                attr.addFlashAttribute("msgExito", "Se ha reportado el problema correctamente");
            }

        }
        return "redirect:/sede/productosPorConfirmar";
    }

    @PostMapping("/devolucion")
    public String devolucionSede(@ModelAttribute("asignaciontiendas") AsignacionTiendas asignacionTiendas,
                                 @ModelAttribute("venta") Ventas ventas,
                                 BindingResult bindingResult,
                                 @RequestParam(value = "idsede") int idsede,
                                 @RequestParam(value = "idproductoinv") String idproductoinv,
                                 @RequestParam(value = "idestadoasign") int idestadoasign,
                                 @RequestParam(value = "idprecioventa") Float idprecioventa,
                                 HttpSession session,
                                 Model model, RedirectAttributes attr) {

        Usuarios sede = (Usuarios) session.getAttribute("usuario");
        AsignadosSedesId id = new AsignadosSedesId(sedeRepository.findById(usuariosRepository.findById(sede.getIdusuarios()).get().getSede().getIdsede()).get(),
                inventarioRepository.findById(idproductoinv).get(),
                idestadoasign, idprecioventa);

        Optional<AsignadosSedes> asignadosSedesOptional = asignadosSedesRepository.findById(id);

        AsignadosSedes asignadosSedes=null;

        if (asignadosSedesOptional.isPresent()) {
            asignadosSedes = asignadosSedesOptional.get();
            asignadosSedes.setEncargado(sede);
            if (asignadosSedes.getCantidadactual() == 0) {
                attr.addFlashAttribute("msgNoDevolucion", "No se puede devolver este producto, dado que la cantidad actual es 0.");
                return "redirect:/sede/productosConfirmados";

            } else {
                if (!bindingResult.hasErrors()) {
                    if (ventas.getCantDevol() < 0) {
                        bindingResult.rejectValue("cantDevol", "error.user", "Ingrese un numero valido");
                    } else {
                        if (ventas.getCantDevol() > asignadosSedes.getCantidadactual()) {
                            bindingResult.rejectValue("cantDevol", "error.user", "La cantidad devuelta no puede ser mayor a la cantidad actual de la sede");
                        }
                        if(ventas.getCantDevol() == 0){
                            bindingResult.rejectValue("cantDevol", "error.user", "Ingrese un numero valido");
                        }
                    }
                }
            }
        } else {
            attr.addFlashAttribute("msgNoDevolucion", "No se puede devolver este producto, dado que no existe.");
            return "redirect:/sede/productosConfirmados";
        }


        if (bindingResult.hasFieldErrors("cantDevol")) {

            Usuarios sede1 = (Usuarios) session.getAttribute("usuario");
            model.addAttribute("cantAsignD", asignadosSedes.getCantidadactual());
            model.addAttribute("listaProductosConfirmados", asignadosSedesRepository.buscarPorSede(sede1.getIdusuarios()));
            model.addAttribute("listaTiendas", tiendaRepository.findAll());
            model.addAttribute("msgErrorD", "ERROR");


            model.addAttribute("idsede", idsede);
            model.addAttribute("idproductoinv", idproductoinv);
            model.addAttribute("idestadoasign", idestadoasign);
            model.addAttribute("idprecioventa", idprecioventa);
            return "sede/ListaProductosConfirmados";
        } else {


            AsignadosSedesId idNew = new AsignadosSedesId(id.getSede(),
                    id.getProductoinventario(), CustomConstants.ESTADO_DEVUELTO_POR_SEDE, id.getPrecioventa());
            Optional<AsignadosSedes> asignSedes = asignadosSedesRepository.findById(idNew);



            if (asignSedes.isPresent()) {

                AsignadosSedes asignadosSedesNew = asignSedes.get();
                asignadosSedesNew.setId(idNew);
                asignadosSedesNew.setCantidadactual(asignadosSedesNew.getCantidadactual() + ventas.getCantDevol());
                asignadosSedesNew.setStock(asignadosSedesNew.getStock() + ventas.getCantDevol());
                asignadosSedesNew.setEncargado(sede);
                asignadosSedesRepository.save(asignadosSedesNew);
            } else {
                AsignadosSedes nuevoAs = new AsignadosSedes();
                nuevoAs.setId(idNew);
                nuevoAs.setFechaenvio(asignadosSedes.getFechaenvio());
                nuevoAs.setCantidadactual(ventas.getCantDevol());
                nuevoAs.setStock(ventas.getCantDevol());
                nuevoAs.setEncargado(sede);
                asignadosSedesRepository.save(nuevoAs);
            }

            asignadosSedes.setCantidadactual(asignadosSedes.getCantidadactual() - ventas.getCantDevol());
            asignadosSedesRepository.save(asignadosSedes);

            attr.addFlashAttribute("msgExito", "Producto devuelto exitosamente");


        }
        return "redirect:/sede/productosConfirmados";
    }


    //Web service
    @ResponseBody
    @GetMapping(value = {"/productosPorConfirmar/get"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Optional<AsignadosSedes>> getAsignsede(@RequestParam(value = "idgestor") int idgestor,
                                                                 @RequestParam(value = "idsede") int idsede,
                                                                 @RequestParam(value = "idproductoinv") String idproductoinv,
                                                                 @RequestParam(value = "idestadoasign") int idestadoasign,
                                                                 @RequestParam(value = "idprecioventa") Float idprecioventa) {

        return new ResponseEntity<>(asignadosSedesRepository.findById(new AsignadosSedesId(sedeRepository.findById(usuariosRepository.findById(idgestor).get().getSede().getIdsede()).get(),
                inventarioRepository.findById(idproductoinv).get(),
                idestadoasign, idprecioventa)), HttpStatus.OK);
    }

    //Web service
    @ResponseBody
    @PostMapping(value = "/productosPorConfirmar/post", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, String>> getAsignsedePost(
                                                                    @RequestParam(value = "sede") Integer sede,
                                                                    @RequestParam(value = "productoinventario") String inv,
                                                                    @RequestParam(value = "estadoasignacion") Integer estadoasignacion,
                                                                    @RequestParam(value = "precioventa") Float precioventa) {

        AsignadosSedesId asignadosSedesId = new
                AsignadosSedesId(sede,inv,estadoasignacion,precioventa);
        return new ResponseEntity<>(new HashMap<String, String>() {{
            asignadosSedesId.setProductoinventario(inventarioRepository.findByCodigoinventario(asignadosSedesId.getProductoinventario().getCodigoinventario()));
                asignadosSedesRepository.findAll();
            AsignadosSedes asignadosSedes = asignadosSedesRepository.findById(asignadosSedesId).orElse(null);
            put("idsede", Integer.toString(asignadosSedesId.getSede().getIdsede()));
            put("idproductoinv", asignadosSedesId.getProductoinventario().getCodigoinventario());
            put("idestadoasign", Integer.toString(asignadosSedesId.getEstadoasignacion()));
            put("idprecioventa", Float.toString(asignadosSedesId.getPrecioventa()));
            put("codinv", asignadosSedesId.getProductoinventario().getCodigoinventario());
            put("fechaenvio", asignadosSedes != null ? asignadosSedes.getFechaenvio().toString() : null);
            put("producto", asignadosSedesId.getProductoinventario().getProductos().getNombre());
            put("precioventa", asignadosSedes != null ? Float.toString(asignadosSedesId.getPrecioventa()) : null);
            put("color", asignadosSedesId.getProductoinventario().getColor());
            put("tamanho", asignadosSedesId.getProductoinventario().getTamanho());
            put("stock", asignadosSedes != null ? String.valueOf(asignadosSedes.getStock()) : null);
            put("foto", asignadosSedesId.getProductoinventario().getFoto());
            put("comunidades", asignadosSedesId.getProductoinventario().getComunidades().getNombre());
        }},
                HttpStatus.OK);
    }

    @ResponseBody
    @PostMapping(value = "/productosConfirmados/post", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, String>> getAsignTiendaPost(
                                                                      @RequestParam(value = "sede") Integer sede,
                                                                      @RequestParam(value = "productoinventario") String inv,
                                                                      @RequestParam(value = "estadoasignacion") Integer estadoasignacion,
                                                                      @RequestParam(value = "precioventa") Float precioventa) {

        AsignadosSedesId asignadosSedesId = new AsignadosSedesId(sede,inv,estadoasignacion,precioventa);
        return new ResponseEntity<>(new HashMap<String, String>() {{
            asignadosSedesId.setProductoinventario(inventarioRepository.findByCodigoinventario(asignadosSedesId.getProductoinventario().getCodigoinventario()));
            asignadosSedesRepository.findAll();
            AsignadosSedes asignadosSedes = asignadosSedesRepository.findById(asignadosSedesId).orElse(null);

            put("idsede", Integer.toString(asignadosSedesId.getSede().getIdsede()));
            put("idproductoinv", asignadosSedesId.getProductoinventario().getCodigoinventario());
            put("idestadoasign", Integer.toString(asignadosSedesId.getEstadoasignacion()));
            put("idprecioventa", Float.toString(asignadosSedesId.getPrecioventa()));
            put("cantAsign", asignadosSedes != null ? Integer.toString(asignadosSedes.getCantidadactual()) : null);

        }},
                HttpStatus.OK);


    }

    @ResponseBody
    @PostMapping(value = "/productosConfirmados/postV", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, String>> getRegistrarVentaPost(@RequestParam(value = "idsede") Integer sede,
                                                                      @RequestParam(value = "inventario") String inv,
                                                                      @RequestParam(value = "idestadoasign") Integer estadoasignacion,
                                                                      @RequestParam(value = "precioventa") Float precioventa, HttpSession session) {
        Usuarios UserLogueo = (Usuarios) session.getAttribute("usuario");

        AsignadosSedesId asignadosSedesId = new AsignadosSedesId(sede,inv,estadoasignacion,precioventa);
        return new ResponseEntity<>(new HashMap<String, String>() {{
            asignadosSedesId.setProductoinventario(inventarioRepository.findByCodigoinventario(asignadosSedesId.getProductoinventario().getCodigoinventario()));
            asignadosSedesRepository.findAll();
            AsignadosSedes asignadosSedes = asignadosSedesRepository.findById(asignadosSedesId).orElse(null);
            put("idsede", Integer.toString(asignadosSedesId.getSede().getIdsede()));
            put("vendedor", Integer.toString(UserLogueo.getIdusuarios()));
            put("inventario", asignadosSedesId.getProductoinventario().getCodigoinventario());
            put("idestadoasign", Integer.toString(asignadosSedesId.getEstadoasignacion()));
            put("precioventa", Float.toString(asignadosSedesId.getPrecioventa()));
            put("cantAsignV", asignadosSedes != null ? Integer.toString(asignadosSedes.getCantidadactual()) : null);

        }},
                HttpStatus.OK);


    }

    @ResponseBody
    @PostMapping(value = "/productosConfirmados/postD", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, String>> getDevPost( @RequestParam(value = "idsede") Integer sede,
                                                                      @RequestParam(value = "idproductoinv") String inv,
                                                                      @RequestParam(value = "idestadoasign") Integer estadoasignacion,
                                                                      @RequestParam(value = "idprecioventa") Float precioventa) {

        AsignadosSedesId asignadosSedesId = new AsignadosSedesId(sede,inv,estadoasignacion,precioventa);
        return new ResponseEntity<>(new HashMap<String, String>() {{
            asignadosSedesId.setProductoinventario(inventarioRepository.findByCodigoinventario(asignadosSedesId.getProductoinventario().getCodigoinventario()));
            asignadosSedesRepository.findAll();
            AsignadosSedes asignadosSedes = asignadosSedesRepository.findById(asignadosSedesId).orElse(null);

            put("idseded", Integer.toString(asignadosSedesId.getSede().getIdsede()));
            put("idproductoinvd", asignadosSedesId.getProductoinventario().getCodigoinventario());
            put("idestadoasignd", Integer.toString(asignadosSedesId.getEstadoasignacion()));
            put("idprecioventad", Float.toString(asignadosSedesId.getPrecioventa()));
            put("cantAsignD", asignadosSedes != null ? Integer.toString(asignadosSedes.getCantidadactual()) : null);

        }},
                HttpStatus.OK);


    }

    @GetMapping(value = "/fotoInv", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<FileSystemResource> getFile(@RequestParam("id") String id) throws IOException {
        Optional<Inventario> optProduct = inventarioRepository.findById(id);
        Inventario user=optProduct.get();
       String path = "C:/FotosProyecto/";
        //String path = "/home/ec2-user/FotosProyecto/";
        File file = new File(path + user.getFoto());
        HttpHeaders respHeaders = new HttpHeaders();
        return new ResponseEntity<FileSystemResource>(
                new FileSystemResource(file), respHeaders, HttpStatus.OK
        );
    }



}
    