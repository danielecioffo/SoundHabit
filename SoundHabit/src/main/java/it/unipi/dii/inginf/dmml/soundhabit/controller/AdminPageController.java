package it.unipi.dii.inginf.dmml.soundhabit.controller;

import it.unipi.dii.inginf.dmml.soundhabit.model.Genre;
import it.unipi.dii.inginf.dmml.soundhabit.model.Song;
import it.unipi.dii.inginf.dmml.soundhabit.persistence.Neo4jDriver;
import it.unipi.dii.inginf.dmml.soundhabit.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdminPageController {
    @FXML private TextField songTitle;
    @FXML private TextField songAuthor;
    @FXML private TextField songUrl;
    @FXML private TextField imageUrl;
    @FXML private CheckBox bluesBox;
    @FXML private CheckBox classicalBox;
    @FXML private CheckBox jazzBox;
    @FXML private CheckBox metalBox;
    @FXML private CheckBox popBox;
    @FXML private CheckBox rockBox;
    @FXML private Button classifyButton;
    @FXML private Button clearFieldsButton;
    @FXML private Button insertSongButton;
    private Neo4jDriver neo4jDriver;

    /**
     ** Method called when the controller is initialized
     */
    public void initialize() {
        neo4jDriver = Neo4jDriver.getInstance();

        classifyButton.setOnMouseClicked(this::classifySong);
        clearFieldsButton.setOnMouseClicked(mouseEvent -> clearFields());
        insertSongButton.setOnMouseClicked(mouseEvent -> insertSong());
    }

    /**
     * Event handler for che click on the "Classify" button
     * @param mouseEvent    event
     */
    private void classifySong(MouseEvent mouseEvent) {
        Node node = (Node) mouseEvent.getSource();
        Stage parentStage = (Stage) node.getScene().getWindow();

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/classify.fxml"));
            Parent root = fxmlLoader.load();
            Stage dialog = new Stage(); dialog.setScene(new Scene(root));
            dialog.setTitle("Classify genre"); dialog.initOwner(parentStage);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Event handler for the click on the "Insert Song" button
     */
    private void insertSong() {
        if(songTitle.getText().equals("") || songAuthor.getText().equals("")
                || ( !bluesBox.isSelected() && !classicalBox.isSelected() && !jazzBox.isSelected() && !metalBox.isSelected() && !popBox.isSelected() && !rockBox.isSelected())) {
            Utils.showErrorAlert("You have to fill at least the title, author and genre fields!");
            return;
        }

        List<Genre> genreList = getSelectedGenres();
        if(genreList.isEmpty()) { // Something went wrong
            Utils.showErrorAlert("Please select at least one genre!");
            return;
        }

        String url;
        if(songUrl.getText().equals(""))
            url = null;
        else
            url = songUrl.getText();

        String image;
        if(imageUrl.getText().equals(""))
            image = null;
        else
            image = imageUrl.getText();

        Song newSong = new Song(songTitle.getText(), genreList, url, songAuthor.getText(), image);

        boolean insert = neo4jDriver.addSong(newSong);

        if(insert) {
            Utils.showInfoAlert("The song was correctly inserted");
            clearFields();
        } else {
            Utils.showErrorAlert("Error during the insertion");
        }
    }

    private ArrayList<Genre> getSelectedGenres() {
        ArrayList<Genre> genres = new ArrayList<>();

        if(bluesBox.isSelected())
            genres.add(Genre.BLUES);
        if(classicalBox.isSelected())
            genres.add(Genre.CLASSICAL);
        if(jazzBox.isSelected())
            genres.add(Genre.JAZZ);
        if(metalBox.isSelected())
            genres.add(Genre.METAL);
        if(rockBox.isSelected())
            genres.add(Genre.ROCK);

        return genres;
    }

    /**
     * Event handler for the "Clear Fields" button
     */
    private void clearFields() {
        songTitle.setText(""); songAuthor.setText("");
        songUrl.setText(""); imageUrl.setText("");
        bluesBox.setSelected(false);
        classicalBox.setSelected(false);
        jazzBox.setSelected(false);
        metalBox.setSelected(false);
        popBox.setSelected(false);
        rockBox.setSelected(false);
    }
}
