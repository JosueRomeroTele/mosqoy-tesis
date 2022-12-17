package com.example.sw2.controller.gestor;

import com.example.sw2.Dao.StorageServiceDao;
import com.example.sw2.constantes.AsignadosSedesId;
import com.example.sw2.constantes.CustomConstants;
import com.example.sw2.entity.*;
import com.example.sw2.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
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

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.YearMonth;

//REPLANTEAR inventario entity

@Controller
@RequestMapping("/gestor/inventario")
public class InventarioController {
    @Autowired
    InventarioRepository inventarioRepository;
    @Autowired
    CategoriasRepository categoriasRepository;
    @Autowired
    ComunidadesRepository comunidadesRepository;
    @Autowired
    ProductosRepository productosRepository;
    @Autowired
    ArtesanosRepository artesanosRepository;
    @Autowired
    StorageServiceDao storageServiceDao;
    @Autowired
    AsignadosSedesRepository asignadosSedesRepository;
    @Autowired
    VentasRepository ventasRepository;

    @GetMapping(value = {"/"})
    public String red() {
        return "redirect:/gestor/inventario";
    }

    @GetMapping(value = {""})
    public String listInv(@ModelAttribute("inventario") Inventario inventario, Model m, HttpSession session) {
        m.addAttribute("listaInv", inventarioRepository.findAllByOrderByFechamodificacionDesc());
        session.setAttribute("controller","gestor/inventario");
        return "gestor/inventarioGestor";
    }

    @GetMapping("detalles")
    public String detallerInventario(@ModelAttribute("inventario") Inventario inventario,Model m,@RequestParam("id") String codigInv){
        Optional<Inventario> invenCod = inventarioRepository.findById(codigInv);
        if( !invenCod.isPresent() ){
            return "redirect: /gestor/inventario";
        }

        List<AsignadosSedes> ProEnSede= asignadosSedesRepository.productoInvEnSede(invenCod.get().getCodigoinventario(),CustomConstants.ESTADO_RECIBIDO_POR_SEDE);
        m.addAttribute("inventarioDescrip",invenCod.get());
        m.addAttribute("stockEnSede",ProEnSede);
        return "gestor/inventarioDetalles";
    }

    @GetMapping("historial")
    public String historialDelInventarioPro(Model m, @RequestParam("id") String codigoinv){
       List<Ventas> listaHis = ventasRepository.findByInventario_CodigoinventarioAndConfirmado(codigoinv,true);
       m.addAttribute("historial",listaHis);
        return "gestor/inventarioHistorial";
    }

    @GetMapping(value = {"/form/"})
    public String red2() {
        return "redirect:/gestor/inventario/form";
    }

    @GetMapping(value = {"/form"})
    public String form(@ModelAttribute("inventario") Inventario inventario, Model m) {
        listasCamposInv(m);
        List<Inventario> listaordenada = inventarioRepository.findAllByOrderByNumpedidoDesc();
        int num;
        if (listaordenada.isEmpty())
            num = 0;
        else
            num = listaordenada.get(0).getNumpedido();
        inventario.setNumpedido(num);
        return "gestor/inventarioGestorForm";
    }


