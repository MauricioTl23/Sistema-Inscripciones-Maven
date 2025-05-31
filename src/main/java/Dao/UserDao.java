/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import controller.ManageUsersController;
import model.Database;
import model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Alert;
import model.Extras;

/**
 *
 * @author mauricioteranlimari
 */
public class UserDao {

    private Database UserConnection;
    private ManageUsersController verify;

    public UserDao() throws ClassNotFoundException, SQLException {
        this.UserConnection = new Database();
        this.verify = new ManageUsersController();
    }

    public boolean register(User usuario) throws Exception {
        try {
            if (isValueExists("cedula_identidad", usuario.getCedula_identidad())) {
                Extras.showAlert("Error", "La cédula de identidad ya está registrada.", Alert.AlertType.ERROR);
                return false;
            }

            if (isValueExists("celular", usuario.getCelular())) {
                Extras.showAlert("Error", "El número de celular ya está registrado.", Alert.AlertType.ERROR);
                return false;
            }
            if (isValueExists("correo", usuario.getCorreo())) {
                Extras.showAlert("Error", "El correo electrónico ya está registrado.", Alert.AlertType.ERROR);
                return false;
            }
            if (isValueExists("usuario", usuario.getUsuario())) {
                Extras.showAlert("Error", "El nombre de usuario ya está registrado.", Alert.AlertType.ERROR);
                return false;
            }
            if (isValueExists("contrasena", usuario.getContrasena())) {
                Extras.showAlert("Error", "La contrasena ya está registrada.", Alert.AlertType.ERROR);
                return false;
            }
            //Consulta para insertar nuevo usuario
            String SQL = "INSERT INTO usuario(nombre, apellido, cedula_identidad, celular, correo, cargo, usuario, contrasena)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            Connection connection = this.UserConnection.getConnection();

            try (PreparedStatement sentence = connection.prepareStatement(SQL)) {
                sentence.setString(1, usuario.getNombre());
                sentence.setString(2, usuario.getApellido());
                sentence.setString(3, usuario.getCedula_identidad());
                sentence.setString(4, usuario.getCelular());
                sentence.setString(5, usuario.getCorreo());
                sentence.setInt(6, usuario.getCargo());
                sentence.setString(7, usuario.getUsuario());
                sentence.setString(8, usuario.getContrasena());

                sentence.executeUpdate();
                sentence.close();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Ocurrio un error al registrar usuario");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();

            return false;
        }
    }

    public List<User> tolist() {

        List<User> listUsers = new ArrayList<>();

        try {

            String SQL = "SELECT * FROM usuario";

            Connection connection = this.UserConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            ResultSet data = sentence.executeQuery();

            while (data.next() == true) {

                User user = new User();

                user.setId(data.getInt(1));
                user.setNombre(data.getString(2));
                user.setApellido(data.getString(3));
                user.setCedula_identidad(data.getString(4));
                user.setCelular(data.getString(5));
                user.setCorreo(data.getString(6));
                user.setCargo(data.getInt(7));
                user.setUsuario(data.getString(8));
                user.setContrasena(data.getString(9));

                listUsers.add(user);
            }
            data.close();
            sentence.close();

        } catch (SQLException e) {
            System.err.println("Ocurrio un error al listar usuarios");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();

        }
        return listUsers;
    }

    public boolean Edit(User user) {

        try {

            String SQL = "UPDATE usuario SET nombre = ?, apellido = ?, celular = ?, correo = ?, cargo = ?, usuario = ?, contrasena = ?"
                    + " WHERE idusuario = ?";

            Connection connection = this.UserConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            sentence.setString(1, user.getNombre());
            sentence.setString(2, user.getApellido());
            sentence.setString(3, user.getCelular());
            sentence.setString(4, user.getCorreo());
            sentence.setInt(5, user.getCargo());
            sentence.setString(6, user.getUsuario());
            sentence.setString(7, user.getContrasena());

            sentence.setInt(8, user.getId());

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

    public boolean Detele(int id) {
        try {

            String SQL = "DELETE FROM usuario WHERE idusuario = ?";

            Connection connection = this.UserConnection.getConnection();
            PreparedStatement sentence = connection.prepareStatement(SQL);

            sentence.setInt(1, id);

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

    public User Login(String username, String password) {
        User user = null;

        try {
            String SQL = "SELECT * FROM usuario WHERE usuario = ? AND contrasena = ?";

            Connection connection = this.UserConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(SQL);

            ManageUsersController verify = new ManageUsersController();
            statement.setString(1, username);
            statement.setString(2, verify.Encrypt(password));

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    user = new User();
                    user.setId(result.getInt(1));
                    user.setNombre(result.getString(2));
                    user.setApellido(result.getString(3));
                    user.setCedula_identidad(result.getString(4));
                    user.setCelular(result.getString(5));
                    user.setCorreo(result.getString(6));
                    user.setCargo(result.getInt(7));
                    user.setUsuario(result.getString(8));
                    user.setContrasena(result.getString(9));
                }
            }

        } catch (SQLException e) {
            System.err.println("Ocurrio un error al iniciar sesion");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");
            e.printStackTrace();
        }

        return user;
    }

    private boolean isValueExists(String field, String value) {
        try {

            String SQL = "SELECT COUNT(*) FROM usuario WHERE " + field + " = ?";
            Connection connection = this.UserConnection.getConnection();
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

    public String username(String user, String password) {
        ManageUsersController en = new ManageUsersController();
        String name = null;
        try {
            String SQL = "SELECT CONCAT(nombre, ' ', apellido) FROM usuario WHERE usuario = ? AND contrasena = ?";

            Connection connection = this.UserConnection.getConnection();
            PreparedStatement sentence = connection.prepareStatement(SQL);

            sentence.setString(1, user);
            sentence.setString(2, en.Encrypt(password));

            ResultSet resultSet = sentence.executeQuery();

            if (resultSet.next()) {
                name = resultSet.getString(1);
            }

            sentence.close();

        } catch (SQLException e) {
            System.err.println("Error al verificar la existencia del valor: " + e.getMessage());
            e.printStackTrace();
        }
        return name;
    }

    public List<String> Advisors() {

        List<String> ListAdvisors = new ArrayList<>();
        try {

            String SQL = "SELECT CONCAT(nombre, ' ', apellido) FROM usuario u "
                    + "LEFT JOIN asesor a ON u.idusuario = a.idusuario "
                    + "WHERE u.cargo = 2 "
                    + "GROUP BY u.idusuario, u.nombre, u.apellido "
                    + "HAVING COUNT(a.idusuario) < 2";

            Connection connection = this.UserConnection.getConnection();
            PreparedStatement sentence = connection.prepareStatement(SQL);

            ResultSet data = sentence.executeQuery();

            while (data.next()) {

                ListAdvisors.add(data.getString(1));

            }
            data.close();
            sentence.close();

        } catch (Exception e) {
            System.err.println("Ocurrio un error al listar asesores");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();

        }
        return ListAdvisors;
    }

    public int idasesor(String fullname) {

        int idaser = 0;

        try {

            String SQL = "SELECT idusuario FROM usuario WHERE CONCAT(nombre,' ',apellido) = ?";

            Connection connection = this.UserConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            sentence.setString(1, fullname);

            ResultSet data = sentence.executeQuery();

            if (data.next()) {

                idaser = data.getInt("idusuario");

            }

            data.close();
            sentence.close();

        } catch (SQLException e) {
            System.err.println("Error al verificar la existencia del asesor");
            e.printStackTrace();
        }
        return idaser;
    }

    public int ExistDirector() {
        try {
            String SQL = "SELECT COUNT(*) FROM usuario WHERE cargo = 0";

            Connection connection = this.UserConnection.getConnection();
            PreparedStatement sentence = connection.prepareStatement(SQL);

            ResultSet data = sentence.executeQuery();

            if (data.next()) {
                return data.getInt(1);
            }

            data.close();
            sentence.close();

            return 0;

        } catch (SQLException e) {
            System.err.println("Error al verificar la existencia de directores");
            e.printStackTrace();
            return -1;
        }

    }

    public int ExistSecretary() {
        try {
            String SQL = "SELECT COUNT(*) FROM usuario WHERE cargo = 1";

            Connection connection = this.UserConnection.getConnection();
            PreparedStatement sentence = connection.prepareStatement(SQL);

            ResultSet data = sentence.executeQuery();

            if (data.next()) {
                return data.getInt(1);
            }

            data.close();
            sentence.close();

            return 0;

        } catch (SQLException e) {
            System.err.println("Error al verificar la existencia de secretarios");
            e.printStackTrace();
            return -1;
        }

    }

    public int ExistRegent() {
        try {
            String SQL = "SELECT COUNT(*) FROM usuario WHERE cargo = 3";

            Connection connection = this.UserConnection.getConnection();
            PreparedStatement sentence = connection.prepareStatement(SQL);

            ResultSet data = sentence.executeQuery();

            if (data.next()) {
                return data.getInt(1);
            }

            data.close();
            sentence.close();

            return 0;

        } catch (SQLException e) {
            System.err.println("Error al verificar la existencia de Regentes");
            e.printStackTrace();
            return -1;
        }

    }

    public List<String> EmailListDirectors() {
        List<String> DirectorsEmails = new ArrayList<>();
        try {

            String SQL = "SELECT correo "
                    + "FROM usuario "
                    + "WHERE cargo = 0";

            Connection connection = this.UserConnection.getConnection();
            PreparedStatement sentence = connection.prepareStatement(SQL);

            ResultSet data = sentence.executeQuery();

            while (data.next()) {

                DirectorsEmails.add(data.getString(1));

            }
            data.close();
            sentence.close();

        } catch (Exception e) {
            System.err.println("Ocurrio un error al listar email's");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();

        }
        return DirectorsEmails;
    }

}
