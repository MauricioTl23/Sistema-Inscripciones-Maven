/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import model.Database;
import model.Documentation;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author mauricioteranlimari
 */
public class DocumentationDao {

    private Database DocumentationConnection;

    public DocumentationDao() throws ClassNotFoundException, SQLException {

        this.DocumentationConnection = new Database();

    }

    public boolean register(Documentation documentation) {

        try {

            String SQL = "INSERT INTO tipo_documento(nombre, obligatorio, cartacompromiso) VALUES (?, ?, ?)";

            Connection connection = this.DocumentationConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            sentence.setString(1, documentation.getNombre());
            sentence.setBoolean(2, documentation.isObligatorio());
            sentence.setBoolean(3, documentation.isCartacompromiso());

            sentence.executeUpdate();
            sentence.close();

            return true;

        } catch (Exception e) {
            System.err.println("Ocurrio un error al registrar el documento");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();

            return false;
        }
    }

    public List<Documentation> toList() {

        List<Documentation> listDocumentation = new ArrayList<>();

        try {

            String SQL = "SELECT * FROM tipo_documento";

            Connection connection = this.DocumentationConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            ResultSet data = sentence.executeQuery();

            while (data.next()) {

                Documentation documentation = new Documentation();

                documentation.setIdtipo_documento(data.getInt(1));
                documentation.setNombre(data.getString(2));
                documentation.setObligatorio(data.getBoolean(3));
                documentation.setCartacompromiso(data.getBoolean(4));

                listDocumentation.add(documentation);

            }

        } catch (Exception e) {
            System.err.println("Ocurrio un error al listar documentacion");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();
        }
        return listDocumentation;
    }

    public boolean editDocumentation(Documentation documentation) {

        try {

            String SQL = "UPDATE tipo_documento SET nombre = ?, obligatorio = ?, cartacompromiso = ? "
                    + "WHERE idtipo_documento = ?";

            Connection connection = this.DocumentationConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            sentence.setString(1, documentation.getNombre());
            sentence.setBoolean(2, documentation.isObligatorio());
            sentence.setBoolean(3, documentation.isCartacompromiso());

            sentence.setInt(4, documentation.getIdtipo_documento());

            sentence.executeUpdate();

            sentence.close();

            return true;

        } catch (Exception e) {

            System.err.println("Ocurrio un error al editar el documento");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();

            return false;
        }
    }

    public boolean deleteDocumentation(int idtipo_documento) {

        try {

            String SQL = "DELETE FROM tipo_documento WHERE idtipo_documento = ?";

            Connection connection = this.DocumentationConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            sentence.setInt(1, idtipo_documento);

            sentence.executeUpdate();

            sentence.close();

            return true;

        } catch (Exception e) {

            System.err.println("Ocurrio un error al editar el documento");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();

            return false;
        }
    }

}
