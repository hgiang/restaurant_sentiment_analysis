package ir.sentiment.extractor;

import ir.sentiment.model.StopWordList;
import ir.sentiment.model.TextUtils;
import java.util.ArrayList;
import java.util.HashSet;

public class BigramExtractor {

    static final int threshold = 2;
    static String bigram_path = "bigram.txt";
    static String bigram_negation_path = "bigram_with_neg.txt";

    public ArrayList<ArrayList<String>> extractAllDup(ArrayList<String> reviews) {
        ArrayList<ArrayList<String>> allTerms = new ArrayList<ArrayList<String>>();
        for (String review : reviews) {
            ArrayList<String> uni = TextUtils.tokenize(review);
            ArrayList<String> words = new ArrayList<String>();
            for (String word : uni) {
                String lemma = TextUtils.lemma(word);
                words.add(lemma);
            }
            ArrayList<String> reviewedTerm = new ArrayList<String>();
            for (int i = 0; i < words.size() - 1; i++) {
                if (!TextUtils.isSpecialCharacter(words.get(i)) && !TextUtils.isSpecialCharacter((words.get(i + 1))) && !(StopWordList.isStopWord(words.get(i)) && StopWordList.isStopWord(words.get(i + 1)))) {
                    String temp = words.get(i) + " " + words.get(i + 1);
                    reviewedTerm.add(temp);
                }
            }
            allTerms.add(reviewedTerm);
        }
        return allTerms;
    }

    //Extract all reviews given into bigrams.
    public ArrayList<ArrayList<String>> extractAll(ArrayList<String> reviews) {
        ArrayList<ArrayList<String>> allTerms = new ArrayList<ArrayList<String>>();
        for (String review : reviews) {
            ArrayList<String> uni = TextUtils.tokenize(review);
            ArrayList<String> words = new ArrayList<String>();
            for (String word : uni) {
                String lemma = TextUtils.lemma(word);
                words.add(lemma);
            }
            ArrayList<String> reviewedTerm = new ArrayList<String>();
            for (int i = 0; i < words.size() - 1; i++) {
                if (!TextUtils.isSpecialCharacter(words.get(i)) && !TextUtils.isSpecialCharacter((words.get(i + 1))) && !(StopWordList.isStopWord(words.get(i)) && StopWordList.isStopWord(words.get(i + 1)))) {
                    String temp = words.get(i) + " " + words.get(i + 1);
                    if (!reviewedTerm.contains(temp)) {
                        reviewedTerm.add(temp);
                    }
                }
            }
            allTerms.add(reviewedTerm);
        }
        return allTerms;
    }

    public ArrayList<ArrayList<String>> extractAllWithNegDup(ArrayList<String> reviews) {
        ArrayList<ArrayList<String>> allTerms = new ArrayList<ArrayList<String>>();
        for (String review : reviews) {
            ArrayList<String> tokens = TextUtils.tokenize(review);
            ArrayList<String> words = new ArrayList<String>();
            boolean isNegated = false;
            for (String token : tokens) {
                String stemmed = TextUtils.lemma(token);
                if (TextUtils.isNegatedWord(token)) {
                    isNegated = true;
                } else if (TextUtils.isSpecialCharacter(token)) {
                    isNegated = false;
                    words.add(stemmed);
                } else {
                    if (isNegated) {
                        words.add(stemmed + "-[NOT]");
                    } else {
                        words.add(stemmed);
                    }
                }
            }
            ArrayList<String> reviewedTerm = new ArrayList<String>();
            for (int i = 0; i < words.size() - 1; i++) {
                if (!TextUtils.isSpecialCharacter(words.get(i)) && !TextUtils.isSpecialCharacter((words.get(i + 1))) && !(StopWordList.isStopWord(words.get(i)) && StopWordList.isStopWord(words.get(i + 1)))) {
                    String temp = words.get(i) + " " + words.get(i + 1);
                    reviewedTerm.add(temp);
                }
            }
            allTerms.add(reviewedTerm);
        }
        return allTerms;
    }

