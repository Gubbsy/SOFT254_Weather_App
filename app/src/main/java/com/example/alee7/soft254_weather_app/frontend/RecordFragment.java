package com.example.alee7.soft254_weather_app.frontend;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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

import static android.content.ContentValues.TAG;
import static android.content.Context.SENSOR_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment implements SensorEventListener, LocationListener{

    private RecordItem recordItem;
    private Boolean sensorHasRecorded = false;
    private Boolean allFieldsFilled = true;

    private Button buttonSubmit, buttonClear;

    private EditText editTextFeelsLike, editTextWindSpeed;
    private Spinner spinnerWeatherType, spinnerWindDirection;
    private TextView textViewPressure, textViewRecordedTemp;

    private WeatherType weatherType = null;
    private WindDirection windDirection = null;
    private double feelsLike, windSpeed, pressure, recordedTemp = 0;
    private double longitude, latitiude = 0;

    private SensorManager sensorManager;
    private Sensor pressureSensor;

    private LocationListener listener;
    private LocationManager locationManager;


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

        //Assign spinner enums and onclick events
        spinnerWeatherType.setAdapter(new ArrayAdapter<WeatherType>(getActivity(), android.R.layout.simple_list_item_1, weatherType.values()));
        spinnerWindDirection.setAdapter(new ArrayAdapter<WindDirection>(getActivity(),android.R.layout.simple_list_item_1,windDirection.values()));





        //Register Sensors
        sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);



        //Check if device has a Pressure Sensor. If not, return error message.
        if(sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) {
            pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        } else {
            Log.i(TAG, "Device has no Pressure Sensor built-in!");
            textViewPressure.setText("Sensor not found.");
        }

        //Create recordItem Object when button pressed
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"Submit Button Pressed");

                if(editTextFeelsLike.getText().toString().trim().length() > 0 && editTextWindSpeed.getText().toString().trim().length() > 0){
                    GetLocation();

                    feelsLike = Double.parseDouble(editTextFeelsLike.getText().toString());
                    windSpeed = Double.parseDouble(editTextWindSpeed.getText().toString());
                    weatherType = WeatherType.getEnumByIndex(spinnerWeatherType.getSelectedItemPosition());
                    windDirection = WindDirection.getEnumByIndex(spinnerWindDirection.getSelectedItemPosition());
                    recordedTemp = 0.0;

                    recordItem = new RecordItem(feelsLike, weatherType, windDirection, windSpeed, pressure, recordedTemp);

                    Log.i(TAG, "Record Item feelsLike: " + recordItem.getFeelsLike());
                    Log.i(TAG, "Record Item weather type: " + recordItem.getWeatherType().toString());
                    Log.i(TAG, "Record Item wind Direction: " + recordItem.getWindDirection().toString());
                    Log.i(TAG, "Record Item wind speed: " + recordItem.getWindSpeed());
                    Log.i(TAG, "Record Item pressure: " + recordItem.getLocalPressure());
                    Log.i(TAG, "Record Item temp: " + recordItem.getRecordedTemp());
                    Log.i(TAG, "Longitude : " + longitude);
                    Log.i(TAG, "Latitude : " + latitiude);


                }else
                    Toast.makeText(getActivity(), "Please enter all fields", Toast.LENGTH_SHORT).show();
            }
        });

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextFeelsLike.setText("");
                editTextWindSpeed.setText("");
                sensorHasRecorded = false;
                spinnerWeatherType.setSelection(0);
                spinnerWindDirection.setSelection(0);
            }
        });


        return view;
    }

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

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG,"Current Location: " + location.getLatitude() + ", " + location.getLongitude());
        longitude = location.getLongitude();
        latitiude = location.getLatitude();

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(getActivity(), "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    private void GetLocation(){
        try {
            Log.i(TAG, "GetLocation called");
            locationManager = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }
}
