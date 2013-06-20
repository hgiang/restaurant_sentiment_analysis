/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.sentiment.extractor;

import java.util.ArrayList;

/**
 *
 * @author Do
 */
public class FinalExtractor {

    public double[][] finalTDM(ArrayList<String> reviews) {
        UnigramExtractor extractor = new UnigramExtractor();
        ArrayList<String> terms = extractor.extractUnigramWithNeg(reviews);
        double[][] mtx = extractor.unigramWithNegTDM(reviews, terms);
        double[][] result = new double[mtx.length + 2][mtx[0].length];
        for (int i = 0; i < mtx.length; i++) {
            for (int j = 0; j < mtx[0].length; j++) {
                result[i][j] = mtx[i][j];
            }
        }
        DictionaryWordExtractor senti = new DictionaryWordExtractor();
        for (int j = 0; j < mtx[0].length; j++) {
            result[mtx.length][j] = senti.countPos(reviews.get(j));
            result[mtx.length + 1][j] = senti.countNeg(reviews.get(j));
        }
        return result;
    }
}
