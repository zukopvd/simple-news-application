package com.apps40in.rosberry.test.newsapp.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.widget.Toast;

import com.apps40in.rosberry.test.newsapp.NewsItem;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.entities.Feed;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnPublishListener;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;

/**
 * Created by sergeysorokin on 7/14/14.
 */
public class SocialSharing {

    private SocialAuthAdapter sAAdapter;

    private Context context;

    public SocialSharing(Context context) {
        this.context = context;
    }

    public void postFacebook(NewsItem item, SimpleFacebook mSimpleFacebook) {
        Feed feed = new Feed.Builder()
                .setMessage("Something interesting...")
                .setName(item.getTitle())
                .setCaption("")
                .setDescription(item.getContent())
                .setPicture(item.getImage())
                .setLink(item.getSource_url())
                .build();
        Permission[] permissions = new Permission[]{
                Permission.USER_PHOTOS,
                Permission.EMAIL,
                Permission.PUBLISH_ACTION
        };
        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId("YOUR_APP_ID")
                .setPermissions(permissions)
                .build();
        SimpleFacebook.setConfiguration(configuration);
        //mSimpleFacebook = SimpleFacebook.getInstance(this);

        if (mSimpleFacebook.isLogin()) {
            mSimpleFacebook.publish(feed, true, new OnPublishListener() {
            });
        } else {
            fbLoginDialog(mSimpleFacebook).show();
        }
    }

    private AlertDialog fbLoginDialog(final SimpleFacebook simpleFacebook) {
        final CharSequence[] items = {"LOGIN",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose...");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("LOGIN")) {
                    simpleFacebook.login(new OnLoginListener() {
                        @Override
                        public void onFail(String reason) {
                        }

                        @Override
                        public void onException(Throwable throwable) {
                        }

                        @Override
                        public void onThinking() {
                        }

                        @Override
                        public void onLogin() {
                        }

                        @Override
                        public void onNotAcceptingPermissions(Permission.Type type) {
                        }
                    });
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog dialog = builder.create();
        return dialog;
    }

    public void postTwitter(NewsItem item) {
        sAAdapter = new SocialAuthAdapter(new ResponseListener(item));
        sAAdapter.authorize(context, SocialAuthAdapter.Provider.TWITTER);
    }

    private final class ResponseListener implements DialogListener {
        private NewsItem item;

        public ResponseListener(NewsItem item) {
            this.item = item;
        }

        public void onComplete(Bundle values) {
            sAAdapter.updateStatus(Html.fromHtml(item.getTitle()).toString(), new MessageListener(), false);
        }

        @Override
        public void onError(SocialAuthError socialAuthError) {
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onBack() {
        }
    }

    private final class MessageListener implements SocialAuthListener<Integer> {

        @Override
        public void onExecute(String s, Integer t) {

            Integer status = t;
            if (status.intValue() == 200 || status.intValue() == 201 || status.intValue() == 204) {
                Toast.makeText(context, "Message posted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Message not posted", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onError(SocialAuthError socialAuthError) {
        }
    }
}
