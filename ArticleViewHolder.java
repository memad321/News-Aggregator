package com.app.newsaggregator.ArticleViewPager;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.newsaggregator.R;

public class ArticleViewHolder extends RecyclerView.ViewHolder {

    TextView txtHeading,txtDate,txtAuthor,txtArticle,txtCounter;
    ImageView imgView;

    public ArticleViewHolder(@NonNull View itemView) {
        super(itemView);
        txtHeading = itemView.findViewById(R.id.txtHeading);
        txtDate = itemView.findViewById(R.id.txtDate);
        txtAuthor = itemView.findViewById(R.id.txtAuthor);
        txtArticle = itemView.findViewById(R.id.txtArticle);
        txtCounter = itemView.findViewById(R.id.txtCounter);
        imgView = itemView.findViewById(R.id.imgView);
    }
}
