package it.unipi.dii.inginf.dmml.soundhabit.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class DiscoveryPageController {
    @FXML private Button homeButton;
    @FXML private Button profileButton;
    @FXML private TextField searchBar;
    @FXML private Button searchButton;
    @FXML private ComboBox<String> searchComboBox;
    @FXML private Button previousButton;
    @FXML private Button nextButton;

    /**
     ** Method called when the controller is initialized
     */
    public void initialize()
    {
        homeButton.setOnMouseClicked(eventHandler -> {System.out.println("Home clicked");});
    }

}
