package com.app.androidkt.googlevisionapi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;



public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void fotografieren (View aView) {
        Intent intent = new Intent(this, Fotografieren.class);
        startActivity(intent);
    }

    public void einkaufsliste (View aView) {
        Intent intent = new Intent(this, Einkaufsliste.class);
        startActivity(intent);
    }


    public void historie(View aView) {
        Intent intent = new Intent(this, Historie.class);
        startActivity(intent);
    }


    public void einstellungen (View aView) {
        Intent intent = new Intent(this, Einstellungen.class);
        startActivity(intent);
    }

}