package com.example.sw2.service;

import com.example.sw2.constantes.CustomConstants;
import com.example.sw2.constantes.ProductoId;
import com.example.sw2.dtoReportes.ReportesArticuloDto;
import com.example.sw2.dtoReportes.ReportesClienteDto;
import com.example.sw2.dtoReportes.ReportesComunidadDto;
import com.example.sw2.dtoReportes.ReportesTotalDto;
import com.example.sw2.dtoReportes.ReportesSedesDto;
import com.example.sw2.entity.*;
import com.example.sw2.repository.ComunidadesRepository;
import com.example.sw2.repository.ProductosRepository;
import com.example.sw2.repository.SedeRepository;
import com.example.sw2.repository.VentasRepository;
import com.example.sw2.utils.ReportesUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class    ReporteGestorService extends ReportesUtils implements IReporteGestorService {

    @Autowired
    VentasRepository ventasRepository;
    @Autowired
    SedeRepository sedeRepository;
    @Autowired
    ProductosRepository productosRepository;
    @Autowired
    ComunidadesRepository comunidadesRepository;

    @Override
    public ByteArrayInputStream generarReporte(Reportes reportes) throws Exception{

        Workbook workbook = new HSSFWorkbook();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        switch (reportes.getOrderBy()){
            case 1: //nos referimos al total
                llenarReporteTotal(workbook, reportes);

                break;
            case 2: //nos referimos a la sede
                llenarReporteSede(workbook, reportes);

                break;
            case 3: //nos referimos al articulo(producto)
                llenarReporteProducto(workbook, reportes);

                break;
            case 4: //nos referimos a la comunidad
                llenarReporteComunidad( workbook, reportes);
                break;
            case 5: //nos referimos al cliente
                llenarReporteCliente(workbook, reportes);

                break;

        }

        workbook.write(stream);
        workbook.close();

        return new ByteArrayInputStream(stream.toByteArray());

    }

    private void llenarReporteTotal(Workbook workbook, Reportes reportes){

        String[] columns = {"Documento","Doc. Número","Medio de Pago","Producto","Cliente","RUC","DNI","Vendedor","DNI vendedor","Precio Unit","Cantidad","Precio Total","Fecha de Venta"};

        Sheet sheet= workbook.createSheet("Reporte total " + LocalDate.now().toString());
        setColumnWidths(sheet,reportes.getOrderBy());
        String titulo = "";
        List<ReportesTotalDto> reportesTotales = new ArrayList<>();
        switch (reportes.getType()){
            case 1:
                reportesTotales = ventasRepository.obtenerReporteAnualTotal(reportes.getYear());
                titulo = "Reporte total del año " + reportes.getYear();
                System.out.println("TestTest");
                System.out.println(titulo);
                break;
            case 2:
                reportesTotales = ventasRepository.obtenerReporteTrimestralTotal(reportes.getSelected(),reportes.getYear());
                titulo = "Reporte total del año " + reportes.getYear()+" trimestre " + CustomConstants.getTrimestre().get(reportes.getSelected());
                System.out.println(titulo);
                break;
            case 3:
                reportesTotales = ventasRepository.obtenerReporteMensualTotal(reportes.getSelected(),reportes.getYear());
                titulo = "Reporte total del año " + reportes.getYear()+" mes " + CustomConstants.getMeses().get(reportes.getSelected());
                System.out.println(titulo);
                break;
        }
        fillCellsInSheet(sheet,columns,reportesTotales,workbook,titulo);
    }

    private void llenarReporteSede(Workbook workbook, Reportes reportes){
        String[] columns = {"Codigo Inventario","Producto","Codigo Producto","Codigo Linea","Cantidad Vendida","Precio de Venta","Ruc / Dni","Nombre del Cliente","Numero de documento","Mes","Dia"};
        Sheet sheet= workbook.createSheet("Reporte de sede " + LocalDate.now().toString());
        setColumnWidths(sheet,reportes.getOrderBy());
        String titulo = "";
        List<ReportesSedesDto> reportesSedes = new ArrayList<>();
        Integer sedesId = Integer.valueOf(reportes.getNombreselect());
        Optional<Sede> byId = sedeRepository.findById(sedesId);

        switch (reportes.getType()){
            case 1://anual
                reportesSedes = ventasRepository.obtenerReporteAnualSede(reportes.getYear(),sedesId);
                titulo = "Reporte total de la sede "+ byId.get().getNombresede() + " del año " + reportes.getYear();
                break;
            case 2://trimestral
                reportesSedes = ventasRepository.obtenerReporteTrimestralSede(sedesId,reportes.getYear(),reportes.getSelected());
                titulo = "Reporte total de la sede "+byId.get().getNombresede()+ " del año " + reportes.getYear()+" trimestre " + CustomConstants.getTrimestre().get(reportes.getSelected());
                break;
            case 3://mensual
                reportesSedes = ventasRepository.obtenerReporteMensualSede(sedesId,reportes.getSelected(),reportes.getYear());
                titulo = "Reporte total de la sede "+byId.get().getNombresede()+" del año del " + reportes.getYear()+" mes " + CustomConstants.getMeses().get(reportes.getSelected());
                break;
        }
        fillCellsInSheet(sheet,columns,reportesSedes,workbook,titulo);
    }

    private void llenarReporteProducto(Workbook workbook, Reportes reportes){
        String[] columns = {"Nombre de cliente","Ruc / Dni","Codigo del Inventario","Mes","Dia","Cantidad vendida","Precio de venta"};

        String titulo = "";
        List<ReportesArticuloDto> reportesArticulos = new ArrayList<>();
        //obtener el codigo del producto con su linea
        String productostr = reportes.getNombreselect();
        int cantidad = productostr.length();
        String linea = productostr.substring(cantidad-1,cantidad);
        String prodCod = productostr.substring(0,cantidad-1);
       ProductoId idPro = new ProductoId(prodCod,linea);
       Optional<Productos> searchProduct = productosRepository.findById(idPro);

       Sheet sheet= workbook.createSheet("Reporte producto " + LocalDate.now().toString() );
       setColumnWidths(sheet,reportes.getOrderBy());

        switch (reportes.getType()){
            case 1:
                reportesArticulos = ventasRepository.obtenerReporteAnualArticuloProducto(searchProduct.get().getNombre(),linea,reportes.getYear());
                titulo = "Ventas por Artículos del año " + reportes.getYear() +" || " +searchProduct.get().getNombre();
                break;
            case 2:
                reportesArticulos = ventasRepository.obtenerReporteTrimestralArticuloProducto(searchProduct.get().getNombre(),linea,reportes.getYear(),reportes.getSelected());
                titulo = "Ventas por Artículos del año " + reportes.getYear()+" trimestre " + CustomConstants.getTrimestre().get(reportes.getSelected());
                break;
            case 3:
                reportesArticulos = ventasRepository.obtenerReporteMensualArticuloProducto(searchProduct.get().getNombre(),linea,reportes.getYear(),reportes.getSelected());
                titulo = "Ventas por Artículos del año " + reportes.getYear()+" mes " + CustomConstants.getMeses().get(reportes.getSelected());
                break;
        }
        fillCellsInSheet(sheet,columns,reportesArticulos,workbook,titulo);
    }

    private void llenarReporteComunidad(Workbook workbook, Reportes reportes){
        String[] columns = {"Codigo inventario","Artesano","Nombre del producto","Codigo del Producto","Codigo de lina","Cantidad","Precio de Venta","Rudc / Dni","Nombre del cliente","Numero de documento","Mes","Dia"};
        Sheet sheet= workbook.createSheet("Reporte comunidad " + LocalDate.now().toString());
        setColumnWidths(sheet,reportes.getOrderBy());
        String titulo = "";
        List<ReportesComunidadDto> reportesComunidad = new ArrayList<>();
        Optional<Comunidades> comunidadId = comunidadesRepository.findById(reportes.getNombreselect());
        switch (reportes.getType()){
            case 1:
                reportesComunidad = ventasRepository.obtenerReporteAnualComunidad(comunidadId.get().getCodigo(),reportes.getYear());
                titulo = "Ventas de la Comunidad "+comunidadId.get().getNombre()+" del año " + reportes.getYear();
                break;
            case 2:
                reportesComunidad = ventasRepository.obtenerReporteTrimestralComunidad(comunidadId.get().getCodigo(),reportes.getYear(),reportes.getSelected());
                titulo = "Ventas de la Comunidad "+comunidadId.get().getNombre()+ " del año " + reportes.getYear()+" trimestre " + CustomConstants.getTrimestre().get(reportes.getSelected());
                break;
            case 3:
                reportesComunidad = ventasRepository.obtenerReporteMensualComunidad(comunidadId.get().getCodigo(),reportes.getYear(),reportes.getSelected());
                titulo = "Ventas de la Comunidad "+comunidadId.get().getNombre()+ " del año " + reportes.getYear()+" mes " + CustomConstants.getMeses().get(reportes.getSelected());
                break;
        }
        fillCellsInSheet(sheet,columns,reportesComunidad,workbook,titulo);
    }

    private void llenarReporteCliente(Workbook workbook, Reportes reportes){
        String[] columns = {"numero de documento","Codigo de inventario","Nombre del producto","Mes","Dia","Cantidad vendidad","Precio de venta"};
        Sheet sheet= workbook.createSheet("Reporte de clientes " + LocalDate.now().toString());
        setColumnWidths(sheet,reportes.getOrderBy());
        String titulo = "";
        List<ReportesClienteDto> reportesClientes = new ArrayList<>();
        int idventa = Integer.valueOf(reportes.getNombreselect());
        Optional<Ventas> byNombrecliente = ventasRepository.findById(idventa);
        switch (reportes.getType()){
            case 1:
                reportesClientes = ventasRepository.obtenerReporteAnualCliente(byNombrecliente.get().getNombrecliente(),reportes.getYear());
                titulo = "Reporte total del cliente "+byNombrecliente.get().getNombrecliente()+ " del año " + reportes.getYear();
                break;
            case 2:
                reportesClientes = ventasRepository.obtenerReporteTrimestralCliente(byNombrecliente.get().getNombrecliente(),reportes.getYear(),reportes.getSelected());
                titulo = "Reporte total del cliente "+byNombrecliente.get().getNombrecliente()+" del año " + reportes.getYear()+" trimestre " + CustomConstants.getTrimestre().get(reportes.getSelected());
                break;
            case 3:
                reportesClientes = ventasRepository.obtenerReporteMensualCliente(byNombrecliente.get().getNombrecliente(),reportes.getYear(),reportes.getSelected());
                titulo = "Reporte total del cliente "+byNombrecliente.get().getNombrecliente()+" del año " + reportes.getYear()+" mes " + CustomConstants.getMeses().get(reportes.getSelected());
                break;
        }
        fillCellsInSheet(sheet,columns,reportesClientes,workbook,titulo);
    }

}
