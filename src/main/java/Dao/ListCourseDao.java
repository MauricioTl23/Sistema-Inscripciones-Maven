/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import controller.ManageGradesController;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import model.Database;
import model.ListCourse;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

/**
 *
 * @author mauricioteranlimari
 */
public class ListCourseDao {

    private final Database ListConnection;

    private ManageGradesController st = new ManageGradesController();

    public ListCourseDao() throws ClassNotFoundException, SQLException {
        this.ListConnection = new Database();
    }

    public List<ListCourse> listnotes(int idcurso, int gestion) throws SQLException {

        List<ListCourse> listaalumnos = new ArrayList<>();

        try {
            String SQL = "SELECT e.nombre,e.apellido,e.cedula_identidad,m.nombre,n.nota "
                    + "FROM estudiante e "
                    + "JOIN inscripcion i ON e.idestudiante = i.id_estudiante "
                    + "JOIN curso c ON i.id_curso = c.idcurso "
                    + "JOIN materia_curso cm ON cm.id_curso = c.idcurso "
                    + "JOIN materia m ON m.idmateria = cm.id_materia "
                    + "LEFT JOIN nota n ON n.id_inscripcion = i.idinscripcion AND n.id_materia = m.idmateria "
                    + "WHERE c.idcurso = ? "
                    + "AND i.gestion = ? "
                    + "ORDER BY e.apellido, e.nombre, m.nombre";

            Connection connection = this.ListConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            sentence.setInt(1, idcurso);
            sentence.setInt(2, gestion);

            ResultSet data = sentence.executeQuery();

            while (data.next()) {

                ListCourse list = new ListCourse();

                list.setNameStudent(data.getString(1) + " " + data.getString(2));
                list.setCedula_identidad(data.getString(3));
                list.setNameMateria(data.getString(4));
                list.setNota(data.getBigDecimal(5));

                listaalumnos.add(list);

            }

            data.close();
            sentence.close();

        } catch (SQLException e) {
            System.err.println("Ocurrio un error al leer lista de estudiantes");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();
        }
        return listaalumnos;
    }

    public int idRegistration(String ci, int idcourse, int gestion) {
        try {
            String SQL = "SELECT i.idinscripcion "
                    + "FROM inscripcion i "
                    + "JOIN estudiante e ON i.id_estudiante = e.idestudiante "
                    + "WHERE e.cedula_identidad = ? AND i.id_curso = ? AND i.gestion = ?";

            Connection connection = this.ListConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            sentence.setString(1, ci);
            sentence.setInt(2, idcourse);
            sentence.setInt(3, gestion);

            ResultSet data = sentence.executeQuery();

            if (data.next()) {
                return data.getInt("idinscripcion");
            }
        } catch (SQLException e) {
            System.err.println("Ocurrio un error al buscar idinscripcion");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();
        }

        return -1;
    }

    public boolean SaveNotes(List<String> listnotes, int idcourse, int gestion) {
        try {
            String SQL = "INSERT INTO nota (id_inscripcion,id_materia,nota) VALUES (?,?,?)";

            Connection connection = this.ListConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            for (String linea : listnotes) {
                String[] partes = linea.split(",");

                if (partes.length < 3) {
                    continue;
                }

                String ci = partes[1].trim();

                int idInscripcion = idRegistration(ci, idcourse, gestion);

                if (idInscripcion == -1) {
                    System.err.println("No se encontró inscripción para CI: " + ci);
                    continue;
                }

                for (int i = 2; i < partes.length; i++) {
                    int idMateria = i - 1;
                    double nota;
                    try {
                        nota = Double.parseDouble(partes[i].trim());
                    } catch (NumberFormatException e) {
                        System.err.println("Nota inválida para CI " + ci + " en posición " + i + ": " + partes[i]);
                        nota = 0;
                    }

                    sentence.setInt(1, idInscripcion);
                    sentence.setInt(2, idMateria);
                    sentence.setDouble(3, nota);
                    sentence.addBatch();
                }
            }

            sentence.executeBatch();
            return true;

        } catch (SQLException e) {
            System.err.println("Ocurrio un error al guardar notas");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();
        }

        return false;
    }

    public boolean NotesExist(int idcourse, int gestion) {
        try {
            String SQL = "SELECT COUNT(n.nota) AS cantidad_notas "
                    + "FROM nota n "
                    + "JOIN materia_curso mc ON n.id_materia = mc.id_materia "
                    + "JOIN inscripcion i ON n.id_inscripcion = i.idinscripcion "
                    + "WHERE mc.id_curso = ? AND i.id_curso = ? AND i.gestion = ? "
                    + "AND n.nota IS NOT NULL";

            Connection connection = this.ListConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            sentence.setInt(1, idcourse);
            sentence.setInt(2, idcourse);
            sentence.setInt(3, gestion);

            ResultSet data = sentence.executeQuery();

            if (data.next()) {
                return data.getInt("cantidad_notas") > 0;
            }

        } catch (SQLException e) {
            System.err.println("Ocurrio un error al verificar las notas");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();
        }
        return false;
    }

    public boolean NotesExistStudent(String ci) {
        try {
            String SQL = "SELECT COUNT(*) FROM nota n "
                    + "JOIN inscripcion i ON n.id_inscripcion = i.idinscripcion "
                    + "JOIN estudiante e ON i.id_estudiante = e.idestudiante "
                    + "WHERE e.cedula_identidad = ?";

            Connection connection = this.ListConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            sentence.setString(1, ci);

            ResultSet data = sentence.executeQuery();

            if (data.next()) {
                return data.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Ocurrio un error al verificar las notas");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();
        }
        return false;
    }

    public boolean updateNoteStudent(String ci, String subjectName, String newNote, int gestion) {

        try {
            String SQL = "UPDATE nota n "
                    + "JOIN inscripcion i ON n.id_inscripcion = i.idinscripcion "
                    + "JOIN estudiante e ON i.id_estudiante = e.idestudiante "
                    + "JOIN materia m ON n.id_materia = m.idmateria "
                    + "SET n.nota = ? "
                    + "WHERE e.cedula_identidad = ? "
                    + "AND m.nombre = ? "
                    + "AND i.gestion = ?";
            
            Connection connection = this.ListConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);
            
            sentence.setBigDecimal(1, new BigDecimal(newNote));
            sentence.setString(2, ci);
            sentence.setString(3, subjectName);
            sentence.setInt(4, gestion);
            
            return sentence.executeUpdate() > 0;
            
        } catch (Exception e) {
            
            System.err.println("Ocurrio un error al modificar la nota");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();
            
        }
        return false;
    }
}
