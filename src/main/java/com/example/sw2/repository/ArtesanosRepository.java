package com.example.sw2.repository;

import com.example.sw2.entity.Artesanos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtesanosRepository extends JpaRepository<Artesanos,String> {

    List<Artesanos> findArtesanosByComunidades_Codigo(String comunidad);
}
