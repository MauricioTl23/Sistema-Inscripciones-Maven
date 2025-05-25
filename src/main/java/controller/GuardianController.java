/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import model.Guardian;
import model.Telf_Guardian;
import Dao.GuardianDao;
import Dao.Telf_GuardianDao;
import model.Student_Guardian;
import Dao.Student_GuardianDao;
import interfaces.DataReceiver;
import interfaces.MainControllerAware;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Extras;

/**
 *
 * @author mauricioteranlimari
 */
public class GuardianController implements Initializable, MainControllerAware, DataReceiver {

    @FXML
    private Button BtnAdd;
    @FXML
    private Button btnModificar;
    @FXML
    private TextField TextName;
    @FXML
    private TextField TextLast_name;
    @FXML
    private TextField TextEmail;
    @FXML
    private TextField TextCi;

    @FXML
    private TextField TextRelacion;
    @FXML
    private TextField TextTelf_work;
    @FXML
    private TextField TextTelf_home;
    @FXML
    private TextField TextTelf_personal;

    @FXML
    private TextField TextName1;
    @FXML
    private TextField TextLast_name1;
    @FXML
    private TextField TextEmail1;
    @FXML
    private TextField TextCi1;

    @FXML
    private TextField TextRelacion1;
    @FXML
    private TextField TextTelf_work1;
    @FXML
    private TextField TextTelf_home1;
    @FXML
    private TextField TextTelf_personal1;

    private GuardianDao guardianDao;
    private Telf_GuardianDao telf_GuardianDao;
    private Student_GuardianDao student_GuardianDao;
    private Guardian tutor1;
    private Guardian tutor2;
    private Student_Guardian r1;
    private Student_Guardian r2;

    private MainMenuController mainController;

