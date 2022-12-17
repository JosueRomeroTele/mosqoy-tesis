package com.example.sw2.controller.admin;

import com.example.sw2.constantes.AsignadosSedesId;
import com.example.sw2.constantes.VentasId;
import com.example.sw2.entity.AsignadosSedes;
import com.example.sw2.entity.Inventario;
import com.example.sw2.entity.Usuarios;
import com.example.sw2.entity.Ventas;
import com.example.sw2.repository.UsuariosRepository;
import com.example.sw2.repository.VentasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Optional;

@Controller
@RequestMapping("/admin/venta")
public class VentasCancelarController {

    @Autowired
    VentasRepository ventasRepository;

    @Autowired
    UsuariosRepository usuariosRepository;

    @GetMapping(value = {""})
    public String listVen(@ModelAttribute("gestor") Usuarios u,
                          //@RequestParam(value = "id", required = false) Integer dni,
                          //@RequestParam(value = "id1", required = false) Integer idventas,
                          @ModelAttribute("venta") Ventas venta,
                          Model model,
                          HttpSession session) {
        Usuarios admin = (Usuarios) session.getAttribute("usuario");
        model.addAttribute("lista", ventasRepository.buscarVentasDeAdmin(admin.getRoles().getIdroles()));
        return "admin/ventasParaCancelar";
    }

    @GetMapping(value = {"/"})
    public String listVen2() {
        return "redirect:/admin/venta";
    }

    @GetMapping("/confirmar")
    public String confirmarCancelacion(
                            @RequestParam("id2") int id2,
                            HttpSession session,
                            RedirectAttributes attr) {

        Optional<Ventas> V = ventasRepository.findById(id2);

        if (V.isPresent()) {
            Ventas V2 = V.get();
            ventasRepository.dev_stock_inv(V2.getCantidad(), V2.getInventario().getCodigoinventario());

            ventasRepository.deleteById(id2);
            attr.addFlashAttribute("msg","Venta cancelada exitosamente");
        }
        return "redirect:/admin/venta";
    }

    @GetMapping("/rechazar")
    public String rechazarCancelacion(
                                      @RequestParam("id3") int id3,
                                      HttpSession session,
                                      RedirectAttributes attr) {

        Optional<Ventas> V = ventasRepository.findById(id3);

        if (V.isPresent()) {
            Ventas V2 = V.get();
            String msg = null;
            int nc = 0;
            V2.setMensaje(msg);
            V2.setNota(nc);

            int vendedor = V2.getCancelar();
            Usuarios idgestor = usuariosRepository.findByIdusuarios(vendedor);
            V2.setVendedor(idgestor);
            int cancel = 0;
            V2.setCancelar(cancel);

            ventasRepository.save(V2);
            attr.addFlashAttribute("msg","Se ha rechazado la cancelación de la venta exitosamente");
        }
        return "redirect:/admin/venta";
    }


    //Web service
    @ResponseBody
    @GetMapping(value = "/get",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Optional<Usuarios>> getGestor(@RequestParam(value = "id") int id){
        return new ResponseEntity<>(usuariosRepository.findById(id), HttpStatus.OK);
    }

    //Web service
    @ResponseBody
    @GetMapping(value = "/has",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String,String>> getVenta(@RequestParam(value = "id1") int id1){
        HashMap<String,String> map = new HashMap<>();
        Ventas v = ventasRepository.findById(id1).orElse(null);
        if (v!=null){
            map.put("nota",v.getNota().toString());
            map.put("mensaje",v.getMensaje());
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }


}
