package com.example.sw2.service;

import com.example.sw2.constantes.CustomConstants;
import com.example.sw2.constantes.ProductoId;
import com.example.sw2.dtoReportes.ReportesArticuloDto;
import com.example.sw2.dtoReportes.ReportesClienteDto;
import com.example.sw2.dtoReportes.ReportesTotalDto;
import com.example.sw2.dtoReportes.ReportesComunidadDto;
import com.example.sw2.entity.Comunidades;
import com.example.sw2.entity.Productos;
import com.example.sw2.entity.Reportes;
import com.example.sw2.entity.Ventas;
import com.example.sw2.repository.ComunidadesRepository;
import com.example.sw2.repository.ProductosRepository;
import com.example.sw2.repository.VentasRepository;
import com.example.sw2.utils.ReportesUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReporteSedeService extends ReportesUtils implements IReporteSedeService {


    @Autowired
    VentasRepository ventasRepository;
    @Autowired
    ProductosRepository productosRepository;
    @Autowired
    ComunidadesRepository comunidadesRepository;

    @Override
    public ByteArrayInputStream generarReporte(Reportes reportes, int idsede) throws Exception{

        Workbook workbook = new HSSFWorkbook();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        switch (reportes.getOrderBy()){
            case 1:
                //nos referimos al total

                llenarReporteTotal(workbook, reportes, idsede);
                break;
            case 3:
                //nos referimos al articulo(producto)

                llenarReporteProducto(workbook, reportes, idsede);
                break;
            case 4:
                //nos referimos a la comunidad

                llenarReporteComunidad( workbook, reportes,idsede);
                break;
            case 5:
                //nos referimos al cliente

                llenarReporteCliente(workbook, reportes,idsede);

                break;

        }

        workbook.write(stream);
        workbook.close();

        return new ByteArrayInputStream(stream.toByteArray());
    }

    private void llenarReporteTotal(Workbook workbook, Reportes reportes, int idsede){

        String[] columns = {"Documento","Doc. Número","Medio de Pago","Producto","Cliente","RUC","DNI","Vendedor","DNI vendedor","Precio Unit.","Cantidad","Precio Total","Fecha de Venta"};

        Sheet sheet= workbook.createSheet("Sede Reporte total " + LocalDate.now().toString());
        setColumnWidths(sheet,reportes.getOrderBy());
        String titulo = "";
        List<ReportesTotalDto> reportesTotales;
        switch (reportes.getType()){
            case 1:
                reportesTotales = ventasRepository.obtenerReporteSedeAnualTotal(reportes.getYear(), idsede);
                titulo = "Reporte total del año " + reportes.getYear();
                break;
            case 2:
                reportesTotales = ventasRepository.obtenerReporteSedeTrimestralTotal(reportes.getSelected(),reportes.getYear(), idsede);
                titulo = "Reporte total del año " + reportes.getYear()+" trimestre " + CustomConstants.getTrimestre().get(reportes.getSelected());
                break;
            case 3:
                reportesTotales = ventasRepository.obtenerReporteSedeMensualTotal(reportes.getSelected(),reportes.getYear(), idsede);
                titulo = "Reporte total del año " + reportes.getYear()+" mes " + CustomConstants.getMeses().get(reportes.getSelected());
                break;
            default:
                reportesTotales = new ArrayList<>();
        }

        fillCellsInSheet(sheet,columns,reportesTotales,workbook,titulo);

    }

    private void llenarReporteProducto(Workbook workbook, Reportes reportes, int idsede){
        String[] columns = {"Nombre de cliente","Ruc / Dni","Codigo del Inventario","Mes","Dia","Cantidad vendida","Precio de venta"};
        Sheet sheet= workbook.createSheet("Reporte producto " + LocalDate.now().toString());
        setColumnWidths(sheet,reportes.getOrderBy());
        String titulo = "";
        List<ReportesArticuloDto> reportesArticulos = new ArrayList<>();

        //obtener el codigo del producto con su linea
        String productostr = reportes.getNombreselect();
        int cantidad = productostr.length();
        String linea = productostr.substring(cantidad-1,cantidad);
        String prodCod = productostr.substring(0,cantidad-1);
        ProductoId idPro = new ProductoId(prodCod,linea);
        Optional<Productos> searchProduct = productosRepository.findById(idPro);

        switch (reportes.getType()){
            case 1:
                reportesArticulos = ventasRepository.obtenerReporteSedeAnualArticuloProducto(reportes.getYear(),idsede,prodCod,linea);
                titulo = "Ventas del Artículo " +searchProduct.get().getNombre() + " del año " + reportes.getYear();
                break;
            case 2:
                reportesArticulos = ventasRepository.obtenerReporteSedeTrimestralArticuloProducto(reportes.getYear(),idsede,prodCod,linea,reportes.getSelected());
                titulo = "Ventas del Artículo " +searchProduct.get().getNombre() + " del año " + reportes.getYear()+" trimestre " + CustomConstants.getTrimestre().get(reportes.getSelected());
                break;
            case 3:
                reportesArticulos = ventasRepository.obtenerReporteSedeMensualArticuloProducto(reportes.getYear(),idsede,prodCod,linea,reportes.getSelected());
                titulo = "Ventas del Artículo " +searchProduct.get().getNombre() + " del año " + reportes.getYear()+" mes " + CustomConstants.getMeses().get(reportes.getSelected());
                break;
            default:
                reportesArticulos = new ArrayList<>();
        }
        fillCellsInSheet(sheet,columns,reportesArticulos,workbook,titulo);
    }

    private void llenarReporteComunidad(Workbook workbook, Reportes reportes, Integer idsede){
        String[] columns = {"Codigo inventario","Artesano","Nombre del producto","Codigo del Producto","Codigo de lina","Cantidad","Precio de Venta","Rudc / Dni","Nombre del cliente","Numero de documento","Mes","Dia"};
        Sheet sheet= workbook.createSheet("reporte comunidad " + LocalDate.now().toString());
        setColumnWidths(sheet,reportes.getOrderBy());
        String titulo = "";
        List<ReportesComunidadDto> reportesComunidad = new ArrayList<>();
        Optional<Comunidades> comunidadId = comunidadesRepository.findById(reportes.getNombreselect());
        switch (reportes.getType()){
            case 1:
                reportesComunidad = ventasRepository.obtenerReporteSedeAnualComunidad(comunidadId.get().getCodigo(),reportes.getYear(),idsede);
                titulo = "Ventas de la Comunidad "+comunidadId.get().getNombre()+ " del año " + reportes.getYear();
                break;
            case 2:
                reportesComunidad = ventasRepository.obtenerReporteSedeTrimestralComunidad(comunidadId.get().getCodigo(),reportes.getYear(),idsede,reportes.getSelected());
                titulo = "Ventas de la Comunidad "+comunidadId.get().getNombre()+ " del año " + reportes.getYear()+" trimestre " + CustomConstants.getTrimestre().get(reportes.getSelected());
                break;
            case 3:
                reportesComunidad = ventasRepository.obtenerReporteSedeMensualComunidad(comunidadId.get().getCodigo(),reportes.getYear(),idsede,reportes.getSelected());
                titulo = "Ventas de la Comunidad "+comunidadId.get().getNombre()+ " del año " + reportes.getYear()+" mes " + CustomConstants.getMeses().get(reportes.getSelected());
                break;
            default:
                reportesComunidad = new ArrayList<>();
        }
        fillCellsInSheet(sheet,columns,reportesComunidad,workbook,titulo);
    }

    private void llenarReporteCliente(Workbook workbook, Reportes reportes,Integer idsede){
        String[] columns = {"numero de documento","Codigo de inventario","Nombre del producto","Mes","Dia","Cantidad vendidad","Precio de venta"};
        Sheet sheet= workbook.createSheet("Reporte de clientes " + LocalDate.now().toString());
        setColumnWidths(sheet,reportes.getOrderBy());
        String titulo = "";
        List<ReportesClienteDto> reportesClientes;
        int idventa = Integer.valueOf(reportes.getNombreselect());
        Optional<Ventas> byNombrecliente = ventasRepository.findById(idventa);
        switch (reportes.getType()){
            case 1:
                reportesClientes = ventasRepository.obtenerReporteSedeAnualCliente(byNombrecliente.get().getNombrecliente(),reportes.getYear(),idsede);
                titulo = "Reporte total del cliente "+byNombrecliente.get().getNombrecliente()+ " del año " + reportes.getYear();
                break;
            case 2:
                reportesClientes = ventasRepository.obtenerReporteSedeTrimestralCliente(byNombrecliente.get().getNombrecliente(),reportes.getYear(),idsede,reportes.getSelected());
                titulo = "Reporte total del cliente "+byNombrecliente.get().getNombrecliente()+ " del año " + reportes.getYear()+" trimestre " + CustomConstants.getTrimestre().get(reportes.getSelected());
                break;
            case 3:
                reportesClientes = ventasRepository.obtenerReporteSedeMensualCliente(byNombrecliente.get().getNombrecliente(),reportes.getYear(),idsede,reportes.getSelected());
                titulo = "Reporte total del cliente "+byNombrecliente.get().getNombrecliente()+ " del año " + reportes.getYear()+" mes " + CustomConstants.getMeses().get(reportes.getSelected());
                break;
            default:
                reportesClientes = new ArrayList<>();
        }

        fillCellsInSheet(sheet,columns,reportesClientes,workbook,titulo);
    }

}
