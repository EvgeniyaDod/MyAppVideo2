package com.example.myappvideo2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText username;
    private EditText password;
    private EditText ipCam;
    private Button bnLogin;
    private CheckBox ckPTZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (EditText) findViewById(R.id.user);
        password = (EditText) findViewById(R.id.pass);
        ipCam = (EditText) findViewById(R.id.ip);

        bnLogin = (Button) findViewById(R.id.bnLog);
        ckPTZ = (CheckBox) findViewById(R.id.checkPTZ);

        bnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, navigation.class);
        intent.putExtra("username", username.getText().toString());  //сохраняем переменные
        intent.putExtra("password", password.getText().toString());
        intent.putExtra("ipCam", ipCam.getText().toString());
        intent.putExtra("ckPTZ", ckPTZ.isChecked());
        startActivity(intent);
        //finish();
    }
}

