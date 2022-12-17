package com.example.sw2.repository;
import com.example.sw2.constantes.VentasId;
import com.example.sw2.dto.DatosGestorVentasDto;
import com.example.sw2.dto.DatosProductoVentaDto;
import com.example.sw2.dtoReportes.ReportesArticuloDto;
import com.example.sw2.dtoReportes.ReportesClienteDto;
import com.example.sw2.dtoReportes.ReportesComunidadDto;
import com.example.sw2.dtoReportes.ReportesSedesDto;
import com.example.sw2.dtoReportes.ReportesTotalDto;
import com.example.sw2.entity.Ventas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Repository
public interface VentasRepository extends JpaRepository<Ventas, Integer> {

    Optional<Ventas> findById(VentasId v);
    List<Ventas> findByInventario_CodigoinventarioAndConfirmado(String codigo,boolean confirmar);

    Optional<Ventas> findByIdventasAndConfirmado(Integer i, Boolean conf);


    List<Ventas> findByVendedor_Sede_IdsedeAndConfirmado(int id,boolean confirmado);

    @Query(value = "select distinct year(fecha) from Ventas", nativeQuery = true)
    List<Integer> getYears();

    List<Ventas> findVentasByConfirmado(boolean b);

    Optional<Ventas> findByIdventasAndConfirmadoAndId_Tipodocumento(Integer idventas, Boolean confirmado,  Integer id_tipodocumento);

    @Query(value = "SELECT * FROM mosqoybase.ventas  where nombrecliente = ?1 limit 1",nativeQuery = true)
    Ventas findByNombrecliente(String cliente);

    void deleteById(VentasId id);

    @Query(value="select v.idventas ,v.productoinventario as codigoproducto, p.nombre as nombreproducto, " +
            "c.nombre as comunidadproducto, i.tamanho as tamanhoproducto, " +
            "i.color as colorproducto, i.foto as fotoproducto,sum(v.cantidad) as cantidadventa, " +
            "sum(v.precio_venta) as venta " +
            "FROM Ventas v " +
            "inner join Inventario i on (v.productoinventario = i.codigo_inventario) " +
            "inner join Comunidades c on (i.comunidad = c.codigo) " +
            "inner join Productos p on (i.producto = p.codigonom) " +
            "inner join Usuarios u on u.dni=v.vendedor " +
            "inner join Sede s on s.idsede=u.sede " +
            "where v.confirmado = 1 and s.idsede=?1 " +
            "group by v.productoinventario, p.nombre",nativeQuery = true)
    List<DatosProductoVentaDto> obtenerDatosPorProducto(int idSede);

    @Query(value="SELECT p.nombre as nombreproducto, p.codigonom as codigoproducto,\n" +
            "v.tipodocumento as tipodocumento , v.numerodocumento as numerodocumento, \n" +
            "v.nombrecliente as nombrecliente, v.ruc_dni as rucdni, \n" +
            "v.cantidad as cantidadventa, v.precio_venta as precioventa, \n" +
            "v.fecha as fechaventa, v.lugarventa as lugarventa FROM Ventas v \n" +
            "INNER JOIN Inventario i ON (v.productoinventario = i.codigo_inventario)\n" +
            "INNER JOIN Productos p ON (i.producto = p.codigonom)",
            nativeQuery = true)
    List<DatosGestorVentasDto> obtenerDatosGestorVentas();

    @Query(value="SELECT COUNT(idventas) FROM Ventas WHERE vendedor = ?1",nativeQuery=true)
    String cantVentasPorGestor(int usuario);

    @Query(value="SELECT COUNT(idventas) FROM Ventas v INNER JOIN Usuarios u ON (v.vendedor = u.dni) WHERE u.rol = ?1",nativeQuery=true)
    String cantVentasTotalesDeGestores(int rol);

    @Query(value="SELECT SUM(v.cantidad) FROM Ventas v INNER JOIN Usuarios u ON (v.vendedor = u.dni) WHERE u.rol = ?1",nativeQuery=true)
    String cantProductosVendidosPorGestor(int rol);

    @Query(value="SELECT ven.* FROM Ventas ven WHERE ven.vendedor = ?1",nativeQuery=true)
    List<Ventas> buscarPorGestor(int gestor);

    List<Ventas> findByVendedor_Idusuarios(int dni);

    @Query(value = "SELECT ven.* FROM Ventas ven INNER JOIN Usuarios usu ON (ven.vendedor = usu.dni) WHERE usu.rol = ?1",
            nativeQuery = true)
    List<Ventas> buscarVentasDeAdmin(int rol);

