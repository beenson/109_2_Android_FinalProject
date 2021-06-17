package com.example.finalproject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;

import static com.example.finalproject.MainActivity.r;

public class AlarmReceiver extends BroadcastReceiver {
    private String NotifiChannel = "Alarm";

    @Override
    public void onReceive(Context context, Intent intent) {
        //Vibrate
        Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(2000);

        ArrayList<Class> classes = new ArrayList<Class>();
        classes.add(MainActivity.class);
        classes.add(Shake.class);
        classes.add(MathQuestions.class);
        classes.add(Memorize.class);

        int mode = intent.getIntExtra("mode", 0);
        Class c = classes.get(mode);
        Log.d("mode", Integer.toString(mode) + " " + c.toString());
        //Notification
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent _intent = new Intent(context, classes.get(intent.getIntExtra("mode", 0)));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, _intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotifiChannel);
        builder.setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Alarm")
                .setContentText("Time is up!!")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setDefaults(0);
        mNotificationManager.notify(0, builder.build());

        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, Math.max(1, audioManager.getStreamVolume(AudioManager.STREAM_RING)), AudioManager.AUDIOFOCUS_NONE);

        //Ringtone
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        r = RingtoneManager.getRingtone(context, notification);
        r.play();
    }
}
