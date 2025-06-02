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
import javafx.application.Platform;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

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

    @FXML
    private VBox MainVBox;

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
    private PieChart PieChartCourses;

    private StudentDao studentDao;

    private FilteredList<Student> filteredData;

    private ObservableList<Student> data;

    private void animarPieChart(PieChart pieChart) {

        for (PieChart.Data data : pieChart.getData()) {
            data.getNode().setOpacity(0);
            data.getNode().setScaleX(0);
            data.getNode().setScaleY(0);

            javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(javafx.util.Duration.seconds(1), data.getNode());
            fade.setFromValue(0);
            fade.setToValue(1);

            javafx.animation.ScaleTransition scale = new javafx.animation.ScaleTransition(javafx.util.Duration.seconds(1), data.getNode());
            scale.setFromX(0);
            scale.setFromY(0);
            scale.setToX(1);
            scale.setToY(1);

            fade.play();
            scale.play();
        }
    }

    private void PieChartCourse(int idCurso) {

        ObservableList<PieChart.Data> chartData = studentDao.GenericQuantity(idCurso);

        if (!chartData.isEmpty()) {
            PieChartCourses.setData(chartData);
            PieChartCourses.setTitle("DISTRIBUCION GENERO");
            for (PieChart.Data data : PieChartCourses.getData()) {
                String label = data.getName();
                String color = switch (label) {
                    case "Masculino" ->
                        "#2196F3";
                    case "Femenino" ->
                        "#F48FB1";
                    default ->
                        "#9E9E9E";
                };
                data.getNode().setStyle("-fx-pie-color: " + color + ";");
            }
            Platform.runLater(() -> {
                PieChartCourses.lookupAll(".chart-legend-item").forEach(legendItem -> {
                    javafx.scene.Node symbol = legendItem.lookup(".chart-legend-item-symbol");
                    javafx.scene.Node label = legendItem.lookup(".label");
                    if (symbol != null && label != null) {
                        String text = ((javafx.scene.control.Labeled) label).getText();
                        String color = switch (text) {
                            case "Masculino" ->
                                "#2196F3";
                            case "Femenino" ->
                                "#F48FB1";
                            default ->
                                "#9E9E9E";
                        };
                        symbol.setStyle("-fx-background-color: " + color + ";");
                    }
                });
            });
            animarPieChart(PieChartCourses);
        } else {
            PieChartCourses.setData(FXCollections.observableArrayList());
            PieChartCourses.setTitle("SIN DATOS PARA EL CURSO");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        stack1.prefHeightProperty().bind(MainVBox.heightProperty().multiply(2.0 / 10.0));
        stack2.prefHeightProperty().bind(MainVBox.heightProperty().multiply(7.0 / 10.0));
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

        ObservableList<String> paraleloItems = studentDao.parallels();
        CboParalelo.setItems(paraleloItems);
        CboParalelo.setValue("Seleccione");

        String[] niveles = {"Primaria", "Secundaria"};
        ObservableList<String> nivelItems = FXCollections.observableArrayList(niveles);
        CboNivel.setItems(nivelItems);
        CboNivel.setValue("Seleccione");
        CboGrado.valueProperty().addListener((obs, oldVal, newVal) -> verificarYFiltrar());
        CboParalelo.valueProperty().addListener((obs, oldVal, newVal) -> verificarYFiltrar());
        CboNivel.valueProperty().addListener((obs, oldVal, newVal) -> verificarYFiltrar());

        Platform.runLater(() -> verificarYFiltrar());

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
            int idCurso = studentDao.getCursoId(gradoNum, paralelo, nivel);
            if (idCurso != -1) {
                PieChartCourse(idCurso);
            } else {
                PieChartCourses.setData(FXCollections.observableArrayList());
                PieChartCourses.setTitle("CURSO NO ENCONTRADO");
            }
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
