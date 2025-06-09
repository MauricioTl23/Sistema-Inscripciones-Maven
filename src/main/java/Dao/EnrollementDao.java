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
            int hasExist = getByIdStudentwithYear(inscripcion.getId_estudiante());
            if(hasExist > 0){
                return -1;
            }
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
    
    public Enrollment obtenerInscripcionConCursoSiguiente(int idEstudiante) {
        Enrollment enrollment = null;

        try {
            Connection connection = dbConnection.getConnection();

            // 1. Obtener la inscripción actual del estudiante en el año vigente
            String sql = "SELECT * FROM inscripcion WHERE id_estudiante = ? AND YEAR(fecha_inscripcion) = YEAR(CURDATE())";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, idEstudiante);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                enrollment = new Enrollment();
                enrollment.setIdinscripcion(rs.getInt("idinscripcion"));
                enrollment.setId_estudiante(rs.getInt("id_estudiante"));
                enrollment.setId_curso(rs.getInt("id_curso")); // Se actualizará más abajo
                enrollment.setId_usuario(rs.getInt("id_usuario"));
                enrollment.setFecha_inscripcion(rs.getDate("fecha_inscripcion"));
                enrollment.setYear(rs.getInt("gestion"));
                enrollment.setEstado(rs.getInt("estado"));
                enrollment.setRude(rs.getInt("rude"));
                enrollment.setObservacion(rs.getString("observacion"));

                // 2. Obtener el curso asignado según si aprobó o no
                String cursoSql = """
                    SELECT 
                        CASE 
                            WHEN AVG(n.nota) >= 51 THEN (
                                SELECT c2.idcurso
                                FROM curso c2
                                WHERE (c2.grado = c.grado + 1 AND c2.nivel = c.nivel)
                    				OR (c2.grado = 5 AND c2.nivel = 0 AND c.grado = 0 AND c.nivel = 1)
                                LIMIT 1
                            )
                            ELSE c.idcurso
                        END AS idcurso
                    FROM inscripcion i
                    JOIN curso c ON i.id_curso = c.idcurso
                    JOIN nota n ON n.id_inscripcion = i.idinscripcion
                    WHERE i.gestion = YEAR(NOW()) - 1  AND i.id_estudiante = ?
                    GROUP BY c.idcurso, c.grado, c.nivel
                """;

                PreparedStatement cursoStmt = connection.prepareStatement(cursoSql);
                cursoStmt.setInt(1, idEstudiante);
                ResultSet cursoRs = cursoStmt.executeQuery();

                if (cursoRs.next()) {
                    int nuevoCurso = cursoRs.getInt("idcurso");
                    enrollment.setId_curso(nuevoCurso); // Modifica el curso con el calculado
                }

                cursoRs.close();
                cursoStmt.close();
            }

            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace(); // o lanzar una excepción personalizada
        }

        return enrollment;
    }


    
    public int getByIdStudentwithYear(int idStudent) {
        int idInscripcion = -1;
        try {
            String sql = "SELECT * FROM inscripcion WHERE id_estudiante = ? AND YEAR(fecha_inscripcion) = YEAR(CURDATE());";
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
