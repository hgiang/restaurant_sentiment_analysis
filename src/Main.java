
import ir.sentiment.model.TextUtils;
import Jama.Matrix;
import ir.sentiment.classfier.FeatureGeneration;
import ir.sentiment.classfier.NaiveBayesClassifier;
import ir.sentiment.classfier.ReviewWriter;
import ir.sentiment.extractor.ReviewReader;
import ir.sentiment.model.Review;
import ir.sentiment.model.StopWordList;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import weka.classifiers.bayes.NaiveBayesMultinomial;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Do
 */
public class Main {

    public static void saveToCSV(Matrix mtx, ArrayList<String> label,
            String path) {
        try {
            File file = new File(path);
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("label");
            for (int i = 0; i < mtx.getRowDimension(); i++) {
                bw.write(",term" + (i + 1));
            }
            bw.newLine();
            for (int i = 0; i < mtx.getColumnDimension(); i++) {
                bw.write("" + label.get(i));
                for (int j = 0; j < mtx.getRowDimension(); j++) {
                    bw.write("," + mtx.get(j, i));
                }
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeToARFF(Matrix matrix, ArrayList<String> label, String path) {
        try {
            File file = new File(path);
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            System.out.println(label.size() + " " + matrix.getRowDimension() + " " + matrix.getRowDimension());
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("@relation data");
            bw.newLine();
            bw.newLine();
            bw.write("@attribute label {neg,neu,pos}");
            bw.newLine();
            for (int i = 0; i < matrix.getRowDimension(); i++) {
                bw.write("@attribute term" + i + " numeric");
                bw.newLine();
            }
            bw.newLine();
            bw.write("@data");
            bw.newLine();
            for (int i = 0; i < label.size(); i++) {
                bw.write("" + label.get(i));
                for (int j = 0; j < matrix.getRowDimension(); j++) {
                    bw.write("," + matrix.get(j, i));
                }
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        ReviewReader.preprocess("train.xml");
        ArrayList<Review> train = ReviewReader.getReviewInfo("reviews.xml");
        ReviewReader.preprocess("final_test.xml");
        ReviewReader.noOfDocs = 700;
        ArrayList<Review> test = ReviewReader.getReviewInfo("reviews.xml");
        long seed = System.nanoTime();
        Collections.shuffle(test, new Random(seed));
        ArrayList<String> trainContents = new ArrayList<String>();
        ArrayList<String> testContents = new ArrayList<String>();
        HashSet<String> users = new HashSet<String>();
        for (Review r : train) {
            if (users.contains(r.getUser())) {
                System.out.println(r.getUser());
            }
            users.add(r.getUser());
        }
        for (Review r : test) {
            if (users.contains(r.getUser())) {
                System.out.println(r.getUser());
            }
            users.add(r.getUser());
        }
        System.out.println(test.size());
        System.out.println(users.size());
        for (Review r : train) {
            trainContents.add(r.getContent());
        }
        for (Review r : test) {
            testContents.add(r.getContent());
        }
        //Initialization
        TextUtils.initialize();
        StopWordList.initialize();
        ArrayList<String> label = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            label.add("pos");
        }
        for (int i = 0; i < 100; i++) {
            label.add("neu");
        }
        for (int i = 0; i < 100; i++) {
            label.add("neg");
        }
        FeatureGeneration generation = new FeatureGeneration();
        Matrix mtx = generation.extractFeatures(trainContents);
        saveToCSV(mtx, label, "train.csv");
        writeToARFF(mtx, label, "train.arff");
        mtx = new Matrix(generation.featureMatrix(testContents));
        ArrayList<String> stars = new ArrayList<String>();
        for (Review r : test) {
            if (r.getRating() == 5) {
                stars.add("pos");
            } else if (r.getRating() > 2) {
                stars.add("neu");
            } else {
                stars.add("neg");
            }
        }
        saveToCSV(mtx, stars, "test.csv");
        writeToARFF(mtx, stars, "test.arff");
        NaiveBayesClassifier classifier = new NaiveBayesClassifier();
        NaiveBayesMultinomial trainedClassifier = classifier.trainModel("train.arff");
        double[][] result = classifier.classify(trainedClassifier, "test.arff");
        ReviewWriter.writeToXML(test, result, "ouput.xml");
    }
}
