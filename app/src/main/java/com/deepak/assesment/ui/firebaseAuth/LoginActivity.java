package com.deepak.assesment.ui.firebaseAuth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.deepak.assesment.R;
import com.deepak.assesment.ui.MainActivity;
import com.deepak.assesment.utils.MyNetworkUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText emailInput, passwordInput;
    private MaterialButton loginButton;
    private FirebaseAuth auth;
    private TextView signup,forgot_password;
    private Context mContext;
    private ProgressBar progressBar;
    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_btn);
        signup = findViewById(R.id.signup);
        progressBar = findViewById(R.id.progressBar);
        forgot_password = findViewById(R.id.forgot_password);

        auth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // From google-services.json
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        loginButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (!MyNetworkUtils.isNetworkAvailable(mContext)){
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "No internet connection. Please check your network.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Invalid Password!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Invalid Email!", Toast.LENGTH_SHORT).show();
                return;
            }


            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(this, MainActivity.class));
                    } else {
                        progressBar.setVisibility(View.GONE);
                        String errorMessage = getFirebaseErrorMessage(task.getException());
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
        });

        findViewById(R.id.btnGoogleSignIn).setOnClickListener(view -> signIn());

        signup.setOnClickListener(v ->{
            startActivity(new Intent(this, RegisterActivity.class));
        });

        forgot_password.setOnClickListener(view -> {
            startActivity(new Intent(this, ForgotPassActivity.class));
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

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign-In was successful
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign-In failed
                String errorMessage = getFirebaseErrorMessage(e);
                Log.e("TAG", "Google Registration failed: " + errorMessage, task.getException());
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign-in success
                        FirebaseUser user = auth.getCurrentUser();
                        startActivity(new Intent(this, MainActivity.class));
                        Toast.makeText(this, "Welcome, " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        // Sign-in failed
                        Log.w("TAG", "signInWithCredential:failure", task.getException());
                        Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
