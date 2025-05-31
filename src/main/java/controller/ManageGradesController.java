/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import Dao.AdvisorDao;
import Dao.CourseDao;
import Dao.ListCourseDao;
import Dao.UserDao;
import interfaces.MainControllerAware;
import interfaces.DataReceiver;
import jakarta.mail.MessagingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Course;
import model.Extras;
import model.GradeUpload;
import model.ListCourse;
import model.StudentNotes;
import model.User;
import model.OPTManager;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author mauricioteranlimari
 */
public class ManageGradesController implements Initializable, MainControllerAware, DataReceiver {

    @FXML
    private Button BtnToFinish;

    @FXML
    private ComboBox<String> CbxCourses;

    @FXML
    private TableView<StudentNotes> TableNotes;

    @FXML
    private Label TextAdvisor;

    @FXML
    private Button BtnUploadFile;

    @FXML
    private Rectangle rectangle1;

    @FXML
    private Rectangle rectangle2;

    @FXML
    private Rectangle rectangle3;

    @FXML
    private StackPane stack1;

    @FXML
    private StackPane stack2;

    @FXML
    private StackPane stack3;

    @FXML
    private VBox MainVBox;

    private Map<String, String> pageMap = new HashMap<>();

    private MainMenuController mainController;

    private User advisor;

    private AdvisorDao advisordao;

    private ListCourseDao listcoursedao;

    private CourseDao coursedao;

    private int idcurso;

    private String nivel;

    private String grado;

    private char paralelo;

    private SchoolSettingsController st;

    private ContextMenu OptionsStudent;

    private StudentNotes select;

    private final OPTManager verify = new OPTManager();

    private String temporarynote = "";

    private final String[] optionsLevel = {"Primaria", "Secundaria"};

    public String[] optionsGrade = {"Primero", "Segundo", "Tercero", "Cuarto", "Quinto", "Sexto"};

    // private final String[] departments = {"LP", "SCZ", "CBBA", "OR", "PT", "CH", "TJA", "BE", "PD"};
    private String PasswordMod = "";

    private UserDao userdao;

    public void setAdvisor(User user) {
        this.advisor = user;
    }

    public User getAdvisor() {
        return advisor;
    }

