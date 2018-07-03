package com.app.androidkt.googlevisionapi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Einkaufsliste extends AppCompatActivity {

    int[] arrayEins = new int[100];

    private static final String TAG = "Einkaufsliste";

    DatabaseHelper mDatabaseHelper;
    private ListView listView;

    @Override
    protected void onResume() {
        ListView listView = (ListView) findViewById(R.id.elist);
        mDatabaseHelper = new DatabaseHelper(this);


        


        //populate an ArrayList<String> from the database and then view it
        ArrayList<String> theList = new ArrayList<>();
        Cursor data = mDatabaseHelper.getListContents();
        if(data.getCount() == 0){
            Toast.makeText(this, "Die Einkaufsliste enthält keine Artikel",Toast.LENGTH_SHORT).show();
        }else{
            while(data.moveToNext()){
                theList.add(data.getString(1));
                ListAdapter listAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,theList);
                listView.setAdapter(listAdapter);


            }
        }


        getComplexObject ();

        for(int a = 0; a< 999; a++ ){
            if(arrayEins[a]==1 ){

                //  System.out.println("eingabe" + a);

                //  listView.requestFocusFromTouch();
                //   listView.setSelection(a);
                //  listView.performItemClick(
                //       listView.getAdapter().getView(a, null, null), a, a);









            }
        }










        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView text = (TextView) view;
                text.setPaintFlags(text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                mDatabaseHelper.update("test", i);

                arrayEins[i]=1;

                String recipes = String.valueOf(adapterView.getItemAtPosition(i));

                System.out.println(l);

                SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor e = prefs.edit();
                e.putString("COMPLEX_OBJECT",new Gson().toJson(arrayEins));
                e.commit();

            }


        });
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_einkaufsliste);
        ListView listView = (ListView) findViewById(R.id.elist);
        mDatabaseHelper = new DatabaseHelper(this);





        //populate an ArrayList<String> from the database and then view it
        ArrayList<String> theList = new ArrayList<>();
        Cursor data = mDatabaseHelper.getListContents();
        if(data.getCount() == 0){
            Toast.makeText(this, "Die Einkaufsliste enthält keine Artikel",Toast.LENGTH_SHORT).show();
        }else{
            while(data.moveToNext()){
                theList.add(data.getString(1));
                ListAdapter listAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,theList);
                listView.setAdapter(listAdapter);


            }
        }


        getComplexObject ();

        for(int a = 0; a< 999; a++ ){
            if(arrayEins[a]==1 ){

              //  System.out.println("eingabe" + a);

              //  listView.requestFocusFromTouch();
             //   listView.setSelection(a);
              //  listView.performItemClick(
                 //       listView.getAdapter().getView(a, null, null), a, a);









            }
        }










        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView text = (TextView) view;
                text.setPaintFlags(text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                  arrayEins[i]=1;

                String recipes = String.valueOf(adapterView.getItemAtPosition(i));

                System.out.println(l);

                SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor e = prefs.edit();
                e.putString("COMPLEX_OBJECT",new Gson().toJson(arrayEins));
                e.commit();

            }


        });

    }




    public void löschen (View view){
        mDatabaseHelper.deleteAll();
    ListView listView = (ListView) findViewById(R.id.elist);
    ArrayList<String> theList = new ArrayList<>();

    listView.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,theList));
}

    public  void getComplexObject (){
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        String sobj = prefs.getString("COMPLEX_OBJECT", null);
        Gson gson = new Gson();
        Type type = new TypeToken<int[]>() {}.getType();
        arrayEins = gson.fromJson(sobj,type);
        if (arrayEins == null){
            arrayEins = new int[999];
        }

    }

public void addButton (View view){
        Intent intent = new Intent(this, Popup.class);
        startActivity(intent);
}



    }