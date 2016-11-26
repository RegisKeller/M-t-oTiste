package com.androstock.myweatherapp;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class Function {

	private static final String OPEN_WEATHER_MAP_API = "b9605b4638ea82be421dc0364cfb4802";

	public static String setWeatherIcon(int actualId, long sunrise, long sunset){
		int id = actualId / 100;
		String icon = "";
		if(actualId == 800){
			long currentTime = new Date().getTime();
			if(currentTime>=sunrise && currentTime<sunset) {
				icon = "&#xf00d;";
			} else {
				icon = "&#xf02e;";
			}
		} else {
			switch(id) {
				case 2 : icon = "&#xf01e;";
					break;
				case 3 : icon = "&#xf01c;";
					break;
				case 7 : icon = "&#xf014;";
					break;
				case 8 : icon = "&#xf013;";
					break;
				case 6 : icon = "&#xf01b;";
					break;
				case 5 : icon = "&#xf019;";
					break;
			}
		}
		return icon;
	}

	public interface AsyncResponse {

		void processFinish(String output1, String output2, String output3, String output4, String output5, String output6, String output7, String output8);
	}

	 public static class placeIdTask extends AsyncTask<String, Void, JSONObject> {

		public AsyncResponse delegate = null;//Call back interface

		 public placeIdTask(AsyncResponse asyncResponse) {
			 delegate = asyncResponse;//Assigning call back interfacethrough constructor
		 }

		@Override
		protected JSONObject doInBackground(String... params) {

			JSONObject jsonWeather = null;

			try {jsonWeather = getWeatherJSONbyLatLon(params[0], params[1], params[2]);
			} catch (Exception e) {Log.d("Error", "Cannot process JSON results LatLon", e);}

			try {jsonWeather = getWeatherJSONbyVille(params[0], params[1]);}
			catch (Exception e) {Log.d("Error", "Cannot process JSON results City", e);}

			return jsonWeather;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
			if(json != null){
				JSONObject details = json.getJSONArray("weather").getJSONObject(0);
				JSONObject main = json.getJSONObject("main");
				DateFormat df = DateFormat.getDateTimeInstance();

				String city = json.getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("sys").getString("country");
				String description = details.getString("description").toUpperCase(Locale.US);
				String temperature = String.format("%.2f", main.getDouble("temp"))+ "°";
				String humidity = main.getString("humidity") + "%";
				String pressure = main.getString("pressure") + " hPa";
				String updatedOn = df.format(new Date(json.getLong("dt")*1000));
				String iconText = setWeatherIcon(details.getInt("id"),
						json.getJSONObject("sys").getLong("sunrise") * 1000,
						json.getJSONObject("sys").getLong("sunset") * 1000);

				delegate.processFinish(city, description, temperature, humidity, pressure, updatedOn, iconText, ""+ (json.getJSONObject("sys").getLong("sunrise") * 1000));
			}
			} catch (JSONException e) {
				//Log.e(LOG_TAG, "Cannot process JSON results", e);
			}
		}
	}

	public static JSONObject getWeatherJSONbyLatLon(String lat, String lon, String lang){
		try {
			URL url = new URL("http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "%s&units=metric&lang=" + lang);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.addRequestProperty("x-api-key", OPEN_WEATHER_MAP_API);

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			StringBuffer json = new StringBuffer(1024);
			String tmp="";
			while((tmp=reader.readLine())!=null)
				json.append(tmp).append("\n");
			reader.close();

			JSONObject data = new JSONObject(json.toString());

			if(data.getInt("cod") != 200){
				return null;
			}

			return data;
		}catch(Exception e){
			return null;
		}
	}

	public static JSONObject getWeatherJSONbyVille(String ville, String lang){
		try {
			URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + ville + "&units=metric&lang=" + lang);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();

			connection.addRequestProperty("x-api-key", OPEN_WEATHER_MAP_API);

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer json = new StringBuffer(1024);
			String tmp="";
			while((tmp=reader.readLine())!=null)
				json.append(tmp).append("\n");
			reader.close();

			JSONObject data = new JSONObject(json.toString());

			if(data.getInt("cod") != 200){
				return null;
			}
			return data;
		}catch(Exception e){
			return null;
		}
	}
}