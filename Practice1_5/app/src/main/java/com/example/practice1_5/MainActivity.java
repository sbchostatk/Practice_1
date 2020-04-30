package com.example.practice1_5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ExecutionException;

import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    OkHttpClient client = new OkHttpClient();

    HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.thecatapi.com/v1/images/search").newBuilder()
            .addQueryParameter("limit", "60")
            .addQueryParameter("page", "20")
            .addQueryParameter("order", "Desc");
    String url = urlBuilder.build().toString();
    Request request = new Request.Builder()
            .header("x-api-key", "904e0566-bc1a-4375-8244-a500c264646b")
            .url(url)
            .build();

    ArrayList<String> urls;
    ArrayList<String> breeds;
    Set<String> c;
    ArrayList<String> cats;
    Queue<String> top10;
    private new_Adapter mAdapter;
    ListView lv;
    Spinner cat;
    Button search;
    Button favs;
    String[] top;
    int o = 0;
    AR asyncRequest = new AR();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        urls = new ArrayList<>();
        breeds = new ArrayList<>();

        c = new HashSet<>();
        cats = new ArrayList<>();
        top10 = new LinkedList<>();
        top = new String[10];

        try {
            String t = asyncRequest.execute("").get();
            parse(t);
        } catch (JSONException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        lv = (ListView) findViewById(R.id.lvMain);
        cat = (Spinner) findViewById(R.id.spinner);
        search = (Button) findViewById(R.id.button_send);
        favs = (Button) findViewById(R.id.button);

        mAdapter = new new_Adapter(this, urls);
        lv.setAdapter(mAdapter);
        c.addAll(cats);
        cats.clear();
        cats.add("Show all");
        cats.addAll(c);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cats);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cat.setAdapter(adapter);

        cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                MainActivity.this.o = selectedItemPosition;
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final Intent intent = new Intent(this, Top10.class);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.mAdapter.getFilter().filter(cats.get(o));
            }
        });

        favs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < top10.size(); i++)
                {
                    top[i] = top10.remove();
                    String temp = top[i];
                    top10.add(temp);
                }
                intent.putExtra("t", top);
                startActivity(intent);
            }
        });

    }

    public void parse(String text) throws JSONException {
        JSONArray jsonArray = new JSONArray(text);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject JO = jsonArray.getJSONObject(i);
            JSONArray breds = JO.getJSONArray("breeds");
            JSONObject breed = breds.getJSONObject(0);
            String b = breed.getString("name");
            String u = JO.getString("url");
            urls.add(i, u);
            breeds.add(i, b);
            cats.add(i, b);
        }
    }

    public class AR extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params) {
            try {
                Response response = client.newCall(request).execute();
                String responseData = response.body().string();
                return responseData;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
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

    private class new_Adapter extends BaseAdapter implements Filterable{
        private LayoutInflater mLayoutInflater;

        new_Adapter(Context context , ArrayList<String> cur) {
            mLayoutInflater = LayoutInflater.from(context);
            this.originalData = new ArrayList<String>();
            originalData.addAll(cur);
            this.filteredData = new ArrayList<String>();
            filteredData.addAll(originalData);
        }

        private ArrayList<String> originalData;
        private ArrayList<String> filteredData;

        @Override
        public int getCount() {
            return filteredData.size();
        }

        @Override
        public Object getItem(int position) {
            return filteredData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Filter getFilter() {
            return new Filter()
            {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence)
                {
                    FilterResults results = new FilterResults();

                    if(charSequence == null || charSequence.length() == 0)
                    {
                        results.values = originalData;
                        results.count = originalData.size();
                    }
                    else
                    {
                        ArrayList<String> filterResultsData = new ArrayList<String>();
                        if (charSequence.toString().equals("Show all"))
                        {
                            filterResultsData.addAll(originalData);
                        }
                        else {
                            for (int i = 0; i < breeds.size(); i++) {
                                if (breeds.get(i).equals(charSequence.toString())) {
                                    filterResultsData.add(urls.get(i));
                                }
                            }
                        }

                        results.values = filterResultsData;
                        results.count = filterResultsData.size();
                    }

                    return results;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults)
                {
                    filteredData = (ArrayList<String>)filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }

        public class ViewHolder {
            public ImageView imageView;
            final Button like, dislike;
            ViewHolder(View view){
                like = (Button) view.findViewById(R.id.button1);
                dislike = (Button) view.findViewById(R.id.button2);
                imageView = (ImageView) view.findViewById(R.id.image);
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if(convertView==null){
                convertView = mLayoutInflater.inflate(R.layout.item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }
            else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            new DownloadImageTask((ImageView) convertView.findViewById(R.id.image))
                    .execute(filteredData.get(position));

            viewHolder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.like.setBackgroundColor(0xff66ff00);
                    viewHolder.like.setTextColor(0xff008000);
                    viewHolder.dislike.setBackgroundColor(0xff888888);
                    viewHolder.dislike.setTextColor(0xff444444);

                    if (top10.size() < 10){
                        top10.add(filteredData.get(position));
                    }
                    else
                    {
                        top10.remove();
                        top10.add(filteredData.get(position));
                    }
                    viewHolder.like.setEnabled(false);
                    viewHolder.dislike.setEnabled(false);
                }
            });
            viewHolder.dislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.like.setBackgroundColor(0xff888888);
                    viewHolder.like.setTextColor(0xff444444);
                    viewHolder.dislike.setBackgroundColor(0xffff0000);
                    viewHolder.dislike.setTextColor(0xff800000);
                    viewHolder.like.setEnabled(false);
                    viewHolder.dislike.setEnabled(false);
                }
            });

            viewHolder.like.setBackgroundColor(0xff66ff00);
            viewHolder.like.setTextColor(0xff008000);
            viewHolder.dislike.setBackgroundColor(0xffff0000);
            viewHolder.dislike.setTextColor(0xff800000);

            viewHolder.like.setEnabled(true);
            viewHolder.dislike.setEnabled(true);

            return convertView;
        }
    }


}
