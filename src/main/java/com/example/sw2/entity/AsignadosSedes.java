package com.example.sw2.entity;
import com.example.sw2.config.Auditable;
import com.example.sw2.constantes.AsignadosSedesId;
import com.example.sw2.constantes.CustomConstants;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name="Asignados_sedes")
public class AsignadosSedes extends Auditable implements Serializable {

    @EmbeddedId
    private AsignadosSedesId id;
    @Column(name = "fecha_envio")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Selecione una fecha")
    private LocalDate fechaenvio;
    @NotNull(message = "Ingrese una cantidad")
    @Min(value = 0, message = "La cantidad debe ser mayor a 0")
    @Column(nullable = false)
    private Integer stock;
    @Column(nullable = false)
    private Integer cantidadactual;

    private String mensaje;

    @Column(name = "devuelto_artesano")
    private boolean devuelto_artesano;

    @OneToOne
    @JoinColumn(name = "encargado")
    private Usuarios encargado;

    public Usuarios getEncargado() {
        return encargado;
    }

    public void setEncargado(Usuarios encargado) {
        this.encargado = encargado;
    }

    /*
    @JsonIgnore
    @OneToMany(mappedBy = "asignadosSedes", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<AsignacionTiendas> sed;


    public Set<AsignacionTiendas> getSed() {
        return sed;
    }

    public void setSed(Set<AsignacionTiendas> sed) {
        this.sed = sed;
    }*/

    public boolean isDevuelto_artesano() {
        return devuelto_artesano;
    }

    public void setDevuelto_artesano(boolean devuelto_artesano) {
        this.devuelto_artesano = devuelto_artesano;
    }

    public AsignadosSedes(){

    }

    public AsignadosSedes(AsignadosSedesId id){
        this.id = id;
    }


    public AsignadosSedes(AsignadosSedesId id,AsignadosSedes as){
        this.id = id;
        fechaenvio = as.getFechaenvio();
        stock = as.getStock();
        cantidadactual = as.getCantidadactual();
    }


    public AsignadosSedesId getId() {
        return id;
    }

    public void setId(AsignadosSedesId id) {
        this.id = id;
    }

    public LocalDate getFechaenvio() {
        return fechaenvio;
    }

    public void setFechaenvio(LocalDate fechaenvio) {
        this.fechaenvio = fechaenvio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getCantidadactual() {
        return cantidadactual;
    }

    public void setCantidadactual(Integer cantidadactual) {
        this.cantidadactual = cantidadactual;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getFechaEnvioStr() {
        String fecha = "---";
        if (this.fechaenvio != null) {
            fecha =fechaenvio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        return fecha;
    }

    public String getClaseHtml() {
        String clase="";
        if (this.id.getEstadoasignacion()==CustomConstants.ESTADO_ENVIADO_A_SEDE){
            clase="badge badge-warning badge-pill";
        }else if(this.id.getEstadoasignacion()==CustomConstants.ESTADO_RECIBIDO_POR_SEDE){
            clase="badge badge-success badge-pill";
        }else if(this.id.getEstadoasignacion()==CustomConstants.ESTADO_DEVUELTO_POR_SEDE){
            clase="badge badge-secondary badge-pill";
        }else if(this.id.getEstadoasignacion()==CustomConstants.ESTADO_RECIBIDO_CON_PROBLEMAS){
            clase="badge badge-danger badge-pill";
        }
        return clase;
    }
}
