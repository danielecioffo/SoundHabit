package it.unipi.dii.inginf.dmml.soundhabit.persistence;

import it.unipi.dii.inginf.dmml.soundhabit.config.ConfigurationParameters;
import it.unipi.dii.inginf.dmml.soundhabit.model.Song;
import it.unipi.dii.inginf.dmml.soundhabit.model.User;
import it.unipi.dii.inginf.dmml.soundhabit.utils.Utils;
import org.neo4j.driver.*;
import org.neo4j.driver.exceptions.ClientException;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.neo4j.driver.Values.NULL;
import static org.neo4j.driver.Values.parameters;

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
     * Function that init the connection
     * @return  True if it is possible to communicate with the database, false otherwise
     */
    public boolean initConnection() {
        try {
            driver = GraphDatabase.driver("neo4j://" + ip + ":" + port, AuthTokens.basic(username, password));
            driver.verifyConnectivity();
        } catch (Exception e) {
            System.err.println("Neo4J is not available");
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

    /**
     * Method that creates a new node in the graphDB with the information of the new user
     * @param firstName     first name of the new user
     * @param lastName      last name of the new user
     * @param username      username of the new user
     * @param password      password of the new user
     */
    public void addUser( final String firstName, final String lastName, final String username,
                         final String password) throws  org.neo4j.driver.exceptions.ClientException
    {
        try ( Session session = driver.session())
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "CREATE (u:User {firstName: $firstName, lastName: $lastName, username: $username," +
                                "password: $password})",
                        parameters( "firstName", firstName, "lastName", lastName, "username",
                                username, "password", password ) );
                return null;
            });
        }
    }

    /**
     * It performs the login with the given username and password
     * @param username  Username of the target user
     * @param password  Password of the target user
     * @return The object user if the login is done successfully, otherwise null
     */
    public User login(final String username, final String password)
    {
        User u = null;
        try ( Session session = driver.session())
        {
            u = session.readTransaction((TransactionWork<User>) tx -> {
                Result result = tx.run( "MATCH (u:User) " +
                                "WHERE u.username = $username " +
                                "AND u.password = $password " +
                                "RETURN u.firstName AS firstName, u.lastName AS lastName, " +
                                "u.username AS username, u.password AS password, " +
                                "CASE WHEN 'Administrator' IN LABELS(u) THEN true ELSE false END AS isAdmin" ,
                        parameters( "username", username,"password",password) );
                User user = null;
                try
                {
                    Record r = result.next();
                    String firstName = r.get("firstName").asString();
                    String lastName = r.get("lastName").asString();
                    boolean isAdmin = r.get("isAdmin").asBoolean();
                    user = new User(firstName, lastName, username, password, isAdmin);
                }
                catch (NoSuchElementException ex)
                {
                    user = null;
                }
                return user;
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return u;
    }

    /**
     * Function used to add a new song
     * @param song  Song that has to be added
     * @return      True if all go well, false otherwise
     */
    public boolean addSong (Song song)
    {
        try ( Session session = driver.session())
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "CREATE (s:Song {name: $name, songLink: $songLink, author: $author, imageLink: $imageLink}) " +
                                "SET s:" + song.getGenre().toProperCase(),
                        parameters( "name", song.getName(), "songLink", song.getSongLink(),
                                "author", song.getAuthor(), "imageLink", song.getImageLink()));
                return null;
            });
            return true;
        }
        catch (Exception ex)
        {
            System.err.println("Error in adding a new song in Neo4J");
            return false;
        }
    }
}
