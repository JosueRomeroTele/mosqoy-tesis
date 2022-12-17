package com.example.sw2.entity;


import com.example.sw2.config.Auditable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="Categorias")
public class Categorias extends Auditable implements Serializable {

    @Id
    @NotBlank
    @Size(max = 1, message = "El codigo debe contener máximo 1 caracter" )
    @Pattern(regexp = "^[A-Za-zÀ-ÿ]$", message = "Ingrese solo caracteres válidos")
    private String codigo;
    @NotBlank
    @Size(max = 10, message = "El nombre debe contener como máximo 10 caracteres" )
    @Pattern(regexp = "^[A-Za-zÀ-ÿ'. ]+$", message = "Ingrese solo caracteres válidos")
    @Column(nullable = false)
    private String nombre;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "categorias")
    private List<Inventario> inventario;

    public List<Inventario> getInventario() {
        return inventario;
    }

    public void setInventario(List<Inventario> inventario) {
        this.inventario = inventario;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo.toUpperCase().trim();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre.trim();
    }

}
