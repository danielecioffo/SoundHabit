package it.unipi.dii.inginf.dmml.soundhabit.controller;

import it.unipi.dii.inginf.dmml.soundhabit.model.Genre;
import it.unipi.dii.inginf.dmml.soundhabit.model.Session;
import it.unipi.dii.inginf.dmml.soundhabit.model.Song;
import it.unipi.dii.inginf.dmml.soundhabit.persistence.Neo4jDriver;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class SongController {
    private Song song; // Song to handle
    private Neo4jDriver neo4jDriver;
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
        neo4jDriver = Neo4jDriver.getInstance();
        songLikeImageView.setOnMouseClicked(actionEvent -> handleClickOnLike());
    }

    public void setSong(Song song) {
        this.song = song;
        nameLabel.setText("Name: " + song.getName());
        authorLabel.setText("Author: " + song.getAuthor());
        genreLabel.setText("Genre: " + song.getGenresString("; "));

        if (song.getSongLink() != null && !song.getSongLink().equals("null")) {
            linkLabel.setText("Link: " + song.getSongLink());
        } else {
            linkLabel.setText(" ");
        }

        if (song.getImageLink() != null && !song.getImageLink().equals("null")) {
            try {
                songImageView.setImage(new Image(song.getImageLink(), true));
            } catch(Exception e) {
                songImageView.setImage(new Image("img/defaultSong.png", true));
            }
        }
        else {
            songImageView.setImage(new Image("img/defaultSong.png", true));
        }

        if(neo4jDriver.isThisSongLikedByUser(Session.getInstance().getLoggedUser(), song))
            songLikeImageView.setImage(new Image("img/alreadyliked.png"));
    }

    /**
     * Handler function for the click on the like
     */
    private void handleClickOnLike()
    {
        if(neo4jDriver.isThisSongLikedByUser(Session.getInstance().getLoggedUser(), song))
        {
            neo4jDriver.unlike(Session.getInstance().getLoggedUser(), song);
            songLikeImageView.setImage(new Image("img/like.png"));
        }
        else
        {
            neo4jDriver.like(Session.getInstance().getLoggedUser(), song);
            songLikeImageView.setImage(new Image("img/alreadyliked.png"));
        }
    }
}
