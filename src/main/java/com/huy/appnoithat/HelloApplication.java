package com.huy.appnoithat;

import com.huy.appnoithat.Scene.HomeScene;
import com.huy.appnoithat.Scene.LoginScene;
import com.huy.appnoithat.Service.SessionService.UserSessionService;
import com.huy.appnoithat.Session.UserSession;
import javafx.application.Application;
import javafx.stage.Stage;
public class HelloApplication extends Application {
    @Override
    public void start(Stage stage){
//        Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler(stage));
        UserSession.getInstance();
        UserSessionService sessionService = new UserSessionService();
        if (sessionService.isLogin()){
            stage.setScene(HomeScene.getInstance().getScene());
        }
        else{
            stage.setScene(LoginScene.getInstance().getScene());
        }
        stage.setTitle("App Noi That");
        stage.show();
    }
    public static void main(String[] args){
        launch();
    }
}