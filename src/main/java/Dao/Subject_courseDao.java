/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import model.Database;
import model.Subject_course;

/**
 *
 * @author mauricioteranlimari
 */
public class Subject_courseDao {
    
    private Database ScourseConnection;

    public Subject_courseDao() throws ClassNotFoundException, SQLException {
        this.ScourseConnection = new Database();
    }

    public boolean addMaterial(List<Subject_course> subjectcourse) {
        try {

            String SQL = "INSERT INTO materia_curso(id_materia,id_curso) VALUES (? , ?)";

            Connection connection = this.ScourseConnection.getConnection();

            connection.setAutoCommit(false);

            PreparedStatement sentence = connection.prepareStatement(SQL);

            for (Subject_course sc : subjectcourse) {
                sentence.setInt(1, sc.getId_materia());
                sentence.setInt(2, sc.getId_curso());
                sentence.addBatch();
            }

            sentence.executeBatch();      
            connection.commit();
            sentence.close();

            return true;

        } catch (Exception e) {

            System.err.println("Ocurrio un error al registrar curso");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();

            return false;

        }
    }
    
}
