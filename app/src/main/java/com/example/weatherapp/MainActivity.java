package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GetDataFromInternet.AsyncResponse {

    private static final String TAG = "MainActivity";

    private Button searchBnt;
    private EditText searchField;
    private TextView nameCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Вытаскиваем id из активитимейн
        searchField = findViewById(R.id.etNameCity);
        nameCity = findViewById(R.id.tvNameCity);
        searchBnt = findViewById(R.id.btnSearchWeatherCity);
        searchBnt.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        URL url = buildUrl(searchField.getText().toString());
        nameCity.setText(searchField.getText(). toString());
        new GetDataFromInternet(this).execute(url);

    }

    private URL buildUrl (String city){

        String base_Url = "https://api.openweathermap.org/data/2.5/weather";
        String param_CITY = "q";
        String param_APPID = "appid";
        String appid_value = "745e6193c0666ad60065381960dea811";

        Uri buildUri = Uri.parse(base_Url).buildUpon().appendQueryParameter(param_CITY, city).appendQueryParameter(param_APPID, appid_value).build();
        URL url = null;
        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }

        Log.d(TAG, "buildUrl: "+url);
        return url;
    }

    @Override
    public void processFinished(String output){
        Log.d(TAG, "processFinished: "+output);
        // --------------------------------------------------------------
        try {
            // Вытаскиваем ДЖСОН данные
            JSONObject resultJSON = new JSONObject(output);
            JSONObject weather = resultJSON.getJSONObject("main");
            JSONObject systemData = resultJSON.getJSONObject("sys");

            // Вытаскиваем данные по температуре
            TextView temp = findViewById(R.id.tvTempValue);
            String temp_K = weather.getString("temp");
            float temp_C = Float.parseFloat(temp_K); // Преобразование во флоат
            temp_C = temp_C - (float)273.15;         // Вычисление в кельвинах
            String temp_C_String =Float.toString(temp_C); // Преобразование в стринг
            temp.setText(temp_C_String);  // Итог

            // Вытакскивем давление
            TextView pressure = findViewById(R.id.tvPressureValue);
            pressure.setText(weather.getString("pressure")); // Итог

            // Вытаскиваем восход
            TextView sunrise = findViewById(R.id.tvTimeSunrise);
            String timeSunrise = systemData.getString("sunrise");

            Locale myLocale = new Locale("ru","RU"); // Локализуем
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", myLocale);

            // Марафетим дату (форматируем данные)
            String dateString = formatter.format(new Date(Long.parseLong(timeSunrise) * 1000 + (60 * 60 * 1000) * 3));
            sunrise.setText(dateString);    // Итог

            // Вытаскиваем закат
            TextView sunset = findViewById(R.id.tvTimeSunset);
            String timeSunset= systemData.getString("sunset");

            // Марафетим дату (форматируем данные)
            dateString = formatter.format(new Date(Long.parseLong(timeSunset) * 1000 + (60 * 60 * 1000) * 3));
            sunset.setText(dateString);    // Итог


        } catch (JSONException e){
            e.printStackTrace();
        }
        // --------------------------------------------------------------
    }
}