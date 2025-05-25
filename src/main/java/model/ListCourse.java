/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;

/**
 *
 * @author mauricioteranlimari
 */
public class ListCourse {
    
    private String NameStudent;
    private String cedula_identidad;
    private String NameMateria;
    private BigDecimal nota;

    public String getNameStudent() {
        return NameStudent;
    }

    public void setNameStudent(String NameStudent) {
        this.NameStudent = NameStudent;
    }

    public String getCedula_identidad() {
        return cedula_identidad;
    }

    public void setCedula_identidad(String cedula_identidad) {
        this.cedula_identidad = cedula_identidad;
    }

     

    public String getNameMateria() {
        return NameMateria;
    }

    public void setNameMateria(String NameMateria) {
        this.NameMateria = NameMateria;
    }

    public BigDecimal getNota() {
        return nota;
    }

    public void setNota(BigDecimal nota) {
        this.nota = nota;
    }
    
}
