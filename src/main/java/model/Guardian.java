/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author mauricioteranlimari
 */
public class Guardian {
    
    private int id;
    private String nombre;
    private String apellido;
    private String cedula_identidad;
    private String correo;
    

    public Guardian() {
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getCedula_identidad() {
        return cedula_identidad;
    }

    public String getCorreo() {
        return correo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setCedula_identidad(String cedula_identidad) {
        this.cedula_identidad = cedula_identidad;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
