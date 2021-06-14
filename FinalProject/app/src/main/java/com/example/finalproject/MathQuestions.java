package com.example.finalproject;

import androidx.annotation.IntRange;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MathQuestions extends AppCompatActivity {
    int count = 5;
    TextView question, left;
    EditText answer;
    int ans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math);

        question = findViewById(R.id.question);
        answer = findViewById(R.id.answer);
        left = findViewById(R.id.left);

        newQuesion();
    }

    public void check(View view) {
        if(Integer.parseInt(answer.getText().toString()) == ans) {
            newQuesion();
            count--;
            left.setText("剩下" + count + "題");
        }
        answer.setText("");

        if(count <= 0) {
            MainActivity.r.stop();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void newQuesion() {
        int a = (int)(Math.random() * 100 + 1), b = (int)(Math.random() * 100 + 1);
        ans = a + b;
        question.setText(a + " + " + b);
    }
}