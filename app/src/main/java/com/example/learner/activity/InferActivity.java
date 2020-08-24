package com.example.learner.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.learner.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.widget.Toast.LENGTH_LONG;

public class InferActivity extends AppCompatActivity {

    private static final int GET_CAPTURE = 1;

    ImageView iv_infer;
    Button btn_capture, btn_infer;
    Bitmap img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infer);

        iv_infer = (ImageView) findViewById(R.id.iv_infer);

        btn_capture = findViewById(R.id.btn_capture);
        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();
            }
        });

        btn_infer = (Button) findViewById(R.id.btn_infer);
        btn_infer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                connectServer(view);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GET_CAPTURE && resultCode == RESULT_OK) {
            try {
                img = (Bitmap) data.getExtras().get("data");
                if(img != null) {
                    iv_infer.setImageBitmap(img);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(intent, GET_CAPTURE);
    }

    private void connectServer(View v) {

        if (img == null) {
            Toast.makeText(this, "이미지를 선택하세요", LENGTH_LONG).show();
        }
        else {
            // 서버 주소를 만듭니다.
            EditText ipv4AddressView = findViewById(R.id.IPAddress);
            String ipv4Address = ipv4AddressView.getText().toString();
            EditText portNumberView = findViewById(R.id.portNumber);
            String portNumber = portNumberView.getText().toString();

            String postUrl = "http://" + ipv4Address + ":" + portNumber + "/infer";

            // Bitmap을 설정합니다.
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;

            img.compress(Bitmap.CompressFormat.JPEG, 20, stream);
            byte[] byteArray = stream.toByteArray();

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            // 이미지를 서버에 전송합니다.
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", timestamp + ".jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray))
                    .build();

            postRequest(postUrl, requestBody);
        }
    }

    private void postRequest(String postUrl, RequestBody postBody) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();

                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView tv_response = findViewById(R.id.tv_response);
                        tv_response.setText("Failed to Connect to Server");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView tv_response = findViewById(R.id.tv_response);
                        try {
                            tv_response.setText(response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}