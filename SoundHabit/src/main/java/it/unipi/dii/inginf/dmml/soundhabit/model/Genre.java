package it.unipi.dii.inginf.dmml.soundhabit.model;

public enum Genre {
    BLUES,
    CLASSICAL,
    JAZZ,
    METAL,
    POP,
    ROCK;

    public String toProperCase() {
        String s = this.name();
        return s.substring(0, 1).toUpperCase() +
                s.substring(1).toLowerCase();
    }
}
