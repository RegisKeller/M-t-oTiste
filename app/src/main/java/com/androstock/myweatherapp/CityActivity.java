package com.androstock.myweatherapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
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

public class CityActivity extends Activity implements LocationListener {

    public Function.placeIdTask asyncTask = null;
    public String longi, lati;

    public SharedPreferences sharedPreferencesFavoris;
    private static final String FAVORIS = "FAVORIS";
    private static final String FAVORIS_VILLE = "FAVORIS_VILLE";
    public Set<String> set;

    public int positionToRemove = 0;
    private LocationManager locationManager;
    private Location location = null;

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
        set = sharedPreferencesFavoris.getStringSet(FAVORIS_VILLE, null);
        if (set != null) {
            list.addAll(set);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(provider, 0, 0, this);
        if(location == null){
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

/* GPS - manu */
            SwitchGeo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                EditText VilleInET = (EditText) findViewById(R.id.EtVille);

                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        VilleInET.setEnabled(false);

                        double latitude = 0;
                        double longitude = 0;

                        try{
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                        catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(CityActivity.this, "Echec de l'obtention de la localisation", Toast.LENGTH_SHORT).show();
                        }

                        String msg = "Latitude: " + latitude + "Longitude: " + longitude;

                        asyncTask = new Function.placeIdTask(new Function.AsyncResponse() {
                            public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {
                                Toast.makeText(getApplicationContext(), "Votre ville est : " + weather_city, Toast.LENGTH_LONG).show();
                                VilleInET.setText(weather_city);
                            }
                        });
                        lati = String.valueOf(latitude);
                        longi = String.valueOf(longitude);

                        asyncTask.execute(lati, longi, "fr");

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
                                adapter.notifyDataSetChanged();
                            }
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

    public void onLocationChanged(Location location) {
    }

    public void onProviderDisabled(String provider) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    public void onProviderEnabled(String provider) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }
}

