/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import Dao.UserDao;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import model.Extras;
import model.User;

/**
 *
 * @author mauricioteranlimari
 */
public class ManageUsersController implements Initializable {

    @FXML
    private Button BtnCancelar;
    @FXML
    private TextField TextBuscarfCi;
    @FXML
    private TextField TextCiUser;
    @FXML
    private TextField TextEmailUser;
    @FXML
    private TextField TextFnameUser;
    @FXML
    private TextField TextLnameUser;
    @FXML
    private TextField TextPasswordUser;
    @FXML
    private TextField TextPhoneUser;
    @FXML
    private TableView<User> tblUser;
    @FXML
    private ComboBox<String> CboCharge;
    @FXML
    private TextField TextUserUser;

    @FXML
    private TextField textcom;

    @FXML
    private ComboBox<String> cbxexp;

    private UserDao userdao;

    private ContextMenu OptionsUsers;

    private User selectUser;

    private FilteredList<User> filteredData;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    private final String[] departments = {"LP", "SCZ", "CBBA", "OR", "PT", "CH", "TJA", "BE", "PD"};

    String[] cargos = {"Director/a", "Secretario/a", "Asesor/a", "Regente/Regenta"};

    public boolean civalid(String number, String cm, int dp) {
        boolean numberValid = number != null && number.matches("\\d{5,8}");
        boolean cmValid = cm == null || cm.trim().isEmpty() || cm.matches("[A-Z]{1,2}");
        boolean dpValid = dp != -1;
        return numberValid && cmValid && dpValid;
    }

