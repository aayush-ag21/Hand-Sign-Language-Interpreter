package com.example.handsl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class Activity_Intro extends AppCompatActivity {

    public static final String EXTRA_CHOICE = "EXTRA_CHOICE";
    public int choice;
    private int CAMERA_PERMISSION_CODE = 1;
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__intro);
        ImageButton asl_id = (ImageButton) findViewById(R.id.asl_button);
        ImageButton isl_id = (ImageButton) findViewById(R.id.isl_button);
        if (ContextCompat.checkSelfPermission(Activity_Intro.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            flag = false;
            requestCameraPermission();
        } else {
            flag = true;
        }
        asl_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity(1);
            }
        });

        isl_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity(2);
            }
        });
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(this)
                    .setTitle("permission needed")
                    .setMessage("Need Camera Permission")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(Activity_Intro.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            flag = false;
                        }
                    })
                    .create().show();
            ;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                flag=true;
            }
            else{
                flag = false;
            }
        }
    }

    public void openMainActivity(int i) {
        choice = i;
        if (flag == true) {
            Intent activity_intent = new Intent(this, MainActivity.class);
            activity_intent.putExtra(EXTRA_CHOICE, choice);
            startActivity(activity_intent);
        }
    }
}