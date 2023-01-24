package com.app.newsaggregator.NewsApi;

import android.os.Build;
import android.util.Log;
import androidx.annotation.RequiresApi;
import com.app.newsaggregator.MainActivity;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class NewsSourcesRunnable implements Runnable {

    private static final String TAG = "WeatherDownloadRunnable";

    private final MainActivity mainActivity;

    private static final String newsURL = "https://newsapi.org/v2/";


    public NewsSourcesRunnable(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(newsURL+"sources?apiKey=9a19e15b6aac4ab9a9c799b43cd82b27");

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
        mainActivity.runOnUiThread(() -> mainActivity.createDrawer(jsonString));
    }

}
