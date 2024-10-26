package com.kashish.music_player;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.*;

public class HelloController implements Initializable {

    // Declarations
    @FXML
    private Label songNameLabel, fullSongTimeLabel, currentTimeLabel, volumeLabel;
    @FXML
    private AnchorPane mainAnchorPane;
    @FXML
    private Slider songVolume, songProgress;
    // TODO: Update progress bar to slider for seeking and update state of music in time limit in V_2.0.0
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

    private boolean getMediaPlayerStatus;

    // Setting MediaPlayer media play state.
    public void songPlayPause(ActionEvent event) {
        mediaPlayer.setVolume(songVolume.getValue() * .01);
        changeSpeed(event);
        if (isMediaPlaying()) {
            setSongPause(event);
        } else {
            setSongPlay(event);
        }
    }

    private void setSongPlay(ActionEvent event) {
        beginTimer(event);
        mediaPlayer.play();
        getMediaPlayerStatus = true;
    }

    private void setSongPause(ActionEvent event) {
        cancelTimer(event);
        mediaPlayer.pause();
        getMediaPlayerStatus = false;
    }

    public void songReset(ActionEvent event) {
        setSongPause(event);
        mediaPlayer.seek(new Duration(0));
        songProgress.setValue(0);
        currentTimeLabel.setText("00:00");
    }


    // Select song prev or next
    public void songPrevious(ActionEvent event) {
        if (songNumber >= 1) {
            songNumber--;
        } else {
            songNumber = songNames.size() - 1;
        }
        nextItem(event);
    }

    public void songNext(ActionEvent event) {
        if (songNumber < songNames.size() - 1) {
            songNumber++;
        } else {
            songNumber = 0;
        }
        nextItem(event);
    }

    private void nextItem(ActionEvent event) {
        mediaPlayer.stop();
        currentTimeLabel.setText("00:00");
        if (running) cancelTimer(event);
        media = new Media(songNames.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        songNameLabel.setText(songNames.get(songNumber).getName().split("_-_")[0]);
        mediaPlayer.setOnReady(() -> {
                    double totalDuration = media.getDuration().toSeconds();
                    Platform.runLater(() -> {
                        fullSongTimeLabel.setText(String.format("%02d:%02d", (int) totalDuration / 60, (int) totalDuration % 60));
                        songProgress.setMax(totalDuration);
                    });
                }
        );
        if (getMediaPlayerStatus) setSongPlay(event);
    }

    // Timer to check and update how much time is completed by media
    public void beginTimer(ActionEvent event) {
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                running = true;
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                double progress = current / end;
                Platform.runLater(() -> {
                    currentTimeLabel.setText(String.format("%02d:%02d", (int) current / 60, (int) current % 60));
                    songProgress.setValue(current);
                    if (getMediaPlayerStatus && media.getDuration().toSeconds() == mediaPlayer.getCurrentTime().toSeconds()) {
                        songNext(event);
                    }
                });
                if (current == end) cancelTimer(event);
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    public void cancelTimer(ActionEvent ignoredEvent) {
        running = false;
        timer.cancel();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        songNames = new ArrayList<>();
        File songDirectory = new File("src/main/resources/musics");
        File[] songFiles = songDirectory.listFiles();

        if (songFiles != null) {
            Collections.addAll(songNames, songFiles);
        }

        media = new Media(songNames.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        songNameLabel.setText(songNames.get(songNumber).getName().split("_-_")[0]);

        for (int speed : songSpeeds) {
            songSpeed.getItems().add(speed + "%");
        }

        songSpeed.setOnAction(this::changeSpeed);

        songVolume.valueProperty().addListener((_, _, _) -> {
            mediaPlayer.setVolume(songVolume.getValue() * .01);
            volumeLabel.setText((int) songVolume.getValue() + "%");
        });

        mediaPlayer.setOnReady(() -> {
                    double totalDuration = media.getDuration().toSeconds();
                    Platform.runLater(() -> {
                                fullSongTimeLabel.setText(String.format("%02d:%02d",
                                        (int) totalDuration / 60,
                                        (int) totalDuration % 60));
                                songProgress.setMin(0);
                                songProgress.setMax(totalDuration);
                            }
                    );
                    getMediaPlayerStatus = isMediaPlaying();
                }
        );
    }

    private void changeSpeed(ActionEvent event) {
        if (songSpeed.getValue() == null) {
            mediaPlayer.setRate(1);
            return;
        }
        // By Kashish Dhirana (me)
        mediaPlayer.setRate(Integer.parseInt(songSpeed.getValue().split("%")[0]) * .01);
        // By Bro Code
//        mediaPlayer.setRate(Integer.parseInt(songSpeed.getValue().substring(0, songSpeed.getValue().length() - 1)) * .01);
    }

    // Return MediaPlayer State of playing or paused
    private boolean isMediaPlaying() {
        return mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING);
    }

    public void seekSong(MouseEvent dragEvent) {
        double seek = songProgress.getValue() * 1000;
        mediaPlayer.seek(new Duration(seek));
    }
}
