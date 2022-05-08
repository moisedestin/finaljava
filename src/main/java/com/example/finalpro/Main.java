package com.example.finalpro;

import com.example.finalpro.background.Server;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Main extends Application {

    public static HashMap<Integer, Peerr> listPeers = new HashMap<>();
    public static int myPort;
    public static String myUsername;
    public static String myFolderName;

    /**
     * stating app
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * init vital config
     * @param portField
     * @param username
     * @param folderName
     */
    public static void setupBasicSettings(int portField,  String username, String folderName) {
        myUsername = username;
        myFolderName = folderName;
        myPort = portField;

        Server server = new Server(myPort, Config.POOL_SIZE);
        server.startServer();
    }

    /**
     * start gui
     * @param primaryStage
     * @throws IOException
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 500);

        primaryStage.setScene(scene);
        primaryStage.show();



    }


}
