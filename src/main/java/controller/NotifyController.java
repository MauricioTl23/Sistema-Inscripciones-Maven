/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import model.EmailService;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import model.Guardian;
import Dao.GuardianDao;
import jakarta.mail.MessagingException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ListCell;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;
import javafx.animation.PauseTransition;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 *
 * @author mauricioteranlimari
 */
public class NotifyController implements Initializable {

    @FXML
    private Button btnEnviar;

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

    @FXML
    private ComboBox<Guardian> CboTutor;
    
    @FXML
    private TextArea TxtCarta;
    
    private GuardianDao guardianDao;
    private ObservableList<Guardian> listaTutores = FXCollections.observableArrayList();
    private FilteredList<Guardian> filteredTutores;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        Stack1.prefHeightProperty().bind(MainVBox.heightProperty().multiply(2.0 / 10.0));
        Stack2.prefHeightProperty().bind(MainVBox.heightProperty().multiply(8.0 / 10.0));
        
        Stack1.prefWidthProperty().bind(MainVBox.widthProperty());
        Stack2.prefWidthProperty().bind(MainVBox.widthProperty());
        
        rectangle1.widthProperty().bind(Stack1.widthProperty());
        rectangle1.heightProperty().bind(Stack1.heightProperty());

        rectangle2.widthProperty().bind(Stack2.widthProperty());
        rectangle2.heightProperty().bind(Stack2.heightProperty());

        try {
            this.guardianDao = new GuardianDao();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ManageUsersController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(NotifyController.class.getName()).log(Level.SEVERE, null, ex);
        }
        listaTutores.addAll(guardianDao.toList());
        filteredTutores = new FilteredList<>(listaTutores, p -> true);
        CboTutor.setItems(listaTutores);

// Mostrar nombre completo
        CboTutor.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Guardian tutor, boolean empty) {
                super.updateItem(tutor, empty);
                setText((tutor == null || empty) ? "" : tutor.getNombre() + " " + tutor.getApellido());
            }
        });

        CboTutor.setConverter(new StringConverter<>() {
            @Override
            public String toString(Guardian tutor) {
                return (tutor != null) ? tutor.getNombre() + " " + tutor.getApellido() : "";
            }

            @Override
            public Guardian fromString(String string) {
                return null; // No se usa
            }
        });

// Búsqueda tipo navegador
        StringBuilder typedText = new StringBuilder();
        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
        pause.setOnFinished(e -> typedText.setLength(0)); // limpiar texto escrito

        CboTutor.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();

            // Ignorar teclas no alfanuméricas
            if (code.isLetterKey() || code.isDigitKey()) {
                typedText.append(event.getText().toLowerCase());

                String currentText = typedText.toString().trim();

                Optional<Guardian> match = listaTutores.stream()
                        .filter(t -> (t.getNombre() + " " + t.getApellido()).toLowerCase().contains(currentText))
                        .findFirst();

                match.ifPresent(guardian -> {
                    CboTutor.getSelectionModel().select(guardian);
                    CboTutor.show(); // mostrar dropdown y mover scroll
                });

                pause.playFromStart(); // reiniciar timer
            } else if (code == KeyCode.BACK_SPACE && typedText.length() > 0) {
                typedText.deleteCharAt(typedText.length() - 1);
                pause.playFromStart();
            } else if (code == KeyCode.ESCAPE) {
                typedText.setLength(0);
                pause.stop();
            }
        });

// Acción al seleccionar manualmente
        CboTutor.setOnAction(event -> {
            Guardian selectedTutor = CboTutor.getSelectionModel().getSelectedItem();
            if (selectedTutor != null) {
                TxtCarta.setText(generarPlantillaCarta(selectedTutor));
                typedText.setLength(0);
                pause.stop();
            }
        });

        // Acción del botón "Enviar"
        btnEnviar.setOnAction(e -> enviarCarta());
    }

    private String generarPlantillaCarta(Guardian tutor) {
        return "Oruro, " + LocalDate.now() + "\n\n"
                + "Sr./Sra. " + tutor.getNombre() + " " + tutor.getApellido() + "\n"
                + "Presente.-\n\n"
                + "Por medio de la presente, nos dirigimos a usted para informarle sobre la situación académica y/o de comportamiento del estudiante "
                + "Fernando Choque, quien cursa actualmente en nuestra unidad educativa.\n\n"
                + "Le solicitamos su presencia en la institución para tratar asuntos importantes respecto a su tutorado.\n\n"
                + "Agradecemos su atención y quedamos atentos a su visita.\n\n"
                + "Atentamente,\n\n"
                + "\t\tDirección de la Unidad Educativa"
                + "\t\t       Jorge Oblitas";
    }

    private void enviarCarta() {
        Guardian tutor = CboTutor.getValue();
        String mensaje = TxtCarta.getText();

        if (tutor == null || mensaje.isEmpty()) {
            System.out.println("Error" + "Seleccione un tutor y escriba un mensaje.");
            return;
        }

        String correoDestino = tutor.getCorreo(); // Asegúrate de que esto no sea null
        String asunto = "Notificación Importante";

        try {
            EmailService emailService = new EmailService();
            emailService.enviarCorreo(correoDestino, asunto, mensaje);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Error" + "No se pudo enviar el correo: " + e.getMessage());
        }
    }

    private Guardian fromText(String text) {
        return listaTutores.stream()
                .filter(t -> (t.getNombre() + " " + t.getApellido()).equalsIgnoreCase(text.trim()))
                .findFirst().orElse(null);
    }
}
