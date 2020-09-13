package com.example.weatherapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    TextView cityName;
    TextView main_message;
    TextView latitude_message;
    TextView longitude_message;
    TextView temperature_message;
    TextView description_message;
    Boolean error=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityName= (TextView)findViewById(R.id.name);
        main_message= (TextView)findViewById(R.id.main);
        latitude_message= (TextView)findViewById(R.id.latitude);
        longitude_message= (TextView)findViewById(R.id.longitude);
        temperature_message= (TextView)findViewById(R.id.temperature);
        description_message= (TextView)findViewById(R.id.description);
    }

    public class DownloadImage extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            Log.i("Message:",urls[0]);
            String results = "";
            HttpURLConnection connection=null;
            URL url;
            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    results += current;
                    data = reader.read();
                }
                Log.i("Message:",results);
                return results;
            } catch (Exception e) {
                error=true;
                e.printStackTrace();
            }
            return null;
        }
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                String weatherInfo = jsonObject.getString("weather");

                JSONArray jsonArray = new JSONArray(weatherInfo);

                for(int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    String main= "";
                    String description="";

                    main = jsonObject1.getString("main");
                    description= jsonObject1.getString("description");
                    main_message.setText("Main: "+main);
                    description_message.setText("Description: "+description);
                }
                /*The code finds the temperature of the Country Entered */
                String tempList = jsonObject.getString("main");
                Pattern p = Pattern.compile("\"temp\":(.*?),");
                Matcher m = p.matcher(tempList);
                String temperature="";
                while(m.find())
                {
                    temperature= m.group(1);
                }
                temperature= String.valueOf(Float.valueOf(temperature)/10);
                temperature_message.setText("Temp: "+(temperature)+"*C");

                /* The Code finds the latitude and longitude of the country Entered */

                String lonLat= jsonObject.getString("coord");
                Pattern lon = Pattern.compile("\"lon\":(.*?),");
                Matcher lon_matcher = lon.matcher(lonLat);
                String longitude="";
                while(lon_matcher.find())
                {
                    longitude= lon_matcher.group(1);
                }
                longitude_message.setText("Longitude: "+longitude);

                Pattern lat =Pattern.compile("\"lat\":(.*?)[}]");
                Matcher lat_matcher= lat.matcher(lonLat);
                String latitude= "";

                while(lat_matcher.find())
                {
                    latitude= lat_matcher.group(1);
                }
                latitude_message.setText("Latitude: "+latitude);


            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),"Weather Not Found!",Toast.LENGTH_LONG).show();
                description_message.setText("Description");
                main_message.setText("Main");
                latitude_message.setText("Latitude");
                longitude_message.setText("Longitude");
                temperature_message.setText("Temperature");
                e.printStackTrace();
            }

        }
    }
    public void findWeather(View view){
        DownloadImage task = new DownloadImage();
        InputMethodManager mgr =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mgr.hideSoftInputFromWindow(cityName.getWindowToken(),0);


        try{
            task.execute("https://api.openweathermap.org/data/2.5/weather?q="+cityName.getText().toString()+"&appid=2e049ff6a52da013e9d74ab7b9542f24");
        }catch(Exception e)
        {
            Toast.makeText(getApplicationContext(),"Weather Not Found!",Toast.LENGTH_LONG).show();
            description_message.setText("Description");
            main_message.setText("Main");
            latitude_message.setText("Latitude");
            longitude_message.setText("Longitude");
            temperature_message.setText("Temperature");
        }
    }
}