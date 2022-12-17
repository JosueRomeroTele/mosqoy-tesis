package com.example.sw2.entity;

import com.example.sw2.config.Auditable;
import com.example.sw2.constantes.VentasId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.example.sw2.constantes.CustomConstants.MediosDePago;


@Entity
@Table(name = "Ventas")
public class Ventas extends Auditable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idventas;
    @Valid
    @Embedded
    private VentasId id;
    @NotNull(message = "Debe seleccionar si está confirmado o no")
    private Boolean confirmado = false;
    //@Size(min= 8, max = 11, message = "El Ruc/Dni debe contener 8 o 11 caracteres")
    @Column(name = "ruc_dni")
    @Pattern(regexp = "^[0-9]*$", message = "Ingrese solo caracteres numéricos")
    private String rucdni;
    @NotBlank(message = "Este campo no puede estar vacío")
    @Size(max = 45, message = "El nombre debe contener 45 caracteres")
    @Column(nullable = false)
    @Pattern(regexp = "^[A-Za-zÀ-ÿ ]*$", message = "Ingrese solo caracteres alfabéticos")
    private String nombrecliente;
    @Column(nullable = false)
    @NotBlank(message = "Este campo no puede estar vacío")
    private String lugarventa;
    @ManyToOne
    @JoinColumn(name = "productoinventario", nullable = false)
    @NotNull()
    private Inventario inventario;
    @Column(nullable = false)
    @NotNull(message = "Ingrese una fecha")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;
    @ManyToOne
    @JoinColumn(name = "vendedor", nullable = false)
    private Usuarios vendedor;
    @NotNull(message = "Ingrese una cantidad")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    @Column(nullable = false)
    private int cantidad;
    @Column(name = "precio_venta", nullable = false)
    @NotNull(message = "Debe ingresar un precio de venta")
    @DecimalMin(value = "0.0", inclusive = false, message = "Ingrese un precio válido")
    @Digits(integer=10, fraction=2, message = "El precio debe tener 2 decimales y 10 dígitos como máximo")
    private BigDecimal precioventa;

    //@Positive
    private Integer nota;
    @JsonIgnore
    private String mensaje="";
    private Integer cancelar;

    private String media;

    private Integer mediopago;

    @Transient
    private int cantDevol;


    public Ventas(){

    }

    public Ventas(Usuarios vendedor, Inventario producto){
        this.vendedor = vendedor;
        this.inventario = producto;
    }

    public Integer getMediopago() {
        return mediopago;
    }

    public boolean validateMedioPago(){
        try
        {
            return mediopago!= null & (mediopago>0 && this.mediopago<(MediosDePago.size() + 1));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public void setMediopago(Integer mediopago) {
        this.mediopago = mediopago;
    }

    public String getNombreMedioDePago(){
        return MediosDePago.get(this.mediopago);
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public Boolean getConfirmado() {
        return confirmado;
    }

    public void setConfirmado(Boolean confirmado) {
        this.confirmado = confirmado;
    }

    public BigDecimal getSumaParcial(){
        return BigDecimal.valueOf(cantidad).multiply(precioventa);
    }

    public Integer getIdventas() {
        return idventas;
    }

    public void setIdventas(Integer idventas) {
        this.idventas = idventas;
    }

    public int getCantDevol() {
        return cantDevol;
    }

    public void setCantDevol(int cantDevol) {
        this.cantDevol = cantDevol;
    }

    public VentasId getId() {
        return id;
    }

    public void setId(VentasId id) {
        this.id = id;
    }


    public String getRucdni() {
        return rucdni;
    }

    public void setRucdni(String rucdni) {
        this.rucdni = rucdni.trim();
    }

    public String getNombrecliente() {
        return nombrecliente;
    }

    public void setNombrecliente(String nombrecliente) {
        this.nombrecliente = nombrecliente.trim();
    }

    public String getLugarventa() {
        return lugarventa;
    }

    public void setLugarventa(String lugarventa) {
        this.lugarventa = lugarventa.trim();
    }

    public Inventario getInventario() {
        return inventario;
    }

    public void setInventario(Inventario inventario) {
        this.inventario = inventario;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Usuarios getVendedor() {
        return vendedor;
    }

    public void setVendedor(Usuarios vendedor) {
        this.vendedor = vendedor;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioventa() {
        return precioventa;
    }

    public void setPrecioventa(BigDecimal precioventa) {
        this.precioventa = precioventa;
    }

    public LocalDateTime getFechamodificacion() {
        return fechamodificacion;
    }

    public void setFechamodificacion(LocalDateTime fechamodificacion) {
        this.fechamodificacion = fechamodificacion;
    }

    public LocalDateTime getFechacreacion() {
        return fechacreacion;
    }

    public void setFechacreacion(LocalDateTime fechacreacion) {
        this.fechacreacion = fechacreacion;
    }

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    public String getMensaje() {
        return mensaje.trim();
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Integer getCancelar() {
        return cancelar;
    }

    public void setCancelar(Integer cancelar) {
        this.cancelar = cancelar;
    }

    public String getFechaDeVentaStr() {
        String fecha2 = "---";
        if (this.fecha != null) {
            fecha2 =fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        return fecha2;
    }
}
