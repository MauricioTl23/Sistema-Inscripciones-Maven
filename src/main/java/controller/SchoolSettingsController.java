/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import Dao.AdvisorDao;
import Dao.CourseDao;
import Dao.DocumentationDao;
import Dao.Subject_courseDao;
import Dao.UserDao;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import model.Advisor;
import model.Course;
import model.Documentation;
import model.Extras;
import model.Subject_course;

/**
 *
 * @author mauricioteranlimari
 */
public class SchoolSettingsController implements Initializable {
    
    @FXML
    private VBox MainVBox;

    @FXML
    private ComboBox<String> CboxGradeCourse;

    @FXML
    private ComboBox<String> CboxLevelCourse;

    @FXML
    private RadioButton RdNoO;

    @FXML
    private RadioButton RdYesO;

    @FXML
    private RadioButton RdNoCC;

    @FXML
    private RadioButton RdYesCC;

    @FXML
    private RadioButton RdNoAN;

    @FXML
    private RadioButton RdYesAN;

    @FXML
    private ComboBox<String> CboxSelect;

    @FXML
    private TableView<Course> TableCourse;

    @FXML
    private TableView<Documentation> TableDocumentation;

    @FXML
    private TextField TextNameDocumentation;

    @FXML
    private TextField TextPalallel;

    @FXML
    private TextField TextQuota;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnSave;

    @FXML
    private ComboBox<String> cbxA;

    @FXML
    private ComboBox<String> cbxC;

    @FXML
    private DatePicker dpF;

    @FXML
    private DatePicker dpI;

    @FXML
    private Button btnAsesor;

    @FXML
    private ComboBox<String> cbxBgrades;

    @FXML
    private TextField textBdocument;

    @FXML
    private TextField textBparallel;
    
    @FXML
    private Rectangle Rectangle1;

    @FXML
    private Rectangle Rectangle2;

    @FXML
    private Rectangle Rectangle3;

    @FXML
    private Rectangle Rectangle4;
    
     @FXML
    private StackPane stack1;

    @FXML
    private StackPane stack2;

    @FXML
    private StackPane stack3;

    @FXML
    private StackPane stack4;

    private CourseDao coursedao;
    private UserDao userdao;
    private DocumentationDao documentationdao;
    private Subject_courseDao scoursedao;
    private AdvisorDao advisordao;
    private char parallel;
    private ContextMenu CourseOptions;
    private ContextMenu DocumentationOptions;
    private Course courseselect;
    private Documentation documentationselect;
    private String advisorN;
    private FilteredList<Course> filteredDataC;

    private String gradeFilter = "";
    private String parallelFilter = "";

    private FilteredList<Documentation> filteredDataD;

    private ObservableList<String> observableListA;

    private ObservableList<String> observableListC;

    public String[] options = {"Gestionar Curso", "Gestionar Documentacion"};

    public String[] optionsLevel = {"Primaria", "Secundaria"};

    public String[] optionsGrade = {"Primero", "Segundo", "Tercero", "Cuarto", "Quinto", "Sexto"};

    //private Extras extras = new Extras();
    //Combo box para gestionar Curso o Documentacion
    @FXML
    void setOnAction(ActionEvent event) {
        //Seleccion del combo box Curso o Documentacion
        String seleccion = CboxSelect.getValue();
        if (seleccion != null) {
            switch (seleccion) {
                case "Gestionar Curso" -> {
                    cleanFieldsCourse();
                    cleanAdvisor();
                    enableCourseField();
                    enableadvisor();

                    cleanFieldsDocumentation();
                    disableDocumentationField();

                    cbxBgrades.setValue("Seleccione");
                    textBparallel.clear();
                    textBdocument.clear();
                }
                case "Gestionar Documentacion" -> {
                    cleanFieldsDocumentation();
                    enableDocumentationField();

                    cleanFieldsCourse();
                    cleanAdvisor();

                    disableCourseField();
                    disableadvisor();

                    cbxBgrades.setValue("Seleccione");
                    textBparallel.clear();
                    textBdocument.clear();
                }
                default ->
                    diseble();
            }
        } else {
            diseble();
        }
    }

    //Boton cancelar
    @FXML
    void BtnCancelarOnAction(ActionEvent event) {

        if (courseselect != null) {
            courseselect = null;
            cleanFieldsDocumentation();

            cleanFieldsCourse();
            cleanAdvisor();

            enableCourseField();
            enableadvisor();

            btnCancelar.setDisable(true);
        } else if (documentationselect != null) {
            documentationselect = null;

            cleanFieldsDocumentation();

            cleanAdvisor();
            cleanFieldsCourse();

            enableDocumentationField();
            btnCancelar.setDisable(true);
        } else if (cbxA.getSelectionModel().getSelectedIndex() != -1) {
            cleanFieldsDocumentation();
            cleanFieldsCourse();
            cleanAdvisor();

            enableadvisor();
            enableCourseField();
            btnCancelar.setDisable(true);
        }

    }

