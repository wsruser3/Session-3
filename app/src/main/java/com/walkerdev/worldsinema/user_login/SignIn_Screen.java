package com.walkerdev.worldsinema.user_login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.walkerdev.worldsinema.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;


public class SignIn_Screen extends AppCompatActivity {
    static String mail, pass;
    String[] data1 = {"email", "password"};
    String[] data2 = new String[2];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_screen);

        TextView txt1 = findViewById(R.id.signin_button_for_login);
        txt1.setOnClickListener(v -> {
            EditText e = findViewById(R.id.signin_input_email);
            mail = e.getText().toString();
            e = findViewById(R.id.signin_input_pass);
            pass = e.getText().toString();
            if (mail.isEmpty() || mail.length() <= 3 || pass.isEmpty() || pass.length() <= 3) {
                Toast.makeText(SignIn_Screen.this, "Вы неправильно заполнили данные", Toast.LENGTH_SHORT).show();
                return;
            }
            data2 = new String[]{mail, pass};
            goRequest();
        });
        TextView txt2 = findViewById(R.id.signin_button_for_register);
        txt2.setOnClickListener(v -> {
            this.finish();
            Intent i = new Intent(SignIn_Screen.this, SignUp_Screen.class);
            startActivity(i);
        });
        /*
                    long newtoken = 0;
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putLong("token", newtoken);
                    editor.apply();*/
    }

    public void goRequest() {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(data1[0], data2[0])
                .addFormDataPart(data1[1], data2[1])
                .build();
        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("http://cinema.areas.su/auth/login")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer 927151")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("FINDME", "Error: " + e.toString());
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    SignIn_Screen.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject joParent = new JSONObject(myResponse);
                                long id = joParent.getLong("token");
                                SharedPreferences sp = getSharedPreferences("user_info", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putLong("token", id);
                                editor.apply();
                                SignIn_Screen.this.finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }
}

/*
                            if(myResponse.equals("Успешная регистрация")) {
                                SignIn_Screen.this.finish();
                                Intent i = new Intent(SignIn_Screen.this, SignIn_Screen.class);
                                startActivity(i);
                            }*/