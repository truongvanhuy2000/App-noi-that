package com.huy.appnoithat.Scene;

import com.huy.appnoithat.Controller.LuaChonNoiThat.LuaChonNoiThatController;
import com.huy.appnoithat.Controller.UsersManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.Objects;

@Getter
public class UserManagementScene {
    private Scene scene;
    private Parent root;
    private static UserManagementScene single_instance = null;
    private final FXMLLoader fxmlLoader;

    public UserManagementScene() {
        String viewPath = "view/UserManagementLayout.fxml";
        try {
            fxmlLoader = new FXMLLoader(getClass().getResource(viewPath));
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        scene = new Scene(root);
        addCssToScence();
    }
    public void setRoot(Parent root) {
        this.root = root;
        scene.setRoot(this.root);
    }

    public void setScene(Scene scene) {
        this.scene = scene;
        scene.setRoot(this.root);
        addCssToScence();
    }
    // Create an object of this class, call this function
    public static synchronized UserManagementScene getInstance() {
        if (single_instance == null)
            single_instance = new UserManagementScene();
        return single_instance;
    }

    private void addCssToScence(){
        String cssPath = "css/UserManagementLayout.css";
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(cssPath)).toExternalForm());
    }

    static public Node getNewRoot() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(UserManagementScene.class.getResource("view/UserManagementLayout.fxml"));
        fxmlLoader.setController(new UsersManagementController());
        Parent root = fxmlLoader.load();
        return root;
    }
}
