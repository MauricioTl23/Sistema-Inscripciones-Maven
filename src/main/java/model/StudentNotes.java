/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import javafx.beans.property.SimpleStringProperty;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mauricioteranlimari
 */
public class StudentNotes {
    
    private final SimpleStringProperty nombreCompleto;
    private final SimpleStringProperty ci;
    private final Map<String, SimpleStringProperty> notasPorMateria;

    public StudentNotes(String nombreCompleto, String ci) {
        this.nombreCompleto = new SimpleStringProperty(nombreCompleto);
        this.ci = new SimpleStringProperty(ci);
        this.notasPorMateria = new HashMap<>();
    }

    public String getNombreCompleto() {
        return nombreCompleto.get();
    }

    public String getCi() {
        return ci.get();
    }

    public Map<String, SimpleStringProperty> getNotasPorMateria() {
        return notasPorMateria;
    }
    
    public void setNota(String materia, String nota) {
        notasPorMateria.put(materia, new SimpleStringProperty(nota));
    }

    public SimpleStringProperty notaProperty(String materia) {
        return notasPorMateria.getOrDefault(materia, new SimpleStringProperty(""));
    }
    
    public String getNota(String materia) {
    SimpleStringProperty notaProp = notasPorMateria.get(materia);
    return (notaProp == null) ? "" : notaProp.get();
}
    
}
