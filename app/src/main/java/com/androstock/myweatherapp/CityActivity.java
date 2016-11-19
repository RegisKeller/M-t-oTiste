package com.androstock.myweatherapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CityActivity extends Activity {

    public Function.placeIdTask asyncTask = null;
    public String longi, lati;

    public SharedPreferences sharedPreferencesFavoris;
    public String SavedVille;
    private static final String FAVORIS = "FAVORIS";
    private static final String FAVORIS_VILLE = "FAVORIS_VILLE";
    public Set<String> set;

    public int positionToRemove = 0;


    ListView ListOfFavoris;
    /** Items entered by the user is stored in this ArrayList variable */
    ArrayList<String> list = new ArrayList<String>();
    /** Declaring an ArrayAdapter to set items to ListView */
    ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

/* Declarations */
        Button buttonOk = (Button) findViewById(R.id.button2);
        Button buttonReturn = (Button) findViewById(R.id.button);
        Button buttonAdd = (Button) findViewById(R.id.button3);
        ListOfFavoris = (ListView) findViewById(R.id.LvOfFavoris);
        Switch SwitchGeo = (Switch) findViewById(R.id.switch1);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        ListOfFavoris.setAdapter(adapter);

        sharedPreferencesFavoris = getBaseContext().getSharedPreferences(FAVORIS, MODE_PRIVATE);
        set = sharedPreferencesFavoris.getStringSet(FAVORIS_VILLE,null);
        if(set !=null) {
            list.addAll(set);
        }



/* GPS - manu */
        SwitchGeo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            EditText VilleInET = (EditText) findViewById(R.id.EtVille);
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    VilleInET.setEnabled(false);

                    GPSTracker gps = new GPSTracker(CityActivity.this);
                    if (gps.canGetLocation()) {
                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();
                        lati = String.valueOf(latitude);
                        longi = String.valueOf(longitude);
                        Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

                        asyncTask = new Function.placeIdTask(new Function.AsyncResponse() {
                            public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {
                                Toast.makeText(getApplicationContext(), "Your Location is : " + weather_city, Toast.LENGTH_LONG).show();
                                VilleInET.setText(weather_city);
                            }
                        });
                        asyncTask.execute(lati, longi);
                    } else {
                        gps.showSettingsAlert();
                    }
                } else {
                    VilleInET.setEnabled(true);
                }
            }
        });

/* Select from favoris */
        ListOfFavoris.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String CitySelected = ((TextView) view).getText().toString();
                EditText text = (EditText) findViewById(R.id.EtVille);
                text.setText(CitySelected);

                sharedPreferencesFavoris.edit();
                set = new HashSet<String>();
                set.addAll(list);
                sharedPreferencesFavoris.edit().putStringSet(FAVORIS_VILLE,set).apply();
            }
        });

/* Delete from favoris */
        ListOfFavoris.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(parent.getContext(), ((TextView)view).getText(), Toast.LENGTH_SHORT).show();
                positionToRemove = position;
                String CitySelected = ((TextView) view).getText().toString();
                new AlertDialog.Builder(CityActivity.this)
                        .setTitle("Attention")
                        .setMessage("Supprimer \"" + CitySelected + "\" de vos favoris ?")
                        .setNegativeButton(android.R.string.cancel, null) // dismisses by default
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialog, int which) {
                                list.remove(positionToRemove);
                                adapter.notifyDataSetChanged();                            }
                        })
                        .create()
                        .show();
                return false;
            }
        });



/* Bouton add */
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText edit = (EditText) findViewById(R.id.EtVille);
                list.add(edit.getText().toString());
                adapter.notifyDataSetChanged();

                sharedPreferencesFavoris.edit();
                set = new HashSet<String>();
                set.addAll(list);
                sharedPreferencesFavoris.edit().putStringSet(FAVORIS_VILLE,set).apply();
            }
        });

/* Bouton Ok */
        buttonOk.setOnClickListener(new View.OnClickListener() {
            EditText VilleInET = (EditText) findViewById(R.id.EtVille);
            public void onClick(View v) {
                Intent result = new Intent();
                result.putExtra(MainActivity.VilleChoisie, VilleInET.getText().toString());
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
}

