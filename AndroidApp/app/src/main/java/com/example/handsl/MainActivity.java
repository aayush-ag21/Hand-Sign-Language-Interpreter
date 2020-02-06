package com.example.handsl;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Debug;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends Activity {

    private static final int camera_activity_id = 810;

    private Button camera_id;
    private ImageView display_id;
    private TextView status_id;
    private TextView result_id;
    private Bitmap photo;
    private boolean imagesSelected = false;
    TextToSpeech anooj;

    private String postUrl = "http://192.168.137.1:80/classify/";
    private int i = 0;
    private int choice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent activity_intent = getIntent();
        choice = activity_intent.getIntExtra(Activity_Intro.EXTRA_CHOICE, 1);

        camera_id = (Button) findViewById(R.id.camera_button);
        display_id = (ImageView) findViewById(R.id.click_image);
        status_id = (TextView) findViewById(R.id.status_text);
        result_id = (TextView) findViewById(R.id.result_text);

        result_id.setText("");

        camera_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera_intent
                        = new Intent(MediaStore
                        .ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera_intent, camera_activity_id);
            }
        });

    }

    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {

        if (requestCode == camera_activity_id) {
            photo = (Bitmap) data.getExtras()
                    .get("data");

            display_id.setImageBitmap(photo);
            imagesSelected = true;
            status_id.setText("Image Taken,Press Upload");
        }
    }
    public void resetResult(View x){
        result_id.setText("");
    }
    public void connectServer(View v) {
        if (imagesSelected == false) {
            status_id.setText("Please Click Image First");
            return;
        }
        status_id.setText("Sending the Files. Please Wait ...");

        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            Bitmap bitmap = photo;
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        } catch (Exception e) {
            status_id.setText(e.toString());
            return;
        }
        byte[] byteArray = stream.toByteArray();

        multipartBodyBuilder.addFormDataPart("image", "Upload" + i + ".jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray));

        RequestBody postBodyImage = multipartBodyBuilder.build();

        postRequest(postUrl + choice, postBodyImage);
    }

    void postRequest(String postUrl, RequestBody postBody) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        status_id.setText("Failed to Connect to Server. Please Try Again.");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                i++;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            status_id.setText("Result Received, Send next Image");
                            result_id.setText(result_id.getText() + response.body().string());
                            anooj=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {
                                    if(status != TextToSpeech.ERROR) {
                                        anooj.setLanguage(Locale.ENGLISH);
                                        anooj.speak(result_id.getText().toString(),TextToSpeech.QUEUE_FLUSH, null);
                                    }
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void onPause(){
        if(anooj !=null){
            anooj.stop();
            anooj.shutdown();
        }
        super.onPause();
    }
}