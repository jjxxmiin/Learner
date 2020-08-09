package com.example.learner.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learner.R;

import java.util.ArrayList;
import java.util.List;

public class TrainListActivity extends AppCompatActivity {

    private ListView list_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_list);

        list_item = (ListView) findViewById(R.id.list_item);

        List<String> data = new ArrayList<>();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
        list_item.setAdapter(adapter);

        data.add("아빠");
        data.add("엄마");
        data.add("아들");
        data.add("딸");
        adapter.notifyDataSetChanged(); // 저장
    }
}