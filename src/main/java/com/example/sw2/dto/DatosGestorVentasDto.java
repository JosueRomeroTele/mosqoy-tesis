package com.example.sw2.dto;

import java.util.Date;

public interface DatosGestorVentasDto {

    String getCodigoproducto();
    String getNombreproducto();
    String getTipodocumento();
    String getNumerodocumento();
    String getNombrecliente();
    String getRucdni();
    int getCantidadventa();
    Float getPrecioVenta();
    Date getFechaventa();
    String getLugarventa();

}
