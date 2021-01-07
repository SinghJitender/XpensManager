package com.example.xpensmanager.BackupAndRestoreUtils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.xpensmanager.R;

import java.util.Calendar;

public class AutomaticBackup extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AutomaticBackup","Recieved Intent");
        context.startService(new Intent(context,BackupService.class));
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED") || intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ) {
           AutomaticBackupManager manager = new AutomaticBackupManager();
           manager.setAutomaticBackup(context);
        }
    }
}
