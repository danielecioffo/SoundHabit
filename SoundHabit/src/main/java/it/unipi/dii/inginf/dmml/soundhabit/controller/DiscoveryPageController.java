package it.unipi.dii.inginf.dmml.soundhabit.controller;

import it.unipi.dii.inginf.dmml.soundhabit.model.Genre;
import it.unipi.dii.inginf.dmml.soundhabit.model.Session;
import it.unipi.dii.inginf.dmml.soundhabit.model.Song;
import it.unipi.dii.inginf.dmml.soundhabit.persistence.Neo4jDriver;
import it.unipi.dii.inginf.dmml.soundhabit.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

public class DiscoveryPageController {
    @FXML private TextField searchBar;
    @FXML private Button searchButton;
    @FXML private ComboBox<String> searchComboBox;
    @FXML private Button previousButton;
    @FXML private Button nextButton;
    @FXML private VBox showingVBox;

    private final int HOW_MANY_SONGS_TO_SHOW = 5;
    private final int LIKE_THRESHOLD = 5;
    private int page; // number of page (at the beginning at 0), increase with nextButton and decrease with previousButton
    private Neo4jDriver neo4jDriver;

    /**
     ** Method called when the controller is initialized
     */
    public void initialize()
    {
        page = 0;

        // Initializing the options of the ComboBox
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Suggested songs",
                        "Songs liked",
                        "Song name",
                        "Song author",
                        "Blues songs",
                        "Classical songs",
                        "Jazz songs",
                        "Metal songs",
                        "Pop songs",
                        "Rock songs",
                        "Most liked songs"
                );
        searchComboBox.setItems(options);
        searchComboBox.setValue("Suggested songs");

        // if some changes happens to the combobox
        searchComboBox.setOnAction(actionEvent -> comboAction((ActionEvent) actionEvent));
        searchButton.setOnAction(actionEvent -> search(actionEvent));

        nextButton.setOnMouseClicked(mouseEvent -> clickOnNext(mouseEvent));
        previousButton.setOnMouseClicked(mouseEvent -> clickOnPrevious(mouseEvent));
        previousButton.setVisible(false); //in the first page it is not visible

