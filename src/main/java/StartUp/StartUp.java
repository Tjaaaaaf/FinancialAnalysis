package StartUp;

import Domain.DomeinController;
import Gui.StartScreenController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StartUp extends Application {

    @Override
    public void start(Stage stage) {
        DomeinController domeincontroller = new DomeinController();

        StartScreenController root = new StartScreenController(domeincontroller);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toString());

        stage.setTitle("FinancialAnalysis");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setWidth(620);
        stage.setHeight(500);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}
