package com.deepak.assesment.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.deepak.assesment.worker.FetchDataWorker;

import java.util.concurrent.TimeUnit;

public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d("BootReceiver", "Device rebooted. Scheduling periodic work.");

            // Reschedule the periodic work after reboot
            PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(
                    FetchDataWorker.class, 15, TimeUnit.MINUTES)
                    .setInitialDelay(0, TimeUnit.MILLISECONDS)
                    .build();

            // Enqueue the work
            WorkManager.getInstance(context).enqueue(periodicWorkRequest);
        }
    }
}
