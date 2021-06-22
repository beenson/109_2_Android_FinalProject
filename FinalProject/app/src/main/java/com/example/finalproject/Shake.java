package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TimeUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Provider;
import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;

import static java.lang.Math.abs;
import static java.lang.Math.signum;

public class Shake extends AppCompatActivity implements SensorEventListener {
    final int totalTime = 5000;
    TextView txt;
    int count = 10, last = 0;
    SensorManager mSensorManager;
    Sensor mAccelerometer;
    ProgressBar progressBar;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);

        progressBar = findViewById(R.id.progressBarShake);
        startTimer();

        txt = findViewById(R.id.shakeTxt);
        txt.setText(Integer.toString(count));

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();

        countDownTimer.cancel();
        if(MainActivity.r != null && MainActivity.r.isPlaying()) {
            MainActivity.callAgain(this);
        }
        mSensorManager.unregisterListener(this);
    }

    public class Finish implements CountDown.Command
    {
        public void execute()
        {
            Toast.makeText(Shake.this, "請搖晃手機", Toast.LENGTH_SHORT).show();
            count = 10;
            startTimer();
        }
    }

    private void startTimer() {
        if(countDownTimer != null)
            countDownTimer.cancel();

        countDownTimer = CountDown.timer(new Finish(), this, progressBar, totalTime, Calendar.getInstance());
        countDownTimer.start();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //Log.d("Sensor", String.valueOf(event.values[0]));
        if(last == 0) {
             if(abs(event.values[0]) >= 7) {
                 last = (int)signum(event.values[0]);
                 count--;
                 startTimer();
             }
        } else if(last == signum(event.values[0] * -1) && abs(event.values[0]) >= 7) {
            last = (int)signum(event.values[0]);
            count--;
            startTimer();
        }
        txt.setText(Integer.toString(count));

        if(count <= 0) {
            MainActivity.r.stop();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}