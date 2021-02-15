package it.unipi.dii.inginf.dmml.soundhabit.model;

public class Song {
    private String name;
    private Genre genre;
    private String songLink;
    private String author;
    private String imageLink;

    public Song(String name, Genre genre, String songLink, String author, String imageLink) {
        this.name = name;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}
