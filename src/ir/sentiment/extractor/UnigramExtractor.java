/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.sentiment.extractor;

import ir.sentiment.model.StopWordList;
import ir.sentiment.model.TextUtils;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author Do
 */
public class UnigramExtractor {

    final static int threshold = 6;
    final static String unigram_path = "unigram.txt";
    final static String unigram_with_neg_path = "unigram_with_neg.txt";

    public ArrayList<String> extractUnigram(ArrayList<String> reviews) {
        ArrayList<String> resultTerms = new ArrayList<String>();
        HashSet<String> stemmedTerms = new HashSet<String>();
        ArrayList< HashSet> reviewSet = new ArrayList<>();
        for (String review : reviews) {
            review = review.toLowerCase();
            ArrayList<String> terms = TextUtils.tokenize(review);
            HashSet<String> words = new HashSet<String>();
            for (String word : terms) {
                String stemmed = TextUtils.stem(word);
                if (TextUtils.isValidStemmedTerm(stemmed)
                        && !StopWordList.isStopWord(word)) {
                    words.add(stemmed);
                }
            }
            reviewSet.add(words);
            stemmedTerms.addAll(words);
        }
        for (String term : stemmedTerms) {
            int count = 0;
            for (HashSet reviewTerms : reviewSet) {
                if (reviewTerms.contains(term)) {
                    count++;
                }
            }
            if (count > threshold) {
                resultTerms.add(term);
            }
        }
        TextUtils.saveTerms(resultTerms, unigram_path);
        return resultTerms;
    }

    public double[] getUnigramCount(String review, ArrayList<String> terms) {
        review = review.toLowerCase();
        ArrayList<String> allTerms = TextUtils.tokenize(review);
        int id = 0;
        double[] result = new double[terms.size()];
        for (String term : terms) {
            result[id] = 0;
            for (int i = 0; i < allTerms.size(); i++) {
                if (TextUtils.stem(allTerms.get(i)).compareTo(term) == 0) {
                    result[id]++;
                }
            }
            id++;
        }
        return result;
    }

    public double[][] unigramTDM(ArrayList<String> reviews, ArrayList<String> terms) {
        double[][] termMatrix = new double[terms.size()][reviews.size()];
        for (int c = 0; c < reviews.size(); c++) {
            String review = reviews.get(c);
            double[] termsVector = getUnigramCount(review, terms);
            for (int i = 0, _n = termsVector.length; i < _n; i++) {
                termMatrix[i][c] = termsVector[i];
            }
        }
        return termMatrix;
    }

    public ArrayList<String> extractUnigramWithNeg(ArrayList<String> reviews) {
        ArrayList<String> resultTerms = new ArrayList<String>();
        HashSet<String> stemmedTerms = new HashSet<String>();
        ArrayList< HashSet> reviewSet = new ArrayList<>();
        for (String review : reviews) {
            review = review.toLowerCase();
            ArrayList<String> tokens = TextUtils.tokenize(review);
            HashSet<String> words = new HashSet<String>();
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
                        words.add(stemmed + "-[NOT]");
                    } else {
                        words.add(stemmed);
                    }
                }
            }
            reviewSet.add(words);
            stemmedTerms.addAll(words);
        }
        System.out.println(reviewSet.size());
        for (String term : stemmedTerms) {
            int count = 0;
            for (HashSet reviewTerms : reviewSet) {
                if (reviewTerms.contains(term)) {
                    count++;
                }
            }
            if (count > threshold) {
                resultTerms.add(term);
            }
        }
        TextUtils.saveTerms(resultTerms, unigram_with_neg_path);
        return resultTerms;
    }

    public double[] getUnigramWithNegCount(String review, ArrayList<String> terms) {
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
        double[] result = new double[terms.size()];
        for (String term : terms) {
            result[id] = 0;
            for (int i = 0; i < allTerms.size(); i++) {
                if (allTerms.get(i).compareTo(term) == 0) {
                    result[id]++;
                }
            }
            id++;
        }
        return result;
    }

    public double[][] unigramWithNegTDM(ArrayList<String> reviews, ArrayList<String> terms) {
        double[][] termMatrix = new double[terms.size()][reviews.size()];
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
