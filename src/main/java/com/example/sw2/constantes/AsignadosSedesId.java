package com.example.sw2.constantes;

import com.example.sw2.entity.Inventario;
import com.example.sw2.entity.Sede;
import com.example.sw2.entity.Usuarios;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Embeddable
public class AsignadosSedesId implements Serializable {
    @ManyToOne
    @JoinColumn(name = "sede")
    private Sede sede;
    @ManyToOne
    @JoinColumn(name= "producto_inventario")
    private Inventario productoinventario;
    @Column(name = "estadoasignacion")
    private Integer estadoasignacion;
    @Column(name = "precioventa")
    @Digits(integer = 10 /*precision*/, fraction = 2 /*scale*/, message = "Ingrese un precio válido")
    private Float precioventa;



    public AsignadosSedesId(){}

    public AsignadosSedesId(Sede sede, Inventario inventario, Integer estadoasignacion, Float precioventa){
        this.sede=sede;
        this.productoinventario=inventario;
        this.estadoasignacion=estadoasignacion;
        this.precioventa=precioventa;
    }

    public AsignadosSedesId( int sede, String productoinventario, Integer estadoasignacion, Float precioventa){
        this.sede=new Sede(sede);
        this.productoinventario=new Inventario();
        this.productoinventario.setCodigoinventario(productoinventario);
        this.estadoasignacion=estadoasignacion;
        this.precioventa=precioventa;
    }


    /*

     */

    public String getNombreEstado(){
        return CustomConstants.getEstadoAsignacion().get(estadoasignacion);
    }

    public void setSede(Sede sede) { this.sede = sede; }

    public Sede getSede() {
        return sede;
    }

    public Inventario getProductoinventario() {
        return productoinventario;
    }

    public void setProductoinventario(Inventario productoinventario) {
        this.productoinventario = productoinventario;
    }

    public Integer getEstadoasignacion() {
        return estadoasignacion;
    }

    public void setEstadoasignacion(Integer estadoasignacion) {
        this.estadoasignacion = estadoasignacion;
    }

    public Float getPrecioventa() {
        return precioventa;
    }

    public void setPrecioventa(Float precioventa) {
        this.precioventa = precioventa;
    }
}
