package com.example.sw2.repository;

import com.example.sw2.entity.Sede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SedeRepository extends JpaRepository<Sede,Integer> {

    Optional<Sede> findByNombresede(String name);

    //selecionar Sedes que hicieron ventas
    @Query(value = "SELECT distinct s.* FROM Ventas v inner join Usuarios u on u.dni=v.vendedor inner join Sede s on s.idsede =u.sede where v.confirmado=1",nativeQuery = true)
    List<Sede>  listSedeConVentas();


}
