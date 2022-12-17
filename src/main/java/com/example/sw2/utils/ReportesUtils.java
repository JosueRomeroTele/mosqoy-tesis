package com.example.sw2.utils;

import com.example.sw2.constantes.CustomConstants;
import com.example.sw2.dtoReportes.*;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.IndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFColor;

import java.util.List;
import static org.apache.poi.ss.util.CellUtil.createCell;

public abstract class ReportesUtils {


    protected final String MENSAJE_NO_VENTA = "No se encontró información de ventas para este reporte";
    protected final Integer BEGINNING_ROW = 3;
    protected final Integer BEGINNING_COLUMN = 1;
    protected final Integer TITLE_ROW = 1;
    private final short HEADER_FONT = 12;
    private final short CONTENT_FONT = 10;
    private final short TITLE_FONT = 20;



    //Cliente
    protected void fillCellsInSheet(Sheet sheet, String[] columns, Object data, Workbook workbook,String titulo){

        List<Object> list = (List<Object>)data;
        if(list.isEmpty()) {
            sheet.createRow(BEGINNING_ROW).createCell(BEGINNING_COLUMN).setCellValue(MENSAJE_NO_VENTA);
        }
        else {
            CellStyle style1 = createContentCellStyle1(workbook);
            CellStyle style2 = createContentCellStyle2(workbook);
            CellStyle headerStyle = createHeaderCellStyle(workbook);
            CellStyle titleStyle = createTitleCellStyle(workbook);
            CellStyle formulaStyle = createFormulaStyle(workbook);
            Row row = sheet.createRow(BEGINNING_ROW);
            Row temp = sheet.createRow(TITLE_ROW);
            createCell(temp,1,titulo,titleStyle);//para el titulo

            for(int i=1,col=BEGINNING_COLUMN; i<columns.length + 1; i++)
                createCell(row,col++,columns[i-1],headerStyle);

            int fila = BEGINNING_ROW;
            CellStyle style;
            int i = BEGINNING_COLUMN;
            if (list.get(0) instanceof ReportesClienteDto){
                //Cliente
                for(ReportesClienteDto dataRow : (List<ReportesClienteDto>)(Object)list){
                    i = BEGINNING_COLUMN;
                    row = sheet.createRow(++fila);
                    style = ((fila%2)==0)?style1:style2;
                    createCell(row,i++,dataRow.getNumerodocumento(),style);
                    createCell(row,i++,dataRow.getCodigo_inventario(),style);
                    createCell(row,i++,"",style).setCellValue(dataRow.getProducto());
                    createCell(row,i++,"",style).setCellValue(dataRow.getMes());
                    createCell(row,i++,"",style).setCellValue(dataRow.getDia());
                    createCell(row,i++,"",style).setCellValue(dataRow.getCantidad());
                    createCell(row,i,"",style).setCellValue(dataRow.getPrecioventa());
                }

                /*String strFormula1= "SUM(F"+(BEGINNING_ROW+2)+":F"+(fila+1)+")";
                String strFormula2= "SUM(G"+(BEGINNING_ROW+2)+":G"+(fila+1)+")";
                sheet.createRow(fila+2).createCell(4);
                row = sheet.createRow(fila+2);
                createCell(row,4,"SUMA",formulaStyle);
                row.createCell(5).setCellFormula(strFormula1);
                row.createCell(6).setCellFormula(strFormula2);
                row.getCell(5).setCellStyle(formulaStyle);
                row.getCell(6).setCellStyle(formulaStyle);*/
            }else
            if (list.get(0) instanceof ReportesComunidadDto){
                //Comunidad
                for(ReportesComunidadDto dataRow : (List<ReportesComunidadDto>)(Object)list){
                    i = BEGINNING_COLUMN;
                    row = sheet.createRow(++fila);
                    style = ((fila%2)==0)?style1:style2;
                    createCell(row, i++,dataRow.getCodigoinventario(),style);
                    createCell(row,i++,dataRow.getArtesano(),style);
                    createCell(row,i++,"",style).setCellValue(dataRow.getNombreproducto());
                    createCell(row,i++,"",style).setCellValue(dataRow.getCodigoproducto());
                    createCell(row,i++,"",style).setCellValue(dataRow.getCodigolinea());
                    createCell(row,i++,"",style).setCellValue(dataRow.getCantidad());
                    createCell(row,i++,"",style).setCellValue(dataRow.getPrecioventa());
                    createCell(row,i++,"",style).setCellValue(dataRow.getRucdni());
                    createCell(row,i++,"",style).setCellValue(dataRow.getNombrecliente());
                    if (dataRow.getNumerodocumento()==null){
                        createCell(row,i++,"",style).setCellValue("---");
                    }else{
                        createCell(row,i++,"",style).setCellValue(dataRow.getNumerodocumento());
                    }
                    createCell(row,i++,"",style).setCellValue(dataRow.getMes());
                    createCell(row,i,"",style).setCellValue(dataRow.getDia());
                }

                //Lo único que va hardoceado son los números 9,10 y 12 que pertenecen a las columnas que se van a sumar
                /*
                String strFormula1= "SUM(E"+(BEGINNING_ROW+2)+":E"+(fila+1)+")";
                String strFormula2= "SUM(F"+(BEGINNING_ROW+2)+":F"+(fila+1)+")";

                sheet.createRow(fila+3).createCell(3);
                createCell(sheet.createRow(fila+3),3,"SUMA",formulaStyle);
                sheet.getRow(fila+3).createCell(4).setCellFormula(strFormula1);
                sheet.getRow(fila+3).createCell(5).setCellFormula(strFormula2);
                sheet.getRow(fila+3).getCell(4).setCellStyle(formulaStyle);
                sheet.getRow(fila+3).getCell(5).setCellStyle(formulaStyle);
                */

            }else
            if (list.get(0) instanceof ReportesArticuloDto){
                //Articulo
                for(ReportesArticuloDto dataRow : (List<ReportesArticuloDto>)(Object)list){
                    i = BEGINNING_COLUMN;
                    row = sheet.createRow(++fila);
                    style = ((fila%2)==0)?style1:style2;
                    createCell(row,i++,dataRow.getNombrecliente(),style);
                    if (dataRow.getRucdni()==null){
                        createCell(row,i++,"--",style);
                    }else{
                        createCell(row,i++,dataRow.getRucdni(),style);
                    }

                    createCell(row,i++,dataRow.getProductoinventario(),style);
                    createCell(row,i++,"",style).setCellValue(dataRow.getMes());
                    createCell(row,i++,"",style).setCellValue(dataRow.getDia());
                    createCell(row,i++,"",style).setCellValue(dataRow.getCantidad());
                    createCell(row,i,"",style).setCellValue(dataRow.getPrecioventa());
                }

                //Lo único que va hardoceado son los números 9,10 y 12 que pertenecen a las columnas que se van a sumar
                /*String strFormula1= "SUM(E"+(BEGINNING_ROW+2)+":E"+(fila+1)+")";
                String strFormula2= "SUM(F"+(BEGINNING_ROW+2)+":F"+(fila+1)+")";
                sheet.createRow(fila+3).createCell(3);
                createCell(sheet.createRow(fila+3),3,"SUMA",formulaStyle);
                sheet.getRow(fila+3).createCell(4).setCellFormula(strFormula1);
                sheet.getRow(fila+3).createCell(5).setCellFormula(strFormula2);
                sheet.getRow(fila+3).getCell(4).setCellStyle(formulaStyle);
                sheet.getRow(fila+3).getCell(5).setCellStyle(formulaStyle);
                */
            }else
            if (list.get(0) instanceof ReportesSedesDto){
                //Sede
                for(ReportesSedesDto dataRow : (List<ReportesSedesDto>)(Object)list){
                    i = BEGINNING_COLUMN;
                    row = sheet.createRow(++fila);
                    style = ((fila%2)==0)?style1:style2;
                    createCell(row,i++,dataRow.getCodigoinventario(),style);
                    createCell(row,i++,"",style).setCellValue(dataRow.getNombreproducto());
                    createCell(row,i++,dataRow.getCodigoproducto(),style);
                    createCell(row,i++,"",style).setCellValue(dataRow.getCodigolinea());
                    createCell(row,i++,"",style).setCellValue(dataRow.getCantidad());
                    createCell(row,i++,"",style).setCellValue(dataRow.getPrecioventa());
                    createCell(row,i++,"",style).setCellValue(dataRow.getRucdni());
                    createCell(row,i++,"",style).setCellValue(dataRow.getNombrecliente());
                    if (dataRow.getNumerodocumento()==null){
                        createCell(row,i++,"",style).setCellValue("--");
                    }else{
                        createCell(row,i++,"",style).setCellValue(dataRow.getNumerodocumento());
                    }

                    createCell(row,i++,"",style).setCellValue(dataRow.getMes());
                    createCell(row,i++,"",style).setCellValue(dataRow.getDia());
                }

                /*String strFormula1= "SUM(F"+(BEGINNING_ROW+2)+":F"+(fila+1)+")";
                String strFormula2= "SUM(G"+(BEGINNING_ROW+2)+":G"+(fila+1)+")";
                sheet.createRow(fila+2).createCell(4);
                row = sheet.createRow(fila+2);
                createCell(row,4,"SUMA",formulaStyle);
                row.createCell(5).setCellFormula(strFormula1);
                row.createCell(6).setCellFormula(strFormula2);
                row.getCell(5).setCellStyle(formulaStyle);
                row.getCell(6).setCellStyle(formulaStyle);*/
            } else
            if (list.get(0) instanceof ReportesTotalDto){
                //Total
                for(ReportesTotalDto dataRow : (List<ReportesTotalDto>)(Object)list){
                    i = BEGINNING_COLUMN;
                    row = sheet.createRow(++fila);
                    style = ((fila%2)==0)?style1:style2;
                    createCell(row,i++,CustomConstants.getTiposDocumento().get(dataRow.getTipodocumento()),style);
                    System.out.println("Numero de tipo de doc" + dataRow.getTipodocumento());
                    createCell(row,i++,dataRow.getNumerodocumento(),style);
                    createCell(row,i++,CustomConstants.getMediosDePago().get(dataRow.getMediopago()),style);
                    createCell(row,i++,dataRow.getProductoinventario(),style);
                    createCell(row,i++,dataRow.getNombrecliente(),style);
                    if(dataRow.getRuc_dni().length() == 11){
                        createCell(row,i++,dataRow.getRuc_dni(),style);
                        createCell(row,i++,"-",style);
                    }else{
                        createCell(row,i++,"-",style);
                        createCell(row,i++,dataRow.getRuc_dni(),style);
                    }
                    createCell(row,i++,dataRow.getVendedor(),style);
                    createCell(row,i++, String.valueOf(dataRow.getDnivendedor()),style);
                    createCell(row,i++,"",style).setCellValue(dataRow.getPrecio_venta());
                    createCell(row,i++,"",style).setCellValue(dataRow.getCantidad());
                    createCell(row,i++,"",style).setCellValue(dataRow.getPrecio_venta()*dataRow.getCantidad());
                    createCell(row,i++,dataRow.getFecha(),style);

                }

                //Lo único que va hardcodeado son los números 10,11 y 12 que pertenecen a las columnas que se van a sumar
               /* String strFormula1= "SUM(L"+(BEGINNING_ROW+2)+":L"+(fila+1)+")";
                String strFormula2= "SUM(M"+(BEGINNING_ROW+2)+":M"+(fila+1)+")";
                sheet.createRow(fila+2).createCell(10);
                row = sheet.createRow(fila+2);
                createCell(row,10,"SUMA",formulaStyle);
                row.createCell(11).setCellFormula(strFormula1);
                row.createCell(12).setCellFormula(strFormula2);
                row.getCell(11).setCellStyle(formulaStyle);
                row.getCell(12).setCellStyle(formulaStyle);*/
            }
        }
    }


