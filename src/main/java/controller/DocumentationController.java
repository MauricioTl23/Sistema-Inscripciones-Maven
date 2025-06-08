/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import interfaces.DataReceiver;
import interfaces.MainControllerAware;
import model.SubmittedDocument;
import model.Documentation;
import Dao.SubmittedDocumentDao;
import Dao.DocumentationDao;
import Dao.EnrollementDao;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import model.Extras;

public class DocumentationController implements Initializable, MainControllerAware, DataReceiver {
    
    
    @FXML
    private Button BtnSave;
    
    @FXML
    private TableView<SubmittedDocument> TblDocuments;
    
     @FXML
    private VBox MainVBox;
     
    @FXML
    private StackPane Stack1;

    @FXML
    private StackPane Stack2;
    
    @FXML
    private Rectangle rectangle1;

    @FXML
    private Rectangle rectangle2;
    
    private SubmittedDocumentDao submittedDocumentDao;
    private DocumentationDao documentationDao;
    private EnrollementDao enrollementDao;
    
    
    private MainMenuController mainController;
    private int idInscripcion;
    private int idStudent;
    private ObservableList<SubmittedDocument> data;
    private boolean cambiosDetectados = false;
    Extras extras;
    
    @Override
    public void onDataReceived(Object data) {
        idStudent = (int)data;
        idInscripcion = enrollementDao.getByIdStudent(idStudent);
        try {
            submittedDocumentDao.register(idInscripcion);
        } catch (Exception ex) {
            Logger.getLogger(DocumentationController.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Idisncripcion "+idInscripcion);
        System.out.println("id estudiante "+idStudent);
        try {
            LoadDocuments(idInscripcion);
        } catch (Exception ex) {
            Logger.getLogger(DocumentationController.class.getName()).log(Level.SEVERE, null, ex);
        }
        showData();
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        Stack1.prefHeightProperty().bind(MainVBox.heightProperty().multiply(8.0 / 10.0));
        Stack2.prefHeightProperty().bind(MainVBox.heightProperty().multiply(2.0 / 10.0));

        Stack1.prefWidthProperty().bind(MainVBox.widthProperty());
        Stack2.prefWidthProperty().bind(MainVBox.widthProperty());
        
        rectangle1.widthProperty().bind(Stack1.widthProperty());
        rectangle1.heightProperty().bind(Stack1.heightProperty());

        rectangle2.widthProperty().bind(Stack2.widthProperty());
        rectangle2.heightProperty().bind(Stack2.heightProperty());


        try {
            this.submittedDocumentDao = new SubmittedDocumentDao();
            this.documentationDao = new DocumentationDao();
            this.enrollementDao = new EnrollementDao();

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ManageUsersController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DocumentationController.class.getName()).log(Level.SEVERE, null, ex);
        }
        BtnSave.setVisible(false);
        
    }

    @Override
    public void setMainController(MainMenuController mainController) {
        this.mainController = mainController;
    }
    
    public void LoadDocuments(int idInscripcion) throws Exception {
        TblDocuments.setItems(FXCollections.observableArrayList());
        TblDocuments.getColumns().clear();

        // Obtener tipos de documentación y mapearlos por ID
        List<Documentation> allDocs = this.documentationDao.toList();
        Map<Integer, Documentation> docMap = new HashMap<>();
        for (Documentation doc : allDocs) {
            docMap.put(doc.getIdtipo_documento(), doc);
        }

        List<SubmittedDocument> entregados = this.submittedDocumentDao.getByInscripcion(idInscripcion);
        data = FXCollections.observableArrayList(entregados);
        TblDocuments.setItems(data);

        // Columna: Nombre del documento
        TableColumn<SubmittedDocument, String> colNombre = new TableColumn<>("Documento");
        colNombre.setCellValueFactory(cellData -> {
            Documentation doc = docMap.get(cellData.getValue().getId_tipodocumento());
            return new SimpleStringProperty(doc != null ? doc.getNombre() : "Desconocido");
        });

        // Columna: ¿Obligatorio?
        TableColumn<SubmittedDocument, String> colObligatorio = new TableColumn<>("¿Obligatorio?");
        colObligatorio.setCellValueFactory(cellData -> {
            Documentation doc = docMap.get(cellData.getValue().getId_tipodocumento());
            return new SimpleStringProperty((doc != null && doc.isObligatorio()) ? "Sí" : "No");
        });

        // Columna: ¿Entregado?
        TableColumn<SubmittedDocument, Integer> colEntregado = new TableColumn<>("¿Entregado?");
        colEntregado.setCellValueFactory(new PropertyValueFactory<>("estado")); // estado es int: 0 o 1

        colEntregado.setCellFactory(col -> new TableCell<SubmittedDocument, Integer>() {
        @Override
        protected void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                return;
            }

            HBox box = new HBox(5);
            ToggleGroup toggleGroup = new ToggleGroup();
            RadioButton rbSi = new RadioButton("Sí");
            RadioButton rbNo = new RadioButton("No");
            rbSi.setToggleGroup(toggleGroup);
            rbNo.setToggleGroup(toggleGroup);

            SubmittedDocument doc = getTableView().getItems().get(getIndex());

            if (doc.getEstado() == 1) {
                rbSi.setSelected(true);
            } else {
                rbNo.setSelected(true);
            }

            toggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
                if (newToggle == rbSi) {
                    doc.setEstado(1);
                } else if (newToggle == rbNo) {
                    doc.setEstado(0);
                }
                showData();
                if (!cambiosDetectados) {
                    cambiosDetectados = true;
                    BtnSave.setVisible(true); // Mostrar botón solo la primera vez
                }
            });

            box.getChildren().addAll(rbSi, rbNo);
            setGraphic(box);
        }
    });



        // Columna: Fecha de entrega
        TableColumn<SubmittedDocument, java.sql.Date> colFecha = new TableColumn<>("Fecha Entrega");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha_entrega"));


        // Agregar columnas a la tabla
        TblDocuments.getColumns().addAll(colNombre, colObligatorio, colEntregado, colFecha);
        TblDocuments.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    @FXML
    void OnSave(ActionEvent event) throws NoSuchAlgorithmException, Exception {
        
        if (data == null) return;

        for (SubmittedDocument doc : data) {
            try {
                submittedDocumentDao.update(doc); // Actualiza en la BD
            } catch (Exception e) {
                e.printStackTrace();
                extras.showAlert("Error al guardar", " el Documento: " +doc.getId_tipodocumento()+ "\n" + e.getMessage(), Alert.AlertType.ERROR);
            }
        }

        
        extras.showAlert("Cambios guardados", "Todos los cambios han sido guardados correctamente.", Alert.AlertType.INFORMATION);
        BtnSave.setVisible(false);
    }
    void showData(){
        System.out.println("Docuemntos");
        for (SubmittedDocument doc : data) {
            // Puedes acceder a los métodos de doc, como:
            
            System.out.println("Id Inscripcion "+doc.getId_inscripcion());
            System.out.println("Tipod e documento "+doc.getId_tipodocumento());
            System.out.println("Entregado "+doc.getEstado());
            System.out.println("Fecha "+ doc.getFecha_entrega());
        }
    }
}
