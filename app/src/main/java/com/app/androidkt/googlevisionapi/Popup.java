package com.app.androidkt.googlevisionapi;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class Popup extends Activity {

    DatabaseHelper mDatabaseHelper;
    DatabaseHelperH mDatabaseHelperH;

    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDatabaseHelper = new DatabaseHelper(this);
        mDatabaseHelperH = new DatabaseHelperH(this);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.2));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y =-500;

        getWindow().setAttributes(params);
    }


    public void button (View view){
        Calendar c = Calendar.getInstance();
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

        editText = findViewById(R.id.editTextArtikeladd);
        String ergebnis = editText.getText().toString();

        if(ergebnis.isEmpty()==false) {
            sDate = sDate + ": " + editText.getText().toString();
            String newEntry = sDate.toString();
            AddData(newEntry);
            editText.setText("");
            finish();
        }
        else{
            Toast.makeText(this,
                    "Bitte zuerst ein Produkt Eingeben", Toast.LENGTH_SHORT).show();
        }


    }
    public void AddData(String newEntry) {
        mDatabaseHelper.addData(newEntry);
        mDatabaseHelperH.addData(newEntry);

        String ergebnis = editText.getText().toString();

        if(ergebnis!="") {
            Toast.makeText(this, "Artikel zu Einkaufsliste hinzugefügt!", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this,
                    "Bitte zuerst einen Gegenstand eingeben!", Toast.LENGTH_SHORT).show();
        }

    }
}
