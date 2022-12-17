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
@Table(name="Comunidades")
public class Comunidades extends Auditable implements Serializable {

    @Id
    @NotBlank
    @Size(max = 2, message = "El codigo debe contener 2 caracteres" )
    @Pattern(regexp = "^[A-Za-zÀ-ÿ]+$", message = "Ingrese solo caracteres válidos")
    private String codigo;
    @NotBlank
    @Size(max = 45, message = "El nombre debe tener 45 caracteres como máximo")
    @Column(nullable = false)
    @Pattern(regexp = "^[A-Za-zÀ-ÿ'. ]+$", message = "Ingrese solo caracteres válidos")
    private String nombre;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "comunidades")
    private List<Artesanos> artesanos;




    public List<Artesanos> getArtesanos() {
        return artesanos;
    }

    public void setArtesanos(List<Artesanos> artesanos) {
        this.artesanos = artesanos;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo.trim().toUpperCase();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre.trim();
    }
}
