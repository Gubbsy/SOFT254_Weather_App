package com.example.alee7.soft254_weather_app.frontend;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

import com.example.alee7.soft254_weather_app.R;
import com.example.alee7.soft254_weather_app.backend.RecordItem;
import com.example.alee7.soft254_weather_app.enumerator.WeatherType;
import com.example.alee7.soft254_weather_app.enumerator.WindDirection;

import static android.content.ContentValues.TAG;
import static android.content.Context.SENSOR_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment implements SensorEventListener{

    private RecordItem recordItem;
    private Boolean sensorHasRecorded = false;

    private Button buttonSubmit = null;
    private EditText editTextFeelsLike, editTextWindSpeed;
    private Spinner spinnerWeatherType, spinnerWindDirection;
    private TextView textViewPressure, textViewRecordedTemp;

    private WeatherType weatherType = WeatherType.HAIL;
    private WindDirection windDirection = WindDirection.W;
    private double feelsLike, windSpeed, pressure, recordedTemp;

    private SensorManager sensorManager;
    private Sensor pressureSensor, tempSensor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record, container, false);

        //Register UI Components
        buttonSubmit = view.findViewById(R.id.button_submit);
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
                feelsLike = Double.parseDouble(editTextFeelsLike.getText().toString());
                windSpeed = Double.parseDouble(editTextWindSpeed.getText().toString());
                weatherType = WeatherType.getEnumByIndex(spinnerWeatherType.getSelectedItemPosition());
                windDirection = WindDirection.getEnumByIndex(spinnerWindDirection.getSelectedItemPosition());
                Log.i(TAG, "Weather Type is: " + weatherType.toString());
                Log.i(TAG, "Wind Direction: " + windDirection.toString());
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
}
