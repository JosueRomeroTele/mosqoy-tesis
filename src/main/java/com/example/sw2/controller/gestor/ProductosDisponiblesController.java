package com.example.sw2.controller.gestor;

import com.example.sw2.Dao.StorageServiceDao;
import com.example.sw2.constantes.AsignadosSedesId;
import com.example.sw2.constantes.CustomConstants;
import com.example.sw2.constantes.VentasId;
import com.example.sw2.entity.*;
import com.example.sw2.repository.*;
import com.example.sw2.utils.CustomMailService;
import com.example.sw2.utils.ExceptionView;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.Null;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static com.example.sw2.constantes.CustomConstants.MANAGER_EMAIL;
import static com.example.sw2.constantes.CustomConstants.MediosDePago;

@Controller
@RequestMapping("/gestor/productosDisponibles")

public class ProductosDisponiblesController {

    @Autowired
    InventarioRepository inventarioRepository;
    @Autowired
    VentasRepository ventasRepository;
    @Autowired
    AsignadosSedesRepository asignadosSedesRepository;
    @Autowired
    UsuariosRepository usuariosRepository;
    @Autowired
    StorageServiceDao storageServiceDao;
    @Autowired
    CustomMailService customMailService;
    @Autowired
    SedeRepository sedeRepository;

    private static int  sede_devuelto_artesano = 1;

    @GetMapping(value ="")
    public String listarProductosDisponibles(Model model, HttpSession session){
        session.setAttribute("controller","gestor/productosDisponibles");
        model.addAttribute("listainventario",inventarioRepository.findAll());
        return "gestor/productosDisponibles";
    }


    @GetMapping(value="venta")
    public String ventasDeProductos(@ModelAttribute("venta") Ventas venta, Model model, @RequestParam("x") String x, HttpSession session){
        Optional<Inventario> optionalInventario = inventarioRepository.findById(x);
        if ( optionalInventario.isPresent()){
            venta = new Ventas((Usuarios)session.getAttribute("usuario"),optionalInventario.get());
            model.addAttribute("venta", venta);
            model.addAttribute("mediosDePago",MediosDePago);
            return "gestor/productosDisponiblesForm";
        }
        return "redirect:/gestor/productosDisponibles";
    }

