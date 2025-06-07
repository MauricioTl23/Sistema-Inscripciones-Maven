/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import controller.ManageUsersController;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Database;

/**
 *
 * @author mauricioteranlimari
 */
public class ReportsDao {

    private final Database ReportConnection;
    private ManageUsersController verify;

    private final String[] levels = {"Primaria", "Secundaria"};
    private final String[] grades = {"Primero", "Segundo", "Tercer", "Cuarto", "Quinto", "Sexto"};

    public ReportsDao() throws ClassNotFoundException, SQLException {
        this.ReportConnection = new Database();
        this.verify = new ManageUsersController();
    }

    public List<Map<String, Object>> ReportOne(int level) {

        List<Map<String, Object>> lista = new ArrayList<>();

        try {
            String SQL = "SELECT c.idcurso,c.nivel,c.grado,c.paralelo,GROUP_CONCAT(DISTINCT CONCAT(u.nombre, ' ', u.apellido) SEPARATOR ' y ') AS asesores,COUNT(DISTINCT i.id_estudiante) AS total_estudiantes "
                    + "FROM curso c "
                    + "JOIN inscripcion i ON i.id_curso = c.idcurso "
                    + "JOIN asesor a ON a.idcurso = c.idcurso "
                    + "JOIN usuario u ON a.idusuario = u.idusuario "
                    + "WHERE c.nivel = ? "
                    + "GROUP BY c.idcurso, c.nivel, c.grado, c.paralelo "
                    + "ORDER BY c.grado, c.paralelo ASC";

            Connection connection = this.ReportConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            sentence.setInt(1, level);

            ResultSet data = sentence.executeQuery();

            while (data.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("Id", data.getInt("idcurso"));
                fila.put("Level", levels[data.getInt("nivel")]);
                fila.put("Grade", grades[data.getInt("grado")]);
                fila.put("Parallel", data.getString("paralelo"));
                fila.put("Advisor", data.getString("asesores"));
                fila.put("NumberOfStudents", data.getInt("total_estudiantes"));
                lista.add(fila);
            }

            data.close();
            sentence.close();
            connection.close();

        } catch (SQLException e) {
            System.err.println("Ocurrio un error al listar Reporte1");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();
        }
        return lista;
    }

    public List<Map<String, Object>> ReportTwo() {

        List<Map<String, Object>> lista = new ArrayList<>();

        try {
            String SQL = "SELECT CONCAT(u.nombre,' ',u.apellido) AS usuario, i.gestion,CONCAT(e.nombre,' ',e.apellido) AS estudiante,c.nivel,c.grado,c.paralelo "
                    + "FROM inscripcion i "
                    + "JOIN usuario u ON i.id_usuario = u.idusuario "
                    + "JOIN estudiante e ON i.id_estudiante = e.idestudiante "
                    + "JOIN curso c ON i.id_curso = c.idcurso";

            Connection connection = this.ReportConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            ResultSet data = sentence.executeQuery();

            while (data.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("NameUser", data.getString("usuario"));
                fila.put("Year", data.getInt("gestion"));
                fila.put("Student",data.getString("estudiante"));
                fila.put("Course", grades[data.getInt("grado")]+" "+data.getString("Paralelo")+" ("+levels[data.getInt("nivel")]+")");
                lista.add(fila);
            }

            data.close();
            sentence.close();
            connection.close();

        } catch (SQLException e) {
            System.err.println("Ocurrio un error al listar Reporte2");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();
        }
        return lista;
    }

}
