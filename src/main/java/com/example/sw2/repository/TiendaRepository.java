package com.example.sw2.repository;

import com.example.sw2.entity.Sede;
import com.example.sw2.entity.Tienda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TiendaRepository extends JpaRepository<Tienda,Integer> {

    Optional<Tienda> findByNombreAndDireccionAndRuc(String n, String d, String ruc);

    @Query(value = "SELECT COUNT(idtienda) FROM Tienda", nativeQuery = true)
    String cantTiendas();

    @Query(value = "select * from Tienda where sede = ?",nativeQuery = true)
    List<Tienda>  listaTiendaEnSedes(int sede);
}