    @FXML
    void BtnAddOnAction(ActionEvent event) throws NoSuchAlgorithmException, Exception {
        if (selectUser == null) {
            //Verificar si se quiere guardar algun NULL
            if (TextFnameUser.getText().isEmpty() || TextLnameUser.getText().isEmpty()
                    || TextCiUser.getText().isEmpty() || TextPhoneUser.getText().isEmpty()
                    || TextEmailUser.getText().isEmpty() || TextPasswordUser.getText().isEmpty()) {

                Extras.showAlert("Advertencia", "Todos los campos deben ser llenados", Alert.AlertType.WARNING);

                return;
            }

            User usuario = new User();

            //Nombre
            usuario.setNombre(TextFnameUser.getText());
            //Apellido
            usuario.setApellido(TextLnameUser.getText());
            //Cedula_Identidad
            String cicomplete;

            if (civalid(TextCiUser.getText(), textcom.getText(), cbxexp.getSelectionModel().getSelectedIndex())) {
                if (textcom.getText() == null || textcom.getText().trim().isEmpty()) {
                    cicomplete = TextCiUser.getText() + "-" + Integer.toString(cbxexp.getSelectionModel().getSelectedIndex());
                } else {
                    cicomplete = TextCiUser.getText() + "-" + textcom.getText() + "-" + Integer.toString(cbxexp.getSelectionModel().getSelectedIndex());
                }
                usuario.setCedula_identidad(cicomplete);
            } else {
                Extras.showAlert("Advertencia", "C.I. Invalido revise los parametros", Alert.AlertType.WARNING);
                TextCiUser.clear();
                textcom.clear();
                cbxexp.getSelectionModel().select("Seleccione");
                return;
            }
            //Celular, verifica si es valido
            if (VerifyNumberUser(TextPhoneUser.getText())) {
                usuario.setCelular(TextPhoneUser.getText());
            } else {
                Extras.showAlert("Advertencia", "Numero de celular invalido", Alert.AlertType.WARNING);
                TextPhoneUser.clear();
                return;
            }
            //Correo, verifica si es valido
            if (VerifyEmailUser(TextEmailUser.getText())) {
                usuario.setCorreo(TextEmailUser.getText());
            } else {
                Extras.showAlert("Advertencia", "Formato de correo invalido", Alert.AlertType.WARNING);
                TextEmailUser.clear();
                return;
            }
            //Agrega cargo
            String selectedCargo = CboCharge.getValue();
            ObservableList<String> items = CboCharge.getItems();
            int selectedIndex = items.indexOf(selectedCargo);
            usuario.setCargo(selectedIndex);
            //Crear Usuario en cuanto se ingrese el nombre
            usuario.setUsuario(TextUserUser.getText());
            //TextUserUser.setEditable(false);
            //encriptar contraseña
            String pass = Encrypt(TextPasswordUser.getText());
            usuario.setContrasena(pass);

            //Para verificar si se puede guardar en la base de datos
            boolean rsp = this.userdao.register(usuario);

            if (rsp) {

                Extras.showAlert("Exito", "Se registro correctamente el usuario", Alert.AlertType.INFORMATION);

                ClearFiels();
                LoadUsers();
                loadCharges();

            } else {

                Extras.showAlert("Error", "hubo un error", Alert.AlertType.ERROR);

            }
        } else {

            if (TextFnameUser.getText().isEmpty() || TextLnameUser.getText().isEmpty()
                    || TextCiUser.getText().isEmpty() || TextPhoneUser.getText().isEmpty()
                    || TextEmailUser.getText().isEmpty() || TextPasswordUser.getText().isEmpty()) {
                Extras.showAlert("Advertencia", "Todos los campos deben ser llenados", Alert.AlertType.WARNING);
                return;
            }

            selectUser.setNombre(TextFnameUser.getText());
            selectUser.setApellido(TextLnameUser.getText());
            selectUser.setCelular(TextPhoneUser.getText());
            selectUser.setCorreo(TextEmailUser.getText());
            String selectedCargo = CboCharge.getValue();
            ObservableList<String> items = CboCharge.getItems();
            int selectedIndex = items.indexOf(selectedCargo);
            selectUser.setCargo(selectedIndex);
            selectUser.setUsuario(TextUserUser.getText());
            selectUser.setContrasena(Encrypt(TextPasswordUser.getText()));

            boolean rsp = this.userdao.Edit(selectUser);

            if (rsp) {

                Extras.showAlert("Exito", "Se guardo correctamente el usuario", Alert.AlertType.INFORMATION);

                ClearFiels();
                LoadUsers();
                loadCharges();

                selectUser = null;

                BtnCancelar.setDisable(true);

            } else {

                Extras.showAlert("Error", "hubo un error", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void BtnCancelarOnAction(ActionEvent event) {

        selectUser = null;

        ClearFiels();

        BtnCancelar.setDisable(true);

    }

    private void ClearFiels() {
        TextFnameUser.clear();
        TextLnameUser.clear();
        TextCiUser.clear();
        TextPhoneUser.clear();
        TextEmailUser.clear();
        TextUserUser.clear();
        TextPasswordUser.clear();
        textcom.clear();
        cbxexp.getSelectionModel().select("Seleccione");
        CboCharge.getSelectionModel().select("Seleccione");
    }

    public void LoadUsers() {

        //tblUser.getItems().clear();
        //tblUser.getColumns().clear();
        List<User> users = this.userdao.tolist();

        ObservableList<User> data = FXCollections.observableArrayList(users);

        filteredData = new FilteredList<>(data, p -> true);
        tblUser.setItems(filteredData);

        TableColumn Idcol = new TableColumn("ID");
        Idcol.setCellValueFactory(new PropertyValueFactory("id"));

        TableColumn Namecol = new TableColumn("NOMBRE(S)");
        Namecol.setCellValueFactory(new PropertyValueFactory("nombre"));

        TableColumn Surnamecol = new TableColumn("APELLIDO(S)");
        Surnamecol.setCellValueFactory(new PropertyValueFactory("apellido"));

        TableColumn CIcol = new TableColumn("CI");
        CIcol.setCellValueFactory(new PropertyValueFactory("cedula_identidad"));
        CIcol.setCellFactory(col -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String[] parts = item.split("-");

                    String lastPart = parts[parts.length - 1];

                    int lastNumber = Integer.parseInt(lastPart);

                    String updatedItem = item.substring(0, item.lastIndexOf("-")) + "-" + departments[lastNumber];

                    setText(updatedItem);
                }
            }
        });
        TableColumn Phonecol = new TableColumn("CELULAR");
        Phonecol.setCellValueFactory(new PropertyValueFactory("celular"));

        TableColumn Emailcol = new TableColumn("CORREO");
        Emailcol.setCellValueFactory(new PropertyValueFactory("correo"));

        TableColumn Chargecol = new TableColumn("CARGO");
        Chargecol.setCellValueFactory(new PropertyValueFactory("cargo"));
        Chargecol.setCellFactory(col -> new TableCell<User, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {

                    String[] cargos = {"Director/a", "Secretario/a", "Asesor/a", "Regente/a"};
                    setText(cargos[item]);

                }
            }
        });

