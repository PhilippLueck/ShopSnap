package com.app.androidkt.googlevisionapi;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.ColorInfo;
import com.google.api.services.vision.v1.model.DominantColorsAnnotation;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.ImageProperties;
import com.google.api.services.vision.v1.model.SafeSearchAnnotation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fotografieren extends AppCompatActivity implements AdapterView.OnItemSelectedListener  {

        DatabaseHelper mDatabaseHelper;
        DatabaseHelperH mDatabaseHelperH;
        Button addButton;
        EditText editText;

    private static final String TAG = "MainActivity";
    private static final int RECORD_REQUEST_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;

    private static final String CLOUD_VISION_API_KEY = "AIzaSyCEJlr45iAsMEP0zEH1rkNvFpxyG1hpmeo";

    @BindView(R.id.takePicture)
    Button takePicture;

    @BindView(R.id.imageProgress)
    ProgressBar imageUploadProgress;

    @BindView(R.id.imageView)
    ImageView imageView;

    @BindView(R.id.spinnerVisionAPI)
    Spinner spinnerVisionAPI;

    @BindView(R.id.visionAPIData)
    TextView visionAPIData;
    private Feature feature;
    private Bitmap bitmap;
    //private String[] visionAPI = new String[]{"LANDMARK_DETECTION", "LOGO_DETECTION", "SAFE_SEARCH_DETECTION", "IMAGE_PROPERTIES", "LABEL_DETECTION"};
    private String[] visionAPI = new String[] {"LABEL_DETECTION"};

    private String api = visionAPI[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fotografieren);
        ButterKnife.bind(this);


        feature = new Feature();
        feature.setType(visionAPI[0]);
        feature.setMaxResults(10);

        spinnerVisionAPI.setOnItemSelectedListener(this);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, visionAPI);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVisionAPI.setAdapter(dataAdapter);

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePictureFromCamera();
            }
        });



        addButton= (Button) findViewById(R.id.addButton);
        mDatabaseHelper = new DatabaseHelper(this);
        mDatabaseHelperH = new DatabaseHelperH(this);




    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            takePicture.setVisibility(View.VISIBLE);
        } else {
            takePicture.setVisibility(View.INVISIBLE);
            makeRequest(Manifest.permission.CAMERA);
        }
    }

    private int checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission);
    }

    private void makeRequest(String permission) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, RECORD_REQUEST_CODE);
    }

    public void takePictureFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            callCloudVision(bitmap, feature);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == RECORD_REQUEST_CODE) {
            if (grantResults.length == 0 && grantResults[0] == PackageManager.PERMISSION_DENIED
                    && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                finish();
            } else {
                takePicture.setVisibility(View.VISIBLE);
            }
        }
    }

    private void callCloudVision(final Bitmap bitmap, final Feature feature) {
        imageUploadProgress.setVisibility(View.VISIBLE);
        final List<Feature> featureList = new ArrayList<>();
        featureList.add(feature);

        final List<AnnotateImageRequest> annotateImageRequests = new ArrayList<>();

        AnnotateImageRequest annotateImageReq = new AnnotateImageRequest();
        annotateImageReq.setFeatures(featureList);
        annotateImageReq.setImage(getImageEncodeImage(bitmap));
        annotateImageRequests.add(annotateImageReq);


        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {

                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    VisionRequestInitializer requestInitializer = new VisionRequestInitializer(CLOUD_VISION_API_KEY);

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(requestInitializer);

                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest = new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(annotateImageRequests);

                    Vision.Images.Annotate annotateRequest = vision.images().annotate(batchAnnotateImagesRequest);
                    annotateRequest.setDisableGZipContent(true);
                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return convertResponseToString(response);
                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " + e.getMessage());
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }

            protected void onPostExecute(String result) {

                String cola = "cola";
                String water = "water";
                String kosmetik ="cosmetics";

                if(result.contains(cola)){
                    visionAPIData.setText("Coca Cola Flasche 0,5 Liter");
                }else if(result.contains(water)){
                    visionAPIData.setText("Volvic - Natürliches Mineralwasser 0,75 Liter");
                } else if (result.contains(kosmetik)){
                    visionAPIData.setText("Hugo Boss - Boss Bottled 200ml");
                } else {
                    visionAPIData.setText("nichts gefunden");
                }

                //visionAPIData.setText(result);
                System.out.println("ballo "+result);
                imageUploadProgress.setVisibility(View.INVISIBLE);
            }
        }.execute();
    }

    @NonNull
    private Image getImageEncodeImage(Bitmap bitmap) {
        Image base64EncodedImage = new Image();
        // Convert the bitmap to a JPEG
        // Just in case it's a format that Android understands but Cloud Vision
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        // Base64 encode the JPEG
        base64EncodedImage.encodeContent(imageBytes);
        return base64EncodedImage;
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {

        AnnotateImageResponse imageResponses = response.getResponses().get(0);

        List<EntityAnnotation> entityAnnotations;

        String message = "";

        api = "LABEL_DETECTION";
        switch (api) {
            case "LANDMARK_DETECTION":
                entityAnnotations = imageResponses.getLandmarkAnnotations();
                message = formatAnnotation(entityAnnotations);
                break;
            case "LOGO_DETECTION":
                entityAnnotations = imageResponses.getLogoAnnotations();
                message = formatAnnotation(entityAnnotations);
                break;
            case "SAFE_SEARCH_DETECTION":
                SafeSearchAnnotation annotation = imageResponses.getSafeSearchAnnotation();
                message = getImageAnnotation(annotation);
                break;
            case "IMAGE_PROPERTIES":
                ImageProperties imageProperties = imageResponses.getImagePropertiesAnnotation();
                message = getImageProperty(imageProperties);
                break;
            case "LABEL_DETECTION":
                entityAnnotations = imageResponses.getLabelAnnotations();
                message = formatAnnotation(entityAnnotations);

        }
        return message;
    }

    private String getImageAnnotation(SafeSearchAnnotation annotation) {
        return String.format("adult: %s\nmedical: %s\nspoofed: %s\nviolence: %s\n",
                annotation.getAdult(),
                annotation.getMedical(),
                annotation.getSpoof(),
                annotation.getViolence());
    }

    private String getImageProperty(ImageProperties imageProperties) {
        String message = "";
        DominantColorsAnnotation colors = imageProperties.getDominantColors();
        for (ColorInfo color : colors.getColors()) {
            message = message + "" + color.getPixelFraction() + " - " + color.getColor().getRed() + " - " + color.getColor().getGreen() + " - " + color.getColor().getBlue();
            message = message + "\n";
        }
        return message;
    }

    private String formatAnnotation(List<EntityAnnotation> entityAnnotation) {
        String message = "";

        if (entityAnnotation != null) {
            for (EntityAnnotation entity : entityAnnotation) {
                message = message + "    " + entity.getDescription() + " " + entity.getScore();
                message += "\n";
            }
        } else {
            message = "Nothing Found";
        }
        return message;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        api = (String) adapterView.getItemAtPosition(i);
        feature.setType(api);
        if (bitmap != null)
            callCloudVision(bitmap, feature);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    Calendar c = Calendar.getInstance();

   /* String sDate = c.get(Calendar.YEAR) + "-"
            + c.get(Calendar.MONTH)
            + "-" + c.get(Calendar.DAY_OF_MONTH)
            + " at " + c.get(Calendar.HOUR_OF_DAY)
            + ":" + c.get(Calendar.MINUTE); */


    //Database

   public void addButton (View view){

       int tag = c.get(Calendar.DAY_OF_MONTH);
       int monat = c.get(Calendar.MONTH);
       int jahr = c.get(Calendar.YEAR);
       int stunde = c.get(Calendar.HOUR_OF_DAY);
       int minute = c.get(Calendar.MINUTE);
       String tagString;
       String monatString;
       String jahrString;
       String stundeString;
       String minuteString;

       if(tag<10) {tagString = "0" + tag;
       }else {tagString = "" + tag;}

       if(monat<10) {monatString = "0" + monat;
       }else {monatString = "" + monat;}

       if(jahr<10) {jahrString = "0" + jahr;
       }else {jahrString = "" + jahr;}

       if(stunde<10) {stundeString = "0" + stunde;
       }else {stundeString = "" + stunde;}

       if(minute<10) {minuteString = "0" + minute;
       }else {minuteString = "" + minute;}

           String sDate = tagString + "."
                   + monatString + "."
                   + jahrString + " "
                   + stundeString + ":"
                   + minuteString;


       String ergebnis = visionAPIData.getText().toString();

       if(ergebnis=="Coca Cola Flasche 0,5 Liter"){
           sDate = sDate +": "+ visionAPIData.getText().toString();
           String newEntry = sDate.toString();
           AddData(newEntry);
           visionAPIData.setText("");

       }else if(ergebnis=="Volvic - Natürliches Mineralwasser 0,75 Liter"){
           sDate = sDate +": "+ visionAPIData.getText().toString();
           String newEntry = sDate.toString();
           AddData(newEntry);
           visionAPIData.setText("");

       } else if (ergebnis=="Hugo Boss - Boss Bottled 200ml"){
           sDate = sDate +": "+ visionAPIData.getText().toString();
           String newEntry = sDate.toString();
           AddData(newEntry);
           visionAPIData.setText("");
       }

       else{
           Toast.makeText(Fotografieren.this,
                   "Bitte zuerst ein Produkt fotografieren", Toast.LENGTH_LONG).show();
       }
   }






    public void AddData(String newEntry) {
        boolean insertData = mDatabaseHelper.addData(newEntry);
        boolean insertDataH = mDatabaseHelperH.addData(newEntry);

        String ergebnis = visionAPIData.getText().toString();

        if(ergebnis=="Coca Cola Flasche 0,5 Liter"){
            Toast.makeText(this, "Artikel zu Einkaufsliste hinzugefügt!", Toast.LENGTH_LONG).show();

        }else if(ergebnis=="Volvic - Natürliches Mineralwasser 0,75 Liter"){
            Toast.makeText(this, "Artikel zu Einkaufsliste hinzugefügt!", Toast.LENGTH_LONG).show();

        } else if (ergebnis=="Hugo Boss - Boss Bottled 200ml"){
            Toast.makeText(this, "Artikel zu Einkaufsliste hinzugefügt!", Toast.LENGTH_LONG).show();
        }

        else{
            Toast.makeText(Fotografieren.this,
                    "Bitte zuerst ein Produkt fotografieren", Toast.LENGTH_LONG).show();
        }

    }

    public void shopProduct(View view){

        String ergebnis = visionAPIData.getText().toString();

       if(ergebnis=="Coca Cola Flasche 0,5 Liter"){

           Uri uri = Uri.parse("https://www.amazon.de/Coca-Cola-Erfrischung-unverwechselbarem-stylischem-Kultdesign/dp/B00H2DNJX8/ref=sr_1_3?ie=UTF8&qid=1530461016&sr=8-3&keywords=coca+cola+0%2C5"); // missing 'http://' will cause crashed
           Intent intent = new Intent(Intent.ACTION_VIEW, uri);
           startActivity(intent);

       }else if(ergebnis=="Volvic - Natürliches Mineralwasser 0,75 Liter"){

               Uri uri = Uri.parse("https://www.amazon.de/Volvic-Naturelle-Mehrweg-Sportflasche-75/dp/B01G554HGQ/ref=sr_1_4?s=grocery&ie=UTF8&qid=1530461417&sr=1-4&keywords=volvic+0%2C75"); // missing 'http://' will cause crashed
               Intent intent = new Intent(Intent.ACTION_VIEW, uri);
               startActivity(intent);

       } else if (ergebnis=="Hugo Boss - Boss Bottled 200ml"){
           Uri uri = Uri.parse("https://www.amazon.de/Hugo-Boss-Bottled-homme-Toilette/dp/B001O8MI2K/ref=sr_1_1?s=beauty&ie=UTF8&qid=1530464260&sr=1-1&keywords=boss+bottled"); // missing 'http://' will cause crashed
           Intent intent = new Intent(Intent.ACTION_VIEW, uri);
           startActivity(intent);
       }

       else{
           Toast.makeText(Fotografieren.this,
                   "Bitte zuerst ein Produkt fotografieren", Toast.LENGTH_LONG).show();
       }
    }

}
