/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.Advisor;
import model.Course;
import model.Database;

/**
 *
 * @author mauricioteranlimari
 */
public class AdvisorDao {

    private Database AdvisorConnection;

    public AdvisorDao() throws ClassNotFoundException, SQLException {
        this.AdvisorConnection = new Database();
    }

    public boolean register(Advisor advisor) {
        try {

            String SQL = "INSERT INTO asesor(idusuario, idcurso, fecha_inicio,fecha_fin) VALUES (?, ?, ?, ?)";

            Connection connection = this.AdvisorConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            sentence.setInt(1, advisor.getIdusuario());
            sentence.setInt(2, advisor.getIdcurso());
            sentence.setDate(3, java.sql.Date.valueOf(advisor.getFechainicio()));
            sentence.setDate(4, java.sql.Date.valueOf(advisor.getFechafin()));

            sentence.executeUpdate();
            sentence.close();

            return true;

        } catch (Exception e) {
            System.err.println("Ocurrio un error al registrar asesor");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();

            return false;
        }
    }

    public boolean delete(String fullname, int idcurso) {
        try {

            String SQL = "DELETE FROM asesor WHERE idusuario = ? AND idcurso = ?";

            Connection connection = this.AdvisorConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            UserDao userdao = new UserDao();

            int id = userdao.idasesor(fullname);

            sentence.setInt(1, id);

            sentence.setInt(2, idcurso);

            sentence.executeUpdate();

            sentence.close();

            return true;

        } catch (Exception e) {

            System.err.println("Ocurrio un error al editar el curso");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();

            return false;
        }
    }

    public boolean Edit(int idasesorn, LocalDate fechafin, int idcurso) {

        try {

            String SQL = "UPDATE asesor SET idusuario = ?, fecha_fin = ?"
                    + " WHERE idcurso = ?";

            Connection connection = this.AdvisorConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            sentence.setInt(1, idasesorn);
            sentence.setDate(2, java.sql.Date.valueOf(fechafin));

            sentence.setInt(3, idcurso);

            sentence.executeUpdate();

            sentence.close();

            return true;

        } catch (Exception e) {
            System.err.println("Ocurrio un error al editar usuario");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();

            return false;
        }
    }

    public List<Course> search(String fullname) {
        List<Course> result = new ArrayList<>();

        try {
            String SQL = "SELECT c.grado, c.paralelo,c.nivel FROM curso c"
                    + " JOIN asesor a ON c.idcurso = a.idcurso"
                    + " JOIN usuario u ON a.idusuario = u.idusuario "
                    + " WHERE CONCAT(u.nombre,' ',u.apellido) = ?"
                    + " AND u.cargo = 2";

            Connection connection = this.AdvisorConnection.getConnection();
            PreparedStatement sentence = connection.prepareStatement(SQL);

            sentence.setString(1, fullname);

            ResultSet data = sentence.executeQuery();

            while (data.next() == true) {

                Course course = new Course();

                course.setGrado(data.getInt(1));
                course.setParalelo(data.getString(2).charAt(0));
                course.setNivel(data.getInt(3));

                result.add(course);
            }
            data.close();
            sentence.close();

        } catch (Exception e) {
            System.err.println("Ocurrio un error al listar usuarios");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();

        }
        return result;
    }

}
