package com.jitender.xpensmanager.BackupAndRestoreUtils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.jitender.xpensmanager.R;

import java.util.Calendar;

public class AutomaticBackupManager extends BroadcastReceiver {

    public void setAutomaticBackup(Context context){
        ComponentName receiver = new ComponentName(context, AutomaticBackup.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        String preference;
        SharedPreferences pref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), context.MODE_PRIVATE);
        preference = pref.getString("frequency","None");
        Log.d("AutomaticBackupPref",preference);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY,2);
        calendar.set(Calendar.MINUTE,0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context,AutomaticBackup.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,1330,intent,PendingIntent.FLAG_ONE_SHOT);
        //am.set(AlarmManager.RTC,System.currentTimeMillis(),pendingIntent);

        //{"None","Daily","Weekly","Monthly"}
        if(preference.equalsIgnoreCase("Daily")){
            am.setRepeating(AlarmManager.RTC,System.currentTimeMillis(),1000*60*60*24,pendingIntent);
        }
        else if(preference.equalsIgnoreCase("Weekly")) {
            am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 1000 * 60 * 60 * 24 * 7, pendingIntent);
        }
        else if(preference.equalsIgnoreCase("Monthly")) {
            am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 1000 * 60 * 60 * 24 * 30, pendingIntent);
        }
        else {
            cancelAutomaticBackup(context);
        }
        Toast.makeText(context,"Automatic backup set to : "+preference,Toast.LENGTH_SHORT).show();
    }

    public void cancelAutomaticBackup(Context context){
        ComponentName receiver = new ComponentName(context, AutomaticBackup.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context,AutomaticBackup.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,1330,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        am.cancel(pendingIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        setAutomaticBackup(context);
    }
}
