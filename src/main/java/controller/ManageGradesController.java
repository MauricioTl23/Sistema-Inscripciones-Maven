/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import Dao.AdvisorDao;
import Dao.CourseDao;
import Dao.ListCourseDao;
import interfaces.MainControllerAware;
import interfaces.DataReceiver;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Course;
import model.Extras;
import model.ListCourse;
import model.StudentNotes;
import model.User;
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
public class ManageGradesController implements Initializable,MainControllerAware,DataReceiver{

    @FXML
    private ComboBox<String> CbxCourses;

    @FXML
    private TableView<StudentNotes> TableNotes;

    @FXML
    private Label TextAdvisor;

    @FXML
    private Button BtnUploadFile;

    @FXML
    void OpenFile(ActionEvent event) {

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

    private String[] optionsLevel = {"Primaria", "Secundaria"};

    public String[] optionsGrade = {"Primero", "Segundo", "Tercero", "Cuarto", "Quinto", "Sexto"};

    private final String[] departments = {"LP", "SCZ", "CBBA", "OR", "PT", "CH", "TJA", "BE", "PD"};

    private String PasswordMod = "DiseñoSistemas2025";

    public void setAdvisor(User user) {
        this.advisor = user;
    }

    public User getAdvisor() {
        return advisor;
    }

    public String convertCi(String ci) {
        if (ci == null || !ci.contains("-")) {
            return null;
        }

        int lastDashIndex = ci.lastIndexOf("-");
        if (lastDashIndex == -1 || lastDashIndex == ci.length() - 1) {
            return null;
        }

        String ciBase = ci.substring(0, lastDashIndex).trim();
        String sufijo = ci.substring(lastDashIndex + 1).trim();

        // Buscar el índice del sufijo
        for (int i = 0; i < departments.length; i++) {
            if (departments[i].equalsIgnoreCase(sufijo)) {
                return ciBase + "-" + i;
            }
        }

        return null;
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

        }
    }

    private List<String> readnotes(File archivo) {
        List<String> listaNotas = new ArrayList<>();

        List<ListCourse> lista = null;

        Set<String> ciExistentes = new HashSet<>();
        try {
            lista = listcoursedao.listnotes(idcurso, LocalDate.now().getYear());
            for (ListCourse lc : lista) {
                ciExistentes.add(lc.getCedula_identidad());
            }
        } catch (SQLException ex) {
            Logger.getLogger(ManageGradesController.class.getName()).log(Level.SEVERE, null, ex);
        }

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
                String ci = convertCi(celdaCedula != null ? celdaCedula.getStringCellValue().trim() : "");

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
        Set<String> materiasSet = new LinkedHashSet<>();

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

        ciCol.setCellFactory(col -> new TableCell<StudentNotes, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || !item.contains("-")) {
                    setText(null);
                } else {
                    try {
                        String[] parts = item.split("-");

                        switch (parts.length) {
                            case 2 -> {
                                String base = parts[0];
                                int index = Integer.parseInt(parts[1]);
                                if (index >= 0 && index < departments.length) {
                                    setText(base + "-" + departments[index]);
                                } else {
                                    setText(item);
                                }
                            }
                            case 3 -> {
                                String base = parts[0];
                                String letra = parts[1];
                                int index = Integer.parseInt(parts[2]);
                                if (index >= 0 && index < departments.length) {
                                    setText(base + "-" + letra + "-" + departments[index]);
                                } else {
                                    setText(item);
                                }
                            }
                            default ->
                                setText(item);
                        }

                    } catch (NumberFormatException e) {
                        setText(item);
                    }
                }
            }
        });
        TableNotes.getColumns().addAll(idCol, nameCol, ciCol);

        // Columnas dinámicas para materias
        for (String materia : materiasSet) {
            TableColumn<StudentNotes, String> colMateria = new TableColumn<>(materia);
            colMateria.setCellValueFactory(cd -> cd.getValue().notaProperty(materia));
            TableNotes.getColumns().add(colMateria);
        }

        TableNotes.setItems(data);

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
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

        st = new SchoolSettingsController();

        try {
            coursedao = new CourseDao();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ManageGradesController.class.getName()).log(Level.SEVERE, null, ex);
        }

        CbxCourses.setOnAction(event -> {
            String seleccionado = CbxCourses.getSelectionModel().getSelectedItem();
            this.idcurso = coursedao.idcourse(st.verifylevel(seleccionado), st.verifycourse(seleccionado));
            if (!CbxCourses.getSelectionModel().isEmpty()) {
                LoadList();
            }

        });

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
}
