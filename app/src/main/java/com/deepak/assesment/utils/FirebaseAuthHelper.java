package com.deepak.assesment.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import android.content.Context;

public class FirebaseAuthHelper {

    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;

    public FirebaseAuthHelper(Context context, GoogleSignInClient googleSignInClient) {
        // Initialize FirebaseAuth instance
        firebaseAuth = FirebaseAuth.getInstance();
        this.googleSignInClient = googleSignInClient;
    }

    /**
     * Logs out the currently signed-in user from Firebase Authentication.
     */
    public void logoutUser(Context context) {
        // Get the currently signed-in user
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            // Check if the user signed in with Google
            if (currentUser.getProviderData().get(1).getProviderId().equals("google.com")) {
                // Sign out from Google
                googleSignInClient.signOut().addOnCompleteListener(task -> {
                    firebaseAuth.signOut();
                    // Perform additional cleanup if needed
                    showToast(context, "Logged out from Google authentication.");
                });
            } else {
                // Sign out from email-password authentication
                firebaseAuth.signOut();
                showToast(context, "Logged out from email-password authentication.");
            }
        } else {
            showToast(context, "No user is currently logged in.");
        }
    }

    /**
     * Helper method to show a Toast message.
     */
    private void showToast(Context context, String message) {
        android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show();
    }
}