    @PostMapping(value = {"/save"})
    public String save(@ModelAttribute("inventario") @Valid Inventario inventario,
                       BindingResult bindingResult, Model m, RedirectAttributes attributes,
                       @RequestParam("conDia") String[] conDiastr,
                       @RequestParam(value = "fechadia", required = false) String fechadia,
                       @RequestParam(value = "fechames", required = false) String fechames,
                       @RequestParam(value = "linea", required = false) String linea,
                       @RequestParam(name = "foto1", required = false) MultipartFile multipartFile) throws IOException {

        LocalDate fechadiaformat = null;
        YearMonth fechamesformat = null;
        LocalDate today = LocalDate.now();
        Boolean conDia = (conDiastr.length == 2);
        StorageServiceResponse s2 = null;

        if (conDia) {
            if (fechadia.isEmpty()) {
                bindingResult.rejectValue("dia", "error.user", "Ingrese una fecha.");
            } else {
                try {
                    DateTimeFormatter dateTimeFormat = DateTimeFormatter.ISO_DATE;
                    fechadiaformat = LocalDate.parse(fechadia, dateTimeFormat);
                    inventario.setFechaDiaFORMAT(fechadiaformat);

                    if (fechadiaformat.isAfter(today)) {
                        bindingResult.rejectValue("dia", "error.user", "No puede ser después de la fecha actual.");
                    }
                } catch (Exception e) {
                    bindingResult.rejectValue("dia", "error.user", "Ingrese una fecha válida.");
                }
            }

        } else {
            if (fechames.isEmpty()) {
                bindingResult.rejectValue("dia", "error.user", "Ingrese una fecha.");
            } else {
                try {
                    DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM");
                    fechamesformat = YearMonth.parse(fechames, dateTimeFormat);
                    inventario.setFechaMesFORMAT(fechamesformat);
                    if (fechamesformat.atDay(1).isAfter(today)) {
                        bindingResult.rejectValue("dia", "error.user", "No puede ser después de la fecha actual.");
                    }
                } catch (Exception e) {
                    bindingResult.rejectValue("dia", "error.user", "Ingrese una fecha válida.");
                }
            }
        }

        if (inventario.getTipoAdquisicion().equalsIgnoreCase("consignado")) {
            if (inventario.getArtesanos() == null) {
                bindingResult.rejectValue("artesanos", "error.user", "Seleccione un artesano de la lista.");
            } else {
                inventario.setComunidades(inventario.getArtesanos().getComunidades());
            }

            if (!bindingResult.hasFieldErrors("fechavencimientoconsignacion")) {
                if (inventario.getFechavencimientoconsignacion() == null) {
                    bindingResult.rejectValue("fechavencimientoconsignacion", "error.user", "Ingrese una fecha.");
                } else {
                    if (fechadiaformat != null) {
                        if (!inventario.getFechavencimientoconsignacion().isAfter(fechadiaformat)) {
                            bindingResult.rejectValue("fechavencimientoconsignacion", "error.user", "Debe ser posterior a la adquisición.");
                        }
                    }
                    if (fechamesformat != null) {
                        if (!inventario.getFechavencimientoconsignacion().isAfter(fechamesformat.atEndOfMonth())) {
                            bindingResult.rejectValue("fechavencimientoconsignacion", "error.user", "Debe ser posterior a la adquisición.");
                        }
                    }

                }
            }


        }
        /*
        Optional<Inventario> optionalInventario = inventarioRepository.findInventariosByNumpedido(inventario.getNumpedido());
        if (optionalInventario.isPresent()) {
            bindingResult.rejectValue("numpedido", "error.user", "Este número de pedido ya existe.");
        }
        */
        Set<String> keySetT = CustomConstants.getTamanhos().keySet();
        if (!keySetT.contains(inventario.getCodtamanho())) {
            bindingResult.rejectValue("codtamanho", "error.user", "Seleccione un tamaño de la lista.");
        }

        Set<String> keySetL = CustomConstants.getLineas().keySet();
        if (!keySetL.contains(inventario.getProductos().getId().getCodigolinea())) {
            bindingResult.rejectValue("cantidadgestor", "error.user", "Seleccione una línea de la lista.");
        } else {
            m.addAttribute("linea", inventario.getProductos().getId().getCodigolinea());
            m.addAttribute("listProd", productosRepository.findProductosByIdCodigolinea(inventario.getProductos().getId().getCodigolinea()));
        }

        //Gustavo lo puso :
        //System.out.println(linea);
        //inventario.getProductos().setCodigolinea(linea);
        Productos prod = productosRepository.findById(inventario.getProductos().getId()).orElse(null);
        if (prod == null) {
            bindingResult.rejectValue("productos", "error.user", "Seleccione un producto de la lista.");
        } else {
            m.addAttribute("pr", inventario.getProductos().getId().getCodigonom());
            inventario.setProductos(prod);
        }
/*
        if (multipartFile.isEmpty()) {
            bindingResult.rejectValue("foto", "error.user", "Debe subir una foto.");
        }
*/
        if (!bindingResult.hasFieldErrors("costotejedor") && !bindingResult.hasFieldErrors("costomosqoy")) {
            if (inventario.getCostotejedor().compareTo(inventario.getCostomosqoy()) != -1) {
                bindingResult.rejectValue("costomosqoy", "error.user", "Debe ser mayor al costo tejedor.");
            }
        }

        if (bindingResult.hasErrors()) {
            listasCamposInv(m);
            if (inventario.getComunidades() != null) {
                m.addAttribute("listArt", artesanosRepository.findArtesanosByComunidades_Codigo(inventario.getComunidades().getCodigo()));
            }
            return "gestor/inventarioGestorForm";
        } else {
            String codInv = generaCodigo(inventario);
           Optional<Inventario> optionalInventario1 = inventarioRepository.findById(codInv);
            if (optionalInventario1.isPresent()) {
                m.addAttribute("msg", "El código " + codInv + " ya existe en el inventario. Si desea aumentar la cantidad de este producto, revise el inventario."
                );

                listasCamposInv(m);
                m.addAttribute("listArt", artesanosRepository.findAll());
                m.addAttribute("listProd", productosRepository.findAll());
                return "gestor/inventarioGestorForm";
            }
/*
            // subida de FOTO
            try {
                StorageServiceResponse s2 = storageServiceDao.store(inventario, multipartFile);
                if (!s2.isSuccess()) {
                    bindingResult.rejectValue("foto", "error.user", s2.getMsg());
                    listasCamposInv(m);
                    if (inventario.getComunidades() != null) {
                        m.addAttribute("listArt", artesanosRepository.findArtesanosByComunidades_Codigo(inventario.getComunidades().getCodigo()));
                    }
                    return "gestor/inventarioGestorForm";
                }
            } catch (HttpClientErrorException e) {
                bindingResult.rejectValue("foto", "error.user", "Error en foto");
                listasCamposInv(m);
                if (inventario.getComunidades() != null) {
                    m.addAttribute("listArt", artesanosRepository.findArtesanosByComunidades_Codigo(inventario.getComunidades().getCodigo()));
                }
                return "gestor/inventarioGestorForm";
            }
*/
            inventario.setCodigoinventario(codInv);

            if (!multipartFile.isEmpty()) {
                HashMap<String,String> s21 =  storageServiceDao.store(inventario, multipartFile);
                if (s21.equals(null)) {
                    //retorna falso
                    bindingResult.rejectValue("foto", "error.user", "problema con respecto a la foto");
                    listasCamposInv(m);
                    if (inventario.getComunidades() != null) {
                        m.addAttribute("listArt", artesanosRepository.findArtesanosByComunidades_Codigo(inventario.getComunidades().getCodigo()));
                    }
                    return "gestor/inventarioGestorForm";
                }
            } else {
                inventario.setFoto("imagenNoDisponible.png");
               /* bindingResult.rejectValue("foto", "error.user", "Debe seleccionar una imagen");
                listasCamposInv(m);
                if (inventario.getComunidades() != null) {
                    m.addAttribute("listArt", artesanosRepository.findArtesanosByComunidades_Codigo(inventario.getComunidades().getCodigo()));
                }
                return "gestor/inventarioGestorForm";*/
            }

            inventario.setCantidadgestor(inventario.getCantidadtotal());
            inventario.setDevuelto_artesano(false);
            System.out.println(inventario.getCodigoinventario());

            try {
                inventarioRepository.save(inventario);
                attributes.addFlashAttribute("msg", "Producto registrado exitosamente! Codigo generado: " + codInv);

            } catch (Exception e) {
                attributes.addFlashAttribute("msgError", "ERROR DE REGISTRO");
            }


            return "redirect:/gestor/inventario";
        }
    }

