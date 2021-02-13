package it.unipi.dii.inginf.dmml.soundhabit.classification;

import it.unipi.dii.inginf.dmml.soundhabit.utils.Utils;
import weka.attributeSelection.CorrelationAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

public class Classifier {
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
        try {
            Instances instances = Utils.loadDataset(PATH_TO_DATASET);
            Instances reduced = selectAttributes(instances);

            classifier = new IBk();
            classifier.setKNN(K);
            classifier.buildClassifier(reduced);

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return classifier;
    }

    /**
     * Function that builds the attribute selection filter
     * @return  the filter
     */
    private AttributeSelection buildAttributeSelectionFilter() {
        AttributeSelection filter = new AttributeSelection();

        try {
            Instances instances = Utils.loadDataset(PATH_TO_DATASET);
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
     * Function that classifies the genre of the song passed as parameter
     * @param unlabeled  Song whose genre we want to classify
     * @return  an array of doubles containing predicted class probability distribution
     */
    public double[] classify(Instances unlabeled) {
        double[] distribution = new double[6];
        try {
            Instances reduced = selectAttributes(unlabeled);
            distribution = ibk.distributionForInstance(reduced.firstInstance());
            System.out.println("PREDICTED CLASS: " + unlabeled.classAttribute().value((int) ibk.classifyInstance(reduced.firstInstance())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return distribution;
    }
}
