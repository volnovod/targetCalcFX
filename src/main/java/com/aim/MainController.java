package com.aim;


import com.aim.SpringLoader.SpringFXMLLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by victor on 02.06.15.
 */
public class MainController {

    public Node getView() {
        return view;
    }

    public void setView(Node view) {
        this.view = view;
    }

    private Node view;

    @FXML
    private TextArea text;

    public MainController() {



        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/fxml/hello.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        SpringFXMLLoader loader = new SpringFXMLLoader();
        String t = Arrays.asList(loader.getService().getAllAim()).toString();
        text.setText(t);
    }


}
