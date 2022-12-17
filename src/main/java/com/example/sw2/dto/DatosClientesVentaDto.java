package com.example.sw2.dto;

import java.util.Date;

public interface DatosClientesVentaDto {

    String getRucdni();
    String getNombrecliente();
    int getTipodocumento();
    String getNumerodocumento();
    String getLugarVenta();
    Date getFechaventa();
    int getCantidadventa();
    Float getPrecioventa();
    String getCodigoproducto();
    String getNombreproducto();
    String getColorproducto();
    String getTamanhoproducto();
    String getComunidadproducto ();
    byte[] getFotoproducto();
}
