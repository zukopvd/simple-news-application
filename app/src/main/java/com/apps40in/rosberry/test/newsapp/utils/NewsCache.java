package com.apps40in.rosberry.test.newsapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.apps40in.rosberry.test.newsapp.items.NewsItem;

import java.util.ArrayList;

/**
 * Created by sergeysorokin on 7/4/14.
 */
public class NewsCache {

    private String user;
    private Context context;
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    public NewsCache(String user, Context context) {
        this.user = user;
        this.context = context;
        dbHelper = new DBHelper(context);
    }

    public void updateDb(String title, String image, String content) {

        db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("title", title);
        cv.put("image", image);
        cv.put("content", content);
        db.insert(user, null, cv);

        dbHelper.close();
    }

    public ArrayList<NewsItem> readDb(){
        ArrayList<NewsItem> newsList = new ArrayList<NewsItem>();
        db = dbHelper.getReadableDatabase();

        String[] columns = new String[] { "title", "image", "content" };
        Cursor c = db.query(user, columns, null, null, null, null, "id");

        if (c.moveToLast()) {
            int titleColIndex = c.getColumnIndex("title");
            int imageColIndex = c.getColumnIndex("image");
            int contentColIndex = c.getColumnIndex("content");

            do {
                NewsItem item = new NewsItem(c.getString(titleColIndex),c.getString(contentColIndex),c.getString(imageColIndex), null);
                if (item.getImage()==null){
                    item.setImageExist(false);
                }
                newsList.add(item);
            } while (c.moveToPrevious());
        } else
            Log.d("LOG_TAG", "0 rows");
        c.close();

        dbHelper.close();
        return newsList;

    }

    public void clearCache(){
        db = dbHelper.getWritableDatabase();
        db.delete(user, null, null);
        dbHelper.close();
    }

    public void createNewTable(){
        db = dbHelper.getReadableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS " + user + " ("
                + "id integer primary key autoincrement," + "title text,"
                + "image text," + "content text" + ");");
        dbHelper.close();
    }

    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, "myDB", null, 1);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + user + " ("
                    + "id integer primary key autoincrement," + "title text,"
                    + "image text," + "content text" + ");");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
