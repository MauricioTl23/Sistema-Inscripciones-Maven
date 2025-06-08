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
import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import model.Database;

/**
 *
 * @author mauricioteranlimari
 */
public class StatisticsDao {

    private final Database StatisticsConnection;

    public StatisticsDao() throws ClassNotFoundException, SQLException {
        this.StatisticsConnection = new Database();
        ManageUsersController manageUsersController = new ManageUsersController();
    }

    public Map<String, Map<String, Integer>> ApprovedvsFailed(int level, int year) {

        Map<String, Map<String, Integer>> resultados = new HashMap<>();

        try {
            String SQL = "SELECT "
                    + "    datos.curso,"
                    + "    datos.estado,"
                    + "    COUNT(*) AS cantidad"
                    + " FROM ("
                    + "    SELECT "
                    + "        CONCAT(c.grado + 1, '° ', c.paralelo) AS curso,"
                    + "        i.id_estudiante,"
                    + "        CASE "
                    + "            WHEN MIN(n.nota) >= 51 THEN 'Aprobado'"
                    + "            ELSE 'Reprobado'"
                    + "        END AS estado "
                    + "    FROM inscripcion i "
                    + "    JOIN nota n ON i.idinscripcion = n.id_inscripcion "
                    + "    JOIN curso c ON i.id_curso = c.idcurso "
                    + "    WHERE c.nivel = ? AND i.gestion = ? "
                    + "    GROUP BY i.id_estudiante, curso "
                    + ") AS datos "
                    + "GROUP BY datos.curso, datos.estado";

            Connection connection = this.StatisticsConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            sentence.setInt(1, level);
            sentence.setInt(2, year);

            ResultSet data = sentence.executeQuery();

            while (data.next()) {
                String curso = data.getString("curso");
                String estado = data.getString("estado");
                int cantidad = data.getInt("cantidad");

                resultados.computeIfAbsent(curso, k -> new HashMap<String, Integer>()).put(estado, cantidad);

            }

        } catch (SQLException e) {

            System.err.println("Ocurrio un error al listar Aprobados y reprobados");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();

        }

        return resultados;
    }

    public Map<String, Integer> RegistrationByYear(int level) {

        Map<String, Integer> resultados = new HashMap<>();

        try {
            String SQL = "SELECT i.gestion,COUNT(i.id_estudiante) AS inscritos "
                    + "FROM inscripcion i "
                    + "JOIN curso c ON i.id_curso = c.idcurso "
                    + "WHERE c.nivel = ? "
                    + "GROUP BY i.gestion;";

            Connection connection = this.StatisticsConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            sentence.setInt(1, level);

            ResultSet data = sentence.executeQuery();

            while (data.next()) {
                String gestion = data.getString("gestion");
                int inscritos = data.getInt("inscritos");
                resultados.put(gestion, inscritos);
            }
        } catch (SQLException e) {

            System.err.println("Ocurrio un error al listar inscritos por año");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();

        }
        return resultados;
    }

    public Map<String, Integer> DistribucionodFaildAndApproved(int level, int year) {

        Map<String, Integer> resultados = new HashMap<>();
        try {
            String SQL = "SELECT "
                    + "CASE "
                    + "WHEN MIN(n.nota) >= 51 THEN 'Aprobado' "
                    + "ELSE 'Reprobado' "
                    + "END AS estado,"
                    + "COUNT(*) AS cantidad "
                    + "FROM inscripcion i "
                    + "JOIN nota n ON i.idinscripcion = n.id_inscripcion "
                    + "JOIN curso c ON i.id_curso = c.idcurso "
                    + "WHERE c.nivel = ? AND i.gestion = ? "
                    + "GROUP BY i.id_estudiante, estado";

            Connection connection = this.StatisticsConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            sentence.setInt(1, level);
            sentence.setInt(2, year);

            ResultSet data = sentence.executeQuery();

            while (data.next()) {
                String estado = data.getString("estado");
                int cantidad = data.getInt("cantidad");

                resultados.merge(estado, cantidad, Integer::sum);
            }

        } catch (SQLException e) {

            System.err.println("Ocurrio un error al listar Aprobados y reprobados");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();

        }

        return resultados;
    }

    public ObservableList<XYChart.Data<Number, Number>> AgevsAVG(int level, int year) {

        ObservableList<XYChart.Data<Number, Number>> resultados = FXCollections.observableArrayList();
        try {
            String SQL = "SELECT "
                    + "TIMESTAMPDIFF(YEAR, e.fecha_nacimiento, CURDATE()) AS edad,"
                    + "ROUND(AVG(n.nota), 2) AS promedio "
                    + "FROM estudiante e "
                    + "JOIN inscripcion i ON e.idestudiante = i.id_estudiante "
                    + "JOIN nota n ON i.idinscripcion = n.id_inscripcion "
                    + "JOIN curso c ON i.id_curso = c.idcurso "
                    + "WHERE i.gestion = ? AND c.nivel = ? "
                    + "GROUP BY e.idestudiante";

            Connection connection = this.StatisticsConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            sentence.setInt(1, year);
            sentence.setInt(2, level);

            ResultSet data = sentence.executeQuery();

            while (data.next()) {
                int edad = data.getInt("edad");
                double promedio = data.getDouble("promedio");

                resultados.add(new XYChart.Data<>(edad, promedio));
            }

        } catch (SQLException e) {

            System.err.println("Ocurrio un error al listar Aprobados y reprobados");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();

        }

        return resultados;
    }

}
