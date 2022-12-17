package com.example.sw2.entity;


import com.example.sw2.config.Auditable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name="Asignacion_tienda")
public class AsignacionTiendas extends Auditable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idtiendas;
    @Digits(integer = 6, fraction = 0, message = "Ingrese un número entero.")
    @Positive(message = "Debe asignar un numero mayor a cero.")
    @Column(nullable = false)
    private int stock;
    @NotNull(message = "Seleccione una fecha de asignacion.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name="fecha_asignacion", nullable = false)
    private LocalDate fechaasignacion;

    @ManyToOne
    @JoinColumn(name="tienda",nullable = false)
    @NotNull(message = "Seleccione una tienda de la lista.")
    private Tienda tienda;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "sede", referencedColumnName = "sede"),
            @JoinColumn(name = "producto_inventario", referencedColumnName = "producto_inventario"),
            @JoinColumn(name = "estadoasignacion",referencedColumnName = "estadoasignacion"),
            @JoinColumn(name = "precioventa",referencedColumnName = "precioventa")
    })
    private AsignadosSedes asignadosSedes;

    public AsignacionTiendas(){}



    public int getIdtiendas() {
        return idtiendas;
    }

    public void setIdtiendas(int idtiendas) {
        this.idtiendas = idtiendas;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public LocalDate getFechaasignacion() {
        return fechaasignacion;
    }

    public void setFechaasignacion(LocalDate fechaasignacion) {
        this.fechaasignacion = fechaasignacion;
    }

    public Tienda getTienda() {
        return tienda;
    }

    public void setTienda(Tienda tienda) {
        this.tienda = tienda;
    }

    public AsignadosSedes getAsignadosSedes() {
        return asignadosSedes;
    }

    public void setAsignadosSedes(AsignadosSedes asignadosSedes) {
        this.asignadosSedes = asignadosSedes;
    }

    public String getFechaAsignacionStr() {
        String fecha = "---";
        if (this.fechaasignacion != null) {
            fecha =fechaasignacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        return fecha;
    }
}
