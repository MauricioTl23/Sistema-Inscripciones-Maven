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
import model.Telf_Guardian;

/**
 *
 * @author mauricioteranlimari
 */
public class Telf_GuardianDao {
    
    private Database connection;

    public Telf_GuardianDao() throws ClassNotFoundException, SQLException {
        this.connection = new Database();
    }

    public boolean register(Telf_Guardian contacto) throws Exception {
        try {
            String SQL = "INSERT INTO telefono_tutor (id_tutor, numero, tipo) VALUES (?, ?, ?)";

            Connection conn = this.connection.getConnection();
            try (PreparedStatement statement = conn.prepareStatement(SQL)) {
                statement.setInt(1, contacto.getId_tutor());
                statement.setString(2, contacto.getNumero());
                statement.setInt(3, contacto.getTipo());

                statement.executeUpdate();
                statement.close();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar contacto");
            e.printStackTrace();
            return false;
        }
    }

    public boolean edit(Telf_Guardian contacto) {
        try {
            String SQL = "UPDATE telefono_tutor SET id_tutor = ?, numero = ?, tipo = ? WHERE idtelefono = ?";

            Connection conn = this.connection.getConnection();
            PreparedStatement statement = conn.prepareStatement(SQL);

            statement.setInt(1, contacto.getId_tutor());
            statement.setString(2, contacto.getNumero());
            statement.setInt(3, contacto.getTipo());
            statement.setInt(4, contacto.getId());

            statement.executeUpdate();
            statement.close();
            return true;

        } catch (Exception e) {
            System.err.println("Error al editar contacto");
            e.printStackTrace();
            return false;
        }
    }
    public List<Telf_Guardian> findByTutorId(int idTutor) {
        List<Telf_Guardian> contactos = new ArrayList<>();

        try {
            String SQL = "SELECT * FROM telefono_tutor WHERE id_tutor = ?";
            Connection conn = this.connection.getConnection();
            PreparedStatement statement = conn.prepareStatement(SQL);

            statement.setInt(1, idTutor);
            ResultSet data = statement.executeQuery();

            while (data.next()) {
                Telf_Guardian contacto = new Telf_Guardian();
                contacto.setId(data.getInt("idtelefono"));
                contacto.setId_tutor(data.getInt("id_tutor"));
                contacto.setNumero(data.getString("numero"));
                contacto.setTipo(data.getInt("tipo"));

                contactos.add(contacto);
            }

            data.close();
            statement.close();

        } catch (Exception e) {
            System.err.println("Error al obtener los contactos del tutor con ID: " + idTutor);
            e.printStackTrace();
        }

        return contactos;
    }

    public boolean deletePhonesByTutorId(int idTutor) {
        String SQL = "DELETE FROM telefono_tutor WHERE id_tutor = ?";

        try (Connection conn = connection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL)) {

            ps.setInt(1, idTutor);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al eliminar teléfonos del tutor con ID: " + idTutor);
            e.printStackTrace();
            return false;
        }
    }
    
}
