package com.example.alee7.soft254_weather_app.frontend;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class RecordFragment extends Fragment implements View.OnClickListener, SensorEventListener{
    private RecordItem recordItem;

    private Button buttonSubmit = null;
    private EditText editTextFeelsLike = null;
    private Spinner spinnerWeatherType = null;
    private Spinner spinnerWindDirection = null;
    private EditText editTextWindSpeed = null;
    private TextView textViewPressure = null;
    private TextView textViewRecordedTemp = null;

    private double feelsLike = 0;
    private WeatherType weatherType = WeatherType.HAIL;
    private WindDirection windDirection = WindDirection.W;
    private double windSpeed = 0;
    private double pressure = 0;
    private double recordedTemp = 0;

    private SensorManager sensorManager;
    private Sensor pressureSensor;
    private Sensor tempSensor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record, container, false);

        //Register UI Components
        buttonSubmit = getActivity().findViewById(R.id.button_submit);
        editTextFeelsLike = getActivity().findViewById(R.id.feels_like_editText);
        editTextWindSpeed = getActivity().findViewById(R.id.wind_speed_editText);
        spinnerWeatherType = getActivity().findViewById(R.id.weather_type_spinner);
        spinnerWindDirection = getActivity().findViewById(R.id.wind_direction_spinner);
        textViewPressure = view.findViewById(R.id.pressure_recorded);
        textViewRecordedTemp = view.findViewById(R.id.temp_recorded);

        //Register Sensors
        sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        //tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        //Check if device has a Ambient Temperature Sensor
        if(sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null) {
            tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        } else {
            Log.i(TAG, "Device has no Ambient Temperature Sensor built-in!");
            textViewRecordedTemp.setText("Sensor not found.");
        }

        return view;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_PRESSURE)
        {
            textViewPressure.setText(String.format("%.3f mbar", event.values[0]));
        }
        else if(event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE)
        {
            //textViewRecordedTemp.setText(String.format("%.3f °C", event.values[0]));
            float temperature = event.values[0];
            textViewRecordedTemp.setText(temperature + "°C");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public RecordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, tempSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
