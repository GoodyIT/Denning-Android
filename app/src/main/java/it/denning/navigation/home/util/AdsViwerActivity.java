package it.denning.navigation.home.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.auth.branch.FirmBranchActivity;

/**
 * Created by denningit on 2017-12-09.
 */

public class AdsViwerActivity extends AppCompatActivity {
    @BindView(R.id.document_webview)
    WebView webView;

    public static void start(Context context, String url) {
        Intent myIntent = new Intent(context, AdsViwerActivity.class);
        myIntent.putExtra("url", url);
        context.startActivity(myIntent);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_viewer);
        ButterKnife.bind(this);

        String url = getIntent().getStringExtra("url");

        webView.loadUrl(url);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }
}
