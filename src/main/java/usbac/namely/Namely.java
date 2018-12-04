package usbac.namely;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Namely extends Application {
    
    private final String TITLE = "Namely";
    private final int WIDTH = 555;
    private final int HEIGHT = 520;
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/FXMLDocument.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/styles.css");
        
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));
        stage.setWidth(WIDTH);
        stage.setHeight(HEIGHT);
        stage.setMinWidth(stage.getWidth());
        stage.setMinHeight(stage.getHeight());
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.show();
    }
    

    public static void main(String[] args) {
        launch(args);
    }

}
