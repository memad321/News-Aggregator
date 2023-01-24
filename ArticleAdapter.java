package com.app.newsaggregator.ArticleViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.newsaggregator.MainActivity;
import com.app.newsaggregator.R;

import java.io.InputStream;
import java.util.ArrayList;

public class ArticleAdapter extends
        RecyclerView.Adapter<ArticleViewHolder> {

    private final MainActivity mainActivity;
    private final ArrayList<Articles> articlesList;

    public ArticleAdapter(MainActivity mainActivity, ArrayList<Articles> articlesList) {
        this.mainActivity = mainActivity;
        this.articlesList = articlesList;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArticleViewHolder(
                LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.article_page, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Articles articles = articlesList.get(position);

        if(!TextUtils.isEmpty(articles.urlToImage)) {
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
                InputStream in = new java.net.URL(articles.urlToImage).openStream();
                Bitmap mIcon11 = BitmapFactory.decodeStream(in);
                holder.imgView.setImageBitmap(mIcon11);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("Error",e.getMessage());
                holder.imgView.setImageResource(R.drawable.brokenimage);
            }
        }
        else{
            holder.imgView.setImageResource(R.drawable.noimage);
        }

        holder.txtArticle.setText(articles.description);
        holder.txtAuthor.setText(articles.author);
        holder.txtDate.setText(articles.publishedAt.split("T")[0]);
        holder.txtHeading.setText(articles.title);

        holder.txtCounter.setText((position+1) +" of "+(articlesList.size()));


        holder.txtHeading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = articles.url;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                mainActivity.startActivity(i);
            }
        });

        holder.imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = articles.url;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                mainActivity.startActivity(i);
            }
        });

        holder.txtArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = articles.url;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                mainActivity.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return articlesList.size();
    }
}