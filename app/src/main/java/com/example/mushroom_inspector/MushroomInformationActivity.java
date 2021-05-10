package com.example.mushroom_inspector;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MushroomInformationActivity extends AppCompatActivity {
    static final String MUSHROOM_SPECIES_NAME = "MUSHROOM_SPECIES_NAME";

    private WebView wikipediaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mushroom_information);
        Bundle extras = getIntent().getExtras();

        wikipediaView = findViewById(R.id.WikipediaView);

        String mushroomSpecies = (String) extras.get(MUSHROOM_SPECIES_NAME);
        wikipediaView.setWebViewClient(new WebViewClient());
        wikipediaView.loadUrl("http://en.m.wikipedia.org/wiki/" + mushroomSpecies);
    }
}