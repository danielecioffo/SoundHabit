package it.unipi.dii.inginf.dmml.soundhabit.utils;

import com.thoughtworks.xstream.XStream;
import it.unipi.dii.inginf.dmml.soundhabit.config.ConfigurationParameters;
import it.unipi.dii.inginf.dmml.soundhabit.controller.SongController;
import it.unipi.dii.inginf.dmml.soundhabit.model.Genre;
import it.unipi.dii.inginf.dmml.soundhabit.model.Song;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Class that contains some useful method
 */
public class Utils {

    /**
     * Snippet of code for jumping in the next scene
     * Every scene has associated its specific controller
     * @param fileName      The name of the file in which i can obtain the GUI (.fxml)
     * @param event         The event that leads to change the scene
     * @return The new controller, because I need to pass some parameters
     */
    public static Object changeScene (String fileName, Event event)
    {
        Scene scene = null;
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(Utils.class.getResource(fileName));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.show();
            return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This function is used to read the config.xml file
     * @return  ConfigurationParameters instance
     */
    public static ConfigurationParameters readConfigurationParameters ()
    {
        if (validConfigurationParameters())
        {
            XStream xs = new XStream();

            String text = null;
            try {
                text = new String(Files.readAllBytes(Paths.get("./config.xml")));
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
            }

            return (ConfigurationParameters) xs.fromXML(text);
        }
        else
        {
            showErrorAlert("Problem with the configuration file!");
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(1); //If i can't read the configuration file I can't continue with the program
        }
        return null;
    }

    /**
     * This function is used to validate the config.xml with the config.xsd
     * @return  true if config.xml is well formatted, otherwise false
     */
    private static boolean validConfigurationParameters()
    {
        try
        {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Document document = documentBuilder.parse("./config.xml");
            Schema schema = schemaFactory.newSchema(new StreamSource("./config.xsd"));
            schema.newValidator().validate(new DOMSource(document));
        }
        catch (Exception e)
        {
            if (e instanceof SAXException)
                System.out.println("Validation Error: " + e.getMessage());
            else
                System.out.println(e.getMessage());

            return false;
        }
        return true;
    }

    /**
     * Function that shows an error alert
     * @param text  Text to be shown
     */
    public static void showErrorAlert (String text)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(text);
        alert.setHeaderText("Ops.. Something went wrong..");
        alert.setTitle("Error");
        ImageView imageView = new ImageView(new Image("/img/error.png"));
        alert.setGraphic(imageView);
        alert.show();
    }

    /**
     * Function that shows an information windows
     * @param text  Text to be shown
     */
    public static void showInfoAlert (String text)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(text);
        alert.setHeaderText("Confirm Message");
        alert.setTitle("Information");
        ImageView imageView = new ImageView(new Image("/img/success.png"));
        imageView.setFitHeight(60);
        imageView.setFitWidth(60);
        imageView.setPreserveRatio(true);
        alert.setGraphic(imageView);
        alert.show();
    }

    /**
     * Function used to remove all element on a pane
     * @param pane      Pane to free
     */
    public static void removeAllFromPane (Pane pane)
    {
        pane.getChildren().remove(0, pane.getChildren().size());
    }


    /**
     * Function that load the instances of the dataset
     * @param path  Path to the dataset
     * @return  The dataset (instances)
     * @throws Exception
     */
    public static Instances loadDataset (String path) throws Exception {
        ConverterUtils.DataSource source = new ConverterUtils.DataSource(path);
        Instances instances = source.getDataSet();
        instances.setClassIndex(instances.numAttributes() - 1);

        return instances;
    }

    public static Genre integerToGenre(int genre) {
        switch (genre) {
            case 0:
                return Genre.BLUES;
            case 1:
                return Genre.CLASSICAL;
            case 2:
                return Genre.JAZZ;
            case 3:
                return Genre.METAL;
            case 4:
                return Genre.POP;
            case 5:
                return Genre.ROCK;
            default:
                return Genre.BLUES;
        }
    }

    /**
     * Function used to show a list of songs in the VBox
     * @param vBox      Pane in which I have to show the songs
     * @param songs     Songs to show
     */
    public static void showSongs(VBox vBox, List<Song> songs) {
        for (Song song: songs)
        {
            Pane commentPane = loadSong(song);
            vBox.getChildren().add(commentPane);
        }
    }

    /**
     * Function used to load the .fxml for the song
     * @param song      Song to show
     * @return          The pane in which I have showed the song
     */
    private static Pane loadSong (Song song)
    {
        Pane pane = null;
        try {
            FXMLLoader loader = new FXMLLoader(Utils.class.getResource("/song.fxml"));
            pane = (Pane) loader.load();
            SongController songController =
                    (SongController) loader.getController();
            songController.setSong(song);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pane;
    }
}
