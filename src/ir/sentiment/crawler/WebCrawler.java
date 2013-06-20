/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.sentiment.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.jsoup.select.Elements;

/**
 *
 * @author Do
 */
public class WebCrawler {

    public String getHTMLSource(String urlPath) {
        String content = new String();
        try {
            Document doc = Jsoup.connect(urlPath).userAgent("Mozilla/4.0").timeout(0).get();
            content = doc.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    public Document getHTMLDocument(String urlPath) {
        try {
            Document doc = Jsoup.connect(urlPath).timeout(0).get();
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Elements getDivisionByAttribute(Document document, String tag, String value) {
        Elements elements = document.getElementsByAttributeValue(tag, value);
        return elements;
    }

    public Element getDivisionById(Document document, String id) {
        Element element = document.getElementById(id);
        return element;
    }
}