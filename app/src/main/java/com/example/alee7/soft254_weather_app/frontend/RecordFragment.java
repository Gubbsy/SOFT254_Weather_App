package com.example.alee7.soft254_weather_app.frontend;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alee7.soft254_weather_app.R;
import com.example.alee7.soft254_weather_app.backend.RecordItem;
import com.example.alee7.soft254_weather_app.enumerator.WeatherType;
import com.example.alee7.soft254_weather_app.enumerator.WindDirection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static android.content.Context.SENSOR_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment implements SensorEventListener, LocationListener, View.OnClickListener {

    private RecordItem recordItem;
    private Boolean sensorHasRecorded = false;

    private Button buttonSubmit, buttonClear;

    private EditText editTextFeelsLike, editTextWindSpeed;
    private Spinner spinnerWeatherType, spinnerWindDirection;
    private TextView textViewPressure, textViewRecordedTemp;

    private WeatherType weatherType = null;
    private WindDirection windDirection = null;
    private double feelsLike, windSpeed, pressure, recordedTemp = 0;

    private SensorManager sensorManager;
    private Sensor pressureSensor;

    private LocationManager locationManager;

    private double lat, lon;

    //Firebase
    private FirebaseFirestore fbData = FirebaseFirestore.getInstance();
    private CollectionReference dbRef = fbData.collection("weather-info");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record, container, false);

        //Register UI Components
        buttonSubmit = view.findViewById(R.id.button_submit);
        buttonClear = view.findViewById(R.id.button_clear);
        editTextFeelsLike = view.findViewById(R.id.feels_like_editText);
        editTextWindSpeed = view.findViewById(R.id.wind_speed_editText);
        spinnerWeatherType = view.findViewById(R.id.weather_type_spinner);
        spinnerWindDirection = view.findViewById(R.id.wind_direction_spinner);
        textViewPressure = view.findViewById(R.id.pressure_recorded);
        textViewRecordedTemp = view.findViewById(R.id.temp_recorded);

        buttonSubmit.setOnClickListener(this);
        buttonClear.setOnClickListener(this);

        //Assign spinner enums and onclick events
        spinnerWeatherType.setAdapter(new ArrayAdapter<WeatherType>(getActivity(), R.layout.spinner, weatherType.values()));
        spinnerWindDirection.setAdapter(new ArrayAdapter<WindDirection>(getActivity(), R.layout.spinner, windDirection.values()));

        //Check Permissions for sensor use
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else{
            Log.i(TAG, "Permissions enabled");
        }

        ////////////////////////////////GPS SENSOR REGISTER/////////////////////////////////////////

        //Assign Location Manager and Location
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        onLocationChanged(location);

        ///////////////////////////////PRESSURE SENSOR REGISTER////////////////////////////////////

        //Register Sensors
        sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);

        //Check if device has a Pressure Sensor. If not, return error message.
        if(sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) {
            pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        } else {
            Log.i(TAG, "Device has no Pressure Sensor built-in!");
            textViewPressure.setText("Sensor not found.");
        }

        return view;
    }


    public RecordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        //sensorManager.registerListener(this, tempSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    ///////////////////////////////LOCATION OVERRIDE METHODS////////////////////////////////////////

    @Override
    public void onLocationChanged(Location location) {
            lat =location.getLatitude();
            lon = location.getLongitude();
            Log.i(TAG, "Long: " + lon + "\nLat" + lat);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    /////////////////////////////////SENSOR OVERRIDE METHODS////////////////////////////////////////

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_PRESSURE && !sensorHasRecorded)
        {
            pressure = event.values[0];
            textViewPressure.setText(String.format("%.1f mbar", event.values[0]));
            sensorHasRecorded = true;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id){
            case R.id.button_submit:
                Log.i(TAG,"Submit Button Pressed");

                if(editTextFeelsLike.getText().toString().trim().length() > 0 && editTextWindSpeed.getText().toString().trim().length() > 0 && (Double.parseDouble(editTextFeelsLike.getText().toString()) <= 100 || Double.parseDouble(editTextFeelsLike.getText().toString()) >= -100)){

                    feelsLike = Double.parseDouble(editTextFeelsLike.getText().toString());
                    windSpeed = Double.parseDouble(editTextWindSpeed.getText().toString());
                    weatherType = WeatherType.getEnumByIndex(spinnerWeatherType.getSelectedItemPosition());
                    windDirection = WindDirection.getEnumByIndex(spinnerWindDirection.getSelectedItemPosition());
                    recordedTemp = 0.0;

                    recordItem = new RecordItem(feelsLike, weatherType, windDirection, windSpeed, pressure, recordedTemp, lat,lon);

                    Log.i(TAG, "Record Item feelsLike: " + recordItem.getFeelsLike());
                    Log.i(TAG, "Record Item weather type: " + recordItem.getWeatherType().toString());
                    Log.i(TAG, "Record Item wind Direction: " + recordItem.getWindDirection().toString());
                    Log.i(TAG, "Record Item wind speed: " + recordItem.getWindSpeed());
                    Log.i(TAG, "Record Item pressure: " + recordItem.getLocalPressure());
                    Log.i(TAG, "Record Item temp: " + recordItem.getRecordedTemp());
                    Log.i(TAG, "Record Item Lat: " + recordItem.getLatitude());
                    Log.i(TAG, "Record Item Lon: : " + recordItem.getLongitude());

                    GeoPoint geoLocation = new GeoPoint(recordItem.getLatitude(), recordItem.getLongitude());

                    Map<String, Object> submitRef = new HashMap<>();

                    submitRef.put("location", geoLocation);
                    submitRef.put("pressure", recordItem.getLocalPressure());
                    submitRef.put("recorded-temp", recordItem.getRecordedTemp());
                    submitRef.put("user-temp", recordItem.getFeelsLike());
                    submitRef.put("weather-type", recordItem.getWeatherType().getPosition());
                    submitRef.put("wind-direction", recordItem.getWindDirection().getPosition());
                    submitRef.put("wind-speed", recordItem.getWindSpeed());

                    //Pop up a 'Snackbar' to notify the user
                    Snackbar.make(v, R.string.InputSent, Snackbar.LENGTH_SHORT).show();

                    dbRef.add(submitRef).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(getContext(),MainActivity.class);
                            startActivity(intent);
                        }
                    });

                }else
                    Toast.makeText(getActivity(), "Please enter all fields correctly", Toast.LENGTH_SHORT).show();
                break;

            case R.id.button_clear:
                editTextFeelsLike.setText("");
                editTextWindSpeed.setText("");
                sensorHasRecorded = false;
                spinnerWeatherType.setSelection(0);
                spinnerWindDirection.setSelection(0);

                //Pop up a 'Snackbar' to notify the user
                Snackbar.make(v, R.string.InputCleared, Snackbar.LENGTH_SHORT).show();
                break;
        }
    }
}