    protected void setColumnWidths(Sheet sheet, Integer orderBy){
        int i = BEGINNING_COLUMN;
        switch (orderBy){
            case 1: //Total
                sheet.setColumnWidth(i++, 3800);
                sheet.setColumnWidth(i++, 4000);
                sheet.setColumnWidth(i++, 5300);
                sheet.setColumnWidth(i++, 6500);
                sheet.setColumnWidth(i++, 4000);
                sheet.setColumnWidth(i++, 3500);
                sheet.setColumnWidth(i++, 2500);
                sheet.setColumnWidth(i++, 4000);
                sheet.setColumnWidth(i++, 4200);
                sheet.setColumnWidth(i++, 4200);
                sheet.setColumnWidth(i++, 4200);
                sheet.setColumnWidth(i++, 4200);
                sheet.setColumnWidth(i, 5000);

                break;
            case 2: // Sede
                sheet.setColumnWidth(i++, 11000);
                sheet.setColumnWidth(i++, 6000);
                sheet.setColumnWidth(i++, 6000);
                sheet.setColumnWidth(i++, 6000);
                sheet.setColumnWidth(i++, 6000);
                sheet.setColumnWidth(i++, 6000);
                sheet.setColumnWidth(i++, 6000);
                sheet.setColumnWidth(i++, 6000);
                sheet.setColumnWidth(i++, 6000);
                sheet.setColumnWidth(i, 6000);
                break;
            case 3: // Producto
                sheet.setColumnWidth(i++, 6000);
                sheet.setColumnWidth(i++, 6000);
                sheet.setColumnWidth(i++, 11000);
                sheet.setColumnWidth(i++, 6000);
                sheet.setColumnWidth(i++, 6000);
                sheet.setColumnWidth(i++, 6000);
                sheet.setColumnWidth(i, 6000);
                break;
            case 4: //Comunidad
                sheet.setColumnWidth(i++, 11000);
                sheet.setColumnWidth(i++, 9000);
                sheet.setColumnWidth(i++, 6000);
                sheet.setColumnWidth(i++, 6000);
                sheet.setColumnWidth(i++, 6000);
                sheet.setColumnWidth(i++, 6000);
                sheet.setColumnWidth(i++, 6000);
                sheet.setColumnWidth(i++, 9000);
                sheet.setColumnWidth(i++, 6000);
                sheet.setColumnWidth(i++, 9000);
                sheet.setColumnWidth(i++, 4000);
                sheet.setColumnWidth(i, 4000);
                break;
            case 5: //Cliente
                sheet.setColumnWidth(i++, 8000);
                sheet.setColumnWidth(i++, 8000);
                sheet.setColumnWidth(i++, 4000);
                sheet.setColumnWidth(i++, 4000);
                sheet.setColumnWidth(i++, 4000);
                sheet.setColumnWidth(i++, 4000);
                sheet.setColumnWidth(i, 6000);
                break;

        }
    }


