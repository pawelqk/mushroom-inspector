package com.example.mushroom_inspector;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends AppCompatActivity {
    static final String URL = "URL";

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Bundle extras = getIntent().getExtras();

        webView = findViewById(R.id.WebView);

        String url = (String) extras.get(URL);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http:" + url);
    }
}