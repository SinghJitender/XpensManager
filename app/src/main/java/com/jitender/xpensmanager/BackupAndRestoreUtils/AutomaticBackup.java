package com.jitender.xpensmanager.BackupAndRestoreUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

public class AutomaticBackup extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AutomaticBackup","Recieved Intent");
        context.startService(new Intent(context,BackupService.class));
        AutomaticBackupManager manager = new AutomaticBackupManager();
        manager.setAutomaticBackup(context);
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            manager.setAutomaticBackup(context);
        }
    }
}
