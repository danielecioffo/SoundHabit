package it.unipi.dii.inginf.dmml.soundhabit.app;

import it.unipi.dii.inginf.dmml.soundhabit.classification.Classifier;
import it.unipi.dii.inginf.dmml.soundhabit.classification.FeatureExtractor;
import it.unipi.dii.inginf.dmml.soundhabit.classification.SongFeature;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class SoundHabit extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/welcome.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle("SoundHabit");
        primaryStage.show();
        primaryStage.getIcons().add(new Image("/img/icon.png"));
        primaryStage.setResizable(false);

        Classifier classifier = Classifier.getInstance();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
