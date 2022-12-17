package com.example.sw2.constantes;

import com.example.sw2.constantes.CustomConstants;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Embeddable
public class VentasId implements Serializable {


    @Max(value = 4,message = "Ingrese un valor válido de tipo de documento")
    @Min(value = 1,message = "Ingrese un valor válido de tipo de documento")
    @NotNull(message = "Ingrese un tipo de documento")
    @Column(name = "tipodocumento")
    private Integer tipodocumento;
    //@NotBlank(message = "Este campo no puede estar vacío")
    @Column(name = "numerodocumento", nullable = true)
    private String numerodocumento;


    public String getTipodocumento2() {
        return CustomConstants.getTiposDocumento().get(this.tipodocumento);
    }

    public Integer getTipodocumento(){return tipodocumento; }

    public VentasId(){

    }

    public VentasId(Integer x, String y){
        this.setTipodocumento(x);
        this.setNumerodocumento(y);
    }

    public String getNombreTipodocumento() {
        return CustomConstants.getTiposDocumento().get(this.tipodocumento);
    }

    public void setTipodocumento(Integer tipodocumento) {
        this.tipodocumento = tipodocumento;
    }

    public String getNumerodocumento() {
        return numerodocumento;
    }

    public boolean validateNumeroDocumento(){
        boolean cond1 =  this.numerodocumento==null || (this.numerodocumento!=null && this.numerodocumento.equals(""));
        if (!cond1){
            return (this.numerodocumento.length()>5) && (this.numerodocumento.length()<45);
        }
        return  false;
    }

    public void setNumerodocumento(String numerodocumento) {
        this.numerodocumento = numerodocumento.replace(",","").trim();
    }
}
