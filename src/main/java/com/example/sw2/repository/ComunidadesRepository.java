package com.example.sw2.repository;

import com.example.sw2.entity.Comunidades;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComunidadesRepository extends JpaRepository<Comunidades,String> {

    Optional<Comunidades> findComunidadesByNombre(String nombre);

    @Query(value = "SELECT distinct com.* FROM Ventas v " +
            "inner join Inventario inv on inv.codigo_inventario=v.productoinventario " +
            "inner join Comunidades com on com.codigo=inv.comunidad where v.confirmado=1",nativeQuery = true)
    List<Comunidades> comunidadesConVentas();

    @Query(value = "SELECT distinct co.* FROM Ventas v " +
            "inner join Usuarios u on u.dni=v.vendedor " +
            "inner join Sede s on s.idsede=u.sede " +
            "inner join Inventario inv on inv.codigo_inventario=v.productoinventario " +
            "inner join Comunidades co on co.codigo=inv.comunidad " +
            "where s.idsede=?1  and v.confirmado =1",nativeQuery = true)
    List<Comunidades> comunidadesConVentasEnSede(int idsede);
}

