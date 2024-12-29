package com.deepak.assesment.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.deepak.assesment.ui.firebaseAuth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Install the splash screen
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        // Check if user is logged in and navigate accordingly
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // User is logged in, navigate to MainActivity
            startActivity(new Intent(this, MainActivity.class));
        } else {
            // User is not logged in, navigate to LoginActivity
            startActivity(new Intent(this, LoginActivity.class));
        }

        // Finish SplashActivity to avoid returning to it
        finish();
    }
}
