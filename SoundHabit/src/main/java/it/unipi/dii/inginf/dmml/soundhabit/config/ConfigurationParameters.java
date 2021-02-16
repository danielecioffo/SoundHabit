package it.unipi.dii.inginf.dmml.soundhabit.config;

import com.thoughtworks.xstream.XStream;
import it.unipi.dii.inginf.dmml.soundhabit.utils.Utils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.lang.Thread.sleep;

/**
 * Class used to store the configuration parameters retrieved from the config.xml
 * There is no need to modify this value, so there are only the getters methods
 */
public class ConfigurationParameters {
    public static volatile ConfigurationParameters instance;
    private String neo4jIp;
    private int neo4jPort;
    private String neo4jUsername;
    private String neo4jPassword;
    private String featureExtractorServerIp;
    private int featureExtractorServerPort;

    public static ConfigurationParameters getInstance(){
        if(instance == null)
        {
            synchronized (ConfigurationParameters.class)
            {
                if(instance==null)
                {
                    instance = readConfigurationParameters();
                }
            }
        }
        return instance;
    }

    /**
     * This function is used to read the config.xml file
     * @return  ConfigurationParameters instance
     */
    private static ConfigurationParameters readConfigurationParameters ()
    {
        if (validConfigurationParameters())
        {
            XStream xs = new XStream();

            String text = null;
            try {
                text = new String(Files.readAllBytes(Paths.get("config.xml")));
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
            }

            return (ConfigurationParameters) xs.fromXML(text);
        }
        else
        {
            Utils.showErrorAlert("Problem with the configuration file!");
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
            Document document = documentBuilder.parse("config.xml");
            Schema schema = schemaFactory.newSchema(new StreamSource("./config.xsd"));
            schema.newValidator().validate(new DOMSource(document));
        }
        catch (Exception e)
        {
            if (e instanceof SAXException)
                System.err.println("Validation Error: " + e.getMessage());
            else
                System.err.println(e.getMessage());

            return false;
        }
        return true;
    }

    public String getNeo4jIp() {
        return neo4jIp;
    }

    public int getNeo4jPort() {
        return neo4jPort;
    }

    public String getNeo4jUsername() {
        return neo4jUsername;
    }

    public String getNeo4jPassword() {
        return neo4jPassword;
    }

    public String getFeatureExtractorServerIp() {
        return featureExtractorServerIp;
    }

    public int getFeatureExtractorServerPort() {
        return featureExtractorServerPort;
    }
}
