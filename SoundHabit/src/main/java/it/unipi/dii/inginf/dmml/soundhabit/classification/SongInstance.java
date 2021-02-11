package it.unipi.dii.inginf.dmml.soundhabit.classification;

import it.unipi.dii.inginf.dmml.soundhabit.model.Song;
import weka.core.DenseInstance;
import weka.core.Instance;

public class SongInstance extends DenseInstance {
    private Song song;


    public SongInstance(Instance instance) {
        super(instance);
    }
}
