package com.walkerdev.worldsinema;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class SignIn_Screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_screen);
        PostRequest pr = new PostRequest();
       // String response = pr.sendPost(this, 0);
    }
}