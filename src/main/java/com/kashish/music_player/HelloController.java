package com.kashish.music_player;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.*;

public class HelloController implements Initializable {
    @FXML
    private Label songNameLabel, fullSongTimeLabel, currentTimeLabel, volumeLabel;
    @FXML
    private AnchorPane mainAnchorPane;
    @FXML
    private Slider songVolume;
    // TODO: Update progress bar to slider for seeking and update state of music in time limit
    @FXML
    private ProgressBar songProgress;
    @FXML
    private ComboBox<String> songSpeed;
    @FXML
    private Button songPlayPause, songReset, songPrevious, songNext;

    private ArrayList<File> songNames;

    private int songNumber;
    private final int[] songSpeeds = new int[]{25, 50, 75, 100, 125, 150, 175, 200};

    private Timer timer;
    private boolean running;

    private Media media;
    private MediaPlayer mediaPlayer;

    public void songPlayPause(ActionEvent event) {
        mediaPlayer.setVolume(songVolume.getValue() * .01);
        changeSpeed(event);
        if (mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            cancelTimer(event);
            mediaPlayer.pause();
        } else {
            beginTimer(event);
            mediaPlayer.play();
        }
    }

    public void songReset(ActionEvent event) {
        songProgress.setProgress(0);
        mediaPlayer.seek(new Duration(0));
    }

    public void songPrevious(ActionEvent event) {
        if (songNumber >= 1) {
            songNumber--;
        } else {
            songNumber = songNames.size() - 1;
        }
        mediaPlayer.stop();
        if (running) cancelTimer(event);
        media = new Media(songNames.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        songNameLabel.setText(songNames.get(songNumber).getName());

//        songPlay(event);
        songPlayPause(event);
    }

    public void songNext(ActionEvent event) {
        if (songNumber < songNames.size() - 1) {
            songNumber++;
        } else {
            songNumber = 0;
        }
        mediaPlayer.stop();
        if (running) cancelTimer(event);
        media = new Media(songNames.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        songNameLabel.setText(songNames.get(songNumber).getName());
    }

    public void beginTimer(ActionEvent event) {
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                running = true;
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                double progress = current / end;
//                System.out.println(current + " " + end + " : " + current / end);
//                System.out.println((int)mediaPlayer.getCurrentTime().toMinutes() + ">:<" + (int)mediaPlayer.getCurrentTime().toMinutes() / 60);
//                currentTimeLabel.setText((int)mediaPlayer.getCurrentTime().toMinutes() + ":" + (int)mediaPlayer.getCurrentTime().toSeconds());
                Platform.runLater(() -> {
                    songProgress.setProgress(progress);
                    currentTimeLabel.setText(String.format("%02d:%02d", (int) current / 60, (int) current % 60));
                });

                if (current == end) cancelTimer(event);
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    public void cancelTimer(ActionEvent event) {
        running = false;
        timer.cancel();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        songNames = new ArrayList<>();
        File songDirectory = new File("src/main/resources/musics");
//        System.out.println(songDirectory.getAbsolutePath());
        File[] songFiles = songDirectory.listFiles();

        if (songFiles != null) {
            for (File file : songFiles) {
                songNames.add(file);
                System.out.println(file);
            }
        }

        media = new Media(songNames.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        songNameLabel.setText(songNames.get(songNumber).getName());

        for (int speed : songSpeeds) {
            songSpeed.getItems().add(speed + "%");
        }

        songSpeed.setOnAction(this::changeSpeed);

        songVolume.valueProperty().addListener((_, _, _) -> {
            mediaPlayer.setVolume(songVolume.getValue() * .01);
            volumeLabel.setText((int)songVolume.getValue() + "%");
        });

//        songProgress.setStyle("-fx-accent: #00ff00;");
        mediaPlayer.setOnReady(() -> {
                    double totalDuration = media.getDuration().toSeconds();
                    Platform.runLater(() -> {
                        fullSongTimeLabel.setText(String.format("%02d:%02d", (int) totalDuration / 60, (int) totalDuration % 60));
                    });
                }
        );

    }

    private void changeSpeed(ActionEvent event) {
        if (songSpeed.getValue() == null) {
            mediaPlayer.setRate(1);
            return;
        }
        // By Kashish Dhirana (me)
//        mediaPlayer.setRate(Integer.parseInt(songSpeed.getValue().split("%")[0]) * .01);
        // By Bro Code
        mediaPlayer.setRate(Integer.parseInt(songSpeed.getValue().substring(0, songSpeed.getValue().length() - 1)) * .01);
    }
}