package com.example.sw2.controller.gestor;

import com.example.sw2.constantes.CustomConstants;
import com.example.sw2.entity.AsignadosSedes;
import com.example.sw2.entity.Inventario;
import com.example.sw2.repository.AsignadosSedesRepository;
import com.example.sw2.repository.InventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/gestor/consignacion")
public class GestionarConsignacion {
        @Autowired
    AsignadosSedesRepository asignadosSedesRepository;
    @Autowired
    InventarioRepository inventarioRepository;
    private int estado_devuelto_artesano = CustomConstants.ESTADO_DEVUELTO_AL_ARTESANO;
    private  int esteMes = LocalDate.now().getMonthValue();
    private  int hoy_dia = LocalDate.now().getDayOfMonth();
    private int este_ano = LocalDate.now().getYear();
    @GetMapping("ProxVencer")
    public String ProductosProximoVencer(Model m){
        if (esteMes==12){
            List<Inventario> proximoAVencer = inventarioRepository.findVencenFinDeAno(este_ano,este_ano+1);
            m.addAttribute("proximoAVencer",proximoAVencer);
        }else{
            List<Inventario> proximoAVencer = inventarioRepository.findProximoAVencer(esteMes, esteMes + 1, hoy_dia,este_ano);
            m.addAttribute("proximoAVencer",proximoAVencer);
        }

        return "gestor/proximoVencer";
    }

    @GetMapping("Vencido")
    public String productoVencido(Model m){
        List<Inventario> productosVencidos = inventarioRepository.findProductosVencidos(esteMes, hoy_dia,este_ano);
        m.addAttribute("productosVencidos",productosVencidos);
        return "gestor/consignacionVencida";
    }

    @GetMapping("DevueltosArtesano")
    public String productoDevueltoArtesano(Model model){
        List<Inventario> byDevuelto_artesano = inventarioRepository.listaDevueltosAlArtesano();
        model.addAttribute("DevueltosArtesano",byDevuelto_artesano);
        return "gestor/productoDevueltoArtesano";
    }
}
