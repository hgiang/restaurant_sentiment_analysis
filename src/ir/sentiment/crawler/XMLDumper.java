/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.sentiment.crawler;

import ir.sentiment.model.Review;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Do
 */
public class XMLDumper {

    public static void writeXML(ArrayList<Review> data, String fileUrl) {
        try {
            File file = new File(fileUrl);
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            int count = 0;
            for (Review review : data) {
                review.setId("" + (++count));
                bw.write(toXMLString(review) + "\n");
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String toXMLString(Review review) {
        String result = new String();
        result += "<doc id='" + review.getId() + "'>\n";
        result += "<stars>" + review.getRating() + "</stars>\n";
        result += "<url>" + review.getUrl() + "</url>\n";
        result += "<date>" + review.getDate() + "</date>\n";
        result += "<user>" + review.getUser() + "</user>\n";
        result += "<review>" + review.getContent() + "</review>\n";
        result += "<length>" + review.getContent().length() + "</length>\n";
        result += "</doc>\n";
        return result;
    }
}
