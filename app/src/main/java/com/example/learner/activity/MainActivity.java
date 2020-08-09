package com.example.learner.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learner.R;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button btn_train, btn_infer, btn_upload, btn_train_list;
    private EditText et_id;
    private String str_id;
    private ImageView img_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionListener)
                .setRationaleMessage("카메라 권한이 필요합니다.")
                .setDeniedMessage("거부하셨습니다.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

        btn_train = (Button) findViewById(R.id.btn_train);
        btn_infer = (Button) findViewById(R.id.btn_infer);
        btn_upload = (Button) findViewById(R.id.btn_upload);
        btn_train_list = (Button) findViewById(R.id.btn_train_list);

        et_id = (EditText) findViewById(R.id.et_id);

        btn_train.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_id = et_id.getText().toString();
                Intent intent = new Intent(MainActivity.this, TrainActivity.class);
                intent.putExtra("id", str_id);
                startActivity(intent);
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UploadActivity.class);
                startActivity(intent);
            }
        });

        btn_infer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_id = et_id.getText().toString();
                Intent intent = new Intent(MainActivity.this, InferActivity.class);
                intent.putExtra("id", str_id);
                startActivity(intent);
            }
        });

        btn_train_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TrainListActivity.class);
                startActivity(intent);
            }
        });

        img_main = (ImageView) findViewById(R.id.img_main);

        img_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "메인 이미지 입니당", Toast.LENGTH_SHORT).show();
            }
        });
    }

    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(getApplicationContext(), "권한이 허용됨",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(getApplicationContext(), "권한이 거부됨",Toast.LENGTH_SHORT).show();
        }
    };
}