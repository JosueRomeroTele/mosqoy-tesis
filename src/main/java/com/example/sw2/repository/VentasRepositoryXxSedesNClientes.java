package com.example.sw2.repository;

import com.example.sw2.constantes.VentasId;
import com.example.sw2.dtoReportes.ReportesTotalDto;
import com.example.sw2.dtoReportes.ReportesSedesDto;
import com.example.sw2.entity.Ventas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VentasRepositoryXxSedesNClientes extends JpaRepository<Ventas, VentasId> {


    //SEDES ALEX
    @Query(value="select distinct v.vendedor from mosqoy.Ventas v inner join mosqoy.Usuarios u on (v.vendedor = u.dni) where u.rol=2;",nativeQuery=true)
    List<ReportesSedesDto> obtenerSedes();

    @Query(value="SELECT CONCAT(u.nombre,' ',u.apellido) as nombre, u.dni, u.correo, u.telefono, sum(v.precio_venta) as sumaventas, sum(v.cantidad) as cantidadvendidos FROM Ventas v inner join Usuarios u ON (v.vendedor = u.dni) WHERE YEAR(v.fecha) = ?1 AND u.rol = 2 group by v.vendedor",nativeQuery=true)
    List<ReportesTotalDto> obtenerReporteAnualSede(int anho);

    @Query(value="SELECT CONCAT(u.nombre,' ',u.apellido) as nombre, u.dni, u.correo, u.telefono, sum(v.precio_venta) as sumaventas, sum(v.cantidad) as cantidadvendidos FROM Ventas v inner join Usuarios u ON (v.vendedor = u.dni) WHERE QUARTER(v.fecha) = ?1 AND YEAR(v.fecha) = ?2 AND u.rol = 2 group by v.vendedor",nativeQuery=true)
    List<ReportesTotalDto> obtenerReporteTrimestralSede(int trimestre, int anho);

    @Query(value="SELECT CONCAT(u.nombre,' ',u.apellido) as nombre, u.dni, u.correo, u.telefono, sum(v.precio_venta) as sumaventas, sum(v.cantidad) as cantidadvendidos FROM Ventas v inner join Usuarios u ON (v.vendedor = u.dni) WHERE MONTH(v.fecha) = ?1 AND YEAR(v.fecha) = ?2 AND u.rol = 2 group by v.vendedor ",nativeQuery=true)
    List<ReportesTotalDto> obtenerReporteMensualSede(int mes, int anho);

    //CLIENTES
    @Query(value="SELECT DISTINCT(cliente) FROM (SELECT v.ruc_dni AS \"cliente\" FROM mosqoy.Ventas v) sub",nativeQuery=true)
    List<ReportesSedesDto> obtenerClientes();

    @Query(value="SELECT v.nombrecliente as nombre , v.ruc_dni, v.productoinventario, sum(v.precio_venta), sum(v.cantidad) FROM Ventas v WHERE YEAR(v.fecha) = ?2 group by v.ruc_dni",nativeQuery=true)
    List<ReportesTotalDto> obtenerReporteAnualCliente(int anho);

    @Query(value="SELECT v.nombrecliente as nombre , v.ruc_dni, v.productoinventario, sum(v.precio_venta), sum(v.cantidad) FROM Ventas v WHERE QUARTER(v.fecha) = ?1 AND YEAR(v.fecha) = ?2 group by v.ruc_dni",nativeQuery=true)
    List<ReportesTotalDto> obtenerReporteTrimestralCliente(int trimestre, int anho);

    @Query(value="SELECT v.nombrecliente as nombre , v.ruc_dni, v.productoinventario, sum(v.precio_venta), sum(v.cantidad) FROM Ventas v WHERE MONTH(v.fecha) = ?1 AND YEAR(v.fecha) = ?2 group by v.ruc_dni",nativeQuery=true)
    List<ReportesTotalDto> obtenerReporteMensualCliente(int mes, int anho);

    // FIN SEDES ALEX
}
