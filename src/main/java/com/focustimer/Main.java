package com.focustimer;

import com.focustimer.tracker.PomodoroTracker;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main extends Application {

    private enum Phase { IDLE, FOCUS, BREAK }

    private static final int DEFAULT_FOCUS_MINUTES = 25;
    private static final int DEFAULT_BREAK_MINUTES = 5;

    private final PomodoroTracker tracker = new PomodoroTracker();

    private Phase phase = Phase.IDLE;
    private long remainingSeconds;
    private long focusDurationSeconds;
    private long breakDurationSeconds;
    private String currentLabel;
    private Timeline timeline;

    private TextField labelField;
    private TextField focusMinutesField;
    private TextField breakMinutesField;
    private Label statusLabel;
    private Label timeLabel;
    private Button startButton;
    private Button pauseResumeButton;
    private Button skipButton;
    private ListView<String> statsListView;
    private Label totalPomodorosLabel;
    private Label totalBreakTimeLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));
        root.setTop(buildTitle());
        root.setCenter(buildTimerPane());
        root.setBottom(buildStatsPane());

        primaryStage.setTitle("Focus Timer (Pomodoro)");
        primaryStage.setScene(new Scene(root, 420, 460));
        primaryStage.show();

        refreshStats();
    }

    private Label buildTitle() {
        Label title = new Label("Focus Timer");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 0 0 12 0;");
        return title;
    }

    private VBox buildTimerPane() {
        labelField = new TextField();
        labelField.setPromptText("Etichetta (es. Progetto X)");

        focusMinutesField = new TextField(String.valueOf(DEFAULT_FOCUS_MINUTES));
        breakMinutesField = new TextField(String.valueOf(DEFAULT_BREAK_MINUTES));

        GridPane form = new GridPane();
        form.setHgap(8);
        form.setVgap(8);
        form.add(new Label("Etichetta:"), 0, 0);
        form.add(labelField, 1, 0);
        form.add(new Label("Concentrazione (min):"), 0, 1);
        form.add(focusMinutesField, 1, 1);
        form.add(new Label("Pausa (min):"), 0, 2);
        form.add(breakMinutesField, 1, 2);

        statusLabel = new Label("Pronto");
        statusLabel.setStyle("-fx-font-size: 14px;");

        timeLabel = new Label("00:00");
        timeLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold;");

        startButton = new Button("Avvia");
        startButton.setOnAction(e -> onStart());

        pauseResumeButton = new Button("Pausa");
        pauseResumeButton.setDisable(true);
        pauseResumeButton.setOnAction(e -> onPauseResume());

        skipButton = new Button("Salta fase");
        skipButton.setDisable(true);
        skipButton.setOnAction(e -> onSkip());

        HBox buttons = new HBox(8, startButton, pauseResumeButton, skipButton);
        buttons.setAlignment(Pos.CENTER);

        VBox pane = new VBox(12, form, statusLabel, timeLabel, buttons);
        pane.setAlignment(Pos.CENTER);
        pane.setPadding(new Insets(12, 0, 12, 0));
        return pane;
    }

    private VBox buildStatsPane() {
        Label header = new Label("Pomodori per etichetta");
        header.setStyle("-fx-font-weight: bold;");

        statsListView = new ListView<>();
        statsListView.setPrefHeight(120);

        totalPomodorosLabel = new Label();
        totalBreakTimeLabel = new Label();

        VBox pane = new VBox(6, header, statsListView, totalPomodorosLabel, totalBreakTimeLabel);
        pane.setPadding(new Insets(12, 0, 0, 0));
        return pane;
    }

    private void onStart() {
        currentLabel = labelField.getText().trim();
        if (currentLabel.isEmpty()) {
            currentLabel = "Senza etichetta";
        }
        focusDurationSeconds = parseMinutesOrDefault(focusMinutesField, DEFAULT_FOCUS_MINUTES) * 60L;
        breakDurationSeconds = parseMinutesOrDefault(breakMinutesField, DEFAULT_BREAK_MINUTES) * 60L;

        labelField.setDisable(true);
        focusMinutesField.setDisable(true);
        breakMinutesField.setDisable(true);
        startButton.setDisable(true);
        pauseResumeButton.setDisable(false);
        pauseResumeButton.setText("Pausa");
        skipButton.setDisable(false);

        startFocusPhase();

        timeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1), e -> onTick()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void onPauseResume() {
        if (timeline == null) {
            return;
        }
        if (pauseResumeButton.getText().equals("Pausa")) {
            timeline.pause();
            pauseResumeButton.setText("Riprendi");
        } else {
            timeline.play();
            pauseResumeButton.setText("Pausa");
        }
    }

    private void onSkip() {
        completeCurrentPhase();
    }

    private void onTick() {
        remainingSeconds--;
        if (remainingSeconds < 0) {
            completeCurrentPhase();
        } else {
            updateTimeLabel();
        }
    }

    private void startFocusPhase() {
        phase = Phase.FOCUS;
        remainingSeconds = focusDurationSeconds;
        statusLabel.setText("Concentrazione su \"" + currentLabel + "\"");
        updateTimeLabel();
    }

    private void startBreakPhase() {
        phase = Phase.BREAK;
        remainingSeconds = breakDurationSeconds;
        statusLabel.setText("Pausa");
        updateTimeLabel();
    }

    private void completeCurrentPhase() {
        if (phase == Phase.FOCUS) {
            tracker.recordFocusCompleted(currentLabel, Duration.ofSeconds(focusDurationSeconds));
            refreshStats();
            startBreakPhase();
        } else if (phase == Phase.BREAK) {
            tracker.recordBreakCompleted(Duration.ofSeconds(breakDurationSeconds));
            refreshStats();
            finishCycle();
        }
    }

    private void finishCycle() {
        if (timeline != null) {
            timeline.stop();
        }
        phase = Phase.IDLE;
        remainingSeconds = 0;
        updateTimeLabel();
        statusLabel.setText("Pomodoro completato! Pronto per il prossimo.");

        labelField.setDisable(false);
        focusMinutesField.setDisable(false);
        breakMinutesField.setDisable(false);
        startButton.setDisable(false);
        pauseResumeButton.setDisable(true);
        pauseResumeButton.setText("Pausa");
        skipButton.setDisable(true);
    }

    private void updateTimeLabel() {
        long minutes = remainingSeconds / 60;
        long seconds = remainingSeconds % 60;
        timeLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void refreshStats() {
        statsListView.setItems(FXCollections.observableArrayList(formatLabelCounts()));
        totalPomodorosLabel.setText("Totale pomodori: " + tracker.getTotalPomodoroCount());
        totalBreakTimeLabel.setText("Tempo totale in pausa: " + formatDuration(tracker.getTotalBreakTime()));
    }

    private List<String> formatLabelCounts() {
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : tracker.getPomodoroCountsByLabel().entrySet()) {
            lines.add(entry.getKey() + ": " + entry.getValue());
        }
        return lines;
    }

    private static String formatDuration(Duration duration) {
        long totalSeconds = duration.getSeconds();
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%d min %02d sec", minutes, seconds);
    }

    private int parseMinutesOrDefault(TextField field, int defaultValue) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            return defaultValue;
        }
        try {
            int value = Integer.parseInt(text);
            if (value <= 0) {
                throw new NumberFormatException();
            }
            return value;
        } catch (NumberFormatException e) {
            showInvalidDurationAlert(text, defaultValue);
            field.setText(String.valueOf(defaultValue));
            return defaultValue;
        }
    }

    private void showInvalidDurationAlert(String invalidValue, int defaultValue) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Valore non valido");
        alert.setHeaderText(null);
        alert.setContentText("\"" + invalidValue + "\" non è un numero di minuti valido. Uso il default: " + defaultValue + ".");
        alert.showAndWait();
    }
}
