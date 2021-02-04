package it.unipi.dii.inginf.dmml.soundhabit.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class SoundHabit extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/welcome.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle("SoundHabit");
        primaryStage.show();
        primaryStage.getIcons().add(new Image("/img/icon.png"));
    }


    public static void main(String[] args) {
        launch(args);
    }
}
