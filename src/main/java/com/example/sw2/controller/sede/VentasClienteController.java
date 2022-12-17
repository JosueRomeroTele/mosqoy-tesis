package com.example.sw2.controller.sede;

import com.example.sw2.constantes.VentasId;
import com.example.sw2.entity.*;
import com.example.sw2.repository.AsignacionTiendasRepository;
import com.example.sw2.repository.AsignadosSedesRepository;
import com.example.sw2.repository.TiendaRepository;
import com.example.sw2.repository.VentasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/sede/ventasCliente")
public class VentasClienteController {

    @Autowired
    VentasRepository ventasRepository;
    @Autowired
    TiendaRepository tiendaRepository;
    @Autowired
    AsignacionTiendasRepository asignacionTiendasRepository;
    @Autowired
    AsignadosSedesRepository asignadosSedesRepository;

    @GetMapping(value = {"", "/"})
    public String ListVentasCliente(@ModelAttribute("ventas") Ventas ventas,
                                    Model model, HttpSession session){
        Usuarios sede = (Usuarios) session.getAttribute("usuario");
        model.addAttribute("listaVentas", ventasRepository.findByVendedor_Sede_IdsedeAndConfirmado(sede.getSede().getIdsede(),true));
        return "sede/ventasPorCliente";
    }


    @GetMapping("/borrar")
    public String borrarVenta(@RequestParam("id1") String numdoc,
                              @RequestParam("id2") int tipodoc,
                              RedirectAttributes attr) {


        Optional<Ventas> optVenta = ventasRepository.findById(new VentasId(tipodoc,numdoc));
        if (optVenta.isPresent()) {
            Ventas v = optVenta.get(); //Se obtiene la venta

            Optional<Tienda> optTienda = tiendaRepository.findByNombreAndDireccionAndRuc(v.getNombrecliente(),
                    v.getLugarventa(),v.getRucdni());

                AsignadosSedes as = asignadosSedesRepository.findById_Productoinventario_CodigoinventarioAndId_PrecioventaAndId_EstadoasignacionAndId_Sede(v.getInventario().getCodigoinventario(), v.getPrecioventa().floatValue(),2, v.getVendedor().getSede().getIdsede());

            //Se verifica si la venta es de una tienda
            if(optTienda.isPresent()){
                //Se obtiene la tienda
                Tienda t = optTienda.get();
                Optional<AsignacionTiendas> optAt = asignacionTiendasRepository.findByTienda_IdtiendaAndAsignadosSedes_Id_Precioventa(t.getIdtienda(), v.getPrecioventa().floatValue());
                System.out.println(t.getIdtienda());
                System.out.println(v.getPrecioventa().floatValue());
                //Verificación si aún existe la fila en Asignados a Tienda
                if(optAt.isPresent()){
                    AsignacionTiendas at = optAt.get();
                    asignacionTiendasRepository.borrar_venta_tienda(
                            as.getId().getSede().getIdsede(), as.getId().getProductoinventario().getCodigoinventario(),
                            as.getId().getEstadoasignacion(),as.getId().getPrecioventa(),v.getCantidad(), at.getIdtiendas());


                }
            //Si la venta no es una tienda se retorna el stock a Productos Confirmados
            }else{


                asignacionTiendasRepository.borrar_venta_as(
                        as.getId().getSede().getIdsede(), as.getId().getProductoinventario().getCodigoinventario(),
                        as.getId().getEstadoasignacion(),as.getId().getPrecioventa(),v.getCantidad());
            }

            ventasRepository.deleteById(new VentasId(tipodoc, numdoc));
            attr.addFlashAttribute("msgBorrado","Venta eliminada exitosamente");
        }
        return "redirect:/sede/ventasCliente";

    }




}
