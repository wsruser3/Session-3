package com.walkerdev.worldsinema;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class SignUp_Screen extends AppCompatActivity {
    SharedPreferences sp;
    final int permissioncode = 7777;
    String[] data1 = {"email", "password", "firstName", "lastName"};
    String[] data2 = new String[5];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        sp = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        long token = sp.getLong("token", 0);
        if (token != 0) {
            this.finish();
            return;
        }

        Button btn1 = findViewById(R.id.signup_button_for_register);
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
                if (i != 4) data2[i] = s;
            }
            Toast.makeText(this, Arrays.toString(data1), Toast.LENGTH_SHORT).show();
            startRequest();
            PostRequest pr = new PostRequest();
        });
//testparol123
        /*
        * {
  "email": "vasya@mail.com",
  "password": "qwerty",
  "firstName": "John",
  "lastName": "Johnson"
}
        *
        * */
        /*
                    long newtoken = 0;
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putLong("token", newtoken);
                    editor.apply();*/
    }

    public void startRequest() {
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            goRequest(data1, data2);
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.INTERNET},
                    permissioncode);
        }
    }
    public void goRequest(String[] data1, String[] data2) {
        String s = sendPost(this, 0, data1, data2, "https://cinema.areas.su/auth/register");
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == permissioncode) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                goRequest(data1, data2);
            } else {
                Toast.makeText(this, "К сожалению, мы не сможем зарегистрировать Вас без интернета!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public String sendPost(Context context, long token, String[] name_body, String[] value_body, String URL) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> Toast.makeText(context, "Response: " + response, Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(context, "Error: " + error.toString(), Toast.LENGTH_SHORT).show()) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                for (int i = 0; i < name_body.length; i++) {
                    params.put(name_body[i], value_body[i]);
                }
                return params;
            }
            public Map<String, String> getHeaders() {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "bearerAuth " + "927151");
                return headers;
            }
        };
        stringRequest.setTag(500);
        requestQueue.add(stringRequest);
        return "stopRequest";
    }
}