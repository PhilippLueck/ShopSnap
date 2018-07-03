package com.app.androidkt.googlevisionapi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Einstellungen extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    public EditText editText1;
    public EditText editText2;
    String eMail;
    String passwort;
    String shop;
    int flag_shop;
    int flag_eMail;
    int flag_passwort;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_einstellungen);
        ButterKnife.bind(this);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.shop, android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);


    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        shop=adapterView.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void buttonclick_bestätigen (View aView) {

        editText1 = (EditText) findViewById(R.id.editText);
        eMail = editText1.getText().toString();

        editText2 = (EditText) findViewById(R.id.editText3);
        passwort = editText2.getText().toString();

        flag_shop = 0;
        flag_eMail = 0;
        flag_passwort = 0;

//Prüfen, ob Felder gefüllt sind
        if (shop.equals("Bitte auswählen...")){
            flag_shop = 1;
        }
        if (eMail.equals("")){
            flag_eMail = 1;
        }
        if(passwort.equals("")){
            flag_passwort = 1;
        }

//Ausgabe je nachdem, welche Eingabe getätigt wurde
        if (flag_shop == 1 && flag_eMail == 0 && flag_passwort == 0){
            Toast.makeText(this, "Bitte Shop eingeben", Toast.LENGTH_SHORT).show();
        }
        if (flag_shop == 1 && flag_eMail == 1 && flag_passwort == 0){
            Toast.makeText(this, "Bitte Shop und E-Mail-Adresse eingeben", Toast.LENGTH_SHORT).show();
        }
        if (flag_shop == 1 && flag_eMail == 0 && flag_passwort == 1){
            Toast.makeText(this, "Bitte Shop und Passwort eingeben", Toast.LENGTH_SHORT).show();
        }
        if (flag_shop == 1 && flag_eMail == 1 && flag_passwort == 1){
            Toast.makeText(this, "Bitte Shop, E-Mail-Adresse und Passwort eingeben", Toast.LENGTH_SHORT).show();
        }
        if (flag_shop == 0 && flag_eMail == 1 && flag_passwort == 0){
            Toast.makeText(this, "Bitte E-Mail-Adresse eingeben", Toast.LENGTH_SHORT).show();
        }
        if (flag_shop == 0 && flag_eMail == 1 && flag_passwort == 1){
            Toast.makeText(this, "Bitte E-Mail-Adresse und Passwort eingeben", Toast.LENGTH_SHORT).show();
        }
        if (flag_shop == 0 && flag_eMail == 0 && flag_passwort == 1){
            Toast.makeText(this, "Bitte Passwort eingeben", Toast.LENGTH_SHORT).show();
        }
        if (flag_shop == 0 && flag_eMail == 0 && flag_passwort == 0){
            Toast.makeText(this, "Erfolgreich eingeloggt!", Toast.LENGTH_SHORT).show();
        }

    }
}

