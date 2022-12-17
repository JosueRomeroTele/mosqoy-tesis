package com.example.sw2.dto;

import java.math.BigDecimal;
import java.util.Date;

public interface DatosProductoVentaDto {

    String getCodigoproducto();
    String getNombreproducto();
    String getComunidadproducto();
    String getTamanhoproducto();
    String getColorproducto();
    String getFotoproducto();
    Integer getCantidadventa();
    BigDecimal getVenta();

}
