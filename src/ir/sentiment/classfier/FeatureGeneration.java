/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.sentiment.classfier;

import Jama.Matrix;
import ir.sentiment.extractor.DictionaryWordExtractor;
import ir.sentiment.extractor.FinalExtractor;
import ir.sentiment.model.StopWordList;
import ir.sentiment.model.TextUtils;
import ir.sentiment.weight.WeightScheme;
import java.util.ArrayList;

/**
 *
 * @author Do
 */
public class FeatureGeneration {

    final static String unigram_with_neg_path = "unigram_with_neg.txt";

    public Matrix extractFeatures(ArrayList<String> contents) {       
        FinalExtractor extractor = new FinalExtractor();
        double[][] mtx = extractor.finalTDM(contents);
        System.out.println(mtx.length);
        WeightScheme scheme = new WeightScheme();
        Matrix trans = new Matrix(mtx);
        //trans = scheme.appTransform(trans);
        return trans;
    }

    private double[] getUnigramWithNegCount(String review, ArrayList<String> terms) {
        review = review.toLowerCase();
        ArrayList<String> allTerms = new ArrayList<String>();
        ArrayList<String> tokens = TextUtils.tokenize(review);
        boolean isNegated = false;
        for (String token : tokens) {
            String stemmed = TextUtils.lemma(token);
            if (TextUtils.isNegatedWord(token)) {
                isNegated = true;
            } else if (TextUtils.isPunctuation(token)) {
                isNegated = false;
            } else if (TextUtils.isValidStemmedTerm(stemmed)
                    && !StopWordList.isStopWord(token)) {
                if (isNegated) {
                    allTerms.add(stemmed + "-[NOT]");
                } else {
                    allTerms.add(stemmed);
                }
            }
        }
        int id = 0;
        double[] result = new double[terms.size() + 2];
        for (String term : terms) {
            result[id] = 0;
            for (int i = 0; i < allTerms.size(); i++) {
                if (allTerms.get(i).compareTo(term) == 0) {
                    result[id]++;
                }
            }
            id++;
        }
        DictionaryWordExtractor senti = new DictionaryWordExtractor();
        result[terms.size()] = senti.countPos(review);
        result[terms.size() + 1] = senti.countNeg(review);
        return result;
    }

    public double[][] featureMatrix(ArrayList<String> reviews) {
        ArrayList<String> terms = TextUtils.loadTerms(unigram_with_neg_path);
        double[][] termMatrix = new double[terms.size() + 2][reviews.size()];
        for (int c = 0; c < reviews.size(); c++) {
            String review = reviews.get(c);
            double[] termsVector = getUnigramWithNegCount(review, terms);
            for (int i = 0, _n = termsVector.length; i < _n; i++) {
                termMatrix[i][c] = termsVector[i];
            }
        }
        return termMatrix;
    }
}
