package com.example.finalproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class MainActivity extends AppCompatActivity {
    static String sharedPrefFile = "com.example.android.finalproject";
    static String MODE_KEY = "mode";
    static Ringtone r;
    static SharedPreferences mPreferences;
    private String HOUR_KEY = "hour", MINUTE_KEY = "minute";
    private ArrayList<String> WEEKDAY_KEY = new ArrayList<>();
    private NotificationManager mNotificationManager;
    ArrayList<LinearLayout> mode = new ArrayList<>();
    Map<String, Boolean> weekday = new HashMap<>();
    Map<String, Button> weekdayBtn = new HashMap<>();
    int currentMode;
    TimePicker timePicker;
    Boolean demo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weekdayBtn.put(getString(R.string.sundayTag), findViewById(R.id.btnSunday));
        weekdayBtn.put(getString(R.string.mondayTag), findViewById(R.id.btnMonday));
        weekdayBtn.put(getString(R.string.tuesdayTag), findViewById(R.id.btnTuesday));
        weekdayBtn.put(getString(R.string.wednesdayTag), findViewById(R.id.btnWednesday));
        weekdayBtn.put(getString(R.string.thursdayTag), findViewById(R.id.btnThursday));
        weekdayBtn.put(getString(R.string.fridayTag), findViewById(R.id.btnFriday));
        weekdayBtn.put(getString(R.string.saturdayTag), findViewById(R.id.btnSaturday));
        WEEKDAY_KEY = new ArrayList<String>(
                Arrays.asList(getString(R.string.sundayTag),
                        getString(R.string.mondayTag),
                        getString(R.string.tuesdayTag),
                        getString(R.string.wednesdayTag),
                        getString(R.string.thursdayTag),
                        getString(R.string.fridayTag),
                        getString(R.string.saturdayTag)));

        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        currentMode = mPreferences.getInt(MODE_KEY, 0);
        WEEKDAY_KEY.forEach((weekday_key) -> {
            weekday.put(weekday_key, mPreferences.getBoolean(weekday_key, true));
            weekdayBtn.get(weekday_key).setBackgroundColor(getColor(weekday.get(weekday_key) ? R.color.weekdaySelected : R.color.weekdayUnselected));
        });

        mode.add(findViewById(R.id.modeClockLayout));
        mode.add(findViewById(R.id.modeShakeLayout));
        mode.add(findViewById(R.id.modeMathLayout));
        mode.add(findViewById(R.id.modeMemorizeLayout));
        mode.get(currentMode).setBackground(getDrawable(R.drawable.mode_selected));

        timePicker = (TimePicker)findViewById(R.id.timePicker);
        timePicker.setHour(mPreferences.getInt(HOUR_KEY, 0));
        timePicker.setMinute(mPreferences.getInt(MINUTE_KEY, 0));
        /*timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                setAlarm();
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // ??????????????????menu???????????????
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.sound_settings) {
            Toast.makeText(this, "????????????????????????????????????", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_SOUND_SETTINGS));
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(r != null && currentMode == 0) {
            r.stop();
        }

        //start mission
        if(r != null && r.isPlaying()) {
            startAct();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();

        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putInt(MODE_KEY, currentMode);
        preferencesEditor.putInt(HOUR_KEY, timePicker.getHour());
        preferencesEditor.putInt(MINUTE_KEY, timePicker.getMinute());
        WEEKDAY_KEY.forEach((value) -> preferencesEditor.putBoolean(value, weekday.get(value)));
        preferencesEditor.apply();

        setAlarm();
    }

    public void changeMode(View view) {
        mode.get(currentMode).setBackground(getDrawable(R.drawable.border));
        currentMode = mode.indexOf(view);
        view.setBackground(getDrawable(R.drawable.mode_selected));
    }

    public void weekdayClick(View view) {
        String day = view.getTag().toString();
        weekday.put(day, !weekday.get(view.getTag()));
        view.setBackgroundColor(getColor(weekday.get(day) ? R.color.weekdaySelected : R.color.weekdayUnselected));
    }

    public void setAlarm() {
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        intent.putExtra("mode", currentMode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        //check there is on at least one weekday
        Boolean active = false;
        for (Map.Entry<String, Boolean> entry : weekday.entrySet()) {
            active |= entry.getValue();
        }
        if(!active || demo)
            return;

        //set alarm time
        Calendar alarm = Calendar.getInstance();

        alarm.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
        alarm.set(Calendar.MINUTE, timePicker.getMinute());
        alarm.set(Calendar.SECOND, 0);

        //get next weekday
        while (alarm.before(Calendar.getInstance()) || !weekday.get(WEEKDAY_KEY.get(alarm.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY))) {
            alarm.add(Calendar.DATE, 1);
        }
        //Toast.makeText(this, alarm.get(Calendar.DATE) + "", Toast.LENGTH_SHORT).show();

        alarmManager.set(AlarmManager.RTC_WAKEUP, alarm.getTimeInMillis(), pendingIntent);

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createNotificationChannel(mNotificationManager);

        if(!(r != null && r.isPlaying())) {
            //?????????????????????????????????
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Alarm");
            builder.setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Alarm")
                    .setContentText(String.format("????????????????????? %02d/%02d(%s) %02d:%02d",
                            alarm.get(Calendar.MONTH) + 1, alarm.get(Calendar.DATE),
                            weekdayBtn.get(WEEKDAY_KEY.get(alarm.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY)).getText(),
                            alarm.get(Calendar.HOUR_OF_DAY), alarm.get(Calendar.MINUTE)))
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setDefaults(0);
            mNotificationManager.notify(0, builder.build());
        }
    }

    public void demo(View view) {
        demo = true;
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, Math.max(1, audioManager.getStreamVolume(AudioManager.STREAM_RING)), AudioManager.AUDIOFOCUS_NONE);

        //Ringtone
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        r = RingtoneManager.getRingtone(this, notification);
        r.play();

        startAct();
    }

    public void startAct() {
        ArrayList<Class> classes = new ArrayList<Class>();
        classes.add(MainActivity.class);
        classes.add(Shake.class);
        classes.add(MathQuestions.class);
        classes.add(Memorize.class);

        Intent intent = new Intent(this, classes.get(currentMode));
        startActivity(intent);
        finish();
    }

    public static void callAgain(Context context) {
        MainActivity.r.stop();
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Calendar nextCall = Calendar.getInstance();
        nextCall.add(Calendar.SECOND, 1);
        alarmManager.set(AlarmManager.RTC_WAKEUP, nextCall.getTimeInMillis(), pendingIntent);

        NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        MainActivity.createNotificationChannel(mNotificationManager);
    }

    public static void createNotificationChannel(NotificationManager mNotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel("Alarm", "alarm", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.WHITE);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("desc");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }
}