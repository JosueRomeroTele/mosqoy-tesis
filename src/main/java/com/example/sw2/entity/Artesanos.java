package com.example.sw2.entity;

import com.example.sw2.config.Auditable;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="Artesanos")
public class Artesanos extends Auditable implements Serializable {

    @Id
    @NotBlank(message = "Este campo no puede estar vacío")
    @Size(max = 4, min = 2, message = "El código debe contener entre 2 y 4 letras")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ']+$", message = "Ingrese solo caracteres válidos")
    private String codigo;
    @NotBlank(message = "Este campo no puede estar vacío")
    @Size(max = 20, message = "El nombre debe tener menos de 20 caracteres")
    @Column(nullable =false)
    @Pattern(regexp = "^[A-Za-zÀ-ÿ'. ]+$", message = "Ingrese solo caracteres válidos")
    private String nombre;
    @Size(max = 20, message = "El apellido debe tener menos de 20 caracteres")
    @NotBlank(message = "Este campo no puede estar vacío")
    @Column(nullable =false)
    @Pattern(regexp = "^[A-Za-zÀ-ÿ'. ]+$", message = "Ingrese solo caracteres válidos")
    private String apellidopaterno;
    @Size(max = 20, message = "El nombre debe tener menos de 20 caracteres")
    private String apellidomaterno;
    @ManyToOne
    @JoinColumn(name="comunidad",nullable =false)
    private Comunidades comunidades;



    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "artesanos")
    private List<Inventario> inventario;

    public List<Inventario> getInventario() {
        return inventario;
    }

    public void setInventario(List<Inventario> inventario) {
        this.inventario = inventario;
    }

    public Artesanos(){
        this.fechacreacion=LocalDateTime.now();
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

    public String getApellidopaterno() {
        return apellidopaterno;
    }

    public void setApellidopaterno(String apellidopaterno) {
        this.apellidopaterno = apellidopaterno.trim();
    }

    public String getApellidomaterno() {
        return apellidomaterno;
    }

    public void setApellidomaterno(String apellidomaterno) {
        this.apellidomaterno = apellidomaterno.trim();
    }

    public Comunidades getComunidades() {
        return comunidades;
    }

    public void setComunidades(Comunidades comunidades) {
        this.comunidades = comunidades;
    }

    public String getPartialFullname(){
        return this.nombre + " " + this.apellidopaterno;
    }

}
