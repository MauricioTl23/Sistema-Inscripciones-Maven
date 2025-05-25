/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Alert;
import model.Database;
import model.Extras;
import model.Guardian;

/**
 *
 * @author mauricioteranlimari
 */
public class GuardianDao {

    private Database guardianConnection;
    private Telf_GuardianDao contacto;

    public GuardianDao() throws ClassNotFoundException, SQLException {
        this.guardianConnection = new Database();
        this.contacto = new Telf_GuardianDao();
    }

    public int register(Guardian guardian) throws Exception {
        try {
            if (isValueExists("cedula_identidad", guardian.getCedula_identidad())) {
                Extras.showAlert("Error", "La cédula de identidad ya está registrada.", Alert.AlertType.ERROR);
                return -1;
            }
            if (isValueExists("correo", guardian.getCorreo())) {
                Extras.showAlert("Error", "El correo electrónico ya está registrado.", Alert.AlertType.ERROR);
                return -1;
            }

            String SQL = "INSERT INTO tutor (nombre, apellido, cedula_identidad, correo) VALUES (?, ?, ?, ?)";

            Connection connection = this.guardianConnection.getConnection();

            try (PreparedStatement sentence = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {
                sentence.setString(1, guardian.getNombre());
                sentence.setString(2, guardian.getApellido());
                sentence.setString(3, guardian.getCedula_identidad());
                sentence.setString(4, guardian.getCorreo());

                int affectedRows = sentence.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = sentence.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            return generatedKeys.getInt(1); // Retorna el ID generado
                        }
                    }
                }

                return -1; // Retorna -1 si no se generó ningún ID
            }
        } catch (SQLException e) {
            System.err.println("Ocurrió un error al registrar el tutor");
            e.printStackTrace();
            return -1;
        }
    }

    public List<Guardian> toList() {
        List<Guardian> listGuardians = new ArrayList<>();

        try {
            String SQL = "SELECT * FROM tutor";
            Connection connection = this.guardianConnection.getConnection();
            PreparedStatement sentence = connection.prepareStatement(SQL);
            ResultSet data = sentence.executeQuery();

            while (data.next()) {
                Guardian guardian = new Guardian();
                guardian.setId(data.getInt("idtutor"));
                guardian.setNombre(data.getString("nombre"));
                guardian.setApellido(data.getString("apellido"));
                guardian.setCedula_identidad(data.getString("cedula_identidad"));
                guardian.setCorreo(data.getString("correo"));

                listGuardians.add(guardian);
            }
            data.close();
            sentence.close();

        } catch (SQLException e) {
            System.err.println("Ocurrió un error al listar tutores");
            e.printStackTrace();
        }
        return listGuardians;
    }

    public Guardian findById(int idTutor) {
        Guardian guardian = null;

        try {
            String SQL = "SELECT * FROM tutor WHERE idtutor = ?";
            Connection connection = this.guardianConnection.getConnection();
            PreparedStatement sentence = connection.prepareStatement(SQL);
            sentence.setInt(1, idTutor);
            ResultSet data = sentence.executeQuery();

            if (data.next()) {
                guardian = new Guardian();
                guardian.setId(data.getInt("idtutor"));
                guardian.setNombre(data.getString("nombre"));
                guardian.setApellido(data.getString("apellido"));
                guardian.setCedula_identidad(data.getString("cedula_identidad"));
                guardian.setCorreo(data.getString("correo"));
            }

            data.close();
            sentence.close();

        } catch (SQLException e) {
            System.err.println("Ocurrió un error al buscar tutor por ID");
            e.printStackTrace();
        }

        return guardian;
    }

    public boolean edit(Guardian guardian) {
        try {
            String SQL = "UPDATE tutor SET nombre = ?, apellido = ?, cedula_identidad = ?, correo = ? WHERE idtutor = ?";
            Connection connection = this.guardianConnection.getConnection();
            PreparedStatement sentence = connection.prepareStatement(SQL);

            sentence.setString(1, guardian.getNombre());
            sentence.setString(2, guardian.getApellido());
            sentence.setString(3, guardian.getCedula_identidad());
            sentence.setString(4, guardian.getCorreo());
            sentence.setInt(5, guardian.getId());

            sentence.executeUpdate();
            sentence.close();
            return true;

        } catch (Exception e) {
            System.err.println("Ocurrió un error al editar tutor");
            e.printStackTrace();
            return false;
        }
    }

    private boolean isValueExists(String field, String value) {
        try {
            String SQL = "SELECT COUNT(*) FROM tutor WHERE " + field + " = ?";
            Connection connection = this.guardianConnection.getConnection();
            PreparedStatement sentence = connection.prepareStatement(SQL);
            sentence.setString(1, value);
            ResultSet resultSet = sentence.executeQuery();

            return resultSet.next() && resultSet.getInt(1) > 0;

        } catch (SQLException e) {
            System.err.println("Error al verificar la existencia del valor");
            e.printStackTrace();
            return false;
        }
    }

    public List<Integer> getTutorIdsByStudent(int studentId) {
        List<Integer> tutorIds = new ArrayList<>();
        String sql = "SELECT id_tutor FROM estudiante_tutor WHERE id_estudiante = ?";
        try {
            Connection connection = this.guardianConnection.getConnection();
            PreparedStatement sentence = connection.prepareStatement(sql);
            sentence.setInt(1, studentId);
            ResultSet rs = sentence.executeQuery();
            while (rs.next()) {
                tutorIds.add(rs.getInt("id_tutor"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tutorIds;
    }

    public int countStudentsByTutorExcluding(int tutorId, int excludedStudentId) {
        String sql = "SELECT COUNT(*) FROM estudiante_tutor WHERE id_tutor = ? AND id_estudiante != ?";
        try {
            Connection connection = this.guardianConnection.getConnection();
            PreparedStatement sentence = connection.prepareStatement(sql);
            sentence.setInt(1, tutorId);
            sentence.setInt(2, excludedStudentId);
            ResultSet rs = sentence.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void deleteOrphanTutorsByStudent(int studentId) {
        List<Integer> tutorIds = getTutorIdsByStudent(studentId);

        for (int tutorId : tutorIds) {
            int count = countStudentsByTutorExcluding(tutorId, studentId);
            if (count == 0) {
                delete(tutorId);
                contacto.deletePhonesByTutorId(tutorId);
            }
        }
    }

    public boolean delete(int tutorId) {
        String sql = "DELETE FROM tutor WHERE id = ?";
        try {
            Connection connection = this.guardianConnection.getConnection();
            PreparedStatement sentence = connection.prepareStatement(sql);
            sentence.setInt(1, tutorId);
            int rowsAffected = sentence.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