    @PostMapping("/addInv")
    public String addInv(@RequestParam("cant") String cant, @RequestParam("codinv") String codinv, RedirectAttributes attributes, Model m) {

        Optional<Inventario> opt = inventarioRepository.findById(codinv);
        if (!opt.isPresent() ) {
            attributes.addFlashAttribute("msgError", "PRODUCTO NO ENCONTRADO");
            return "redirect:/gestor/inventario";

        }
        if (opt.get().getCodAdquisicion()==1){
            attributes.addFlashAttribute("msgError", "EL PRODUCTO ES CONSIGNADO, NO SE PUEDE AGREGAR CANTIDAD");
            return "redirect:/gestor/inventario";
        }

        int cantInt;
        try {
            cantInt = Integer.parseInt(cant);
            if (cantInt < 1) {
                throw new Exception("");
            }
        } catch (Exception e) {
            attributes.addFlashAttribute("cantError", "Cantidad no válida.");
            attributes.addFlashAttribute("cant", cant);
            attributes.addFlashAttribute("codinv", codinv);
            attributes.addFlashAttribute("msgError", "ERROR DE CANTIDAD");
            return "redirect:/gestor/inventario";
        }


        Inventario inventario = opt.get();
        inventario.setCantidadtotal(cantInt + inventario.getCantidadtotal());
        inventario.setCantidadgestor(cantInt + inventario.getCantidadgestor());
        if (cantInt == 1) {
            attributes.addFlashAttribute("msg", cantInt + " producto de código " + inventario.getCodigoinventario() + " añadidos exitosamente!");
        } else {
            attributes.addFlashAttribute("msg", cantInt + " productos de código " + inventario.getCodigoinventario() + " añadidos exitosamente!");
        }


        return "redirect:/gestor/inventario";
    }


