/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import interfaces.DataReceiver;
import interfaces.MainControllerAware;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import model.Extras;
import model.User;

/**
 *
 * @author mauricioteranlimari
 */
public class MenuOptionsController implements Initializable, MainControllerAware, DataReceiver {

    @FXML
    private HBox MainHBox;

    @FXML
    private StackPane stackPane1;

    @FXML
    private StackPane stackPane2;

    @FXML
    private StackPane stackPane3;

    @FXML
    private Button btnGestionarEstudiantes;

    @FXML
    private Button btnConfiguracion;

    @FXML
    private Button btnReportes;

    @FXML
    private Button btnGestionarNotas;

    @FXML
    private Button btnAyuda;

    @FXML
    private Button btnGestionarUsuarios;

    @FXML
    private ImageView ihelp;

    @FXML
    private ImageView inotes;

    @FXML
    private ImageView ireports;

    @FXML
    private ImageView isettings;

    @FXML
    private ImageView istudents;

    @FXML
    private ImageView iusers;

    @FXML
    private Rectangle Rectangle1;

    @FXML
    private Rectangle Rectangle2;

    @FXML
    private Rectangle Rectangle3;

    private final Map<String, String> pageMap = new HashMap<>();

    private MainMenuController mainController;

    public User logged;

    public void setLogged(User logged) {
        this.logged = logged;
    }

    public User getLogged() {
        return logged;
    }

    private double mult1;
    private double mult2;
    private double mult3;

    @Override
    public void onDataReceived(Object data) {
        if (data instanceof User user) {
            this.logged = user;
            System.out.println("Usuario recibido: " + user.getNombre() + " " + user.getApellido());
            /*if (user.getCargo() == 0) {
                SettingsView(1.0 / 3.0, 1.0 / 3.0, 1.0 / 3.0);
            } else if (user.getCargo() == 1 || user.getCargo() == 2 || user.getCargo() == 3) {
                SettingsView(1.0 / 2.0, 0.0 / 2.0, 1.0 / 2.0);
            }*/
        }
    }

    private void SettingsView(double m1, double m2, double m3) {
        stackPane1.prefWidthProperty().bind(MainHBox.widthProperty().multiply(m1));

        /*if (m2 == 0) {
            stackPane2.setVisible(false);
            stackPane2.setManaged(false);
        } else {
            stackPane2.setVisible(true);
            stackPane2.setManaged(true);
            stackPane2.prefWidthProperty().bind(MainHBox.widthProperty().multiply(m2));
        }*/
        stackPane2.prefWidthProperty().bind(MainHBox.widthProperty().multiply(m2));

        stackPane3.prefWidthProperty().bind(MainHBox.widthProperty().multiply(m3));

        stackPane1.prefHeightProperty().bind(MainHBox.heightProperty());
        stackPane2.prefHeightProperty().bind(MainHBox.heightProperty());
        stackPane3.prefHeightProperty().bind(MainHBox.heightProperty());

        Rectangle1.widthProperty().bind(stackPane1.widthProperty());
        Rectangle1.heightProperty().bind(stackPane1.heightProperty());

        Rectangle2.widthProperty().bind(stackPane2.widthProperty());
        Rectangle2.heightProperty().bind(stackPane2.heightProperty());

        Rectangle3.widthProperty().bind(stackPane3.widthProperty());
        Rectangle3.heightProperty().bind(stackPane3.heightProperty());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Image student = new Image(getClass().getResourceAsStream("/icons/student.gif"));
        istudents.setImage(student);
        Image note = new Image(getClass().getResourceAsStream("/icons/note.gif"));
        inotes.setImage(note);
        Image user = new Image(getClass().getResourceAsStream("/icons/user.gif"));
        iusers.setImage(user);
        Image setting = new Image(getClass().getResourceAsStream("/icons/setting.gif"));
        isettings.setImage(setting);
        Image report = new Image(getClass().getResourceAsStream("/icons/report.gif"));
        ireports.setImage(report);
        Image help = new Image(getClass().getResourceAsStream("/icons/help.gif"));
        ihelp.setImage(help);
        
        SettingsView(1/3, 1/3, 1/3);

        pageMap.put("Gestionar Estudiantes", "ManageStudents");
        pageMap.put("Configuracion", "SchoolSettings");
        pageMap.put("Reportes", "Report");
        pageMap.put("Gestionar Notas", "ManageGrades");
        pageMap.put("Ayuda", "Help");
        pageMap.put("Gestionar Usuarios", "ManageUsers");

        btnGestionarEstudiantes.setOnAction(e -> navigateTo("Gestionar Estudiantes"));
        btnConfiguracion.setOnAction(e -> navigateTo("Configuracion"));
        btnReportes.setOnAction(e -> navigateTo("Reportes"));
        btnGestionarNotas.setOnAction(e -> navigateTo("Gestionar Notas"));
        btnAyuda.setOnAction(e -> navigateTo("Ayuda"));
        btnGestionarUsuarios.setOnAction(e -> navigateTo("Gestionar Usuarios"));

    }

    public void navigateTo(String pageName) {
        System.out.println("Presionado " + pageName);
        if (mainController != null) {
            if (pageName.equals("Gestionar Notas")) {
                mainController.loadSceneWithData("ManageGrades", mainController.getLogged());
            } else {
                mainController.loadView(pageMap.get(pageName));
            }
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
