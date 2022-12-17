package com.example.sw2.repository;


import com.example.sw2.constantes.ProductoId;
import com.example.sw2.entity.Productos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductosRepository extends JpaRepository<Productos, ProductoId> {

    List<Productos> findProductosByIdCodigolinea(String linea);

    @Query(value = "SELECT DISTINCT p.* FROM Ventas v " +
            "inner join Inventario inv on inv.codigo_inventario=v.productoinventario " +
            "inner join Productos p on p.codigonom=inv.producto where v.confirmado=1",nativeQuery = true)
    List<Productos> productoConVentas();

    @Query(value = "SELECT distinct p.* FROM Ventas v " +
            "inner join Usuarios u on u.dni=v.vendedor " +
            "inner join Sede s on s.idsede=u.sede " +
            "inner join Inventario inv on inv.codigo_inventario = v.productoinventario " +
            "inner join Productos p on p.codigonom=inv.producto and p.linea=inv.linea " +
            "where s.idsede =?1 and v.confirmado =1",nativeQuery = true)
    List<Productos> productosConVentasEnSede(int idsede);

    Optional<Productos> findProductosByIdCodigonomAndIdCodigolinea( String codigo, String Linea);


}
