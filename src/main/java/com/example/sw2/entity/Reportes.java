package com.example.sw2.entity;


import com.example.sw2.constantes.CustomConstants;
import com.example.sw2.constantes.ProductoId;

import java.time.LocalDate;

public class Reportes {

    private Integer orderBy;
    private Integer year;
    private Integer type;
    private Integer selected;
    private String nombreselect;



    public Reportes(){}

    public Reportes(Integer orderBy, Integer year, Integer type, Integer selected,String nombreselect){
        this.orderBy = orderBy;
        this.year = year;
        this.type = type;
        this.selected = selected;
        this.nombreselect=nombreselect;

    }

    public String createNameGestor(){
        String n = "Reporte ";

        switch (orderBy){
            case 1:
                n+="Total de Ventas";
                break;
            case 2:
                n+="de Ventas por Sedes";
                break;
            case 3:
                n+="de Ventas por Productos";
                break;
            case 4:
                n+="de Ventas por Comunidades";
                break;
            case 5:
                n+="de Ventas por Clientes";
                break;
        }

        switch (type){
            case 1:
                n+=" del año " + year;
                break;
            case 2:
                n+=" del año " + year + " trimestre " + CustomConstants.getTrimestre().get(selected);
                break;
            case 3:
                n+=" del año " + year + " mes " + CustomConstants.getMeses().get(selected);
                break;
        }


        return n+" "+ LocalDate.now().toString();
    }

    public boolean validateGestor(){
        boolean validate = true;
        if (orderBy<1 || orderBy>5) validate=false;
        return validate && validateGeneral();
    }

    public boolean validateSede(){
        boolean validate = true;
        if (orderBy<1 || orderBy>5) validate=false;
        if (orderBy==2) validate=false;
        return validate && validateGeneral();
    }

    private boolean validateGeneral(){
        boolean validate = true;

        if (year<2000 || year>3000) validate=false;
        if (type<1 || type>3)
            validate = false;
        else {
            if (type==2)//Trimestral
            {
                if (selected<1||selected>4) validate=false;
            }
            else if (type==3)//Mensual
            {
                if (selected<1||selected>12) validate=false;
            }
        }

        return  validate;
    }



    public String getNombreselect() {
        return nombreselect;
    }

    public void setNombreselect(String nombreselect) {this.nombreselect = nombreselect;}

    public Integer getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Integer orderBy) {
        this.orderBy = orderBy;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getSelected() {
        return selected;
    }

    public void setSelected(Integer selected) {
        this.selected = selected;
    }


}
