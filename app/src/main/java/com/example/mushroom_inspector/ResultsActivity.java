package com.example.mushroom_inspector;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {
    static final String MATCHED_SPECIES = "MATCHED_SPECIES";

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ShroomResultsAdapter madapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Bundle extras = getIntent().getExtras();

        recyclerView = (RecyclerView) findViewById(R.id.ResultsRecyclerView);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        List<String> mylist = new ArrayList<>();
        if (extras != null) {
            String[] mushroomSpecies = extras.getStringArray(MATCHED_SPECIES);
            mylist = Arrays.asList(mushroomSpecies);
        }

        madapter = new ShroomResultsAdapter(mylist);
        recyclerView.setAdapter(madapter);
    }
}