    @FXML
    void OpenFile(ActionEvent event) throws SQLException {

        if (!CbxCourses.getSelectionModel().isEmpty()) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccione el Archivo Excel");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Archivos Excel", "*.xls", "*.xlsx"));
            Stage stage = (Stage) BtnUploadFile.getScene().getWindow();

            File selectedFile = fileChooser.showOpenDialog(stage);

            if (selectedFile != null) {

                if (ReadAdvisors(selectedFile) == false) {
                    Extras.showAlert("Advertencia", "Revise el documento, el asesor no coincide", Alert.AlertType.ERROR);
                    return;
                }

                if (ReadCourse(selectedFile)) {
                    if (CbxCourses.getSelectionModel().getSelectedItem().toUpperCase().equals(grado + " " + paralelo + "(" + nivel + ")") == false) {
                        Extras.showAlert("Advertencia", "EL curso seleccionado no coincide con el del archivo", Alert.AlertType.WARNING);
                        return;
                    }
                }

                List<String> notas = readnotes(selectedFile);

                if (notas != null) {
                    notas.forEach(System.out::println);
                }

                if (notas != null && !notas.isEmpty()) {
                    if (listcoursedao.NotesExist(idcurso, LocalDate.now().getYear())) {
                        Extras.showAlert("Advertencia", "Las para este curso ya fueron subidas", Alert.AlertType.WARNING);
                        return;
                    }
                    boolean rsp = listcoursedao.SaveNotes(notas, this.idcurso, LocalDate.now().getYear());
                    if (rsp) {
                        Extras.showAlert("Exito", "Notas subidas correctamente", Alert.AlertType.INFORMATION);
                        GradeUpload.guardarFechaSubida(idcurso, LocalDate.now().getYear());
                        Extras.showAlert("Informacion", "Las notas se subieron el " + LocalDate.now() + ", solo tiene 7 dias para modificar las notas en caso de haber cometido errores.", Alert.AlertType.INFORMATION);
                        LoadList();
                    } else {
                        Extras.showAlert("Error", "Error al subir notas", Alert.AlertType.ERROR);
                    }
                }
            }
        } else {
            Extras.showAlert("Advertencia", "Debe seleccionar un curso!", Alert.AlertType.WARNING);
        }

    }

    public void init() {
        if (advisor != null && advisor.getCargo() == 2) {
            TextAdvisor.setText("Asesor: " + advisor.getNombre() + " " + advisor.getApellido());
            List<Course> cursos = advisordao.search(advisor.getNombre() + " " + advisor.getApellido());

            List<String> listaCursosFormateados = new ArrayList<>();

            for (Course curso : cursos) {
                int grado = curso.getGrado();
                int nivel = curso.getNivel();
                String gradoTexto = (grado >= 0 && grado < optionsGrade.length) ? optionsGrade[grado] : "Desconocido";
                String nivelTexto = (nivel >= 0 && nivel < optionsLevel.length) ? optionsLevel[nivel] : "Desconocido";
                listaCursosFormateados.add(gradoTexto + " " + curso.getParalelo() + "(" + nivelTexto + ")");
            }

            ObservableList<String> observableCursos = FXCollections.observableArrayList(listaCursosFormateados);
            CbxCourses.setItems(observableCursos);
            CbxCourses.setValue("Seleccione");
            BtnUploadFile.setDisable(true);

        }
    }

    private List<String> readnotes(File archivo) throws SQLException {

        List<ListCourse> lista = null;

        lista = listcoursedao.listnotes(idcurso, LocalDate.now().getYear());

        List<String> ciExistentes = lista.stream().map(ListCourse::getCedula_identidad).filter(Objects::nonNull).map(String::trim).toList();

        List<String> listaNotas = new ArrayList<>();

        try {

            FileInputStream input = new FileInputStream(archivo);
            XSSFWorkbook libro = new XSSFWorkbook(input);

            XSSFSheet hoja = libro.getSheetAt(0);

            FormulaEvaluator evaluator = libro.getCreationHelper().createFormulaEvaluator();

            int columnasNotas = ("PRIMERO".equals(grado) || "SEGUNDO".equals(grado) || "TERCERO".equals(grado)) ? 13 : 12;

            for (int i = 6; i <= hoja.getLastRowNum() && listaNotas.size() < 30; i++) {
                Row fila = hoja.getRow(i);
                if (fila == null) {
                    continue;
                }

                Cell celdaNombre = fila.getCell(1);
                if (celdaNombre == null || celdaNombre.getCellType() != CellType.STRING || celdaNombre.getStringCellValue().trim().isEmpty()) {
                    break;
                }

                StringBuilder linea = new StringBuilder();

                linea.append(celdaNombre.getStringCellValue().trim()).append(",");

                Cell celdaCedula = fila.getCell(2);
                String ci = "";

                if (celdaCedula != null) {
                    if (celdaCedula.getCellType() == CellType.STRING) {
                        ci = celdaCedula.getStringCellValue().trim();
                    } else {
                        Extras.showAlert("Error", "El CI debe estar en formato de texto.", Alert.AlertType.ERROR);
                        return null;
                    }
                }

                if (!ciExistentes.contains(ci)) {
                    Extras.showAlert("Error", "Revise el ci de los estudiantes: " + ci, Alert.AlertType.ERROR);
                    return null;
                } else {
                    linea.append(ci).append(",");
                }

                for (int j = 3; j < 3 + columnasNotas; j++) {
                    Cell celda = fila.getCell(j);
                    int nota = 0;

                    if (celda != null) {
                        switch (celda.getCellType()) {
                            case NUMERIC ->
                                nota = (int) celda.getNumericCellValue();
                            case FORMULA -> {
                                CellValue valor = evaluator.evaluate(celda);
                                if (valor != null && valor.getCellType() == CellType.NUMERIC) {
                                    nota = (int) valor.getNumberValue();
                                }
                            }
                        }
                    }

                    linea.append(nota);
                    if (j < 2 + columnasNotas) {
                        linea.append(",");
                    }
                }

                listaNotas.add(linea.toString());
            }

            libro.close();
            input.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaNotas;
    }

    private boolean ReadCourse(File archivo) {

        try {
            FileInputStream input = new FileInputStream(archivo);
            XSSFWorkbook libro = new XSSFWorkbook(input);

            XSSFSheet hoja = libro.getSheetAt(0);

            Row fila = hoja.getRow(1);

            if (fila == null) {
                Extras.showAlert("Error", "No se encontró la fila 2 en el archivo.", Alert.AlertType.ERROR);
                return false;
            }

            // Celda 2B -> índice 1
            Cell celdaNivel = fila.getCell(1);
            // Celda 2C -> índice 2
            Cell celdaGrado = fila.getCell(2);
            // Celda 2D -> índice 3
            Cell celdaParalelo = fila.getCell(3);

            if (celdaNivel == null || celdaNivel.getCellType() != CellType.STRING
                    || celdaGrado == null || celdaGrado.getCellType() != CellType.STRING
                    || celdaParalelo == null || celdaParalelo.getCellType() != CellType.STRING
                    || celdaParalelo.getStringCellValue().trim().length() != 1) {

                Extras.showAlert("Error", "Verifique que las celdas nivel, grado y paralelo contengan texto válido", Alert.AlertType.ERROR);
                //return false;
            } else {

                this.nivel = celdaNivel.getStringCellValue().trim();
                this.grado = celdaGrado.getStringCellValue().trim();
                this.paralelo = celdaParalelo.getStringCellValue().trim().charAt(0);

                return true;
            }

            input.close();
            libro.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean ReadAdvisors(File archivo) {

        boolean allowedAdvisor = false;
        try {
            FileInputStream input = new FileInputStream(archivo);
            XSSFWorkbook libro = new XSSFWorkbook(input);

            XSSFSheet hoja = libro.getSheetAt(0);

            Row fila = hoja.getRow(2);

            if (fila == null) {
                System.err.println("Error: La fila 3 no existe.");

            }

            Cell celda3B = fila.getCell(1);
            if (celda3B == null || celda3B.getCellType() != CellType.STRING || celda3B.getStringCellValue().trim().isEmpty()) {
                Extras.showAlert("Error", "Revise el campo del asesor 1", Alert.AlertType.ERROR);

            }

            String asesor3B = celda3B.getStringCellValue().trim();

            Cell celda4B = fila.getCell(2);
            String asesor4B = null;
            if (celda4B != null && celda4B.getCellType() == CellType.STRING && !celda4B.getStringCellValue().trim().isEmpty()) {
                asesor4B = celda4B.getStringCellValue().trim();
            }

            String nameadvisor = advisor.getNombre() + " " + advisor.getApellido();

            boolean asesor3BV = asesor3B.equals(nameadvisor);
            boolean asesor4BV = asesor4B != null && asesor4B.equals(nameadvisor);

            if (asesor3BV || asesor4BV) {
                System.out.println("Hola asesor: " + nameadvisor);
                allowedAdvisor = true;
            } else {
                Extras.showAlert("Error", "Usted no puede subir las notas", Alert.AlertType.ERROR);
            }

            input.close();
            libro.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return allowedAdvisor;

    }

    @Override
    public void onDataReceived(Object data
    ) {
        if (data instanceof User user) {
            this.advisor = user;
            System.out.println("Usuario recibido: " + advisor.getNombre() + " " + advisor.getApellido());
            init();
        }
    }

    @Override
    public void setMainController(MainMenuController mainController
    ) {
        this.mainController = mainController;
    }

    private final Set<String> materiasSet = new LinkedHashSet<>();

    public void LoadList() {

        TableNotes.getItems().clear();
        TableNotes.getColumns().clear();

        List<ListCourse> lista = null;

        try {

            lista = listcoursedao.listnotes(idcurso, LocalDate.now().getYear());
        } catch (SQLException ex) {
            Logger.getLogger(ManageGradesController.class.getName()).log(Level.SEVERE, null, ex);
        }

        Map<String, StudentNotes> studentmap = new LinkedHashMap<>();

        for (ListCourse course : lista) {
            String key = course.getNameStudent() + "|" + course.getCedula_identidad();
            materiasSet.add(course.getNameMateria());

            StudentNotes estudiante = studentmap.get(key);
            if (estudiante == null) {
                estudiante = new StudentNotes(course.getNameStudent(), course.getCedula_identidad());
                studentmap.put(key, estudiante);
            }
            // Asignar nota (convertir BigDecimal a String)
            estudiante.setNota(course.getNameMateria(),
                    course.getNota() != null ? course.getNota().toString() : "");
        }

        List<StudentNotes> estudiantesOrdenados = new ArrayList<>(studentmap.values());
        estudiantesOrdenados.sort(Comparator.comparing(s -> {
            String[] partes = s.getNombreCompleto().split(" ");
            int len = partes.length;
            String primerApellido = len > 1 ? partes[len - 2] : "";
            String segundoApellido = partes[len - 1];
            return (primerApellido + " " + segundoApellido).toLowerCase();
        }));

        ObservableList<StudentNotes> data = FXCollections.observableArrayList(estudiantesOrdenados);

        // Columna número
        TableColumn<StudentNotes, Number> idCol = new TableColumn<>("N°");
        idCol.setCellValueFactory(cd
                -> new ReadOnlyObjectWrapper<>(TableNotes.getItems().indexOf(cd.getValue()) + 1)
        );

        // Columna nombre completo
        TableColumn<StudentNotes, String> nameCol = new TableColumn<>("Estudiante");
        nameCol.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getNombreCompleto()));

        // Columna CI
        TableColumn<StudentNotes, String> ciCol = new TableColumn<>("CI");
        ciCol.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getCi()));

        TableNotes.getColumns().addAll(idCol, nameCol, ciCol);

        // Columnas dinámicas para materias
        for (String materia : materiasSet) {
            TableColumn<StudentNotes, String> colMateria = new TableColumn<>(materia);
            colMateria.setCellValueFactory(cd -> cd.getValue().notaProperty(materia));
            TableNotes.getColumns().add(colMateria);
        }

        TableNotes.setItems(data);

    }

    @FXML
    void ToFinish(ActionEvent event) {
        TableNotes.setEditable(false);
        Extras.showAlert("Exito", "Modificacion de Notas Finalizada", Alert.AlertType.INFORMATION);
        BtnToFinish.setVisible(false);
        BtnToFinish.setDisable(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        stack1.prefHeightProperty().bind(MainVBox.heightProperty().multiply(1.5 / 10.0));
        stack2.prefHeightProperty().bind(MainVBox.heightProperty().multiply(7.5 / 10.0));
        stack3.prefHeightProperty().bind(MainVBox.heightProperty().multiply(1.0 / 10.0));

        stack1.prefWidthProperty().bind(MainVBox.widthProperty());
        stack2.prefWidthProperty().bind(MainVBox.widthProperty());
        stack3.prefWidthProperty().bind(MainVBox.widthProperty());

        rectangle1.widthProperty().bind(stack1.widthProperty());
        rectangle1.heightProperty().bind(stack1.heightProperty());

        rectangle2.widthProperty().bind(stack2.widthProperty());
        rectangle2.heightProperty().bind(stack2.heightProperty());

        rectangle3.widthProperty().bind(stack3.widthProperty());
        rectangle3.heightProperty().bind(stack3.heightProperty());

        try {
            advisordao = new AdvisorDao();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ManageGradesController.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            listcoursedao = new ListCourseDao();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ManageGradesController.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            userdao = new UserDao();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ManageGradesController.class.getName()).log(Level.SEVERE, null, ex);
        }

        st = new SchoolSettingsController();

        try {
            coursedao = new CourseDao();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ManageGradesController.class.getName()).log(Level.SEVERE, null, ex);
        }

        OptionsStudent = new ContextMenu();

        MenuItem modify = new MenuItem("Modificar Notas");

        OptionsStudent.getItems().addAll(modify);

        CbxCourses.setOnAction((ActionEvent event) -> {

            if (!CbxCourses.getSelectionModel().isEmpty()) {

                String seleccionado = CbxCourses.getSelectionModel().getSelectedItem();

                this.idcurso = coursedao.idcourse(st.verifylevel(seleccionado), st.verifycourse(seleccionado));

                if (listcoursedao.NotesExist(idcurso, LocalDate.now().getYear())) {
                    BtnUploadFile.setDisable(true);
                } else {
                    BtnUploadFile.setDisable(false);
                }

                LoadList();

                if (GradeUpload.puedeModificarNotas(idcurso, LocalDate.now().getYear())) {

                    TableNotes.setContextMenu(OptionsStudent);

                    modify.setOnAction((ActionEvent t) -> {

                        BtnUploadFile.setDisable(true);

                        BtnToFinish.setVisible(true);
                        BtnToFinish.setDisable(false);

                        int index = TableNotes.getSelectionModel().getSelectedIndex();

                        if (index != -1) {
                            select = TableNotes.getItems().get(index);
                        } else {
                            return;
                        }
                        if (!listcoursedao.NotesExistStudent(select.getCi())) {
                            Extras.showAlert("Advertencia", "El estudiante no tiene notas", Alert.AlertType.WARNING);
                            return;
                        }

                        new Thread(() -> {
                            try {
                                if (verify.isOTPExpired()) {
                                    SendCodetothedirector();
                                } else {
                                    System.out.println("Código OTP todavía válido, no se envía correo.");
                                    System.out.println("El codigo es: "+ PasswordMod);
                                }

                                Platform.runLater(() -> {
                                    
                                    String password = Extras.showPasswordInput("Validacion", "Ingrese la contraseña.", "Contraseña: ");

                                    if (password == null || password.equals("")) {
                                        return;
                                    } else if (!verify.validateOTP(password)) {
                                        Extras.showAlert("Error", "La contraseña no coincide", Alert.AlertType.ERROR);
                                        return;
                                    }

                                    // Si la contraseña es correcta, habilitamos edición y columnas:
                                    TableNotes.setEditable(true);

                                    TableNotes.getColumns().removeIf(col -> materiasSet.contains(col.getText()));

                                    for (String materia : materiasSet) {
                                        TableColumn<StudentNotes, String> colMateria = new TableColumn<>(materia);
                                        colMateria.setCellValueFactory(cd -> cd.getValue().notaProperty(materia));
                                        colMateria.setCellFactory(TextFieldTableCell.forTableColumn());

                                        colMateria.setOnEditStart(editStartEvent -> {
                                            StudentNotes student = editStartEvent.getRowValue();
                                            String matter = colMateria.getText();
                                            temporarynote = student.getNota(matter);
                                        });

                                        colMateria.setOnEditCommit(editEvent -> {
                                            StudentNotes student = editEvent.getRowValue();
                                            if (!student.getCi().equals(select.getCi())) {
                                                Extras.showAlert("Error", "Solo puede editar al estudiante autorizado", Alert.AlertType.ERROR);
                                                LoadList();
                                                return;
                                            }

                                            String nuevaNota = editEvent.getNewValue();

                                            if (!esNotaValida(nuevaNota)) {
                                                Extras.showAlert("Error", "Valor de la nota no valido", Alert.AlertType.ERROR);
                                                student.setNota(materia, temporarynote);
                                                editEvent.getTableView().refresh();
                                                editEvent.getTableView().edit(editEvent.getTablePosition().getRow(), colMateria);
                                                return;
                                            }
                                            student.setNota(materia, nuevaNota);

                                            boolean rsp = listcoursedao.updateNoteStudent(select.getCi(), materia, nuevaNota, LocalDate.now().getYear());

                                            if (!rsp) {
                                                Extras.showAlert("Error", "Error al modificar la nota", Alert.AlertType.ERROR);
                                                LoadList();
                                            }
                                        });

                                        TableNotes.getColumns().add(colMateria);
                                    }
                                });
                            } catch (MessagingException ex) {
                                Logger.getLogger(ManageGradesController.class.getName()).log(Level.SEVERE, null, ex);

                            }
                        }).start();

                    });
                }
            }

        });

    }

    public boolean esNotaValida(String nota) {
        try {
            BigDecimal bd = new BigDecimal(nota);

            // Validar que no tenga más de 2 decimales
            if (bd.scale() > 2) {
                return false;
            }

            // Validar que esté entre 0 y 999.99 (decimal(5,2))
            BigDecimal max = new BigDecimal("100.00");
            BigDecimal min = BigDecimal.ZERO;

            return !(bd.compareTo(min) < 0 || bd.compareTo(max) > 0);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void navigateTo(String pageName) {
        System.out.println("Presionado " + pageName);
        if (mainController != null) {
            mainController.loadView(pageMap.get(pageName));
            mainController.addPage(pageName, pageMap.get(pageName));
        } else {
            System.out.println("Error: MainMenuController no está disponible.");
        }
    }

    private void SendCodetothedirector() throws MessagingException {

        this.PasswordMod = verify.generateOTP();

        EmailService emailService = new EmailService();

        for (String emailDirector : userdao.EmailListDirectors()) {
            emailService.enviarCorreo(emailDirector, "Código OTP para modificar notas",
                    "Su código es: " + PasswordMod + ". Válido por 10 minutos.");
        }

        emailService.enviarCorreo("teranmauricio22@gmail.com", "Código OTP para modificar notas", "Su código es: " + PasswordMod + ". Válido por 10 minutos.");

    }
}
