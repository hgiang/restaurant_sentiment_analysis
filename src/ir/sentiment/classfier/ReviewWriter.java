/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.sentiment.classfier;

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
public class ReviewWriter {

    public static void writeToXML(ArrayList<Review> reviews, double[][] result,
            String fileUrl) {
        try {
            File file = new File(fileUrl);
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);            
            for (int i = 0; i < reviews.size(); i++) {
                Review review = reviews.get(i);
                review.setId("" + (i + 1));
                bw.write(toXMLString(review, result[i][0], result[i][1]) + "\n");
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String toXMLString(Review review, double label, double conf) {
        String result = new String();
        result += "<doc id='" + review.getId() + "'>\n";
        result += "<stars>" + review.getRating() + "</stars>\n";
        result += "<url>" + review.getUrl() + "</url>\n";
        result += "<date>" + review.getDate() + "</date>\n";
        result += "<user>" + review.getUser() + "</user>\n";
        result += "<review>" + review.getContent() + "</review>\n";
        result += "<label>" + getLabel(label) + "</label>\n";
        result += "<confidence>" + refineConf(conf) + "</confidence>\n";
        result += "</doc>\n";
        return result;
    }

    private static String getLabel(double label) {
        if (label == 0) {
            return "-1";
        } else if (label == 1) {
            return "0";
        }
        return "+1";
    }

    private static double refineConf(double conf) {        
        if (conf > 1 - (1E-16) ) {            
            return 0.9999999999999999;
        }
        return conf;
    }
}
