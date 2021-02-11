package it.unipi.dii.inginf.dmml.soundhabit.classification;

import org.python.core.*;
import org.python.util.PythonInterpreter;

import java.util.Properties;

public class FeatureExtractor {
    private PythonInterpreter interpreter;

    public FeatureExtractor ()
    {
        /*Properties properties = new Properties();
        properties.setProperty("python.path", "../FeatureExtractor");
        PythonInterpreter.initialize(System.getProperties(), properties, new String[]{""});
        interpreter = new PythonInterpreter();
        interpreter.exec("from Extractor import square");

        /*interpreter.exec("result = square(5)");
        interpreter.exec("print(result)");
        PyInteger result = (PyInteger) interpreter.get("result");
        System.out.println("result: "+ result.asInt());*/
        /*PyFunction pf = (PyFunction) interpreter.get("square");
        System.out.println(pf.__call__(new PyInteger(5)));
/*
        interpreter.exec("from Extractor import extract_feature");
        PyFunction pf = (PyFunction) interpreter.get("extract_feature");
        System.out.println(pf.__call__(new PyString("/home/danielecioffo/Documenti/GitHub/SoundHabit/FeatureExtractor/genres/blues/blues.00000.wav")));
    */
    }
}
