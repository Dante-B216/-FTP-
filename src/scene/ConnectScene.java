package scene;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

public class ConnectScene {
    public static void load(Stage stage){
        try {
            //ConnectScene.class.getResource("../fxml/connectScene.fxml")通过Class对象来获取一个URL，它指向了项目类路径classpath下的指定资源文件
            //FXMLLoader是JavaFX提供的一个类，用于从FXML文件中加载界面描述，并将其转换为JavaFX的图形组件
            //load()是FXMLLoader类的静态方法，用于加载并解析给定的FXML文件，然后返回FXML文件的根节点。该根节点通常是一个JavaFX的布局容器
            //Parent是JavaFX中的一个类，表示所有图形组件的父类。实际上，所有可视的JavaFX组件都是Parent类的子类或间接子类
            Parent root= FXMLLoader.load(ConnectScene.class.getResource("/fxml/connectScene.fxml"));
            stage.getScene().setRoot(root);  //把FXML文件的根节点设置到窗口的场景中
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
