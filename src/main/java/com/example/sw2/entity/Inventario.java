package com.example.sw2.entity;

import com.example.sw2.config.Auditable;
import com.example.sw2.constantes.CustomConstants;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;


import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Table(name = "Inventario")
public class Inventario extends Auditable implements Serializable {

    @Id
    @Column(name = "codigo_inventario")
    private String codigoinventario;

    @Min(value = 1, message = "Ingrese un número válido.")
    @Digits(integer = 11, fraction = 0, message = "Ingrese un número entero.")
    @Column(nullable = false)
    private int numpedido;

    @ManyToOne
    @JoinColumn(name = "categoria", nullable = false)
    @NotNull(message = "Seleccione una categoría de la lista.")
    private Categorias categorias;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "producto", nullable = false, referencedColumnName = "codigonom"),
            @JoinColumn(name = "linea", nullable = false, referencedColumnName = "linea")})
    private Productos productos;

    @Column(name = "tamanho", nullable = false)
    private String codtamanho;

    @ManyToOne
    @JoinColumn(name = "comunidad", nullable = false)
    @NotNull(message = "Seleccione una comunidad de la lista.")
    private Comunidades comunidades;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "artesano")
    private Artesanos artesanos;

    @Digits(integer = 11, fraction = 0, message = "Ingrese un número entero.")
    @Positive(message = "Ingrese una cantidad válida.")
    @Column(name = "cantidad_total", nullable = false)
    private int cantidadtotal;

    @Column(name = "cantidad_gestor", nullable = false)
    private int cantidadgestor;

    @Range(min = 0, max = 1, message = "Seleccione un modo disponible.")
    @Column(name = "tipoadquisicion", nullable = false)
    private int codAdquisicion;

    @Column(name = "fecha_adquisicion")
    private LocalDate fechaadquisicion;

    private int dia;

    @Column(nullable = false)
    private int mes;

    @Column(nullable = false)
    private int anho;

    @Size(max = 45, message = "Máximo 45 caracteres.")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ ]*$", message = "Ingrese solo caracteres alfabéticos")
    private String color;

    @Column(nullable = false)
    private String foto;

    @Column(nullable = false)
    @Digits(integer = 9, fraction = 2, message = "Costo no válido.")
    @Positive(message = "Debe ser mayor a cero.")
    @NotNull(message = "Ingrese un costo.")
    private BigDecimal costotejedor;

    @Column(nullable = false)
    @Digits(integer = 9, fraction = 2, message = "Costo no válido.")
    @Positive(message = "Debe ser mayor a cero.")
    @NotNull(message = "Ingrese un costo.")
    private BigDecimal costomosqoy;

    @Column(nullable = false)
    @Size(max = 45, message = "Máximo 45 caracteres.")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ', ]*$", message = "Ingrese solo caracteres alfabéticos")
    @NotBlank(message = "Ingrese un facilitador.")
    private String facilitador;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_vencimiento_consignacion")
    private LocalDate fechavencimientoconsignacion;

    @Column(name = "devuelto_artesano", nullable = false)
    private boolean devuelto_artesano;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "inventario")
    private List<Ventas> ventas;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id.productoinventario")
    private List<AsignadosSedes> asignadosSedes;

    public boolean isDevuelto_artesano() {
        return devuelto_artesano;
    }

    public void setDevuelto_artesano(boolean devuelto_artesano) {
        this.devuelto_artesano = devuelto_artesano;
    }

    public List<Ventas> getVentas() {
        return ventas;
    }

    public void setVentas(List<Ventas> ventas) {
        this.ventas = ventas;
    }

    public List<AsignadosSedes> getAsignadosSedes() {
        return asignadosSedes;
    }

    public void setAsignadosSedes(List<AsignadosSedes> asignadosSedes) {
        this.asignadosSedes = asignadosSedes;
    }

    public Inventario() {
    }

    //public Inventario(String codigoinventario){this.codigoinventario=codigoinventario;}


    public void subtractCantidad(int c) {
        this.cantidadgestor -= c;
    }

    public void addCantidad(int c) {
        this.cantidadgestor += c;
        this.cantidadtotal+=c;
    }

    public void addCantidadTotal(int c) {
        this.cantidadtotal+=c;
    }

    public String getCodigoinventario() {
        return codigoinventario;
    }

    public void setCodigoinventario(String codigoinventario) {
        this.codigoinventario = codigoinventario;
    }

    public int getNumpedido() {
        return numpedido;
    }

    public void setNumpedido(int numpedido) {
        this.numpedido = numpedido;
    }

    public Categorias getCategorias() {
        return categorias;
    }

    public void setCategorias(Categorias categorias) {
        this.categorias = categorias;
    }

    public Productos getProductos() {
        return productos;
    }

    public void setProductos(Productos productos) {
        this.productos = productos;
    }

    public String getCodtamanho() {
        return codtamanho;
    }

    public void setCodtamanho(String codtamanho) {
        this.codtamanho = codtamanho;
    }

    public String getTamanho() {
        return CustomConstants.getTamanhos().get(this.codtamanho);
    }

    public Comunidades getComunidades() {
        return comunidades;
    }

    public void setComunidades(Comunidades comunidades) {
        this.comunidades = comunidades;
    }

    public Artesanos getArtesanos() {
        return artesanos;
    }

    public void setArtesanos(Artesanos artesanos) {
        this.artesanos = artesanos;
    }

    public int getCantidadtotal() {
        return cantidadtotal;
    }

    public void setCantidadtotal(int cantidadtotal) {
        this.cantidadtotal = cantidadtotal;
    }

    public int getCantidadgestor() {
        return cantidadgestor;
    }

    public void setCantidadgestor(int cantidadgestor) {
        this.cantidadgestor = cantidadgestor;
    }

    public int getCodAdquisicion() {
        return codAdquisicion;
    }

    public String getTipoAdquisicion() {
        return CustomConstants.getTiposAdquisicion().get(this.codAdquisicion);
    }

    public void setCodAdquisicion(int codAdquisicion) {
        this.codAdquisicion = codAdquisicion;
    }

    public LocalDate getFechaadquisicion() {
        return fechaadquisicion;
    }


    public String getFechaAdquiStr() {
        String fecha = "";
        if (this.dia != 0) {
            fecha = fechaadquisicion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        } else {
            fecha = fechaadquisicion.format(DateTimeFormatter.ofPattern("MM/yyyy"));
        }
        return fecha;
    }

    public void setFechaMesFORMAT(YearMonth fechamesformat) {
        this.setFechaadquisicion(fechamesformat.atEndOfMonth());
        this.setDia(0);
        this.setMes(fechamesformat.getMonthValue());
        this.setAnho(fechamesformat.getYear());
    }

    public void setFechaDiaFORMAT(LocalDate fechadiaformat) {
        this.setFechaadquisicion(fechadiaformat);
        this.setDia(fechadiaformat.getDayOfMonth());
        this.setMes(fechadiaformat.getMonthValue());
        this.setAnho(fechadiaformat.getYear());
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getMes() {
        return mes;
    }

    public String getMesCorto(int mes2) {
        return CustomConstants.getMeses().get(mes2);
    }

    public void setMes(int codmes) {
        this.mes = codmes;
    }


    public int getAnho() {
        return anho;
    }

    public void setAnho(int anho) {
        this.anho = anho;
    }


    public void setFechaadquisicion(LocalDate fechainicioconsignacion) {
        this.fechaadquisicion = fechainicioconsignacion;
    }


    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public BigDecimal getCostotejedor() {
        return costotejedor;
    }

    public void setCostotejedor(BigDecimal costotejedor) {
        this.costotejedor = costotejedor;
    }

    public BigDecimal getCostomosqoy() {
        return costomosqoy;
    }

    public void setCostomosqoy(BigDecimal costomosqoy) {
        this.costomosqoy = costomosqoy;
    }

    public String getFacilitador() {
        return facilitador;
    }

    public void setFacilitador(String facilitador) {
        this.facilitador = facilitador;
    }

    public LocalDate getFechavencimientoconsignacion() {
        return fechavencimientoconsignacion;
    }

    public String getFechaVencimientoStr() {
        String fecha = "---";
        if (this.fechavencimientoconsignacion != null) {
            fecha =fechavencimientoconsignacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        return fecha;
    }

    public void setFechavencimientoconsignacion(LocalDate fechavencimientoconsignacion) {
        this.fechavencimientoconsignacion = fechavencimientoconsignacion;
    }

}
