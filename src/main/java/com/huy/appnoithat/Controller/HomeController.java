package com.huy.appnoithat.Controller;

import com.huy.appnoithat.Scene.DatabaseModify.DatabaseModifyPhongCachScene;
import com.huy.appnoithat.Scene.LoginScene;
import com.huy.appnoithat.Scene.NewTabScene;
import com.huy.appnoithat.Scene.UserManagementScene;
import com.huy.appnoithat.Service.SessionService.UserSessionService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class HomeController {
    @FXML
    private Button LogoutButton;
    @FXML
    private Button LuaChonNoiThatButton;
    @FXML
    private Button QuanLyNguoiDungButton;
    @FXML
    private Button suadoidatabaseButton;
    @FXML
    private Text UserName;
    private final UserSessionService sessionService;
    public HomeController() {
        this.sessionService = new UserSessionService();
    }
    @FXML
    void logout(ActionEvent event) {
        sessionService.cleanUserSession();
        LogoutButton.getScene().getWindow().hide();
        sceneSwitcher(event);
    }

    // Initialize scene
    public void initialize() {
        // Hide all button
        suadoidatabaseButton.setDisable(true);
        QuanLyNguoiDungButton.setDisable(true);
        LuaChonNoiThatButton.setDisable(true);
        // Set username using current session
        String username = sessionService.getLoginAccount().getUsername();
        UserName.setText(username);

        // Show button based on role
        String role = sessionService.getLoginAccount().getRoleList().contains("ROLE_ADMIN") ? "Admin" : "User";
        switch (role) {
            case "Admin" -> {
                QuanLyNguoiDungButton.setDisable(false);
                suadoidatabaseButton.setDisable(false);
                LuaChonNoiThatButton.setDisable(true);
            }
            case "User" -> {
                LuaChonNoiThatButton.setDisable(false);
                QuanLyNguoiDungButton.setDisable(true);
                suadoidatabaseButton.setDisable(true);
            }
            default -> {
            }
        }
    }
    // Central unit to switch scene based on context
    @FXML
    private void sceneSwitcher(ActionEvent actionEvent) {
        Scene scene = null;
        Stage stage = null;
        Object source = actionEvent.getSource();
        stage = (Stage) ((Node)source).getScene().getWindow();
        stage.setResizable(false);
        if (source == LogoutButton){
            scene = LoginScene.getInstance().getScene();
        }
        else if (source == LuaChonNoiThatButton) {
            Stage newStage = new Stage();
            scene = NewTabScene.getInstance().getScene();
            newStage.setMaximized(true);
            newStage.setScene(scene);
            newStage.show();
            return;
        }
        else if (source == QuanLyNguoiDungButton) {
            scene = UserManagementScene.getInstance().getScene();
        }else if (source == suadoidatabaseButton) {
            scene = DatabaseModifyPhongCachScene.getInstance().getScene();
            DatabaseModifyPhongCachScene.getInstance().getController().initializePhongCach();
        }
        else {
            return;
        }
        stage.setScene(scene);
        stage.show();
    }

}
