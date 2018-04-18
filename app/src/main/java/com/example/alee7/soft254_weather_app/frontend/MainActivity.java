package com.example.alee7.soft254_weather_app.frontend;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.alee7.soft254_weather_app.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import junit.framework.Test;

public class MainActivity extends AppCompatActivity  {



    private EditText textEmail;
    private EditText textPsw;
    private Button signinButton, registerButton;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        textEmail = findViewById(R.id.email_editText);
        textPsw = findViewById(R.id.password_editText);
        signinButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        firebaseAuth = FirebaseAuth.getInstance();

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Register();
            }
        });
    }

    public void Login() {

        ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "Please wait...", "Proccessing.... ", true);

        (firebaseAuth.signInWithEmailAndPassword(textEmail.getText().toString(), textPsw.getText().toString()))
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(MainActivity.this, LoggedInActivity.class);
                            startActivity(i);
                        } else {
                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("ERROR", task.getException().toString());
                        }
                    }
                });
    }

    public void Register(){
        ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "Please wait...", "Proccessing.... ", true);

        (firebaseAuth.createUserWithEmailAndPassword(textEmail.getText().toString(), textPsw.getText().toString()))
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Account Creation Successful", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(MainActivity.this, MainActivity.class);
                            startActivity(i);
                        } else {
                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("ERROR", task.getException().toString());
                        }
                    }
                });
    }
}




