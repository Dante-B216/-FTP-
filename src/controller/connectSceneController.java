package controller;


import org.apache.commons.net.ftp.*;

import ftp.FTPManager;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import main.Manager;
import org.apache.commons.net.ftp.FTPClient;

import java.util.Objects;


public class connectSceneController {

     @FXML
     private TextField textField;  //文本输入框

    @FXML
    private ImageView imageView;

    //private FTPManager ftpManager = new FTPManager();
    
    @FXML
    void initialize() {
         // 为TextField添加事件处理器
         // 处理文本框textField上的键盘按键按下事件
        textField.setOnKeyPressed(event -> {
            try {
                KeyPressEnter(event);  //在FXML textField中设置了onKeyPressed="#KeyPressEnter"

                //如果把FXML textField中的onKeyPressed="#KeyPressEnter"删除，直接调用handleKeyPress(event)也是可以的
                //handleKeyPress(event);  //处理按键事件

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    //处理按键事件
    private void handleKeyPress(KeyEvent event) throws Exception {
        if (event.getCode() == KeyCode.ENTER) {  //如果按下的是enter键
            // 在这里处理Enter键输入事件
            String input=textField.getText();  //获取输入的内容
            System.out.println(input);
            if(input.startsWith("ftp://")){  //处理ftp链接

                String[] parts = input.split("://");  //分割字符串

                FTPManager.ftp_server=parts[1];  //获得FTP服务器地址

                if(Objects.equals(parts[1], "192.168.206.131")){  //如果输入的地址是搭建好的FTP服务器地址

                    FTPManager.ftpManager.connect();  //连接FTP服务器

                    if(FTPManager.client.getReplyCode()==230){  //如果FTP服务器返回的状态码为230，说明连接成功

                        //label.setText("成功连接FTP服务器！");
                        String imagePath = "/成功连接FTP服务器.png";
                        Image image = new Image(imagePath);
                        imageView.setImage(image);

                        // 创建PauseTransition，设置持续时间为2秒
                        PauseTransition pause = new PauseTransition(Duration.seconds(2));  //画面暂停2秒

                        // 设置事件处理器，当暂停完成时执行Manager.toConnectScene()
                        pause.setOnFinished(e -> Manager.getInstance().toFTPContentScene());  //切换到文件树场景

                        // 开始暂停
                        pause.play();
                    }
                }else{  //不是ftp链接
                    //label.setText("无法连接FTP服务器！");

                    String imagePath = "/无法连接FTP服务器.png";
                    Image image = new Image(imagePath);
                    imageView.setImage(image);

                    // 创建PauseTransition，设置持续时间为2秒
                    PauseTransition pause = new PauseTransition(Duration.seconds(2));

                    // 设置事件处理器，当暂停完成时执行Manager.toConnectScene()
                    pause.setOnFinished(e -> Manager.getInstance().toConnectScene());  //切换回连接场景

                    // 开始暂停
                    pause.play();
                }
            }else {
                //label.setText("无法连接FTP服务器！");
                String imagePath = "/无法连接FTP服务器.png";
                Image image = new Image(imagePath);
                imageView.setImage(image);

                // 创建PauseTransition，设置持续时间为2秒
                PauseTransition pause = new PauseTransition(Duration.seconds(2));  //切换回连接场景

                // 设置事件处理器，当暂停完成时执行Manager.toConnectScene()
                pause.setOnFinished(e -> Manager.getInstance().toConnectScene());

                // 开始暂停
                pause.play();
            }

        }
        event.consume();  //标记事件已经被处理，防止事件进一步传播
     }


    @FXML
    void KeyPressEnter(KeyEvent event) throws Exception {
        handleKeyPress(event);  //调用处理按键
    }
}
