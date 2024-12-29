package com.deepak.assesment.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.util.Log;

public class NetworkReceiver extends BroadcastReceiver {

    // Interface to notify when the internet is available
    public interface NetworkListener {
        void onNetworkConnected();
        void onNetworkDisconnected();
    }

    private NetworkListener networkListener;

    public NetworkReceiver(NetworkListener listener) {
        this.networkListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Check for active network
        Network activeNetwork = cm.getActiveNetwork();
        if (activeNetwork != null) {
            // Get the capabilities of the active network
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(activeNetwork);

            if (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                // If the network has internet capability, it means the device is connected
                Log.d("NetworkReceiver", "Network connected");
                if (networkListener != null) {
                    networkListener.onNetworkConnected(); // Notify MainActivity to trigger the API call
                }
            }
        } else {
            // No network available
            Log.d("NetworkReceiver", "Network disconnected");
            if (networkListener != null) {
                networkListener.onNetworkDisconnected();
            }
        }
    }
}
