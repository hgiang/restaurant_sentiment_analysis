
import ir.sentiment.classfier.ReviewWriter;
import ir.sentiment.extractor.ReviewReader;
import ir.sentiment.model.Review;
import java.util.ArrayList;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Do
 */
public class Refine {
    public static void main(String args[]){
        ReviewReader.preprocess("ouput.xml");
    }
}
