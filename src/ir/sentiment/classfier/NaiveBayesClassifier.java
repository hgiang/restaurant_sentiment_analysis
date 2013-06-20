/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.sentiment.classfier;

import java.io.BufferedReader;
import java.io.FileReader;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.core.Debug;
import weka.core.Instances;

/**
 *
 * @author Do
 */
public class NaiveBayesClassifier {

    public NaiveBayesMultinomial trainModel(String trainFile) {
        NaiveBayesMultinomial classifier = new NaiveBayesMultinomial();
        try {
            BufferedReader reader = new BufferedReader(
                    new FileReader(trainFile));
            Instances trainData = new Instances(reader);
            reader.close();
            // setting class attribute
            trainData.setClassIndex(0);
            classifier.buildClassifier(trainData);
            Evaluation evaluation = new Evaluation(trainData);
            evaluation.crossValidateModel(classifier, trainData, 10, new Debug.Random(1));
            System.out.println(evaluation.toSummaryString());
            System.out.println("F-Measure = " + evaluation.weightedFMeasure());
            double[][] mtx = evaluation.confusionMatrix();
            for (int i = 0; i < mtx.length; i++) {
                for (int j = 0; j < mtx[0].length; j++) {
                    System.out.print(mtx[i][j] + " ");
                }
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classifier;
    }

    public double[][] classify(NaiveBayesMultinomial classifier, String testFile) {
        double[][] result = null;
        try {
            BufferedReader reader = new BufferedReader(
                    new FileReader(testFile));
            Instances testData = new Instances(reader);
            result = new double[testData.numInstances()][2];
            reader.close();
            // setting class attribute
            testData.setClassIndex(0);
            for (int i = 0; i < testData.numInstances(); i++) {
                double label = classifier.classifyInstance(testData.instance(i));
                double [] prob = classifier.distributionForInstance(testData.instance(i));
                System.out.println(label);
                result[i][0] = label;
                result[i][1] = Math.max(prob[0], Math.max(prob[1], prob[2]));
            }            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
