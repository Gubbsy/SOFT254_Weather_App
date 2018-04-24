package com.example.alee7.soft254_weather_app.frontend;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static android.content.Context.SENSOR_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment implements SensorEventListener, LocationListener {

    private RecordItem recordItem;
    private Boolean sensorHasRecorded = false;
    private Boolean canSubmit = true;

    private WeatherType weatherType = null;
    private WindDirection windDirection = null;
    private double feelsLike, windSpeed, pressure = 0;

    private Button buttonSubmit, buttonClear;
    private EditText editTextFeelsLike, editTextWindSpeed;
    private Spinner spinnerWeatherType, spinnerWindDirection;
    private TextView textViewPressure;

    private SensorManager sensorManager;
    private Sensor pressureSensor;
    private LocationManager locationManager;
    private double lat, lon;

    //FIREBASE
    private FirebaseFirestore fbData = FirebaseFirestore.getInstance();
    private CollectionReference dbRef = fbData.collection("weather-info");
    private FirebaseAuth fbAuth = FirebaseAuth.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record, container, false);

        getActivity().setTitle(R.string.recordTitle);

        //Register UI Components
        buttonSubmit = view.findViewById(R.id.button_submit);
        buttonClear = view.findViewById(R.id.button_clear);
        editTextFeelsLike = view.findViewById(R.id.feels_like_editText);
        editTextWindSpeed = view.findViewById(R.id.wind_speed_editText);
        spinnerWeatherType = view.findViewById(R.id.weather_type_spinner);
        spinnerWindDirection = view.findViewById(R.id.wind_direction_spinner);
        textViewPressure = view.findViewById(R.id.pressure_recorded);

        //Assign spinner enums and onclick events
        spinnerWeatherType.setAdapter(new ArrayAdapter<WeatherType>(getActivity(), android.R.layout.simple_list_item_1, weatherType.values()));
        spinnerWindDirection.setAdapter(new ArrayAdapter<WindDirection>(getActivity(), android.R.layout.simple_list_item_1, windDirection.values()));

        //Check Permissions for sensor use
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else{
            Log.i(TAG, "Permissions enabled");
        }

        ////////////////////////////////GPS SENSOR REGISTER/////////////////////////////////////////

        try{
            //Assign Location Manager and Location
            locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
            onLocationChanged(location);
            MapFragment.SetCurentLocation(lat,lon);
        } catch(SecurityException e){
            Toast.makeText(getActivity(), R.string.Enable_Location, Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }


        ///////////////////////////////PRESSURE SENSOR REGISTER////////////////////////////////////

        //Register Sensors
        sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);

        //Check if device has a Pressure Sensor. If not, return error message.
        if(sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) {
            pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        } else {
            Log.i(TAG, getString(R.string.No_Pressure_Sensor));
            textViewPressure.setText(R.string.Sensor_not_Found);
        }


        ////////////////////////////// BUTTON CLICKS LISTENERS//////////////////////////////////////

        //Create recordItem Object when button pressed
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextFeelsLike.getText().toString().trim().length() > 0 && editTextWindSpeed.getText().toString().trim().length() > 0 && canSubmit){

                    canSubmit = false;

                    feelsLike = Double.parseDouble(editTextFeelsLike.getText().toString());
                    windSpeed = Double.parseDouble(editTextWindSpeed.getText().toString());
                    weatherType = WeatherType.getEnumByIndex(spinnerWeatherType.getSelectedItemPosition());
                    windDirection = WindDirection.getEnumByIndex(spinnerWindDirection.getSelectedItemPosition());

                    recordItem = new RecordItem(feelsLike, weatherType, windDirection, windSpeed, pressure, lat,lon);

                    GeoPoint geoLocation = new GeoPoint(recordItem.getLatitude(), recordItem.getLongitude());

                    Map<String, Object> submitRef = new HashMap<>();
                    submitRef.put("location", geoLocation);
                    submitRef.put("pressure", recordItem.getLocalPressure());
                    submitRef.put("user-temp", recordItem.getFeelsLike());
                    submitRef.put("weather-type", recordItem.getWeatherType().getPosition());
                    submitRef.put("wind-direction", recordItem.getWindDirection().getPosition());
                    submitRef.put("wind-speed", recordItem.getWindSpeed());
                    submitRef.put("posterID", fbAuth.getUid());
                    submitRef.put("postTime", Calendar.getInstance().getTime());

                    dbRef.add(submitRef).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            ClearPage();
                            Toast.makeText(getContext(), R.string.Submission_Successful, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                        }
                    });

                    new CountDownTimer(1000*60*30, 1000) {
                        public void onTick(long millisUntilFinished) {}
                        public void onFinish() {
                            Toast.makeText(getContext(), R.string.Can_Submit, Toast.LENGTH_LONG).show();
                            CreatePushNotification();
                            canSubmit = true;
                            buttonSubmit.setEnabled(true);
                        }
                    }.start();

                }else if (!canSubmit){
                    Toast.makeText(getActivity(), R.string.Only_Submit, Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(getActivity(), R.string.Enter_All_Feilds, Toast.LENGTH_SHORT).show();
            }
        });

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClearPage();
            }
        });
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

    public void CreatePushNotification(){
        Intent intent = new Intent(getContext(), LoggedInActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder b = new NotificationCompat.Builder(getContext());

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setTicker("")
                .setContentTitle(getString(R.string.SAW_Needs_You))
                .setContentText("You can record your local weather information again")
                .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent)
                .setContentInfo("Info");


        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, b.build());
    }

    public void ClearPage(){
        editTextFeelsLike.setText("");
        editTextWindSpeed.setText("");
        sensorHasRecorded = false;
        spinnerWeatherType.setSelection(0);
        spinnerWindDirection.setSelection(0);
    }

}
