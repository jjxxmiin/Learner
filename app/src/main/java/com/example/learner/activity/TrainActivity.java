package com.example.learner.activity;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.learner.R;
import com.gc.materialdesign.views.ButtonRectangle;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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

public class TrainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_ALBUM = 2;
    private static final int REQUEST_MULTI_IMAGE_ALBUM = 3;
    public static int NUM_IMAGES = 3;

    ImageView iv_select;
    TextView tv_label;
    ButtonRectangle btn_select, btn_send, btn_capture, btn_multi_select;
    Bitmap selectedImage;
    Bitmap[] selectedMultiImage = new Bitmap[NUM_IMAGES];
    String captureImagePath;
    String label;
    Boolean isCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        Intent intent = getIntent();
        label = intent.getStringExtra("label");

        tv_label = (TextView) findViewById(R.id.tv_label);
        tv_label.setText(label);

        btn_send = (ButtonRectangle) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                connectServer(view);
            }
        });

//        btn_multi_select = (Button) findViewById(R.id.btn_multi_select);
//        btn_multi_select.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                selectMultiImage(view);
//            }
//        });

        btn_select = (ButtonRectangle) findViewById(R.id.btn_select);
        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(view);
            }
        });

        btn_capture = (ButtonRectangle) findViewById(R.id.btn_capture);
        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();
            }
        });

        iv_select = findViewById(R.id.iv_select);

    }
    @Override
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_IMAGE_ALBUM: {
                if (data.getData() != null) {
                    try {
                        isCapture = false;
                        Uri dataUri = data.getData();

                        iv_select.setImageURI(dataUri);
                        selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dataUri);
                        // selectedImagePath = PathUtils.getPath(getApplicationContext(), dataUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case REQUEST_IMAGE_CAPTURE: {
                try{
                    isCapture = true;
                    Bitmap captureImage = BitmapFactory.decodeFile(captureImagePath);

                    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String fileName = "picture_" + timestamp + ".jpg";

                    selectedImage = rotate(captureImage);

                    createDirectoryAndSaveFile(selectedImage, fileName);
                    iv_select.setImageBitmap(selectedImage);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            }
            case REQUEST_MULTI_IMAGE_ALBUM: {
                Uri uri = data.getData();
                ClipData clipData = data.getClipData();

                if (clipData != null) { // multi image
                    for (int i=0; i < clipData.getItemCount(); i++) {
                        try {
                            Uri dataUri =  clipData.getItemAt(i).getUri();

                            iv_select.setImageURI(dataUri);

                            selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dataUri);

                            // selectedImagePath = PathUtils.getPath(getApplicationContext(), dataUri);

                            selectedMultiImage[i] = selectedImage;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                else if (uri != null) { // single image
                    iv_select.setImageURI(uri);
                    selectedMultiImage[0] = selectedImage;
                }
            }
        }
    }
    public void selectMultiImage(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), REQUEST_MULTI_IMAGE_ALBUM);
    }
    public void selectImage(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");

        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_IMAGE_ALBUM);
    }
    public void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                captureImagePath = photoFile.getAbsolutePath();
            } catch (IOException e) {
                Toast.makeText(this,
                        "Photo file can't be created, please try again",
                        Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                Uri imgUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName(), photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "picture_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = File.createTempFile(
                fileName,
                ".jpg",
                storageDir
        );
        return photoFile;
    }
    private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {
        String img_folder = "/sdcard/Pictures/Learner";

        File ImageDirectory = new File(img_folder);

        imageToSave = Bitmap.createScaledBitmap(imageToSave, 1080, 1080, false);

        if (!ImageDirectory.exists()) {
            ImageDirectory.mkdirs();
        }

        File img = new File(img_folder, fileName);

        try {
            FileOutputStream out = new FileOutputStream(img);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 20, out);
            sendBroadcast(new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(img)) );
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private Bitmap rotate(Bitmap img) throws IOException {
        Bitmap rotate_img;
        // rotate
        ExifInterface exif = new ExifInterface(captureImagePath);
        int exifOrientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        rotate_img = _rotate(img, _exifOrientationToDegrees(exifOrientation));

        return rotate_img;
    }
    private Bitmap _rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
    private int _exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }
    private void connectServer(View v) {

        if (selectedImage == null) {
            Toast.makeText(this, "이미지를 선택하세요", LENGTH_LONG).show();
        }
        else {
            // 서버 주소를 만듭니다.
            EditText ipv4AddressView = findViewById(R.id.IPAddress);
            String ipv4Address = ipv4AddressView.getText().toString();
            EditText portNumberView = findViewById(R.id.portNumber);
            String portNumber = portNumberView.getText().toString();
            String postUrl;

            if(isCapture){
                postUrl = "http://" + ipv4Address + ":" + portNumber + "/train/capture";
            }
            else {
                postUrl = "http://" + ipv4Address + ":" + portNumber + "/train/gallery";
            }

            // Bitmap을 설정합니다.
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;

            // 경로에 있는 이미지의 Bitmap을 읽어 들입니다.
            // Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath, options);
            selectedImage.compress(Bitmap.CompressFormat.JPEG, 20, stream);
            byte[] byteArray = stream.toByteArray();

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            // 이미지를 서버에 전송합니다.
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("label", tv_label.getText().toString())
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