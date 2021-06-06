package com.example.mushroom_inspector;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.mushroom_inspector.env.Logger;
import com.example.mushroom_inspector.tflite.Classifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final Logger LOGGER = new Logger();
    private static final int RESULT_LOAD_IMG_CODE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private Classifier classifier;
    private Bitmap picture;
    private boolean classficationRunning;
    private Disposable classificationDisposable;
    private Uri photoUri;

    private Button btnSelectPicture;
    private Button btnRunClassifier;
    private Button btnTakePhoto;
    private ImageView ivPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        init();
    }

    @Override
    protected void onDestroy() {
        if (classifier != null)
            classifier.close();
        if (classificationDisposable != null)
            classificationDisposable.dispose();
        super.onDestroy();
    }

    private void initViews() {
        btnSelectPicture = findViewById(R.id.btn_image_select);
        btnRunClassifier = findViewById(R.id.btn_run_classification);
        btnTakePhoto = findViewById(R.id.btn_take_photo);
        ivPicture = findViewById(R.id.iv_picture);

        btnSelectPicture.setOnClickListener(v -> selectPictureFromGallery());
        btnRunClassifier.setOnClickListener(v -> runClassification());
        btnTakePhoto.setOnClickListener(v -> takePhoto());
    }

    private void init() {
        classficationRunning = false;

    }

    private void selectPictureFromGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG_CODE);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (reqCode == RESULT_LOAD_IMG_CODE) {
            if (resultCode == RESULT_OK)
                loadPicture(data.getData());
            else
                Toast.makeText(
                        this,
                        "Getting picture from gallery unsuccessful",
                        Toast.LENGTH_SHORT
                ).show();
        } else if (reqCode == REQUEST_IMAGE_CAPTURE)
            loadPicture(photoUri);
    }

    private void loadPicture(Uri imageUri) {
        try {
            BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
            decodeOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
            InputStream imageStream = getContentResolver().openInputStream(imageUri);
            if (picture != null)
                picture.recycle();
            picture = BitmapFactory.decodeStream(imageStream, null, decodeOptions);
            ivPicture.setImageBitmap(picture);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(
                    this,
                    "Couldn't load picture from uri: " + imageUri,
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void runClassification() {
        if (classficationRunning)
            return;

        if (picture == null) {
            LOGGER.i("runClassification() picture is null");
            Toast.makeText(this, "Please select a picture", Toast.LENGTH_SHORT).show();
            return;
        }

        doRunClassifier();
    }

    private void recreateClassifier() {
        if (classifier != null) {
            LOGGER.d("Closing classifier.");
            classifier.close();
            classifier = null;
        }
        try {
            LOGGER.d("Creating classifier");
            classifier = Classifier.create(this);
        } catch (IOException e) {
            LOGGER.e(e, "Failed to create classifier.");
        }
    }

    private void doRunClassifier() {
        LOGGER.i("Running classification");
        Toast.makeText(this, "Running classification", Toast.LENGTH_SHORT).show();
        classficationRunning = true;
        classificationDisposable = Observable
                .fromCallable(() -> {
                    recreateClassifier();
                    return classifier.recognizeImage(picture, 0);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recognitions -> {
                    if (classifier != null) {
                        classifier.close();
                    }
                    classficationRunning = false;
                    showClassificationResult(recognitions);
                }, errorThrowable -> {
                    if (classifier != null)
                        classifier.close();
                    classficationRunning = false;
                    classificationFailed(errorThrowable);
                });
    }

    private void classificationFailed(Throwable errorThrowable) {
        LOGGER.i("MainActivity::classificationFailed() error:%s", errorThrowable.toString());
        Toast.makeText(this, "Classification failed", Toast.LENGTH_SHORT).show();
    }

    private void showClassificationResult(List<Classifier.Recognition> results) {
        List<String> parsedResults = new ArrayList<>();
        for (Classifier.Recognition result : results) {
            parsedResults.add(String.format(Locale.getDefault(), "%s (%.2f %%)", result.getTitle(), result.getConfidence() * 100));
        }

        String[] resultsStrArray = parsedResults.toArray(new String[parsedResults.size()]);

        Intent mushroomResultsIntent = new Intent(this, ResultsActivity.class);
        mushroomResultsIntent.putExtra(ResultsActivity.MATCHED_SPECIES, resultsStrArray);
        startActivity(mushroomResultsIntent);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
        }

        if (photoFile != null) {
            photoUri = FileProvider.getUriForFile(this,
                    "com.example.android.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

}