    @PostMapping("/editInv")
    public String editInv(@ModelAttribute("inventario") @Valid Inventario inv,
                          BindingResult bindingResult,
                          RedirectAttributes att,
                          Model m) {
        Optional<Inventario> opt = inventarioRepository.findById(inv.getCodigoinventario());


        if (!opt.isPresent()) {
            att.addFlashAttribute("msgError", "PRODUCTO NO ENCONTRADO");

        } else {
            Inventario invOld = opt.get();

            if (inv.getCodAdquisicion() == 1) {
                if (inv.getFechavencimientoconsignacion() == null) {
                    bindingResult.rejectValue("fechavencimientoconsignacion", "error.user", "Ingrese una fecha.");
                }
                if (!bindingResult.hasFieldErrors("fechavencimientoconsignacion")) {
                    if (invOld.getDia() == 0) {
                        DateTimeFormatter mesFormat = DateTimeFormatter.ofPattern("yyyy-MM");

                        String fechames = invOld.getAnho() + "-";
                        if (invOld.getMes() < 10) {
                            fechames += "0" + invOld.getMes();
                        } else {
                            fechames += invOld.getMes();
                        }

                        YearMonth fechamesformat = YearMonth.parse(fechames, mesFormat);

                        if (fechamesformat != null) {
                            if (!inv.getFechavencimientoconsignacion().isAfter(fechamesformat.atEndOfMonth())) {
                                bindingResult.rejectValue("fechavencimientoconsignacion", "error.user", "Debe ser posterior a la adquisición (" + fechamesformat + ")");
                            }
                        }
                    } else {
                        LocalDate fechadiaformat = invOld.getFechaadquisicion();
                        if (fechadiaformat != null) {
                            if (!inv.getFechavencimientoconsignacion().isAfter(fechadiaformat)) {
                                bindingResult.rejectValue("fechavencimientoconsignacion", "error.user", "Debe ser posterior a la adquisición (" + fechadiaformat + ")");
                            }
                        }
                    }
                }
            }
            if (!bindingResult.hasFieldErrors("costotejedor") && !bindingResult.hasFieldErrors("costomosqoy")) {
                if (inv.getCostotejedor().compareTo(inv.getCostomosqoy()) != -1) {
                    bindingResult.rejectValue("costomosqoy", "error.user", "Debe ser mayor al costo tejedor.");
                }
            }
            if (bindingResult.hasFieldErrors("facilitador") || bindingResult.hasFieldErrors("costomosqoy") || bindingResult.hasFieldErrors("costotejedor") || bindingResult.hasFieldErrors("fechavencimientoconsignacion")) {
                m.addAttribute("listaInv", inventarioRepository.findAllByOrderByFechamodificacionDesc());
                m.addAttribute("msgError", "ERROR DE EDICION");
                return "gestor/inventarioGestor";
            } else {
                invOld.setCostotejedor(inv.getCostotejedor());
                invOld.setCostomosqoy(inv.getCostomosqoy());

                invOld.setFacilitador(inv.getFacilitador());
                if (inv.getCodAdquisicion() == 1) {
                    invOld.setFechavencimientoconsignacion(inv.getFechavencimientoconsignacion());
                }
                inventarioRepository.save(invOld);
                att.addFlashAttribute("msg", "Producto " + invOld.getCodigoinventario() + " actualizado exitosamente!");
            }

        }
        return "redirect:/gestor/inventario";
    }


