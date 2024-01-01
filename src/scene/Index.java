package scene;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Index {
    public static void load(Stage stage){  //把初始场景放进窗口
        try {
            //设置根节点
            Parent root= FXMLLoader.load(Index.class.getResource("/fxml/index.fxml"));
            //把设置好的根节点放进原始场景里面
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
