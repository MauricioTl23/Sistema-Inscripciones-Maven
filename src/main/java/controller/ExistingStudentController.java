/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import Dao.StudentDao;
import Dao.GuardianDao;
import Dao.Student_GuardianDao;
import interfaces.DataReceiver;
import model.Student;
import model.Guardian;
import interfaces.MainControllerAware;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import model.Student_Guardian;
import model.Course;
import Dao.CourseDao;
import Dao.EnrollementDao;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.TextArea;
import model.Enrollment;
import Dao.SubmittedDocumentDao;

public class ExistingStudentController implements Initializable, MainControllerAware, DataReceiver {

    @FXML
    private Button btnDocumentacion;

    @FXML
    private Button btnTutorNuevo;
    @FXML
    private Button btnModificar;
    @FXML
    private Button BtnAdd;
    @FXML
    private ComboBox<Guardian> CboTutor;
    @FXML
    private ComboBox<Guardian> CboTutor1;
    @FXML
    private ComboBox<String> CboGender;
    @FXML
    private TextField TextName;
    @FXML
    private TextField TextLast_name;
    @FXML
    private TextField TextAddress;
    @FXML
    private DatePicker TimeDateBirth;
    @FXML
    private TextField TextEmail;
    @FXML
    private TextField TextCi;
    //Inscripcion 
    @FXML
    private ComboBox<Course> CboCurso;

    @FXML
    private TextField TextRude;
    @FXML
    private TextArea TextObs;

    @FXML
    private TextField TextRelacion;
    @FXML
    private TextField TextRelacion1;

    private StudentDao studentdao;
    private GuardianDao guardianDao;
    private Student_GuardianDao student_GuardianDao;
    private CourseDao courseDao;
    private EnrollementDao enrollmentDao;
    private SubmittedDocumentDao submittedDocumentDao;
    private Student selectStudent;
    private ObservableList<Guardian> listaTutores = FXCollections.observableArrayList();
    private FilteredList<Guardian> filteredTutores;
    private ObservableList<Course> listaCursos = FXCollections.observableArrayList();
    private FilteredList<Course> filteredCursos;
    private MainMenuController mainController;
    private int idtutor1;
    private int idtutor2;
    private int ultimoIdRegistrado = 0;
    private boolean fueInscrito = true;

