package it.unipi.dii.inginf.dmml.soundhabit.app;

import it.unipi.dii.inginf.dmml.soundhabit.persistence.Neo4jDriver;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class SoundHabit extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/welcome.fxml"));
        FXMLLoader loadErrorPage = new FXMLLoader(getClass().getResource("/errorPage.fxml"));

        boolean connectionDoneNeo4j = Neo4jDriver.getInstance().initConnection();
        if(!connectionDoneNeo4j)
        {
            primaryStage.setScene(new Scene(loadErrorPage.load()));
        }
        else
            primaryStage.setScene(new Scene(loader.load()));

        primaryStage.setTitle("SoundHabit");
        primaryStage.show();
        primaryStage.getIcons().add(new Image("/img/icon.png"));
        primaryStage.setResizable(false);


        primaryStage.setOnCloseRequest(actionEvent -> Neo4jDriver.getInstance().closeConnection());
    }


    public static void main(String[] args) {
        launch(args);
    }
}
