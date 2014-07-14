package com.apps40in.rosberry.test.newsapp.items;

/**
 * Created by sergeysorokin on 7/3/14.
 */
public class NewsItem {
    private String title;
    private String content;
    private String image;
    private String source_url;
    private boolean imageExist=true;


    public NewsItem(String title, String content, String image, String source_url) {
        this.title = title;
        this.content = content;
        this.image = image;
        this.source_url = source_url;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getImage() {
        return image;
    }

    public String getSource_url() {
        return source_url;
    }

    public boolean isImageExist() {
        return imageExist;
    }

    public void setImageExist(boolean imageExist) {
        this.imageExist = imageExist;
    }
}
