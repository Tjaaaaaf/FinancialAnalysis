package Gui;

import Domein.DomeinController;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

public class StartScreenController extends BorderPane {

    private final DomeinController domeincontroller;

    public StartScreenController(DomeinController domeincontroller) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("StartScreen.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        this.domeincontroller = domeincontroller;
        buildGui();
    }

    private void buildGui() {
        this.setCenter(new DocumentManagementScreenController(domeincontroller, this));
    }

}
