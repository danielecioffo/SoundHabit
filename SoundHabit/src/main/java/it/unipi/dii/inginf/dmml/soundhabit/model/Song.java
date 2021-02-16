package it.unipi.dii.inginf.dmml.soundhabit.model;

import java.util.List;

public class Song {
    private String name;
    private List<Genre> genre;
    private String songLink;
    private String author;
    private String imageLink;

    public Song(String name, List<Genre> genre, String songLink, String author, String imageLink) {
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

    public List<Genre> getGenre() {
        return genre;
    }

    public String getGenresString() {
        StringBuilder genres = new StringBuilder();
        for(int i = 0; i<genre.size(); i++) {
            Genre g = genre.get(i);
            genres.append(" ").append(g.toProperCase());
            if(i != genre.size() -1) {
                genres.append(";");
            }
        }
        return String.valueOf(genres);
    }

    public void setGenre(List<Genre> genre) {
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

    @Override
    public String toString() {
        return "Song{" +
                "name='" + name + '\'' +
                ", genre=" + genre +
                ", songLink='" + songLink + '\'' +
                ", author='" + author + '\'' +
                ", imageLink='" + imageLink + '\'' +
                '}';
    }
}
