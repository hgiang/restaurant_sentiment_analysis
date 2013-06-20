/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.sentiment.extractor;

import edu.stanford.nlp.ling.TaggedWord;
import ir.sentiment.model.StopWordList;
import ir.sentiment.model.TextUtils;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author Do
 */
public class TaggedWordExtractor {

    final static int threshold = 2;
    final static String tagged_path = "tagged.txt";
    final static String tagged_with_neg_path = "tagged_with_neg.txt";

    public ArrayList<String> extractTagged(ArrayList<String> reviews) {
        ArrayList<String> resultTerms = new ArrayList<String>();
        HashSet<String> stemmedTerms = new HashSet<String>();
        ArrayList< HashSet> reviewSet = new ArrayList<>();
        for (String review : reviews) {
            review = review.toLowerCase();
            ArrayList<TaggedWord> tokens = TextUtils.tagPOS(review);
            HashSet<String> words = new HashSet<String>();
            for (TaggedWord token : tokens) {
                String word = token.value();
                String tag = token.tag();
                String stemmed = TextUtils.lemma(word);
                if (TextUtils.isValidStemmedTerm(stemmed)
                        && !StopWordList.isStopWord(word)
                        && (tag.startsWith("JJ") || tag.startsWith("RB"))) {
                    words.add(stemmed + "_" + tag);
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
        TextUtils.saveTerms(resultTerms, tagged_path);
        return resultTerms;
    }

    public double[] getTaggedCount(String review, ArrayList<String> terms) {
        review = review.toLowerCase();
        ArrayList<String> allTerms = new ArrayList<String>();
        ArrayList<TaggedWord> tokens = TextUtils.tagPOS(review);
        for (TaggedWord token : tokens) {
            String word = token.value();
            String tag = token.tag();
            String stemmed = TextUtils.lemma(word);
            if (TextUtils.isValidStemmedTerm(stemmed)
                    && !StopWordList.isStopWord(word)
                    && (tag.startsWith("JJ") || tag.startsWith("RB"))) {
                allTerms.add(stemmed + "_" + tag);
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

    public double[][] taggedTDM(ArrayList<String> reviews, ArrayList<String> terms) {
        double[][] termMatrix = new double[terms.size()][reviews.size()];
        for (int c = 0; c < reviews.size(); c++) {
            String review = reviews.get(c);
            double[] termsVector = getTaggedCount(review, terms);
            for (int i = 0, _n = termsVector.length; i < _n; i++) {
                termMatrix[i][c] = termsVector[i];
            }
        }
        return termMatrix;
    }

    public ArrayList<String> extractTaggedWithNeg(ArrayList<String> reviews) {
        ArrayList<String> resultTerms = new ArrayList<String>();
        HashSet<String> stemmedTerms = new HashSet<String>();
        ArrayList< HashSet> reviewSet = new ArrayList<>();
        for (String review : reviews) {
            review = review.toLowerCase();
            ArrayList<TaggedWord> tokens = TextUtils.tagPOS(review);
            HashSet<String> words = new HashSet<String>();
            boolean isNegated = false;
            for (TaggedWord token : tokens) {
                String word = token.value();
                String tag = token.tag();
                String stemmed = TextUtils.lemma(word);
                if (TextUtils.isNegatedWord(word)) {
                    isNegated = true;
                } else if (TextUtils.isPunctuation(word)) {
                    isNegated = false;
                } else if (TextUtils.isValidStemmedTerm(stemmed)
                        && !StopWordList.isStopWord(word)
                        && (tag.startsWith("JJ") || tag.startsWith("RB"))) {
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
        TextUtils.saveTerms(resultTerms, tagged_with_neg_path);
        return resultTerms;
    }

    public double[] getTaggedWithNegCount(String review, ArrayList<String> terms) {
        review = review.toLowerCase();
        ArrayList<String> allTerms = new ArrayList<String>();
        ArrayList<TaggedWord> tokens = TextUtils.tagPOS(review);
        boolean isNegated = false;
        for (TaggedWord token : tokens) {
            String word = token.value();
            String tag = token.tag();
            String stemmed = TextUtils.lemma(word);
            if (TextUtils.isNegatedWord(word)) {
                isNegated = true;
            } else if (TextUtils.isPunctuation(word)) {
                isNegated = false;
            } else if (TextUtils.isValidStemmedTerm(stemmed)
                    && !StopWordList.isStopWord(word)
                    && (tag.startsWith("JJ") || tag.startsWith("RB"))) {
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

    public double[][] taggedWithNegTDM(ArrayList<String> reviews, ArrayList<String> terms) {
        double[][] termMatrix = new double[terms.size()][reviews.size()];
        for (int c = 0; c < reviews.size(); c++) {
            String review = reviews.get(c);
            double[] termsVector = getTaggedWithNegCount(review, terms);
            for (int i = 0, _n = termsVector.length; i < _n; i++) {
                termMatrix[i][c] = termsVector[i];
            }
        }
        return termMatrix;
    }
}
