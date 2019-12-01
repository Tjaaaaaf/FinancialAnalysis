package Gui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class ErrorAlert {

    public static void showAlert(String title, String headerText, String contentText) {
        Alert foutAlert = new Alert(Alert.AlertType.ERROR);

        foutAlert.setTitle(title);
        foutAlert.setHeaderText(headerText);
        foutAlert.setContentText(contentText);

        ButtonType btnOk = new ButtonType("Ok");
        foutAlert.getButtonTypes().clear();
        foutAlert.getButtonTypes().addAll(btnOk);

        foutAlert.showAndWait();
    }
}
