package it.unipi.dii.inginf.dmml.soundhabit.classification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FeatureExtractor {
    private final String ANACONDA_PROMPT_DESTINATION = "C:/Users/feder/anaconda3/Scripts/activate.bat C:/Users/feder/anaconda3";
    private final String PATH_TO_PYTHON_CODE = "C:/Users/feder/IdeaProjects/SoundHabit/FeatureExtractor/main.py";

    public FeatureExtractor () {
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/C",  ANACONDA_PROMPT_DESTINATION + " && python " + PATH_TO_PYTHON_CODE);
            Process p = pb.start();

            BufferedReader bfr = new BufferedReader(new InputStreamReader(p.getInputStream()));

            System.out.println(".........start   process.........");
            String line;
            while ((line = bfr.readLine()) != null) {
                System.out.println("Python Output: " + line);
            }

            System.out.println("........end   process.......");
        } catch (IOException e) {
            e.printStackTrace();
        }

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
