package Gui;

import Domein.DomeinController;
import Enums.ReportStyle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class SettingsScreenController extends VBox {

    //region Properties
    private final DomeinController domeinController;
    private final StartScreenController startScreenController;
    private final DocumentManagementScreenController documentManagementScreenController;
    private String companyType = "";
    private String sheetType = "";
    private MakeReportScreenController next;
    //endregion

    //region FXMLProperties
    @FXML
    private ChoiceBox<String> chboxCompany;
    @FXML
    private ChoiceBox<String> chboxSheet;
    //endregion

    //region Constructor
    public SettingsScreenController(DomeinController domeinController, StartScreenController startScreenController, DocumentManagementScreenController documentManagementScreenController) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SettingsScreen.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        this.domeinController = domeinController;
        this.startScreenController = startScreenController;
        this.documentManagementScreenController = documentManagementScreenController;

        configureChoiceboxes();
    }
    //endregion

    //region FXMLFunctions
    @FXML
    private void back(ActionEvent event) {
        startScreenController.setCenter(documentManagementScreenController);
        startScreenController.switchColorDocumentStep();
        startScreenController.switchColorSettingsStep();
    }

    @FXML
    private void save(ActionEvent event) {
        try {
            companyType = chboxCompany.getSelectionModel().getSelectedItem();
            sheetType = chboxSheet.getSelectionModel().getSelectedItem();

            if (companyType == null || companyType.equals("")) {
                throw new IllegalArgumentException("Bedrijfstype mag niet leeg zijn.");
            }
            if (sheetType == null || sheetType.equals("")) {
                throw new IllegalArgumentException("Overzichtstype mag niet leeg zijn.");
            }

            switch (companyType) {
                case "NV":
                    if (sheetType.equals("Historiek")) {
                        next = new MakeReportScreenController(domeinController, startScreenController, ReportStyle.HISTORIEKNV, this);
                    } else {
                        next = new MakeReportScreenController(domeinController, startScreenController, ReportStyle.VERGELIJKINGNV, this);
                    }
                    break;
                case "BVBA":
                    if (sheetType.equals("Historiek")) {
                        next = new MakeReportScreenController(domeinController, startScreenController, ReportStyle.HISTORIEKBVBA, this);
                    } else {
                        next = new MakeReportScreenController(domeinController, startScreenController, ReportStyle.VERGELIJKINGBVBA, this);
                    }
                    break;
                default:
                    startScreenController.setCenter(new MakeReportScreenController(domeinController, startScreenController, ReportStyle.HISTORIEKNV, this));
            }
            startScreenController.setCenter(next);

            startScreenController.switchColorSettingsStep();
            startScreenController.switchColorSaveStep();
        } catch (IllegalArgumentException ex) {
            CustomAlert.showAlert("Ontbrekende informatie", "Ontbrekende informatie", ex.getMessage(), this.getScene().getWindow(), Alert.AlertType.ERROR);
        }
    }
    //endregion

    //region Functions
    private void configureChoiceboxes() {
        chboxCompany.setItems(FXCollections.observableArrayList("NV", "BVBA"));
        chboxSheet.setItems(FXCollections.observableArrayList("Historiek", "Vergelijking"));
        chboxCompany.setValue(companyType);
        chboxSheet.setValue(sheetType);
    }
    //endregion
}
