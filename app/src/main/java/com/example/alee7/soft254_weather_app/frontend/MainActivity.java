package com.example.alee7.soft254_weather_app.frontend;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.alee7.soft254_weather_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {



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

        setTitle(R.string.welcomeTitle);

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

        ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, getString(R.string.Please_Wait), getString(R.string.Processing), true);

        try{
            (firebaseAuth.signInWithEmailAndPassword(textEmail.getText().toString().trim(), textPsw.getText().toString().trim()))
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            Intent i = new Intent(MainActivity.this, LoggedInActivity.class);
                            startActivity(i);
                        }
                    }
                });
        }catch(IllegalArgumentException e){
            progressDialog.dismiss();
            Snackbar.make(this.findViewById(android.R.id.content), "Please enter your email and password!", Snackbar.LENGTH_SHORT).show();
            //Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            Log.e(getString(R.string.Error), e.toString());
        }
    }

    public void Register(){
        ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, getString(R.string.Please_Wait), getString(R.string.Processing), true);

        try{
            (firebaseAuth.createUserWithEmailAndPassword(textEmail.getText().toString().trim(), textPsw.getText().toString().trim()))
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, R.string.AC_Successful, Toast.LENGTH_LONG).show();
                            Intent i = new Intent(MainActivity.this, MainActivity.class);
                            startActivity(i);
                        }
                    }
                });
        }catch(IllegalArgumentException e){
            progressDialog.dismiss();
            Snackbar.make(this.findViewById(android.R.id.content), "Please enter your email and password!", Snackbar.LENGTH_SHORT).show();
            //Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            Log.e(getString(R.string.Error), e.toString());
        }
    }

}




