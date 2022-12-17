package com.example.sw2.dtoReportes;

import java.math.BigDecimal;

public interface ReportesTotalDto {

    String getRuc_dni();
    String getNombrecliente();
    Integer getTipodocumento();
    String getNumerodocumento();
    String getLugarventa();
    String getProductoinventario();
    String getFecha();
    Integer getCantidad();
    String getVendedor();
    Integer getDnivendedor();
    Double getPrecio_venta();
    Integer getMediopago();

}
