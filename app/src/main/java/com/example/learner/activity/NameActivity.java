package com.example.learner.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learner.R;

public class NameActivity extends AppCompatActivity {

    EditText et_label;
    Button btn_label;
    String label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        et_label = (EditText) findViewById(R.id.et_label);
        btn_label = (Button) findViewById(R.id.btn_label);

        btn_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                label = et_label.getText().toString();
                Intent intent = new Intent(NameActivity.this, TrainActivity.class);
                intent.putExtra("label", label);
                startActivity(intent);
            }
        });
    }
}