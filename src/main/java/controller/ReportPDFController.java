package controller;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
import Dao.ReportsDao;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import model.Extras;
import model.ReportLogger;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 * FXML Controller class
 *
 * @author mauricioteranlimari
 */
public class ReportPDFController implements Initializable {

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
    private ComboBox<String> CbxReport;

    @FXML
    private DatePicker DatePff;

    @FXML
    private DatePicker DatePfi;

    @FXML
    private ComboBox<String> CbxLevel;

    @FXML
    private PieChart PieCharReports;

    @FXML
    private TableView<String[]> TableReports;

    private final String[] level = {"Primaria", "Secundaria"};
    private final String[] report = {"Distribución Estudiantil", "Inscripciones General", "Inscripciones por Fechas", "Cupos de los Cursos", "Estudiantes con Documentación Pendiente", "Tutores de los Estudiantes"};

    ReportsDao reportDao;

    private void Disable() {
        CbxLevel.setDisable(true);
        DatePfi.setDisable(true);
        DatePff.setDisable(true);
    }

    private void Clean() {
        CbxLevel.getSelectionModel().select("Seleccione");
        DatePff.setValue(null);
        DatePfi.setValue(null);
    }

    private void generateDistribucionEstudiantil() throws ClassNotFoundException, SQLException, IOException {

        List<Map<String, Object>> data = reportDao.ReportOne(CbxLevel.getSelectionModel().getSelectedIndex());

        if (data.isEmpty()) {
            Extras.showAlert("Informacion", "No hay datos para mostrar", Alert.AlertType.ERROR);
            return;
        }

        Collection<Map<String, ?>> collection = new ArrayList<>(data);

        try {

            JasperReport reporte = (JasperReport) JRLoader.loadObject(getClass().getResource("/reports/ReportByCourseAndLevel.jasper"));
            JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(collection);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("NumeroReporte", ReportLogger.getNextReportNumber(CbxReport.getSelectionModel().getSelectedItem()));
            parametros.put("LevelText", CbxLevel.getSelectionModel().getSelectedItem());

            JasperPrint print = JasperFillManager.fillReport(reporte, parametros, dataSource);

            String userHome = System.getProperty("user.home");
            String outputPath = userHome + "/Desktop/DistribucionEstudiantil" + "-" + ReportLogger.getNextReportNumber(CbxReport.getSelectionModel().getSelectedItem()) + ".pdf";

            JasperExportManager.exportReportToPdfFile(print, outputPath);

            Extras.showAlert("Éxito", "PDF exportado correctamente", Alert.AlertType.INFORMATION);
            ReportLogger.logReport(CbxReport.getSelectionModel().getSelectedItem());

            try {
                File pdfFile = new File(outputPath);
                if (pdfFile.exists() && Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);
                } else {
                    System.out.println("No se pudo abrir el PDF automáticamente.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    public void generateInscripcionesGenerales() {

        List<Map<String, Object>> data = reportDao.ReportTwo();

        if (data.isEmpty()) {
            Extras.showAlert("Informacion", "No hay datos para mostrar", Alert.AlertType.ERROR);
            return;
        }

        Collection<Map<String, ?>> collection = new ArrayList<>(data);

        try {

            JasperReport reporte = (JasperReport) JRLoader.loadObject(getClass().getResource("/reports/GeneralRegistrations.jasper"));
            JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(collection);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("NumeroReporte", ReportLogger.getNextReportNumber(CbxReport.getSelectionModel().getSelectedItem()));

            JasperPrint print = JasperFillManager.fillReport(reporte, parametros, dataSource);

            String userHome = System.getProperty("user.home");
            String outputPath = userHome + "/Desktop/InscripcionesGenerales" + "-" + ReportLogger.getNextReportNumber(CbxReport.getSelectionModel().getSelectedItem()) + ".pdf";

            JasperExportManager.exportReportToPdfFile(print, outputPath);

            Extras.showAlert("Éxito", "PDF exportado correctamente", Alert.AlertType.INFORMATION);
            ReportLogger.logReport(CbxReport.getSelectionModel().getSelectedItem());

            try {
                File pdfFile = new File(outputPath);
                if (pdfFile.exists() && Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);
                } else {
                    System.out.println("No se pudo abrir el PDF automáticamente.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (JRException e) {
            e.printStackTrace();
        }

    }

    private void generateInscripcionesporFechas() throws ClassNotFoundException, SQLException, IOException {

        if (DatePfi.getValue() == null || DatePff.getValue() == null) {
            Extras.showAlert("Advertencia", "Debe seleccionar fechas válidas", Alert.AlertType.WARNING);
            return;
        } else if (DatePfi.getValue().isAfter(DatePff.getValue())) {
            Extras.showAlert("Advertencia", "La fecha inicio no puede ser después de la fecha fin", Alert.AlertType.WARNING);
            return;
        }

        List<Map<String, Object>> data = reportDao.ReportThree(DatePfi.getValue().atStartOfDay(), DatePff.getValue().atStartOfDay());

        String range = DatePfi.getValue().toString() + " - " + DatePff.getValue().toString();

        if (data.isEmpty()) {
            Extras.showAlert("Informacion", "No hay datos para mostrar", Alert.AlertType.ERROR);
            return;
        }

        Collection<Map<String, ?>> collection = new ArrayList<>(data);

        try {

            JasperReport reporte = (JasperReport) JRLoader.loadObject(getClass().getResource("/reports/DetailedGeneralRegistrations.jasper"));
            JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(collection);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("NumeroReporte", ReportLogger.getNextReportNumber(CbxReport.getSelectionModel().getSelectedItem()));
            parametros.put("DateRange", range);

            JasperPrint print = JasperFillManager.fillReport(reporte, parametros, dataSource);

            String userHome = System.getProperty("user.home");
            String outputPath = userHome + "/Desktop/InscripcionesporFechas" + "-" + ReportLogger.getNextReportNumber(CbxReport.getSelectionModel().getSelectedItem()) + ".pdf";

            JasperExportManager.exportReportToPdfFile(print, outputPath);

            Extras.showAlert("Éxito", "PDF exportado correctamente", Alert.AlertType.INFORMATION);
            ReportLogger.logReport(CbxReport.getSelectionModel().getSelectedItem());

            try {
                File pdfFile = new File(outputPath);
                if (pdfFile.exists() && Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);
                } else {
                    System.out.println("No se pudo abrir el PDF automáticamente.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    private void generateCuposDisponibles() throws ClassNotFoundException, SQLException, IOException {

        if (CbxLevel.getSelectionModel().getSelectedIndex() == -1) {
            Extras.showAlert("Advertencia", "Debe Seleccionar el nivel Academico", Alert.AlertType.ERROR);
            return;
        }
        List<Map<String, Object>> data = reportDao.ReportFour(CbxLevel.getSelectionModel().getSelectedIndex());

        if (data.isEmpty()) {
            Extras.showAlert("Informacion", "No hay datos para mostrar", Alert.AlertType.ERROR);
            return;
        }

        Collection<Map<String, ?>> collection = new ArrayList<>(data);

        try {

            JasperReport reporte = (JasperReport) JRLoader.loadObject(getClass().getResource("/reports/QuotasForTheCourses.jasper"));
            JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(collection);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("NumeroReporte", ReportLogger.getNextReportNumber(CbxReport.getSelectionModel().getSelectedItem()));
            parametros.put("LevelText", CbxLevel.getSelectionModel().getSelectedItem());

            JasperPrint print = JasperFillManager.fillReport(reporte, parametros, dataSource);

            String userHome = System.getProperty("user.home");
            String outputPath = userHome + "/Desktop/CuposDisponibles" + "-" + ReportLogger.getNextReportNumber(CbxReport.getSelectionModel().getSelectedItem()) + ".pdf";

            JasperExportManager.exportReportToPdfFile(print, outputPath);

            Extras.showAlert("Éxito", "PDF exportado correctamente", Alert.AlertType.INFORMATION);
            ReportLogger.logReport(CbxReport.getSelectionModel().getSelectedItem());

            try {
                File pdfFile = new File(outputPath);
                if (pdfFile.exists() && Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);
                } else {
                    System.out.println("No se pudo abrir el PDF automáticamente.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    private void generateDocumentacionPendiente() throws ClassNotFoundException, SQLException, IOException {

        List<Map<String, Object>> data = reportDao.ReportFive(LocalDateTime.now().getYear());

        if (data.isEmpty()) {
            Extras.showAlert("Informacion", "No hay datos para mostrar", Alert.AlertType.ERROR);
            return;
        }

        Collection<Map<String, ?>> collection = new ArrayList<>(data);

        try {

            JasperReport reporte = (JasperReport) JRLoader.loadObject(getClass().getResource("/reports/MissingDocumentation.jasper"));
            JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(collection);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("NumeroReporte", ReportLogger.getNextReportNumber(CbxReport.getSelectionModel().getSelectedItem()));

            JasperPrint print = JasperFillManager.fillReport(reporte, parametros, dataSource);

            String userHome = System.getProperty("user.home");
            String outputPath = userHome + "/Desktop/DocumentacionPendiente" + "-" + ReportLogger.getNextReportNumber(CbxReport.getSelectionModel().getSelectedItem()) + ".pdf";

            JasperExportManager.exportReportToPdfFile(print, outputPath);

            Extras.showAlert("Éxito", "PDF exportado correctamente", Alert.AlertType.INFORMATION);
            ReportLogger.logReport(CbxReport.getSelectionModel().getSelectedItem());

            try {
                File pdfFile = new File(outputPath);
                if (pdfFile.exists() && Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);
                } else {
                    System.out.println("No se pudo abrir el PDF automáticamente.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    private void generateTutoresDeEstudiantes() throws ClassNotFoundException, SQLException, IOException {

        List<Map<String, Object>> data = reportDao.ReportSix(LocalDateTime.now().getYear());

        if (data.isEmpty()) {
            Extras.showAlert("Informacion", "No hay datos para mostrar", Alert.AlertType.ERROR);
            return;
        }

        Collection<Map<String, ?>> collection = new ArrayList<>(data);

        try {

            JasperReport reporte = (JasperReport) JRLoader.loadObject(getClass().getResource("/reports/StudentTutors.jasper"));
            JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(collection);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("NumeroReporte", ReportLogger.getNextReportNumber(CbxReport.getSelectionModel().getSelectedItem()));

            JasperPrint print = JasperFillManager.fillReport(reporte, parametros, dataSource);

            String userHome = System.getProperty("user.home");
            String outputPath = userHome + "/Desktop/TutoresdeEstudiates" + "-" + ReportLogger.getNextReportNumber(CbxReport.getSelectionModel().getSelectedItem()) + ".pdf";

            JasperExportManager.exportReportToPdfFile(print, outputPath);

            Extras.showAlert("Éxito", "PDF exportado correctamente", Alert.AlertType.INFORMATION);
            ReportLogger.logReport(CbxReport.getSelectionModel().getSelectedItem());

            try {
                File pdfFile = new File(outputPath);
                if (pdfFile.exists() && Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);
                } else {
                    System.out.println("No se pudo abrir el PDF automáticamente.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void BtnExportPDFAction(ActionEvent event) throws ClassNotFoundException, SQLException, IOException {

        int reportIndex = CbxReport.getSelectionModel().getSelectedIndex();

        switch (reportIndex) {
            case 0 ->
                generateDistribucionEstudiantil();
            case 1 ->
                generateInscripcionesGenerales();
            case 2 -> 
                generateInscripcionesporFechas();
            case 3 -> 
                generateCuposDisponibles();
            case 4 -> 
                generateDocumentacionPendiente();
            case 5 -> 
                generateTutoresDeEstudiantes();                
            default ->
                Extras.showAlert("Advertencia", "Debe seleccionar un Reporte Valido", Alert.AlertType.WARNING);
        }
        LoadReports();
    }

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

    private void LoadReports() {
        TableReports.getItems().clear();
        TableReports.getColumns().clear();

        List<String> result = ReportLogger.getMaxReportNumbersAllTypes();
        ObservableList<String[]> data = FXCollections.observableArrayList();

        for (String item : result) {
            String[] parts = item.split(",");
            if (parts.length >= 2) {
                String nombre = parts[0].trim();
                String cantidad = parts[1].trim();
                data.add(new String[]{nombre, cantidad});
            } else {
                System.out.println("Línea inválida: " + item);
            }
        }

        TableColumn<String[], String> nombreCol = new TableColumn<>("NOMBRE REPORTE");
        nombreCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[0]));

        TableColumn<String[], String> cantidadCol = new TableColumn<>("CANTIDAD DE REPORTES");
        cantidadCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[1]));

        //nombreCol.setPrefWidth(500);
        //cantidadCol.setPrefWidth(260);

        PieCharReports.getData().clear();
        PieCharReports.setTitle("DISTRIBUCION DE LOS REPORTES");
        PieCharReports.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        for (String[] item : data) {
            String nombre = item[0];
            String cantidadStr = item[1];
            try {
                int cantidad = Integer.parseInt(cantidadStr);
                PieChart.Data slice = new PieChart.Data(nombre, cantidad);
                PieCharReports.getData().add(slice);
            } catch (NumberFormatException e) {
                System.out.println("Cantidad inválida para el reporte '" + nombre + "': " + cantidadStr);
            }
        }
        animarPieChart(PieCharReports);

        TableReports.setItems(data);
        TableReports.getColumns().addAll(nombreCol, cantidadCol);
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

        ObservableList<String> reports = FXCollections.observableArrayList(this.report);
        CbxReport.setItems(reports);
        CbxReport.setValue("Seleccione");

        ObservableList<String> levels = FXCollections.observableArrayList(this.level);
        CbxLevel.setItems(levels);
        CbxLevel.setValue("Seleccione");

        Disable();

        LoadReports();

        CbxReport.setOnAction((var event) -> {
            int index = CbxReport.getSelectionModel().getSelectedIndex();

            switch (index) {
                case 0 -> {
                    Clean();
                    CbxLevel.setDisable(false);
                    DatePfi.setDisable(true);
                    DatePff.setDisable(true);
                }
                case 1 -> {
                    Clean();
                    Disable();
                }
                case 2 -> {
                    Clean();
                    DatePfi.setDisable(false);
                    DatePff.setDisable(false);
                    CbxLevel.setDisable(true);
                }
                case 3 -> {
                    Clean();
                    CbxLevel.setDisable(false);
                    DatePfi.setDisable(true);
                    DatePff.setDisable(true);
                }
                case 4 -> {
                    Clean();
                    Disable();
                }
                default -> {
                    Clean();
                    Disable();
                }
            }
        });

        try {
            reportDao = new ReportsDao();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ReportPDFController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
