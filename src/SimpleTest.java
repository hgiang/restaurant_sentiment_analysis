
import ir.sentiment.extractor.ReviewReader;
import ir.sentiment.model.Review;
import java.util.ArrayList;
import java.util.HashSet;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Do
 */
public class SimpleTest {

    public static void main(String args[]) {
        ReviewReader.noOfDocs = 1000;
        ArrayList<Review> reviews = ReviewReader.getReviewInfo("reviews.xml");
        HashSet<String> link = new HashSet<String>();
        for(Review r: reviews){
            System.out.println(r.getRating() + " " + r.getTitle());
            link.add(r.getUrl());
        }
        System.out.println(link.size());
    }
}
