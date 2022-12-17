package com.example.sw2.controller.sede;

import com.example.sw2.entity.Inventario;
import com.example.sw2.entity.Usuarios;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/sede/ventasProducto")
public class VentasProductoController {

    @Autowired
    VentasRepository ventasRepository;
    @Autowired
    UsuariosRepository usuariosRepository;
    @Autowired
    InventarioRepository inventarioRepository;

    @GetMapping(value = {"", "ListaVentasProductos"})
    public String ListaVentasCliente(Model model, HttpSession session){

        Usuarios sede = (Usuarios) session.getAttribute("usuario");
        model.addAttribute("listaProductos", ventasRepository.obtenerDatosPorProducto(sede.getSede().getIdsede()));
        return "sede/ventasPorProducto";
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
