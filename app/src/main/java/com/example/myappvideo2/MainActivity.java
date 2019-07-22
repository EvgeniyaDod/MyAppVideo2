package com.example.myappvideo2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText username;
    private EditText password;
    private EditText ipCam;
    private Button bnLogin;
    private CheckBox ckPTZ;
    private String settings="MyCameraSettings";
    private String set1="IP";
    private String set2="Login";
    private String set3="Password";
    private String set4="PTZ";
    SharedPreferences mSettings;

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
        mSettings=getSharedPreferences(settings, Context.MODE_PRIVATE);
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

    @Override
    protected void onResume() {
        super.onResume();
        ipCam.setText(mSettings.getString(set1, ""));
        username.setText(mSettings.getString(set2, ""));
        password.setText(mSettings.getString(set3, ""));
        ckPTZ.setChecked(mSettings.getBoolean(set4, false));
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor e =mSettings.edit();
        e.putString(set1, ipCam.getText().toString());
        e.putString(set2, username.getText().toString());
        e.putString(set3, password.getText().toString());
        e.putBoolean(set4, ckPTZ.isChecked());
        e.apply();
    }
}

