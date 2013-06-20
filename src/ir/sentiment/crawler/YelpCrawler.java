/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.sentiment.crawler;

import ir.sentiment.model.Review;
import java.util.ArrayList;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Do
 */
public class YelpCrawler extends WebCrawler {

    public static final String url = "http://www.yelp.com/biz/house-of-prime-rib-san-francisco";

    public ArrayList<Review> getAllReviews(String urlPath) {
        ArrayList<Review> reviews = new ArrayList<Review>();
        //do{
        for (int j = 0; j < 10; j++) {
            Document document = getHTMLDocument(urlPath);
            Elements elements = getDivisionByAttribute(document, "itemprop", "review");
            for (int i = 0; i < elements.size(); i++) {
                Review review = extractReview(elements.get(i));
                if (review.getRating() == 5.0 && review.getContent().length() < 300) {
                    continue;
                }
                if (review.getRating() == 4.0) {
                    continue;
                }
                reviews.add(review);
            }
            System.out.println(urlPath);
            try {
                Thread.sleep(10000);
            } catch (Exception e) {
            }
            urlPath = getNextPageUrl(document);
        }
        return reviews;
    }

    private Review extractReview(Element element) {
        Review review = new Review();
        //Url
        Element urlElement = element
                .getElementsByAttributeValue("class", "i-wrap ig-wrap-common i-orange-link-common-wrap").get(0);
        String urlValue = urlElement.attr("href");
        review.setUrl(urlValue);
        //Rating
        Element ratingElement = element
                .getElementsByAttributeValue("itemprop", "ratingValue").get(0);
        String ratingValue = ratingElement.attr("content");
        review.setRating(Double.parseDouble(ratingValue));
        //Date
        Element dateElement = element
                .getElementsByAttributeValue("itemprop", "datePublished").get(0);
        String dateValue = dateElement.attr("content");
        review.setDate(dateValue);
        //User
        Element userElement = element
                .getElementsByAttributeValue("class", "user-name").get(0);
        String userUrl = userElement.child(0).attr("href");
        review.setUser(userUrl);
        //Content
        String content = element
                .getElementsByAttributeValue("itemprop", "description").text();
        review.setContent(content);
        return review;
    }

    public String getNextPageUrl(Document document) {
        String nextUrl = new String();
        Element element = getDivisionById(document, "paginationControls");
        Element curPage = element.getElementsByAttributeValue("class", "highlight2").get(0);
        Element nextPage = curPage.nextElementSibling();
        if (nextPage != null) {
            nextUrl = "http://www.yelp.com" + nextPage.attr("href");
        }
        return nextUrl;
    }

    public static void main(String args[]) {
        YelpCrawler crawler = new YelpCrawler();
        ArrayList<Review> rs = crawler.getAllReviews(url);
        for (Review r : rs) {
            System.out.println(r.getContent().length() + "   " + r);
        }
        XMLDumper.writeXML(rs, "temp.xml");
        /*Document document = crawler.getHTMLDocument("http://www.yelp.com/biz/house-of-prime-rib-san-francisco?start=720");
         String urlPath = crawler.getNextPageUrl(document);
         System.out.println(urlPath);*/
    }
}
