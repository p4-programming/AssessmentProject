package com.deepak.assesment.worker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.deepak.assesment.R;

public class FetchDataWorker extends Worker {

    private final Context context;

    public FetchDataWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Log.d("FetchDataWorker", "Worker executed at: " + System.currentTimeMillis());

            // Simulate network delay
            Thread.sleep(3000);

            // Show a notification upon completion
            showNotification();

            return Result.success();
        } catch (InterruptedException e) {
            Log.e("FetchDataWorker", "Error while fetching data: " + e.getMessage());
            return Result.failure();
        }
    }

    private void showNotification() {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "FETCH_DATA_CHANNEL",
                    "Background Task Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications for background tasks");
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new Notification.Builder(context, "FETCH_DATA_CHANNEL")
                .setContentTitle("Task Completed")
                .setContentText("Background task completed.")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .build();

        notificationManager.notify(1, notification);
    }
}
