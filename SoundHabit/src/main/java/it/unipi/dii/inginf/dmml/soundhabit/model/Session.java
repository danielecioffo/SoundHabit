package it.unipi.dii.inginf.dmml.soundhabit.model;

public class Session {
    private static Session instance = null; // Singleton
    private User loggedUser;

    public static Session getInstance()
    {
        if(instance == null) {
            synchronized (Session.class) {
                instance = new Session();
            }
        }
        return instance;
    }

    private Session() {}

    public static void setInstance(Session instance) {
        Session.instance = instance;
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }
}
