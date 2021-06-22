package com.example.finalproject;

import android.content.Context;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.widget.ProgressBar;

import java.util.Calendar;

public class CountDown {
    public interface Command
    {
        public void execute();
    }

    public static CountDownTimer timer(Command command, Context context, ProgressBar progressBar, int totalTime, Calendar startTime){
        return new CountDownTimer(totalTime,10) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressBar.setProgress(100 - ((int)(Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis()) * 100 / totalTime));
            }

            @Override
            public void onFinish() {
                progressBar.setProgress(0);

                command.execute();

                AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
                audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamVolume(AudioManager.STREAM_RING) + 1, AudioManager.AUDIOFOCUS_NONE);
            }
        };
    }
}
