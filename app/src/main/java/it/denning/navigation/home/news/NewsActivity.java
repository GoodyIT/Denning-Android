package it.denning.navigation.home.news;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.general.DISharedPreferences;
import it.denning.general.EndlessRecyclerViewScrollListener;
import it.denning.model.News;
import it.denning.navigation.home.util.AdsViwerActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.utils.OnItemClickListener;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.KeyboardUtils;

public class NewsActivity extends BaseActivity  implements OnItemClickListener {
    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;
    @BindView(R.id.recycler_list)
    protected RecyclerView recyclerView;
    @BindView(R.id.news_imageview)
    ImageView headerImage;
    @BindView(R.id.news_title)
    TextView newsTitle;
    @BindView(R.id.news_content)
    TextView newsContent;
    @BindView(R.id.news_date)
    TextView newsPublishedDate;

    @OnClick(R.id.back_btn)
    void onBack() {
        KeyboardUtils.hideKeyboard(this);
        finish();
    }

    private NewsAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private int page = 1;
    private String type = "news";

    @Override
    protected int getContentResId() {
        return R.layout.activity_news;
    }

    public static void start(Context context, String type) {
        Intent intent = new Intent(context, NewsActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();
        displayHeader();
    }

    private void initFields() {
        toolbarTitle.setText(getString(R.string.news_title));
        type = getIntent().getStringExtra("type");
        setupRecyclerView();

        setupEndlessScroll();
    }

    private void displayHeader() {
        if (DISharedPreferences.news.isEmpty()) {
            return;
        }
        News headerNews = DISharedPreferences.news.get(0);

        headerImage.setImageBitmap(headerNews.getImage());
        newsTitle.setText(headerNews.title);
        newsContent.setText(headerNews.shortDescription);
        newsPublishedDate.setText(DIHelper.convertToSimpleDateFormat(headerNews.theDateTime));
    }

    private void setupRecyclerView() {
        adapter = new NewsAdapter(new ArrayList<News>(), this);
        linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.item_decorator)));

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.updateAdapter(DISharedPreferences.news);
    }

    private void setupEndlessScroll() {
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNews();
            }
        };
        recyclerView.clearOnScrollListeners();
        recyclerView.addOnScrollListener(scrollListener);
    }

    private void loadNews() {
        showActionBarProgress();
        String url = DIConstants.NEWS_LATEST_URL;
        if (type.equals("updates")) {
            url = DIConstants.UPDATES_LATEST_URL;
        }
        NetworkManager.getInstance().sendPublicGetRequest( url + "?page=" + page, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                parseNews(jsonElement.getAsJsonArray());
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                hideActionBarProgress();
                showSnackbar(error, Snackbar.LENGTH_SHORT);
            }
        });
    }

    private void parseNews(JsonArray jsonArray) {
        hideActionBarProgress();
        it.denning.model.News[] news = new Gson().fromJson(jsonArray, it.denning.model.News[].class);
        if (news.length > 0) {
            page++;
        }

        adapter.updateAdapter(Arrays.asList(news));
    }

    @Override
    public void onClick(View view, int position) {
        News news = adapter.getModels().get(position);
        AdsViwerActivity.start(this, news.URL);
    }
}
