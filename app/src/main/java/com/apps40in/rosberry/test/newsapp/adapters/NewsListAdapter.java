package com.apps40in.rosberry.test.newsapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.Html;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.apps40in.rosberry.test.newsapp.items.NewsItem;
import com.apps40in.rosberry.test.newsapp.R;
import com.apps40in.rosberry.test.newsapp.utils.ImageUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by sergeysorokin on 7/3/14.
 */
public class NewsListAdapter extends BaseAdapter {
    private Context context;
    private AQuery aq;
    private ArrayList<NewsItem> news;
    private boolean isOnline;

    public NewsListAdapter(Context context, ArrayList<NewsItem> news, boolean isOnline) {
        this.context = context;
        this.news = news;
        this.isOnline = isOnline;
        aq = new AQuery(context);
    }

    @Override
    public int getCount() {
        return news.size();
    }

    @Override
    public Object getItem(int position) {
        return news.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item, parent, false);

        rowView.setBackgroundResource(R.drawable.lv_selector);

        TextView title = (TextView) rowView.findViewById(R.id.itemTitle);
        TextView content = (TextView) rowView.findViewById(R.id.itemContent);
        ImageView image = (ImageView) rowView.findViewById(R.id.itemImg);
        title.setText(Html.fromHtml(news.get(position).getTitle()).toString());

        if (news.get(position).isImageExist()) {
            int imgSize = (int) context.getResources().getDimension(R.dimen.item_image_size);
            if (isOnline) {
                aq.id(image).image(news.get(position).getImage(), true, true, 0, AQuery.INVISIBLE);
            } else {
                aq.id(image).image(new File(news.get(position).getImage()), imgSize);
            }
            String text = Html.fromHtml(news.get(position).getContent()).toString();

            int leftMargin = imgSize + 10;
            int imgDp = (int) (context.getResources().getDimension(R.dimen.item_image_size) / context.getResources().getDisplayMetrics().density);
            SpannableString ss = new SpannableString(text);
            ss.setSpan(new AdapterItemTextSpan(imgDp / 16, leftMargin), 0, ss.length(), 0);

            content.setText(ss);
        } else {
            content.setText(Html.fromHtml(news.get(position).getContent()).toString());
        }
        image.setOnClickListener(imageClick);


        return rowView;
    }

    View.OnClickListener imageClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(context, "Processing...", Toast.LENGTH_SHORT).show();
            ImageView iv1 = (ImageView) v;
            Bitmap img1 = ((BitmapDrawable) iv1.getDrawable()).getBitmap();
            int w = img1.getWidth(), h = img1.getHeight();
            int[] pix = new int[w * h];
            img1.getPixels(pix, 0, w, 0, 0, w, h);

            int[] resultInt = ImageUtils.ImgToGray(pix, w, h);
            Bitmap resultImg = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
            resultImg.setPixels(resultInt, 0, w, 0, 0, w, h);
            iv1.setImageBitmap(resultImg);
        }
    };
}