    //Extract all reviews given into bigrams with negation.
    public ArrayList<ArrayList<String>> extractAllWithNeg(ArrayList<String> reviews) {
        ArrayList<ArrayList<String>> allTerms = new ArrayList<ArrayList<String>>();
        for (String review : reviews) {
            ArrayList<String> tokens = TextUtils.tokenize(review);
            ArrayList<String> words = new ArrayList<String>();
            boolean isNegated = false;
            for (String token : tokens) {
                String stemmed = TextUtils.lemma(token);
                if (TextUtils.isNegatedWord(token)) {
                    isNegated = true;
                } else if (TextUtils.isSpecialCharacter(token)) {
                    isNegated = false;
                    words.add(stemmed);
                } else {
                    if (isNegated) {
                        words.add(stemmed + "-[NOT]");
                    } else {
                        words.add(stemmed);
                    }
                }
            }
            ArrayList<String> reviewedTerm = new ArrayList<String>();
            for (int i = 0; i < words.size() - 1; i++) {
                if (!TextUtils.isSpecialCharacter(words.get(i)) && !TextUtils.isSpecialCharacter((words.get(i + 1))) && !(StopWordList.isStopWord(words.get(i)) && StopWordList.isStopWord(words.get(i + 1)))) {
                    String temp = words.get(i) + " " + words.get(i + 1);
                    if (!reviewedTerm.contains(temp)) {
                        reviewedTerm.add(temp);
                    }
                }
            }
            allTerms.add(reviewedTerm);
        }
        return allTerms;
    }

    public ArrayList<String> extractBigram(ArrayList<String> reviews) {
        ArrayList<String> resultTerms = new ArrayList<String>();
        ArrayList<ArrayList<String>> allTerms = extractAll(reviews);
        for (int i = 0; i < allTerms.size() - 1; i++) {
            ArrayList<String> temp = allTerms.get(i);
            for (int j = 0; j < temp.size(); j++) {
                int termCount = 1;
                for (int k = i + 1; k < allTerms.size(); k++) {
                    if (allTerms.get(k).contains(temp.get(j))) {
                        termCount++;
                    }
                }
                if (termCount >= threshold && !resultTerms.contains(temp.get(j))) {
                    resultTerms.add(temp.get(j));
                }
            }
        }

        TextUtils.saveTerms(resultTerms, bigram_negation_path);
        return resultTerms;
    }

    public ArrayList<String> extractBigramWithNeg(ArrayList<String> reviews) {
        ArrayList<String> resultTerms = new ArrayList<String>();
        ArrayList<ArrayList<String>> allTerms = extractAllWithNeg(reviews);
        for (int i = 0; i < allTerms.size() - 1; i++) {
            ArrayList<String> temp = allTerms.get(i);
            for (int j = 0; j < temp.size(); j++) {
                int termCount = 1;
                for (int k = i + 1; k < allTerms.size(); k++) {
                    if (allTerms.get(k).contains(temp.get(j))) {
                        termCount++;
                    }
                }
                if (termCount >= threshold && !resultTerms.contains(temp.get(j))) {
                    resultTerms.add(temp.get(j));
                }
            }
        }

        TextUtils.saveTerms(resultTerms, bigram_negation_path);
        return resultTerms;
    }

    public double[][] bigramTDM(ArrayList<String> reviews) {
        ArrayList<String> allBigram = extractBigram(reviews);
        ArrayList<ArrayList<String>> allTerm = extractAllDup(reviews);
        int doc = reviews.size();
        int maxTerm = allBigram.size();
        double[][] bigram = new double[maxTerm][doc];
        for (int i = 0; i < doc; i++) {
            for (int j = 0; j < maxTerm; j++) {
                bigram[j][i] = 0;
            }
        }
        for (int i = 0; i < doc; i++) {
            ArrayList<String> terms = allTerm.get(i);
            for (int j = 0; j < maxTerm; j++) {
                int count = 0;
                for (int k = 0; k < terms.size(); k++) {
                    if (terms.get(k).equals(allBigram.get(j))) {
                        count++;
                    }
                }
                bigram[j][i] = count;
            }

        }
        return bigram;
    }

