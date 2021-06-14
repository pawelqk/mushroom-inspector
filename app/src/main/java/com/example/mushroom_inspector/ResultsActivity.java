package com.example.mushroom_inspector;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ResultsActivity extends AppCompatActivity implements View.OnClickListener {
    static final String MATCHED_SPECIES = "MATCHED_SPECIES";

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ShroomResultsAdapter madapter;
    private List<Result> results = new ArrayList();

    class SpeciesData {
        String edibility;
        Optional<String> url;

        public SpeciesData(String edibility, Optional<String> url) {
            this.edibility = edibility;
            this.url = url;
        }
    }

    class Result {
        String speciesWithConfidence;
        SpeciesData data;

        public Result(String speciesWithConfidence, SpeciesData data) {
            this.speciesWithConfidence = speciesWithConfidence;
            this.data = data;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

        recyclerView = (RecyclerView) findViewById(R.id.ResultsRecyclerView);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        List<String> speciesWithConfidences = Arrays.asList(extras.getStringArray(MATCHED_SPECIES));
        fillResults(speciesWithConfidences);

        madapter = new ShroomResultsAdapter(results, this::onClick);
        recyclerView.setAdapter(madapter);
    }

    @Override
    public void onClick(final View view) {
        int itemPosition = recyclerView.getChildLayoutPosition(view);
        Optional<String> url = results.get(itemPosition).data.url;
        if (!url.isPresent()) {
            Toast.makeText(this, "URL for this species was not found", Toast.LENGTH_LONG).show();
            return;
        }

        Intent webViewActivity = new Intent(this, WebViewActivity.class);
        webViewActivity.putExtra(WebViewActivity.URL, url.get());
        startActivity(webViewActivity);
    }

    private void fillResults(List<String> speciesWithConfidences) {
        try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(getAssets().open("mushrooms_data.csv")))) {
            String row;
            while ((row = csvReader.readLine()) != null) {
                String[] data = row.trim().split(",");
                String name = data[0].replace("\"", "");
                String url = data[2].replace("\"", "");;
                String edibility = data[3].replace("\"", "");;
                for (String speciesWithConfidence : speciesWithConfidences) {
                    if (name.equals(extractSpeciesName(speciesWithConfidence))) {
                        Result result = new Result(speciesWithConfidence, new SpeciesData(edibility, Optional.of(url)));
                        results.add(result);
                        break;
                    }
                }

            }
        } catch (IOException e) {
            Log.i("ResultsActivity", e.getMessage());
            return;
        }

        fillUnknownResults(speciesWithConfidences);
    }

    private void fillUnknownResults(List<String> speciesWithConfidences) {
        for (String speciesWithConfidence : speciesWithConfidences) {
            boolean found = false;
            for (Result result : results) {
                if (result.speciesWithConfidence.equals(speciesWithConfidence)) {
                    found = true;
                }
            }

            if (!found) {
                Result result = new Result(speciesWithConfidence, new SpeciesData("Edibility unknown", Optional.empty()));
                results.add(result);
            }
        }
    }

    private String extractSpeciesName(String speciesWithConfidence) {
        return speciesWithConfidence.substring(0, speciesWithConfidence.indexOf(" ("));
    }
}