package com.example.sw2.entity;


import com.example.sw2.config.Auditable;
import com.example.sw2.repository.UsuariosRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import net.bytebuddy.utility.RandomString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Table(name = "Usuarios")
public class Usuarios extends Auditable implements Serializable {

    @Id
    @Min(value = 10000000, message = "El número de DNI debe tener 8 dígitos")
    @Max(value = 99999999,  message = "El número de DNI debe tener 8 dígitos")
    @Column(name = "dni")
    private int idusuarios;
    @Column(nullable = false)
    @NotBlank(message = "Este campo no puede estar vacío")
    @Size(max = 20, message = "Debe contener 20 caracteres como maximo")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ'. ]+$", message = "Ingrese solo caracteres válidos")
    private String nombre;
    @Size(max = 20, message = "Debe contener 20 caracteres como maximo")
    @Column(nullable = false)
    @NotBlank(message = "Este campo no puede estar vacío")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ'. ]+$", message = "Ingrese solo caracteres válidos")
    private String apellido;
    private String foto;
    @Email(message = "Ingrese una dirección de email válida")
    @Column(nullable = false)
    @Size(max = 45, message = "Debe contener 45 caracteres como maximo")
    @NotBlank(message = "Este campo no puede estar vacío")
    private String correo;
    @Column(nullable = false)
    @JsonIgnore
    private String password;
    @Min(value = 900000000, message = "Ingrese un número de telefono válido")
    @Max(value = 999999999,  message = "Ingrese un número de telefono válido")
    private int telefono;
    @Column(name = "token")
    private String token;
    @ManyToOne
    @JoinColumn(name = "rol",nullable = false)
    private Roles roles;
    //@Digits(integer = 4, fraction = 0)
    //@NotBlank
    @Column(nullable = false)
    private Boolean cuentaactivada=true;

    @ManyToOne
    @JoinColumn(name = "sede")
    private Sede sede;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "vendedor")
    private List<Ventas> ventas;


    public Usuarios(){

    }

    public Usuarios(int idusuarios){
        this.idusuarios=idusuarios;
    }

    public BindingResult validateUser(BindingResult bindingResult, int type, UsuariosRepository usuariosRepository){
        if(usuariosRepository.findByCorreoAndIdusuariosNot(this.getCorreo(),this.getIdusuarios())!=null){
            bindingResult.rejectValue("correo", "error.user", "Este correo ya está registrado.");
        }
        if(type==1 && usuariosRepository.findById(this.getIdusuarios()).isPresent()){ //if new
            bindingResult.rejectValue("idusuarios","error.user","Este dni ya está registrado");
        }
        return bindingResult;
    }

    public String generateNewPassword(){
        RandomString rs = new RandomString(8);
        int[] randomNum = {ThreadLocalRandom.current().nextInt(0, 9),ThreadLocalRandom.current().nextInt(0, 9)};
        String newpassword = rs.nextString().toLowerCase()+ String.valueOf(randomNum[0])+ String.valueOf(randomNum[1]) ;
        password = new BCryptPasswordEncoder().
                encode(newpassword);
        return newpassword;
    }

    public boolean validatePassword(){
        if(password!=null){
            int len = password.length();
            if (len>7){
                int count=0;
                for(String i : password.split("//[-&+$?.@]")){
                    if (password.contains(i)) count++;
                }
                if (count>=1){
                    return true;
                }

            }
        }
        return false;
    }

    public Sede getSede() {
        return sede;
    }

    public void setSede(Sede sede) {
        this.sede = sede;
    }

    public List<Ventas> getVentas() {
        return ventas;
    }

    public void setVentas(List<Ventas> ventas) {
        this.ventas = ventas;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre.trim();
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido.trim();
    }

    public Boolean isGestor(){
        return this.roles.getIdroles()==2;
    }

    public Boolean isSede(){
        return this.roles.getIdroles()==3;
    }

    public Boolean isAdmin(){
        return this.roles.getIdroles()==1;
    }


    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = new BCryptPasswordEncoder().encode(password);
    }

    public String getPassword2() {
        return password;
    }

    public void setPassword2(String password) { this.password = password;}


    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public Roles getRoles() {
        return roles;
    }

    public void setRoles(Roles roles) {
        this.roles = roles;
    }

    public Boolean getCuentaactivada() {
        return cuentaactivada;
    }

    public void setCuentaactivada(Boolean cuentaactivada) {
        this.cuentaactivada = cuentaactivada;
    }

    public int getIdusuarios() {
        return idusuarios;
    }

    public void setIdusuarios(int idusuarios) {
        this.idusuarios = idusuarios;
    }

    public String getFullname(){
        return this.nombre + " " + this.apellido;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Usuarios updateFields(Usuarios u2){
        nombre = u2.getNombre();
        apellido = u2.getApellido();
        correo = u2.getCorreo();
        telefono = u2.getTelefono();
        return this;
    }

    public Usuarios updateFields(Usuarios u2, StorageServiceResponse s2){
        nombre = u2.getNombre();
        apellido = u2.getApellido();
        correo = u2.getCorreo();
        telefono = u2.getTelefono();
        if (s2!=null && s2.isSuccess()){
            foto = u2.getFoto();
        }
        return this;
    }

}
