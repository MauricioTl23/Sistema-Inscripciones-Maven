/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import interfaces.MainControllerAware;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author mauricioteranlimari
 */
public class ReportController implements Initializable, MainControllerAware {

    @FXML
    private Button btnUsers;

    @FXML
    private Button btnCourse;

    @FXML
    private Button btnNotify;


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
    private HBox MainHBox;

    @FXML
    private ImageView IAcademic1;

    @FXML
    private ImageView IAcademic2;

    @FXML
    private ImageView IAdministrative1;

    @FXML
    private ImageView IAdministrative2;

    @FXML
    private ImageView INotify2;
    
    @FXML
    private Button BtnReportPDF;

    @FXML
    private Button BtnStatistics;

    private MainMenuController mainController;

    private final Map<String, String> pageMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        stack1.prefWidthProperty().bind(MainHBox.widthProperty().multiply(1.0 / 3.0));
        stack2.prefWidthProperty().bind(MainHBox.widthProperty().multiply(1.0 / 3.0));
        stack3.prefWidthProperty().bind(MainHBox.widthProperty().multiply(1.0 / 3.0));

        stack1.prefHeightProperty().bind(MainHBox.heightProperty());
        stack2.prefHeightProperty().bind(MainHBox.heightProperty());
        stack3.prefHeightProperty().bind(MainHBox.heightProperty());

        rectangle1.widthProperty().bind(stack1.widthProperty());
        rectangle1.heightProperty().bind(stack1.heightProperty());

        rectangle2.widthProperty().bind(stack2.widthProperty());
        rectangle2.heightProperty().bind(stack2.heightProperty());

        rectangle3.widthProperty().bind(stack3.widthProperty());
        rectangle3.heightProperty().bind(stack3.heightProperty());

        //Imagenes
        Image icon1Acad = new Image(getClass().getResourceAsStream("/icons/ListStudents.gif"));
        IAcademic1.setImage(icon1Acad);
        
        Image icon2Acad = new Image(getClass().getResourceAsStream("/icons/ReportStudents.gif"));
        IAcademic2.setImage(icon2Acad);
        
        Image icon1Admin = new Image(getClass().getResourceAsStream("/icons/ListUsers.gif"));
        IAdministrative1.setImage(icon1Admin);
        
        Image icon2Admin = new Image(getClass().getResourceAsStream("/icons/ReportUsers.gif"));
        IAdministrative2.setImage(icon2Admin);
        
        
        Image icon2Not = new Image(getClass().getResourceAsStream("/icons/NotifyPPFF.gif"));
        INotify2.setImage(icon2Not);

        // Asociar páginas
        pageMap.put("Usuarios", "RUsers");
        pageMap.put("Curso", "RCourses");
        pageMap.put("Notificar", "Notify");
        pageMap.put("Notificaciones", "Notifications");
        pageMap.put("ReportesPDF", "ReportPDF");
        pageMap.put("Estadisticas", "Statistics");

        // Asignar eventos a botones
        btnUsers.setOnAction(e -> navigateTo("Usuarios"));
        btnCourse.setOnAction(e -> navigateTo("Curso"));
        btnNotify.setOnAction(e -> navigateTo("Notificar"));
        BtnReportPDF.setOnAction(e -> navigateTo("ReportesPDF"));
        BtnStatistics.setOnAction(e -> navigateTo("Estadisticas"));
    }

    private void navigateTo(String pageName) {
        System.out.println("Presionado " + pageName);
        if (mainController != null) {
            mainController.loadView(pageMap.get(pageName));
            mainController.addPage(pageName, pageMap.get(pageName));
        } else {
            System.out.println("Error: MainMenuController no está disponible.");
        }
    }

    @Override
    public void setMainController(MainMenuController mainController) {
        this.mainController = mainController;
    }
}
