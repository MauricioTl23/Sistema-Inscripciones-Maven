/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Alert;
import model.Database;
import model.Enrollment;
import model.Extras;

public class EnrollementDao {
    
    private Database dbConnection;

    public EnrollementDao() throws ClassNotFoundException, SQLException {
        this.dbConnection = new Database();
    }
    
    public int register(Enrollment inscripcion) {
        try {
            String sql = "INSERT INTO inscripcion (id_estudiante, id_curso, id_usuario, fecha_inscripcion, gestion, estado, rude, observacion) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            Connection connection = dbConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setInt(1, inscripcion.getId_estudiante());
            stmt.setInt(2, inscripcion.getId_curso());
            stmt.setInt(3, inscripcion.getId_usuario());
            stmt.setDate(4, new java.sql.Date(inscripcion.getFecha_inscripcion().getTime()));
            stmt.setInt(5, inscripcion.getYear());
            stmt.setInt(6, inscripcion.getEstado());
            stmt.setInt(7, inscripcion.getRude());
            stmt.setString(8, inscripcion.getObservacion());

            int affectedRows = stmt.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            return generatedKeys.getInt(1); // Retorna el ID generado
                        }
                    }
                }

                return -1;

        } catch (SQLException e) {
            Extras.showAlert("Error", "Ocurrió un error al registrar la inscripción.\n" + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
            return -1;
        }
    }

    public List<Enrollment> toList() {
        List<Enrollment> lista = new ArrayList<>();

        try {
            String sql = "SELECT * FROM inscripcion";
            Connection connection = dbConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Enrollment i = new Enrollment();

                i.setIdinscripcion(rs.getInt("idinscripcion"));
                i.setId_estudiante(rs.getInt("id_estudiante"));
                i.setId_curso(rs.getInt("id_curso"));
                i.setId_usuario(rs.getInt("id_usuario"));
                i.setFecha_inscripcion(rs.getDate("fecha_inscripcion"));
                i.setYear(rs.getInt("year"));
                i.setEstado(rs.getInt("estado"));
                i.setRude(rs.getInt("rude"));
                i.setObservacion(rs.getString("observacion"));

                lista.add(i);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            Extras.showAlert("Error", "Ocurrió un error al listar inscripciones.\n" + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }

        return lista;
    }
    
    public int getByIdStudent(int idStudent) {
        int idInscripcion = -1;
        try {
            String sql = "SELECT * FROM inscripcion WHERE id_estudiante = ? ORDER BY fecha_inscripcion DESC LIMIT 1";
            Connection connection = dbConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, idStudent); // Falta este set

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                idInscripcion = rs.getInt("idinscripcion");
            }

            rs.close();
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return idInscripcion;
    }


    public boolean edit(Enrollment i) {
        try {
            String sql = "UPDATE inscripcion SET id_estudiante = ?, id_curso = ?, id_usuario = ?, fecha_inscripcion = ?, year = ?, estado = ?, rude = ?, observacion = ? "
                       + "WHERE idinscripcion = ?";

            Connection connection = dbConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setInt(1, i.getId_estudiante());
            stmt.setInt(2, i.getId_curso());
            stmt.setInt(3, i.getId_usuario());
            stmt.setDate(4, new java.sql.Date(i.getFecha_inscripcion().getTime()));
            stmt.setInt(5, i.getYear());
            stmt.setInt(6, i.getEstado());
            stmt.setInt(7, i.getRude());
            stmt.setString(8, i.getObservacion());
            stmt.setInt(9, i.getIdinscripcion());

            stmt.executeUpdate();
            stmt.close();

            return true;

        } catch (SQLException e) {
            Extras.showAlert("Error", "Ocurrió un error al editar la inscripción.\n" + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        try {
            String sql = "DELETE FROM inscripcion WHERE idinscripcion = ?";
            Connection connection = dbConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setInt(1, id);
            stmt.executeUpdate();
            stmt.close();

            return true;

        } catch (SQLException e) {
            Extras.showAlert("Error", "Ocurrió un error al eliminar la inscripción.\n" + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
            return false;
        }
    }

    
}
