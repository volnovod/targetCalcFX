package com.aim.comvision;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Created by victor on 06.07.15.
 */
public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }

        });
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/hello.fxml"));
        AnchorPane root = fxmlLoader.load();
        Controller controller = fxmlLoader.getController();
        controller.init();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Custom Control");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