    @FXML
    void btnSaveOnAction(ActionEvent event) throws ClassNotFoundException {

        //Controloa que se realizara es decir curso o documentacion
        String selected = CboxSelect.getValue();

        if (selected == null) {

            Extras.showAlert("Advertencia", "Debe Seleccionar una opcion", Alert.AlertType.WARNING);

        } else if (selected.equals("Gestionar Curso")) {

            if (courseselect == null) {

                Course course = new Course();

                //Seleccion del tipo de nivel Primaria o Secundaria
                if (CboxLevelCourse.getValue().equals("Primaria")) {
                    course.setNivel(0);
                } else {
                    course.setNivel(1);
                }
                //Seleccion de Grado de un Curso
                switch (CboxGradeCourse.getValue()) {
                    case "Primero" ->
                        course.setGrado(0);
                    case "Segundo" ->
                        course.setGrado(1);
                    case "Tercero" ->
                        course.setGrado(2);
                    case "Cuarto" ->
                        course.setGrado(3);
                    case "Quinto" ->
                        course.setGrado(4);
                    case "Sexto" ->
                        course.setGrado(5);
                    default -> {
                    }
                }
                //Campo para verificar el campo de Parallelo Automatico
                if (CboxLevelCourse.getSelectionModel().getSelectedIndex() == -1 || CboxGradeCourse.getSelectionModel().getSelectedIndex() == -1) {

                    Extras.showAlert("Advertencia", "Los campos no pueden estar vacios", Alert.AlertType.WARNING);
                    return;

                } else if (CboxLevelCourse.getSelectionModel().getSelectedIndex() != -1 && CboxGradeCourse.getSelectionModel().getSelectedIndex() != -1) {
                    parallel = coursedao.reeturnParallel(CboxLevelCourse.getSelectionModel().getSelectedIndex(), CboxGradeCourse.getSelectionModel().getSelectedIndex());
                    switch (parallel) {
                        case 'A' -> {
                            parallel = 'A';
                            course.setParalelo(parallel);
                        }
                        case 'Z' -> {
                            Extras.showAlert("Advertencia", "Maximo de cursos permitido", Alert.AlertType.WARNING);
                            return;
                        }
                        default -> {
                            parallel++;
                            course.setParalelo(parallel);
                        }
                    }
                }
                //Llenado de datos a la Base de Datos
                boolean resp = this.coursedao.register(course);
                if (resp) {
                    Extras.showAlert("Exito", "Se registro correctamente el curso", Alert.AlertType.INFORMATION);
                    //Una vez creado el curso se agrega las materias ya definida
                    int tr = CboxGradeCourse.getSelectionModel().getSelectedIndex();
                    if (tr == 0 || tr == 1 || tr == 2) {
                        addMaterialsForGrade(13);
                    } else if (tr == 3 || tr == 4 || tr == 5) {
                        addMaterialsForGrade(12);
                    }

                    //Actualizar los campos y limpiarlos
                    cleanFieldsCourse();
                    enableCourseField();

                    cleanFieldsDocumentation();
                    cleanAdvisor();

                    UploadCourses();

                } else {

                    Extras.showAlert("Error", "Hubo un error", Alert.AlertType.ERROR);

                }
            } else {

                if (courseselect.getNivel() == CboxLevelCourse.getSelectionModel().getSelectedIndex() && courseselect.getGrado() == CboxGradeCourse.getSelectionModel().getSelectedIndex() && String.valueOf(courseselect.getParalelo()).equals(TextPalallel.getText())) {

                    Extras.showAlert("Advertencia", "No se pueden guardar los mismos datos", Alert.AlertType.WARNING);

                } else {

                    //Editar
                    courseselect.setNivel(CboxLevelCourse.getSelectionModel().getSelectedIndex());
                    courseselect.setGrado(CboxGradeCourse.getSelectionModel().getSelectedIndex());

                    if (TextPalallel.getText() != null && TextPalallel.getText().matches("[A-Z]")) {
                        courseselect.setParalelo(TextPalallel.getText().charAt(0));
                    } else {
                        Extras.showAlert("Advertencia", "Debe ser un paralelo valido", Alert.AlertType.WARNING);
                        return;
                    }
                    //Guardar en la Base de Datos los cambios
                    boolean rsp = this.coursedao.editCourse(courseselect);

                    if (rsp) {
                        Extras.showAlert("Exito", "Se guardo correctamente el curso", Alert.AlertType.INFORMATION);

                        cleanFieldsCourse();
                        enableCourseField();

                        cleanFieldsDocumentation();
                        cleanAdvisor();

                        UploadCourses();

                        courseselect = null;

                        btnCancelar.setDisable(true);

                    } else {
                        Extras.showAlert("Error", "Hubo un error", Alert.AlertType.ERROR);
                    }
                }
            }
        } else if (selected.equals("Gestionar Documentacion")) {
            //Verifica si es para guardar o editar
            if (documentationselect == null) {

                if (TextNameDocumentation.getText().isEmpty() || (!RdYesO.isSelected() && !RdNoO.isSelected()) || (!RdYesCC.isSelected() && !RdNoCC.isSelected())) {

                    Extras.showAlert("Advertencia", "Los campos no pueden estar vacios", Alert.AlertType.WARNING);
                    return;

                }

                Documentation documentation = new Documentation();

                //Nombre de la Documentacion
                documentation.setNombre(TextNameDocumentation.getText());

                //Radio button para el documento es obligatorio
                if (RdYesO.isSelected()) {
                    documentation.setObligatorio(true);
                } else if (RdNoO.isSelected()) {
                    documentation.setObligatorio(false);
                } else {
                    Extras.showAlert("Advertencia", "No puede estar vacio", Alert.AlertType.WARNING);
                }
                //Radio button para el documento si requiere carta compromiso
                if (RdYesCC.isSelected()) {
                    documentation.setCartacompromiso(true);
                } else if (RdNoCC.isSelected()) {
                    documentation.setCartacompromiso(false);
                } else {
                    Extras.showAlert("Advertencia", "No puede estar vacio", Alert.AlertType.WARNING);
                }

                //Guardar en la base de datos
                boolean resp = this.documentationdao.register(documentation);

                if (resp) {

                    Extras.showAlert("Exito", "Se registro correcto el documento", Alert.AlertType.INFORMATION);

                    //Actualizar y limpiar campos
                    cleanFieldsDocumentation();
                    enableDocumentationField();

                    cleanFieldsCourse();
                    cleanAdvisor();

                    LoadDocumentation();

                } else {
                    Extras.showAlert("Error", "Hubo un error", Alert.AlertType.ERROR);
                }
            } else {
                //Verifica las respuestas son las mismas que las seleccionadas
                boolean rspRd1;
                boolean rspRd2;

                if (RdYesO.isSelected()) {
                    rspRd1 = true;
                } else {
                    rspRd1 = false;
                }

                if (RdYesCC.isSelected()) {
                    rspRd2 = true;
                } else {
                    rspRd2 = false;
                }

                if (documentationselect.getNombre().equals(TextNameDocumentation.getText()) && documentationselect.isObligatorio() == rspRd1
                        && documentationselect.isCartacompromiso() == rspRd2) {

                    Extras.showAlert("Advertencia", "No se pueden guardar los mismos datos", Alert.AlertType.WARNING);

                } else {
                    //Nombre documento
                    documentationselect.setNombre(TextNameDocumentation.getText());
                    //Radio Button es obligatorio
                    if (RdYesO.isSelected()) {
                        documentationselect.setObligatorio(true);
                    } else if (RdNoO.isSelected()) {
                        documentationselect.setObligatorio(false);
                    } else {
                        Extras.showAlert("Advertencia", "No puede estar vacio", Alert.AlertType.WARNING);
                    }
                    //Radio Button requiere carta compromiso
                    if (RdYesCC.isSelected()) {
                        documentationselect.setCartacompromiso(true);
                    } else if (RdNoCC.isSelected()) {
                        documentationselect.setCartacompromiso(false);
                    } else {
                        Extras.showAlert("Advertencia", "No puede estar vacio", Alert.AlertType.WARNING);
                    }

                    //Guardar en la base de datos
                    boolean resp = this.documentationdao.editDocumentation(documentationselect);
                    if (resp) {

                        Extras.showAlert("Exito", "Se guardo correctamente el documento", Alert.AlertType.INFORMATION);
                        //Actualizar y limpiar campos
                        cleanFieldsDocumentation();
                        enableDocumentationField();

                        cleanFieldsCourse();
                        cleanAdvisor();

                        documentationselect = null;

                        btnCancelar.setDisable(true);

                        LoadDocumentation();
                    } else {
                        Extras.showAlert("Error", "Hubo un error", Alert.AlertType.ERROR);
                    }
                }
            }
        }

    }

