package com.deepak.assesment.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.deepak.assesment.R;
import com.deepak.assesment.adapter.ItemAdapter;
import com.deepak.assesment.dao.DataBaseHelper;
import com.deepak.assesment.ui.firebaseAuth.LoginActivity;
import com.deepak.assesment.utils.MyNetworkUtils;
import com.deepak.assesment.utils.NetworkReceiver;
import com.deepak.assesment.viewmodel.ItemViewModel;
import com.deepak.assesment.viewmodel.ItemViewModelFactory;
import com.deepak.assesment.worker.FetchDataWorker;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NetworkReceiver.NetworkListener {

    private ProgressBar progressBar;
    private ItemViewModel userViewModel;
    private RecyclerView recyclerView;
    private ItemAdapter userAdapter;
    private NetworkReceiver networkReceiver;
    private boolean isLoading = false;
    private Context context;
    private int currentPage = 1;
    private Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    private final int REQUEST_NOTIFICATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        initializeViews();

        userAdapter = new ItemAdapter(new ArrayList<>()); // Initialize with an empty list
        recyclerView.setAdapter(userAdapter);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if the permission is already granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIFICATION_PERMISSION);
            }
        }

        // Initialize and register the NetworkReceiver
        networkReceiver = new NetworkReceiver(this);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter); // Register to listen for network changes

        // Initialize ViewModel
        ItemViewModelFactory factory = new ItemViewModelFactory(getApplication());
        userViewModel = new ViewModelProvider(this, factory).get(ItemViewModel.class);
        observeViewModel();
        setupPagination();

    }

    private void initializeViews() {
        toolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar = findViewById(R.id.progressBar);
    }

    private void observeViewModel() {
        // Observe data from the ViewModel and update the adapter

        userViewModel.getData().observe(this, users -> {
            if (users != null && !users.isEmpty()) {
                Log.d("MainActivity", "Users data loaded: " + users.size() + " items"); // Debug log
                userAdapter.addUsers(users); // Append new users to the adapter
                isLoading = false; // Data loaded, hide the progress bar
                progressBar.setVisibility(View.GONE); // Hide progress bar
            } else {
                Log.d("MainActivity", "No users loaded or empty list"); // Debug log
            }
        });

        // Observe if the data for the current page already exists in the database
        userViewModel.isDataAlreadyInDatabase(currentPage).observe(this, dataExists -> {
            if (dataExists) {
                isLoading = false;  // Avoid multiple requests

                // Show progress bar when loading
                progressBar.setVisibility(View.GONE);
                // Data already exists for this page, so we don't need to fetch from API
                Log.d("MainActivity", "Data already exists for page " + currentPage);
            } else {
                // Data doesn't exist for this page, fetch from API
                if (!isLoading) {
                    if (!MyNetworkUtils.isNetworkAvailable(context)){
                        Toast.makeText(context, "No internet connection. Please check your network.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    isLoading = true;  // Prevent multiple requests
                    progressBar.setVisibility(View.VISIBLE); // Show loader
                    userViewModel.fetchUsersByPostId(currentPage); // Fetch from API
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId(); // Get the item ID

        if (id == R.id.action_logout) {
            showLogoutConfirmationDialog();
            return true;
        } else {
            return super.onOptionsItemSelected(item); // Default case
        }
    }


    private void showLogoutConfirmationDialog() {
        // Create and display a confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        performLogout(); // Logout when the user confirms
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Dismiss the dialog
                    }
                })
                .show();
    }

    private void performLogout() {
        // Perform Firebase logout
        if (firebaseAuth.getCurrentUser() != null) {
            logout();
        } else {
            Toast.makeText(this, "No user is currently logged in.", Toast.LENGTH_SHORT).show();
        }
    }

    private void logout(){
        firebaseAuth.signOut();
        clearRoomDatabase();
        Toast.makeText(context, "Logout Successfully.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupPagination() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!MyNetworkUtils.isNetworkAvailable(context)){
                    Toast.makeText(context, "No internet connection. Please check your network.", Toast.LENGTH_SHORT).show();
                    return;
                }
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading) {
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                    if (lastVisibleItemPosition >= userAdapter.getItemCount() - 1) {
                        // When reaching the end of the list, load the next page

                        isLoading = true;

                        // Show progress bar when loading
                        progressBar.setVisibility(View.VISIBLE);

                        // Call the ViewModel with the page number (currentPage)
                        userViewModel.fetchUsersByPostId(currentPage);
                        // Increment the page number after the request
                        currentPage++;
                    }
                }
            }
        });
    }


    private void clearRoomDatabase() {
        // Get the Room database instance
        DataBaseHelper db = DataBaseHelper.getDB(this);
        // Clear all tables in the database on a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            db.clearAllTables();
        });
    }

    private void schedulePeriodicWork() {
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(
                FetchDataWorker.class, 15, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork(
                        "FetchDataWork",
                        ExistingPeriodicWorkPolicy.UPDATE,
                        periodicWorkRequest
                );

        Log.d("MainActivity", "Periodic work scheduled.");
    }

    @Override
    public void onNetworkConnected() {
        // Initial data fetch
        userViewModel.fetchUsersByPostId(currentPage);
        setupPagination();
        schedulePeriodicWork();
    }

    @Override
    public void onNetworkDisconnected() {
        if (isLoading){
            isLoading = false;
            progressBar.setVisibility(View.GONE);
        }

        Toast.makeText(this, "No internet connection available.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can send notifications
            } else {
                // Permission denied, handle accordingly
            }
        }
    }

}
