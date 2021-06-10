package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.android.finalproject";
    private String MODE_KEY = "mode";
    private ArrayList<String> WEEKDAY_KEY = new ArrayList<>();
    ArrayList<LinearLayout> mode = new ArrayList<>();
    Map<String, Boolean> weekday = new HashMap<>();
    int currentMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Map<String, Button> weekdayBtn = new HashMap<>();
        weekdayBtn.put(getString(R.string.mondayTag), findViewById(R.id.btnMonday));
        weekdayBtn.put(getString(R.string.tuesdayTag), findViewById(R.id.btnTuesday));
        weekdayBtn.put(getString(R.string.wednesdayTag), findViewById(R.id.btnWednesday));
        weekdayBtn.put(getString(R.string.thursdayTag), findViewById(R.id.btnThursday));
        weekdayBtn.put(getString(R.string.fridayTag), findViewById(R.id.btnFriday));
        weekdayBtn.put(getString(R.string.saturdayTag), findViewById(R.id.btnSaturday));
        weekdayBtn.put(getString(R.string.sundayTag), findViewById(R.id.btnSunday));
        WEEKDAY_KEY = new ArrayList<String>(
                Arrays.asList(getString(R.string.mondayTag),
                        getString(R.string.tuesdayTag),
                        getString(R.string.wednesdayTag),
                        getString(R.string.thursdayTag),
                        getString(R.string.fridayTag),
                        getString(R.string.saturdayTag),
                        getString(R.string.sundayTag)));

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
    }

    @Override
    protected void onPause(){
        super.onPause();

        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putInt(MODE_KEY, currentMode);
        WEEKDAY_KEY.forEach((value) -> preferencesEditor.putBoolean(value, weekday.get(value)));
        preferencesEditor.apply();
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
}