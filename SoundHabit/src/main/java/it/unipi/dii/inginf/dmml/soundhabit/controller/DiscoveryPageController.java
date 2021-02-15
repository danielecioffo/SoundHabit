package it.unipi.dii.inginf.dmml.soundhabit.controller;

import it.unipi.dii.inginf.dmml.soundhabit.model.Genre;
import it.unipi.dii.inginf.dmml.soundhabit.model.Song;
import it.unipi.dii.inginf.dmml.soundhabit.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class DiscoveryPageController {
    @FXML private TextField searchBar;
    @FXML private Button searchButton;
    @FXML private ComboBox<String> searchComboBox;
    @FXML private Button previousButton;
    @FXML private Button nextButton;
    @FXML private VBox showingVBox;


    /**
     ** Method called when the controller is initialized
     */
    public void initialize()
    {
        List<Song> songList = new ArrayList<>();
        songList.add(new Song("Prova", Genre.BLUES, "www.google.com", "Mozart", ""));
        songList.add(new Song("Prova", Genre.BLUES, "www.google.com", "Mozart", ""));
        Utils.showSongs(showingVBox, songList);
    }

}
