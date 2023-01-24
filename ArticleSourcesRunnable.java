package com.app.newsaggregator.ArticleViewPager;

import android.os.Build;
import android.util.Log;
import androidx.annotation.RequiresApi;
import com.app.newsaggregator.MainActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class ArticleSourcesRunnable implements Runnable {

    private static final String TAG = "WeatherDownloadRunnable";

    private final MainActivity mainActivity;
    private final String id;

    private static final String newsURL = "https://newsapi.org/v2/top-headlines";


    public ArticleSourcesRunnable(MainActivity mainActivity, String id) {
        this.mainActivity = mainActivity;
        this.id = id;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(newsURL+"?sources="+id+"&apiKey=9a19e15b6aac4ab9a9c799b43cd82b27");

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("User-Agent","");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.connect();

            if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                handleResults(null);
                return;
            }

            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "doInBackground: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            handleResults(null);
            return;
        }
        handleResults(sb.toString());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void handleResults(final String jsonString) {
        ArrayList<Articles> articles = new ArrayList<>();
        articles.clear();
        int count  = 0;

        try {
            JSONObject data = new JSONObject(jsonString);
            JSONArray sources = data.getJSONArray("articles");
            count = data.getInt("totalResults");
            for (int i = 0; i < sources.length(); i++) {
                JSONObject jo_inside = sources.getJSONObject(i);
                articles.add(new Articles(
                        jo_inside.getString("author"),
                        jo_inside.getString("title"),
                        jo_inside.getString("description"),
                        jo_inside.getString("url"),
                        jo_inside.getString("urlToImage"),
                        jo_inside.getString("publishedAt")
                ));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        int finalCount = count;
        mainActivity.runOnUiThread(() -> mainActivity.acceptResults(articles, finalCount));
    }

}
