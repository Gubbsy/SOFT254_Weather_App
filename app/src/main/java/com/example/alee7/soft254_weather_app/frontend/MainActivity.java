package com.example.alee7.soft254_weather_app.frontend;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.alee7.soft254_weather_app.R;

import junit.framework.Test;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private FrameLayout FrameMain;
    //private RecordFragment recordFragment;
    private MapFragment mapFragment;
    private RecordFragment recordFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        //Initialize FrameLayout
        FrameMain = findViewById(R.id.FrameMain);

        //Initialize fragments
        //recordFragment = new RecordFragment();
        mapFragment = new MapFragment();
        recordFragment = new RecordFragment();
        setTitle("Something About Weather");
        setFragment(recordFragment);    //Set the fragment to recordFragment when the app starts

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_record:
                setFragment(recordFragment);
                return true;
            case R.id.navigation_map:
                setFragment(mapFragment);
                return true;
        }
        return false;
    }

    public void setFragment(Fragment fragment)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.FrameMain, fragment);  //Assign fragment to the FrameLayout
        transaction.commit();   //Apply changes
    }

}
