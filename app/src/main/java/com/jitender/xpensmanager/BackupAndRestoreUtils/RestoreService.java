package com.jitender.xpensmanager.BackupAndRestoreUtils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.jitender.xpensmanager.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RestoreService extends Service {
    private NotificationManagerCompat managerCompat;
    private NotificationCompat.Builder builder;
    private ExecutorService executorService;
    public static boolean isServiceRunning = true;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()->{
            isServiceRunning = true;
            BackupExportRestoreUtil backupExportRestoreUtil = new BackupExportRestoreUtil(getApplicationContext());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "Other Notifications";
                String description = "Restore from back up";
                int importance = NotificationManager.IMPORTANCE_LOW;
                NotificationChannel channel = new NotificationChannel("3", name, importance);
                channel.setDescription(description);
                NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
            managerCompat = NotificationManagerCompat.from(getApplication());
            builder = new NotificationCompat.Builder(getApplicationContext(),"3");

            builder.setContentTitle("Restoring Data")
                    .setSmallIcon(R.drawable.info)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setProgress(100,0,true);
            managerCompat.notify(3,builder.build());

            String message = backupExportRestoreUtil.restoreFromBackUp();

            if(message.contains("Successfully")) {
                builder.setContentTitle("Restored Successfully");
            }else{
                builder.setContentTitle("Failed to restore");
            }

            builder.setProgress(0,0,false)
                    .setContentText(message);
            managerCompat.notify(3,builder.build());
            getApplicationContext().stopService(new Intent(getApplicationContext(),RestoreService.class));
        });
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        isServiceRunning = false;
        super.onDestroy();
    }
}
