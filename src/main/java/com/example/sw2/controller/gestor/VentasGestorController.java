package com.example.sw2.controller.gestor;

import com.example.sw2.constantes.AsignadosSedesId;
import com.example.sw2.constantes.VentasId;
import com.example.sw2.entity.AsignadosSedes;
import com.example.sw2.entity.Usuarios;
import com.example.sw2.entity.Ventas;
import com.example.sw2.repository.UsuariosRepository;
import com.example.sw2.repository.VentasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.util.Optional;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/gestor/venta")
public class VentasGestorController {

    @Autowired
    VentasRepository ventasRepository;

    @Autowired
    UsuariosRepository usuariosRepository;


    @GetMapping(value = {""})
    public String listVen(@ModelAttribute("venta") Ventas ven,
                          Model model,
                          HttpSession session) {
        Usuarios gestor = (Usuarios) session.getAttribute("usuario");
        model.addAttribute("lista", ventasRepository.buscarPorGestor(gestor.getIdusuarios()));
        return "gestor/ventas";
    }

    @GetMapping(value = {"/"})
    public String listVen2() {
        return "redirect:/gestor/venta";
    }

    @PostMapping("/save")
    public String editVen(@ModelAttribute("venta") @Valid Ventas ventas,
                          BindingResult bindingResult, RedirectAttributes attr, Model model,
                          //@RequestParam("id1") String id1,
                          //@RequestParam("id2") int id2,
                          HttpSession session) {

        if (!ventas.getRucdni().isEmpty()) {
            if (ventas.getRucdni().trim().length() == 9) {
                bindingResult.rejectValue("rucdni", "error.user", "Ingrese un Ruc(11 dígitos) / DNI(8 dígitos) válido.");
            }
            if (ventas.getRucdni().trim().length() == 10) {
                bindingResult.rejectValue("rucdni", "error.user", "Ingrese un Ruc(11 dígitos) / DNI(8 dígitos) válido.");
            }
            if (ventas.getRucdni().trim().length() < 8) {
                bindingResult.rejectValue("rucdni", "error.user", "Ingrese un Ruc(11 dígitos) / DNI(8 dígitos) válido.");
            }
            if (ventas.getRucdni().trim().length() > 11) {
                bindingResult.rejectValue("rucdni", "error.user", "Ingrese un Ruc(11 dígitos) / DNI(8 dígitos) válido.");
            }
            if (!Pattern.compile("[0-9]").matcher(ventas.getRucdni()).find()) {
                bindingResult.rejectValue("rucdni", "error.user", "Ingrese solo valores numéricos.");
            }
        }

        if (!ventas.getNombrecliente().isEmpty()) {
            if (Pattern.compile("[0-9]").matcher(ventas.getNombrecliente()).find()) {
                bindingResult.rejectValue("nombrecliente", "error.user", "Ingrese un nombre válido.");
            }
            if (ventas.getNombrecliente().trim().length() == 0) {
                bindingResult.rejectValue("nombrecliente", "error.user", "Ingrese un nombre.");
            }
        }

        Usuarios gestor = (Usuarios) session.getAttribute("usuario");

        if (bindingResult.hasFieldErrors("rucdni") || bindingResult.hasFieldErrors("nombrecliente")) {
            model.addAttribute("lista", ventasRepository.buscarPorGestor(gestor.getIdusuarios()));
            model.addAttribute("msgError", "ERROR");
            return "gestor/ventas";
        } else {
            Optional<Ventas> optionalVentas = ventasRepository.findById(ventas.getIdventas());
            if (optionalVentas.isPresent()) {
                Ventas ven = optionalVentas.get();
                ven.setRucdni(ventas.getRucdni());
                ven.setNombrecliente(ventas.getNombrecliente());
                attr.addFlashAttribute("msg", "Venta actualizada exitosamente");
                ventasRepository.save(ven);
            } else {
                attr.addFlashAttribute("msgError", "Hubo un problema, no se pudo guardar");
                return "redirect:/gestor/venta";
            }
        }

        return "redirect:/gestor/venta";
    }

    @GetMapping("/delete")
    public String deleteVen(@RequestParam("id1") String id1,
                            @RequestParam("id2") int id2,
                            RedirectAttributes attr) {
        Optional<Ventas> v = ventasRepository.findById(new VentasId(id2, id1));
        if (v.isPresent()) {
            ventasRepository.deleteById(new VentasId(id2, id1));
            attr.addFlashAttribute("msg", "Venta borrada exitosamente");
        }
        return "redirect:/gestor/venta";
    }

    @GetMapping("/cancelar")
    public String cancelarVenta(@ModelAttribute("venta") @Valid Ventas ventas,
                                BindingResult bindingResult,
                                Model model,
                                //@RequestParam(value = "nota") int nota,
                                //@RequestParam(value = "mensaje") String mensaje,
                                //@RequestParam("id1") String id1,
                                //@RequestParam("id2") int id2,
                                RedirectAttributes attr,
                                HttpSession session) {

        if (ventas.getMensaje().isEmpty()) {
            bindingResult.rejectValue("mensaje", "error.user", "Debe ingresar el problema por el cual se quiere anular la venta.");
        }

        if (!(ventas.getNota()!=null &&
                (ventas.getNota()>10000) && (ventas.getNota()<999999999))){
            bindingResult.rejectValue("nota", "error.user", "Debe ingresar una nota válida");
        }

        Usuarios gestor = (Usuarios) session.getAttribute("usuario");

        if (bindingResult.hasFieldErrors("mensaje") || bindingResult.hasFieldErrors("nota")) {
            model.addAttribute("lista", ventasRepository.buscarPorGestor(gestor.getIdusuarios()));
            model.addAttribute("msgError", "ERROR_cancelar");
            return "gestor/ventas";
        } else {
            Optional<Ventas> V = ventasRepository.findById(ventas.getIdventas());

            if (V.isPresent()) {
                Ventas ven = V.get();

                int idgestor = ven.getVendedor().getIdusuarios();
                ven.setCancelar(idgestor);
                Usuarios idadmin = usuariosRepository.obtenerAdmin();
                ven.setVendedor(idadmin);

                ven.setNota(ventas.getNota());
                ven.setMensaje(ventas.getMensaje());
                attr.addFlashAttribute("msg", "Se ha reportado la cancelación de la venta al administrador");

                ventasRepository.save(ven);
            }
            return "redirect:/gestor/venta";
        }
    }


    //Web service
    @ResponseBody
    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Optional<Ventas>> getVen(@RequestParam(value = "id") int id) {
        return new ResponseEntity<>(ventasRepository.findById(id), HttpStatus.OK);
    }


    /*

/*    @GetMapping(value = {"/excelAnual2020"})
    public ResponseEntity<InputStreamResource> exportAllData() throws Exception{

        ByteArrayInputStream stream = serviceAnual.exportAllData();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition","attachment; filename=ventasAnual.xls");

        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(stream));
    }*/


}
