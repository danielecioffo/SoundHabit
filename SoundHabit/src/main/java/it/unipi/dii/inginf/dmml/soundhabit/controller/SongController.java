package it.unipi.dii.inginf.dmml.soundhabit.controller;

import it.unipi.dii.inginf.dmml.soundhabit.model.Song;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class SongController {
    private Song song; // Song to handle
    @FXML private AnchorPane songPane;
    @FXML private ImageView songImageView;
    @FXML private ImageView songLikeImageView;
    @FXML private Label nameLabel;
    @FXML private Label authorLabel;
    @FXML private Label genreLabel;
    @FXML private Label linkLabel;


    /**
     * Method called when the controller is initialized
     */
    public void initialize ()
    {

    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
        nameLabel.setText("Name: " + song.getName());
        authorLabel.setText("Author: " + song.getAuthor());
        genreLabel.setText("Genre: " + song.getGenre().toProperCase());
        linkLabel.setText("Link: " + song.getSongLink());
    }
}
