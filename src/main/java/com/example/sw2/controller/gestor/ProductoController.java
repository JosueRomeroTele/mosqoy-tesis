package com.example.sw2.controller.gestor;


import com.example.sw2.constantes.CustomConstants;
import com.example.sw2.constantes.ProductoId;
import com.example.sw2.entity.Inventario;
import com.example.sw2.entity.Productos;
import com.example.sw2.repository.InventarioRepository;
import com.example.sw2.repository.ProductosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/gestor/producto")
public class ProductoController {


    @Autowired
    ProductosRepository productosRepository;
    @Autowired
    InventarioRepository inventarioRepository;

    @GetMapping(value = {"/"})
    public String redirectAT(){
        return "redirect:/gestor/producto";
    }

    @GetMapping(value = {""})
    public String listar(@ModelAttribute("producto") Productos productos, Model model) {
        model.addAttribute("lista", productosRepository.findAll());
        model.addAttribute("lineas", CustomConstants.getLineas());
        return "gestor/productos";
    }

    @GetMapping(value = {"/registroProdCompras"})
    public String registroDeCompras(RedirectAttributes attributes,Model model,@RequestParam("id")String codinom, @RequestParam("li") String linea){

        Optional<Productos> productoCodCompra = productosRepository.findProductosByIdCodigonomAndIdCodigolinea(codinom, linea);
        if (productoCodCompra.isPresent()){
            List<Inventario> listProductosComprados = inventarioRepository.listProductosComprados(codinom, linea);
            if (listProductosComprados.isEmpty()){
                attributes.addFlashAttribute("msgCompra","No hay compras con respecto a al producto " + productoCodCompra.get().getNombre() + " de linea " + productoCodCompra.get().getNombreLinea());
                return "redirect:/gestor/producto";
            }else{
                model.addAttribute("listaProd",listProductosComprados);
                return "gestor/productosComprados";
            }
        }else {
            attributes.addFlashAttribute("msgCompra","No existe ese codigo");
            return "redirect:/gestor/producto";
        }


    }

    @PostMapping("/save")
    public String editar(@ModelAttribute("producto") @Valid Productos productos,
                          BindingResult bindingResult, @RequestParam("type") int type,
                         RedirectAttributes attr, Model model) {

        if(type==1 && productosRepository.findById(productos.getId()).isPresent()){ //if new
            bindingResult.rejectValue("id.codigonom","error.user","Este codigo ya existe en la linea "+productos.getId().getNombreLinea());
        }

        if (!productos.getId().validateCodigoLinea()){
            bindingResult.rejectValue("id.codigolinea","error.user","Elija una línea válida");
        }

        if(bindingResult.hasErrors()){
            model.addAttribute("formtype",Integer.toString(type));
            model.addAttribute("lista", productosRepository.findAll());
            model.addAttribute("msgError", "ERROR");
            model.addAttribute("lineas", CustomConstants.getLineas());
            return "gestor/productos";
        }
        else {
            Optional<Productos> optionalProductos = productosRepository.findById(productos.getId());
            if (optionalProductos.isPresent() && type==0) {
                attr.addFlashAttribute("msg", "Producto actualizado exitosamente");
            }
            else if (type==1){
                attr.addFlashAttribute("msg", "Producto creado exitosamente");
            }
            else {
                attr.addFlashAttribute("msgError", "Hubo un problema, no se pudo guardar");
                return "redirect:/gestor/producto";
            }
            productosRepository.save(productos);
            return "redirect:/gestor/producto";
        }
    }

    @GetMapping("/delete")
    public String borrar(Productos productos,
                            RedirectAttributes attr) {
        Optional<Productos> c = productosRepository.findById(productos.getId());
        if (c.isPresent()) {
            productosRepository.deleteById(productos.getId());
            attr.addFlashAttribute("msg","Producto borrado exitosamente");
        }
        return "redirect:/gestor/producto";
    }

    //Web service
    @ResponseBody
    @GetMapping(value = "/get",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Optional<Productos>> getCat(@RequestParam(value = "codigonom") String cod,
                                                      @RequestParam(value = "codigolinea") String lin){
        return new ResponseEntity<>(productosRepository.findById(new ProductoId(cod,lin)), HttpStatus.OK);
    }


    @ResponseBody
    @GetMapping(value = "/has", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<HashMap<String,String>>> hasItems(@RequestParam(value = "codigonom") String cn,
                                                                 @RequestParam(value = "codigolinea") String cl){
        return new ResponseEntity<>(new ArrayList<HashMap<String,String>>() {{
            Objects.requireNonNull(productosRepository.findById(new ProductoId(cn,cl)).orElse(null)).getInventario().forEach((i)->
            {
                add(new HashMap<String, String>() {
                    {
                        put("codigo", i.getCodigoinventario());
                        put("comunidad", i.getComunidades().getNombre());
                        put("categoria", i.getCategorias().getNombre());
                    }
                });
            });
        }},
                HttpStatus.OK);
    }

}
