package com.huy.appnoithat.Controller.FileNoiThatExplorer;

import com.huy.appnoithat.Scene.LuaChonNoiThat.FileNoiThatExplorerScene;
import javafx.application.Application;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//public class FileNoiThatExplorerControllerTest extends Application {
//    private Thread thread;
//
//    @BeforeEach
//    void setUp() {
//        thread = new Thread("JavaFX Init Thread") {
//            public void run() {
//                Application.launch(FileNoiThatExplorerControllerTest.class, new String[0]);
//            }
//        };
//        thread.start();
//        System.out.println("FX App thread started");
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @AfterEach
//    void tearDown() {
//    }
//
//    @Test
//    void initialize() {
//        try {
//            thread.join();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public void start(Stage stage) throws Exception {
//        stage.setScene(FileNoiThatExplorerScene.getInstance().getScene());
//        FileNoiThatExplorerScene.getInstance().getController().init();
//        stage.setTitle("App Noi That");
//        stage.setMaximized(true);
//        stage.show();
//    }
//}