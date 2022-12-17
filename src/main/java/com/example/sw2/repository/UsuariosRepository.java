package com.example.sw2.repository;

import com.example.sw2.entity.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuariosRepository extends JpaRepository<Usuarios,Integer> {
    /*
    @Modifying
    @Query("update Usuarios u set u.token=null where u.idusuarios=:id")
    void setNullToken(@Param("id") Integer id);*/

    Usuarios findByCorreo(String correo);

    Usuarios findByCorreoAndIdusuariosNot(String correo, int idusuarios);

    Usuarios findByIdusuarios(int dni);

    List<Usuarios> findUsuariosByRoles_Nombrerol(String rol);

    @Query(value = "SELECT * FROM Usuarios WHERE rol=3;",nativeQuery = true)
    List<Usuarios> buscarSedes();
    List<Usuarios> findUsuariosByRoles_idroles(int rol);

    Optional<Usuarios> findUsuariosByRoles_idrolesAndIdusuarios(int rol, int idUsuario);

    Optional<Usuarios> findByCorreoAndCuentaactivada( String correo, Boolean cuentaactivada);



    @Procedure(procedureName = "delete_user")
    void delete_user(int idusuario);

    @Procedure(procedureName = "actualizar_password")
    void actualizar_password(String token, String pass, int id);

    //@Query(value = "SELECT * FROM mosqoy2.Ventas v WHERE v.vendedor=?1;",nativeQuery = true)
    //List<Ventas> buscarSedesenVentas(int idUsuario);

    //@Query(value = "SELECT * FROM mosqoy2.Asignados_sedes asigse WHERE asigse.gestor=?1;",nativeQuery = true)
    //List<AsignadosSedes> (int idUsuario);

    List<Usuarios> findUsuariosByRoles_IdrolesOrderByApellido(int rol);

    Usuarios findByToken(String token);




    @Query(value = "SELECT u.* FROM Usuarios u WHERE rol = 1 LIMIT 1", nativeQuery = true)
    Usuarios obtenerAdmin();

    @Query(value = "SELECT COUNT(dni) FROM Usuarios WHERE rol = ?1", nativeQuery = true)
    String cantUsuarios(int rol);

}