    @GetMapping("/delete")
    public String borrar(Model model,
                         @RequestParam("codigoinventario") String id,
                         RedirectAttributes attr) {
        Optional<Inventario> c = inventarioRepository.findById(id);
        if (c.isPresent()) {
            try {
                inventarioRepository.deleteById(id);
            } catch (Exception e) {
                attr.addFlashAttribute("msgError", "El registro seleccionado no puede ser borrado");
                return "redirect:/gestor/inventario";
            }

            attr.addFlashAttribute("msg", "Registro borrado exitosamente!");
        } else {
            attr.addFlashAttribute("msgError", "El registro seleccionado no existe");
        }
        return "redirect:/gestor/inventario";
    }

    @GetMapping("/devArtesano")
    public String devolverArtesano(Model m, @RequestParam("codigoinventario") String invId, RedirectAttributes attributes){

        Optional<Inventario> byId = inventarioRepository.findById(invId);
        if (byId.isPresent() && byId.get().getCodAdquisicion()==1 && byId.get().getCantidadtotal()>0 ){
            byId.get().setDevuelto_artesano(true);
                inventarioRepository.save(byId.get());
            String ArtesanoString = byId.get().getArtesanos().getNombre() + " " +byId.get().getArtesanos().getApellidopaterno() + " " + byId.get().getArtesanos().getApellidomaterno();
            attributes.addFlashAttribute("msg", "Producto devuelto al artesan@ "+ ArtesanoString + " | Codigo : " + byId.get().getCodigoinventario());
        }else{
            if (byId.get().getCantidadtotal()==0){
                attributes.addFlashAttribute("msgError", "NO SE PUEDE DEVOLVER AL ARTESANO SI NO TIENE PRODUCTOS.");
            }
            attributes.addFlashAttribute("msgError", "EL CODIGO NO FUE ENCONTRADO");
        }
        return "redirect:/gestor/inventario";
    }

    @PostMapping("addFotoInv")
    public String agregarFotoAlInventario( @RequestParam("addFoto1") MultipartFile multipartFile,@RequestParam("codigoinventario") String codInv,RedirectAttributes attr) throws IOException {
        Optional<Inventario> invFoto = inventarioRepository.findById(codInv);

        if (invFoto.isPresent()){
            if (invFoto.get().getFoto()!="Imagen_no_disponible.png" || invFoto.get().getFoto()!="imagenNoDisponible.png" ){
                if (!multipartFile.isEmpty()) {
                    HashMap<String,String> s21 =  storageServiceDao.store(invFoto.get(), multipartFile);
                    if (s21.equals(null)) {
                        //retorna falso
                        //bindingResult.rejectValue("foto", "error.user", "problema con respecto a la foto");
                        attr.addFlashAttribute("msgError","Hubo problemas para cargar la imagen");
                        return "redirect:/gestor/inventario";
                    }else{
                        attr.addFlashAttribute("msg","Se cargo correctamente la foto del inventario " +invFoto.get().getCodigoinventario());
                        return "redirect:/gestor/inventario";
                    }
                } else {
                    attr.addFlashAttribute("msgError","Verificar que se haya subido la imagen " );
                    return "redirect:/gestor/inventario";
                }
            }else{
                attr.addFlashAttribute("msgError","El inventario ya posee una imagen");
                return "redirect:/gestor/inventario";
            }

        }else {
            attr.addFlashAttribute("msgError","NO SE ENCONTRO EL INVENTARIO");
            return "redirect:/gestor/inventario";
        }

    }