    public double[][] bigramWithNegTDM(ArrayList<String> reviews) {
        ArrayList<String> allBigram = extractBigramWithNeg(reviews);
        ArrayList<ArrayList<String>> allTerm = extractAllWithNegDup(reviews);
        int doc = reviews.size();
        int maxTerm = allBigram.size();
        double[][] bigram = new double[maxTerm][doc];
        for (int i = 0; i < doc; i++) {
            for (int j = 0; j < maxTerm; j++) {
                bigram[j][i] = 0;
            }
        }
        for (int i = 0; i < doc; i++) {
            ArrayList<String> terms = allTerm.get(i);
            for (int j = 0; j < maxTerm; j++) {
                int count = 0;
                for (int k = 0; k < terms.size(); k++) {
                    if (terms.get(k).equals(allBigram.get(j))) {
                        count++;
                    }
                }
                bigram[j][i] = count;
            }

        }
        return bigram;
    }

    public static void setBigram_path(String path) {
        bigram_path = path;
    }

    public static void setBigram_negation_path(String path) {
        bigram_negation_path = path;
    }

    public ArrayList<String> extractBiGram(ArrayList<String> reviews) {
        ArrayList<String> resultTerms = new ArrayList<String>();
        HashSet<String> stemmedTerms = new HashSet<String>();
        ArrayList< HashSet> reviewSet = new ArrayList<>();
        for (String review : reviews) {
            review = review.toLowerCase();
            review = TextUtils.refineString(review);
            ArrayList<String> terms = TextUtils.tokenize(review);
            HashSet<String> words = new HashSet<String>();
            for (int i = 1; i < terms.size(); i++) {
                String[] word = {terms.get(i - 1), terms.get(i)};
                String[] stemmed = {TextUtils.lemma(word[0]),
                    TextUtils.lemma(word[1])};
                if (!StopWordList.isStopWord(word[0]) || !StopWordList.isStopWord(word[1])) {
                    words.add(stemmed[0] + " " + stemmed[1]);
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
        TextUtils.saveTerms(resultTerms, bigram_path);
        return resultTerms;
    }

    public double[] getBiGramCount(String review, ArrayList<String> terms) {
        ArrayList<String> allTerms = new ArrayList<String>();
        review = review.toLowerCase();
        review = TextUtils.refineString(review);
        ArrayList<String> tokens = TextUtils.tokenize(review);
        for (int i = 1; i < tokens.size(); i++) {
            String[] word = {tokens.get(i - 1), tokens.get(i)};
            String[] stemmed = {TextUtils.lemma(word[0]),
                TextUtils.lemma(word[1])};
            if (!StopWordList.isStopWord(word[0]) || !StopWordList.isStopWord(word[1])) {
                allTerms.add(stemmed[0] + " " + stemmed[1]);
            }
        }
        int id = 0;
        double[] result = new double[terms.size()];
        for (String term : terms) {
            result[id] = 0;
            for (String token : allTerms) {
                if (token.compareTo(term) == 0) {
                    result[id]++;
                }
            }
            id++;
        }
        return result;
    }

    public double[][] biGramTDM(ArrayList<String> reviews, ArrayList<String> terms) {
        double[][] termMatrix = new double[terms.size()][reviews.size()];
        for (int c = 0; c < reviews.size(); c++) {
            String review = reviews.get(c);
            double[] termsVector = getBiGramCount(review, terms);
            for (int i = 0, _n = termsVector.length; i < _n; i++) {
                termMatrix[i][c] = termsVector[i];
            }
        }
        return termMatrix;
    }
}