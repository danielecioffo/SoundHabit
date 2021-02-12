package it.unipi.dii.inginf.dmml.soundhabit.classification;

import weka.classifiers.lazy.IBk;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

import java.io.File;
import java.io.IOException;

public class Classifier
{
    private Instances dataset;
    private IBk ibk;
    private final int K = 5;

    public Classifier()
    {
        try
        {
            dataset = loadDataset("./data.csv");
            dataset.setClassIndex(dataset.numAttributes()-1);
            ibk = new IBk(K);
            // By default, LinearNNSearch, Euclidean Distance
            ibk.buildClassifier(dataset);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.err.println("Error in opening the dataset");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Function that load the instances of the dataset
     * @param path  Path of the dataset
     * @return      The dataset (instances)
     * @throws IOException
     */
    private Instances loadDataset (String path) throws IOException {
        CSVLoader loader = new CSVLoader();
        loader.setSource(new File(path));
        return loader.getDataSet();
    }
}
