package com.example.sw2.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Tienda")
public class Tienda implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idtienda")
    private int idtienda;
    @NotBlank(message = "El campo no puede estar vacío")
    @Column(nullable = false)
    private String nombre;
    @NotBlank(message = "El campo no puede estar vacío")
    @Pattern(regexp = "^[0-9]{11}$", message = "Ingresar un número de RUC válido (11 Dígitos)")
    @Column(nullable = false)
    private String ruc;
    @NotBlank(message = "El campo no puede estar vacío")
    @Column(nullable = true)
    private String direccion;

    @ManyToOne
    @JoinColumn(name = "sede", nullable = false)
    private Sede sede;

    public Sede getSede() {
        return sede;
    }

    public void setSede(Sede sede) {
        this.sede = sede;
    }

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tienda")
    private List<AsignacionTiendas> asignacionTiendas;


    public List<AsignacionTiendas> getAsignacionTiendas() {
        return asignacionTiendas;
    }

    public void setAsignacionTiendas(List<AsignacionTiendas> asignacionTiendas) {
        this.asignacionTiendas = asignacionTiendas;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getIdtienda() {
        return idtienda;
    }

    public void setIdtienda(int idtienda) {
        this.idtienda = idtienda;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }
}