    @FXML
    void btnAddAsesor(ActionEvent event) {

        if (courseselect == null) {

            if (cbxA.getSelectionModel().getSelectedIndex() != -1 && cbxC.getSelectionModel().getSelectedIndex() != -1) {

                Advisor advisor = new Advisor();

                //id del asesor
                advisor.setIdusuario(userdao.idasesor(cbxA.getSelectionModel().getSelectedItem()));

                //id del curso
                advisor.setIdcurso(coursedao.idcourse(verifylevel(cbxC.getSelectionModel().getSelectedItem()), verifycourse(cbxC.getSelectionModel().getSelectedItem())));

                //fecha inicio
                if (dpI.getValue().isBefore(LocalDate.now())) {
                    Extras.showAlert("Advertencia", "Fecha invalida, no debe ser anterior a la fecha actual", Alert.AlertType.WARNING);
                    return;
                } else {
                    advisor.setFechainicio(dpI.getValue());
                }

                //fecha fin
                if (dpF.getValue().isBefore(LocalDate.now())) {
                    Extras.showAlert("Advertencia", "Fecha invalida, no debe ser anterior a la fecha actual", Alert.AlertType.WARNING);
                    return;
                } else {
                    advisor.setFechafin(dpF.getValue());
                }

                boolean rsp = this.advisordao.register(advisor);
                if (rsp) {

                    Extras.showAlert("Exito", "Se registro correctamente el asesor", Alert.AlertType.INFORMATION);

                    cleanFieldsCourse();
                    cleanAdvisor();
                    enableCourseField();
                    enableadvisor();
                    UploadCourses();
                    UpdateAdversors();

                } else {
                    Extras.showAlert("Error", "Hugo un error al guardar", Alert.AlertType.ERROR);

                }

            } else {
                Extras.showAlert("Advertencia", "Deben estar los campos seleccionados", Alert.AlertType.WARNING);
            }
        } else {

            if (dpF.getValue().isBefore(LocalDate.now())) {
                Extras.showAlert("Advertencia", "No puede ser una fecha menor a la actual", Alert.AlertType.WARNING);
            } else {

                advisorN = cbxA.getSelectionModel().getSelectedItem();

                if (advisorN.equals(courseselect.getAsesor()) && dpF.getValue().equals(courseselect.getFechaf())) {
                    Extras.showAlert("Advertencia", "No se puede guardar la misma informacion", Alert.AlertType.WARNING);
                } else {

                    boolean rsp;
                    rsp = advisordao.Edit(userdao.idasesor(advisorN), dpF.getValue(), coursedao.idcourse(verifylevel(cbxC.getSelectionModel().getSelectedItem()), verifycourse(cbxC.getSelectionModel().getSelectedItem())));

                    if (rsp) {
                        Extras.showAlert("Exito", "Se guardo correctamente el asesor", Alert.AlertType.INFORMATION);

                        cleanFieldsCourse();
                        cleanAdvisor();
                        enableCourseField();
                        enableadvisor();
                        UploadCourses();
                        UpdateAdversors();

                        courseselect = null;

                    } else {

                        Extras.showAlert("Error", "Hubo un error al modificar", Alert.AlertType.ERROR);

                    }
                }
            }
        }
    }

