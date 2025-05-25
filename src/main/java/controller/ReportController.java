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
import javafx.scene.image.ImageView;
import interfaces.MainControllerAware;

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
    private Button btnNotifications;

    @FXML
    private ImageView imgStudents;

    @FXML
    private ImageView imgUsers;

    @FXML
    private ImageView imgCourse;

    @FXML
    private ImageView imgNotify;

    @FXML
    private ImageView imgNotifications;

    private MainMenuController mainController;

    private final Map<String, String> pageMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        /*
        imgStudents.setImage(new Image(getClass().getResourceAsStream("/resources/icons/student.gif")));
        imgUsers.setImage(new Image(getClass().getResourceAsStream("/resources/icons/user.gif")));
        imgCourse.setImage(new Image(getClass().getResourceAsStream("/resources/icons/course.gif")));
        imgNotify.setImage(new Image(getClass().getResourceAsStream("/resources/icons/notify.gif")));
        imgNotifications.setImage(new Image(getClass().getResourceAsStream("/resources/icons/notification.gif")));
         */
        // Asociar páginas
        pageMap.put("Usuarios", "RUsers");
        pageMap.put("Curso", "RCourses");
        pageMap.put("Notificar", "Notify");
        pageMap.put("Notificaciones", "Notifications");

        // Asignar eventos a botones
        btnUsers.setOnAction(e -> navigateTo("Usuarios"));
        btnCourse.setOnAction(e -> navigateTo("Curso"));
        btnNotify.setOnAction(e -> navigateTo("Notificar"));
        btnNotifications.setOnAction(e -> navigateTo("Notificaciones"));
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
