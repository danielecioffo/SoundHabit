package it.unipi.dii.inginf.dmml.soundhabit.persistence;

import it.unipi.dii.inginf.dmml.soundhabit.config.ConfigurationParameters;
import it.unipi.dii.inginf.dmml.soundhabit.model.Genre;
import it.unipi.dii.inginf.dmml.soundhabit.model.Song;
import it.unipi.dii.inginf.dmml.soundhabit.model.User;
import org.neo4j.driver.*;

import java.util.*;
import java.util.stream.Collectors;

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
        if(instance == null)
        {
            synchronized (Neo4jDriver.class)
            {
                if(instance==null)
                {
                    instance = new Neo4jDriver(ConfigurationParameters.getInstance());
                }
            }
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
    public boolean addSong (final Song song)
    {
        try ( Session session = driver.session())
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "CREATE (s:Song {name: $name, songLink: $songLink, author: $author, imageLink: $imageLink}) " +
                                "SET s:" + song.getGenresString(":"),
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

    /**
     * Function used to check if this song is liked by the user
     * @param song      Song to check
     * @param user      User to consider
     * @return          True if there exists the likes relation, false otherwise
     */
    public Boolean isThisSongLikedByUser (final User user, final Song song)
    {
        Boolean relation = false;
        try(Session session = driver.session())
        {
            relation = session.readTransaction((TransactionWork<Boolean>) tx -> {
                Result r = tx.run("MATCH (:User {username: $username})" +
                                    "-[:LIKES]->" +
                                    "(:Song {name: $name, author: $author}) " +
                                    "RETURN COUNT(*)",
                        parameters("username", user.getUsername(),"name", song.getName(),
                                "author", song.getAuthor()));
                Record rec = r.next();
                if(rec.get(0).asInt()==0)
                    return false;
                else
                    return true;
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return relation;
    }

    /**
     * It creates the relation user-[:LIKES]->song
     * @param user      The User from which starts the relation
     * @param song      The song that will be reached
     */
    public void like (final User user, final Song song)
    {
        try(Session session = driver.session())
        {
            session.writeTransaction((TransactionWork<Integer>) tx -> {
                tx.run("MATCH (u:User {username: $username}) " +
                        "MATCH (s:Song {name: $name, author: $author}) " +
                        "MERGE (u)-[:LIKES]->(s)",
                        parameters("username",user.getUsername(),"name",song.getName(),
                                "author", song.getAuthor()));
                return null;
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * It deletes the relation user-[:LIKES]->song
     * @param user          The User
     * @param song          The Song
     */
    public void unlike (final User user, final Song song)
    {
        try(Session session = driver.session())
        {
            session.writeTransaction((TransactionWork<Integer>) tx -> {
                tx.run("MATCH (u:User {username:$username})-[l:LIKES]->(s:Song {name: $name, author: $author}) " +
                        "DELETE l",
                        parameters("username", user.getUsername(),"name", song.getName(),
                                "author", song.getAuthor()));
                return null;
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Function that returns the songs of a specific genre
     * @param genre             Genre to consider
     * @param howManySkip       How many songs to skip
     * @param howMany           How many song to obtain
     * @return                  List of songs
     */
    public List<Song> getSongsOfGenre (final Genre genre, final int howManySkip, final int howMany)
    {
        List<Song> songs = new ArrayList<>();
        try(Session session = driver.session()) {
            session.readTransaction(tx -> {
                Result result = tx.run("MATCH (s:" + genre.toProperCase() + ")" +
                                "RETURN s.name AS name, s.songLink AS songLink, s.author AS author, " +
                                "s.imageLink AS imageLink, LABELS(s) AS labels " +
                                "SKIP $skip LIMIT $limit",
                        parameters("skip", howManySkip, "limit", howMany));

                while(result.hasNext()){
                    Record r = result.next();
                    String name = r.get("name").asString();
                    String songLink = r.get("songLink").asString();
                    String author = r.get("author").asString();
                    String imageLink = r.get("imageLink").asString();
                    List<Genre> genres = getGenresFromListOfLabels(r.get("labels").asList());
                    Song song = new Song(name, genres, songLink, author, imageLink);
                    songs.add(song);
                }
                return null;
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return songs;
    }

    /**
     * Function that search the songs based on a portion of the name
     * @param partialName       Portion of the name of the song
     * @param howManySkip       How many songs skip
     * @param howMany           How many songs obtain
     * @return                  List of songs
     */
    public List<Song> searchByName (final String partialName, final int howManySkip, final int howMany)
    {
        List<Song> songs = new ArrayList<>();
        try(Session session = driver.session()) {
            session.readTransaction(tx -> {
                Result result = tx.run("MATCH (s:Song) " +
                                "WHERE toLower(s.name) CONTAINS toLower($name) " +
                                "RETURN s.name AS name, s.songLink AS songLink, s.author AS author, " +
                                "s.imageLink AS imageLink, LABELS(s) AS labels " +
                                "SKIP $skip LIMIT $limit",
                        parameters("name", partialName, "skip", howManySkip, "limit", howMany));

                while (result.hasNext()) {
                    Record r = result.next();
                    List<Genre> genres = getGenresFromListOfLabels(r.get("labels").asList());
                    String name = r.get("name").asString();
                    String songLink = r.get("songLink").asString();
                    String author = r.get("author").asString();
                    String imageLink = r.get("imageLink").asString();
                    Song song = new Song(name, genres, songLink, author, imageLink);
                    songs.add(song);
                }
                return null;
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return songs;
    }

    /**
     * Function that search the songs based on a portion of the author name
     * @param partialName       Portion of the name of the author of the song
     * @param howManySkip       How many songs skip
     * @param howMany           How many songs obtain
     * @return                  List of songs
     */
    public List<Song> searchByAuthorName (final String partialName, final int howManySkip, final int howMany)
    {
        List<Song> songs = new ArrayList<>();
        try(Session session = driver.session()) {
            session.readTransaction(tx -> {
                Result result = tx.run("MATCH (s:Song) " +
                                "WHERE toLower(s.author) CONTAINS toLower($author) " +
                                "RETURN s.name AS name, s.songLink AS songLink, s.author AS author, " +
                                "s.imageLink AS imageLink, LABELS(s) AS labels " +
                                "SKIP $skip LIMIT $limit",
                        parameters("author", partialName, "skip", howManySkip, "limit", howMany));

                while (result.hasNext()) {
                    Record r = result.next();
                    String name = r.get("name").asString();
                    String songLink = r.get("songLink").asString();
                    String author = r.get("author").asString();
                    String imageLink = r.get("imageLink").asString();
                    List<Genre> genres = getGenresFromListOfLabels(r.get("labels").asList());
                    Song song = new Song(name, genres, songLink, author, imageLink);
                    songs.add(song);
                }
                return null;
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return songs;
    }

    /**
     * Function that search the songs that the user has liked
     * @param user              User to consider
     * @param howManySkip       How many songs skip
     * @param howMany           How many songs obtain
     * @return                  List of songs
     */
    public List<Song> searchSongsLiked (final User user, final int howManySkip, final int howMany)
    {
        List<Song> songs = new ArrayList<>();
        try(Session session = driver.session()) {
            session.readTransaction(tx -> {
                Result result = tx.run("MATCH (u:User {username: $username})-[:LIKES]->(s:Song) " +
                                "RETURN s.name AS name, s.songLink AS songLink, s.author AS author, " +
                                "s.imageLink AS imageLink, LABELS(s) AS labels " +
                                "SKIP $skip LIMIT $limit",
                        parameters("username", user.getUsername(), "skip", howManySkip, "limit", howMany));

                while (result.hasNext()) {
                    Record r = result.next();
                    String name = r.get("name").asString();
                    String songLink = r.get("songLink").asString();
                    String author = r.get("author").asString();
                    String imageLink = r.get("imageLink").asString();
                    List<Genre> genres = getGenresFromListOfLabels(r.get("labels").asList());
                    Song song = new Song(name, genres, songLink, author, imageLink);
                    songs.add(song);
                }
                return null;
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return songs;
    }

    /**
     * Function that search the songs the most liked songs
     * @param howManySkip       How many songs skip
     * @param howMany           How many songs obtain
     * @return                  List of songs
     */
    public List<Song> getMostLikedSongs (final int howManySkip, final int howMany)
    {
        List<Song> songs = new ArrayList<>();
        try(Session session = driver.session()) {
            session.readTransaction(tx -> {
                Result result = tx.run("MATCH (:User)-[l:LIKES]->(s:Song) " +
                                "RETURN s.name AS name, s.songLink AS songLink, s.author AS author, " +
                                "s.imageLink AS imageLink, LABELS(s) AS labels, " +
                                "COUNT(DISTINCT l) AS totLikes " +
                                "ORDER BY totLikes DESC " +
                                "SKIP $skip LIMIT $limit",
                        parameters("skip", howManySkip, "limit", howMany));

                while (result.hasNext()) {
                    Record r = result.next();
                    String name = r.get("name").asString();
                    String songLink = r.get("songLink").asString();
                    String author = r.get("author").asString();
                    String imageLink = r.get("imageLink").asString();
                    List<Genre> genres = getGenresFromListOfLabels(r.get("labels").asList());
                    Song song = new Song(name, genres, songLink, author, imageLink);
                    songs.add(song);
                }
                return null;
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return songs;
    }

    /**
     * Function that returns the list of songs suggested to the user
     * The songs of the most liked genre will be shown
     * @param user              User to consider
     * @param howManySkip       How many songs to skip
     * @param howMany           How many song to obtain
     * @return                  List of songs
     */
    public List<Song> getSuggestedSongsConsideringGenre (final User user, final int howManySkip, final int howMany)
    {
        List<Song> songs = new ArrayList<>();
        try(Session session = driver.session()) {
            songs = session.readTransaction((TransactionWork<List<Song>>)tx -> {
                // Statistics count
                Result result = tx.run("MATCH (u:User {username: $username}) " +
                                "OPTIONAL MATCH (u)-[bluesLike:LIKES]->(:Blues) " +
                                "OPTIONAL MATCH (u)-[classicalLike:LIKES]->(:Classical) " +
                                "OPTIONAL MATCH (u)-[jazzLike:LIKES]->(:Jazz) " +
                                "OPTIONAL MATCH (u)-[metalLike:LIKES]->(:Metal) " +
                                "OPTIONAL MATCH (u)-[popLike:LIKES]->(:Pop) " +
                                "OPTIONAL MATCH (u)-[rockLike:LIKES]->(:Rock) " +
                                "RETURN COUNT(DISTINCT bluesLike) AS bluesLikes, " +
                                "COUNT(DISTINCT classicalLike) AS classicalLikes, " +
                                "COUNT(DISTINCT jazzLike) AS jazzLikes, " +
                                "COUNT(DISTINCT metalLike) AS metalLikes, " +
                                "COUNT(DISTINCT popLike) AS popLikes, " +
                                "COUNT(DISTINCT rockLike) AS rockLikes",
                        parameters("username", user.getUsername()));

                List<Song> songList = new ArrayList<>();
                Record r = result.next();
                int bluesLikes = r.get("bluesLikes").asInt();
                int classicalLikes = r.get("classicalLikes").asInt();
                int jazzLikes = r.get("jazzLikes").asInt();
                int metalLikes = r.get("metalLikes").asInt();
                int popLikes = r.get("popLikes").asInt();
                int rockLikes = r.get("rockLikes").asInt();

                int[] counters = {bluesLikes, classicalLikes, jazzLikes, metalLikes, popLikes, rockLikes};
                Arrays.sort(counters); //ascending order
                int max = counters[counters.length-1];

                if (max != 0) // if there is at least one genre liked
                {
                    Genre genre = null;
                    if (bluesLikes == max)
                    {
                        genre = Genre.BLUES;
                    }
                    else if (classicalLikes == max)
                    {
                        genre = Genre.CLASSICAL;
                    }
                    else if (jazzLikes == max)
                    {
                        genre = Genre.JAZZ;
                    }
                    else if (metalLikes == max)
                    {
                        genre = Genre.METAL;
                    }
                    else if (popLikes == max)
                    {
                        genre = Genre.POP;
                    }
                    else if (rockLikes == max)
                    {
                        genre = Genre.ROCK;
                    }
                    songList.addAll(getSongsOfGenre(genre, howManySkip, howMany));
                }

                return songList;
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return songs;
    }

    /**
     * Function that returns the list of songs suggested to the user (considering like)
     * A song is suggested if is liked by one other user that likes at least LIKE_THRESHOLD songs that the user like
     * The idea is that if some other user likes the songs that I likes, probably we have similar taste
     * Only the songs which for now the user don't like will be showed
     * @param user              User to consider
     * @param likeThreshold     Threshold for the number of likes
     * @param howManySkip       How many songs to skip
     * @param howMany           How many song to obtain
     * @return                  List of songs
     */
    public List<Song> getSuggestedSongsConsideringLike (final User user, final int likeThreshold,
                                                        final int howManySkip, final int howMany)
    {
        List<Song> songs = new ArrayList<>();
        try(Session session = driver.session()) {
            session.readTransaction(tx -> {
                Result result = tx.run("MATCH (s:Song)<-[:LIKES]-(:User)-[l:LIKES]->(:Song)<-[:LIKES]-(u:User {username: $username}) " +
                                "WITH COUNT(DISTINCT l) AS like, s " +
                                "WHERE like >= $likeThreshold " +
                                "RETURN s.name AS name, s.songLink AS songLink, s.author AS author, " +
                                "s.imageLink AS imageLink, LABELS(s) AS labels " +
                                "SKIP $skip LIMIT $limit",
                        parameters("username", user.getUsername(),"likeThreshold", likeThreshold,
                                "skip", howManySkip, "limit", howMany));

                while (result.hasNext()) {
                    Record r = result.next();
                    String name = r.get("name").asString();
                    String songLink = r.get("songLink").asString();
                    String author = r.get("author").asString();
                    String imageLink = r.get("imageLink").asString();
                    List<Genre> genres = getGenresFromListOfLabels(r.get("labels").asList());
                    Song song = new Song(name, genres, songLink, author, imageLink);
                    songs.add(song);
                }

                return null;
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return songs;
    }


    /**
     * Function that obtain the genres from the list of labels of the node
     * @param list          List of labels
     * @return              List of genre
     */
    private List<Genre> getGenresFromListOfLabels (List<Object> list)
    {
        List<Genre> genres = new ArrayList<>();
        List<String> labels = list.stream()
                .map(object -> Objects.toString(object, null))
                .collect(Collectors.toList());
        for (String label: labels) {
            switch (label) {
                case "Blues":
                    genres.add(Genre.BLUES);
                    break;
                case "Classical":
                    genres.add(Genre.CLASSICAL);
                    break;
                case "Jazz":
                    genres.add(Genre.JAZZ);
                    break;
                case "Metal":
                    genres.add(Genre.METAL);
                    break;
                case "Pop":
                    genres.add(Genre.POP);
                    break;
                case "Rock":
                    genres.add(Genre.ROCK);
                    break;
            }
        }
        return genres;
    }

    public void populateDatabase() {
        // ROCK
        addSong(new Song("Purple Haze", Collections.singletonList(Genre.ROCK), "https://youtu.be/WGoDaYjdfSg", "Jimi Hendrix", "https://c-sf.smule.com/rs-s24/arr/18/84/e5edb226-59d0-4d4f-858f-6fce20a7b326.jpg"));
        addSong(new Song("Under Pressure", Collections.singletonList(Genre.ROCK), "https://youtu.be/a01QQZyl-_I", "Queen", "https://e-cdns-images.dzcdn.net/images/cover/e16455433a84c7e19025403ae3eec52d/350x350.jpg"));
        addSong(new Song("Start Me Up", Collections.singletonList(Genre.ROCK), "https://youtu.be/SGyOaCXr8Lw", "The Rolling Stones", "https://images-na.ssl-images-amazon.com/images/I/91Bl4KIUFUL._AC_SL1500_.jpg"));
        addSong(new Song("Proud Mary", Collections.singletonList(Genre.ROCK), "https://youtu.be/5hid10EgMXE", "Creedence Clearwater Revival", "https://upload.wikimedia.org/wikipedia/en/thumb/3/35/CCR_-_Proud_Mary.png/220px-CCR_-_Proud_Mary.png"));
        addSong(new Song("Rockin’ in the Free World", Collections.singletonList(Genre.ROCK), "https://youtu.be/TnAgc1kgvLc", "Neil Young", "https://upload.wikimedia.org/wikipedia/en/thumb/5/58/Rockin_in_the_Free_World_single_cover.jpg/220px-Rockin_in_the_Free_World_single_cover.jpg"));
        addSong(new Song("Whole Lotta Love", Collections.singletonList(Genre.ROCK), "https://youtu.be/HQmmM_qwG4k", "Led Zeppelin", "https://www.debaser.it/resize.aspx?path=%2Ffiles%2F2016%2F44395.jpg"));
        addSong(new Song("Should I Stay or Should I Go", Collections.singletonList(Genre.ROCK), "https://youtu.be/BN1WwnEDWAM", "The Clash","https://static.wikia.nocookie.net/strangerthings8338/images/6/68/Combat_rock.jpg/revision/latest?cb=20170617174328"));
        addSong(new Song("Walk This Way", Collections.singletonList(Genre.ROCK), "https://youtu.be/4c8O2n1Gfto", "Aerosmith", "https://img.discogs.com/M7eH7TjqnLX8R-CIT-qZIY0HYAk=/fit-in/600x600/filters:strip_icc():format(jpeg):mode_rgb():quality(90)/discogs-images/R-2789979-1443013763-6843.jpeg.jpg"));
        addSong(new Song("Baba O’Riley", Collections.singletonList(Genre.ROCK), "https://youtu.be/gY5rztWa1TM", "The Who", "https://upload.wikimedia.org/wikipedia/en/5/5a/The_Who_-_Baba_cover.jpg"));
        addSong(new Song("Comfortably Numb", Collections.singletonList(Genre.ROCK), "https://youtu.be/x-xTttimcNk", "Pink Floyd", "https://i.ytimg.com/vi/_FrOQC-zEog/hqdefault.jpg"));

        // METAL
        addSong(new Song("Paranoid", Collections.singletonList(Genre.METAL), "https://youtu.be/0qanF-91aJo", "Black Sabbath", "https://images-na.ssl-images-amazon.com/images/I/71vSn%2BlK4WL._AC_SL1425_.jpg"));
        addSong(new Song("Master of Puppets", Collections.singletonList(Genre.METAL), "https://youtu.be/u6LahTuw02c", "Metallica", "https://images-na.ssl-images-amazon.com/images/I/81hryXAVZjL._AC_SX522_.jpg"));
        addSong(new Song("Ace of Spades", Collections.singletonList(Genre.METAL), "https://youtu.be/3mbvWn1EY6g", "Motorhead", "https://i.ebayimg.com/images/g/DUUAAOSweD5ZsRsO/s-l300.jpg"));
        addSong(new Song("Crazy Train", Collections.singletonList(Genre.METAL), "https://youtu.be/tMDFv5m18Pw", "Ozzy Osbourne", "https://img.discogs.com/8bUU93zXGsURUsyY-j4ZzEECB_4=/fit-in/300x300/filters:strip_icc():format(jpeg):mode_rgb():quality(40)/discogs-images/R-9746157-1485711017-2680.jpeg.jpg"));
        addSong(new Song("Welcome To The Jungle", Collections.singletonList(Genre.METAL), "https://youtu.be/o1tj2zJ2Wvg", "Guns N' Roses", "https://upload.wikimedia.org/wikipedia/en/d/d6/Welcometothejungle.jpg"));
        addSong(new Song("The Number of the Beast", Collections.singletonList(Genre.METAL), "https://youtu.be/WxnN05vOuSM", "Iron Maiden", "https://img.discogs.com/bDcM6fLNCYutvHmREnGWpWw_4To=/fit-in/600x600/filters:strip_icc():format(jpeg):mode_rgb():quality(90)/discogs-images/R-6191421-1428850748-1506.jpeg.jpg"));
        addSong(new Song("Hallowed Be Thy Name", Collections.singletonList(Genre.METAL), "https://youtu.be/HAQQUDbuudY", "Iron Maiden", "https://m.media-amazon.com/images/I/81wajmBj+6L._SS500_.jpg"));
        addSong(new Song("War Pigs", Collections.singletonList(Genre.METAL), "https://youtu.be/iL23hA-RBz4", "Black Sabbath","https://www.debaser.it/files/2011%2F36069.jpeg"));
        addSong(new Song("Back in Black", Collections.singletonList(Genre.METAL), "https://youtu.be/pAgnJDJN4VA", "AC/DC", "https://upload.wikimedia.org/wikipedia/commons/b/be/Acdc_backinblack_cover.jpg"));
        addSong(new Song("Iron Man", Collections.singletonList(Genre.METAL), "https://youtu.be/5s7_WbiR79E", "Black Sabbath", "https://i.pinimg.com/originals/b0/cd/1b/b0cd1ba25db2030343bad6a0598c1ac6.jpg"));

        // POP
        addSong(new Song("Like a Prayer", Collections.singletonList(Genre.POP), "https://youtu.be/79fzeNUqQbQ", "Madonna", "https://images-na.ssl-images-amazon.com/images/I/71WJtJ0Y3aL._AC_SX425_.jpg"));
        addSong(new Song("When Doves Cry", Collections.singletonList(Genre.POP), "https://youtu.be/UG3VcCAlUgE", "Prince", "https://i1.sndcdn.com/artworks-000430829199-eh8fy7-t500x500.jpg"));
        addSong(new Song("I Wanna Dance With Somebody", Collections.singletonList(Genre.POP), "https://youtu.be/eH3giaIzONA", "Whitney Houston", "https://m.media-amazon.com/images/I/71LuMhNwMBL._SS500_.jpg"));
        addSong(new Song("Baby One More Time", Collections.singletonList(Genre.POP), "https://youtu.be/C-u5WLJ9Yk4", "Britney Spears", "https://images.genius.com/661f50edcfefa2be10498a3b064dfada.1000x1000x1.jpg"));
        addSong(new Song("It's Gonna Be Me", Collections.singletonList(Genre.POP), "https://youtu.be/GQMlWwIXg3M", "'N Sync", "https://upload.wikimedia.org/wikipedia/en/f/f2/ItsGonnaBeMe.jpg"));
        addSong(new Song("Everybody (Backstreet's Back", Collections.singletonList(Genre.POP), "https://youtu.be/6M6samPEMpM", "Backstreet Boys", "https://img.discogs.com/Xg16mnU-q539cuXWMcG4amSFFKs=/fit-in/600x525/filters:strip_icc():format(jpeg):mode_rgb():quality(90)/discogs-images/R-553783-1537901504-9791.jpeg.jpg"));
        addSong(new Song("Rolling in the Deep", Collections.singletonList(Genre.POP), "https://youtu.be/rYEDA3JcQqw", "Adele", "https://m.media-amazon.com/images/I/91CzVbxeUVL._SS500_.jpg"));
        addSong(new Song("Don't Stop Believing", Collections.singletonList(Genre.POP), "https://youtu.be/1k8craCGpgs", "Journey", "https://img.discogs.com/Abn91eeWzN38tKbPPcnEmYIlNuY=/fit-in/300x300/filters:strip_icc():format(jpeg):mode_rgb():quality(40)/discogs-images/R-419741-1454675353-7372.jpeg.jpg"));
        addSong(new Song("Genie In a Bottle", Collections.singletonList(Genre.POP), "https://youtu.be/kIDWgqDBNXA", "Christina Aguilera", "https://upload.wikimedia.org/wikipedia/en/thumb/4/42/Genie_in_a_Bottle.png/220px-Genie_in_a_Bottle.png"));
        addSong(new Song("Shake It Off", Collections.singletonList(Genre.POP), "https://youtu.be/nfWlot6h_JM", "Taylor Swift", "https://hooksandharmony.com/wp-content/uploads/2014/12/Taylor-Swift-Shake-It-Off-e1534088206551.jpg"));

        // BLUES
        addSong(new Song("I Can't Quit You Baby", Collections.singletonList(Genre.BLUES), "https://youtu.be/Uy2tEP3I3DM", "Otis Rush", "https://img.discogs.com/UdK54nheZ7ZNllIpXUm7pEXpMwo=/fit-in/600x600/filters:strip_icc():format(jpeg):mode_rgb():quality(90)/discogs-images/R-11501352-1517468434-9815.jpeg.jpg"));
        addSong(new Song("I'd Rather Go Blind", Collections.singletonList(Genre.BLUES), "https://youtu.be/u9sq3ME0JHQ", "Etta James", "https://img.discogs.com/5iXl17TSCYxw37aux2POT95r2lM=/fit-in/300x300/filters:strip_icc():format(jpeg):mode_rgb():quality(40)/discogs-images/R-5759576-1401882130-9427.jpeg.jpg"));
        addSong(new Song("Crossroad Blues", Collections.singletonList(Genre.BLUES), "https://youtu.be/Yd60nI4sa9A", "Robert Johnson", "https://i.ytimg.com/vi/GtDlZdhHRCI/hqdefault.jpg"));
        addSong(new Song("Pride and Joy", Collections.singletonList(Genre.BLUES), "https://youtu.be/0vo23H9J8o8", "Stevie Ray Vaughan", "https://img.discogs.com/9YfJ71TFWGA6RcfhqHeku3zZgA4=/fit-in/600x578/filters:strip_icc():format(jpeg):mode_rgb():quality(90)/discogs-images/R-5566914-1422280383-3583.jpeg.jpg"));
        addSong(new Song("I'm Tore Down", Collections.singletonList(Genre.BLUES), "https://youtu.be/YB52eLfirFA", "Freddie King", "https://images-na.ssl-images-amazon.com/images/I/51zPOmBtU5L._AC_SY355_.jpg"));
        addSong(new Song("Born Under a Bad Sign", Collections.singletonList(Genre.BLUES), "https://youtu.be/2Py37G9qsfY", "Albert King", "https://images-na.ssl-images-amazon.com/images/I/71wrBTBEVgL._AC_SL1400_.jpg"));
        addSong(new Song("Sunshine of your Love", Collections.singletonList(Genre.BLUES), "https://youtu.be/y_u1eu6Lpds", "Cream", "http://static1.squarespace.com/static/572a4d599f7266c0b7e6632c/59e67c6101002744ef9cbd42/59e67f9fb078695e0a004eea/1508371483289/03330327e03d85efcd0f90d54b6d7851.998x1000x1.jpg?format=1500w"));
        addSong(new Song("Hoochie Coochie Man", Collections.singletonList(Genre.BLUES), "https://youtu.be/U5QKpsVzndc", "Muddy Waters", "https://images-na.ssl-images-amazon.com/images/I/71Eliheb7bL._AC_SY355_.jpg"));
        addSong(new Song("Red House", Collections.singletonList(Genre.BLUES), "https://youtu.be/G2SlSokLN4c", "Jimi Hendrix", "https://images-na.ssl-images-amazon.com/images/I/51kuUityivL._AC_SY355_.jpg"));
        addSong(new Song("The Thrill is Gone", Collections.singletonList(Genre.BLUES), "https://youtu.be/kpC69qIe02E", "B.B. King", "https://m.media-amazon.com/images/I/61u5jrBN1jL._SS500_.jpg"));

        // JAZZ
        addSong(new Song("So What", Collections.singletonList(Genre.JAZZ), "https://youtu.be/ylXk1LBvIqU", "Miles Davis", "https://images-na.ssl-images-amazon.com/images/I/713VRATpUtL._AC_SL1227_.jpg"));
        addSong(new Song("Fly Me To The Moon", Collections.singletonList(Genre.JAZZ), "https://youtu.be/ZEcqHA7dbwM", "Frank Sinatra", "https://static.qobuz.com/images/covers/9b/u4/qe9o9pdvuu49b_600.jpg"));
        addSong(new Song("Mood Indigo", Collections.singletonList(Genre.JAZZ), "https://youtu.be/bZyVBVFnrm4", "Duke Ellington", "https://img.discogs.com/uxpumP8pFHjVSbQXEwPiWMTrcmY=/fit-in/300x300/filters:strip_icc():format(jpeg):mode_rgb():quality(40)/discogs-images/R-6347836-1417031363-2628.jpeg.jpg"));
        addSong(new Song("Take Five", Collections.singletonList(Genre.JAZZ), "https://youtu.be/vmDDOFXSgAs", "Dave Brubeck Quartet", "https://images-na.ssl-images-amazon.com/images/I/71lsG3fCaOL._SL1200_.jpg"));
        addSong(new Song("The Girl From Ipanema", Collections.singletonList(Genre.JAZZ), "https://youtu.be/j8VPmtyLqSY", "Stan Getz", "https://img.discogs.com/Ds_Q2CL5v0s585K0jjIb3qMTIaM=/fit-in/300x300/filters:strip_icc():format(jpeg):mode_rgb():quality(40)/discogs-images/R-7575968-1444386356-9111.jpeg.jpg"));
        addSong(new Song("Minnie the Moocher", Collections.singletonList(Genre.JAZZ), "https://youtu.be/zZ5gCGJorKk", "Cab Calloway", "https://images-na.ssl-images-amazon.com/images/I/41ZTZSVYF6L._AC_.jpg"));
        addSong(new Song("What a Wonderful World", Collections.singletonList(Genre.JAZZ), "https://youtu.be/CWzrABouyeE", "Louis Armstrong", "https://images-na.ssl-images-amazon.com/images/I/71oBdOk%2B2BL._AC_SL1099_.jpg"));
        addSong(new Song("Strange Fruit", Collections.singletonList(Genre.JAZZ), "https://youtu.be/Web007rzSOI", "Billie Holiday", "https://images-na.ssl-images-amazon.com/images/I/71SsPaS6-9L._AC_SY355_.jpg"));
        addSong(new Song("Georgia on my Mind", Collections.singletonList(Genre.JAZZ), "https://youtu.be/QL3EZwSJAh0", "Ray Charles", "https://images-na.ssl-images-amazon.com/images/I/71N2xzDmgPL._AC_SL1083_.jpg"));
        addSong(new Song("My Baby Just Cares for Me", Collections.singletonList(Genre.JAZZ), "https://youtu.be/3ZS7iKdRo5Q", "Nina Simone", "https://images-na.ssl-images-amazon.com/images/I/81X6wONNRtL._AC_SL1500_.jpg"));

        // CLASSICAL
        addSong(new Song("Eine kleine Nachtmusik", Collections.singletonList(Genre.CLASSICAL), "https://youtu.be/oy2zDJPIgwc", "Mozart", "https://www.slowfood.it/wp-content/uploads/2016/08/mozart-480x360.jpeg"));
        addSong(new Song("Für Elise", Collections.singletonList(Genre.CLASSICAL), "https://youtu.be/_mVW8tgGY_w", "Beethoven", "https://upload.wikimedia.org/wikipedia/commons/c/c0/Beethovensmall.jpg"));
        addSong(new Song("O mio babbino caro", Collections.singletonList(Genre.CLASSICAL), "https://youtu.be/0Wlcr2bIKmk", "Puccini", "https://i2.wp.com/www.gbopera.it/wp-content/uploads/2019/02/Acculturarsi_-Michele-Placido-d%C3%A0-voce-a-Puccini-tra-parole-e-mus_.jpg?fit=658%2C718&ssl=1"));
        addSong(new Song("Toccata and Fugue in D Minor", Collections.singletonList(Genre.CLASSICAL), "https://youtu.be/Nnuq9PXbywA", "J.S. Bach", "https://upload.wikimedia.org/wikipedia/commons/6/6a/Johann_Sebastian_Bach.jpg"));
        addSong(new Song("Symphony No.5 in C minor", Collections.singletonList(Genre.CLASSICAL), "https://youtu.be/fOk8Tm815lE", "Beethoven", "https://upload.wikimedia.org/wikipedia/commons/c/c0/Beethovensmall.jpg"));
        addSong(new Song("The Four Seasons", Collections.singletonList(Genre.CLASSICAL), "https://youtu.be/GRxofEmo3HA", "Vivaldi", "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bd/Vivaldi.jpg/220px-Vivaldi.jpg"));
        addSong(new Song("Carmen", Collections.singletonList(Genre.CLASSICAL), "https://youtu.be/pmuFOuh3QHs", "Bizet", "https://upload.wikimedia.org/wikipedia/commons/9/96/Georges_bizet.jpg"));
        addSong(new Song("The Blue Danube", Collections.singletonList(Genre.CLASSICAL), "https://youtu.be/_CTYymbbEL4", "Johann Strauss II", "https://pociopocio.altervista.org/wp-content/uploads/2021/01/STRAUSS-II.2.jpg"));
        addSong(new Song("Boléro", Collections.singletonList(Genre.CLASSICAL), "https://youtu.be/r30D3SW4OVw", "Ravel", "https://biografieonline.it/img/bio/m/Maurice_Ravel.jpg"));
        addSong(new Song("Flower Duet", Collections.singletonList(Genre.CLASSICAL), "https://youtu.be/8Qx2lMaMsl8", "Delibes", "https://upload.wikimedia.org/wikipedia/commons/e/e2/L%C3%A9o_Delibes.jpg"));
    }
}
