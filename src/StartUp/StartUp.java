package StartUp;

import Domein.DomeinController;
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
        
        stage.setTitle("FinancialAnalysis");
        stage.setScene(scene);
        stage.setWidth(370);
        stage.setHeight(370);
        stage.setResizable(false);
        stage.show();
        
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