    private CellStyle createFormulaStyle(Workbook workbook){
        Font font = workbook.createFont();
        font.setFontHeightInPoints(HEADER_FONT);
        font.setFontName("Courier New");
        font.setBold(true);
        CellStyle style = workbook.createCellStyle();
        style.setFillBackgroundColor(IndexedColors.YELLOW.getIndex());
        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(font);
        return style;
    }

    private CellStyle createTitleCellStyle(Workbook workbook){
        Font font = workbook.createFont();
        font.setFontHeightInPoints(TITLE_FONT);
        font.setFontName("Courier New");
        font.setBold(true);
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setFont(font);
        return style;
    }


    private CellStyle createHeaderCellStyle(Workbook workbook){
        Font font = workbook.createFont();
        font.setFontHeightInPoints(HEADER_FONT);
        font.setFontName("Courier New");
        font.setBold(true);
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setFillBackgroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(font);
        return style;
    }

    private CellStyle createContentCellStyle1(Workbook workbook){
        Font font = workbook.createFont();
        font.setFontHeightInPoints(CONTENT_FONT);
        font.setFontName("Arial");
        CellStyle style = workbook.createCellStyle();
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        //style.setBorderTop(BorderStyle.MEDIUM_DASHED);
        //style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setFillBackgroundColor(IndexedColors.WHITE1.getIndex());
        style.setFillForegroundColor(IndexedColors.WHITE1.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(font);
        return style;
    }

    private CellStyle createContentCellStyle2(Workbook workbook){
        Font font = workbook.createFont();
        font.setFontHeightInPoints(CONTENT_FONT);
        font.setFontName("Arial");
        CellStyle style = workbook.createCellStyle();
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        //style.setBorderTop(BorderStyle.MEDIUM_DASHED);
        //style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setFillBackgroundColor(IndexedColors.WHITE.getIndex());
        style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(font);
        return style;
    }

}