    @ExceptionView(value = "gestor/productosDisponiblesForm", getValue ="gestor/productosDisponiblesForm")
    @PostMapping("/registrarventa")
    public String registrarventa(@ModelAttribute("venta") @Valid Ventas venta, BindingResult bindingResult,
                                 Model model, RedirectAttributes attributes,
                                 HttpSession session,
                                 @RequestParam(name = "foto1", required = false) MultipartFile multipartFile) throws Exception {

        HashMap<String,String> s2;

        Optional<Inventario> optionalInventario = inventarioRepository.findInventarioByCodigoinventarioAndCantidadgestorIsGreaterThan(
                venta.getInventario().getCodigoinventario(),0
        );

        if(!optionalInventario.isPresent()){
            attributes.addFlashAttribute("msgError", "Hubo un error");
            return "redirect:/gestor/productosDisponibles";
        } else
            venta.setInventario(optionalInventario.get());

        //Se verifica DNI o RUC
        if (venta.getRucdni().length() != 8 && venta.getRucdni().length() != 11 && venta.getRucdni().length() != 0 ) {
            System.out.println("es incorrecto el dni o ruc");
            bindingResult.rejectValue("rucdni", "error.user", "La Cantidad de dígitos debe ser 8 o 11");
        }

        // Se verifica fecha
        if((venta.getFecha()!=null) && venta.getFecha().isBefore(venta.getInventario().getFechaadquisicion()))
            bindingResult.rejectValue("fecha", "error.user", "La fecha debe ser después de la fecha de adquisicion: "+ venta.getInventario().getFechaadquisicion().toString());

        if((venta.getFecha()!=null) && (venta.getInventario().getFechavencimientoconsignacion()!=null)
        && venta.getInventario().getFechavencimientoconsignacion().isBefore(venta.getFecha()))
            bindingResult.rejectValue("fecha", "error.user", "La fecha debe ser antes de la fecha de vencimiento: "+ venta.getInventario().getFechavencimientoconsignacion().toString());


        // Se verifica cantidad
        if(venta.getInventario().getCantidadgestor()<venta.getCantidad())
            bindingResult.rejectValue("cantidad", "error.user", "Cantidad mayor a la disponible");

        //Verificar que se ingreso el numerodocumento si la venta esta confirmada
        if (venta.getConfirmado() && !venta.getId().validateNumeroDocumento()){
                bindingResult.rejectValue("id.numerodocumento","error.user","Ingrese un numero de documento valido");
            if (!(venta.getMediopago()!=null && venta.getMediopago()>0 && venta.getMediopago()<(MediosDePago.size()+1))){
                bindingResult.rejectValue("mediopago","error.user","Ingrese un medio de pago correcto");
            }
        }

        //Verificar que el número de documento sea único
        if (!bindingResult.hasFieldErrors("id") && venta.getConfirmado() && ventasRepository.findById(venta.getId()).isPresent()) {
                bindingResult.rejectValue("id.numerodocumento", "error.user", "El numero y tipo de documento de esta venta ya ha sido registrada anteriormente");
        }


        if ((bindingResult.hasErrors())) {
            model.addAttribute("venta", venta);
            model.addAttribute("mediosDePago",MediosDePago);
            return "gestor/productosDisponiblesForm";
        }else {
            if(multipartFile!=null && !multipartFile.isEmpty()){
                HashMap<String,String> file = storageServiceDao.storeVenta(venta,multipartFile);
                if (!file.get("estado").equals("exito")){
                    bindingResult.rejectValue("media", "error.user", "error subiendo el archivo");
                    model.addAttribute("venta", venta);
                    model.addAttribute("mediosDePago",MediosDePago);
                    return "gestor/productosDisponiblesForm";
                }

            }
            venta.getInventario().setCantidadgestor(venta.getInventario().getCantidadgestor()-venta.getCantidad());
            venta.getInventario().setCantidadtotal(venta.getInventario().getCantidadtotal()-venta.getCantidad());
            inventarioRepository.save(venta.getInventario());
            venta.setVendedor((Usuarios) session.getAttribute("usuario"));
            ventasRepository.save(venta);
            try {
                ArrayList<String> sa = new ArrayList<String>(){
                    {usuariosRepository.findUsuariosByRoles_idroles(2).forEach(usuarios -> {
                        if (!(venta.getVendedor().getIdusuarios()==usuarios.getIdusuarios()))
                            add(usuarios.getCorreo());});}
                };
                String[] mails = new String[sa.size()];
                mails = sa.toArray(mails);
                customMailService.sendSaleConfirmation(venta,mails);
            } catch (MessagingException | IOException  e) {
                e.printStackTrace();
            }
            attributes.addFlashAttribute("msg", "Venta de producto realizada");
            return "redirect:/gestor/productosDisponibles";
        }
    }


    @GetMapping(value="asignar")
    public String asignarProducto(@ModelAttribute("asignadosSedes") AsignadosSedes asignadosSedes ,Model model, @RequestParam("x") String x){
        Optional<Inventario> optionalInventario = inventarioRepository.findById(x);
        if ( optionalInventario.isPresent()){
            asignadosSedes = new AsignadosSedes(new AsignadosSedesId(null,optionalInventario.get(),null,null));
            //Inventario inventario=optionalInventario.get();
            model.addAttribute("asignadosSedes", asignadosSedes);
            //model.addAttribute("listasedes",usuariosRepository.findUsuariosByRoles_IdrolesOrderByApellido(3));
            model.addAttribute("listaDeSedes",sedeRepository.findAll());
            return "gestor/productosDisponiblesAsignar";
        }
        return "redirect:/gestor/productosDisponibles";
    }



