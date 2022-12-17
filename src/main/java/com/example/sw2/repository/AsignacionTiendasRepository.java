package com.example.sw2.repository;


import com.example.sw2.constantes.AsignadosSedesId;
import com.example.sw2.dto.DatosAsignadosTiendaDto;
import com.example.sw2.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import javax.transaction.Transactional;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AsignacionTiendasRepository extends JpaRepository<AsignacionTiendas,Integer> {

    Optional<AsignacionTiendas> findByAsignadosSedes_Id_SedeAndAsignadosSedes_Id_Productoinventario_CodigoinventarioAndTienda_Ruc(Sede asignadosSedes_id_sede, String asignadosSedes_id_productoinventario_codigoinventario,  String tienda_ruc);

    @Query(value="select p.nombre as nombreproducto, a.precioventa as precioventa," +
            "att.stock as stockasignadotienda, att.fecha_asignacion as fechaasignacionproducto, t.nombre as nombretienda,"+
            "i.tamanho as tamanhoproducto, i.color as colorproducto, att.idtiendas as idasignados\n"+
            "FROM Productos p\n" +
            "inner join Inventario i on (p.codigonom = i.producto)\n" +
            "inner join Asignados_sedes a on (i.codigo_inventario = a.producto_inventario)"+
            "inner join Asignacion_tiendas att on (a.idAsignados = att.productoasignado)"+
            "inner join Tienda t on (att.tienda =  t.idtienda)"+
            "group by att.tienda",
            nativeQuery = true)
    List<DatosAsignadosTiendaDto> obtenerDatosAsignados();

    @Query(value="SELECT ast.*\n" +
            "FROM Asignacion_tienda ast\n" +
            "WHERE sede=?1",nativeQuery=true)

    List<AsignacionTiendas> buscarPorSede(int sede);

    List<AsignacionTiendas> findAsignacionTiendasByStockGreaterThanAndAsignadosSedes_Id_Sede(int a, Sede sede);
    List<AsignacionTiendas> findAsignacionTiendasByTiendaAndAsignadosSedes(Tienda tienda,AsignadosSedes as);

    Optional<AsignacionTiendas> findByTienda_IdtiendaAndAsignadosSedes_Id_Precioventa(int tienda, Float precio);

    @Procedure(name = "tienda_registra")
    void tienda_registra(int sede, String codigo,
                                int estado, BigDecimal precio, int cant, int aTienda);

    @Procedure(name = "tienda_devolucion")
    void tienda_devolucion(int idSede, String codigo,
                      int estado, Float precio, int cant, int aTienda);

    @Procedure(name = "borrar_venta_tienda")
    void borrar_venta_tienda(int dni_sede, String codigo,
                         int estado, Float precio, int cant, int aTienda);

    @Procedure(name = "borrar_venta_as")
    void borrar_venta_as(int dni_sede, String codigo,
                             int estado, Float precio, int cant);

    @Query(value = "SELECT SUM(stock) FROM Asignacion_tienda WHERE sede = ?1", nativeQuery = true)
    String cantProductosEnTienda(int sede);

}
