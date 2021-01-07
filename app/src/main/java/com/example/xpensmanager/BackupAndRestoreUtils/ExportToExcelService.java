package com.example.xpensmanager.BackupAndRestoreUtils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.xpensmanager.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExportToExcelService extends Service {
private NotificationManagerCompat managerCompat;
private NotificationCompat.Builder builder;
private ExecutorService executorService;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()->{
            BackupExportRestoreUtil backupExportRestoreUtil = new BackupExportRestoreUtil(getApplicationContext());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "Other Notifications";
                String description = "Export To Excel Notification";
                int importance = NotificationManager.IMPORTANCE_LOW;
                NotificationChannel channel = new NotificationChannel("2", name, importance);
                channel.setDescription(description);
                NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
            managerCompat = NotificationManagerCompat.from(getApplicationContext());
            builder = new NotificationCompat.Builder(getApplicationContext(), "2");
            builder.setContentTitle("Exporting to excel")
                    .setSmallIcon(R.drawable.cool)
                    .setPriority(NotificationCompat.PRIORITY_LOW);
            builder.setProgress(100,0,true);
            managerCompat.notify(2,builder.build());

            String message = backupExportRestoreUtil.exportToExcel();

            if(message.contains("storage")) {
                builder.setContentTitle("Exported to excel successfully");
            }else{
                builder.setContentTitle("Failed to export");
            }
            builder.setContentText(message);
            builder.setProgress(0,0,false);
            managerCompat.notify(2,builder.build());
            getApplication().stopService(new Intent(getApplicationContext(), ExportToExcelService.class));
        });

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("ExportToExcel Service","Service Destoryed");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
