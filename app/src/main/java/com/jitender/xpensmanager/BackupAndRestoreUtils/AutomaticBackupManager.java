package com.jitender.xpensmanager.BackupAndRestoreUtils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.android.material.snackbar.Snackbar;
import com.jitender.xpensmanager.R;

import java.util.Calendar;

public class AutomaticBackupManager extends BroadcastReceiver {

    public void setAutomaticBackup(Context context){
        ComponentName receiver = new ComponentName(context, AutomaticBackupManager.class);

        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        SharedPreferences pref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), context.MODE_PRIVATE);
        String preference = pref.getString("frequency","None");

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context,AutomaticBackupManager.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,1330,intent,PendingIntent.FLAG_ONE_SHOT);

        if(preference.equalsIgnoreCase("Daily")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //am.setAndAllowWhileIdle(AlarmManager.RTC,AlarmManager.INTERVAL_DAY,pendingIntent);
                am.setAndAllowWhileIdle(AlarmManager.RTC,System.currentTimeMillis()+(1000 * 60),pendingIntent);
                Toast.makeText(context,"Alarm Set - Daily",Toast.LENGTH_SHORT).show();
            }else{
                am.set(AlarmManager.RTC,AlarmManager.INTERVAL_DAY,pendingIntent);
            }
        }
        else if(preference.equalsIgnoreCase("Weekly")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //am.setAndAllowWhileIdle(AlarmManager.RTC, AlarmManager.INTERVAL_DAY*7, pendingIntent);
                am.setAndAllowWhileIdle(AlarmManager.RTC,System.currentTimeMillis()+(1000 * 60* 3),pendingIntent);
                Toast.makeText(context,"Alarm Set - Weekly",Toast.LENGTH_SHORT).show();
            }else{
                am.set(AlarmManager.RTC,AlarmManager.INTERVAL_DAY*7,pendingIntent);
            }
        }
        else if(preference.equalsIgnoreCase("Monthly")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //am.setAndAllowWhileIdle(AlarmManager.RTC, AlarmManager.INTERVAL_DAY*30, pendingIntent);
                am.setAndAllowWhileIdle(AlarmManager.RTC,System.currentTimeMillis()+(1000 * 60 * 6),pendingIntent);
                Toast.makeText(context,"Alarm Set - Monthly",Toast.LENGTH_SHORT).show();

            }else{
                am.set(AlarmManager.RTC,AlarmManager.INTERVAL_DAY*30,pendingIntent);
            }
        }
        else {
            Toast.makeText(context,"Cancelling Alarm",Toast.LENGTH_SHORT).show();
            if(am != null)
                am.cancel(pendingIntent);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        setAutomaticBackup(context);
        context.startService(new Intent(context,BackupService.class));
        setAutomaticBackup(context);
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            setAutomaticBackup(context);
        }
    }
}
