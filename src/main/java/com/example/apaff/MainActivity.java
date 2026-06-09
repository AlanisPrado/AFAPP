package com.example.apaff;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.*;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    TextView latitudeTextView, longitudeTextView, enderecoTextView, climaTextView;
    Button btnBuscarClima, btnVerHistorico;

    FusedLocationProviderClient fusedLocationClient;
    FirebaseFirestore db;

    double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitudeTextView = findViewById(R.id.latitudeTextView);
        longitudeTextView = findViewById(R.id.longitudeTextView);
        enderecoTextView = findViewById(R.id.enderecoTextView);
        climaTextView = findViewById(R.id.climaTextView);

        btnBuscarClima = findViewById(R.id.btnBuscarClima);
        btnVerHistorico = findViewById(R.id.btnVerHistorico);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        db = FirebaseFirestore.getInstance();

        pedirPermissao();
        pegarLocalizacao();

        btnBuscarClima.setOnClickListener(v -> getWeather(latitude, longitude));

        btnVerHistorico.setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));
    }

    private void pedirPermissao() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                100);
    }

    private void pegarLocalizacao() {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {

                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        latitudeTextView.setText("Lat: " + latitude);
                        longitudeTextView.setText("Lon: " + longitude);

                        getAddress(latitude, longitude);
                    }
                });
    }

    private void getAddress(double lat, double lon) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> list = geocoder.getFromLocation(lat, lon, 1);

            if (!list.isEmpty()) {
                enderecoTextView.setText(list.get(0).getAddressLine(0));
            }
        } catch (Exception e) {
            enderecoTextView.setText("Erro endereço");
        }
    }

    private void getWeather(double lat, double lon) {

        RetrofitClient.getApi()
                .getWeather(lat, lon, true)
                .enqueue(new Callback<WeatherResponse>() {

                    @Override
                    public void onResponse(Call<WeatherResponse> call,
                                           Response<WeatherResponse> response) {

                        if (response.body() != null) {

                            CurrentWeather w = response.body().current_weather;

                            climaTextView.setText(
                                    "🌡 " + w.temperature +
                                            "\n🌬 " + w.windspeed +
                                            "\n☁ " + w.weathercode
                            );

                            WeatherRecord record = new WeatherRecord(
                                    lat,
                                    lon,
                                    w.temperature,
                                    w.windspeed,
                                    w.weathercode
                            );

                            record.note = "Consulta salva";

                            db.collection("weather").add(record);
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherResponse> call, Throwable t) {
                        Toast.makeText(MainActivity.this,
                                "Erro API", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}