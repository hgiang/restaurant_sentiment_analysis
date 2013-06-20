/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.sentiment.extractor;

import ir.sentiment.model.Review;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Do
 */
public class ReviewReader extends DefaultHandler {

    public static int noOfDocs = 300;
    public static String filePath = "reviews.xml";

    private static String fixXMLString(String s) {
        s = s.replace("&", "&amp;");
        return s;
    }

    public static void preprocess(String path) {
        try {
            File file = new File(filePath);
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line = new String();
            while ((line = br.readLine()) != null) {
                bw.write(fixXMLString(line));
                bw.newLine();
            }
            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Review> getReviewInfo(String dir) {
        ArrayList<Review> reviews = new ArrayList<>();
        try {
            for (int i = 0; i < noOfDocs; i++) {
                File xml = new File(dir);
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(xml);
                doc.getDocumentElement().normalize();
                Review review = new Review(String.valueOf(i + 1));
                Node node = doc.getElementsByTagName("stars").item(i);
                if (node != null) {
                    review.setRating(Double.parseDouble(node.getTextContent()));
                }
                node = doc.getElementsByTagName("url").item(i);
                if (node != null) {
                    review.setUrl(node.getTextContent().trim());
                }
                node = doc.getElementsByTagName("date").item(i);
                if (node != null) {
                    review.setDate(node.getTextContent().trim());
                }
                node = doc.getElementsByTagName("user").item(i);
                if (node != null) {
                    review.setUser(node.getTextContent());
                }
                node = doc.getElementsByTagName("title").item(i);
                if (node != null) {
                    review.setTitle(node.getTextContent());
                }
                node = doc.getElementsByTagName("review").item(i);
                if (node != null) {
                    review.setContent(node.getTextContent());
                }
                node = doc.getElementsByTagName("label").item(i);
                if (node != null) {
                    review.setTitle(node.getTextContent());
                }
                reviews.add(review);
            }
            for (Review r : reviews) {
                System.out.println(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reviews;
    }
}
