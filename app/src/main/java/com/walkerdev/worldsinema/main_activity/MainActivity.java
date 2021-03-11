package com.walkerdev.worldsinema.main_activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;
import com.walkerdev.worldsinema.R;
import com.walkerdev.worldsinema.common.menuFragment;
import com.walkerdev.worldsinema.movie.MovieScreen;
import com.walkerdev.worldsinema.user_login.SignUp_Screen;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity {
    private int menu = 0;
    private static long token = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sp = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        token = sp.getLong("token", 0);
        if (token == 0) {
            startActivity(new Intent(MainActivity.this, SignUp_Screen.class));
        }
        while (token == 0) {
            token = sp.getLong("token", 0);
        }
        TextView img = findViewById(R.id.menu_for_you);
        img.setOnClickListener(v -> {
            menu = 2;
            updateMenu();
        });
        img = findViewById(R.id.menu_new);
        img.setOnClickListener(v -> {
            menu = 1;
            updateMenu();
        });
        img = findViewById(R.id.menu_rating);
        img.setOnClickListener(v -> {
            menu = 0;
            updateMenu();
        });
        loadCover();
        loadFirst();
        TextView txt = findViewById(R.id.open_cover_film);
        txt.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MovieScreen.class)));
        setMovie();
        loadLastVideo();
        BottomNavigationView navigation = findViewById(R.id.navigation);
        //navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
    /*private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_default:
                    loadFragment(menuFragment.newInstance(0));
                    return true;
                case R.id.nav_for_me:
                    loadFragment(menuFragment.newInstance(1));
                    return true;
                case R.id.nav_collection:
                    loadFragment(menuFragment.newInstance(2));
                    return true;
                case R.id.nav_profile:
                    loadFragment(menuFragment.newInstance(3));
                    return true;
            }
            return false;
        }
    };
    private void loadFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fl_content, fragment);
        ft.commit();
    }*/
    private void loadLastVideo() {

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_token", String.valueOf(token))
                .build();
        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("http://cinema.areas.su/user/history")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("FINDME", "Error: " + e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray ja = new JSONArray(myResponse);
                                JSONObject j = ja.getJSONObject(0);
                                int episode = j.getInt("episodeId");
                                int movie = j.getInt("movieId");
                                loadLastVideo_secondMethod(episode, movie);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("FINDMENOW", "Error: " + e.toString());
                            }

                        }
                    });
                }
            }
        });
    }
    private void loadLastVideo_secondMethod(int episode, int movie) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://cinema.areas.su/movies/" + movie +"/episodes",
                response -> {
                    int index = -1;
                    try {
                        JSONArray ja = new JSONArray(response);
                        for(int i = 0; i < ja.length(); i++) {
                            JSONObject j = ja.getJSONObject(i);
                            if(j.getInt("episodeId") != episode) continue;
                            index = i;
                            break;
                        }
                        if(index == -1) index = 0;
                        JSONObject j = ja.getJSONObject(index);
                        JSONArray jc = j.getJSONArray("images");
                        ImageView img = findViewById(R.id.image_for_main_last_video);
                        Picasso.with(this)
                                .load("http://cinema.areas.su/up/images/" + jc.getString(0))
                                .placeholder(R.drawable.simple_icon)
                                .error(R.drawable.simple_icon)
                                .into(img);
                        img.setOnClickListener(v -> {
                            SharedPreferences sp = getSharedPreferences("user_info", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putLong("cover_id", movie);
                            editor.apply();
                            startActivity(new Intent(MainActivity.this, MovieScreen.class));
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("FINDME", "Error: " + e.toString());
                    }

                }, error -> Toast.makeText(this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show());
        queue.add(stringRequest);
    }
    private void updateMenu() {
        switch(menu) {
            case 0: {
                setMovie();
                ImageView img = findViewById(R.id.image_to_for_you);
                img.setVisibility(ImageView.INVISIBLE);
                img = findViewById(R.id.image_to_new);
                img.setVisibility(ImageView.INVISIBLE);
                img = findViewById(R.id.image_to_rating);
                img.setVisibility(ImageView.VISIBLE);
                break;
            }
            case 1: {
                setMovie();
                ImageView img = findViewById(R.id.image_to_for_you);
                img.setVisibility(ImageView.INVISIBLE);
                img = findViewById(R.id.image_to_new);
                img.setVisibility(ImageView.VISIBLE);
                img = findViewById(R.id.image_to_rating);
                img.setVisibility(ImageView.INVISIBLE);
                break;
            }
            case 2: {
                setMovie();
                ImageView img = findViewById(R.id.image_to_for_you);
                img.setVisibility(ImageView.VISIBLE);
                img = findViewById(R.id.image_to_new);
                img.setVisibility(ImageView.INVISIBLE);
                img = findViewById(R.id.image_to_rating);
                img.setVisibility(ImageView.INVISIBLE);
                break;
            }
        }
    }
    private void setMovie() {
        Integer[] imgs = {R.id.menu_photo_1, R.id.menu_photo_2, R.id.menu_photo_3};
        SharedPreferences sp = getSharedPreferences("user_info", Context.MODE_PRIVATE);

        String filter = null;
        switch(menu) {
            case 0: { filter = "inTrend"; break; }
            case 1: { filter = "new"; break; }
            case 2: { filter = "forMe"; break; }
        }
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://cinema.areas.su/movies?filter=" + filter,
                response -> {
                    try {
                        JSONArray js = new JSONArray(response);
                        for(int i = 0; i < imgs.length; i++) {
                            JSONObject j = js.getJSONObject(i);
                            ImageView img = findViewById(imgs[i]);
                            Picasso.with(this)
                                    .load("http://cinema.areas.su/up/images/" + j.getString("poster"))
                                    .placeholder(R.drawable.simple_icon)
                                    .error(R.drawable.simple_icon)
                                    .into(img);
                            img.setOnClickListener(v -> {
                                SharedPreferences.Editor editor = sp.edit();
                                try {
                                    editor.putLong("cover_id", j.getInt("movieId"));
                                    editor.apply();
                                    startActivity(new Intent(MainActivity.this, MovieScreen.class));
                                } catch (JSONException e) {
                                    Log.e("FINDME", e.toString());
                                }
                            });
                        }
                    }
                    catch (JSONException e) {
                        Log.e("FINDME", e.toString());
                    }
                }, error -> Toast.makeText(this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show());
        queue.add(stringRequest);
    }
    private void loadCover() {
        SharedPreferences sp = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://cinema.areas.su/movies/cover",
                response -> {
                    try {
                        ImageView img = findViewById(R.id.img_cover_film);

                        JSONObject joParent = new JSONObject(response);

                        long id = joParent.getLong("movieId");
                        String image = joParent.getString("backgroundImage");

                        Picasso.with(this)
                                .load("http://cinema.areas.su/up/images/" + image)
                                .placeholder(R.drawable.simple_icon)
                                .error(R.drawable.simple_icon)
                                .into(img);

                        SharedPreferences.Editor editor = sp.edit();
                        editor.putLong("cover_id", id);
                        editor.apply();

                    } catch (JSONException e) {
                        Toast.makeText(this, "Error" + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }, error -> Toast.makeText(this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show());
        queue.add(stringRequest);

        ImageView img = findViewById(R.id.image_to_for_you);
        img.setVisibility(ImageView.INVISIBLE);
        img = findViewById(R.id.image_to_new);
        img.setVisibility(ImageView.INVISIBLE);
    }
    public void loadFirst() {
    }
}
