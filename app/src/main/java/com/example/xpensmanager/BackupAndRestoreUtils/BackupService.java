package com.example.xpensmanager.BackupAndRestoreUtils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.xpensmanager.Database.ExpenseDB;
import com.example.xpensmanager.R;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackupService extends Service {
    private NotificationManagerCompat manager;
    private NotificationCompat.Builder builder;
    private ExecutorService executorService;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()->{
            BackupExportRestoreUtil backupExportRestoreUtil = new BackupExportRestoreUtil(getApplicationContext());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "Other Notifications";
                String description = "Backup Notification";
                int importance = NotificationManager.IMPORTANCE_LOW;
                NotificationChannel channel = new NotificationChannel("1", name, importance);
                channel.setDescription(description);
                NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
            manager =  NotificationManagerCompat.from(getApplicationContext());
            builder = new NotificationCompat.Builder(getApplicationContext(),"1");
            builder.setContentTitle("Backup")
                    .setSmallIcon(R.drawable.check)
                    .setPriority(NotificationCompat.PRIORITY_LOW);
            builder.setProgress(100,0,true);
            manager.notify(1,builder.build());

            String message = backupExportRestoreUtil.createBackUp();

            if(message.contains("storage")){
                sharedPref = getApplicationContext().getSharedPreferences(
                        getString(R.string.preference_file_key), getApplicationContext().MODE_PRIVATE);
                editor = sharedPref.edit();
                editor.putString("lastBackupDate", ExpenseDB.dateToString(new Date()));
                editor.apply();
                builder.setContentTitle("Backup created successfully");
            }else{
                builder.setContentTitle("Failed to create backup");
            }
            builder.setContentText(message);
            builder.setProgress(0,0,false);
            manager.notify(1,builder.build());
            //getApplicationContext().stopService(new Intent(getApplicationContext(),BackupService.class));
        });
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("Backup Service","OnDestroy Called");
        super.onDestroy();
    }
}