        neo4jDriver = Neo4jDriver.getInstance();
        showSuggestedSongs();
    }

    /**
     * Function that shows the suggested songs
     */
    private void showSuggestedSongs ()
    {
        List<Song> songs = neo4jDriver.getSuggestedSongsConsideringLike(Session.getInstance().getLoggedUser(),
                LIKE_THRESHOLD,HOW_MANY_SONGS_TO_SHOW*page, HOW_MANY_SONGS_TO_SHOW);
        if (songs.size() != 0)
        {
            Label label = new Label("What others likes");
            label.setFont(Font.font(36));
            showingVBox.getChildren().add(label);
            Utils.showSongs(showingVBox, songs);
        }
        songs = neo4jDriver.getSuggestedSongsConsideringGenre(Session.getInstance().getLoggedUser(),
                HOW_MANY_SONGS_TO_SHOW*page, HOW_MANY_SONGS_TO_SHOW);
        if (songs.size() != 0)
        {
            Label label = new Label("Most liked genre");
            label.setFont(Font.font(36));
            showingVBox.getChildren().add(label);
            Utils.showSongs(showingVBox, songs);
        }
    }

    /**
     * Function that handle the click on the search button
     * @param actionEvent
     */
    private void search(ActionEvent actionEvent) {
        Utils.removeAllFromPane(showingVBox);
        if (String.valueOf(searchComboBox.getValue()).equals("Suggested songs"))
        {
            showSuggestedSongs();
        }
        else if (String.valueOf(searchComboBox.getValue()).equals("Songs liked"))
        {
            List<Song> songs = neo4jDriver.searchSongsLiked(Session.getInstance().getLoggedUser(),
                    HOW_MANY_SONGS_TO_SHOW*page, HOW_MANY_SONGS_TO_SHOW);
            Utils.showSongs(showingVBox, songs);
        }
        else if (String.valueOf(searchComboBox.getValue()).equals("Song name"))
        {
            List<Song> songs = neo4jDriver.searchByName(searchBar.getText(), HOW_MANY_SONGS_TO_SHOW*page,
                    HOW_MANY_SONGS_TO_SHOW);
            Utils.showSongs(showingVBox, songs);
        }
        else if (String.valueOf(searchComboBox.getValue()).equals("Song author"))
        {
            List<Song> songs = neo4jDriver.searchByAuthorName(searchBar.getText(),
                    HOW_MANY_SONGS_TO_SHOW*page, HOW_MANY_SONGS_TO_SHOW);
            Utils.showSongs(showingVBox, songs);
        }
        else if (String.valueOf(searchComboBox.getValue()).equals("Blues songs"))
        {
            List<Song> songs = neo4jDriver.getSongsOfGenre(Genre.BLUES, HOW_MANY_SONGS_TO_SHOW*page,
                    HOW_MANY_SONGS_TO_SHOW);
            Utils.showSongs(showingVBox, songs);
        }
        else if (String.valueOf(searchComboBox.getValue()).equals("Classical songs"))
        {
            List<Song> songs = neo4jDriver.getSongsOfGenre(Genre.CLASSICAL, HOW_MANY_SONGS_TO_SHOW*page,
                    HOW_MANY_SONGS_TO_SHOW);
            Utils.showSongs(showingVBox, songs);
        }
        else if (String.valueOf(searchComboBox.getValue()).equals("Jazz songs"))
        {
            List<Song> songs = neo4jDriver.getSongsOfGenre(Genre.JAZZ, HOW_MANY_SONGS_TO_SHOW*page,
                    HOW_MANY_SONGS_TO_SHOW);
            Utils.showSongs(showingVBox, songs);
        }
        else if (String.valueOf(searchComboBox.getValue()).equals("Metal songs"))
        {
            List<Song> songs = neo4jDriver.getSongsOfGenre(Genre.METAL, HOW_MANY_SONGS_TO_SHOW*page,
                    HOW_MANY_SONGS_TO_SHOW);
            Utils.showSongs(showingVBox, songs);
        }
        else if (String.valueOf(searchComboBox.getValue()).equals("Pop songs"))
        {
            List<Song> songs = neo4jDriver.getSongsOfGenre(Genre.POP, HOW_MANY_SONGS_TO_SHOW*page,
                    HOW_MANY_SONGS_TO_SHOW);
            Utils.showSongs(showingVBox, songs);
        }
        else if (String.valueOf(searchComboBox.getValue()).equals("Rock songs"))
        {
            List<Song> songs = neo4jDriver.getSongsOfGenre(Genre.ROCK, HOW_MANY_SONGS_TO_SHOW*page,
                    HOW_MANY_SONGS_TO_SHOW);
            Utils.showSongs(showingVBox, songs);
        }
        else if(String.valueOf(searchComboBox.getValue()).equals("Most liked songs"))
        {
            List<Song> songs = neo4jDriver.getMostLikedSongs(HOW_MANY_SONGS_TO_SHOW*page,
                    HOW_MANY_SONGS_TO_SHOW);
            Utils.showSongs(showingVBox, songs);
        }
    }

    /**
     * Function that handle the changes to the searchComboBox
     * @param event     Event that leads to this function
     */
    private void comboAction(ActionEvent event) {
        page = 0;
        Utils.removeAllFromPane(showingVBox);
    }

    /**
     * Handler for the next button
     * @param mouseEvent    Event that leads to this function
     */
    private void clickOnNext(MouseEvent mouseEvent) {
        page++;
        if (page > 0)
            previousButton.setVisible(true);
        searchButton.fire(); // simulate the click of the button
    }

    /**
     * Handler for the previous button
     * @param mouseEvent    Event that leads to this function
     */
    private void clickOnPrevious(MouseEvent mouseEvent) {
        page--;
        if (page < 1)
            previousButton.setVisible(false);
        searchButton.fire(); // simulate the click of the button
    }

}
