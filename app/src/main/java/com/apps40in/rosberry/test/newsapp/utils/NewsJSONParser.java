package com.apps40in.rosberry.test.newsapp.utils;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;

/**
 * Created by zuko on 02.07.2014.
 */
public class NewsJSONParser {

    private InputStream is = null;
    private JSONObject jObj = null;
    private String json = "";

    public NewsJSONParser() {
    }

    public JSONArray getJSONFromUrl(final String url) throws ExecutionException, InterruptedException {
        JSONObject jsonObject = null;
        try {
            jsonObject = (new GetListAsync().execute(url).get()).getJSONObject("responseData");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray jsonArr = null;
        try {
            jsonArr = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArr;
    }

    private class GetListAsync extends AsyncTask<String, Void, JSONObject> {

        public JSONObject doInBackground(String... urls) {
            String u = urls[0];
            try {
                DefaultHttpClient httpClient = new DefaultHttpClient();

                HttpGet httpGet = new HttpGet(u);

                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                is = httpEntity.getContent();
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    is.close();
                    json = sb.toString();
                } catch(Exception e) {
                    return null;
                }
                try {
                    jObj = new JSONObject(json);
                } catch(JSONException e) {
                    return null;
                }
                return jObj;

            } catch (Exception e) {
                return null;
            }
        }

        public void onPostExecute(String srt0) {
        }
    }
}
