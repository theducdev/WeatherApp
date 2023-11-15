package com.theduc.weatherapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView cityNameTV, temperatureTV, conditionTV;
    private RecyclerView weatherRV;
    private TextInputEditText cityEdt;
    private ImageView backIV, iconIV, searchIV;
    private ArrayList <WeatherRVModal> weatherRVModalArrayList;
    private WeatherRVAdapter weatherRVAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;

    private String cityName;

    private List<WeatherInfo> weatherInfoArrayList;
    private WeatherInfoAdapter weatherInfoAdapter;

    private RecyclerView weatherInfoRV;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        homeRL = findViewById(R.id.idRLHome);
        loadingPB = findViewById(R.id.idPBloading);
        cityNameTV = findViewById(R.id.idTVCityName);
        temperatureTV = findViewById(R.id.idTVTemperature);
        conditionTV = findViewById(R.id.idTVCondition);
        weatherRV = findViewById(R.id.idRVWeather);
        cityEdt = findViewById(R.id.idEdtCity);
        iconIV = findViewById(R.id.idIVIcon);
        backIV = findViewById(R.id.idIVBack);
        searchIV = findViewById(R.id.idIVSearch);
        weatherRVModalArrayList = new ArrayList<>();
        weatherInfoRV = findViewById(R.id.idRVWeatherInfo);
        weatherInfoArrayList = new ArrayList<>();



        weatherRVAdapter = new WeatherRVAdapter(this, weatherRVModalArrayList);
        weatherRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        weatherRV.setAdapter(weatherRVAdapter);

        weatherInfoAdapter = new WeatherInfoAdapter(this, weatherInfoArrayList);
        weatherInfoRV.setLayoutManager(new LinearLayoutManager(this));
        weatherInfoRV.setAdapter(weatherInfoAdapter);





        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION")!=PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"}, PERMISSION_CODE);
        }else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                cityName = getCityName(location.getLongitude(), location.getLatitude());
                getWeatherInfo(cityName);
            } else {
                Toast.makeText(MainActivity.this, "Cannot retrieve location information", Toast.LENGTH_SHORT).show();
            }
        }


        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = normalizeCityName(cityEdt.getText().toString());

                if (city.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Enter City Name", Toast.LENGTH_SHORT).show();
                }else {
                    cityNameTV.setText(cityName);
                    getWeatherInfo(city);
                }
            }
        });

    }
    public static String normalizeCityName(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return "";
        }
        String trimmedName = fullName.trim();
        String lowerCaseName = trimmedName.toLowerCase();
        String[] nameParts = lowerCaseName.split("\\s+");
        for (int i = 0; i < nameParts.length; i++) {
            String word = nameParts[i];
            if (!word.isEmpty()) {
                nameParts[i] = word.substring(0, 1).toUpperCase() + word.substring(1);
            }
        }
        String normalizedFullName = String.join(" ", nameParts);
        return normalizedFullName;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==PERMISSION_CODE) {
            if (grantResults.length>0 &&  grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double longitude, double latitude) {
        String cityName = "Not found";
        Geocoder gcd =  new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List < Address> addresses = gcd.getFromLocation(latitude, longitude, 10);
            for (Address adr : addresses) {
                if (adr!=null) {
                    String city =adr.getLocality();
                    if (city!=null && !city.equals("")) {
                        cityName = city;
                    }
                }
            }
            if (cityName.equals("Not found")) {
                Log.d("TAG", "CITY NOT FOUND");
                Toast.makeText(this, "User City Not Found..", Toast.LENGTH_SHORT).show();
            }
        }
        catch (IOException e ) {
            e.printStackTrace();
        }
        return cityName;
    }

    private void getWeatherInfo(String cityName) {
        String url = "http://api.weatherapi.com/v1/forecast.json?key=d7a9b27d31884870ba3105745231011&q=" + cityName +"&days=1&aqi=no&alerts=no";
        cityNameTV.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                homeRL.setVisibility(View.VISIBLE);
                weatherRVModalArrayList.clear();
                weatherInfoArrayList.clear();


                try {
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    temperatureTV.setText(temperature+ "°C");

                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(iconIV);
                    conditionTV.setText(condition);
                    if (isDay==1) {
                        Picasso.get().load("https://qnct.edu.vn/hinh-anh-bau-troi-xanh/imager_8_20135_700.jpg").into(backIV);
                    }else {
                        Picasso.get().load("https://thuonghieuvacuocsong.vn/uploads/images/cdtt/sao_moc-1608385718420.jpg").into(backIV);

                    }
                    Double wind_kph = response.getJSONObject("current").getDouble("wind_kph");
                    String wind_dir = response.getJSONObject("current").getString("wind_dir");
                    Double pressure_mb = response.getJSONObject("current").getDouble("pressure_mb");
                    Double precip_mm = response.getJSONObject("current").getDouble("precip_mm");
                    Double humidity = response.getJSONObject("current").getDouble("humidity");
                    Double cloud = response.getJSONObject("current").getDouble("cloud");
                    Double feelslike_c = response.getJSONObject("current").getDouble("feelslike_c");
                    Double vis_km = response.getJSONObject("current").getDouble("vis_km");
                    Double uv = response.getJSONObject("current").getDouble("uv");
                    Double temp_f = response.getJSONObject("current").getDouble("temp_f");
                    Double wind_mph = response.getJSONObject("current").getDouble("wind_mph");
                    Double pressure_in = response.getJSONObject("current").getDouble("pressure_in");
                    Double feelslike_f = response.getJSONObject("current").getDouble("feelslike_f");
                    Double gust_kph = response.getJSONObject("current").getDouble("gust_kph");

                    weatherInfoArrayList.add(new WeatherInfo("Wind Speed (kph): ", Double.toString(wind_kph)));
                    weatherInfoArrayList.add(new WeatherInfo("Wind Speed (mph): ", Double.toString(wind_mph)));
                    weatherInfoArrayList.add(new WeatherInfo("Wind Direction: ", wind_dir));
                    weatherInfoArrayList.add(new WeatherInfo("Temperature (°F): ", Double.toString(temp_f)));
                    weatherInfoArrayList.add(new WeatherInfo("Pressure (mb): ", Double.toString(pressure_mb)));
                    weatherInfoArrayList.add(new WeatherInfo("Pressure (in): ", Double.toString(pressure_in)));
                    weatherInfoArrayList.add(new WeatherInfo("Precipitation (mm): ", Double.toString(precip_mm)));
                    weatherInfoArrayList.add(new WeatherInfo("Humidity (%): ", Double.toString(humidity)));
                    weatherInfoArrayList.add(new WeatherInfo("Cloud Coverage (%): ", Double.toString(cloud)));
                    weatherInfoArrayList.add(new WeatherInfo("Feels Like (°C): ", Double.toString(feelslike_c)));
                    weatherInfoArrayList.add(new WeatherInfo("Feels Like (°F): ", Double.toString(feelslike_f)));
                    weatherInfoArrayList.add(new WeatherInfo("Visibility (km): ", Double.toString(vis_km)));
                    weatherInfoArrayList.add(new WeatherInfo("UV Index: ", Double.toString(uv)));
                    weatherInfoArrayList.add(new WeatherInfo("Wind Gust (kph): ", Double.toString(gust_kph)));

                    weatherInfoAdapter.notifyDataSetChanged();


                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecast0 = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecast0.getJSONArray("hour");
                    for (int i = 0; i < hourArray.length();i++) {
                        JSONObject hourObj = hourArray.getJSONObject(i);

                        String time = hourObj.getString("time");
                        String temper = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");
                        weatherRVModalArrayList.add (new WeatherRVModal(time, temper, img, wind));
                    }

                    weatherRVAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("MyApp", "Error: " + error.toString());
                Toast.makeText(MainActivity.this, "Please enter valid city name..", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);


    }
}
