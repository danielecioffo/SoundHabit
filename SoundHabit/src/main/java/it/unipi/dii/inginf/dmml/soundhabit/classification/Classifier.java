package it.unipi.dii.inginf.dmml.soundhabit.classification;

import weka.attributeSelection.CorrelationAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.lazy.IBk;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

public class Classifier {
    private final String PATH_TO_CLASSIFIER = "./IBk.model";
    private final String PATH_TO_DATASET = "./data.csv";
    private final int K = 5;

    private static volatile Classifier instance;
    private IBk ibk;
    private AttributeSelection filter;

    /**
     * Double-Checked Locking with Singleton
     * @return a Classifier instance
     */
    public static Classifier getInstance() {
        if(instance == null) {
            synchronized (Classifier.class) {
                instance = new Classifier();
            }
        }
        return instance;
    }

    /**
     * Private constructor
     */
    private Classifier() {
        filter = buildAttributeSelectionFilter();
        ibk = buildClassifier();
    }

    /**
     * Function that builds the k-nearest neighbors classifiers
     * @return  the classifier
     */
    private IBk buildClassifier() {
        IBk classifier = null;

        try {   // If the classifier has already been built, we already have it
            classifier = (IBk) SerializationHelper.read(PATH_TO_CLASSIFIER);
            System.out.println("Model is present already. Reading it from file...");
        } catch (Exception e) { // If we can't find the file or something is wrong, we rebuild it
            System.out.println("Model is not present yet. Building it...");
            try {
                Instances instances = loadDataset(PATH_TO_DATASET);
                Instances reduced = selectAttributes(instances);

                classifier = new IBk();
                classifier.setKNN(K);
                classifier.buildClassifier(reduced);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        // The model is saved so that we won't need to rebuild it
        saveModel(classifier, PATH_TO_CLASSIFIER);

        return classifier;
    }

    /**
     * Function that builds the attribute selection filter
     * @return  the filter
     */
    private AttributeSelection buildAttributeSelectionFilter() {
        AttributeSelection filter = new AttributeSelection();

        try {
            Instances instances = loadDataset(PATH_TO_DATASET);
            CorrelationAttributeEval eval = new CorrelationAttributeEval();
            Ranker search = new Ranker();
            search.setThreshold(0.2);
            filter.setEvaluator(eval);
            filter.setSearch(search);
            filter.setInputFormat(instances);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return filter;
    }

    /**
     *
     * @param oldData   instances whose attributes have to be selected
     * @return  new instances with reduced attributes
     * @throws Exception
     */
    private Instances selectAttributes(Instances oldData) throws Exception {
        return Filter.useFilter(oldData, filter);
    }

    /**
     * Function that serializes the given classifier to the specified stream
     * @param classifier    classifier to be saved
     * @param pathToClassifier  where to save it
     */
    private void saveModel(IBk classifier, String pathToClassifier) {
        try {
            SerializationHelper.write(pathToClassifier, classifier);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Function that load the instances of the dataset
     * @param path  Path to the dataset
     * @return  The dataset (instances)
     * @throws Exception
     */
    private Instances loadDataset (String path) throws Exception {
        ConverterUtils.DataSource source = new ConverterUtils.DataSource(path);
        Instances instances = source.getDataSet();
        instances.setClassIndex(instances.numAttributes() - 1);

        return instances;
    }

    /**
     * Function that classifies the genre of the song passed as parameter
     * @param song  Song whose genre we want to classify
     * @return  an array of doubles containing predicted class probability distribution
     */
    public double[] classify(Instance song) {
        //TODO applica filtro

        double[] distribution = new double[6];
        try {
             distribution = ibk.distributionForInstance(song);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return distribution;
    }
}
