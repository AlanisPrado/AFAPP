package com.example.apaff;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.apaff.CurrentWeather;
import com.example.apaff.RetrofitClient;
import com.example.apaff.WeatherResponse;
import com.google.android.gms.location.*;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GPSActivity extends AppCompatActivity {

    TextView latitudeTextView, longitudeTextView, enderecoTextView;
    TextView climaTextView;

    FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        latitudeTextView = findViewById(R.id.latitudeTextView);
        longitudeTextView = findViewById(R.id.longitudeTextView);
        enderecoTextView = findViewById(R.id.enderecoTextView);

        climaTextView = findViewById(R.id.climaTextView);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    200);
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {

                    if (location != null) {

                        double lat = location.getLatitude();
                        double lon = location.getLongitude();

                        latitudeTextView.setText("Lat: " + lat);
                        longitudeTextView.setText("Lon: " + lon);

                        getAddress(lat, lon);
                        getWeather(lat, lon); // 🌦 AQUI ENTRA A API

                    } else {
                        Toast.makeText(this,
                                "Sem localização",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    
    private void getAddress(double lat, double lon) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> list = geocoder.getFromLocation(lat, lon, 1);

            if (!list.isEmpty()) {
                enderecoTextView.setText("Endereço: " + list.get(0).getAddressLine(0));
            }

        } catch (IOException e) {
            enderecoTextView.setText("Erro endereço");
        }
    }

    
    private void getWeather(double lat, double lon) {

        RetrofitClient.getApi().getWeather(lat, lon, true)
                .enqueue(new Callback<WeatherResponse>() {
                    @Override
                    public void onResponse(Call<WeatherResponse> call,
                                           Response<WeatherResponse> response) {

                        if (response.body() != null) {

                            CurrentWeather w = response.body().current_weather;

                            String clima =
                                    "🌡 Temp: " + w.temperature +
                                            "\n🌬 Vento: " + w.windspeed +
                                            "\n☁ Código: " + w.weathercode;

                            climaTextView.setText(clima);
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherResponse> call, Throwable t) {
                        climaTextView.setText("Erro ao buscar clima");
                    }
                });
    }
}