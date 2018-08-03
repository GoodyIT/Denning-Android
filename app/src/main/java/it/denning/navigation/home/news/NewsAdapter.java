package it.denning.navigation.home.news;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.general.DIHelper;
import it.denning.model.Event;
import it.denning.model.News;
import it.denning.search.utils.OnItemClickListener;

/**
 * Created by denningit on 18/04/2017.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    List<News> newsList;
    private final OnItemClickListener itemClickListener;

    public NewsAdapter(List<News> newsList, OnItemClickListener itemClickListener) {
        this.newsList = newsList;
        this.itemClickListener = itemClickListener;
    }

    public List<News> getModels() {
        return newsList;
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.cardview_news, parent, false);

        return new NewsAdapter.NewsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, final int position) {
        final int index = position + 1;
        News news = this.newsList.get(index);
        holder.newsImage.setImageBitmap(news.getImage());
        holder.newsContent.setText(news.shortDescription);
        holder.newsDateTime.setText(DIHelper.convertToSimpleDateFormat(news.theDateTime));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, index);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size()-1;
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        protected @BindView(R.id.news_image) ImageView newsImage;
        protected @BindView(R.id.news_content) TextView newsContent;
        protected @BindView(R.id.news_datetime) TextView newsDateTime;
        protected @BindView(R.id.card_view_news)
        CardView cardView;

        public NewsViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public void updateAdapter(List<News> events) {
        this.newsList.addAll(events);
        notifyDataSetChanged();
    }
}
