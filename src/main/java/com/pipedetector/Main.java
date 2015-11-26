package com.pipedetector;

import com.pipedetector.controller.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.MalformedURLException;

/**
 * Created by track on 26.11.15.
 */
public class Main extends Application{


    public static void main(String[] args) throws MalformedURLException {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }

        });

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/photoDisplay.fxml"));

        AnchorPane root = loader.load();
        Scene scene = new Scene(root);
        Controller controller = loader.getController();
        controller.init();

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
