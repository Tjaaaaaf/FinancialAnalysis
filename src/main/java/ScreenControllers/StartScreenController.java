package ScreenControllers;

import Services.DomainController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import java.io.IOException;

public class StartScreenController extends BorderPane {

    //region Properties
    private final DomainController domeincontroller;
    //endregion

    //region FXMLProperties
    @FXML
    private Label lblDocuments;
    @FXML
    private Label lblSettings;
    @FXML
    private Label lblSave;
    @FXML
    private Circle circDocuments;
    @FXML
    private Circle circSettings;
    @FXML
    private Circle circSave;
    //endregion

    //region Constructor
    public StartScreenController(DomainController domeincontroller) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/StartScreen.fxml"));
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
    //endregion

    //region BuildGUI
    private void buildGui() {
        this.setCenter(new DocumentManagementScreenController(domeincontroller, this));
    }
    //endregion

    //region Functions
    public void switchColorDocumentStep() {
        if (circDocuments.getFill().equals(Paint.valueOf("#465e8c"))) {
            circDocuments.setFill(Paint.valueOf("#d0dff2"));
            lblDocuments.setTextFill(Paint.valueOf("#d0dff2"));
        } else {
            circDocuments.setFill(Paint.valueOf("#465e8c"));
            lblDocuments.setTextFill(Paint.valueOf("#465e8c"));
        }
    }

    public void switchColorSettingsStep() {
        if (circSettings.getFill().equals(Paint.valueOf("#465e8c"))) {
            circSettings.setFill(Paint.valueOf("#d0dff2"));
            lblSettings.setTextFill(Paint.valueOf("#d0dff2"));
        } else {
            circSettings.setFill(Paint.valueOf("#465e8c"));
            lblSettings.setTextFill(Paint.valueOf("#465e8c"));
        }
    }

    public void switchColorSaveStep() {
        if (circSave.getFill().equals(Paint.valueOf("#465e8c"))) {
            circSave.setFill(Paint.valueOf("#d0dff2"));
            lblSave.setTextFill(Paint.valueOf("#d0dff2"));
        } else {
            circSave.setFill(Paint.valueOf("#465e8c"));
            lblSave.setTextFill(Paint.valueOf("#465e8c"));
        }
    }
    //endregion
}
