package com.kashish.music_player;

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
    @FXML private Label songNameLabel;
    @FXML private AnchorPane mainAnchorPane;
    @FXML private Slider songVolume;
    @FXML private ProgressBar songProgress;
    @FXML private ComboBox<String> songSpeed;
    @FXML private Button songPlay, songPause, songReset, songPrevious, songNext;

    private ArrayList<File> songNames;

    private int songNumber;
    private final int[] songSpeeds = new int[]{25, 50, 75, 100, 125, 150, 175, 200};

    private Timer timer;
    private boolean running;

    private Media media;
    private MediaPlayer mediaPlayer;

    public void songPlay(ActionEvent event) {
        beginTimer(event);
        changeSpeed(event);
        mediaPlayer.play();
    }

    public void songPause(ActionEvent event) {
        cancelTimer(event);
        mediaPlayer.pause();
    }

    public void songReset(ActionEvent event) {
        songProgress.setProgress(0);
        mediaPlayer.seek(new Duration(0));
    }

    public void songPrevious(ActionEvent event) {
        if(songNumber >= 1) {
            songNumber--;
        } else {
            songNumber = songNames.size() - 1;
        }
        mediaPlayer.stop();
        if(running) cancelTimer(event);
        media = new Media(songNames.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        songNameLabel.setText(songNames.get(songNumber).getName());

        songPlay(event);
    }

    public void songNext(ActionEvent event) {
        if(songNumber < songNames.size() - 1) {
            songNumber++;
        } else {
            songNumber = 0;
        }
        mediaPlayer.stop();
        if(running) cancelTimer(event);
        media = new Media(songNames.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        songNameLabel.setText(songNames.get(songNumber).getName());

        songPlay(event);
    }

    public void beginTimer(ActionEvent event) {
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                running = true;
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                System.out.println(current + " " + end + " : " + current / end);
                songProgress.setProgress(current / end);

                if (current == end) cancelTimer(event);
            }
        };
        timer.schedule(timerTask, 1000, 1000);
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

        if(songFiles != null) {
            for(File file: songFiles) {
                songNames.add(file);
                System.out.println(file);
            }
        }

        media = new Media(songNames.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        songNameLabel.setText(songNames.get(songNumber).getName());

        for (int speed : songSpeeds) {
            songSpeed.getItems().add(Integer.toString(speed) + "%");
        }

        songSpeed.setOnAction(this::changeSpeed);

        songVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
            mediaPlayer.setVolume(songVolume.getValue() * .01);
        });
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