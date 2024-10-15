package com.kashish.music_player;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // import layout from fxml file
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());

        // Setting Title, icon and resize
        stage.setTitle("MP3 Player");
        stage.getIcons().addAll(new Image("/icons/AppIcon16.png"), new Image("/icons/AppIcon32.png"), new Image("/icons/AppIcon48.png"));
        stage.setResizable(false);

        // Start Scene
        stage.setScene(scene);
        stage.show();

        // Exit
        stage.setOnCloseRequest(_ -> {
            // Code to execute before exiting
            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch();
    }
}