    //Funcion para cambiar el string de un curso a los valores de la base de datos
    public String verifycourse(String full) {
        full = full.replaceAll("\\s*\\(.*?\\)", "").trim();
        for (int i = 0; i < optionsGrade.length; i++) {
            if (full.contains(optionsGrade[i])) {
                return full.replace(optionsGrade[i], String.valueOf(i));
            }
        }
        return null;
    }

    public int verifylevel(String full) {
        if (full == null) {
            return -1;
        }
        full = full.toLowerCase();

        if (full.contains("(primaria)")) {
            return 0;
        } else if (full.contains("(secundaria)")) {
            return 1;
        } else {
            return -1;
        }

    }

    //Funcion para añadir las materias a los distintos cursos
    private void addMaterialsForGrade(int numberOfSubjects) {
        int idcurso = coursedao.returnIdcurso();
        List<Subject_course> list = new ArrayList<>();

        for (int i = 1; i <= numberOfSubjects; i++) {
            Subject_course addmaterial = new Subject_course();
            addmaterial.setId_materia(i);
            addmaterial.setId_curso(idcurso);
            list.add(addmaterial);
        }

        boolean res = this.scoursedao.addMaterial(list);
        if (res) {
            System.out.println("Se registraron las materias correctamente");
        } else {
            Extras.showAlert("Error", "Hubo un error", Alert.AlertType.ERROR);
        }
    }

    //Funcion para limpiar los campos de curso 
    private void cleanFieldsCourse() {

        TextPalallel.clear();
        TextQuota.clear();
        RdYesAN.setSelected(false);
        RdNoAN.setSelected(false);
        CboxLevelCourse.getSelectionModel().select("Seleccione");
        CboxGradeCourse.getSelectionModel().select("Seleccione");
        btnCancelar.setDisable(true);
        cbxBgrades.getSelectionModel().select("Seleccione");
        textBparallel.clear();

    }

    //Funcion para limpiar los campos de asesor
    private void cleanAdvisor() {

        cbxA.getSelectionModel().select("Seleccione");
        cbxC.getSelectionModel().select("Seleccione");
        dpI.setValue(LocalDate.now());
        dpF.setValue(LocalDate.now().plusYears(1));
        btnAsesor.setDisable(true);
    }

    //Funcion para limpiar los campos de documentacion
    private void cleanFieldsDocumentation() {

        TextNameDocumentation.clear();
        RdYesO.setSelected(false);
        RdNoO.setSelected(false);
        RdYesCC.setSelected(false);
        RdNoCC.setSelected(false);
        btnCancelar.setDisable(true);
        textBdocument.clear();
    }

