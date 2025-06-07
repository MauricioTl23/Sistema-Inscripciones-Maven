/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import Dao.UserDao;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.User;

/**
 *
 * @author mauricioteranlimari
 */
public class RUsersController implements Initializable {

    @FXML
    private Button btnExport;

    @FXML
    private PieChart PieChartUsers;

    @FXML
    private ComboBox<String> CboCharge;
    @FXML
    private TableView tbUsers;

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

    private UserDao userdao;
    private FilteredList<User> filteredData;

    private ObservableList<User> data;

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

    private void PieChartUser() {
        PieChartUsers.setData(userdao.AmountofCharges());
        PieChartUsers.setTitle("DISTRIBUCION DE CARGOS");
        animarPieChart(PieChartUsers);
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

        String[] cargos = {"Director/a", "Secretario/a", "Asesor/a", "Regente/Regenta", "Todos"};
        ObservableList<String> items = FXCollections.observableArrayList(cargos);
        CboCharge.setItems(items);
        CboCharge.setValue("Seleccione");
        try {
            this.userdao = new UserDao();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ManageUsersController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(RUsersController.class.getName()).log(Level.SEVERE, null, ex);
        }
        LoadUsers();

        PieChartUser();

        CboCharge.valueProperty().addListener((observable, oldValue, newValue) -> {
            int selectedIndex = CboCharge.getSelectionModel().getSelectedIndex();

            filteredData.setPredicate(user -> {
                if (selectedIndex == -1 || newValue.equals("Seleccione") || newValue.equals("Todos")) {
                    return true; // Mostrar todos si no se ha seleccionado nada
                }

                return user.getCargo() == selectedIndex;
            });
        });

    }

    @FXML
    void Export(ActionEvent event) throws NoSuchAlgorithmException, Exception {
        exportToExcel(filteredData);
    }

    public void LoadUsers() {

        tbUsers.getItems().clear();
        tbUsers.getColumns().clear();

        List<User> users = this.userdao.tolist();

        ObservableList<User> data = FXCollections.observableArrayList(users);

        filteredData = new FilteredList<>(data, p -> true);
        tbUsers.setItems(filteredData);

        TableColumn Namecol = new TableColumn("NOMBRE(S)");
        Namecol.setCellValueFactory(new PropertyValueFactory("nombre"));

        TableColumn Surnamecol = new TableColumn("APELLIDO(S)");
        Surnamecol.setCellValueFactory(new PropertyValueFactory("apellido"));

        TableColumn CIcol = new TableColumn("CI");
        CIcol.setCellValueFactory(new PropertyValueFactory("cedula_identidad"));

        TableColumn Phonecol = new TableColumn("CELULAR");
        Phonecol.setCellValueFactory(new PropertyValueFactory("celular"));

        TableColumn Emailcol = new TableColumn("CORREO");
        Emailcol.setCellValueFactory(new PropertyValueFactory("correo"));

        TableColumn Chargecol = new TableColumn("CARGO");
        Chargecol.setCellValueFactory(new PropertyValueFactory("cargo"));
        Chargecol.setCellFactory(col -> new TableCell<User, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {

                    String[] cargos = {"Director/a", "Secretario/a", "Asesor/a", "Regente/a"};
                    setText(cargos[item]);

                }
            }
        });

        //tblUser.setItems(data);
        tbUsers.getColumns().addAll(Namecol, Surnamecol, CIcol, Phonecol, Emailcol, Chargecol);

    }

    public void exportToExcel(ObservableList<User> filteredData) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Usuarios");

        // Crear encabezados
        String[] headers = {"Nombre", "Apellido", "CI", "Correo", "Celular", "Cargo"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Agregar datos
        int rowNum = 1;
        for (User user : filteredData) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(user.getNombre());
            row.createCell(1).setCellValue(user.getApellido());
            row.createCell(2).setCellValue(user.getCedula_identidad());
            row.createCell(3).setCellValue(user.getCorreo());
            row.createCell(4).setCellValue(user.getCelular());
            row.createCell(5).setCellValue(user.getCargo());
        }

        // Autoajustar columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Guardar archivo con FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos Excel", "*.xlsx"));
        fileChooser.setInitialFileName("Usuarios.xlsx");

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