    private void listasCamposInv(Model m) {
        LocalDate todayd = LocalDate.now();
        YearMonth todaym = YearMonth.now();
        m.addAttribute("taman", CustomConstants.getTamanhos());
        m.addAttribute("tipoAdqui", CustomConstants.getTiposAdquisicion());
        m.addAttribute("lineas", CustomConstants.getLineas());
        m.addAttribute("listCat", categoriasRepository.findAll());
        m.addAttribute("listCom", comunidadesRepository.findAll());
        m.addAttribute("todayd", todayd);
        m.addAttribute("todaym", todaym);
    }

    private String generaCodigo(Inventario inv) {
        String cod = inv.getProductos().getCodigolinea()
                + inv.getCategorias().getCodigo()
                + inv.getProductos().getCodigonom()
                + inv.getProductos().getCodigodesc()
                + inv.getCodtamanho()
                + inv.getComunidades().getCodigo();
        if (inv.getCodAdquisicion() == 1) {
            int anho = inv.getAnho() % 100;
            String mes = CustomConstants.getMeses().get(inv.getMes());

            cod += inv.getArtesanos().getCodigo()
                    + anho
                    + mes;
        }
        return cod;
    }

    //Web Service
    @ResponseBody
    @GetMapping(value = "/getAsigSede",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AsignadosSedes>>getAsigSede(@RequestParam("id") String inv){
        return new ResponseEntity<>(asignadosSedesRepository.findProductoAunEnSedes(inv),HttpStatus.OK);
    }

    //Web service
    @ResponseBody
    @GetMapping(value = {"/getInv", "/editInv/getInv", "/addInv/getInv","/detalles"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Optional<Inventario>> getInv(@RequestParam(value = "id") String cod) {
        return new ResponseEntity<>(inventarioRepository.findById(cod), HttpStatus.OK);
    }

    //Web service
    @ResponseBody
    @GetMapping(value = {"/form/getLinea", "/save/getLinea"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Productos>> getCom(@RequestParam(value = "linea") String linea) {
        return new ResponseEntity<>(productosRepository.findProductosByIdCodigolinea(linea), HttpStatus.OK);
    }

    //Web service
    @ResponseBody
    @GetMapping(value = {"/form/getArtesanos", "/save/getArtesanos"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Artesanos>> getArtesanos(@RequestParam(value = "comunidad") String comunidad) {
        return new ResponseEntity<>(artesanosRepository.findArtesanosByComunidades_Codigo(comunidad), HttpStatus.OK);
    }

    //Has items
    @ResponseBody
    @GetMapping(value = {"/has", "/editInv/has", "/addInv/has"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<List<HashMap<String, String>>>> hasItems(@RequestParam(value = "id") String id) {
        return new ResponseEntity<>(new ArrayList<List<HashMap<String, String>>>() {
            {
                add(new ArrayList<HashMap<String, String>>() {{
                    Objects.requireNonNull(inventarioRepository.findById(id).orElse(null)).getAsignadosSedes().forEach((i) -> {
                        add(new HashMap<String, String>() {{
                            put("sede", i.getId().getSede().getNombresede());
                            put("stock", i.getStock().toString());
                            put("envio", i.getFechaEnvioStr());
                            put("precio",i.getId().getPrecioventa().toString());
                        }});
                    });
                }});
                add(new ArrayList<HashMap<String, String>>() {{
                    Objects.requireNonNull(inventarioRepository.findById(id).orElse(null)).getVentas().forEach((i) -> {
                        add(new HashMap<String, String>() {{
                            put("numdocumento", i.getId().getNumerodocumento());
                            put("fechaVenta", i.getFechaDeVentaStr());
                            put("vendedor", i.getVendedor().getFullname());
                        }});
                    });
                }});
            }
        },
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


  /*  @GetMapping(value = "/cantidadTienda",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AsignadosSedes> getCantidadEnTienda(@RequestParam(value = "id1") String codigoInv, @RequestParam(value = "id2") int Idsede, @RequestParam(value = "id3") float precioventa){


        return new ResponseEntity<>();
    }*/
}
