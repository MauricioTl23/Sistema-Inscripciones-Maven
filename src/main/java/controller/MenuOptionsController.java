/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import interfaces.MainControllerAware;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author mauricioteranlimari
 */
public class MenuOptionsController implements Initializable, MainControllerAware {

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

    private final Map<String, String> pageMap = new HashMap<>();

    private MainMenuController mainController;

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
