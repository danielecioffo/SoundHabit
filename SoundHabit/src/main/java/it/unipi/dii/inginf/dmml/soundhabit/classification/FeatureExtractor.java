package it.unipi.dii.inginf.dmml.soundhabit.classification;

import it.unipi.dii.inginf.dmml.soundhabit.model.Song;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FeatureExtractor {
    private final String ANACONDA_PROMPT_DESTINATION = "C:/Users/feder/anaconda3/Scripts/activate.bat C:/Users/feder/anaconda3";
    private final String PATH_TO_PYTHON_CODE = "C:/Users/feder/IdeaProjects/SoundHabit/FeatureExtractor/main.py";
    private SongFeature song;

    public FeatureExtractor () {
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/C",  ANACONDA_PROMPT_DESTINATION + " && python " + PATH_TO_PYTHON_CODE);
            Process p = pb.start();

            BufferedReader bfr = new BufferedReader(new InputStreamReader(p.getInputStream()));

            System.out.println(".........start   process.........");
            String line;
            while ((line = bfr.readLine()) != null) {
                System.out.println(line); //TODO rimuovi stampe per debug
                song = createSongInstance(line);
            }

            System.out.println("........end   process.......");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SongFeature createSongInstance(String features) {
        String[] splitStr = features.split("\\s+");
        double chromaStft = Double.parseDouble(splitStr[0]);
        double rms = Double.parseDouble(splitStr[1]);
        double spectralCentroid = Double.parseDouble(splitStr[2]);
        double spectralBandwidth = Double.parseDouble(splitStr[3]);
        double spectralRolloff = Double.parseDouble(splitStr[4]);
        double zeroCrossingRate = Double.parseDouble(splitStr[5]);

        List<Double> mfcc = new ArrayList<>();
        for(int i = 6; i<26; i++) {
            mfcc.add(Double.parseDouble(splitStr[i]));
        }

        return new SongFeature(chromaStft, rms, spectralCentroid, spectralBandwidth, spectralRolloff, zeroCrossingRate, mfcc);
    }

    public SongFeature getSong() {
        return song;
    }
}
