/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.sentiment.extractor;

import ir.sentiment.model.TextUtils;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author Do
 */
public class DictionaryWordExtractor {

    final static int threshold = 2;
    private HashSet<String> positiveWords = new HashSet<>();
    private HashSet<String> negativeWords = new HashSet<>();
    private final String positivePath =
            "resources/positive-words.txt";
    private final String negativePath =
            "resources/negative-words.txt";
    private final String dict_path = "dict_wotds.txt";
    private final String dict_with_neg_path = "dict_with_neg_wotds.txt";

    public DictionaryWordExtractor() {
        initSentiWords();
    }

    private void initSentiWords() {
        try {
            String sCurrentLine;
            BufferedReader br = new BufferedReader(new FileReader(positivePath));
            while ((sCurrentLine = br.readLine()) != null) {
                positiveWords.add(sCurrentLine);
            }
            br.close();
            br = new BufferedReader(new FileReader(negativePath));
            while ((sCurrentLine = br.readLine()) != null) {
                negativeWords.add(sCurrentLine);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> extractDictWord(ArrayList<String> reviews) {
        ArrayList<String> resultTerms = new ArrayList<String>();
        ArrayList< HashSet> reviewSet = new ArrayList<>();
        HashSet<String> dicWords = new HashSet<String>();
        for (String review : reviews) {
            review = review.toLowerCase();
            ArrayList<String> terms = TextUtils.tokenize(review);
            HashSet<String> words = new HashSet<String>();
            for (String word : terms) {
                word = word.toLowerCase();
                if (positiveWords.contains(word) || negativeWords.contains(word)) {
                    words.add(word);
                }
            }
            reviewSet.add(words);
            dicWords.addAll(words);
        }
        for (String term : dicWords) {
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
        TextUtils.saveTerms(resultTerms, dict_path);
        return resultTerms;
    }

    public double[] getDictWordCount(String review, ArrayList<String> terms) {
        review = review.toLowerCase();
        ArrayList<String> allTerms = TextUtils.tokenize(review);
        int id = 0;
        double[] result = new double[terms.size()];
        for (String term : terms) {
            result[id] = 0;
            for (int i = 0; i < allTerms.size(); i++) {
                String word = allTerms.get(i).toLowerCase();
                if (word.compareTo(term) == 0) {
                    result[id]++;
                }
            }
            id++;
        }
        return result;
    }

    public double[][] dictTDM(ArrayList<String> reviews, ArrayList<String> terms) {
        double[][] termMatrix = new double[terms.size()][reviews.size()];
        for (int c = 0; c < reviews.size(); c++) {
            String review = reviews.get(c);
            double[] termsVector = getDictWordCount(review, terms);
            for (int i = 0, _n = termsVector.length; i < _n; i++) {
                termMatrix[i][c] = termsVector[i];
            }
        }
        return termMatrix;
    }

    public ArrayList<String> extractDictWordWithNeg(ArrayList<String> reviews) {
        ArrayList<String> resultTerms = new ArrayList<String>();
        HashSet<String> stemmedTerms = new HashSet<String>();
        ArrayList< HashSet> reviewSet = new ArrayList<>();
        for (String review : reviews) {
            review = review.toLowerCase();
            ArrayList<String> tokens = TextUtils.tokenize(review);
            HashSet<String> words = new HashSet<String>();
            boolean isNegated = false;
            for (String token : tokens) {
                if (TextUtils.isNegatedWord(token)) {
                    isNegated = true;
                } else if (TextUtils.isPunctuation(token)) {
                    isNegated = false;
                } else if (positiveWords.contains(token) || negativeWords.contains(token)) {
                    if (isNegated) {
                        words.add(token + "-[NOT]");
                    } else {
                        words.add(token);
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
        TextUtils.saveTerms(resultTerms, dict_with_neg_path);
        return resultTerms;
    }

    public double[] getDictWithNegWordCount(String review, ArrayList<String> terms) {
        review = review.toLowerCase();
        ArrayList<String> allTerms = new ArrayList<String>();
        ArrayList<String> tokens = TextUtils.tokenize(review);
        boolean isNegated = false;
        for (String token : tokens) {
            if (TextUtils.isNegatedWord(token)) {
                isNegated = true;
            } else if (TextUtils.isPunctuation(token)) {
                isNegated = false;
            } else if (positiveWords.contains(token) || negativeWords.contains(token)) {
                if (isNegated) {
                    allTerms.add(token + "-[NOT]");
                } else {
                    allTerms.add(token);
                }
            }
        }
        int id = 0;
        double[] result = new double[terms.size()];
        for (String term : terms) {
            result[id] = 0;
            for (int i = 0; i < allTerms.size(); i++) {
                String word = allTerms.get(i);
                if (word.compareTo(term) == 0) {
                    result[id]++;
                }
            }
            id++;
        }
        return result;
    }

    public double[][] dictWithNegTDM(ArrayList<String> reviews, ArrayList<String> terms) {
        double[][] termMatrix = new double[terms.size()][reviews.size()];
        for (int c = 0; c < reviews.size(); c++) {
            String review = reviews.get(c);
            double[] termsVector = getDictWithNegWordCount(review, terms);
            for (int i = 0, _n = termsVector.length; i < _n; i++) {
                termMatrix[i][c] = termsVector[i];
            }
        }
        return termMatrix;
    }

    public int getSentiSenScore(String sen) {
        ArrayList<String> words = TextUtils.tokenize(sen);
        int result = 0;
        for (String word : words) {
            word = word.toLowerCase();
            if (positiveWords.contains(word)) {
                result++;
            }
            if (negativeWords.contains(word)) {
                result--;
            }
        }
        return result;
    }

    public int getSentiWithNegScore(String review) {
        int result = 0;
        ArrayList<String> tokens = TextUtils.tokenize(review);
        boolean isNegated = false;
        for (String token : tokens) {
            if (TextUtils.isNegatedWord(token)) {
                isNegated = true;
            } else if (TextUtils.isPunctuation(token)) {
                isNegated = false;
            }
            if (isNegated) {
                if (positiveWords.contains(token)) {
                    result--;
                } else if (negativeWords.contains(token)) {
                    result++;
                }
            } else {
                if (positiveWords.contains(token)) {
                    result++;
                } else if (negativeWords.contains(token)) {
                    result--;
                }
            }
        }
        return result;
    }

    public int countPos(String review) {
        int result = 0;
        ArrayList<String> tokens = TextUtils.tokenize(review);
        boolean isNegated = false;
        for (String token : tokens) {
            if (TextUtils.isNegatedWord(token)) {
                isNegated = true;
            } else if (TextUtils.isPunctuation(token)) {
                isNegated = false;
            }
            if (isNegated) {
                if (negativeWords.contains(token)) {
                    result++;
                }
            } else {
                if (positiveWords.contains(token)) {
                    result++;
                }
            }
        }
        return result;
    }

    public int countNeg(String review) {
        int result = 0;
        ArrayList<String> tokens = TextUtils.tokenize(review);
        boolean isNegated = false;
        for (String token : tokens) {
            if (TextUtils.isNegatedWord(token)) {
                isNegated = true;
            } else if (TextUtils.isPunctuation(token)) {
                isNegated = false;
            }
            if (isNegated) {
                if (positiveWords.contains(token)) {
                    result++;
                }
            } else {
                if (negativeWords.contains(token)) {
                    result++;
                }
            }
        }
        return result;
    }
}
