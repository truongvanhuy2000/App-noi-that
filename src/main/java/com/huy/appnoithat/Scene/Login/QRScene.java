package com.huy.appnoithat.Scene.Login;

import com.huy.appnoithat.Scene.GenericScene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import lombok.Getter;

import java.io.IOException;
import java.util.Objects;

@Getter
public class QRScene implements GenericScene {
    private static final String VIEW_PATH = "/com/huy/appnoithat/Scene/view/QRLayout.fxml";
    private static final String CSS_PATH = "/com/huy/appnoithat/Scene/css/UserManagementLayout.css";
    private final Scene scene;
    private final Parent root;
    private final FXMLLoader fxmlLoader;

    public QRScene() {
        try {
            fxmlLoader = new FXMLLoader(getClass().getResource(VIEW_PATH));
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        scene = new Scene(root);
        addCssToScence();
    }

    private void addCssToScence() {
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(CSS_PATH)).toExternalForm());
    }
}
