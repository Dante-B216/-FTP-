package controller;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import main.Manager;

public class indexController {

    @FXML
    private ImageView startButton;

    @FXML
    void mouseClickstartButton(MouseEvent event) {
        Manager.getInstance().toConnectScene(); //切换到连接场景
    }

    @FXML
    void mouseEnterstartButton(MouseEvent event) {
        startButton.setOpacity(0.8);
    }  //更改按钮的不透明度

    @FXML
    void mouseExitstartButton(MouseEvent event) {
        startButton.setOpacity(1);
    }  //更改按钮的不透明度

}
