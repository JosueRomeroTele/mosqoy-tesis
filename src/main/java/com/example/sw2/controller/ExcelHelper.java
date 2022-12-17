package com.example.sw2.controller;

import com.example.sw2.constantes.ProductoId;
import com.example.sw2.entity.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERs = { "codigo_inventario", "numpedido", "categoria", "producto","linea","tamanho","tipoadquisicion","fecha_adquisicion","fecha_vencimiento_consignacion","comunidad","artesano","cantidad_total","costotejedos","costomosqoy","facilitador","color"};
    static String SHEET = "inventario";

    public static boolean hasExcelFormat(MultipartFile file) {

        if (!TYPE.equals(file.getContentType())) {
            return false;
        }

        return true;
    }

    public static List<Inventario> excelToInventario(InputStream is){

        try{
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheet(SHEET);
            Iterator<Row> rows = sheet.iterator();

            List<Inventario> inventarioList = new ArrayList<Inventario>();

            int rowNumber = 0;

            while (rows.hasNext()){
                Row currentRow = rows.next();

                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }
                Iterator<Cell> cellsInRow = currentRow.iterator();

                    Inventario inventario = new Inventario();

                ProductoId productoId = new ProductoId();
                Comunidades comunidades = new Comunidades();
                int cellIdx = 0;
                while (cellsInRow.hasNext()){
                    Cell currentCell = cellsInRow.next();

                    switch (cellIdx){
                        case 0://codigo inventario
                            inventario.setCodigoinventario(currentCell.getStringCellValue());
                            inventario.setFoto("Imagen_no_disponible.png");
                            inventario.setDevuelto_artesano(false);
                            break;
                        case 1://numero de pedido
                            inventario.setNumpedido((int) currentCell.getNumericCellValue());
                            break;
                        case 2://categoria
                            Categorias categorias = new Categorias();
                            categorias.setCodigo(currentCell.getStringCellValue());
                            inventario.setCategorias(categorias);
                            break;
                        case 3://Linea
                            productoId.setCodigolinea(currentCell.getStringCellValue());
                            break;
                        case 4://Producto
                            productoId.setCodigonom(currentCell.getStringCellValue());
                            Productos productos = new Productos();
                            productos.setId(productoId);
                            inventario.setProductos(productos);
                            break;
                        case 5://tamaño
                            inventario.setCodtamanho(currentCell.getStringCellValue());
                            break;
                        case 6://tipo de aquisicion
                            boolean tipAdqui;
                            if (currentCell.getStringCellValue().equalsIgnoreCase("comprado") ){
                                inventario.setCodAdquisicion(0);
                            }else if (currentCell.getStringCellValue().equalsIgnoreCase("consignado")){
                                inventario.setCodAdquisicion(1);
                            }
                            break;
                        case 7://fecha de aquisicion
                            inventario.setFechaadquisicion(currentCell.getLocalDateTimeCellValue().toLocalDate());
                            break;
                        case 8://fecha de vencimiento de consignacion
                            if (inventario.getTipoAdquisicion().equals("Consignado")){
                                inventario.setFechavencimientoconsignacion(currentCell.getLocalDateTimeCellValue().toLocalDate());
                                inventario.setDia(inventario.getFechavencimientoconsignacion().getDayOfMonth());
                                inventario.setMes(inventario.getFechavencimientoconsignacion().getMonthValue());
                                inventario.setAnho(inventario.getFechavencimientoconsignacion().getYear());
                            }else{
                                inventario.setFechavencimientoconsignacion(null);
                                inventario.setMes(inventario.getFechaadquisicion().getMonthValue());
                                inventario.setAnho(inventario.getFechaadquisicion().getYear());
                            }
                            break;
                        case 9://comunidad
                            comunidades.setCodigo(currentCell.getStringCellValue());
                            inventario.setComunidades(comunidades);
                            break;
                        case 10://artesano
                            if (inventario.getTipoAdquisicion().equals("Consignado")){
                            Artesanos artesanos = new Artesanos();
                            artesanos.setComunidades(comunidades);
                            artesanos.setCodigo(currentCell.getStringCellValue());
                            inventario.setArtesanos(artesanos);
                            }
                            break;
                        case 11://cantidad_total
                            inventario.setCantidadtotal((int) currentCell.getNumericCellValue());
                            inventario.setCantidadgestor((int) currentCell.getNumericCellValue());
                            break;
                        case 12://costo del tejedor
                            BigDecimal b = BigDecimal.valueOf(currentCell.getNumericCellValue());
                            inventario.setCostotejedor(b);
                            break;
                        case 13://costo mosqoy
                            BigDecimal b1 = BigDecimal.valueOf(currentCell.getNumericCellValue());
                            inventario.setCostomosqoy(b1);
                            break;
                        case 14://facilitador
                            inventario.setFacilitador(currentCell.getStringCellValue());
                            break;
                        case 15://color
                            inventario.setColor(currentCell.getStringCellValue());
                            break;
                            default:
                            break;
                    }
                    cellIdx++;
                }

                inventarioList.add(inventario);
            }
            workbook.close();
            return inventarioList;

        }catch (IOException e){
            throw new RuntimeException("fail to parse excel file: " + e.getMessage());
        }
    }



}
