package it.unipi.dii.inginf.dmml.soundhabit.controller;

import it.unipi.dii.inginf.dmml.soundhabit.classification.Classifier;
import it.unipi.dii.inginf.dmml.soundhabit.classification.FeatureExtractor;
import it.unipi.dii.inginf.dmml.soundhabit.classification.SongFeature;
import it.unipi.dii.inginf.dmml.soundhabit.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;

public class ClassifyPageController {
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

    private void selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("WAV Files", "*.wav"));
        File file = fileChooser.showOpenDialog(null);

        if(file != null) {
            filePath.setText(file.getAbsolutePath());
        }
    }

    private void classifySong() {
        if(filePath.getText().equals("")) {
            Utils.showErrorAlert("You have to select a file first");
            return;
        }

        waitLabel.setVisible(true);
        progressCircle.setVisible(true);

        Classifier classifier = Classifier.getInstance();
        FeatureExtractor f = new FeatureExtractor();
        SongFeature song = f.getSongFeaturesOfSong(filePath.getText());
        int classify = classifier.classify(song.toInstances());

        progressCircle.setVisible(false);
        waitLabel.setText("The genre is " + Utils.integerToGenre(classify));
    }
}
