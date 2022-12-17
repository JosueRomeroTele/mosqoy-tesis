package com.example.sw2.entity;

import com.example.sw2.config.Auditable;
import com.example.sw2.constantes.CustomConstants;
import com.example.sw2.constantes.ProductoId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@EnableJpaAuditing
@Table(name = "Productos")
public class Productos extends Auditable implements Serializable {

    @Valid
    @EmbeddedId
    private ProductoId id;
    @Column(nullable = false)
    @NotBlank
    @Pattern(regexp = "^[A-Za-zÀ-ÿ'. ]+$", message = "Ingrese solo caracteres válidos")
    private String nombre;
    @Column(nullable = false)
    @Size(max = 45, message = "Máximo 45 caracteres")
    private String descripcion;
    @NotBlank
    @Column(nullable = false)
    @Size(max = 3, message = "El codigo debe contener máximo 3 caracteres")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ]+$", message = "Ingrese solo caracteres válidos")
    private String codigodesc;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "productos")
    private List<Inventario> inventario;

    public List<Inventario> getInventario() {
        return inventario;
    }
    public void setInventario(List<Inventario> inventario) {
        this.inventario = inventario;
    }


    public ProductoId getId() {
        return id;
    }

    public void setId(ProductoId id) {
        this.id = id;
    }

    public Productos(){
    }

    public Productos(String cod){
        this.id = new ProductoId();
        this.id.setCodigonom(cod);
    }


    public void setCodigolinea(String codigolinea) {
        this.id.setCodigolinea(codigolinea.trim());
    }

    public String getCodigolinea() {
        return id.getCodigolinea();
    }


    public String getCodigonom() {
        return id.getCodigonom();
    }

    public void setCodigonom(String codigonom) {
        this.id.setCodigonom(codigonom.toUpperCase().trim());
    }

    public String getNombreLinea(){
        return CustomConstants.getLineas().get(this.id.getCodigolinea());
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCodigodesc() {
        return codigodesc;
    }

    public void setCodigodesc(String codigodesc) {
        this.codigodesc = codigodesc.toUpperCase();
    }

}
