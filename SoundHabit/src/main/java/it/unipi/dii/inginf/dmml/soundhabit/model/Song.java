package it.unipi.dii.inginf.dmml.soundhabit.model;

import java.util.List;

public class Song {
    private String name;
    private List<Genre> genreList;
    private String songLink;
    private String author;
    private String imageLink;

    public Song(String name, List<Genre> genreList, String songLink, String author, String imageLink) {
        this.name = name;
        this.genreList = genreList;
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

    public List<Genre> getGenreList() {
        return genreList;
    }

    /**
     * Function that given the delimeter returns a string for the value of the genres list
     * @param delimiter     Delimeter to use
     * @return              The string that contains the genres of the song
     */
    public String getGenresString(String delimiter) {
        StringBuilder genres = new StringBuilder();
        for(int i = 0; i< genreList.size(); i++) {
            Genre g = genreList.get(i);
            genres.append(g.toProperCase());
            if(i != genreList.size() -1) {
                genres.append(delimiter);
            }
        }
        return String.valueOf(genres);
    }

    public void setGenreList(List<Genre> genreList) {
        this.genreList = genreList;
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
                ", genre=" + getGenresString("; ") +
                ", songLink='" + songLink + '\'' +
                ", author='" + author + '\'' +
                ", imageLink='" + imageLink + '\'' +
                '}';
    }
}
