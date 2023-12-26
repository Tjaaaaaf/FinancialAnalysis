package StartUp;

import Services.DomainController;
import ScreenControllers.StartScreenController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StartApplication extends Application {
    private static Scene scene;

    @Override
    public void start(Stage stage) {
        setupScene();

        stage.setTitle("FinancialAnalysis");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setWidth(620);
        stage.setHeight(500);
        stage.show();
    }

    public static Scene getScene() {
        setupScene();
        return scene;
    }

    private static void setupScene() {
        if (scene == null) {
            scene = new Scene(new StartScreenController(new DomainController()));
            scene.getStylesheets().add(StartApplication.class.getResource("/css/style.css").toString());
        }
    }

    public void main(String[] args) {
        launch(args);
    }
}
