package com.example.sw2.repository;


import com.example.sw2.entity.AsignadosSedes;
import com.example.sw2.entity.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario,String> {
    List<Inventario> findAllByOrderByFechamodificacionDesc();
    List<Inventario> findAllByOrderByNumpedidoDesc();
    List<Inventario> findInventariosByArtesanos_Codigo(String codigo);
    Inventario findByCodigoinventario(String cod);
    Optional<Inventario> findInventariosByNumpedido(int num);

    @Query(value = "select inv.* FROM Inventario inv " +
            "inner join Productos p on inv.producto = p.codigonom and inv.linea = p.linea " +
            "where inv.producto = ?1 and inv.linea = ?2",nativeQuery = true)
    List<Inventario> listProductosComprados(String codigonom,String linea);

    @Query(value = "Select * from Inventario where devuelto_artesano = 1",nativeQuery = true)
    List<Inventario> listaDevueltosAlArtesano();

    @Query(value = "select * from Inventario i where i.fecha_vencimiento_consignacion is not null and month(i.fecha_vencimiento_consignacion)=?1", nativeQuery = true)
    List<Inventario> findInvPorFechasDeVencimiento(int m);

    //List<Inventario> findInventariosByFechavencimientoconsignacion_Month(int i);
    @Query(value = "select * from Inventario i where i.fecha_vencimiento_consignacion is not null and \n" +
            "(month(i.fecha_vencimiento_consignacion) = ?1 and day(i.fecha_vencimiento_consignacion) >?3 and year(i.fecha_vencimiento_consignacion)=?4) " +
            "or  month(i.fecha_vencimiento_consignacion) = ?2 and year(i.fecha_vencimiento_consignacion)=?4",nativeQuery = true)
    List<Inventario> findProximoAVencer(int mes, int mes_sgte, int hoy_dia,int ano);
    @Query(value = "select * from Inventario i where i.fecha_vencimiento_consignacion is not null and \n" +
            " (month(i.fecha_vencimiento_consignacion) = 12 and year(i.fecha_vencimiento_consignacion)=?1 and day(i.fecha_vencimiento_consignacion) >1 )or \n" +
            "        ( month(i.fecha_vencimiento_consignacion) = 1 and year(i.fecha_vencimiento_consignacion)=?2 )  \n" +
            "          ",nativeQuery = true)
    List<Inventario> findVencenFinDeAno(int este_ano, int ano_sgte);

    @Query(value = "select * from Inventario i where i.fecha_vencimiento_consignacion is not null and " +
            "(month(i.fecha_vencimiento_consignacion) = ?1  and day(i.fecha_vencimiento_consignacion) <=?2 and year(i.fecha_vencimiento_consignacion)=?3) " +
            "or month(i.fecha_vencimiento_consignacion) < ?1 and year(i.fecha_vencimiento_consignacion)<=?3",nativeQuery = true)
    List<Inventario> findProductosVencidos(int mes, int dia, int este_ano);

    Optional<Inventario> findInventarioByCodigoinventarioAndCantidadgestorIsGreaterThan(String cod, int num);

    @Query(value = "SELECT COUNT(codigo_inventario) FROM Inventario", nativeQuery = true)
    String cantInventario();

    @Query(value = "SELECT SUM(cantidad_total) FROM Inventario", nativeQuery = true)
    String stockTotal();

    @Query(value = "SELECT SUM(cantidad_gestor) FROM Inventario", nativeQuery = true)
    String stockGestor();

    @Query(value = "SELECT COUNT(codigo_inventario) FROM Inventario WHERE cantidad_gestor>0", nativeQuery = true)
    String cantPoductosGestor();
}
