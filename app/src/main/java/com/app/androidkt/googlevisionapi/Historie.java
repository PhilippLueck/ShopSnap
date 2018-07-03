package com.app.androidkt.googlevisionapi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Historie extends AppCompatActivity {

    private static final String TAG = "Historie";

    DatabaseHelperH mDatabaseHelperH;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historie);
        ListView listView = (ListView) findViewById(R.id.elist);
        mDatabaseHelperH = new DatabaseHelperH(this);



        //populate an ArrayList<String> from the database and then view it
        ArrayList<String> theList = new ArrayList<>();
        Cursor data = mDatabaseHelperH.getListContents();
        if(data.getCount() == 0){
            Toast.makeText(this, "Die Historie enthält keine Artikel!",Toast.LENGTH_LONG).show();
        }else{
            while(data.moveToNext()){
                theList.add(data.getString(1));
                ListAdapter listAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,theList);
                listView.setAdapter(listAdapter);

            }
        }




    }

    public void löschen (View view){
        mDatabaseHelperH.deleteAll();
        ListView listView = (ListView) findViewById(R.id.elist);
        ArrayList<String> theList = new ArrayList<>();

        listView.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,theList));
    }







}