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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
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

    private final String[] level = {"Primaria", "Secundaria"};
    //private final String[] grade = {"Primero", "Segundo", "Tercer", "Cuarto", "Quinto", "Sexto"};
    private final String[] report = {"Distribución Estudiantil", "Inscripciones General", "Inscripciones por Fechas", "Cupos de los Cursos", "Estudiantes con Documentación Pendiente"};

    ReportsDao reportDao;

    private void Disable() {
        CbxLevel.setDisable(true);
        DatePfi.setDisable(true);
        DatePff.setDisable(true);
    }
    
    private void Clean(){
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
            parametros.put("NumeroReporte", ReportLogger.getNextReportNumber());
            parametros.put("LevelText", CbxLevel.getSelectionModel().getSelectedItem());

            JasperPrint print = JasperFillManager.fillReport(reporte, parametros, dataSource);

            String userHome = System.getProperty("user.home");
            String outputPath = userHome + "/Desktop/DistribucionEstudiantil"+"-"+ReportLogger.getNextReportNumber()+".pdf";

            JasperExportManager.exportReportToPdfFile(print, outputPath);

            Extras.showAlert("Éxito", "PDF exportado correctamente", Alert.AlertType.INFORMATION);
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
    
    public void generateInscripcionesGenerales(){
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
            parametros.put("NumeroReporte", ReportLogger.getNextReportNumber());

            JasperPrint print = JasperFillManager.fillReport(reporte, parametros, dataSource);

            String userHome = System.getProperty("user.home");
            String outputPath = userHome + "/Desktop/InscripcionesGenerales"+"-"+ReportLogger.getNextReportNumber()+".pdf";

            JasperExportManager.exportReportToPdfFile(print, outputPath);

            Extras.showAlert("Éxito", "PDF exportado correctamente", Alert.AlertType.INFORMATION);
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
            case 0 -> {
                generateDistribucionEstudiantil();
                ReportLogger.logReport(CbxReport.getSelectionModel().getSelectedItem());
            }
            case 1 ->{
                generateInscripcionesGenerales();
                ReportLogger.logReport(CbxReport.getSelectionModel().getSelectedItem());
            }
            case 2 ->
                System.out.println("Inscripciones por Fechas (pendiente)");
            case 3 ->
                System.out.println("Cupos de los Cursos (pendiente)");
            case 4 ->
                System.out.println("Estudiantes con Documentación Pendiente (pendiente)");
            default ->
                Extras.showAlert("Advertencia", "Debe seleccionar un Reporte Valido", Alert.AlertType.WARNING);
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

        ObservableList<String> reports = FXCollections.observableArrayList(this.report);
        CbxReport.setItems(reports);
        CbxReport.setValue("Seleccione");

        ObservableList<String> levels = FXCollections.observableArrayList(this.level);
        CbxLevel.setItems(levels);
        CbxLevel.setValue("Seleccione");

        Disable();

        CbxReport.setOnAction(event -> {
            int index = CbxReport.getSelectionModel().getSelectedIndex();

            switch (index) {
                case 0 -> {
                    Clean();
                    CbxLevel.setDisable(false);
                    DatePfi.setDisable(true);
                    DatePff.setDisable(true);
                }
                case 1 ->{
                    Clean();
                    Disable();
                }
                case 2 -> {
                    Clean();
                    DatePfi.setDisable(false);
                    DatePff.setDisable(false);
                    CbxLevel.setDisable(true);
                }
                case 3 ->{
                    Clean();
                    Disable();
                }
                case 4 ->{
                    Clean();
                    Disable();
                }
                default ->{
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
