package ir.sentiment.model;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Do
 */
public class Review {

    private String id = new String();
    private String url = new String();
    private double rating = 0;
    private String title = new String();
    private String date = new String();
    private String user = new String();
    private String content = new String();

    public Review() {
    }

    public Review(String id) {
        this.id = id;
    }

    public Review(String id, String url, double rating, String date, String user, String content) {
        this.id = id;
        this.url = url;
        this.rating = rating;
        this.date = date;
        this.user = user;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Review{" + "id=" + id + ", url=" + url + ", rating=" + rating
                + ", date=" + date + ", user=" + user + ", content=" + content + '}';
    }
}
