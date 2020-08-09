package com.example.learner.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learner.R;

public class InferActivity extends AppCompatActivity {

    private TextView tv_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        tv_id = (TextView) findViewById(R.id.tv_id);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        tv_id.setText(id);
    }
}