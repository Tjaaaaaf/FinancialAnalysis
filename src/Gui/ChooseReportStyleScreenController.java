package Gui;

import Domein.DomeinController;
import Enums.ReportStyle;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class ChooseReportStyleScreenController extends VBox {

    private final DomeinController domeinController;
    private final StartScreenController startScreenController;

    @FXML
    private Button btnHistoriekNV;
    @FXML
    private Button btnHistoriekBVBA;
    @FXML
    private Button btnVergelijkingNV;
    @FXML
    private Button btnVergelijkingBVBA;
    @FXML
    private Button btnTerug;

    public ChooseReportStyleScreenController(DomeinController domeinController, StartScreenController startScreenController) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ChooseReportStyleScreen.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        this.domeinController = domeinController;
        this.startScreenController = startScreenController;
    }

    @FXML
    private void goToPreviousScreen(ActionEvent event) {
        startScreenController.setCenter(new DocumentManagementScreenController(domeinController, startScreenController));
    }

    @FXML
    private void maakHistoriekNV(ActionEvent event) {
        startScreenController.setCenter(new MakeReportScreenController(domeinController, startScreenController, ReportStyle.HISTORIEKNV));
    }

    @FXML
    private void maakHistoriekBVBA(ActionEvent event) {
        startScreenController.setCenter(new MakeReportScreenController(domeinController, startScreenController, ReportStyle.HISTORIEKBVBA));
    }

    @FXML
    private void maakVergelijkingNV(ActionEvent event) {
        startScreenController.setCenter(new MakeReportScreenController(domeinController, startScreenController, ReportStyle.VERGELIJKINGNV));
    }

    @FXML
    private void maakVergelijkingBVBA(ActionEvent event) {
        startScreenController.setCenter(new MakeReportScreenController(domeinController, startScreenController, ReportStyle.VERGELIJKINGBVBA));
    }
}
