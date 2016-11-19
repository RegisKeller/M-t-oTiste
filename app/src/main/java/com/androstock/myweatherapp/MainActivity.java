package com.androstock.myweatherapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView cityField, detailsField, currentTemperatureField, humidity_field, pressure_field, weatherIcon, updatedField;

    Typeface weatherFont;
    public final static int ChoixDeVille = 0;
    public final static String VilleChoisie = null;
    public String my_weather_city;
    public String my_weather_updatedOn;
    public String my_weather_description;
    public String my_weather_temperature;
    public String my_weather_humidity;
    public String my_weather_pressure;

    public SharedPreferences sharedPreferences;
    private static final String PREFS = "PREFS";
    private static final String PREFS_VILLE = "PREFS_VILLE";

    public Function.placeIdTask asyncTask = null;
    public String myVille = "Strasbourg, FR";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        myVille = sharedPreferences.getString(PREFS_VILLE, null);

        weatherFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/weathericons-regular-webfont.ttf");

        cityField = (TextView)findViewById(R.id.city_field);
        updatedField = (TextView)findViewById(R.id.updated_field);
        detailsField = (TextView)findViewById(R.id.details_field);
        currentTemperatureField = (TextView)findViewById(R.id.current_temperature_field);
        humidity_field = (TextView)findViewById(R.id.humidity_field);
        pressure_field = (TextView)findViewById(R.id.pressure_field);
        weatherIcon = (TextView)findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);

        asyncTask =new Function.placeIdTask(new Function.AsyncResponse() {
            public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {
                my_weather_city = weather_city;
                my_weather_updatedOn = weather_updatedOn;
                my_weather_description = weather_description;
                my_weather_temperature = weather_temperature;
                my_weather_humidity = weather_humidity;
                my_weather_pressure = weather_pressure;

                cityField.setText(weather_city);
                updatedField.setText(weather_updatedOn);
                detailsField.setText(weather_description);
                currentTemperatureField.setText(weather_temperature);
                humidity_field.setText("Humidité: "+weather_humidity);
                pressure_field.setText("Pression: "+weather_pressure);
                weatherIcon.setText(Html.fromHtml(weather_iconText));
            }
        });
        asyncTask.execute(myVille);
    }

    //Affiche les icônes dans la toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Gère le click sur une action de l'ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.citychange:
                Intent cityActivity = new Intent(MainActivity.this, CityActivity.class);
                startActivityForResult(cityActivity, ChoixDeVille);
                return true;
            case R.id.action_settings:
                return true;
            case R.id.sharemail:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"destinataire@example.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "MétéoTiste Report");
                i.putExtra(Intent.EXTRA_TEXT   , "Ville : " + my_weather_city);
                i.putExtra(Intent.EXTRA_TEXT   ,
                        "Ville : " + my_weather_city + " \n" +
                        "Date : " + my_weather_updatedOn + " \n" +
                        "Météo : " + my_weather_description + " \n" +
                        "Température : " + my_weather_temperature + " \n" +
                        "Humidité : " + my_weather_humidity + " \n" +
                        "Pression : " + my_weather_pressure + " \n" +
                         "\n Bonne journée avec MétéoTiste");

                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "Aucun client mail n'est installé.", Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ChoixDeVille) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Vous avez choisi la ville " + data.getStringExtra(VilleChoisie), Toast.LENGTH_SHORT).show();
                myVille = data.getStringExtra(VilleChoisie);
                asyncTask =new Function.placeIdTask(new Function.AsyncResponse() {
                    public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {
                        my_weather_city = weather_city;
                        my_weather_updatedOn = weather_updatedOn;
                        my_weather_description = weather_description;
                        my_weather_temperature = weather_temperature;
                        my_weather_humidity = weather_humidity;
                        my_weather_pressure = weather_pressure;

                        cityField.setText(weather_city);
                        updatedField.setText(weather_updatedOn);
                        detailsField.setText(weather_description);
                        currentTemperatureField.setText(weather_temperature);
                        humidity_field.setText("Humidité: "+weather_humidity);
                        pressure_field.setText("Pression: "+weather_pressure);
                        weatherIcon.setText(Html.fromHtml(weather_iconText));
                    }
                });
                asyncTask.execute(myVille);
                sharedPreferences.edit().putString(PREFS_VILLE, myVille).apply();
            }
        }
    }
}

