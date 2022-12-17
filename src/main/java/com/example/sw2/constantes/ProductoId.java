package com.example.sw2.constantes;


import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Embeddable
public class ProductoId implements Serializable {

    @NotBlank
    @Size(max = 3, message = "El codigo debe contener máximo 3 caracteres")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ]+$", message = "Ingrese solo caracteres válidos")
    private String codigonom;

    @NotBlank
    @Size(min = 1, max = 1, message = "Debe contener un caracter")
    @Column(name="linea",nullable = false)
    private String codigolinea;


    public ProductoId(){

    }

    public ProductoId(String cn, String cl){
        this.codigonom=cn;
        this.codigolinea=cl;
    }


    public boolean validateCodigoLinea(){
        return CustomConstants.getLineas().containsKey(this.codigolinea);
    }

    public void setCodigolinea(String codigolinea) {
        this.codigolinea = codigolinea.trim();
    }

    public String getNombreLinea(){
        return CustomConstants.getLineas().get(this.codigolinea);
    }

    public String getCodigolinea() {
        return codigolinea;
    }


    public String getCodigonom() {
        return codigonom;
    }

    public void setCodigonom(String codigonom) {
        this.codigonom = codigonom.toUpperCase().trim();
    }



}
