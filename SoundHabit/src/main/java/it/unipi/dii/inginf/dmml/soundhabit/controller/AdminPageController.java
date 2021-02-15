package it.unipi.dii.inginf.dmml.soundhabit.controller;

import it.unipi.dii.inginf.dmml.soundhabit.model.Author;
import it.unipi.dii.inginf.dmml.soundhabit.model.Genre;
import it.unipi.dii.inginf.dmml.soundhabit.model.Song;
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

public class AdminPageController {
    @FXML private TextField songTitle;
    @FXML private TextField songAuthor;
    @FXML private TextField songUrl;
    @FXML private TextField imageUrl;
    @FXML private ComboBox genresComboBox;
    @FXML private Button classifyButton;
    @FXML private Button clearFieldsButton;
    @FXML private Button insertSongButton;


    /**
     ** Method called when the controller is initialized
     */
    public void initialize() {
        genresComboBox.getItems().addAll(Genre.BLUES, Genre.CLASSICAL, Genre.JAZZ, Genre.METAL, Genre.POP, Genre.ROCK);
        classifyButton.setOnMouseClicked(mouseEvent -> classifySong(mouseEvent));
        clearFieldsButton.setOnMouseClicked(mouseEvent -> clearFields());
        insertSongButton.setOnMouseClicked(mouseEvent -> insertSong());
    }

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

    private void insertSong() {
        if(songTitle.getText().equals("") || songAuthor.getText().equals("") || songUrl.getText().equals("")
        || imageUrl.getText().equals("") || genresComboBox.getSelectionModel().isEmpty()) {
            Utils.showErrorAlert("You have to fill all the fields!");
            return;
        }

        Genre songGenre = (Genre) genresComboBox.getSelectionModel().getSelectedItem();
        Song newSong = new Song(songTitle.getText(), songGenre, songUrl.getText(), new Author(songAuthor.getText()), imageUrl.getText());

        //TODO fai inserimento nel DB con risultato inserito in "insert"
        boolean insert = true;

        if(insert) {
            Utils.showInfoAlert("The song was correctly inserted");
            clearFields();
        } else {
            Utils.showErrorAlert("Error during the insertion");
        }
    }

    private void clearFields() {
        songTitle.setText(""); songAuthor.setText("");
        songUrl.setText(""); imageUrl.setText("");
        genresComboBox.getSelectionModel().clearSelection();
    }
}