/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;

/**
 *
 * @author mauricioteranlimari
 */
public class Course {
    
    private int idcurso;
    private int nivel;
    private int grado;
    private char paralelo;
    private int cupo_max;
    private boolean admite_nuevos;
    private String asesor;
    private LocalDate fechai;
    private LocalDate fechaf;
    
     public Course() {
    }

    public int getIdcurso() {
        return idcurso;
    }

    public void setIdcurso(int idcurso) {
        this.idcurso = idcurso;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public int getGrado() {
        return grado;
    }

    public void setGrado(int grado) {
        this.grado = grado;
    }

    public char getParalelo() {
        return paralelo;
    }

    public void setParalelo(char paralelo) {
        this.paralelo = paralelo;
    }

    public int getCupo_max() {
        return cupo_max;
    }

    public void setCupo_max(int cupo_max) {
        this.cupo_max = cupo_max;
    }

    public boolean getAdmite_nuevos() {
        return admite_nuevos;
    }

    public void setAdmite_nuevos(boolean admite_nuevos) {
        this.admite_nuevos = admite_nuevos;
    }
    public String getAsesor() {
        return asesor;
    }

    public void setAsesor(String asesor) {
        this.asesor = asesor;
    }

    public LocalDate getFechai() {
        return fechai;
    }

    public void setFechai(LocalDate fechai) {
        this.fechai = fechai;
    }

    public LocalDate getFechaf() {
        return fechaf;
    }

    public void setFechaf(LocalDate fechaf) {
        this.fechaf = fechaf;
    }
    
}
