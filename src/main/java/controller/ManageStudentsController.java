/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import interfaces.MainControllerAware;
import java.net.URL;
import java.util.List;
import javafx.collections.transformation.FilteredList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Student;
import Dao.StudentDao;
import Dao.GuardianDao;
import Dao.Student_GuardianDao;
import java.sql.SQLException;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.StageStyle;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.InputStream;

/**
 *
 * @author mauricioteranlimari
 */
public class ManageStudentsController implements Initializable, MainControllerAware {

    @FXML
    private Button btnEstudianteNuevo;
    @FXML
    private TableView<Student> TblStudent;
    @FXML
    private TextField TextSearch;

    private StudentDao studentDao;
    private GuardianDao guardianDao;
    private Student_GuardianDao student_GuardianDao;

    private ContextMenu OptionsStudents;

    private Student selectStudent;

    private MainMenuController mainController;

    private ObservableList<Student> data;
    private FilteredList<Student> filteredData;

    @Override
    public void setMainController(MainMenuController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnEstudianteNuevo.setOnAction(e -> navigateTo("Estudiante Nuevo", "ExistingStudent"));
        try {
            this.studentDao = new StudentDao();
            this.guardianDao = new GuardianDao();
            this.student_GuardianDao = new Student_GuardianDao();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ManageUsersController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ManageStudentsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        LoadStudents();

        OptionsStudents = new ContextMenu();
        MenuItem editE = new MenuItem("Editar Estudiante");

        MenuItem editT = new MenuItem("Editar Tutor");
        MenuItem delete = new MenuItem("Eliminar Estudiante");
        OptionsStudents.getItems().addAll(editE, editT, delete);

        editE.setOnAction((ActionEvent t) -> {
            Student est = new Student();

            int index = TblStudent.getSelectionModel().getSelectedIndex();
            selectStudent = TblStudent.getItems().get(index);
            if (mainController != null) {
                mainController.loadSceneWithData("ExistingStudent", selectStudent);
                mainController.addPage("Estudiante " + selectStudent.getNombre() + " " + selectStudent.getApellido(), "ExistingStudent");

            } else {
                System.out.println("Error: MainMenuController no está disponible.");
            }
        });
        editT.setOnAction((ActionEvent t) -> {
            Student est = new Student();

            int index = TblStudent.getSelectionModel().getSelectedIndex();
            selectStudent = TblStudent.getItems().get(index);
            if (mainController != null) {
                mainController.loadSceneWithData("Guardian", selectStudent.getId());
                mainController.addPage("Tutores de " + selectStudent.getNombre() + " " + selectStudent.getApellido(), "Guardian");

            } else {
                System.out.println("Error: MainMenuController no está disponible.");
            }
        });

        delete.setOnAction((ActionEvent t) -> {

            int index = TblStudent.getSelectionModel().getFocusedIndex();

            Student deleteStudent = TblStudent.getItems().get(index);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

            alert.setTitle("Confirmacion");
            alert.setHeaderText(null);
            alert.setContentText("¿Desea eliminar el usuario: "
                    + deleteStudent.getNombre() + " " + deleteStudent.getApellido()
                    + "?");
            alert.initStyle(StageStyle.UTILITY);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.OK) {

                boolean rsp = studentDao.Delete(deleteStudent.getId());
                student_GuardianDao.deleteRelationsByStudentId(deleteStudent.getId());
                guardianDao.deleteOrphanTutorsByStudent(deleteStudent.getId());

                if (rsp) {
                    LoadStudents();
                    Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                    alert2.setTitle("Exito");
                    alert2.setHeaderText(null);
                    alert2.setContentText("Se elimino correctamente el usuario");
                    alert2.initStyle(StageStyle.UTILITY);
                    alert2.showAndWait();
                } else {

                    Alert alert2 = new Alert(Alert.AlertType.ERROR);
                    alert2.setTitle("Error");
                    alert2.setHeaderText(null);
                    alert2.setContentText("Hubo un error al eliminar");
                    alert2.initStyle(StageStyle.UTILITY);
                    alert2.showAndWait();
                }
            }
            generarCarta(deleteStudent.getNombre() + " " + deleteStudent.getApellido(), deleteStudent.getCedula_identidad());
        });

        TblStudent.setContextMenu(OptionsStudents);

        TextSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            // Realiza el filtro dinámico mientras el usuario escribe
            filteredData.setPredicate(student -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                return student.getCedula_identidad().toLowerCase().contains(newValue.toLowerCase());
            });
        });
    }

    public void navigateTo(String pageName, String fxmlName) {
        System.out.println("Presionado " + pageName);
        if (mainController != null) {
            mainController.loadView(fxmlName);
            mainController.addPage(pageName, fxmlName);
        } else {
            System.out.println("Error: MainMenuController no está disponible.");
        }
    }

    public void LoadStudents() {

        TblStudent.setItems(FXCollections.observableArrayList());
        TblStudent.getColumns().clear();

        List<Student> students = this.studentDao.tolist();
        data = FXCollections.observableArrayList(students); // Actualiza la variable global
        filteredData = new FilteredList<>(data, p -> true);
        TblStudent.setItems(filteredData);

        TableColumn<Student, Integer> Idcol = new TableColumn<>("ID");
        Idcol.setCellValueFactory(new PropertyValueFactory<>("id"));

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

        TblStudent.getColumns().addAll(Idcol, Namecol, Surnamecol, datecol, CIcol, gendercol, adresscol, Emailcol);
        TblStudent.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void generarCarta(String nombre, String ci) {
        try {
            Document doc = new Document();
            String nombreArchivo = "constancia_retiro_" + nombre.replace(" ", "_") + ".pdf";
            PdfWriter.getInstance(doc, new FileOutputStream(nombreArchivo));
            doc.open();

            // Logo de la unidad educativa
            try {
                InputStream logoStream = getClass().getResourceAsStream("/resources/icons/logoC.png");
                byte[] logoBytes = logoStream.readAllBytes(); // desde Java 9+
                Image logo = Image.getInstance(logoBytes);

                logo.scaleToFit(80, 80);
                logo.setAlignment(Image.ALIGN_LEFT);
                doc.add(logo);
            } catch (Exception e) {
                System.out.println("No se pudo cargar el logo. Se continúa sin imagen.");
            }

            // Nombre de la unidad educativa
            Paragraph nombreUnidad = new Paragraph("UNIDAD EDUCATIVA “JORGE OBLITAS”",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            nombreUnidad.setAlignment(Element.ALIGN_CENTER);
            nombreUnidad.setSpacingAfter(10);
            doc.add(nombreUnidad);

            // Título de la carta
            Paragraph titulo = new Paragraph("CONSTANCIA DE RETIRO",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            doc.add(titulo);

            // Fecha actual
            String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy"));

            // Cuerpo del documento
            Font cuerpoFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            String cuerpoTexto
                    = "Por medio de la presente, la Unidad Educativa “Jorge Oblitas” hace constar que el/la estudiante "
                    + nombre + ", portador(a) del C.I. Nº " + ci + ", ha sido retirado(a) oficialmente de esta institución educativa "
                    + "con fecha " + fecha + ".\n\n"
                    + "Esta constancia se emite a solicitud del/de la interesado(a) para los fines que estime conveniente.";

            Paragraph cuerpo = new Paragraph(cuerpoTexto, cuerpoFont);
            cuerpo.setAlignment(Element.ALIGN_JUSTIFIED);
            cuerpo.setSpacingAfter(30);
            doc.add(cuerpo);

            // "Atentamente," alineado a la izquierda
            Paragraph atentamente = new Paragraph("Atentamente,", cuerpoFont);
            atentamente.setAlignment(Element.ALIGN_LEFT);
            atentamente.setSpacingBefore(40);
            doc.add(atentamente);

            // Firma alineada al centro sin línea
            Paragraph firma = new Paragraph("\n\n\n\nFirma del Responsable\nDirección de la Unidad Educativa", cuerpoFont);
            firma.setAlignment(Element.ALIGN_CENTER);
            doc.add(firma);

            // Lugar y fecha al final
            Paragraph lugarFecha = new Paragraph("Oruro, " + fecha, cuerpoFont);
            lugarFecha.setAlignment(Element.ALIGN_RIGHT);
            lugarFecha.setSpacingBefore(20);
            doc.add(lugarFecha);

            doc.close();

            // Abrir el PDF automáticamente
            Desktop.getDesktop().open(new File(nombreArchivo));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
