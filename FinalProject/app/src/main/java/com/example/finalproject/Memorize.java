package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import static com.google.android.material.internal.ViewUtils.dpToPx;

public class Memorize extends AppCompatActivity {
    Button[][] btn = new Button[4][4];
    Boolean[][] selected = new Boolean[4][4];
    Boolean[][] answer = new Boolean[4][4];
    boolean allowRestart = true;
    int totalTime = 3000;
    ProgressBar progressBar;
    CountDownTimer countDownTimer;
    Calendar startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memorize);

        progressBar = findViewById(R.id.progressBarMemorize);

        //動態生成 (https://thumbb13555.pixnet.net/blog/post/329641828-create-ui-dynamically)
        LinearLayout layout = findViewById(R.id.mLayout);
        ArrayList<LinearLayout> hLayout = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            hLayout.add(linearLayout);

            layout.addView(linearLayout);
            for (int j = 0; j < 4; j++) {
                btn[i][j] = newBtn(String.valueOf(i) + String.valueOf(j));
                linearLayout.addView(btn[i][j]);
            }
        }

        start();
    }

    public void restart(View view) {
        start();
    }

    private void start(){
        if(!allowRestart)
            return;

        if(countDownTimer != null)
            countDownTimer.cancel();
        allowRestart = false;
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                selected[i][j] = false;
                answer[i][j] = Math.random() >= 0.5;
                btn[i][j].setBackgroundColor(answer[i][j] ? getColor(R.color.orange) : getColor(R.color.gray));
                btn[i][j].setOnClickListener(v -> {});
            }
        }

        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                for(int i = 0; i < 4; i++) {
                    for(int j = 0; j < 4; j++) {
                        btn[i][j].setBackgroundColor(getColor(R.color.gray));
                        btn[i][j].setOnClickListener(v -> {
                            clicked(v);
                            startTimer();
                        });
                    }
                }
                allowRestart = true;
            }
        }, 3000);
    }

    private void clicked (View view) {
        int tag = Integer.parseInt(view.getTag().toString());
        int x = tag / 10, y = tag % 10;
        selected[x][y] = !selected[x][y];
        view.setBackgroundColor(selected[x][y] ? getColor(R.color.orange) : getColor(R.color.gray));

        if(check()) {
            MainActivity.r.stop();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    private boolean check(){
        for(int i = 0; i < 4; i++)
            if(!Arrays.equals(answer[i], selected[i]))
                return false;

        return true;
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
                restart(null);
            }
        };
        countDownTimer.start();
    }

    private Button newBtn(String txt) {
        Button button = new Button(this);
        LinearLayout.LayoutParams btnLayout = new LinearLayout.LayoutParams(dpToPx(80), dpToPx(80));
        btnLayout.setMargins(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5));
        button.setLayoutParams(btnLayout);
        button.setBackgroundColor(getColor(R.color.gray));
        button.setTag(txt);
        /*
         <style name="memorizeBtn">
             <item name="android:backgroundTint">@color/gray</item>
             <item name="android:layout_margin">5dp</item>
             <item name="android:layout_width">80dp</item>
             <item name="android:layout_height">80dp</item>
         </style>
         */
        return button;
    }

    private int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}