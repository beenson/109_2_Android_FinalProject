package com.example.finalproject;

import androidx.annotation.IntRange;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MathQuestions extends AppCompatActivity {
    final int totalTime = 10000;
    int count = 5;
    TextView question, left;
    EditText answer;
    int ans;
    ProgressBar progressBar;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math);

        progressBar = findViewById(R.id.progressBarMath);
        question = findViewById(R.id.question);
        answer = findViewById(R.id.answer);
        left = findViewById(R.id.left);

        newQuesion();
    }

    @Override
    protected void onPause() {
        super.onPause();

        countDownTimer.cancel();
        if(MainActivity.r != null && MainActivity.r.isPlaying()) {
            MainActivity.callAgain(this);
        }
    }

    public void check(View view) {
        try{
            if(Integer.parseInt(answer.getText().toString()) == ans) {
                count--;
                left.setText("剩下" + count + "題");
                newQuesion();
            }

            answer.setText("");
            startTimer();

            if(count <= 0) {
                MainActivity.r.stop();

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }catch (NumberFormatException e){

        }
    }

    public void newQuesion() {
        int a = (int)(Math.random() * 100 + 1), b = (int)(Math.random() * 100 + 1);
        ans = a + b;
        question.setText(a + " + " + b);
        startTimer();
    }

    public class Finish implements CountDown.Command
    {
        public void execute()
        {
            startTimer();
            newQuesion();
        }
    }

    private void startTimer() {
        if(countDownTimer != null)
            countDownTimer.cancel();

        countDownTimer = CountDown.timer(new Finish(), this, progressBar, totalTime, Calendar.getInstance());
        countDownTimer.start();
    }
}