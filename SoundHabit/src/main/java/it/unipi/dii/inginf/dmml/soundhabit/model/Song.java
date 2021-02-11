package it.unipi.dii.inginf.dmml.soundhabit.model;

public class Song {
    private String name;
    private int duration; // seconds
    private Genre genre;
    private String songLink;
    private Author author;
    private String imageLink;

    public Song(String name, int duration, Genre genre, String songLink, Author author, String imageLink) {
        this.name = name;
        this.duration = duration;
        this.genre = genre;
        this.songLink = songLink;
        this.author = author;
        this.imageLink = imageLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public String getSongLink() {
        return songLink;
    }

    public void setSongLink(String songLink) {
        this.songLink = songLink;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}
