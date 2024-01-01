package main;

import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import scene.ConnectScene;
import scene.FTPContentScene;
import scene.Index;

import java.io.File;
import java.net.MalformedURLException;

public class Manager {

    public static final int WIDTH = 1520, HEIGHT = 880;
    private static Manager instance = new Manager();  //Manager的实例

    private static Stage stage;

    //Manager初始化场景，并且作为场景调度者
    private Manager() {
    }

    public static Manager getInstance() {
        return instance;
    }

    //设置初始化场景并切换到首页场景
    public void init(Stage stage) throws MalformedURLException {
        AnchorPane anchorPane=new AnchorPane();
        Scene scene=new Scene(anchorPane,WIDTH,HEIGHT);
        scene.setCursor(new ImageCursor(new Image("/click.png")));  //给场景设置光标

        //给窗口设置宽高，不然切换场景会不适应窗口
        stage.setHeight(HEIGHT);
        stage.setWidth(WIDTH);

        //给窗口设置图标标题
        stage.setTitle("FTP客户端");
        stage.getIcons().add(new Image("/icon.png"));
        stage.setResizable(false);  //不可以改变窗口大小
        stage.setScene(scene);
        this.stage=stage;

        toIndex();  //切换到首页场景
        stage.show();
    }

    public void toIndex(){
        Index.load(stage);
    }  //切换到首页场景

    public void toConnectScene(){
        ConnectScene.load(stage);
    }  //切换到连接场景

    public void toFTPContentScene(){
        FTPContentScene.load(stage);
    }  //切换到FTP文件树场景


}