    @Override
    public void setMainController(MainMenuController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void onDataReceived(Object data) {
        BtnAdd.setVisible(false);
        btnModificar.setVisible(true);
        int idStudent = (int) data;
        List<Student_Guardian> list = student_GuardianDao.toListByIdStudent(idStudent);
        if (list.size() >= 1) {
            r1 = list.get(0);
            tutor1 = guardianDao.findById(list.get(0).getId_tutor());
            fillGuardianFields(false);

            System.out.println("TUTOR 1");
            System.out.println("Nombre: " + tutor1.getNombre());
            System.out.println("Apellido: " + tutor1.getApellido());
            System.out.println("CI: " + tutor1.getCedula_identidad());
            System.out.println("Correo: " + tutor1.getCorreo());
            System.out.println("Relación con estudiante: " + r1.getRelacion());
        }

        if (list.size() >= 2) {
            r2 = list.get(1);
            tutor2 = guardianDao.findById(list.get(1).getId_tutor());
            fillGuardianFields(true);

            System.out.println("TUTOR 2");
            System.out.println("Nombre: " + tutor2.getNombre());
            System.out.println("Apellido: " + tutor2.getApellido());
            System.out.println("CI: " + tutor2.getCedula_identidad());
            System.out.println("Correo: " + tutor2.getCorreo());
            System.out.println("Relación con estudiante: " + r2.getRelacion());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            this.guardianDao = new GuardianDao();
            this.telf_GuardianDao = new Telf_GuardianDao();
            this.student_GuardianDao = new Student_GuardianDao();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ManageUsersController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(GuardianController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void BtnAddOnAction(ActionEvent event) throws NoSuchAlgorithmException, Exception {
        if (TextName.getText().isEmpty() || TextLast_name.getText().isEmpty()
                || TextCi.getText().isEmpty() || TextEmail.getText().isEmpty()
                || TextRelacion.getText().isEmpty()) {

            Extras.showAlert("Error", "Todos los campos del primer tutor deben ser llenados.", Alert.AlertType.WARNING);
            return;
        } else if (TextTelf_home.getText().isEmpty() && TextTelf_personal.getText().isEmpty() && TextTelf_work.getText().isEmpty()) {
            Extras.showAlert("Error", "Debe Llenar un telefono como minimo.", Alert.AlertType.WARNING);
            return;
        } else if (!VerifyNumberUser(TextTelf_home.getText()) && !VerifyNumberUser(TextTelf_work.getText()) && !VerifyNumberUser(TextTelf_personal.getText())) {
            Extras.showAlert("Error", "Numero de celular invalido", Alert.AlertType.ERROR);
            TextTelf_home.clear();
            TextTelf_work.clear();
            TextTelf_personal.clear();

        } else {
            // Crear objeto Tutor
            boolean tutor2 = true;
            if (TextName1.getText().isEmpty() || TextLast_name1.getText().isEmpty()
                    || TextCi1.getText().isEmpty() || TextEmail1.getText().isEmpty()
                    || TextRelacion1.getText().isEmpty()) {
                tutor2 = false;
            }

            Guardian tutor = new Guardian();
            Guardian tutor1 = new Guardian();
            String Relacion2 = null;
            tutor.setNombre(TextName.getText());
            tutor.setApellido(TextLast_name.getText());
            tutor.setCedula_identidad(TextCi.getText());
            if (VerifyEmailUser(TextEmail.getText())) {
                tutor.setCorreo(TextEmail.getText());
            } else {
                Extras.showAlert("Error", "Formato de correo invalido", Alert.AlertType.ERROR);
                TextEmail.clear();
            }

            String Relacion1 = TextRelacion.getText();
            //Segundo Tutor
            if (tutor2) {

                tutor1.setNombre(TextName1.getText());
                tutor1.setApellido(TextLast_name1.getText());
                tutor1.setCedula_identidad(TextCi1.getText());
                if (VerifyEmailUser(TextEmail1.getText())) {
                    tutor1.setCorreo(TextEmail1.getText());
                } else {
                    Extras.showAlert("Error", "Formato de correo invalido", Alert.AlertType.ERROR);
                    TextEmail1.clear();
                }

                Relacion2 = TextRelacion.getText();
            }
            try {

                int tutorId1 = this.guardianDao.register(tutor);
                int tutorId2 = 0;
                if (tutor2) {
                    tutorId2 = this.guardianDao.register(tutor1);
                }
                if (tutorId1 > 0) {

                    Telf_Guardian contacto;
                    if (!TextTelf_work.getText().isEmpty()) {
                        if (VerifyNumberUser(TextTelf_work.getText())) {
                            contacto = new Telf_Guardian(tutorId1, TextTelf_work.getText(), 1); // Teléfono del trabajo                    
                            boolean rsp = this.telf_GuardianDao.register(contacto);
                            if (rsp) {
                                Extras.showAlert("Exito", "Se registro correctamente el tutor", Alert.AlertType.INFORMATION);
                            } else {
                                Extras.showAlert("Error", "Hubo un error al guardar", Alert.AlertType.ERROR);
                            }
                        } else {
                            Extras.showAlert("Error", "Numero de celular invalido", Alert.AlertType.ERROR);
                            TextTelf_work.clear();
                        }
                    }
                    if (!TextTelf_home.getText().isEmpty()) {
                        if (VerifyNumberUser(TextTelf_home.getText())) {
                            contacto = new Telf_Guardian(tutorId1, TextTelf_home.getText(), 2); // Teléfono de casa
                            boolean rsp = this.telf_GuardianDao.register(contacto);
                            if (rsp) {
                                Extras.showAlert("Exito", "Se registro correctamente el tutor", Alert.AlertType.INFORMATION);
                            } else {
                                Extras.showAlert("Error", "Hubo un error al guardar", Alert.AlertType.ERROR);
                            }
                        } else {
                            Extras.showAlert("Error", "Numero de celular invalido", Alert.AlertType.ERROR);
                            TextTelf_home.clear();
                        }
                    }
                    if (!TextTelf_personal.getText().isEmpty()) {
                        if (VerifyNumberUser(TextTelf_personal.getText())) {
                            contacto = new Telf_Guardian(tutorId1, TextTelf_personal.getText(), 3); // Teléfono personal
                            boolean rsp = this.telf_GuardianDao.register(contacto);
                            if (rsp) {
                                Extras.showAlert("Exito", "Se registro correctamente el tutor", Alert.AlertType.INFORMATION);
                            } else {
                                Extras.showAlert("Error", "Hubo un error al guardar", Alert.AlertType.ERROR);
                            }
                        } else {
                            Extras.showAlert("Error", "Numero de celular invalido", Alert.AlertType.ERROR);
                            TextTelf_personal.clear();

                        }
                    }
                    if (tutorId2 > 0) {

                        if (!TextTelf_work1.getText().isEmpty()) {
                            if (VerifyNumberUser(TextTelf_work1.getText())) {
                                contacto = new Telf_Guardian(tutorId1, TextTelf_work1.getText(), 1); // Teléfono del trabajo                    
                                boolean rsp = this.telf_GuardianDao.register(contacto);
                                if (rsp) {
                                    Extras.showAlert("Exito", "Se registro correctamente el tutor", Alert.AlertType.INFORMATION);
                                } else {
                                    Extras.showAlert("Error", "Hubo un error al guardar", Alert.AlertType.ERROR);
                                }
                            } else {
                                Extras.showAlert("Error", "Numero de celular invalido", Alert.AlertType.ERROR);
                                TextTelf_work.clear();
                            }
                        }
                        if (!TextTelf_home1.getText().isEmpty()) {
                            if (VerifyNumberUser(TextTelf_home1.getText())) {
                                contacto = new Telf_Guardian(tutorId1, TextTelf_home1.getText(), 2); // Teléfono de casa
                                boolean rsp = this.telf_GuardianDao.register(contacto);
                                if (rsp) {
                                    Extras.showAlert("Exito", "Se registro correctamente el tutor", Alert.AlertType.INFORMATION);
                                } else {
                                    Extras.showAlert("Error", "Hubo un error al guardar", Alert.AlertType.ERROR);
                                }
                            } else {
                                Extras.showAlert("Error", "Numero de celular invalido", Alert.AlertType.ERROR);
                                TextTelf_home.clear();
                            }
                        }
                        if (!TextTelf_personal1.getText().isEmpty()) {
                            if (VerifyNumberUser(TextTelf_personal1.getText())) {
                                contacto = new Telf_Guardian(tutorId1, TextTelf_personal1.getText(), 3); // Teléfono personal
                                boolean rsp = this.telf_GuardianDao.register(contacto);
                                if (rsp) {
                                    Extras.showAlert("Exito", "Se registro correctamente el tutor", Alert.AlertType.INFORMATION);
                                } else {
                                    Extras.showAlert("Error", "Hubo un error al guardar", Alert.AlertType.ERROR);
                                }
                            } else {
                                Extras.showAlert("Error", "Numero de celular invalido", Alert.AlertType.ERROR);
                                TextTelf_personal.clear();

                            }
                        }
                    }
                    if (tutorId1 > 0) {
                        if (tutorId2 > 0) {
                            Extras.showAlert("Éxito", "Tutores registrado correctamente.", Alert.AlertType.INFORMATION);
                        } else {
                            Extras.showAlert("Éxito", "Tutor registrado correctamente.", Alert.AlertType.INFORMATION);
                        }
                    }

                    /// Lógica para volver a "Nuevo Estudiante"
                    if (mainController != null) {
                        mainController.pop();
                        mainController.loadSceneWithData("ExistingStudent", Map.of("id_tutor1", tutorId1, "relacion1", Relacion1, "id_tutor2", tutorId2, "relacion2", Relacion2));

                    } else {
                        System.out.println("Error: MainMenuController no está disponible.");
                    }

                } else {
                    Extras.showAlert("Error", "No se pudo registrar el tutor.", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Extras.showAlert("Error", "Ocurrió un error al registrar el tutor.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void onEdit(ActionEvent event) throws NoSuchAlgorithmException, Exception {
        if (tutor1 != null) {
            if (TextName.getText().isEmpty() || TextLast_name.getText().isEmpty()
                    || TextCi.getText().isEmpty() || TextEmail.getText().isEmpty()
                    || TextRelacion.getText().isEmpty()) {

                Extras.showAlert("Advertencia", "Todos los campos del tutor 1 deben estar completos.", Alert.AlertType.WARNING);
                return;
            }

            tutor1.setNombre(TextName.getText());
            tutor1.setApellido(TextLast_name.getText());
            tutor1.setCorreo(TextEmail.getText());
            tutor1.setCedula_identidad(TextCi.getText());

            boolean actualizado = guardianDao.edit(tutor1);

            if (actualizado) {
                student_GuardianDao.edit(r1.getId_estudiante(), r1.getId_tutor(), TextRelacion.getText());

                updateOrInsertPhonesForTutor(tutor1.getId(),
                        TextTelf_personal.getText(),
                        TextTelf_home.getText(),
                        TextTelf_work.getText()
                );
            }
        }

        if (tutor2 != null) {
            if (TextName1.getText().isEmpty() || TextLast_name1.getText().isEmpty()
                    || TextCi1.getText().isEmpty() || TextEmail1.getText().isEmpty()
                    || TextRelacion1.getText().isEmpty()) {

Extras.showAlert("Advertencia", "Todos los campos del tutor 1 deben estar completos.", Alert.AlertType.WARNING);
                return;
            }

            tutor2.setNombre(TextName1.getText());
            tutor2.setApellido(TextLast_name1.getText());
            tutor2.setCorreo(TextEmail1.getText());
            tutor2.setCedula_identidad(TextCi1.getText());

            boolean actualizado = guardianDao.edit(tutor2);

            if (actualizado) {
                student_GuardianDao.edit(r2.getId_estudiante(), r2.getId_tutor(), TextRelacion1.getText());

                updateOrInsertPhonesForTutor(tutor2.getId(),
                        TextTelf_personal1.getText(),
                        TextTelf_home1.getText(),
                        TextTelf_work1.getText()
                );
            }
        }

        Extras.showAlert("Exito", "Los tutores han sido modificados con exito", Alert.AlertType.INFORMATION);
    }

    private void updateOrInsertPhonesForTutor(int idTutor, String personal, String home, String work) throws Exception {
        List<Telf_Guardian> telefonosExistentes = telf_GuardianDao.findByTutorId(idTutor);

        // Mapeamos los teléfonos existentes por tipo (1 = Personal, 2 = Casa, 3 = Trabajo)
        Map<Integer, Telf_Guardian> mapPorTipo = new HashMap<>();
        for (Telf_Guardian tel : telefonosExistentes) {
            mapPorTipo.put(tel.getTipo(), tel);
        }

        // PERSONAL (tipo 1)
        if (personal != null && !personal.trim().isEmpty()) {
            if (mapPorTipo.containsKey(1)) {
                Telf_Guardian tel = mapPorTipo.get(1);
                tel.setNumero(personal);
                telf_GuardianDao.edit(tel);
            } else {
                Telf_Guardian nuevo = new Telf_Guardian();
                nuevo.setId_tutor(idTutor);
                nuevo.setNumero(personal);
                nuevo.setTipo(3);
                telf_GuardianDao.register(nuevo);
            }
        }

        // CASA (tipo 2)
        if (home != null && !home.trim().isEmpty()) {
            if (mapPorTipo.containsKey(2)) {
                Telf_Guardian tel = mapPorTipo.get(2);
                tel.setNumero(home);
                telf_GuardianDao.edit(tel);
            } else {
                Telf_Guardian nuevo = new Telf_Guardian();
                nuevo.setId_tutor(idTutor);
                nuevo.setNumero(home);
                nuevo.setTipo(2);
                telf_GuardianDao.register(nuevo);
            }
        }

        // TRABAJO (tipo 3)
        if (work != null && !work.trim().isEmpty()) {
            if (mapPorTipo.containsKey(3)) {
                Telf_Guardian tel = mapPorTipo.get(3);
                tel.setNumero(work);
                telf_GuardianDao.edit(tel);
            } else {
                Telf_Guardian nuevo = new Telf_Guardian();
                nuevo.setId_tutor(idTutor);
                nuevo.setNumero(work);
                nuevo.setTipo(1);
                telf_GuardianDao.register(nuevo);
            }
        }
    }

    private void fillGuardianFields(boolean isSecond) {
        if (tutor1 != null) {
            TextName.setText(tutor1.getNombre());
            TextLast_name.setText(tutor1.getApellido());
            TextCi.setText(tutor1.getCedula_identidad());
            TextEmail.setText(tutor1.getCorreo());
            TextRelacion.setText(r1.getRelacion() != null ? r1.getRelacion() : "");
            // Teléfonos tutor1
            List<Telf_Guardian> telefonos1 = telf_GuardianDao.findByTutorId(tutor1.getId());
            for (Telf_Guardian tel : telefonos1) {
                switch (tel.getTipo()) {
                    case 1:
                        TextTelf_personal.setText(tel.getNumero());
                        break;
                    case 2:
                        TextTelf_home.setText(tel.getNumero());
                        break;
                    case 3:
                        TextTelf_work.setText(tel.getNumero());
                        break;
                }
            }
        }

        if (tutor2 != null) {
            TextName1.setText(tutor2.getNombre());
            TextLast_name1.setText(tutor2.getApellido());
            TextCi1.setText(tutor2.getCedula_identidad());
            TextEmail1.setText(tutor2.getCorreo());

            TextRelacion1.setText(r2.getRelacion() != null ? r2.getRelacion() : "");

            // Teléfonos tutor2
            List<Telf_Guardian> telefonos2 = telf_GuardianDao.findByTutorId(tutor2.getId());
            for (Telf_Guardian tel : telefonos2) {
                switch (tel.getTipo()) {
                    case 1:
                        TextTelf_personal1.setText(tel.getNumero());
                        break;
                    case 2:
                        TextTelf_home1.setText(tel.getNumero());
                        break;
                    case 3:
                        TextTelf_work1.setText(tel.getNumero());
                        break;
                }
            }
        }
    }

    private void fillPhoneFields(List<Telf_Guardian> telefonos, boolean isSecond) {
        for (Telf_Guardian telf : telefonos) {
            switch (telf.getTipo()) {
                case 3:
                    if (!isSecond) {
                        TextTelf_personal.setText(telf.getNumero());
                    } else {
                        TextTelf_personal1.setText(telf.getNumero());
                    }
                    break;
                case 2:
                    if (!isSecond) {
                        TextTelf_home.setText(telf.getNumero());
                    } else {
                        TextTelf_home1.setText(telf.getNumero());
                    }
                    break;
                case 1:
                    if (!isSecond) {
                        TextTelf_work.setText(telf.getNumero());
                    } else {
                        TextTelf_work1.setText(telf.getNumero());
                    }
                    break;
            }
        }
    }

    public static boolean VerifyNumberUser(String number) {
        return number != null && number.matches("\\d{8}");
    }
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    public static boolean VerifyEmailUser(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
