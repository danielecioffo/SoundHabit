package it.unipi.dii.inginf.dmml.soundhabit.classification;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FeatureExtractor {
    private DataOutputStream dataOutputStream;
    private BufferedReader bufferedReader;
    private final int FILE_PACKET_SIZE = 4 * 1024;
    private final String FEATURE_EXTRACTOR_SERVER_IP = "localhost";
    private final int FEATURE_EXTRACTOR_SERVER_PORT = 5000;

    public FeatureExtractor () {
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
     * Function that sends a file to the server, dividing it into packet
     * @param path          Path of the file to send
     * @throws Exception
     */
    private void sendFile(String path) throws Exception{
        int bytes = 0;
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);

        // Send the commands
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
}