    //Cargar documentacion
    private void LoadDocumentation() {

        //TableDocumentation.getItems().clear();
        //TableDocumentation.getColumns().clear();
        //Crea la tabla
        List<Documentation> documentations = this.documentationdao.toList();

        ObservableList<Documentation> data = FXCollections.observableArrayList(documentations);

        //Para la busqueda por filtros
        filteredDataD = new FilteredList<>(data, p -> true);
        TableDocumentation.setItems(filteredDataD);

        TableColumn iddocumenttypeCol = new TableColumn("ID");

        iddocumenttypeCol.setCellValueFactory(new PropertyValueFactory("idtipo_documento"));

        TableColumn nameCol = new TableColumn("NOMBRE");

        nameCol.setCellValueFactory(new PropertyValueFactory("nombre"));

        TableColumn compulsoryCol = new TableColumn("OBLIGATORIO");

        compulsoryCol.setCellValueFactory(new PropertyValueFactory("obligatorio"));

        compulsoryCol.setCellFactory(col -> new TableCell<Course, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {

                    String[] level = {"NO", "SI"};
                    setText(level[item ? 1 : 0]);

                }
            }
        });

        TableColumn commitmentletterCol = new TableColumn("CARTA COMPROMISO");

        commitmentletterCol.setCellValueFactory(new PropertyValueFactory("cartacompromiso"));

        commitmentletterCol.setCellFactory(col -> new TableCell<Course, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {

                    String[] level = {"NO", "SI"};
                    setText(level[item ? 1 : 0]);

                }
            }
        });

        //TableDocumentation.setItems(dataD);
        TableDocumentation.getColumns().addAll(iddocumenttypeCol, nameCol, compulsoryCol, commitmentletterCol);
    }

    //Cargar Cursos
    public void UploadCourses() {

        //TableCourse.getItems().clear();
        //TableCourse.getColumns().clear();
        //Crear la Tabla
        List<Course> courses = this.coursedao.toList();

        ObservableList<Course> data = FXCollections.observableArrayList(courses);

        //Filtrar para la busqueda
        filteredDataC = new FilteredList<>(data, p -> true);
        TableCourse.setItems(filteredDataC);

        TableColumn idcourseCol = new TableColumn("ID");

        idcourseCol.setCellValueFactory(new PropertyValueFactory("idcurso"));

        TableColumn levelCol = new TableColumn("NIVEL");

        levelCol.setCellValueFactory(new PropertyValueFactory("nivel"));
        levelCol.setCellFactory(col -> new TableCell<Course, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {

                    String[] level = {"Primaria", "Secundaria"};
                    setText(level[item]);

                }
            }
        });

        TableColumn gradeCol = new TableColumn("GRADO");

        gradeCol.setCellValueFactory(new PropertyValueFactory("grado"));

        gradeCol.setCellFactory(col -> new TableCell<Course, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {

                    String[] level = {"Primero", "Segundo", "Tercero", "Cuarto", "Quinto", "Sexto"};
                    setText(level[item]);

                }
            }
        });

        TableColumn parallelCol = new TableColumn("PARALELO");

        parallelCol.setCellValueFactory(new PropertyValueFactory("paralelo"));

        TableColumn advisorsCol = new TableColumn("ASESOR");

        advisorsCol.setCellValueFactory(new PropertyValueFactory("asesor"));

        TableColumn startdateCol = new TableColumn("FECHA INICIO");

        startdateCol.setCellValueFactory(new PropertyValueFactory("fechai"));

        TableColumn enddateCol = new TableColumn("FECHA FIN");

        enddateCol.setCellValueFactory(new PropertyValueFactory("fechaf"));

        TableColumn quotaCol = new TableColumn("CUPO");

        quotaCol.setCellValueFactory(new PropertyValueFactory("cupo_max"));

        TableColumn admitsnewCol = new TableColumn("ADMITE NUEVOS");

        admitsnewCol.setCellValueFactory(new PropertyValueFactory("admite_nuevos"));

        admitsnewCol.setCellFactory(col -> new TableCell<Course, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {

                    String[] level = {"NO", "SI"};
                    setText(level[item ? 1 : 0]);

                }
            }
        });

        //TableCourse.setItems(data);
        TableCourse.getColumns().addAll(idcourseCol, levelCol, gradeCol, parallelCol, advisorsCol, startdateCol, enddateCol, quotaCol, admitsnewCol);

    }

    //Funcion para la busqueda de el ultimo paralelo y si llega a la Z ya no lo permite
    private char updateText() {
        char prueba = 'A';
        if (CboxLevelCourse.getSelectionModel().getSelectedIndex() != -1 && CboxGradeCourse.getSelectionModel().getSelectedIndex() != -1) {
            prueba = coursedao.reeturnParallel(CboxLevelCourse.getSelectionModel().getSelectedIndex(), CboxGradeCourse.getSelectionModel().getSelectedIndex());

            switch (prueba) {
                case 'A' -> {
                    TextPalallel.setText(String.valueOf(prueba));
                }
                case 'Z' ->
                    Extras.showAlert("Error", "Maximo de cursos permitido", Alert.AlertType.ERROR);
                default -> {
                    prueba++;
                    TextPalallel.setText(String.valueOf(prueba));
                }
            }
        }
        //System.out.println("PRUEBA: "+prueba);
        return prueba;
    }

    //Funcion para desactivar todos los campos
    private void diseble() {
        //Curso
        CboxLevelCourse.setDisable(true);
        CboxGradeCourse.setDisable(true);
        TextPalallel.setDisable(true);
        TextQuota.setDisable(true);
        TableCourse.setDisable(true);
        //Asesor
        cbxA.setDisable(true);
        cbxC.setDisable(true);
        dpI.setDisable(true);
        dpF.setDisable(true);
        btnAsesor.setDisable(true);
        //Documentacion
        TextNameDocumentation.setDisable(true);
        RdNoO.setDisable(true);
        RdYesO.setDisable(true);
        RdNoCC.setDisable(true);
        RdYesCC.setDisable(true);
        RdNoAN.setDisable(true);
        RdYesAN.setDisable(true);
        TableDocumentation.setDisable(true);
        //Boton Guardar y Cancelar
        btnSave.setDisable(true);
        btnCancelar.setDisable(true);
        //Combo Box General
        CboxSelect.getSelectionModel().select("Seleccione");
        //Campos para las busquedas
        textBdocument.setDisable(true);
        textBparallel.setDisable(true);
        cbxBgrades.setDisable(true);
    }

    //Funcion para controlar los asesores 
    private void UpdateAdversors() {

        this.observableListA = FXCollections.observableArrayList(userdao.Advisors());

        if (observableListA.isEmpty()) {
            cbxA.setItems(FXCollections.observableArrayList("Sin datos"));
            cbxA.setValue("Sin datos");
            cbxA.setDisable(true);
        } else {
            cbxA.setItems(observableListA);
            cbxA.setValue("Seleccione");
        }
    }

    //Funcion para controlar los cursos
    private void UpdateCourses() {

        if (cbxA.getSelectionModel().isEmpty()) {
            this.observableListC = FXCollections.observableArrayList(coursedao.CoursesAdvisors());
        } else {
            this.observableListC = FXCollections.observableArrayList(coursedao.CoursesAdvisorsC(userdao.idasesor(cbxA.getSelectionModel().getSelectedItem())));
        }

        if (observableListC.isEmpty()) {
            cbxC.setItems(FXCollections.observableArrayList("Sin datos"));
            cbxC.setValue("Sin datos");
            cbxC.setDisable(true);
        } else {
            cbxC.setItems(observableListC);
            cbxC.setValue("Seleccione");
        }
    }

    //Funcion para habilitar los campos de curso
    private void enableCourseField() {
        //Combo box de Nivel y Curso 
        CboxLevelCourse.setDisable(false);
        CboxGradeCourse.setDisable(false);
        //Texto Paralelo y Text CupoMax
        TextPalallel.setDisable(false);
        TextPalallel.setEditable(false);
        TextQuota.setDisable(false);
        TextQuota.setEditable(false);
        //Radio buton Admite nuevos
        RdNoAN.setDisable(true);
        RdYesAN.setDisable(true);
        RdYesAN.setSelected(true);
        //Tabla curso
        TableCourse.setDisable(false);
        btnSave.setDisable(false);
        //Busqueda
        cbxBgrades.setDisable(false);
        textBparallel.setDisable(false);
    }

    //Funcion para habilitar los campos de asesor
    private void enableadvisor() {
        //Asesor
        cbxA.setDisable(false);
        cbxC.setDisable(false);
        dpI.setDisable(false);
        dpI.setEditable(false);
        dpI.setValue(LocalDate.now());
        dpF.setDisable(false);
        dpF.setValue(LocalDate.now().plusYears(1));
        btnAsesor.setDisable(false);
        //Tabla
        TableCourse.setDisable(false);
        //Busqueda
        cbxBgrades.setDisable(false);
        textBparallel.setDisable(false);
    }

    //Funcion para Desabilitar los campos de curso
    private void disableCourseField() {
        //Combo box de Nivel y Curso 
        CboxLevelCourse.setDisable(true);
        CboxGradeCourse.setDisable(true);
        //Texto Paralelo y Text CupoMax
        TextPalallel.setDisable(true);
        TextQuota.setDisable(true);
        //Tabla curso
        TableCourse.setDisable(true);
        //Radio buton
        RdNoAN.setDisable(true);
        RdYesAN.setDisable(true);
        //Busqueda
        cbxBgrades.setDisable(true);
        textBparallel.setDisable(true);
    }

    //Funcio para desabilitar los campos de asesor
    private void disableadvisor() {
        //Asesor
        cbxA.setDisable(true);
        cbxC.setDisable(true);
        dpI.setDisable(true);
        dpF.setDisable(true);
        btnAsesor.setDisable(true);
        //Tabla
        TableCourse.setDisable(true);
        //Busqueda
        cbxBgrades.setDisable(true);
        textBparallel.setDisable(true);
    }

    //Funcion para habilitar los campos de documentacion
    private void enableDocumentationField() {

        //Text nombre documento
        TextNameDocumentation.setEditable(true);
        TextNameDocumentation.setDisable(false);
        //Radio buton para ver si el documento es obligatorio
        RdNoO.setDisable(false);
        RdYesO.setDisable(false);
        //Radio buton para ver si el documento tiene carta compromiso
        RdNoCC.setDisable(false);
        RdYesCC.setDisable(false);
        TableDocumentation.setDisable(false);
        btnSave.setDisable(false);
        //Busqueda
        textBdocument.setDisable(false);
    }

    //Funcion para desabilitar los campo de documentacion
    private void disableDocumentationField() {
        //Text nombre documento
        TextNameDocumentation.setDisable(true);
        //Radio buton para ver si el documento es obligatorio
        RdYesO.setDisable(true);
        RdNoO.setDisable(true);
        //Radio buton para ver si el documento tiene carta compromiso
        RdNoCC.setDisable(true);
        RdYesCC.setDisable(true);
        //Tabla documentacio
        TableDocumentation.setDisable(true);
        //Busqueda
        textBdocument.setDisable(true);
    }

    //Funcion para filtrar busqueda de la tabla curso
    private void aplicarFiltro() {
        filteredDataC.setPredicate(course -> {
            boolean matchesGrade = true;
            boolean matchesParallel = true;

            if (gradeFilter != null && !gradeFilter.isEmpty()) {
                matchesGrade = String.valueOf(course.getGrado())
                        .toLowerCase()
                        .contains(gradeFilter.toLowerCase());
            }

            if (parallelFilter != null && !parallelFilter.isEmpty()) {
                matchesParallel = String.valueOf(course.getParalelo())
                        .toLowerCase()
                        .contains(parallelFilter.toLowerCase());
            }

            return matchesGrade && matchesParallel;
        });
    }

    //Metodo Initialize
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        stack1.prefHeightProperty().bind(MainVBox.heightProperty().multiply(1.0 / 10.0));
        stack2.prefHeightProperty().bind(MainVBox.heightProperty().multiply(4.0 / 10.0));
        stack3.prefHeightProperty().bind(MainVBox.heightProperty().multiply(4.0 / 10.0));
        stack4.prefHeightProperty().bind(MainVBox.heightProperty().multiply(1.0 / 10.0));
        
        stack1.prefWidthProperty().bind(MainVBox.widthProperty());
        stack2.prefWidthProperty().bind(MainVBox.widthProperty());
        stack3.prefWidthProperty().bind(MainVBox.widthProperty());
        stack4.prefWidthProperty().bind(MainVBox.widthProperty());
        
        Rectangle1.widthProperty().bind(stack1.widthProperty());
        Rectangle1.heightProperty().bind(stack1.heightProperty());

        Rectangle2.widthProperty().bind(stack2.widthProperty());
        Rectangle2.heightProperty().bind(stack2.heightProperty());
        
        Rectangle3.widthProperty().bind(stack3.widthProperty());
        Rectangle3.heightProperty().bind(stack3.heightProperty());
        
        Rectangle4.widthProperty().bind(stack4.widthProperty());
        Rectangle4.heightProperty().bind(stack4.heightProperty());

        diseble();

        ObservableList<String> items = FXCollections.observableArrayList(this.options);

        CboxSelect.setItems(items);
        CboxSelect.setValue("Seleccione");

        ObservableList<String> itemsLevel = FXCollections.observableArrayList(this.optionsLevel);

        CboxLevelCourse.setItems(itemsLevel);
        CboxLevelCourse.setValue("Seleccione");

        ObservableList<String> itemsGrade = FXCollections.observableArrayList(this.optionsGrade);

        CboxGradeCourse.setItems(itemsGrade);
        CboxGradeCourse.setValue("Seleccione");

        cbxBgrades.setItems(itemsGrade);
        cbxBgrades.setValue("Seleccione");

        ToggleGroup group0 = new ToggleGroup();

        RdYesO.setToggleGroup(group0);
        RdNoO.setToggleGroup(group0);

        ToggleGroup group1 = new ToggleGroup();
        RdYesCC.setToggleGroup(group1);
        RdNoCC.setToggleGroup(group1);

        ToggleGroup group2 = new ToggleGroup();
        RdYesAN.setToggleGroup(group2);
        RdNoAN.setToggleGroup(group2);

        dpI.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                dpF.setValue(newDate.plusYears(1));
            }
        });

        try {
            this.coursedao = new CourseDao();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SchoolSettingsController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(SchoolSettingsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            this.documentationdao = new DocumentationDao();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SchoolSettingsController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(SchoolSettingsController.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            this.scoursedao = new Subject_courseDao();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SchoolSettingsController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(SchoolSettingsController.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            this.userdao = new UserDao();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SchoolSettingsController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(SchoolSettingsController.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            this.advisordao = new AdvisorDao();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SchoolSettingsController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(SchoolSettingsController.class.getName()).log(Level.SEVERE, null, ex);
        }

        //lista de asesores
        UpdateAdversors();

        //lista de los cursos
        UpdateCourses();

        TextNameDocumentation.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() > 100) {
                TextNameDocumentation.setText(oldText);
            }
        });

        textBparallel.setTextFormatter(new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();

            if (newText.matches("[A-Z]?")) {
                return change;
            } else {
                Extras.showAlert("Advertencia", "Solo paralelos validos", Alert.AlertType.WARNING);
                textBparallel.clear();
            }
            return null;
        }));

        cbxA.setOnAction(event -> {
            cleanFieldsCourse();
            disableCourseField();
            enableadvisor();
            UpdateCourses();

        });
        CboxLevelCourse.setOnAction(event -> {
            cleanAdvisor();
            disableadvisor();
            enableCourseField();
        });

        CboxLevelCourse.setOnAction(event -> updateText());
        CboxGradeCourse.setOnAction(event -> updateText());

        UploadCourses();
        LoadDocumentation();

        //CURSO
        CourseOptions = new ContextMenu();

        MenuItem EditCourse = new MenuItem("Editar Curso");
        MenuItem DeleteCourse = new MenuItem("Eliminar Curso");
        MenuItem EditAdviser = new MenuItem("Cambiar Asesor");
        MenuItem DeleteAdviser = new MenuItem("Eliminar Asesor");

        CourseOptions.getItems().addAll(EditCourse, DeleteCourse, EditAdviser, DeleteAdviser);

        DeleteCourse.setOnAction((ActionEvent t) -> {
            int index = TableCourse.getSelectionModel().getSelectedIndex();

            Course deleteCourse = TableCourse.getItems().get(index);

            if (Extras.showConfirmation("¿Desea eliminar el curso: " + optionsGrade[deleteCourse.getGrado()] + " " + deleteCourse.getParalelo() + "?")) {

                boolean rsp = coursedao.deleteCourse(deleteCourse.getIdcurso());

                if (rsp) {

                    Extras.showAlert("Exito", "Se elimino correctamente el Curso", Alert.AlertType.INFORMATION);

                    cleanAdvisor();
                    cleanFieldsCourse();

                    enableCourseField();
                    enableadvisor();

                    UpdateCourses();
                    UploadCourses();

                } else {

                    Extras.showAlert("Error", "Hubo un error", Alert.AlertType.ERROR);

                }
            }
        });

        EditCourse.setOnAction((ActionEvent t) -> {

            int index = TableCourse.getSelectionModel().getSelectedIndex();

            courseselect = TableCourse.getItems().get(index);

            CboxLevelCourse.getSelectionModel().select(courseselect.getNivel());

            CboxGradeCourse.getSelectionModel().select(courseselect.getGrado());

            TextPalallel.setText(String.valueOf(courseselect.getParalelo()));

            TextQuota.setText(String.valueOf(courseselect.getCupo_max()));

            if (courseselect.getAdmite_nuevos()) {
                RdYesAN.setSelected(true);
            } else {
                RdNoAN.setSelected(true);
            }

            TextPalallel.setDisable(false);
            TextPalallel.setEditable(true);
            btnCancelar.setDisable(false);

            cleanAdvisor();
            disableadvisor();

        });

        EditAdviser.setOnAction((ActionEvent t) -> {
            int index = TableCourse.getSelectionModel().getSelectedIndex();

            courseselect = TableCourse.getItems().get(index);

            if (courseselect.getAsesor() != null) {

                cbxA.getSelectionModel().select(courseselect.getAsesor());

                String namecourse = optionsGrade[courseselect.getGrado()] + " " + courseselect.getParalelo();

                cbxC.getSelectionModel().select(namecourse);

                disableCourseField();

                dpI.setValue(courseselect.getFechai());

                dpF.setValue(courseselect.getFechaf());

                cbxA.setDisable(false);
                cbxC.setDisable(true);
                dpI.setDisable(true);
                dpF.setDisable(false);

                btnCancelar.setDisable(false);
            } else {
                Extras.showAlert("Advertencia", "El curso no tiene Asesor", Alert.AlertType.WARNING);
            }
        });

        DeleteAdviser.setOnAction((ActionEvent t) -> {
            int index = TableCourse.getSelectionModel().getSelectedIndex();

            Course deleteadviser = TableCourse.getItems().get(index);

            if (deleteadviser.getAsesor() != null) {
                if (Extras.showConfirmation("¿Desea eliminar el asesor: " + deleteadviser.getAsesor() + " del curso " + optionsGrade[deleteadviser.getGrado()] + " " + deleteadviser.getParalelo() + "?")) {

                    boolean rsp = advisordao.delete(deleteadviser.getAsesor(), coursedao.idcourse(deleteadviser.getNivel(), verifycourse(optionsGrade[deleteadviser.getGrado()] + " " + deleteadviser.getParalelo())));

                    if (rsp) {

                        Extras.showAlert("Exito", "Se elimino correctamente el Asesor", Alert.AlertType.INFORMATION);

                        UploadCourses();

                        UpdateCourses();

                        UpdateAdversors();

                        cleanAdvisor();
                        cleanFieldsCourse();

                        enableCourseField();
                        enableadvisor();

                    } else {

                        Extras.showAlert("Error", "Hubo un error", Alert.AlertType.ERROR);
                    }
                }
            } else {
                Extras.showAlert("Advertencia", "El curso no tiene Asesor", Alert.AlertType.WARNING);
            }
        });

        TableCourse.setContextMenu(CourseOptions);

        //DOCUMENTOS
        DocumentationOptions = new ContextMenu();

        MenuItem EditDocumentation = new MenuItem("Editar");
        MenuItem DeleteDocumentation = new MenuItem("Eliminar");

        DocumentationOptions.getItems().addAll(EditDocumentation, DeleteDocumentation);

        DeleteDocumentation.setOnAction((ActionEvent t) -> {

            int index = TableDocumentation.getSelectionModel().getSelectedIndex();

            Documentation deleteDocumentation = TableDocumentation.getItems().get(index);

            if (Extras.showConfirmation("¿Desea eliminar el documento: " + deleteDocumentation.getNombre() + "?")) {

                boolean rsp = documentationdao.deleteDocumentation(deleteDocumentation.getIdtipo_documento());

                if (rsp) {

                    Extras.showAlert("Exito", "Se elimino correctamente la Documentacion", Alert.AlertType.INFORMATION);

                    LoadDocumentation();

                    cleanFieldsDocumentation();

                    enableDocumentationField();

                } else {

                    Extras.showAlert("Error", "Hubo un error", Alert.AlertType.ERROR);
                }
            }
        });

        EditDocumentation.setOnAction((ActionEvent t) -> {
            int index = TableDocumentation.getSelectionModel().getSelectedIndex();

            documentationselect = TableDocumentation.getItems().get(index);

            TextNameDocumentation.setText(documentationselect.getNombre());

            if (documentationselect.isObligatorio()) {
                RdYesO.setSelected(true);
            } else {
                RdNoO.setSelected(true);
            }

            if (documentationselect.isCartacompromiso()) {
                RdYesCC.setSelected(true);
            } else {
                RdNoCC.setSelected(true);
            }

            btnCancelar.setDisable(false);
        });

        TableDocumentation.setContextMenu(DocumentationOptions);

        cbxBgrades.getSelectionModel().selectedIndexProperty().addListener((observable, oldIndex, newIndex) -> {

            int index = newIndex.intValue();
            if (index >= 0) {
                gradeFilter = String.valueOf(index);
            } else {
                gradeFilter = "";
            }
            aplicarFiltro();
        });

        textBparallel.textProperty().addListener((obs, oldVal, newVal) -> {
            parallelFilter = newVal.toLowerCase();
            aplicarFiltro();
        });

        textBdocument.textProperty().addListener((observable, oldValue, newValue) -> {
            // Realiza el filtro dinámico mientras el usuario escribe
            filteredDataD.setPredicate(documentation -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                return documentation.getNombre().toLowerCase().contains(newValue.toLowerCase());
            });
        });
    }
}
