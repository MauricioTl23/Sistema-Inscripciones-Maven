/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Student;
import Dao.StudentDao;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.sql.SQLException;

/**
 *
 * @author mauricioteranlimari
 */
public class RCoursesController implements Initializable {

    @FXML
    private Button btnExport;
    @FXML
    private ComboBox<String> CboParalelo;
    @FXML
    private ComboBox<String> CboGrado;
    @FXML
    private ComboBox<String> CboNivel;

    @FXML
    private TableView TblStudent;
    private StudentDao studentDao;
    private FilteredList<Student> filteredData;

    private ObservableList<Student> data;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            this.studentDao = new StudentDao();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ManageUsersController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(RCoursesController.class.getName()).log(Level.SEVERE, null, ex);
        }

        String[] grados = {"Primero", "Segundo", "Tercero", "Cuarto", "Quinto", "Sexto"};
        ObservableList<String> gradoItems = FXCollections.observableArrayList(grados);
        CboGrado.setItems(gradoItems);
        CboGrado.setValue("Seleccione");

        String[] paralelos = {"A", "B", "C", "D", "E", "F"};
        ObservableList<String> paraleloItems = FXCollections.observableArrayList(paralelos);
        CboParalelo.setItems(paraleloItems);
        CboParalelo.setValue("Seleccione");

        String[] niveles = {"Primaria", "Secundaria"};
        ObservableList<String> nivelItems = FXCollections.observableArrayList(niveles);
        CboNivel.setItems(nivelItems);
        CboNivel.setValue("Seleccione");
        CboGrado.valueProperty().addListener((obs, oldVal, newVal) -> verificarYFiltrar());
        CboParalelo.valueProperty().addListener((obs, oldVal, newVal) -> verificarYFiltrar());
        CboNivel.valueProperty().addListener((obs, oldVal, newVal) -> verificarYFiltrar());

    }

    @FXML
    void Export(ActionEvent event) throws NoSuchAlgorithmException, Exception {
        exportToExcel(filteredData);
    }

    public void LoadStudents(int grado, String paralelo, int nivel) {

        TblStudent.setItems(FXCollections.observableArrayList());
        TblStudent.getColumns().clear();

        List<Student> students = this.studentDao.getEstudiantesPorGradoParaleloYNivel(grado, paralelo, nivel);

        data = FXCollections.observableArrayList(students);
        filteredData = new FilteredList<>(data, p -> true);
        TblStudent.setItems(filteredData);

        TableColumn<Student, String> Namecol = new TableColumn<>("NOMBRE(S)");
        Namecol.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Student, String> Surnamecol = new TableColumn<>("APELLIDO(S)");
        Surnamecol.setCellValueFactory(new PropertyValueFactory<>("apellido"));

        TableColumn<Student, String> datecol = new TableColumn<>("Fecha Nacimiento");
        datecol.setCellValueFactory(new PropertyValueFactory<>("fecha_nacimiento"));

        TableColumn<Student, String> CIcol = new TableColumn<>("CI");
        CIcol.setCellValueFactory(new PropertyValueFactory<>("cedula_identidad"));

        TableColumn<Student, Integer> gendercol = new TableColumn<>("GENERO");
        gendercol.setCellValueFactory(new PropertyValueFactory<>("genero"));
        gendercol.setCellFactory(col -> new TableCell<Student, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String[] gender = {"Masculino", "Femenino"};
                    setText(gender[item]);
                }
            }
        });

        TableColumn<Student, String> adresscol = new TableColumn<>("DIRECCION");
        adresscol.setCellValueFactory(new PropertyValueFactory<>("direccion"));

        TableColumn<Student, String> Emailcol = new TableColumn<>("CORREO");
        Emailcol.setCellValueFactory(new PropertyValueFactory<>("correo"));

        TblStudent.getColumns().addAll(Namecol, Surnamecol, datecol, CIcol, gendercol, adresscol, Emailcol);
        TblStudent.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void verificarYFiltrar() {
        String paralelo = CboParalelo.getValue();
        String nivelText = CboNivel.getValue();

        int indexGrado = CboGrado.getSelectionModel().getSelectedIndex();
        int indexNivel = CboNivel.getSelectionModel().getSelectedIndex();

        if (indexGrado != -1 && paralelo != null && indexNivel != -1
                && !paralelo.equals("Seleccione")) {

            int gradoNum = indexGrado;
            int nivel = indexNivel;

            LoadStudents(gradoNum, paralelo, nivel);
        }
    }

    public void exportToExcel(FilteredList<Student> filteredData) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Estudiantes");

        // Crear encabezados (sin "ID")
        String[] headers = {"NOMBRE(S)", "APELLIDO(S)", "Fecha Nacimiento", "CI", "GÉNERO", "DIRECCIÓN", "CORREO"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Agregar datos (sin "ID")
        int rowNum = 1;
        for (Student s : filteredData) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(s.getNombre());
            row.createCell(1).setCellValue(s.getApellido());
            row.createCell(2).setCellValue(s.getFecha_nacimiento());
            row.createCell(3).setCellValue(s.getCedula_identidad());
            row.createCell(4).setCellValue(s.getGenero() == 0 ? "Masculino" : "Femenino");
            row.createCell(5).setCellValue(s.getDireccion());
            row.createCell(6).setCellValue(s.getCorreo());
        }

        // Autoajustar columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Guardar archivo con FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("Estudiantes.xlsx");
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
                workbook.close();
                System.out.println("Archivo Excel exportado correctamente.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
