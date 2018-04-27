package com.example.alee7.soft254_weather_app.frontend;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.alee7.soft254_weather_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    //Define UI elements
    private EditText textEmail;
    private EditText textPsw;
    private Button signinButton, registerButton;
    private FirebaseAuth firebaseAuth;
    private MediaPlayer mp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Register UI Elements
        textEmail = findViewById(R.id.email_editText);
        textPsw = findViewById(R.id.password_editText);
        signinButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        //Register buttons' OnClickListener
        signinButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);

        //Change the title on the action bar
        setTitle(R.string.welcomeTitle);

        //Get instance from FireBase
        firebaseAuth = FirebaseAuth.getInstance();


    }

    //Handle the action of buttons
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id){

            case R.id.loginButton:
                Login();
                break;

            case R.id.registerButton:
                Register();
                break;
        }
    }

    public void Login() {

        //Display the Progress Dialog while communicating with FireBase server
        ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, getString(R.string.Please_Wait), getString(R.string.Processing), true);

        //Prevent crash due to submission with empty String of textEmail and textPsw
        try{
            (firebaseAuth.signInWithEmailAndPassword(textEmail.getText().toString().trim(), textPsw.getText().toString().trim()))
                    .addOnCompleteListener(task -> {

                        //Close the Progress Dialog
                        progressDialog.dismiss();

                        //Switch activity once logged in successfully
                        if (task.isSuccessful()) {
                            startActivity(new Intent(MainActivity.this, LoggedInActivity.class));
                        }else{

                            //play the error sound
                            PlaySound(R.raw.error);

                            //Play the shake animation on both EdteText
                            textEmail.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake));
                            textPsw.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake));

                            //Make a Snackbar to notify user
                            Snackbar.make(findViewById(android.R.id.content), R.string.wrongLogin, Snackbar.LENGTH_SHORT).show();
                        }
                    });

        }catch(IllegalArgumentException e){

            //Close the Progress Dialog
            progressDialog.dismiss();

            //Show a Snackbar to ask user to fill in email and password
            Snackbar.make(this.findViewById(android.R.id.content), R.string.emptyData, Snackbar.LENGTH_SHORT).show();

            //Play the error sound
            PlaySound(R.raw.error);

            //Play the shake animation on both EdteText
            textEmail.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake));
            textPsw.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake));

            Log.e(getString(R.string.Error), e.toString());
        }
    }

    public void Register(){

        //Display the Progress Dialog while communicating with FireBase server
        ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, getString(R.string.Please_Wait), getString(R.string.Processing), true);

        //Prevent crash due to submission with empty String of textEmail and textPsw
        try{
            (firebaseAuth.createUserWithEmailAndPassword(textEmail.getText().toString().trim(), textPsw.getText().toString().trim()))
                    .addOnCompleteListener(task -> {

                        //Close the Progress Dialog
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {

                            //Show a Toast to notify user the registration has been done
                            Toast.makeText(MainActivity.this, R.string.AC_Successful, Toast.LENGTH_LONG).show();

                            //Switch to the next activity
                            startActivity(new Intent(MainActivity.this, MainActivity.class));
                        }else{

                            //play the error sound
                            PlaySound(R.raw.error);

                            //Play the shake animation on both EdteText
                            textEmail.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake));
                            textPsw.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake));

                            //Check whether user enters an email address
                            if(isEmailValid(textEmail.getText().toString()))
                            {
                                //If yes, it's an existing email
                                Snackbar.make(findViewById(android.R.id.content), R.string.wrongReg, Snackbar.LENGTH_SHORT).show();
                            }else
                                //If no, ask user to input an email address instead
                                Snackbar.make(findViewById(android.R.id.content), R.string.notAnEmail, Snackbar.LENGTH_SHORT).show();

                        }
                    });

        }catch(IllegalArgumentException e){

            //Close the progress dialog
            progressDialog.dismiss();

            ////Show a Snackbar to ask user to fill in email and password
            Snackbar.make(this.findViewById(android.R.id.content), R.string.emptyData, Snackbar.LENGTH_SHORT).show();

            //Play the error sound
            PlaySound(R.raw.error);

            //Play the shake animation on both EdteText
            textEmail.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake));
            textPsw.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake));
            
            Log.e(getString(R.string.Error), e.toString());
        }
    }

    public void PlaySound(int resid){

        //Register the MediaPlayer with resource file
        mp = MediaPlayer.create(this, resid);

        //Play the sound
        mp.start();

        //Check when the audio ends
        mp.setOnCompletionListener(mp -> {

            //Release the MediaPlayer otherwise exception will occur
            mp.release();
        });
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}




