package it.unipi.dii.inginf.dmml.soundhabit.controller;

import it.unipi.dii.inginf.dmml.soundhabit.classification.Classifier;
import it.unipi.dii.inginf.dmml.soundhabit.classification.FeatureExtractor;
import it.unipi.dii.inginf.dmml.soundhabit.classification.SongFeature;
import it.unipi.dii.inginf.dmml.soundhabit.utils.Utils;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.util.Pair;

import java.io.File;

public class ClassifyPageController {
    @FXML public BarChart barChart;
    @FXML private TextField filePath;
    @FXML private Button selectFileButton;
    @FXML private Button classifyButton;
    @FXML private ProgressIndicator progressCircle;
    @FXML private Label waitLabel;

    /**
     ** Method called when the controller is initialized
     */
    public void initialize() {
        selectFileButton.setOnMouseClicked(mouseEvent -> selectFile());
        classifyButton.setOnMouseClicked(mouseEvent -> classifySong());
    }

    /**
     * Event handler for the click on the "Select File" button
     */
    private void selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("WAV Files", "*.wav"));
        File file = fileChooser.showOpenDialog(null);

        if(file != null) {
            filePath.setText(file.getAbsolutePath());
        }
    }

    /**
     * Event handler for the click on the "Classify" button
     */
    private void classifySong() {
        if(filePath.getText().equals("")) {
            Utils.showErrorAlert("You have to select a file first");
            return;
        }

        clearScene();
        progressCircle.setVisible(true);
        waitLabel.setVisible(true);

        Task<Pair<Integer, double[]>> classifyTask = new Task<>() {
            @Override
            protected Pair<Integer, double[]> call() throws Exception {
                Classifier classifier = Classifier.getInstance();
                FeatureExtractor f = new FeatureExtractor();
                SongFeature song = f.getSongFeaturesOfSong(filePath.getText());
                Pair<Integer, double[]> classify = classifier.classify(song.toInstances());
                return classify;
            }
        };

        classifyTask.setOnSucceeded(event -> {
            Pair<Integer,double[]> classify = classifyTask.getValue();
            progressCircle.setVisible(false);
            waitLabel.setText("The genre is " + Utils.integerToGenre(classify.getKey()));
            barChart.setVisible(true);

            XYChart.Series series1 = new XYChart.Series();
            series1.setName("% Affinity");
            series1.getData().add(new XYChart.Data("BLUES", classify.getValue()[0] * 100));
            series1.getData().add(new XYChart.Data("CLASSICAL", classify.getValue()[1] * 100));
            series1.getData().add(new XYChart.Data("JAZZ", classify.getValue()[2] * 100));
            series1.getData().add(new XYChart.Data("METAL", classify.getValue()[3] * 100));
            series1.getData().add(new XYChart.Data("POP", classify.getValue()[4] * 100));
            series1.getData().add(new XYChart.Data("ROCK", classify.getValue()[5] * 100));
            barChart.getData().addAll(series1);
        });

        new Thread(classifyTask).start(); //TODO handle exception
    }

    /**
     * Clears the previous results (if any)
     */
    private void clearScene() {
        barChart.getData().clear(); barChart.setVisible(false);
        waitLabel.setText("Please wait..."); waitLabel.setVisible(false);
    }
}
