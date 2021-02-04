package it.unipi.dii.inginf.dmml.soundhabit.persistence;

import it.unipi.dii.inginf.dmml.soundhabit.config.ConfigurationParameters;
import it.unipi.dii.inginf.dmml.soundhabit.utils.Utils;
import org.neo4j.driver.*;

/**
 * This class is used to communicate with Neo4j
 */
public class Neo4jDriver {
    private static Neo4jDriver instance = null; // Singleton Instance

    private Driver driver;
    private String ip;
    private int port;
    private String username;
    private String password;

    private Neo4jDriver(ConfigurationParameters configurationParameters) {
        this.ip = configurationParameters.getNeo4jIp();
        this.port = configurationParameters.getNeo4jPort();
        this.username = configurationParameters.getNeo4jUsername();
        this.password = configurationParameters.getNeo4jPassword();
    }

    public static Neo4jDriver getInstance() {
        if (instance == null) {
            instance = new Neo4jDriver(Utils.readConfigurationParameters());
        }
        return instance;
    }

    /**
     * Method that inits the Driver
     */
    public boolean initConnection() {
        try {
            driver = GraphDatabase.driver("neo4j://" + ip + ":" + port, AuthTokens.basic(username, password));
            driver.verifyConnectivity();
        } catch (Exception e) {
            System.out.println("Neo4J is not available");
            return false;
        }
        return true;
    }

    /**
     * Method for closing the connection of the Driver
     */
    public void closeConnection() {
        if (driver != null)
            driver.close();
    }
}
