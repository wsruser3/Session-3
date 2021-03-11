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

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;


public class SignUp_Screen extends AppCompatActivity {
    final int permissioncode = 7777;
    String[] data1 = {"email", "password", "firstName", "lastName"};
    String[] data2 = new String[5];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        SharedPreferences sp = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        long token = sp.getLong("token", 0);
        if (token != 0) {
            this.finish();
            return;
        }
        if(sp.getInt("first_load", 0) == 0) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putLong("first_load", 1);
            editor.apply();
        }
        else {
            startActivity(new Intent(SignUp_Screen.this, SignIn_Screen.class));
            this.finish();
            return;
        }

        TextView btn1 = findViewById(R.id.signup_button_for_register);
        btn1.setOnClickListener(v -> {
            Integer[] id = {R.id.signup_input_email, R.id.signup_input_firstname, R.id.signup_input_lastname, R.id.signup_input_password_one, R.id.signup_input_password_two};
            EditText[] e = new EditText[5];
            for (int i = 0; i <= 4; i++) {
                String s;
                e[i] = findViewById(id[i]);
                s = e[i].getText().toString();
                if (s.isEmpty() || s.length() <= 3) {
                    String temp = e[i].getHint().toString();
                    Toast.makeText(SignUp_Screen.this, "Вы неправильно заполнили поле " + temp, Toast.LENGTH_SHORT).show();
                    return;
                }
                data2[i] = s;
            }
            if(!data2[3].equals(data2[4])) {
                Toast.makeText(SignUp_Screen.this, "Неправильно указан повтор пароля", Toast.LENGTH_SHORT).show();
                return;
            }
            goRequest(data1, data2);
        });
        
        TextView btn2 = findViewById(R.id.signup_button_for_have);
        btn2.setOnClickListener(v -> {
            this.finish();
            Intent i = new Intent(SignUp_Screen.this, SignIn_Screen.class);
            startActivity(i);
        });
    }
    public void goRequest(String[] data1, String[] data2)  {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(data1[0], data2[0])
                .addFormDataPart(data1[1], data2[1])
                .addFormDataPart(data1[2], data2[2])
                .addFormDataPart(data1[3], data2[3])
                .build();
        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("http://cinema.areas.su/auth/register")
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

                    SignUp_Screen.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SignUp_Screen.this, "Response: " + myResponse, Toast.LENGTH_SHORT).show();
                            if(myResponse.equals("Успешная регистрация")) {
                                SignUp_Screen.this.finish();
                                Intent i = new Intent(SignUp_Screen.this, SignIn_Screen.class);
                                startActivity(i);
                            }
                        }
                    });
                }
            }
        });
    }
}