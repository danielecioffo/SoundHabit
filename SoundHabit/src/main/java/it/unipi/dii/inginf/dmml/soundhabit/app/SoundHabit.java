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

        //TODO DEBUG cancella poi
        List<Double> mfcc = Arrays.asList(-52.51793670654297,37.48890686035156,14.116278648376465,18.180391311645508,16.781204223632812,5.961240291595459,6.443281650543213,2.7201662063598633,-0.4362645447254181,3.2229056358337402,-3.9291481971740723,1.6018062829971313,0.29740315675735474,-0.16164188086986542,-0.5097914934158325,-1.5459802150726318,2.0748283863067627,1.2206205129623413,4.581011772155762,1.6373120546340942);
        SongFeature song = new SongFeature(0.44746074080467224,0.19151729345321655,3889.624969187498,3271.614422137673,8056.984613849651,0.19104230662248453,mfcc);
        double[] classify = classifier.classify(song.toInstances());
        for(Double d: classify) {
            System.out.println(d);
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
