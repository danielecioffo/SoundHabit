package it.unipi.dii.inginf.dmml.soundhabit.app;

import it.unipi.dii.inginf.dmml.soundhabit.classification.Classifier;
import it.unipi.dii.inginf.dmml.soundhabit.classification.FeatureExtractor;
import it.unipi.dii.inginf.dmml.soundhabit.classification.SongFeature;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class SoundHabit extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/welcome.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle("SoundHabit");
        primaryStage.show();
        primaryStage.getIcons().add(new Image("/img/icon.png"));
        primaryStage.setResizable(false);

        //TODO DEBUG cancella poi

        Classifier classifier = Classifier.getInstance();
        FeatureExtractor f = new FeatureExtractor();
        SongFeature song = f.getSongFeaturesOfSong("../Test Set/[POP] Taylor Swift - Shake It Off.wav");

        double[] classify = classifier.classify(song.toInstances());
        for(Double d: classify) {
            System.out.println(d);
        }

        /*
        // POP
        /*List<Double> mfcc = Arrays.asList(-52.51793670654297,37.48890686035156,14.116278648376465,18.180391311645508,16.781204223632812,5.961240291595459,6.443281650543213,2.7201662063598633,-0.4362645447254181,3.2229056358337402,-3.9291481971740723,1.6018062829971313,0.29740315675735474,-0.16164188086986542,-0.5097914934158325,-1.5459802150726318,2.0748283863067627,1.2206205129623413,4.581011772155762,1.6373120546340942);
        SongFeature song = new SongFeature(0.44746074080467224,0.19151729345321655,3889.624969187498,3271.614422137673,8056.984613849651,0.19104230662248453,mfcc);
        double[] classify = classifier.classify(song.toInstances());
        for(Double d: classify) {
            System.out.println(d);
        }

        // BLUES
        List<Double> mfcc2 = Arrays.asList(-284.81951904296875,108.78562927246094,9.131957054138184,51.2590217590332,18.111255645751953,7.621315956115723,8.781747817993164,2.3724257946014404,0.011786398477852345,2.2504167556762695,4.200277805328369,-3.303734302520752,1.6015605926513672,2.6605160236358643,3.3234546184539795,3.2589197158813477,-4.551105976104736,0.49384555220603943,5.937065601348877,3.231544256210327);
        SongFeature song2 = new SongFeature(0.2784844636917114,0.07697049528360367,1198.6076653608022,1573.308974392403,2478.3766802619484,0.051987591911764705, mfcc2);
        double[] classify2 = classifier.classify(song2.toInstances());
        for(Double d: classify2) {
            System.out.println(d);
        }*/
    }


    public static void main(String[] args) {
        launch(args);
    }
}
