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
import model.Database;
import model.Student_Guardian;

/**
 *
 * @author mauricioteranlimari
 */
public class Student_GuardianDao {
    
    private Database connection;

    public Student_GuardianDao() throws ClassNotFoundException, SQLException {
        this.connection = new Database();
    }

    // Registrar
    public boolean register(int idTutor,int idEstudiante, String relacion) {
        String SQL = "INSERT INTO estudiante_tutor (id_estudiante, id_tutor, relacion) VALUES (?, ?, ?)";

        try (Connection conn = connection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL)) {

            ps.setInt(1, idEstudiante);
            ps.setInt(2, idTutor);
            ps.setString(3, relacion);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al registrar estudiante-tutor");
            e.printStackTrace();
            return false;
        }
    }

    // Listar todas las relaciones
    public List<Student_Guardian> toList() {
        List<Student_Guardian> lista = new ArrayList<>();
        String SQL = "SELECT * FROM estudiante_tutor";

        try (Connection conn = connection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Student_Guardian et = new Student_Guardian();
                et.setId_estudiante(rs.getInt("id_estudiante"));
                et.setId_tutor(rs.getInt("id_tutor"));
                et.setRelacion(rs.getString("relacion"));
                lista.add(et);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener lista de relaciones");
            e.printStackTrace();
        }

        return lista;
    }
    public List<Student_Guardian> toListByIdStudent(int idStudent) {
        List<Student_Guardian> lista = new ArrayList<>();
        String SQL = "SELECT * FROM estudiante_tutor WHERE id_estudiante = ? ";

        try (Connection conn = connection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL)) {
            ps.setInt(1, idStudent);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Student_Guardian et = new Student_Guardian();
                et.setId_estudiante(rs.getInt("id_estudiante"));
                et.setId_tutor(rs.getInt("id_tutor"));
                et.setRelacion(rs.getString("relacion"));
                lista.add(et);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener lista de relaciones");
            e.printStackTrace();
        }

        return lista;
    }


    // Modificar relación (por ejemplo, cambiar la relación del tutor con el estudiante)
    public boolean edit(int idEstudiante, int idTutor, String nuevaRelacion) {
        String SQL = "UPDATE estudiante_tutor SET relacion = ? WHERE id_estudiante = ? AND id_tutor = ?";

        try (Connection conn = connection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL)) {

            ps.setString(1, nuevaRelacion);
            ps.setInt(2, idEstudiante);
            ps.setInt(3, idTutor);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al editar la relación estudiante-tutor");
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteRelationsByStudentId(int idEstudiante) {
        String SQL = "DELETE FROM estudiante_tutor WHERE id_estudiante = ?";

        try (Connection conn = connection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL)) {

            ps.setInt(1, idEstudiante);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al eliminar relaciones de estudiante con ID: " + idEstudiante);
            e.printStackTrace();
            return false;
        }
    }
    
}
