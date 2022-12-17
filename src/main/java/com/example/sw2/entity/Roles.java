package com.example.sw2.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Roles")
public class  Roles implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idroles;
    @Column(nullable = false)
    private String nombrerol;

    public Roles(){

    }

    public Roles(int idroles){
        this.idroles = idroles;
        String x = "";
        switch (idroles){
            case 1:
                x = "admin";
                break;
            case 2:
                x = "gestor";
                break;
            case 3:
                x = "sede";
                break;
        }
        this.nombrerol = x;
    }

    public int getIdroles() {
        return idroles;
    }

    public void setIdroles(int idroles) {
        this.idroles = idroles;
    }

    public String getNombrerol() {
        return nombrerol;
    }

    public void setNombrerol(String nombrerol) {
        this.nombrerol = nombrerol;
    }
}
