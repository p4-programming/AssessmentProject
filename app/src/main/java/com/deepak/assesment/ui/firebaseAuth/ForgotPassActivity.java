package com.deepak.assesment.ui.firebaseAuth;

import android.content.Context;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.deepak.assesment.R;
import com.deepak.assesment.utils.MyNetworkUtils;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class ForgotPassActivity extends AppCompatActivity {
    private EditText emailInput;
    private MaterialButton reset_btn;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mContext = this;
        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Find views
        emailInput = findViewById(R.id.email_input);
        reset_btn = findViewById(R.id.reset_btn);
        progressBar = findViewById(R.id.progressBar);

        // Handle reset password button click
        reset_btn.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String email = emailInput.getText().toString().trim();


            if (!MyNetworkUtils.isNetworkAvailable(mContext)){
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "No internet connection. Please check your network.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (email.isEmpty()) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }


            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(this, "Password reset email sent!", Toast.LENGTH_SHORT).show();
                            finish(); // Close the ForgotPasswordActivity
                        } else {
                            progressBar.setVisibility(View.GONE);
                            String errorMessage = getFirebaseErrorMessage(task.getException());
                            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private String getFirebaseErrorMessage(Exception exception) {
        if (exception == null) {
            return "Unknown error occurred. Please try again.";
        }

        if (exception instanceof FirebaseAuthWeakPasswordException) {
            return "Your password is too weak. Please use a stronger password.";
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            return "Invalid email address. Please enter a valid email.";
        } else if (exception instanceof FirebaseAuthUserCollisionException) {
            return "This email is already registered. Please log in or use a different email.";
        } else if (exception instanceof FirebaseAuthInvalidUserException) {
            return "No account found with this email. Please register first.";
        }

        // Generic fallback message
        return exception.getMessage() != null ? exception.getMessage() : "An unexpected error occurred.";
    }

}
