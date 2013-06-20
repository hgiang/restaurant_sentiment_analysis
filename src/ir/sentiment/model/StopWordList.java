/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.sentiment.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

/**
 *
 * @author Do
 */
public class StopWordList {

    private static String path = "resources/stop_words.txt";
    private static HashSet<String> set = new HashSet();

    public static void initialize() {
        try {
            String sCurrentLine;
            BufferedReader br = new BufferedReader(new FileReader(path));
            while ((sCurrentLine = br.readLine()) != null) {
                set.add(sCurrentLine);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isStopWord(String word) {
        return set.contains(word.toLowerCase());
    }
}