        TableColumn Usercol = new TableColumn("USUARIO");
        Usercol.setCellValueFactory(new PropertyValueFactory("usuario"));

        TableColumn Passwordcol = new TableColumn("CONTRASEÑA");
        Passwordcol.setCellFactory(col -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {

                    setText(Decrypt(item));
                }
            }
        });

        Passwordcol.setCellValueFactory(new PropertyValueFactory("contrasena"));

        //tblUser.setItems(data);
        tblUser.getColumns().addAll(Idcol, Namecol, Surnamecol, CIcol, Phonecol, Emailcol, Chargecol, Usercol, Passwordcol);

    }

    public boolean EditUsers() {
        try {

        } catch (Exception e) {
        }
        return true;
    }

    public static boolean VerifyNumberUser(String number) {
        return number != null && number.matches("\\d{8}");
    }

    public static boolean VerifyEmailUser(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String GenerateUser(String name) {
        String[] nameParts = name.split(" ");
        String firstName = nameParts[0];
        String secondLetter = nameParts.length > 1 ? nameParts[1].substring(0, 1) : "";
        Random random = new Random();
        int randomNum = random.nextInt(1000);
        String randomNumber = String.format("%03d", randomNum);
        return firstName + secondLetter + randomNumber;
    }

    private String key = "PumasAndinos";

    public SecretKeySpec CreateKey(String password) {
        try {
            byte[] chain = password.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            chain = md.digest(chain);
            chain = Arrays.copyOf(chain, 16);
            SecretKeySpec secretKeySpec = new SecretKeySpec(chain, "AES");
            return secretKeySpec;

        } catch (Exception e) {
            return null;
        }
    }

    //Encriptacion
    public String Encrypt(String encrypt) {
        try {
            SecretKeySpec secretKeySpec = CreateKey(key);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            byte[] chain = encrypt.getBytes(StandardCharsets.UTF_8);
            byte[] encrypted = cipher.doFinal(chain);
            String chain_encrypted = Base64.getEncoder().encodeToString(encrypted);
            return chain_encrypted;

        } catch (Exception e) {
            return " ";
        }
    }

    //Desencriptar
    public String Decrypt(String decrypt) {
        try {
            SecretKeySpec secretKeySpec = CreateKey(key);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

            byte[] chain = Base64.getDecoder().decode(decrypt);
            byte[] decrypted = cipher.doFinal(chain);
            String chain_decrypted = new String(decrypted);
            return chain_decrypted;

        } catch (Exception e) {
            return " ";
        }
    }

    private void loadCharges() {
        ObservableList<String> items = FXCollections.observableArrayList(cargos);

        if (userdao.ExistDirector() >= 2) {
            items.remove("Director/a");
        }
        if (userdao.ExistSecretary() >= 2) {
            items.remove("Secretario/a");
        }
        if (userdao.ExistRegent() >= 4) {
            items.remove("Regente/Regenta");
        }

        CboCharge.setItems(items);
        CboCharge.setValue("Seleccione");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        ObservableList<String> departments = FXCollections.observableArrayList(this.departments);
        cbxexp.setItems(departments);
        cbxexp.setValue("Selecciones");

        try {
            this.userdao = new UserDao();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ManageUsersController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ManageUsersController.class.getName()).log(Level.SEVERE, null, ex);
        }

        //loadCharges();
        loadCharges();
        LoadUsers();

        TextFnameUser.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                String generatedUsername = GenerateUser(newValue);
                TextUserUser.setText(generatedUsername);
            } else {
                TextUserUser.setText("");
            }
        });
        OptionsUsers = new ContextMenu();

        //Verificacion de campos
        TextFnameUser.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() > 100) {
                TextFnameUser.setText(oldText);
            }
        });
        TextLnameUser.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() > 100) {
                TextLnameUser.setText(oldText);
            }
        });
        TextCiUser.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*") || newText.length() > 8) {
                TextCiUser.setText(oldText);
            }
        });
        textcom.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("[A-Z]*") || newText.length() > 2) {
                textcom.setText(oldText);
            }
        });
        TextPhoneUser.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("[0-9]*") || newText.length() > 8) {
                TextPhoneUser.setText(oldText);
            }
        });
        TextEmailUser.textProperty().addListener((obs, oldText, newText) -> {
            // Verifica que no exceda los 150 caracteres y que sea un correo válido
            if (newText.length() > 150) {
                TextEmailUser.setText(oldText);
            }
        });
        TextPasswordUser.textProperty().addListener((obs, oldText, newText) -> {
            try {
                byte[] utf8Bytes = newText.getBytes("UTF-8");
                if (utf8Bytes.length > 96) {
                    TextPasswordUser.setText(oldText);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        MenuItem edit = new MenuItem("Editar");
        MenuItem delete = new MenuItem("Eliminar");

        OptionsUsers.getItems().addAll(edit, delete);

        edit.setOnAction((ActionEvent t) -> {
            int index = tblUser.getSelectionModel().getSelectedIndex();

            selectUser = tblUser.getItems().get(index);

            TextFnameUser.setText(selectUser.getNombre());
            TextLnameUser.setText(selectUser.getApellido());

            String cedula = selectUser.getCedula_identidad();
            String[] partes = cedula.split("-");

            TextCiUser.setText(partes[0]);
            TextCiUser.setEditable(false);

            if (partes.length == 3) {
                textcom.setText(partes[1]);
                cbxexp.getSelectionModel().select(Integer.parseInt(partes[2]));
            } else if (partes.length == 2) {
                textcom.setText("");
                cbxexp.getSelectionModel().select(Integer.parseInt(partes[1]));
            }
            textcom.setEditable(false);
            cbxexp.setDisable(true);

            TextPhoneUser.setText(selectUser.getCelular());
            TextEmailUser.setText(selectUser.getCorreo());
            CboCharge.getSelectionModel().select(selectUser.getCargo());
            TextUserUser.setText(selectUser.getUsuario());
            TextUserUser.setEditable(true);
            TextPasswordUser.setText(Decrypt(selectUser.getContrasena()));

            BtnCancelar.setDisable(false);
        });

        delete.setOnAction((ActionEvent t) -> {
            int index = tblUser.getSelectionModel().getFocusedIndex();

            User deleteUser = tblUser.getItems().get(index);

            if (Extras.showConfirmation("¿Desea eliminar el usuario: " + deleteUser.getNombre() + " " + deleteUser.getApellido() + "?")) {

                boolean rsp = userdao.Detele(deleteUser.getId());

                if (rsp) {

                    Extras.showAlert("Exito", "Se elimino correctamente el usuario", Alert.AlertType.INFORMATION);
                    LoadUsers();
                    loadCharges();

                } else {

                    Extras.showAlert("Error", "Hubo un error", Alert.AlertType.ERROR);

                }
            }
        });

        tblUser.setContextMenu(OptionsUsers);

        TextBuscarfCi.textProperty().addListener((observable, oldValue, newValue) -> {
            // Realiza el filtro dinámico mientras el usuario escribe
            filteredData.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                return user.getCedula_identidad().toLowerCase().contains(newValue.toLowerCase());
            });
        });
    }

}
