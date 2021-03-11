package com.walkerdev.worldsinema;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.walkerdev.worldsinema.main_activity.MainActivity;
import com.walkerdev.worldsinema.user_login.SignUp_Screen;

public class LaunchScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);
        SharedPreferences sp = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(LaunchScreen.this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }, 100);
    }
}