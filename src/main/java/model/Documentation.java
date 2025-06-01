/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
public class Documentation {
    
    private int idtipo_documento;
    private String nombre;
    private boolean obligatorio;
    private boolean cartacompromiso;

    public Documentation() {
    }
    

    public int getIdtipo_documento() {
        return idtipo_documento;
    }

    public void setIdtipo_documento(int idtipo_documento) {
        this.idtipo_documento = idtipo_documento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isObligatorio() {
        return obligatorio;
    }

    public void setObligatorio(boolean obligatorio) {
        this.obligatorio = obligatorio;
    }

    public boolean isCartacompromiso() {
        return cartacompromiso;
    }

    public void setCartacompromiso(boolean cartacompromiso) {
        this.cartacompromiso = cartacompromiso;
    }

}
