package com.jitender.xpensmanager.BackupAndRestoreUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
