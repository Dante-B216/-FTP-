package main;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        Application.launch(args);
        //System.out.println(System.getProperty("java.class.path"));
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        //把窗口传给Manager的实例
        Manager.getInstance().init(primaryStage);
    }
}
