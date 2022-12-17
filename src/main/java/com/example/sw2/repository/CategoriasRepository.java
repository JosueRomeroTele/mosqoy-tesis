package com.example.sw2.repository;


import com.example.sw2.entity.Categorias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriasRepository extends JpaRepository<Categorias,String> {

    Optional<Categorias> findCategoriasByNombre(String nombre);

}
