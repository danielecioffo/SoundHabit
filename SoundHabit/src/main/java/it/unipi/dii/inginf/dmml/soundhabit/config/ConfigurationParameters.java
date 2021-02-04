package it.unipi.dii.inginf.dmml.soundhabit.config;

/**
 * Class used to store the configuration parameters retrieved from the config.xml
 * There is no need to modify this value, so there are only the getters methods
 */
public class ConfigurationParameters {
    private String neo4jIp;
    private int neo4jPort;
    private String neo4jUsername;
    private String neo4jPassword;

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
}
