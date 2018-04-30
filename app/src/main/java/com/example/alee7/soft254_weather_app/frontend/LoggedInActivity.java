package com.example.alee7.soft254_weather_app.frontend;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.alee7.soft254_weather_app.R;

/**
 * Created by alee7 on 18/04/2018.
 */

public class LoggedInActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private FrameLayout FrameMain;
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
        recordFragment = new RecordFragment();
        mapFragment = new MapFragment();
        recordFragment = new RecordFragment();
        setFragment(recordFragment);    //Set the fragment to recordFragment when the app starts

    }

    //Set fragment dependant on navbar selection
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

    public void setFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.FrameMain, fragment);  //Assign fragment to the FrameLayout
        transaction.commit();   //Apply changes
    }

}
