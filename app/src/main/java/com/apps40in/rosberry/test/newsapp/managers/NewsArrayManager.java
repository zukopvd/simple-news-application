package com.apps40in.rosberry.test.newsapp.managers;

import com.apps40in.rosberry.test.newsapp.items.NewsItem;
import com.apps40in.rosberry.test.newsapp.utils.NewsJSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by sergeysorokin on 7/3/14.
 */
public class NewsArrayManager {

    private ArrayList<NewsItem> newsList;
    private JSONArray jsonArray;
    private String url;
    private NewsJSONParser parser;
    private NewsItem newsItem;

    public NewsArrayManager(String url) {
        this.url = url;
        init();
    }

    private void init() {
        newsList = new ArrayList<NewsItem>();
        parser = new NewsJSONParser();
        newsLoading();
    }

    private void newsLoading() {

        String img = null;

        try {
            jsonArray = parser.getJSONFromUrl(url);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = null;
            try {
                jsonObject = jsonArray.getJSONObject(i);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                try {
                    img = jsonObject.getJSONObject("image").getString("url");

                } catch (JSONException e) {
                    img = null;
                }
                
                newsItem = new NewsItem(jsonObject.getString("title"), jsonObject.getString("content"), img, jsonObject.getString("unescapedUrl"));
                if (img == null){
                    newsItem.setImageExist(false);
                }
                newsList.add(newsItem);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public ArrayList<NewsItem> getNewsList() {
        return newsList;
    }
}
