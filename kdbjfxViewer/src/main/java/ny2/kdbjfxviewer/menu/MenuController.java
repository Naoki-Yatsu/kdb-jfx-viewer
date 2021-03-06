package ny2.kdbjfxviewer.menu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ny2.kdbjfxviewer.util.SpringFXMLLoader;

@Component
public class MenuController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    SpringFXMLLoader fxmlLoader;

    @FXML
    private VBox vbox;
    @FXML
    private Button button1;
    @FXML
    private Button button2;
    @FXML
    private Button button3;
    @FXML
    private Button button4;
    @FXML
    private AnchorPane anchor;

    @FXML
    public void initialize() {
        logger.info("{} initializd.", getClass().getSimpleName());
    }

    // Event Listener on VBox[#vbox].onInputMethodTextChanged
    @FXML
    public void aaaaa(InputMethodEvent event) {
        // TODO Autogenerated
    }

    // Event Listener on Button[#button1].onAction
    @FXML
    public void action1(ActionEvent event) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText("I have a great message for you!");
        alert.showAndWait();
    }

    // Event Listener on Button[#button2].onAction
    @FXML
    public void action2(ActionEvent event) {
        Parent root = fxmlLoader.loadFXML(PollingViewerController.class);
        Scene scene = new Scene(root, 800, 600);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Polling Viewer");
        stage.show();
    }

    // Event Listener on Button[#button3].onAction
    @FXML
    public void action3(ActionEvent event) {
        // TODO Autogenerated
    }

    @FXML
    public void action4(ActionEvent event) {
        // TODO Autogenerated
    }






}
