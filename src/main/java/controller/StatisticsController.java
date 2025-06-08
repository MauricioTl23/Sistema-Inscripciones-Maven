/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import Dao.StatisticsDao;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.chart.Chart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;
import model.Extras;

/**
 * FXML Controller class
 *
 * @author mauricioteranlimari
 */
public class StatisticsController implements Initializable {

    @FXML
    private VBox MainVBox;

    @FXML
    private Rectangle rectangle1;

    @FXML
    private Rectangle rectangle2;

    @FXML
    private Rectangle rectangle3;

    @FXML
    private Rectangle rectangle4;

    @FXML
    private Rectangle RectangleGral;

    @FXML
    private StackPane stack1;

    @FXML
    private StackPane stack2;

    @FXML
    private StackPane stack3;

    @FXML
    private StackPane stack4;

    @FXML
    private StackPane stackGral;

    @FXML
    private ComboBox<String> CboxLevel1;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private CategoryAxis xAxis1;

    @FXML
    private NumberAxis yAxis1;

    @FXML
    private ScatterChart<Number, Number> AgeAndFinaAverage;

    @FXML
    private BarChart<String, Number> ApprovedVSFailed;

    @FXML
    private PieChart DistribucionodFaildAndApproved;

    @FXML
    private BarChart<String, Number> RegistrationsByYear;

    private StatisticsDao statsdao;

    private final String[] levels = {"Primaria", "Secundaria"};

    private boolean ExistData1 = false;
    private boolean ExistData2 = false;
    private boolean ExistData3 = false;
    private boolean ExistData4 = false;

    /**
     * Initializes the controller class.
     *
     * @param nivel
     * @param anio
     */
    public void cargarGraficoAprobadosVsReprobados(int nivel, int anio) {
        Map<String, Map<String, Integer>> resultados = statsdao.ApprovedvsFailed(nivel, anio);

        XYChart.Series<String, Number> serieAprobados = new XYChart.Series<>();
        serieAprobados.setName("Aprobados");

        XYChart.Series<String, Number> serieReprobados = new XYChart.Series<>();
        serieReprobados.setName("Reprobados");

        // Recorrer cada curso y obtener la cantidad de aprobados y reprobados
        for (Map.Entry<String, Map<String, Integer>> entry : resultados.entrySet()) {
            String curso = entry.getKey();
            Map<String, Integer> estados = entry.getValue();

            int aprobados = estados.getOrDefault("Aprobado", 0);
            int reprobados = estados.getOrDefault("Reprobado", 0);

            serieAprobados.getData().add(new XYChart.Data<>(curso, aprobados));
            serieReprobados.getData().add(new XYChart.Data<>(curso, reprobados));
        }

        ApprovedVSFailed.getData().clear();
        ApprovedVSFailed.getData().clear();
        if (serieAprobados.getData().isEmpty() && serieReprobados.getData().isEmpty()) {
            ApprovedVSFailed.setTitle("SIN DATOS");
            ExistData1 = false;
        } else {
            ApprovedVSFailed.setTitle("Aprobados vs Reprobados");
            ApprovedVSFailed.getData().addAll(serieAprobados, serieReprobados);
            ExistData1 = true;
        }
    }

    public void cargarGraficoInscripcionesPorAnio(int level) {
        Map<String, Integer> datos = statsdao.RegistrationByYear(level);

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Inscripciones");

        for (Map.Entry<String, Integer> entry : datos.entrySet()) {
            String yearStr = String.valueOf(entry.getKey());  // Convertir explícito a String
            serie.getData().add(new XYChart.Data<>(yearStr, entry.getValue()));
        }

        RegistrationsByYear.getData().clear();
        xAxis1.setLabel("Año");
        yAxis1.setLabel("Cantidad de inscripciones");
        if (serie.getData().isEmpty()) {
            RegistrationsByYear.setTitle("SIN DATOS");
            ExistData2 = false;
        } else {
            RegistrationsByYear.setTitle("Inscripciones por año");
            RegistrationsByYear.getData().add(serie);
            ExistData2 = true;
        }
    }

