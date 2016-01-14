package com.aim.test;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Created by victor on 08.06.15.
 */
public class Exam extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }

        });
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/test2.fxml"));
        AnchorPane root = fxmlLoader.load();
        ShooterController controller = fxmlLoader.getController();
        controller.initialize();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Aim");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }


}
