package com.app.newsaggregator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.app.newsaggregator.ArticleViewPager.ArticleAdapter;
import com.app.newsaggregator.ArticleViewPager.ArticleSourcesRunnable;
import com.app.newsaggregator.ArticleViewPager.Articles;
import com.app.newsaggregator.NewsApi.NewsSourcesRunnable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private Menu menu;
    ArrayList<String> country_name = new ArrayList<>();
    ArrayList<String> country_code = new ArrayList<>();
    ArrayList<String> language_name = new ArrayList<>();
    ArrayList<String> language_code = new ArrayList<>();
    ArrayList<String> items = new ArrayList<>();
    ArrayList<String> itemsId = new ArrayList<>();
    ArrayList<String> topic = new ArrayList<>();
    ArrayList<String> topicColors = new ArrayList<>();
    ArrayList<String> country = new ArrayList<>();
    ArrayList<String> language = new ArrayList<>();
    JSONObject full;
    JSONObject current;
    String selectedTopic = "all", selectedCountry = "all", selectedLanguage = "all";
    String selectedArticleId = "";
    String selectedArticle = "";

    ArticleAdapter articleAdapter;
    ArrayList<Articles> articlesList = new ArrayList<Articles>();
    ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewInilization();
        getData();
    }

    public void viewInilization(){
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset("country"));
            JSONArray m_jArry = obj.getJSONArray("countries");

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                country_code.add(jo_inside.getString("code").toLowerCase());
                country_name.add(jo_inside.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset("language"));
            JSONArray m_jArry = obj.getJSONArray("languages");

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                language_code.add(jo_inside.getString("code").toLowerCase());
                language_name.add(jo_inside.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.drawer_open,
                R.string.drawer_close
        );

        viewPager = findViewById(R.id.viewPager);
        articleAdapter = new ArticleAdapter(this,articlesList);
        viewPager.setAdapter(articleAdapter);
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    public void getData(){
        NewsSourcesRunnable loaderTaskRunnable = new NewsSourcesRunnable(MainActivity.this);
        new Thread(loaderTaskRunnable).start();
    }

    public void createDrawer(String jsonString){
        // Make sample items for the drawer list
        try {
            full = new JSONObject(jsonString);
            current = new JSONObject(jsonString);
            JSONArray sources = full.getJSONArray("sources");

            for (int i = 0; i < sources.length(); i++) {
                JSONObject jo_inside = sources.getJSONObject(i);
                items.add(jo_inside.getString("name"));
                itemsId.add(jo_inside.getString("id"));
                topic.add(jo_inside.getString("category"));
                language.add(jo_inside.getString("language"));
                country.add(jo_inside.getString("country"));
            }



            HashSet<String> hashSet = new HashSet<String>();
            hashSet.addAll(topic);
            topic.clear();
            topic.add("all");
            topic.addAll(hashSet);
            Collections.sort(topic);

            for(int i=0;i<topic.size();i++){
                Random rnd = new Random();
                topicColors.add(rnd.nextInt(256)+","+ rnd.nextInt(256)+","+rnd.nextInt(256));
            }

            hashSet.clear();
            hashSet.addAll(language);
            language.clear();
            language.addAll(hashSet);
            Collections.sort(language);

            hashSet.clear();
            hashSet.addAll(country);
            country.clear();
            country.addAll(hashSet);
            Collections.sort(country);



        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, items));

        mDrawerList.setOnItemClickListener(
                (parent, view, position, id) -> selectItem(position)
        );

        setTitle("News Gateway ("+items.size()+")");

        SubMenu subm = menu.getItem(0).getSubMenu();
        subm.clear();

        for(int i=0;i<topic.size();i++){
            subm.add(0, i, i,topic.get(i));
        }


        subm = menu.getItem(1).getSubMenu();
        subm.clear();

        subm.add(1, 1, 1,"all");
        for(int i=0;i<country.size();i++){
            if(country_code.contains(country.get(i)))
                subm.add(1, i+1, i+1,country_name.get(country_code.indexOf(country.get(i))));
        }

        subm = menu.getItem(2).getSubMenu();
        subm.clear();

        subm.add(2, 1, 1,"all");
        for(int i=0;i<language.size();i++){
            if(language_code.contains(language.get(i)))
                subm.add(2, i+1, i+1,language_name.get(language_code.indexOf(language.get(i))));
        }

        updateDrawer();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        this.menu = menu;
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Important!
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }
        else {
            if(!item.hasSubMenu()) {
                if(item.getGroupId() == 0){
                    selectedTopic = item.getTitle().toString();
                    updateDrawer();
                }
                else  if(item.getGroupId() == 1){
                    if(!item.getTitle().toString().equalsIgnoreCase("all")) {
                        selectedCountry = country_code.get(country_name.indexOf(item.getTitle().toString()));
                    }
                    else{
                        selectedCountry = "all";
                    }
                    updateDrawer();
                }
                else  if(item.getGroupId() == 2){
                    if(!item.getTitle().toString().equalsIgnoreCase("all")) {
                        selectedLanguage = language_code.get(language_name.indexOf(item.getTitle().toString()));
                    }
                    else{
                        selectedLanguage = "all";
                    }
                    updateDrawer();
                }
            }
            return super.onOptionsItemSelected(item);
        }

    }

    private void selectItem(int position) {
        mDrawerLayout.closeDrawer(mDrawerList);
        setTitle(items.get(position));
        selectedArticle = items.get(position);
        selectedArticleId = itemsId.get(position);

        ArticleSourcesRunnable loaderTaskRunnable = new ArticleSourcesRunnable(MainActivity.this,itemsId.get(position));
        new Thread(loaderTaskRunnable).start();
    }

    public String loadJSONFromAsset(String name) {
        if(name.equalsIgnoreCase("country")) {
            String json = null;
            try {
                InputStream is = getResources().openRawResource(R.raw.country_codes);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
            return json;
        }
        else{
            String json = null;
            try {
                InputStream is = getResources().openRawResource(R.raw.language_codes);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
            return json;
        }
    }

    public void updateDrawer(){
        items.clear();
        itemsId.clear();
        try {
            current = full;
            JSONArray sources = current.getJSONArray("sources");
            for (int i = 0; i < sources.length(); i++) {
                JSONObject jo_inside = sources.getJSONObject(i);
                if(selectedTopic.equalsIgnoreCase("all")){
                    if(selectedLanguage.equalsIgnoreCase("all")){
                        if(selectedCountry.equalsIgnoreCase("all")){
                            items.add(jo_inside.getString("name"));
                            itemsId.add(jo_inside.getString("id"));
                        }
                        else{
                            if(jo_inside.getString("country").equalsIgnoreCase(selectedCountry)){
                                items.add(jo_inside.getString("name"));
                                itemsId.add(jo_inside.getString("id"));
                            }
                        }
                    }
                    else{
                        if(jo_inside.getString("language").equalsIgnoreCase(selectedLanguage)){
                            if(selectedCountry.equalsIgnoreCase("all")){
                                items.add(jo_inside.getString("name"));
                                itemsId.add(jo_inside.getString("id"));
                            }
                            else{
                                if(jo_inside.getString("country").equalsIgnoreCase(selectedCountry)){
                                    items.add(jo_inside.getString("name"));
                                    itemsId.add(jo_inside.getString("id"));
                                }
                            }
                        }
                    }
                }
                else{
                    if(jo_inside.getString("category").equalsIgnoreCase(selectedTopic)){
                        if(selectedLanguage.equalsIgnoreCase("all")){
                            if(selectedCountry.equalsIgnoreCase("all")){
                                items.add(jo_inside.getString("name"));
                                itemsId.add(jo_inside.getString("id"));
                            }
                            else{
                                if(jo_inside.getString("country").equalsIgnoreCase(selectedCountry)){
                                    items.add(jo_inside.getString("name"));
                                    itemsId.add(jo_inside.getString("id"));
                                }
                            }
                        }
                        else{
                            if(jo_inside.getString("language").equalsIgnoreCase(selectedLanguage)){
                                if(selectedCountry.equalsIgnoreCase("all")){
                                    items.add(jo_inside.getString("name"));
                                    itemsId.add(jo_inside.getString("id"));
                                }
                                else{
                                    if(jo_inside.getString("country").equalsIgnoreCase(selectedCountry)){
                                        items.add(jo_inside.getString("name"));
                                        itemsId.add(jo_inside.getString("id"));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (JSONException e) {
            Log.d("UpdateDrawer",e.getMessage());
            e.printStackTrace();
        }


        if(items.size()<=0){
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Warning!");
            alert.setMessage("No news source matching the topic,country or language exists");
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                }
            });
            alert.show();
        }

        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, items));

        setTitle("News Gateway ("+items.size()+")");

        mDrawerList.setOnItemClickListener(
                (parent, view, position, id) -> selectItem(position)
        );
    }

    public void acceptResults(ArrayList<Articles> articlesList,int count) {
        if (articlesList == null) {
            Toast.makeText(this, "Data loader failed", Toast.LENGTH_LONG).show();
        } else {
            this.articlesList.clear();
            this.articlesList.addAll(articlesList);
            articleAdapter.notifyDataSetChanged();
            viewPager.setAdapter(articleAdapter);
            setTitle(selectedArticle);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putString("TOPIC", selectedTopic);
        outState.putString("LANGUAGE", selectedLanguage);
        outState.putString("COUNTRY", selectedCountry);
        outState.putString("ARTICLE", selectedArticle);
        outState.putString("ARTICLEID", selectedArticleId);

        // Call super last
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        // Call super first
        super.onRestoreInstanceState(savedInstanceState);

        selectedTopic = savedInstanceState.getString("TOPIC");
        selectedCountry = savedInstanceState.getString("LANGUAGE");
        selectedLanguage = savedInstanceState.getString("COUNTRY");
        selectedArticle = savedInstanceState.getString("ARTICLE");
        selectedArticleId = savedInstanceState.getString("ARTICLEID");

        if(!TextUtils.isEmpty(selectedArticle)){
            ArticleSourcesRunnable loaderTaskRunnable = new ArticleSourcesRunnable(MainActivity.this,selectedArticleId);
            new Thread(loaderTaskRunnable).start();
        }

    }

}