package com.app.newsaggregator.ArticleViewPager;

public class Articles {
    public String author;
    public String title;
    public String description;
    public String url;
    public String urlToImage;
    public String publishedAt;

    public Articles(){}

    public Articles(String author, String title, String description, String url, String urlToImage, String publishedAt) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
    }

}