    @PostMapping("/resgistrarasignacion")
        public String registrarAsignacionProducto(@ModelAttribute("asignadosSedes") @Valid AsignadosSedes asignadosSedes, BindingResult bindingResult,
                                              Model model, RedirectAttributes attributes,
                                              HttpSession session) {
        Optional<Inventario> optionalInventario;
        Optional<Sede> optionalSede;
        try{
            optionalInventario = inventarioRepository.findInventarioByCodigoinventarioAndCantidadgestorIsGreaterThan(

                    asignadosSedes.getId().getProductoinventario().getCodigoinventario(), 0
            );

            optionalSede = sedeRepository.findById(asignadosSedes.getId().getSede().getIdsede());


        }catch (NullPointerException ex){
            ex.printStackTrace();
            attributes.addFlashAttribute("msgError", "Hubo un error");
            return "redirect:/gestor/productosDisponibles";
        }


        // Se verifica que el producto de Inventario
        if(!optionalInventario.isPresent()){
            attributes.addFlashAttribute("msgError", "Hubo un error");
            return "redirect:/gestor/productosDisponibles";
        } else
            asignadosSedes.getId().setProductoinventario(optionalInventario.get());

        //se verifica la sede, no usuario
        if(!optionalSede.isPresent())
            bindingResult.rejectValue("id.sede","error.user","escoja una sede");
        else
            asignadosSedes.getId().setSede(optionalSede.get());

        if (asignadosSedes.getStock()==null || asignadosSedes.getStock()==0)
            bindingResult.rejectValue("stock","error.user","La cantidad debe ser mayor a 0");

        //Se verifica la fecha de envio
        if ((asignadosSedes.getFechaenvio()!=null) && asignadosSedes.getFechaenvio().isBefore(asignadosSedes.getId().getProductoinventario().getFechaadquisicion()))
            bindingResult.rejectValue("fechaenvio","error.user","La fecha debe ser después de la fecha de adquisición: "+asignadosSedes.getId().getProductoinventario().getFechaadquisicion().toString());

        if((asignadosSedes.getFechaenvio()!=null) && (asignadosSedes.getId().getProductoinventario().getFechavencimientoconsignacion()!=null)
                && asignadosSedes.getId().getProductoinventario().getFechavencimientoconsignacion().isBefore(asignadosSedes.getFechaenvio()))
            bindingResult.rejectValue("fechaenvio", "error.user", "La fecha debe ser antes de la fecha de vencimiento: "+ asignadosSedes.getId().getProductoinventario().getFechavencimientoconsignacion().toString());


        //Se verfica el precio de venta
        if (!((asignadosSedes.getId().getPrecioventa()!=null) && (asignadosSedes.getId().getPrecioventa()>=0.01) && (asignadosSedes.getId().getPrecioventa()<=((float)9999999.99)) ))
            bindingResult.rejectValue("id.precioventa","error.user","Ingrese un precio válido");
        else {
             if( getNumberOfDecimalPlaces(new BigDecimal(Float.toString(asignadosSedes.getId().getPrecioventa())))>2) {
                 bindingResult.rejectValue("id.precioventa", "error.user", "Ingrese un precio válido");
             }
        }

        //Se verifica la cantidad asignada
        if((asignadosSedes.getStock()!=null) && (optionalInventario.get().getCantidadgestor()<asignadosSedes.getStock()))
            bindingResult.rejectValue("stock","error.user","La cantidad asignada es mayor a la cantidad disponible");


        if (bindingResult.hasErrors()) {
            model.addAttribute("asignadosSedes", asignadosSedes);
            //model.addAttribute("listasedes",usuariosRepository.findUsuariosByRoles_IdrolesOrderByApellido(3));
            model.addAttribute("listaDeSedes",sedeRepository.findAll());
            return "gestor/productosDisponiblesAsignar";
        } else {
            Usuarios user = (Usuarios)session.getAttribute("usuario");

            asignadosSedes.getId().setSede( asignadosSedes.getId().getSede());

            asignadosSedes.getId().getProductoinventario().subtractCantidad(asignadosSedes.getStock());
            inventarioRepository.save(asignadosSedes.getId().getProductoinventario());
            asignadosSedes.getId().setEstadoasignacion(CustomConstants.ESTADO_ENVIADO_A_SEDE);
            asignadosSedes.setCantidadactual(asignadosSedes.getStock());
            Optional<AsignadosSedes> optional = asignadosSedesRepository.findById(asignadosSedes.getId());
            if(optional.isPresent()){
                optional.get().setDevuelto_artesano(false);
                AsignadosSedes asignadosSedesTemp = optional.get();
                asignadosSedesTemp.setStock(asignadosSedesTemp.getStock()+  asignadosSedes.getStock());
                asignadosSedesRepository.save(asignadosSedesTemp);
            }else{ asignadosSedesRepository.save(asignadosSedes); }
            /*
            inv.setCantidadgestor(inv.getCantidadgestor()-asignadosSedes.getStock());
            inventarioRepository.save(inv);
            Usuarios usuarios = (Usuarios)session.getAttribute("usuario") ;
            Optional<Usuarios> optionalUsuarios = usuariosRepository.findById(sede);
            Usuarios sedes = optionalUsuarios.get();
            asignadosSedes.setId(new AsignadosSedesId(usuarios, sedes, inv,1,precio));
            asignadosSedes.setCantidadactual(asignadosSedes.getStock());
            asignadosSedesRepository.save(asignadosSedes);
*/
            attributes.addFlashAttribute("msg", "Producto asignado exitosamente");
            return "redirect:/gestor/productosDisponibles";
        }
    }


    public int getNumberOfDecimalPlaces(BigDecimal bigDecimal) {
        String string = bigDecimal.stripTrailingZeros().toPlainString();
        int index = string.indexOf(".");
        return index < 0 ? 0 : string.length() - index - 1;
    }


}
