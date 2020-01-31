package Gui;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

public class CustomAlert {

    public static void showAlert(String title, String headerText, String contentText, Window owner, AlertType type) {
        Alert customAlert = new Alert(type);

        customAlert.initOwner(owner);

        customAlert.setTitle(title);
        customAlert.setHeaderText(headerText);
        customAlert.setContentText(contentText);

        ButtonType btnOk = new ButtonType("Ok");
        customAlert.getButtonTypes().clear();
        customAlert.getButtonTypes().addAll(btnOk);

        customAlert.showAndWait();
    }
}
