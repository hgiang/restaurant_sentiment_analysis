package ir.sentiment.model;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.util.Version;
import org.tartarus.snowball.ext.PorterStemmer;

/**
 *
 * @author Do
 */
public class TextUtils {

    final private static String path = "resources/pos_models/"
            + "wsj-0-18-bidirectional-distsim.tagger";
    private static MaxentTagger tagger;

    public static void initialize() {
        try {
            tagger = new MaxentTagger(path);
        } catch (Exception e) {
        }
    }

    public static ArrayList<String> tokenize(String sentence) {
        sentence = sentence.trim();
        ArrayList<String> result = new ArrayList<String>();
        Reader reader = new StringReader(sentence);
        Tokenizer<CoreLabel> tokenizer = new PTBTokenizer<CoreLabel>(reader, new CoreLabelTokenFactory(), "");
        while (tokenizer.hasNext()) {
            CoreLabel token = tokenizer.next();
            String word = token.originalText();
            result.add(word);
        }
        return result;
    }

    public static String tagSentence(String sentence) {
        // The tagged string
        String tagged = tagger.tagString(sentence);
        return tagged;
    }

    public static ArrayList<TaggedWord> tagPOS(String review) {
        ArrayList<TaggedWord> result = new ArrayList<TaggedWord>();
        List<List<HasWord>> sentences = MaxentTagger.tokenizeText(new StringReader(review));
        for (List<HasWord> sentence : sentences) {
            ArrayList<TaggedWord> tSentence = tagger.tagSentence(sentence);
            result.addAll(tSentence);
        }
        return result;
    }

    public static String lemma(String text) {
        String result = text;
        try {
            EnglishAnalyzer en_an = new EnglishAnalyzer(Version.LUCENE_42);
            QueryParser parser = new QueryParser(Version.LUCENE_42, "", en_an);
            result = parser.parse(QueryParser.escape(text.toLowerCase())).toString();
        } catch (ParseException ex) {
            System.err.println(text);
            ex.printStackTrace();
        }
        if (result.length() > 0) {
            return result;
        } else {
            return text;
        }
    }

    public static String stem(String word) {
        PorterStemmer stem = new PorterStemmer();
        stem.setCurrent(word.toLowerCase());
        stem.stem();
        String result = stem.getCurrent();
        return result;
    }

    public static boolean isNegatedWord(String token) {
        String negRegex = "(?:"
                + "^(?:never|no|nothing|nowhere|noone|none|not"
                + "|havent|hasnt|hadnt|cant|couldnt|shouldnt"
                + "|wont|wouldnt|dont|doesnt|didnt|isnt|arent|aint"
                + ")$"
                + ")"
                + "|"
                + "n't";
        return token.matches(negRegex);
    }

    public static boolean isPunctuation(String token) {
        String puncRegex = "^[.:;!,-?]$";
        return token.matches(puncRegex);
    }

    public static boolean isSpecialCharacter(String token) {
        String puncRegex = "^[.:;!-?,|_]$";
        return token.matches(puncRegex);
    }

    public static String refineString(String s) {
        return s.replaceAll("[.:;!-?,|_]", "");
    }

    public static boolean isValidStemmedTerm(String word) {
        if (word.length() < 2) {
            return false;
        } else if (!Character.isLetter(word.charAt(0))) {
            return false;
        }
        return true;
    }

    public static void saveTerms(ArrayList<String> terms, String path) {
        try {
            File file = new File(path);
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            for (int i = 0; i < terms.size(); i++) {
                bw.write(terms.get(i) + "\n");
            }
            bw.close();
        } catch (Exception e) {
        }
    }

    public static ArrayList<String> loadTerms(String path) {
        ArrayList<String> terms = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String term = new String();
            while ((term = br.readLine()) != null) {
                terms.add(term);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return terms;
    }
}
