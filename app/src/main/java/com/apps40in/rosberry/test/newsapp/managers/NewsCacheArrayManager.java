package com.apps40in.rosberry.test.newsapp.managers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.apps40in.rosberry.test.newsapp.NewsItem;
import com.apps40in.rosberry.test.newsapp.utils.NewsCache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by sergeysorokin on 7/4/14.
 */
public class NewsCacheArrayManager {
    private Context context;
    private String user;
    private ProgressDialog dialog;
    private String local_image = null;
    private NewsCache cache;
    private ArrayList<NewsItem> newsList;

    public NewsCacheArrayManager(Context context, String user) {
        this.context = context;
        this.user = user;
        init();
    }

    private void init() {
        cache = new NewsCache(user, context);
    }

    public ArrayList<NewsItem> getNewsList() {
        return cache.readDb();
    }

    public void saveItem(String title, String image, String content) {
        if (image != null) {
            dialog = ProgressDialog.show(context, "Loading", "Please wait...", true);
            dialog.setCancelable(true);
            local_image = createDstFile();
            new DownloadImage().execute(image);
            cache.updateDb(title, local_image, content);
        }else{
            cache.updateDb(title, image, content);
        }

    }

    private class DownloadImage extends AsyncTask<String, Void, Void> {

        protected Void doInBackground(String... urls) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                input = connection.getInputStream();
                output = new FileOutputStream(local_image);

                byte data[] = new byte[4096];
                int count;
                while ((count = input.read(data)) != -1) {
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }

                    output.write(data, 0, count);
                }
            } catch (Exception e) {
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            dialog.dismiss();
            return null;
        }
    }

    private String createDstFile() {
        File file = null;
        file = new File(Environment.getExternalStorageDirectory(), "NewsApp/pics/" + user + "/" + "photo_"
                + System.currentTimeMillis() + ".jpg");

        return file.getPath();
    }

    public void createNewCache(){
        cache.createNewTable();
    }

    public void clearCacheDAta(){
        ArrayList<NewsItem> items = cache.readDb();
        File delFile;
        for (int i = 0; i<items.size(); i++){
            if (items.get(i).getImage()!=null) {
                delFile = new File(items.get(i).getImage());
                delFile.delete();
            }
        }
        cache.clearCache();
    }

}