    public void cargarDistribucionAprobadosReprobados(int level, int year) {
        Map<String, Integer> datos = statsdao.DistribucionodFaildAndApproved(level, year);

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();

        for (Map.Entry<String, Integer> entry : datos.entrySet()) {
            pieData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        if (!pieData.isEmpty()) {
            DistribucionodFaildAndApproved.setTitle("Distribucion General de Reprobados");
            DistribucionodFaildAndApproved.setData(pieData);
            ExistData3 = true;
        } else {
            DistribucionodFaildAndApproved.setTitle("SIN DATOS");
            ExistData3 = false;
        }

    }

    public void cargarAgevsAVG(int level, int year) {
        ObservableList<XYChart.Data<Number, Number>> data = statsdao.AgevsAVG(level, year);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Edad vs Promedio");
        series.setData(data);

        AgeAndFinaAverage.getData().clear();
        if (data.isEmpty()) {
            AgeAndFinaAverage.setTitle("SIN DATOS");
            ExistData4 = false;
        } else {
            AgeAndFinaAverage.setTitle("Edad vs Promedio");
            AgeAndFinaAverage.getData().add(series);
            ExistData4 = true;
        }
    }

    public void exportChartToPDF(Chart chart, String titulo, String reporte) {
        try {
            String outputPath = System.getProperty("user.home") + "/Desktop/" + reporte + ".pdf";

            WritableImage snapshot = chart.snapshot(null, null);
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            baos.flush();
            byte[] imageBytes = baos.toByteArray();
            baos.close();

            Document document = new Document();

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputPath));

            document.open();

            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1, 2});

            InputStream logoStream = getClass().getResourceAsStream("/icons/logoC.png");
            if (logoStream == null) {
                throw new RuntimeException("No se encontró el recurso /icons/logoC.png");
            }
            byte[] logoBytes = logoStream.readAllBytes();
            Image logo = Image.getInstance(logoBytes);
            logo.scaleToFit(110, 110);

            PdfPCell logoCell = new PdfPCell(logo, false);
            logoCell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerTable.addCell(logoCell);

            String fechaActual = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            String textoFecha = "Fecha: " + fechaActual;
            Font fechaFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.DARK_GRAY);
            PdfPCell fechaCell = new PdfPCell(new Phrase(textoFecha, fechaFont));
            fechaCell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
            fechaCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            fechaCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerTable.addCell(fechaCell);

            document.add(headerTable);

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph(titulo, titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingBefore(10f);
            title.setSpacingAfter(20f);
            document.add(title);

            Image pdfImage = Image.getInstance(imageBytes);
            pdfImage.scaleToFit(document.getPageSize().getWidth() - 50, document.getPageSize().getHeight() - 150);
            pdfImage.setAlignment(Element.ALIGN_CENTER);
            document.add(pdfImage);

            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.DARK_GRAY);
            String mensajeFinal = "Este reporte presenta un análisis detallado de las estadísticas correspondientes a " + titulo + ".";

            ColumnText column = new ColumnText(writer.getDirectContent());

            float left = document.leftMargin();
            float right = document.getPageSize().getWidth() - document.rightMargin();
            float bottom = document.bottomMargin() - 10;  
            float top = document.bottomMargin() + 20;     

            column.setSimpleColumn(left, bottom, right, top);

            Paragraph p = new Paragraph(mensajeFinal, footerFont);
            p.setAlignment(Element.ALIGN_CENTER);

            column.addElement(p);
            column.go();

            document.close();

            File pdfFile = new File(outputPath);
            if (pdfFile.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
            }
            System.out.println("PDF generado correctamente: " + outputPath);

        } catch (DocumentException | IOException | RuntimeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        stackGral.prefHeightProperty().bind(MainVBox.heightProperty().multiply(1.5 / 10.0));
        stack1.prefHeightProperty().bind(MainVBox.heightProperty().multiply(4.25 / 10.0));
        stack2.prefHeightProperty().bind(MainVBox.heightProperty().multiply(4.25 / 10.0));
        stack3.prefHeightProperty().bind(MainVBox.heightProperty().multiply(4.25 / 10.0));
        stack3.prefHeightProperty().bind(MainVBox.heightProperty().multiply(4.25 / 10.0));

        stackGral.prefWidthProperty().bind(MainVBox.widthProperty().multiply(1));
        stack1.prefWidthProperty().bind(MainVBox.widthProperty().multiply(5.0 / 10.0));
        stack2.prefWidthProperty().bind(MainVBox.widthProperty().multiply(5.0 / 10.0));
        stack3.prefWidthProperty().bind(MainVBox.widthProperty().multiply(5.0 / 10.0));
        stack4.prefWidthProperty().bind(MainVBox.widthProperty().multiply(5.0 / 10.0));

        RectangleGral.widthProperty().bind(stackGral.widthProperty());
        RectangleGral.heightProperty().bind(stackGral.heightProperty());

        rectangle1.widthProperty().bind(stack1.widthProperty());
        rectangle1.heightProperty().bind(stack1.heightProperty());

        rectangle2.widthProperty().bind(stack2.widthProperty());
        rectangle2.heightProperty().bind(stack2.heightProperty());

        rectangle3.widthProperty().bind(stack3.widthProperty());
        rectangle3.heightProperty().bind(stack3.heightProperty());

        rectangle4.widthProperty().bind(stack4.widthProperty());
        rectangle4.heightProperty().bind(stack4.heightProperty());

        try {
            statsdao = new StatisticsDao();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex);
        }

        ObservableList<String> itemsLevel = FXCollections.observableArrayList(this.levels);

        CboxLevel1.setItems(itemsLevel);
        CboxLevel1.setValue("Seleccione");

        CboxLevel1.setOnAction(event -> {
            if (CboxLevel1.getSelectionModel().getSelectedIndex() != -1) {
                AgeAndFinaAverage.getData().clear();
                ApprovedVSFailed.getData().clear();
                DistribucionodFaildAndApproved.getData().clear();
                RegistrationsByYear.getData().clear();
                cargarGraficoAprobadosVsReprobados(CboxLevel1.getSelectionModel().getSelectedIndex(), LocalDateTime.now().getYear());
                cargarGraficoInscripcionesPorAnio(CboxLevel1.getSelectionModel().getSelectedIndex());
                cargarDistribucionAprobadosReprobados(CboxLevel1.getSelectionModel().getSelectedIndex(), LocalDateTime.now().getYear());
                cargarAgevsAVG(CboxLevel1.getSelectionModel().getSelectedIndex(), LocalDateTime.now().getYear());
            }
        });

        ApprovedVSFailed.setOnMouseClicked(event -> {
            if (Extras.showConfirmation("Desea exportar a PDF")) {
                if (ExistData1) {
                    exportChartToPDF(ApprovedVSFailed, "Aprobados vs Reprobados", "ApprovedVSFailed");
                } else {
                    Extras.showAlert("Advertencia", "No existen datos", Alert.AlertType.WARNING);
                }
            }
        });

        AgeAndFinaAverage.setOnMouseClicked(event -> {
            if (Extras.showConfirmation("Desea exportar a PDF")) {
                if (ExistData2) {
                    exportChartToPDF(AgeAndFinaAverage, "Edad vs Promedio Nota Final", "AgeAndFinaAverage");
                } else {
                    Extras.showAlert("Advertencia", "No existen datos", Alert.AlertType.WARNING);
                }
            }
        });

        DistribucionodFaildAndApproved.setOnMouseClicked(event -> {
            if (Extras.showConfirmation("Desea exportar a PDF")) {
                if (ExistData3) {
                    exportChartToPDF(DistribucionodFaildAndApproved, "Distribucion de Aprobados y Reprobados", "DistribucionodFaildAndApproved");
                } else {
                    Extras.showAlert("Advertencia", "No existen datos", Alert.AlertType.WARNING);
                }
            }
        });

        RegistrationsByYear.setOnMouseClicked(event -> {
            if (Extras.showConfirmation("Desea exportar a PDF")) {
                if (ExistData4) {
                    exportChartToPDF(RegistrationsByYear, "Inscripciones por año", "RegistrationsByYear");
                } else {
                    Extras.showAlert("Advertencia", "No existen datos", Alert.AlertType.WARNING);
                }
            }
        });

    }

}