    @Override
    public void setMainController(MainMenuController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void onDataReceived(Object data) {
        if (data == null) {
            System.out.println("Inicialización sin datos.");
            return;
        }

        if (data instanceof Student) {
            selectStudent = (Student) data;

            // Llenar campos con los datos del estudiante
            TextName.setText(selectStudent.getNombre());
            TextLast_name.setText(selectStudent.getApellido());
            TextAddress.setText(selectStudent.getDireccion());
            TextEmail.setText(selectStudent.getCorreo());
            TextCi.setText(selectStudent.getCedula_identidad());
            TimeDateBirth.setValue(selectStudent.getFecha_nacimiento().toLocalDate());

            CboGender.setValue(
                    selectStudent.getGenero() == 0 ? "Masculino" : "Femenino"
            );

            btnTutorNuevo.setVisible(false);
            BtnAdd.setVisible(false);

            btnDocumentacion.setVisible(true);
            btnModificar.setVisible(true);
            List<Student_Guardian> relaciones = student_GuardianDao.toListByIdStudent(selectStudent.getId());

            if (relaciones.size() > 0) {
                System.out.println(relaciones.get(0).getId_tutor() + " | " + relaciones.get(0).getRelacion());
                selectTutor1ById(relaciones.get(0).getId_tutor());
                TextRelacion.setText(relaciones.get(0).getRelacion());
            }

            if (relaciones.size() > 1) {
                System.out.println(relaciones.get(0).getId_tutor() + " | " + relaciones.get(0).getRelacion());
                selectTutor1ById2(relaciones.get(0).getId_tutor());
                TextRelacion1.setText(relaciones.get(1).getRelacion());
            }

        } else if (data instanceof Map) {
            Map<String, Object> mapData = (Map<String, Object>) data;
            idtutor1 = (int) mapData.get("id_tutor1");
            String relacion1 = (String) mapData.get("relacion1");
            idtutor2 = (int) mapData.get("id_tutor2");
            String relacion2 = (String) mapData.get("relacion2");
            TextRelacion.setText(relacion1);
            selectTutor1ById(idtutor1);
            TextRelacion1.setText(relacion2);
            selectTutor1ById2(idtutor2);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnDocumentacion.setOnAction(e -> {
            if (mainController != null) {
                if (selectStudent != null) {
                    mainController.loadSceneWithData("Documentation", selectStudent.getId());
                    mainController.addPage("Documentación de " + selectStudent.getNombre() + " " + selectStudent.getApellido(), "Documentation");
                }
            } else {
                System.out.println("Error: MainMenuController no está disponible.");
            }
            
        });
        btnDocumentacion.setVisible(false);
        btnTutorNuevo.setOnAction(e -> navigateTo("Nuevo Tutor", "Guardian"));

        try {
            this.guardianDao = new GuardianDao();
            this.student_GuardianDao = new Student_GuardianDao();
            this.studentdao = new StudentDao();
            this.courseDao = new CourseDao();
            this.enrollmentDao = new EnrollementDao();
            this.submittedDocumentDao = new SubmittedDocumentDao();

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ManageUsersController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ExistingStudentController.class.getName()).log(Level.SEVERE, null, ex);
        }

        ObservableList<String> ol = FXCollections.observableArrayList("Masculino", "Femenino");
        CboGender.setItems(ol);
        CboGender.setValue("Seleccione");
        CboTutor.setEditable(true);

        CboTutor.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Guardian tutor, boolean empty) {
                super.updateItem(tutor, empty);
                setText((tutor == null || empty) ? "" : tutor.getNombre() + " " + tutor.getApellido());
            }
        });

        CboTutor.setConverter(new StringConverter<>() {
            @Override
            public String toString(Guardian tutor) {
                return (tutor != null) ? tutor.getNombre() + " " + tutor.getApellido() : "";
            }

            @Override
            public Guardian fromString(String string) {
                return listaTutores.stream()
                        .filter(t -> (t.getNombre() + " " + t.getApellido()).equalsIgnoreCase(string))
                        .findFirst().orElse(null);
            }
        });

        listaTutores.addAll(guardianDao.toList());
        filteredTutores = new FilteredList<>(listaTutores, p -> true);
        CboTutor.setItems(filteredTutores);

        CboTutor.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            final TextField editor = CboTutor.getEditor();
            final Guardian selected = CboTutor.getSelectionModel().getSelectedItem();

            filteredTutores.setPredicate(tutor -> {
                if (newVal == null || newVal.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newVal.toLowerCase().trim();

                String fullName = (tutor.getNombre() + " " + tutor.getApellido()).toLowerCase();
                return fullName.contains(lowerCaseFilter);
            });

            if (!CboTutor.isShowing() && !newVal.isEmpty()) {
                Platform.runLater(CboTutor::show);
            }
            if (selected == null || !editor.getText().equals(selected.getNombre() + " " + selected.getApellido())) {
                CboTutor.getSelectionModel().clearSelection();
            }
            imprimirItemsComboBox(CboTutor);
        });
        CboTutor.setOnAction(event -> {
            Guardian selectedTutor = CboTutor.getSelectionModel().getSelectedItem();
            if (selectedTutor != null) {
                CboTutor.setValue(selectedTutor);
                CboTutor.getEditor().setText(CboTutor.getConverter().toString(selectedTutor));
                CboTutor.getEditor().positionCaret(CboTutor.getEditor().getText().length());
                CboTutor.hide();
            }
        });
        CboTutor.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue) {
                String inputText = CboTutor.getEditor().getText();
                Guardian match = fromText(inputText);
                if (match != null) {
                    CboTutor.setValue(match);
                } else {
                    CboTutor.getSelectionModel().clearSelection();
                    CboTutor.getEditor().clear();
                }
            }
        });

        Platform.runLater(() -> {
            TextField editor = CboTutor.getEditor();
            editor.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
                if (event.getCode() == KeyCode.SPACE) {
                    event.consume();
                }
            });
        });

        CboTutor1.setEditable(true);

        CboTutor1.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Guardian tutor, boolean empty) {
                super.updateItem(tutor, empty);
                setText((tutor == null || empty) ? "" : tutor.getNombre() + " " + tutor.getApellido());
            }
        });

        CboTutor1.setConverter(new StringConverter<>() {
            @Override
            public String toString(Guardian tutor) {
                return (tutor != null) ? tutor.getNombre() + " " + tutor.getApellido() : "";
            }

            @Override
            public Guardian fromString(String string) {
                return listaTutores.stream()
                        .filter(t -> (t.getNombre() + " " + t.getApellido()).equalsIgnoreCase(string))
                        .findFirst().orElse(null);
            }
        });

        CboTutor1.setItems(filteredTutores);

        CboTutor1.getEditor().textProperty().addListener((obs1, oldVal1, newVal1) -> {
            final TextField editor1 = CboTutor1.getEditor();
            final Guardian selected1 = CboTutor1.getSelectionModel().getSelectedItem();

            filteredTutores.setPredicate(tutor -> {
                if (newVal1 == null || newVal1.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newVal1.toLowerCase().trim();

                String fullName = (tutor.getNombre() + " " + tutor.getApellido()).toLowerCase();
                return fullName.contains(lowerCaseFilter);
            });

            if (!CboTutor1.isShowing() && !newVal1.isEmpty()) {
                Platform.runLater(CboTutor1::show);
            }

            if (selected1 == null || !editor1.getText().equals(selected1.getNombre() + " " + selected1.getApellido())) {
                CboTutor1.getSelectionModel().clearSelection();
            }

            imprimirItemsComboBox(CboTutor);
        });

        CboTutor1.setOnAction(event -> {
            Guardian selectedTutor = CboTutor1.getSelectionModel().getSelectedItem();
            if (selectedTutor != null) {
                CboTutor1.setValue(selectedTutor);
                CboTutor1.getEditor().setText(CboTutor1.getConverter().toString(selectedTutor));
                CboTutor1.getEditor().positionCaret(CboTutor1.getEditor().getText().length());
                CboTutor1.hide();
            }
        });

        CboTutor1.focusedProperty().addListener((obs1, oldValue1, newValue1) -> {
            if (!newValue1) {
                String inputText = CboTutor1.getEditor().getText();
                Guardian match = fromText(inputText);
                if (match != null) {
                    CboTutor1.setValue(match);
                } else {
                    CboTutor1.getSelectionModel().clearSelection();
                    CboTutor1.getEditor().clear();
                }
            }
        });

        Platform.runLater(() -> {
            TextField editor1 = CboTutor1.getEditor();
            editor1.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
                if (event.getCode() == KeyCode.SPACE) {
                    event.consume();
                }
            });
        });

        CboCurso.setEditable(true);

        listaCursos.addAll(courseDao.toList());
        filteredCursos = new FilteredList<>(listaCursos, p -> true);
        CboCurso.setItems(filteredCursos);

        CboCurso.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Course curso, boolean empty) {
                super.updateItem(curso, empty);
                if (empty || curso == null) {
                    setText(null);
                } else {
                    setText(getNombreGrado(curso.getGrado()) + " " + curso.getParalelo()
                            + " (" + getNombreNivel(curso.getNivel()) + ")");
                }
            }
        });

        CboCurso.setConverter(new StringConverter<Course>() {
            @Override
            public String toString(Course curso) {
                return (curso != null)
                        ? getNombreNivel(curso.getNivel()) + " " + getNombreGrado(curso.getGrado()) + " " + curso.getParalelo()
                        + " (" + getNombreNivel(curso.getNivel()) + ")"
                        : "";
            }

            @Override
            public Course fromString(String string) {
                return listaCursos.stream()
                        .filter(c -> (getNombreNivel(c.getNivel()) + " " + getNombreGrado(c.getGrado()) + " " + c.getParalelo()
                        + " (" + getNombreNivel(c.getNivel()) + ")").equalsIgnoreCase(string))
                        .findFirst().orElse(null);
            }
        });

        CboCurso.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            final TextField editor = CboCurso.getEditor();
            final Course selected = CboCurso.getSelectionModel().getSelectedItem();

            filteredCursos.setPredicate(curso -> {
                if (newVal == null || newVal.isEmpty()) {
                    return true;
                }

                String filter = newVal.toLowerCase().trim();
                String display = (getNombreNivel(curso.getNivel()) + " " + getNombreGrado(curso.getGrado()) + " " + curso.getParalelo()
                        + " (" + getNombreNivel(curso.getNivel()) + ")").toLowerCase();
                return display.contains(filter);
            });

            if (!CboCurso.isShowing() && !newVal.isEmpty()) {
                Platform.runLater(CboCurso::show);
            }

            if (selected == null || !editor.getText().equals(CboCurso.getConverter().toString(selected))) {
                CboCurso.getSelectionModel().clearSelection();
            }
        });

        CboCurso.setOnAction(event -> {
            Course selectedCourse = CboCurso.getSelectionModel().getSelectedItem();
            if (selectedCourse != null) {
                CboCurso.setValue(selectedCourse);
                CboCurso.getEditor().setText(CboCurso.getConverter().toString(selectedCourse));
                CboCurso.getEditor().positionCaret(CboCurso.getEditor().getText().length());
                CboCurso.hide();
            }
        });

        CboCurso.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue) {
                String input = CboCurso.getEditor().getText();
                Course match = filteredCursos.stream()
                        .filter(c -> CboCurso.getConverter().toString(c).equalsIgnoreCase(input))
                        .findFirst()
                        .orElse(null);

                if (match != null) {
                    CboCurso.setValue(match);
                } else {
                    CboCurso.getSelectionModel().clearSelection();
                    CboCurso.getEditor().clear();
                }
            }
        });

        Platform.runLater(() -> {
            TextField editor = CboCurso.getEditor();
            editor.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.SPACE) {
                    event.consume();
                }
            });
        });

        cargarEstado();
        System.out.println(fueInscrito + " " + ultimoIdRegistrado);
        if (!fueInscrito) {
            selectStudent = studentdao.SearchbyId(ultimoIdRegistrado);
            TextName.setText(selectStudent.getNombre());
            TextLast_name.setText(selectStudent.getApellido());
            TextAddress.setText(selectStudent.getDireccion());
            TextEmail.setText(selectStudent.getCorreo());
            TextCi.setText(selectStudent.getCedula_identidad());
            TimeDateBirth.setValue(selectStudent.getFecha_nacimiento().toLocalDate());

            CboGender.setValue(
                    selectStudent.getGenero() == 0 ? "Masculino" : "Femenino"
            );

            btnTutorNuevo.setVisible(false);
            BtnAdd.setVisible(false);

            btnDocumentacion.setVisible(true);
            btnModificar.setVisible(true);
            List<Student_Guardian> relaciones = student_GuardianDao.toListByIdStudent(selectStudent.getId());

            if (relaciones.size() > 0) {
                System.out.println(relaciones.get(0).getId_tutor() + " | " + relaciones.get(0).getRelacion());
                selectTutor1ById(relaciones.get(0).getId_tutor());
                TextRelacion.setText(relaciones.get(0).getRelacion());
            }

            if (relaciones.size() > 1) {
                System.out.println(relaciones.get(0).getId_tutor() + " | " + relaciones.get(0).getRelacion());
                selectTutor1ById2(relaciones.get(0).getId_tutor());
                TextRelacion1.setText(relaciones.get(1).getRelacion());
            }

        }
    }

    public void navigateTo(String pageName, String fxmlName) {
        System.out.println("Presionado " + pageName);
        if (mainController != null) {
            mainController.loadView(fxmlName);
            mainController.addPage(pageName, fxmlName);
        } else {
            System.out.println("Error: MainMenuController no está disponible.");
        }
    }

    private Guardian fromText(String text) {
        return listaTutores.stream()
                .filter(t -> (t.getNombre() + " " + t.getApellido()).equalsIgnoreCase(text.trim()))
                .findFirst().orElse(null);
    }

    @FXML
    void BtnAddOnAction(ActionEvent event) throws NoSuchAlgorithmException, Exception {
        if (TextName.getText().isEmpty() || TextLast_name.getText().isEmpty()
                || TextCi.getText().isEmpty() || TimeDateBirth.getValue() == null
                || TextEmail.getText().isEmpty() || TextAddress.getText().isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Todos los campos deben ser llenados.");
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();
            return;
        }

        Student estudiante = new Student();

        idtutor1 = CboTutor.getSelectionModel().getSelectedItem() != null
                ? CboTutor.getSelectionModel().getSelectedItem().getId()
                : idtutor1;
        estudiante.setNombre(TextName.getText());
        estudiante.setApellido(TextLast_name.getText());
        estudiante.setCedula_identidad(TextCi.getText());
        estudiante.setFecha_nacimiento(java.sql.Date.valueOf(TimeDateBirth.getValue()));
        String selectedGender = CboGender.getValue();
        ObservableList<String> items = CboGender.getItems();
        int selectedIndex = items.indexOf(selectedGender);
        estudiante.setGenero(selectedIndex);
        estudiante.setDireccion(TextAddress.getText());
        estudiante.setCorreo(TextEmail.getText());

        //Para verificar si se puede guardar en la base de datos
        int idStudent = this.studentdao.register(estudiante);
        
        System.out.println("Id estudiante " + idStudent + " id tutor " + idtutor1 + "Relacion " + TextRelacion.getText());

        boolean rsp = this.student_GuardianDao.register(idtutor1, idStudent, TextRelacion.getText());
        boolean rsp1 = false;
        if (idtutor2 > 0) {
            rsp1 = this.student_GuardianDao.register(idtutor2, idStudent, TextRelacion1.getText());
        }

        if (idStudent > 0 && rsp) {
            ultimoIdRegistrado = idStudent;
            fueInscrito = false;
            guardarEstado();
            if (rsp1) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Exito");
                alert.setHeaderText(null);
                alert.setContentText("Se registro correctamente el Estudiante con sus dos tutores");
                alert.initStyle(StageStyle.UTILITY);
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Exito");
                alert.setHeaderText(null);
                alert.setContentText("Se registro correctamente el Estudiante con un tutor");
                alert.initStyle(StageStyle.UTILITY);
                alert.showAndWait();
            }
            clearFields();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Hubo un error al guardar");
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();
        }
    }

    @FXML
    void onEdit(ActionEvent event) throws NoSuchAlgorithmException, Exception {
        if (selectStudent != null) {
            if (TextName.getText().isEmpty() || TextLast_name.getText().isEmpty()
                    || TextCi.getText().isEmpty() || TimeDateBirth.getValue() == null
                    || TextEmail.getText().isEmpty() || TextAddress.getText().isEmpty()) {

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Todos los campos deben ser llenados.");
                alert.initStyle(StageStyle.UTILITY);
                alert.showAndWait();
                return;
            }
            selectStudent.setNombre(TextName.getText());
            selectStudent.setApellido(TextLast_name.getText());
            selectStudent.setDireccion(TextAddress.getText());
            selectStudent.setCorreo(TextEmail.getText());
            selectStudent.setCedula_identidad(TextCi.getText());
            selectStudent.setGenero(CboGender.getSelectionModel().getSelectedIndex());
            selectStudent.setFecha_nacimiento(Date.valueOf(TimeDateBirth.getValue())); // convertir LocalDate

            boolean success = studentdao.Edit(selectStudent);

            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Estudiante modificado con éxito");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error al modificar estudiante");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void clearFields() {
        TextName.clear();
        TextLast_name.clear();
        TextAddress.clear();
        TimeDateBirth.setValue(null);
        TextEmail.clear();
        TextCi.clear();
        CboGender.getSelectionModel().clearSelection();
        CboTutor.getSelectionModel().clearSelection();
        CboTutor1.getSelectionModel().clearSelection();
        TextRelacion.clear();
        TextRelacion1.clear();
    }

    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    public static boolean VerifyEmailUser(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void selectTutor1ById(int idTutor) {
        for (Guardian tutor : listaTutores) {
            if (tutor.getId() == idTutor) {
                CboTutor.setValue(tutor);
                break;
            }
        }
    }

    public void selectTutor1ById2(int idTutor) {
        for (Guardian tutor : listaTutores) {
            if (tutor.getId() == idTutor) {
                CboTutor1.setValue(tutor);
                break;
            }
        }
    }

    public void imprimirItemsComboBox(ComboBox<Guardian> comboBox) {
        ObservableList<Guardian> items = comboBox.getItems();

        System.out.println("== Elementos del ComboBox ==");
        if (items == null || items.isEmpty()) {
            System.out.println("vacío");
        } else {
            for (Guardian tutor : items) {
                System.out.println(tutor.getNombre() + " " + tutor.getApellido());
            }
        }
        System.out.println("============================");
    }

    @FXML
    void onEnrollment(ActionEvent event) throws NoSuchAlgorithmException, Exception {
        if (TextRude.getText().isEmpty() || CboCurso.getValue() == null || TextObs.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Todos los campos deben ser llenados.");
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();
            return;
        } else {

            Enrollment inscripcion = new Enrollment();

            inscripcion.setId_estudiante(ultimoIdRegistrado);
            inscripcion.setId_curso(CboCurso.getValue().getIdcurso());
            inscripcion.setId_usuario(mainController.logged.getId());
            inscripcion.setFecha_inscripcion(java.sql.Date.valueOf(LocalDate.now()));
            inscripcion.setYear(LocalDate.now().getYear());
            inscripcion.setEstado(1);
            inscripcion.setRude(Integer.parseInt(TextRude.getText()));
            inscripcion.setObservacion(TextObs.getText());

            int success = enrollmentDao.register(inscripcion);

            if (success > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Éxito");
                alert.setHeaderText(null);
                alert.setContentText("¡Inscripción realizada correctamente!");
                alert.initStyle(StageStyle.UTILITY);
                alert.showAndWait();
                clearFields();
                fueInscrito = true;
                guardarEstado();
                //llamar a crear docuemntacion
                submittedDocumentDao.register(success);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Error al registrar la inscripción.");
                alert.initStyle(StageStyle.UTILITY);
                alert.showAndWait();
            }

        }

    }

    private String getNombreNivel(int nivel) {
        return switch (nivel) {
            case 0 ->
                "Primaria";
            case 1 ->
                "Secundaria";
            default ->
                "Desconocido";
        };
    }

    private String getNombreGrado(int grado) {
        return switch (grado) {
            case 0 ->
                "Primero";
            case 1 ->
                "Segundo";
            case 2 ->
                "Tercero";
            case 3 ->
                "Cuarto";
            case 4 ->
                "Quinto";
            case 5 ->
                "Sexto";
            default ->
                "Desconocido";
        };
    }

    public void guardarEstado() {
        String contenido = ultimoIdRegistrado + "\n" + fueInscrito;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("estado_estudiante.txt"))) {
            writer.write(contenido);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cargarEstado() {
        File archivo = new File("estado_estudiante.txt");
        if (archivo.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
                ultimoIdRegistrado = Integer.parseInt(reader.readLine());
                fueInscrito = Boolean.parseBoolean(reader.readLine());
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
                // si hay error, valores por defecto
                ultimoIdRegistrado = 0;
                fueInscrito = true;
            }
        }
    }
}
