package com.apps40in.rosberry.test.newsapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.apps40in.rosberry.test.newsapp.adapters.NewsListAdapter;
import com.apps40in.rosberry.test.newsapp.managers.NewsArrayManager;
import com.apps40in.rosberry.test.newsapp.managers.NewsCacheArrayManager;
import com.apps40in.rosberry.test.newsapp.utils.SocialSharing;
import com.sromku.simple.fb.SimpleFacebook;

import java.io.File;
import java.util.ArrayList;


public class NewsListActivity extends ActionBarActivity {

    private final String TAG = "!!! TAG !!!";
    private File directory;

    private SimpleFacebook mSimpleFacebook;
    private SocialSharing social;
    private Intent intent;
    private ListView mainList;
    private BaseAdapter adapter;
    private NewsArrayManager newsManager;
    private NewsCacheArrayManager cacheManager;
    private String user_name;
    private String url = "http://www.google.com/uds/GnewsSearch?q=Google&v=1.0&rsz=8";
    private ArrayList<NewsItem> currentItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        intent = getIntent();
        user_name = intent.getStringExtra("name");
        mainList = (ListView) findViewById(R.id.newsListView);

        createDirectory();

        if (checkNetwork(this)) {
            onlineData();
        } else {
            offlineData();
            Toast.makeText(this, "Check your network connection", Toast.LENGTH_LONG).show();
        }
        setListeners();

    }

    private void onlineData() {
        cacheManager = new NewsCacheArrayManager(this, user_name);
        if (intent.getExtras().getBoolean("newUser")) {
            cacheManager.createNewCache();
        }
        newsManager = new NewsArrayManager(url);
        currentItems = newsManager.getNewsList();
        adapter = new NewsListAdapter(this, currentItems, true);
        mainList.setAdapter(adapter);
        social = new SocialSharing(this);

    }

    private void offlineData() {
        if (!intent.getExtras().getBoolean("newUser")) {
            cacheManager = new NewsCacheArrayManager(this, user_name);
            adapter = new NewsListAdapter(this, cacheManager.getNewsList(), false);
            mainList.setAdapter(adapter);
            Toast.makeText(getApplicationContext(), "Cache data loaded.", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(), "You are new here. No any cache data.", Toast.LENGTH_LONG).show();
        }
    }

    private void setListeners() {
        mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (checkNetwork(getApplicationContext())) {
                    String url = newsManager.getNewsList().get(position).getSource_url();

                    if (!url.startsWith("http://") && !url.startsWith("https://"))
                        url = "http://" + url;

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "You must be online", Toast.LENGTH_LONG).show();
                }
            }
        });

        mainList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (checkNetwork(getApplicationContext())) {
                    longClickDialog(position).show();
                }
                return false;
            }
        });
    }

    private AlertDialog longClickDialog(final int pos) {
        final CharSequence[] items = {"Save this item", "Share with Facebook", "Share with Twitter",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose...");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Save this item")) {
                    cacheManager.saveItem(currentItems.get(pos).getTitle(), currentItems.get(pos).getImage(), currentItems.get(pos).getContent());

                } else if (items[item].equals("Share with Facebook")) {
                    social.postFacebook(currentItems.get(pos),mSimpleFacebook);

                } else if (items[item].equals("Share with Twitter")) {
                    social.postTwitter(currentItems.get(pos));

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog dialog = builder.create();
        return dialog;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_show_cache) {
            adapter = new NewsListAdapter(this, cacheManager.getNewsList(), false);
            mainList.setAdapter(adapter);
            ((BaseAdapter) mainList.getAdapter()).notifyDataSetChanged();
            return true;
        } else {
            if (id == R.id.action_clear_cache) {
                cacheManager.clearCacheDAta();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createDirectory() {
        directory = new File(
                Environment
                        .getExternalStorageDirectory(), "NewsApp/pics/" +
                user_name
        );
        if (!directory.exists())
            directory.mkdirs();
    }

    public static  boolean checkNetwork(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        if (i == null)
            return false;
        if (!i.isConnected())
            return false;
        if (!i.isAvailable())
            return false;
        return true;
    }
}
