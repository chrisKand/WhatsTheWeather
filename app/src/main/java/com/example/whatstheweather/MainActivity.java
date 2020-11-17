package com.example.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView resultTextView;

    public class DownloadJSON extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... jsonURL) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try{
                url = new URL(jsonURL[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream stream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(stream);
                int data = reader.read();

                while (data != -1){
                    char current = (char)data;
                    result += current;
                    data = reader.read();
                }
                return result;
            }catch (Exception e){
                e.printStackTrace();
                EditText input = findViewById(R.id.cityEditText);

                String city = input.getText().toString();
                resultTextView.setText("Failed to get weather for city '" + city + "'");
                return "Failed";
            }
        }


        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);

                String weatherInfo = jsonObject.getString("weather");

                Log.i("weather", weatherInfo);

                JSONArray jsonArray = new JSONArray(weatherInfo);

                String resultText = "";
                for (int i = 0; i < jsonArray.length(); i++){

                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    resultText += jsonObject1.getString("main") + ": " + jsonObject1.getString("description") + "\r\n";

                    Log.i("main", jsonObject1.getString("main"));
                    Log.i("description", jsonObject1.getString("description"));

                }

                resultTextView.setText(resultText);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultTextView = findViewById(R.id.resultTextView);


    }

    public void getWeather(View view){

        EditText input = findViewById(R.id.cityEditText);

        String city = input.getText().toString();

        DownloadJSON jsonTask = new DownloadJSON();
        String result = null;
        try {
            result = jsonTask.execute("https://openweathermap.org/data/2.5/weather?q=" + city + "&appid=439d4b804bc8187953eb36d2a8c26a02").get();
        }catch (Exception e){
            e.printStackTrace();
        }

        Log.i("json", result);

        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        manager.hideSoftInputFromWindow(input.getWindowToken(), 0);

    }
}