package com.example.finalproject;

import androidx.annotation.IntRange;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Calendar;

public class MathQuestions extends AppCompatActivity {
    int count = 5;
    TextView question, left;
    EditText answer;
    int ans;
    int totalTime = 10000;
    ProgressBar progressBar;
    CountDownTimer countDownTimer;
    Calendar startTime;

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

    private void startTimer() {
        startTime = Calendar.getInstance();
        if(countDownTimer != null)
            countDownTimer.cancel();

        countDownTimer=new CountDownTimer(totalTime,10) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressBar.setProgress(100 - ((int)(Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis()) * 100 / totalTime));
            }

            @Override
            public void onFinish() {
                progressBar.setProgress(0);
                startTimer();
                newQuesion();
            }
        };
        countDownTimer.start();
    }
}