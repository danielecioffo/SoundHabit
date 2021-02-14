package it.unipi.dii.inginf.dmml.soundhabit.classification;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FeatureExtractor {
    //private final String ANACONDA_PROMPT_DESTINATION = "C:/Users/feder/anaconda3/Scripts/activate.bat C:/Users/feder/anaconda3";
    //private final String PATH_TO_PYTHON_CODE = "C:/Users/feder/IdeaProjects/SoundHabit/FeatureExtractor/main.py";
    //private SongFeature song;

    private DataOutputStream dataOutputStream;
    private BufferedReader bufferedReader;
    private final int FILE_PACKET_SIZE = 4 * 1024;
    private final String FEATURE_EXTRACTOR_SERVER_IP = "localhost";
    private final int FEATURE_EXTRACTOR_SERVER_PORT = 5000;

    public FeatureExtractor () {
        /*try {
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/C",  ANACONDA_PROMPT_DESTINATION + " && python " + PATH_TO_PYTHON_CODE);
            Process p = pb.start();

            BufferedReader bfr = new BufferedReader(new InputStreamReader(p.getInputStream()));

            System.out.println(".........start   process.........");
            String line;
            while ((line = bfr.readLine()) != null) {
                System.out.println(line);
                song = createSongInstance(line);
                System.out.println(song.getChromaStft());
            }

            System.out.println("........end   process.......");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * Function that returns a SongFeature given the path of the song (.mav file)
     * @param path      Path to the song
     * @return          SongFeature instance
     */
    public SongFeature getSongFeaturesOfSong(final String path)
    {
        try(Socket socket = new Socket(FEATURE_EXTRACTOR_SERVER_IP,FEATURE_EXTRACTOR_SERVER_PORT)) {
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            sendFile(path);

            List<String> values = Arrays.asList(bufferedReader.readLine().split(" "));
            SongFeature songFeature = new SongFeature();
            songFeature.setChromaStft(Double.parseDouble(values.get(0)));
            songFeature.setRms(Double.parseDouble(values.get(1)));
            songFeature.setSpectralCentroid(Double.parseDouble(values.get(2)));
            songFeature.setSpectralBandwidth(Double.parseDouble(values.get(3)));
            songFeature.setSpectralRolloff(Double.parseDouble(values.get(4)));
            songFeature.setZeroCrossingRate(Double.parseDouble(values.get(5)));
            List<Double> mfcc = new ArrayList<>();
            for (int i=6; i<values.size(); i++)
            {
                mfcc.add(Double.parseDouble(values.get(i)));
            }
            songFeature.setMfcc(mfcc);

            dataOutputStream.close();
            bufferedReader.close();
            return songFeature;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Function that sends a file, dividing it into packet
     * @param path          Path of the file to send
     * @throws Exception
     */
    private void sendFile(String path) throws Exception{
        int bytes = 0;
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);

        dataOutputStream.writeUTF("SendFile");
        dataOutputStream.flush();

        // send file size
        dataOutputStream.writeUTF(String.valueOf(file.length()));
        dataOutputStream.flush();
        // break file into chunks
        byte[] buffer = new byte[FILE_PACKET_SIZE];
        while ((bytes=fileInputStream.read(buffer))!=-1){
            dataOutputStream.write(buffer,0, bytes);
            dataOutputStream.flush();
        }
        fileInputStream.close();
    }

    /*
    private SongFeature createSongInstance(String features) {
        String[] splitStr = features.split("\\s+");
        double chromaStft = Double.parseDouble(splitStr[0]);
        System.out.println(chromaStft);
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
    }*/
}