    @Procedure(name = "dev_stock_inv")
    void dev_stock_inv(int cant_devol, String codigo);

    @Query(value="SELECT * FROM mosqoy.Ventas ven WHERE YEAR(ven.fecha_creacion) = ?1",nativeQuery=true)
    List<ReportesTotalDto> obtenerReporteAnual(int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv, mosqoy.Productos pro WHERE ven.productoinventario=inv.codigo_inventario AND inv.producto=pro.codigonom AND YEAR(ven.fecha) = ?1 AND pro.codigonom = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerReporteAnualxProducto(int anho, String codProd);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv, mosqoy.Comunidades com WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = com.codigo AND YEAR(ven.fecha) = ?1 AND com.codigo = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerReporteAnualxComunidad(int anho, String codCom);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven WHERE YEAR(ven.fecha) = ?1 AND ven.nombrecliente = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerReporteAnualxNombreCliente(int anho, String nomCliente);
    //////FIN REPORTES ANUALES

    @Query(value = "select p.nombre as nombreproducto, c.nombre as comunidadproducto, i.tamanho as tamanhoproducto, i.color as colorproducto, i.foto as fotoproducto,\n" +
            "            v.fecha as fechaventa,sum(v.cantidad) as cantidadventa, v.precio_venta as precioventa, v.productoinventario as codigoproducto\n" +
            "            FROM Ventas v inner join Inventario i on (v.productoinventario = i.codigo_inventario)\n" +
            "            inner join Comunidades c on (i.comunidad = c.codigo) inner join Productos p on (i.producto = p.codigonom) WHERE YEAR(fecha) = YEAR(CURRENT_DATE - INTERVAL 1 MONTH)\n" +
            "AND MONTH(fecha) = MONTH(CURRENT_DATE - INTERVAL 1 MONTH) group by v.productoinventario", nativeQuery = true)
    List<DatosProductoVentaDto> obtenerDatosPorProductoUltimoMes();

    @Query(value = "select p.nombre as nombreproducto, c.nombre as comunidadproducto, i.tamanho as tamanhoproducto, i.color as colorproducto, i.foto as fotoproducto,\n" +
            "            v.fecha as fechaventa,sum(v.cantidad) as cantidadventa, v.precio_venta as precioventa, v.productoinventario as codigoproducto\n" +
            "            FROM Ventas v inner join Inventario i on (v.productoinventario = i.codigo_inventario)\n" +
            "            inner join Comunidades c on (i.comunidad = c.codigo) inner join Productos p on (i.producto = p.codigonom) WHERE YEAR(fecha) = YEAR(CURRENT_DATE - INTERVAL 3 MONTH)\n" +
            "AND MONTH(fecha) <= MONTH(CURRENT_DATE - INTERVAL 1 MONTH) and month(fecha) >= month(current_date - interval 3 month) group by v.productoinventario", nativeQuery = true)
    List<DatosProductoVentaDto> obtenerDatosPorProductoUltimoTrimestre();



    /////////// ventas de una comunidad por anho especifico

    @Query(value="SELECT * FROM mosqoy.Ventas ven WHERE YEAR(ven.fecha) = ?1",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasGENERALESPorAnho(int anho);

    /////////// FIN ventas de una comunidad por anho especifico

    /////////// ventas de una comunidad por anho especifico

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv, mosqoy.Comunidades com WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadPorAnho(String comunidadId, int anho);

    /////////// FIN ventas de una comunidad por anho especifico

    /////////// ventas de una comunidad por TRIMESTRE especifico en un anho especifico

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv, mosqoy.Comunidades com WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND MONTH(ven.fecha) > 0 AND MONTH(ven.fecha) < 4 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadDelPRIMERTrimestre(String comunidadId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv, mosqoy.Comunidades com WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND MONTH(ven.fecha) > 3 AND MONTH(ven.fecha) < 7 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadDelSEGUNDOTrimestre(String comunidadId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv, mosqoy.Comunidades com WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND MONTH(ven.fecha) > 6 AND MONTH(ven.fecha) < 10 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadDelTERCERTrimestre(String comunidadId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv, mosqoy.Comunidades com WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND MONTH(ven.fecha) > 9 AND MONTH(ven.fecha) < 13 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadDelCUARTOTrimestre(String comunidadId, int anho);

    /////////// FIN ventas de una comunidad por TRIMESTRE especifico en un anho especifico

    /////////// ventas de una comunidad por MES especifico en un anho especifico

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND MONTH(ven.fecha) = 1 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadDeENERO(String comunidadId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND MONTH(ven.fecha) = 2 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadDeFEBRERO(String comunidadId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND MONTH(ven.fecha) = 3 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadDeMARZO(String comunidadId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND MONTH(ven.fecha) = 4 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadDeABRIL(String comunidadId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND MONTH(ven.fecha) = 5 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadDeMAYO(String comunidadId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND MONTH(ven.fecha) = 6 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadDeJUNIO(String comunidadId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND MONTH(ven.fecha) = 7 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadDeJULIO(String comunidadId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND MONTH(ven.fecha) = 8 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadDeAGOSTO(String comunidadId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND MONTH(ven.fecha) = 9 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadDeSETIEMBRE(String comunidadId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND MONTH(ven.fecha) = 10 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadDeOCTUBRE(String comunidadId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND MONTH(ven.fecha) = 11 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadDeNOVIEMBRE(String comunidadId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND MONTH(ven.fecha) = 12 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadDeDICIEMBRE(String comunidadId, int anho);

    /////////// FIN ventas de una comunidad por MES especifico en un anho especifico



























    /////////// ventas de un producto por anho especifico

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoPorAnho(String productoId, int anho);

    /////////// FIN ventas de un producto por anho especifico

    /////////// ventas de una producto por TRIMESTRE especifico en un anho especifico

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2 AND MONTH(ven.fecha) > 0 AND MONTH(ven.fecha) < 4",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoDelPRIMERTrimestre(String productoId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2 AND MONTH(ven.fecha) > 3 AND MONTH(ven.fecha) < 7",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoDelSEGUNDOTrimestre(String productoId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2 AND MONTH(ven.fecha) > 6 AND MONTH(ven.fecha) < 10",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoDelTERCERTrimestre(String productoId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2 AND MONTH(ven.fecha) > 9 AND MONTH(ven.fecha) < 13",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoDelCUARTOTrimestre(String productoId, int anho);

    /////////// FIN ventas de un producto por TRIMESTRE especifico en un anho especifico

    /////////// ventas de un producto por MES especifico en un anho especifico

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2 AND MONTH(ven.fecha) = 1",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoDeENERO(String productoId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2 AND MONTH(ven.fecha) = 2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoDeFEBRERO(String productoId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2 AND MONTH(ven.fecha) = 3",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoDeMARZO(String productoId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2 AND MONTH(ven.fecha) = 4",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoDeABRIL(String productoId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2 AND MONTH(ven.fecha) = 5",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoDeMAYO(String productoId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2 AND MONTH(ven.fecha) = 6",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoDeJUNIO(String productoId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2 AND MONTH(ven.fecha) = 7",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoDeJULIO(String productoId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2 AND MONTH(ven.fecha) = 8",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoDeAGOSTO(String productoId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2 AND MONTH(ven.fecha) = 9",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoDeSETIEMBRE(String productoId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2 AND MONTH(ven.fecha) = 10",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoDeOCTUBRE(String productoId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2 AND MONTH(ven.fecha) = 11",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoDeNOVIEMBRE(String productoId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2 AND MONTH(ven.fecha) = 12",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoDeDICIEMBRE(String productoId, int anho);

    /////////// FIN ventas de un producto por MES especifico en un anho especifico

    @Query(value = "SELECT v.* FROM Ventas v " +
            "inner join Usuarios u on u.dni=v.vendedor " +
            "inner join Sede s on s.idsede=u.sede " +
            "where s.idsede=?1  and v.confirmado =1",nativeQuery = true)
    List<Ventas> clientesConVentasEnSede(int sede);


    @Query(value = "SELECT * FROM Ventas v where v.confirmado = 1 group by nombrecliente ", nativeQuery = true)
    List<Ventas> clientesConVentas();









    //SEDES ALEX

    @Query(value="SELECT inv.codigo_inventario as codigoinventario, p.nombre as nombreproducto,p.codigonom as codigoproducto,p.linea as codigolinea, v.cantidad, v.precio_venta as precioventa, v.ruc_dni as rucdni, v.nombrecliente,v.numerodocumento, month(v.fecha) as mes, dayofmonth(v.fecha) as dia " +
            "FROM Ventas v " +
            "inner join Usuarios u on u.dni=v.vendedor " +
            "inner join Inventario inv on inv.codigo_inventario=v.productoinventario " +
            "inner join Productos p on p.codigonom = inv.producto where year(v.fecha)=?1 and u.sede=?2 AND v.confirmado = 1 ",nativeQuery=true)
    List<ReportesSedesDto> obtenerReporteAnualSede(int anho,int sede);

    @Query(value="SELECT inv.codigo_inventario as codigoinventario, p.nombre as nombreproducto,p.codigonom as codigoproducto,p.linea as codigolinea, v.cantidad, v.precio_venta as precioventa, v.ruc_dni as rucdni, v.nombrecliente,v.numerodocumento, month(v.fecha) as mes, dayofmonth(v.fecha) as dia " +
            "FROM Ventas v " +
            "inner join Usuarios u on u.dni=v.vendedor " +
            "inner join Inventario inv on inv.codigo_inventario=v.productoinventario " +
            "inner join Productos p on p.codigonom = inv.producto where u.sede=?1 and year(v.fecha)=?2 and QUARTER(v.fecha) = ?3 AND v.confirmado = 1",nativeQuery=true)
    List<ReportesSedesDto> obtenerReporteTrimestralSede(int sede, int anho, int trimestre);

    @Query(value="SELECT inv.codigo_inventario as codigoinventario, p.nombre as nombreproducto,p.codigonom as codigoproducto,p.linea as codigolinea, v.cantidad, v.precio_venta as precioventa, v.ruc_dni as rucdni, v.nombrecliente,v.numerodocumento, month(v.fecha) as mes, dayofmonth(v.fecha) as dia \n" +
            "FROM Ventas v " +
            "inner join Usuarios u on u.dni=v.vendedor " +
            "inner join Inventario inv on inv.codigo_inventario=v.productoinventario " +
            "inner join Productos p on p.codigonom = inv.producto where u.sede=?1 and year(v.fecha)=?3 and month(v.fecha) = ?2 AND v.confirmado = 1",nativeQuery=true)
    List<ReportesSedesDto> obtenerReporteMensualSede(int sede,int mes, int anho);

    //FIN SEDES ALEX

    //CLIENTES ALEX

    @Query(value = "SELECT v.numerodocumento ,inv.codigo_inventario,p.nombre as producto,month(v.fecha) as mes,dayofmonth(v.fecha) as dia,v.cantidad,v.precio_venta as precioventa " +
            "FROM Ventas v " +
            "inner join Inventario inv on inv.codigo_inventario=v.productoinventario " +
            "inner join Productos p on p.codigonom =inv.producto and p.linea =inv.linea " +
            "where v.nombrecliente=?1 and year(v.fecha)=?2 AND v.confirmado = 1",nativeQuery = true)
    List<ReportesClienteDto> obtenerReporteAnualCliente(String cliente,int anho);

    @Query(value = "SELECT v.numerodocumento ,inv.codigo_inventario,p.nombre as producto,month(v.fecha) as mes,dayofmonth(v.fecha) as dia,v.cantidad,v.precio_venta as precioventa " +
            "FROM Ventas v " +
            "inner join Inventario inv on inv.codigo_inventario=v.productoinventario " +
            "inner join Productos p on p.codigonom =inv.producto and p.linea =inv.linea " +
            "where v.nombrecliente=?1 and year(v.fecha)=?2 and quarter(v.fecha)=?3 AND v.confirmado = 1",nativeQuery = true)
    List<ReportesClienteDto> obtenerReporteTrimestralCliente(String cliente, int anho, int trimestre);

    @Query(value = "SELECT v.numerodocumento ,inv.codigo_inventario,p.nombre as producto,month(v.fecha) as mes,dayofmonth(v.fecha) as dia,v.cantidad,v.precio_venta as precioventa " +
            "FROM Ventas v " +
            "inner join Inventario inv on inv.codigo_inventario=v.productoinventario " +
            "inner join Productos p on p.codigonom =inv.producto and p.linea =inv.linea " +
            "where v.nombrecliente=?1 and year(v.fecha)=?2 and month(v.fecha)=?3 AND v.confirmado = 1",nativeQuery = true)
    List<ReportesClienteDto> obtenerReporteMensualCliente(String cliente,int anho, int mes);

    // FIN CLIENTES ALEX

    //COMUNIDAD FER

    @Query(value = "SELECT inv.codigo_inventario as codigoinventario,concat(art.nombre,' ',art.apellidopaterno,' ',art.apellidomaterno) as artesano, p.nombre as nombreproducto,p.codigonom as codigoproducto,p.linea as codigolinea, v.cantidad, v.precio_venta as precioventa, v.ruc_dni as rucdni, v.nombrecliente,v.numerodocumento, month(v.fecha) as mes, dayofmonth(v.fecha) as dia " +
            "FROM Ventas v " +
            "inner join Usuarios u on u.dni=v.vendedor " +
            "inner join Inventario inv on inv.codigo_inventario=v.productoinventario " +
            "inner join Productos p on p.codigonom = inv.producto " +
            "inner join comunidades com on inv.comunidad=com.codigo " +
            "inner join artesanos art on art.codigo=inv.artesano " +
            "where com.codigo=?1 and year(v.fecha)=?2 AND v.confirmado = 1 ",nativeQuery = true)
    List<ReportesComunidadDto> obtenerReporteAnualComunidad(String codComunidad,int anho);

    @Query(value = "SELECT inv.codigo_inventario as codigoinventario,concat(art.nombre,' ',art.apellidopaterno,' ',art.apellidomaterno) as artesano, p.nombre as nombreproducto,p.codigonom as codigoproducto,p.linea as codigolinea, v.cantidad, v.precio_venta as precioventa, v.ruc_dni as rucdni, v.nombrecliente,v.numerodocumento, month(v.fecha) as mes, dayofmonth(v.fecha) as dia " +
            "FROM Ventas v " +
            "inner join Usuarios u on u.dni=v.vendedor " +
            "inner join Inventario inv on inv.codigo_inventario=v.productoinventario " +
            "inner join Productos p on p.codigonom = inv.producto " +
            "inner join Comunidades com on inv.comunidad=com.codigo " +
            "inner join Artesanos art on art.codigo=inv.artesano " +
            "where com.codigo=?1 and year(v.fecha)=?2 and quarter(v.fecha)=?3 AND v.confirmado = 1",nativeQuery = true)
    List<ReportesComunidadDto> obtenerReporteTrimestralComunidad(String codComunidad, int anho, int trimestre);

    @Query(value = "SELECT inv.codigo_inventario as codigoinventario,concat(art.nombre,' ',art.apellidopaterno,' ',art.apellidomaterno) as artesano, p.nombre as nombreproducto,p.codigonom as codigoproducto,p.linea as codigolinea, v.cantidad, v.precio_venta as precioventa, v.ruc_dni as rucdni, v.nombrecliente,v.numerodocumento, month(v.fecha) as mes, dayofmonth(v.fecha) as dia " +
            "FROM Ventas v " +
            "inner join Usuarios u on u.dni=v.vendedor " +
            "inner join Inventario inv on inv.codigo_inventario=v.productoinventario " +
            "inner join Productos p on p.codigonom = inv.producto " +
            "inner join Comunidades com on inv.comunidad=com.codigo " +
            "inner join Artesanos art on art.codigo=inv.artesano " +
            "where com.codigo=?1 and year(v.fecha)=?2 and month(v.fecha)=?3 AND v.confirmado = 1",nativeQuery = true)
    List<ReportesComunidadDto> obtenerReporteMensualComunidad(String codComunidad, int anho, int mes);

    //FIN COMUNIDAD FER

    //ARTICULOS FER

    @Query(value="SELECT v.nombrecliente, v.ruc_dni as rucdni, v.productoinventario,month(v.fecha) as mes, dayofmonth(v.fecha) as dia ,v.cantidad,v.precio_venta as precioventa FROM Ventas v " +
            "inner join Inventario inv on inv.codigo_inventario= v.productoinventario " +
            "inner join Productos p on p.codigonom=inv.producto " +
            "where p.nombre= ?1 and p.linea=?2 and year(v.fecha)= ?3 AND v.confirmado = 1",nativeQuery=true)
    List<ReportesArticuloDto> obtenerReporteAnualArticuloProducto(String idcodProd,String linea,int anho);

    @Query(value="SELECT v.nombrecliente, v.ruc_dni as rucdni, v.productoinventario,month(v.fecha) as mes, dayofmonth(v.fecha) as dia ,v.cantidad,v.precio_venta as precioventa FROM Ventas v " +
            "inner join Inventario inv on inv.codigo_inventario= v.productoinventario " +
            "inner join Productos p on p.codigonom=inv.producto " +
            "where p.nombre= ?1 and p.linea=?2 and year(v.fecha)= ?3 and quarter(v.fecha)=?4 AND v.confirmado = 1",nativeQuery=true)
    List<ReportesArticuloDto> obtenerReporteTrimestralArticuloProducto(String nomProd,String codLinea ,int anho, int  trimestre);

    @Query(value="SELECT v.nombrecliente, v.ruc_dni as rucdni, v.productoinventario,month(v.fecha) as mes, dayofmonth(v.fecha) as dia ,v.cantidad,v.precio_venta as precioventa FROM Ventas v " +
            "inner join Inventario inv on inv.codigo_inventario= v.productoinventario " +
            "inner join Productos p on p.codigonom=inv.producto " +
            "where p.nombre= ?1 and p.linea=?2 and year(v.fecha)= ?3 and month(v.fecha)=?4 AND v.confirmado = 1",nativeQuery=true)
    List<ReportesArticuloDto> obtenerReporteMensualArticuloProducto(String nomProd,String codLinea, int anho, int mes);

    //FIN ARTICULOS FER

    //TOTAL FER

    @Query(value="SELECT ven.ruc_dni, ven.nombrecliente, ven.mediopago, ven.tipodocumento, ven.numerodocumento, ven.precio_venta , ven.lugarventa, ven.productoinventario, ven.fecha, ven.cantidad, CONCAT(usu.nombre, \" \",usu.apellido) AS \"vendedor\", usu.dni AS \"dnivendedor\", ven.media FROM Ventas ven, Usuarios usu WHERE YEAR(ven.fecha) = ?1 AND ven.confirmado = 1 AND ven.vendedor = usu.dni",nativeQuery=true)
    List<ReportesTotalDto> obtenerReporteAnualTotal(int anho);

    @Query(value="SELECT ven.ruc_dni, ven.nombrecliente, ven.mediopago, ven.tipodocumento, ven.numerodocumento, ven.precio_venta , ven.lugarventa, ven.productoinventario, ven.fecha, ven.cantidad, CONCAT(usu.nombre, \" \",usu.apellido) AS \"vendedor\", usu.dni AS \"dnivendedor\", ven.media FROM Ventas ven, Usuarios usu WHERE QUARTER(ven.fecha) = ?1 AND ven.confirmado = 1 AND YEAR(ven.fecha) = ?2 AND ven.vendedor = usu.dni",nativeQuery=true)
    List<ReportesTotalDto> obtenerReporteTrimestralTotal(int trimestre, int anho);

    @Query(value="SELECT ven.ruc_dni, ven.nombrecliente, ven.mediopago, ven.tipodocumento, ven.numerodocumento, ven.precio_venta , ven.lugarventa, ven.productoinventario, ven.fecha, ven.cantidad, CONCAT(usu.nombre, \" \",usu.apellido) AS \"vendedor\", usu.dni AS \"dnivendedor\", ven.media FROM Ventas ven, Usuarios usu WHERE MONTH(ven.fecha) = ?1 AND ven.confirmado = 1 AND YEAR(ven.fecha) = ?2 AND ven.vendedor = usu.dni",nativeQuery=true)
    List<ReportesTotalDto> obtenerReporteMensualTotal(int mes, int anho);

    // FIN TOTAL FER







    /*----------------------------------- QUERIES PARA SEDES -----------------------------------*/

    //TOTAL ALEX

    @Query(value="SELECT ven.ruc_dni, ven.nombrecliente, ven.mediopago, ven.tipodocumento, ven.precio_venta, ven.numerodocumento, ven.lugarventa, ven.productoinventario, ven.fecha, ven.cantidad, CONCAT(usu.nombre, \" \",usu.apellido) AS \"vendedor\", usu.dni AS \"dnivendedor\" FROM Ventas ven, Usuarios usu WHERE YEAR(ven.fecha) = ?1 AND ven.confirmado = 1 AND ven.vendedor = usu.dni AND usu.sede = ?2 ",nativeQuery=true)
    List<ReportesTotalDto> obtenerReporteSedeAnualTotal(int anho, int idsede);

    @Query(value="SELECT ven.ruc_dni, ven.nombrecliente, ven.mediopago, ven.tipodocumento, ven.precio_venta, ven.numerodocumento, ven.lugarventa, ven.productoinventario, ven.fecha, ven.cantidad, CONCAT(usu.nombre, \" \",usu.apellido) AS \"vendedor\", usu.dni AS \"dnivendedor\" FROM Ventas ven, Usuarios usu WHERE QUARTER(ven.fecha) = ?1 AND YEAR(ven.fecha) = ?2 AND ven.confirmado = 1 AND ven.vendedor = usu.dni AND usu.sede  = ?3",nativeQuery=true)
    List<ReportesTotalDto> obtenerReporteSedeTrimestralTotal(int trimestre, int anho, int idusuario);

    @Query(value="SELECT ven.ruc_dni, ven.nombrecliente, ven.mediopago, ven.tipodocumento, ven.precio_venta, ven.numerodocumento, ven.lugarventa, ven.productoinventario, ven.fecha, ven.cantidad, CONCAT(usu.nombre, \" \",usu.apellido) AS \"vendedor\", usu.dni AS \"dnivendedor\" FROM Ventas ven, Usuarios usu WHERE MONTH(ven.fecha) = ?1 AND YEAR(ven.fecha) = ?2 AND ven.confirmado = 1 AND ven.vendedor = usu.dni AND usu.sede = ?3",nativeQuery=true)
    List<ReportesTotalDto> obtenerReporteSedeMensualTotal(int mes, int anho, int idusuario);

    // FIN TOTAL ALEX

    //COMUNIDAD FER

    @Query(value = "SELECT inv.codigo_inventario as codigoinventario,concat(art.nombre,' ',art.apellidopaterno,' ',art.apellidomaterno) as artesano, p.nombre as nombreproducto,p.codigonom as codigoproducto,p.linea as codigolinea, v.cantidad, v.precio_venta as precioventa, v.ruc_dni as rucdni, v.nombrecliente,v.numerodocumento, month(v.fecha) as mes, dayofmonth(v.fecha) as dia " +
            "FROM Ventas v " +
            "inner join Usuarios u on u.dni=v.vendedor " +
            "inner join Inventario inv on inv.codigo_inventario=v.productoinventario " +
            "inner join Productos p on p.codigonom = inv.producto " +
            "inner join Comunidades com on inv.comunidad=com.codigo " +
            "inner join Artesanos art on art.codigo=inv.artesano " +
            "where com.codigo=?1 and year(v.fecha)=?2 and u.sede=?3 AND v.confirmado = 1 ",nativeQuery = true)
    List<ReportesComunidadDto> obtenerReporteSedeAnualComunidad(String cod,int anho, int idsede);

    @Query(value = "SELECT inv.codigo_inventario as codigoinventario,concat(art.nombre,' ',art.apellidopaterno,' ',art.apellidomaterno) as artesano, p.nombre as nombreproducto,p.codigonom as codigoproducto,p.linea as codigolinea, v.cantidad, v.precio_venta as precioventa, v.ruc_dni as rucdni, v.nombrecliente,v.numerodocumento, month(v.fecha) as mes, dayofmonth(v.fecha) as dia " +
            "FROM Ventas v " +
            "inner join Usuarios u on u.dni=v.vendedor " +
            "inner join Inventario inv on inv.codigo_inventario=v.productoinventario " +
            "inner join Productos p on p.codigonom = inv.producto " +
            "inner join Comunidades com on inv.comunidad=com.codigo " +
            "inner join Artesanos art on art.codigo=inv.artesano " +
            "where com.codigo=?1 and year(v.fecha)=?2 and u.sede=?3 and quarter(v.fecha) =?4 AND v.confirmado = 1 ",nativeQuery = true)
    List<ReportesComunidadDto> obtenerReporteSedeTrimestralComunidad(String codCom, int anho, int idsede,int trimestre);

    @Query(value = "SELECT inv.codigo_inventario as codigoinventario,concat(art.nombre,' ',art.apellidopaterno,' ',art.apellidomaterno) as artesano, p.nombre as nombreproducto,p.codigonom as codigoproducto,p.linea as codigolinea, v.cantidad, v.precio_venta as precioventa, v.ruc_dni as rucdni, v.nombrecliente,v.numerodocumento, month(v.fecha) as mes, dayofmonth(v.fecha) as dia " +
            "FROM Ventas v " +
            "inner join Usuarios u on u.dni=v.vendedor " +
            "inner join Inventario inv on inv.codigo_inventario=v.productoinventario " +
            "inner join Productos p on p.codigonom = inv.producto " +
            "inner join Comunidades com on inv.comunidad=com.codigo " +
            "inner join Artesanos art on art.codigo=inv.artesano " +
            "where com.codigo=?1 and year(v.fecha)=?2 and u.sede=?3 and month(v.fecha) =?4 AND v.confirmado = 1 ",nativeQuery = true)
    List<ReportesComunidadDto> obtenerReporteSedeMensualComunidad(String cod, int anho, int idsede,int mes);

    //FIN COMUNIDAD FER


    //CLIENTES

    @Query(value = "SELECT v.numerodocumento ,inv.codigo_inventario,p.nombre as producto,month(v.fecha) as mes,dayofmonth(v.fecha) as dia,v.cantidad,v.precio_venta as precioventa " +
            "FROM Ventas v " +
            "inner join Inventario inv on inv.codigo_inventario=v.productoinventario " +
            "inner join Productos p on p.codigonom =inv.producto and p.linea =inv.linea " +
            "inner join Usuarios u on u.dni=v.vendedor " +
            "where v.nombrecliente=?1 and year(v.fecha)=?2 and u.sede=?3 AND v.confirmado = 1",nativeQuery = true)
    List<ReportesClienteDto> obtenerReporteSedeAnualCliente(String cliente,int anho, int idsede);

    @Query(value = "SELECT v.numerodocumento ,inv.codigo_inventario,p.nombre as producto,month(v.fecha) as mes,dayofmonth(v.fecha) as dia,v.cantidad,v.precio_venta as precioventa " +
            "FROM Ventas v " +
            "inner join Inventario inv on inv.codigo_inventario=v.productoinventario " +
            "inner join Productos p on p.codigonom =inv.producto and p.linea =inv.linea " +
            "inner join Usuarios u on u.dni=v.vendedor " +
            "where v.nombrecliente=?1 and year(v.fecha)=?2 and u.sede=?3 and quarter(v.fecha)=?4 AND v.confirmado = 1",nativeQuery = true)
    List<ReportesClienteDto> obtenerReporteSedeTrimestralCliente(String cliente, int anho, int idsede,int trimestre);

    @Query(value = "SELECT v.numerodocumento ,inv.codigo_inventario,p.nombre as producto,month(v.fecha) as mes,dayofmonth(v.fecha) as dia,v.cantidad,v.precio_venta as precioventa " +
            "FROM Ventas v " +
            "inner join Inventario inv on inv.codigo_inventario=v.productoinventario " +
            "inner join Productos p on p.codigonom =inv.producto and p.linea =inv.linea " +
            "inner join Usuarios u on u.dni=v.vendedor " +
            "where v.nombrecliente=?1 and year(v.fecha)=?2 and u.sede=?3 and month(v.fecha)=?4 AND v.confirmado = 1",nativeQuery = true)
    List<ReportesClienteDto> obtenerReporteSedeMensualCliente(String cliente, int anho, int idsede,int mes);

    // FIN CLIENTES

    //ARTICULOS FER

    @Query(value = "SELECT v.nombrecliente, v.ruc_dni as rucdni, v.productoinventario,month(v.fecha) as mes, dayofmonth(v.fecha) as dia ,v.cantidad,v.precio_venta as precioventa " +
            "FROM Ventas v " +
            "inner join Inventario inv on inv.codigo_inventario= v.productoinventario " +
            "inner join Productos p on (p.codigonom=inv.producto and p.linea=inv.linea) " +
            "inner join Usuarios u on u.dni=v.vendedor " +
            "inner join Sede s on s.idsede=u.sede "+
            "where  year(v.fecha)= ?1 and s.idsede=?2 and v.confirmado = 1 and (p.codigonom= ?3 and p.linea=?4) ",nativeQuery = true)
    List<ReportesArticuloDto> obtenerReporteSedeAnualArticuloProducto(int anho, int idsede,String codiPro,String linea);

    @Query(value = "SELECT v.nombrecliente, v.ruc_dni as rucdni, v.productoinventario,month(v.fecha) as mes, dayofmonth(v.fecha) as dia ,v.cantidad,v.precio_venta as precioventa " +
            "FROM Ventas v " +
            "inner join Inventario inv on inv.codigo_inventario= v.productoinventario " +
            "inner join Productos p on (p.codigonom=inv.producto and p.linea=inv.linea) " +
            "inner join Usuarios u on u.dni=v.vendedor " +
            "inner join Sede s on s.idsede=u.sede "+
            "where  year(v.fecha)= ?1 and s.idsede=?2 and v.confirmado = 1 and (p.codigonom= ?3 and p.linea=?4) and quarter(v.fecha)=?5 ",nativeQuery = true)
    List<ReportesArticuloDto> obtenerReporteSedeTrimestralArticuloProducto( int anho, int idsede,String codPro,String linea,int trimestre);

    @Query(value = "SELECT v.nombrecliente, v.ruc_dni as rucdni, v.productoinventario,month(v.fecha) as mes, dayofmonth(v.fecha) as dia ,v.cantidad,v.precio_venta as precioventa " +
            "FROM Ventas v " +
            "inner join Inventario inv on inv.codigo_inventario= v.productoinventario " +
            "inner join Productos p on (p.codigonom=inv.producto and p.linea=inv.linea) " +
            "inner join Usuarios u on u.dni=v.vendedor " +
            "inner join Sede s on s.idsede=u.sede "+
            "where  year(v.fecha)= ?1 and s.idsede=?2 and v.confirmado = 1 and (p.codigonom= ?3 and p.linea=?4) and month(v.fecha)=?5 ",nativeQuery = true)
    List<ReportesArticuloDto> obtenerReporteSedeMensualArticuloProducto(int anho, int idsede, String proCod,String linea,int mes);




}