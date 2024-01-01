package scene;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

public class FTPContentScene {
    public static void load(Stage stage){
        try {
            Parent root= FXMLLoader.load(FTPContentScene.class.getResource("/fxml/FTPDirectoryTreeView.fxml"));
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
