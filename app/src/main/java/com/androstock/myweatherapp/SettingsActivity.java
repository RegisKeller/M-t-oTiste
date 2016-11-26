package com.androstock.myweatherapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class SettingsActivity extends Activity implements Spinner.OnItemSelectedListener {

    public SharedPreferences sharedPreferences;
    private static final String LANGUE = "LANGUE";
    private static final String FAVORIS_LANGUE = "FAVORIS_LANGUE";

    public String myLangue = "fr";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getBaseContext().getSharedPreferences(LANGUE, MODE_PRIVATE);
        myLangue = sharedPreferences.getString(FAVORIS_LANGUE, null);

        Button buttonOk = (Button) findViewById(R.id.button5);
        Button buttonReturn = (Button) findViewById(R.id.button4);

        // Spinner element
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Français");
        categories.add("English");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);


        /* Bouton Ok */
        buttonOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent result = new Intent();
                result.putExtra(MainActivity.LangueChoisie, myLangue);
                //Toast.makeText(getApplicationContext(), "Vous avez choisi la langue ppp " + myLangue, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, result);
                finish();
            }
        });

        /* Bouton Retour */
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();

        if(item == "Français"){
            myLangue = "fr";
        }
        if(item == "English"){
            myLangue = "uk";
        }

        sharedPreferences.edit().putString(FAVORIS_LANGUE, myLangue);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}

