package com.example.practice1_5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

public class Top10 extends AppCompatActivity {
    private new_Adapter mAdapter;
    ListView lv;
    String[] t10;
    ArrayList<String> top10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top10);
        Bundle intent = getIntent().getExtras();
        t10 = new String[10];
        top10 = new ArrayList<>();
        t10 = intent.getStringArray("t");
        for(int i = 0; i < t10.length; i++)
            top10.add(i, t10[i]);
        while(top10.remove(null));
        lv = (ListView) findViewById(R.id.lv);
        mAdapter = new new_Adapter(this);
        lv.setAdapter(mAdapter);
    }

    private class new_Adapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;

        new_Adapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return top10.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {
            public ImageView imageView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = mLayoutInflater.inflate(R.layout.item_top10, null);

            new DownloadImageTask((ImageView) convertView.findViewById(R.id.image))
                    .execute(top10.get(position));

            return convertView;
        }
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
