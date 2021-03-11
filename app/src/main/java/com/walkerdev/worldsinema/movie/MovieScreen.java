package com.walkerdev.worldsinema.movie;

import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.squareup.picasso.Picasso;
import com.walkerdev.worldsinema.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class MovieScreen extends AppCompatActivity {
    long id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_screen);
        SharedPreferences sp = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        id = sp.getLong("cover_id", 0);
        if(id == 0) {
            this.finish();
            return;
        }
        ImageView back = findViewById(R.id.image_for_one_film_back);
        back.setOnClickListener(v -> this.finish());
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://cinema.areas.su/movies/" + id,
                response -> {
                    try {
                        JSONObject joParent = new JSONObject(response);
                        long id = joParent.getLong("movieId");
                        String name = joParent.getString("name");
                        String description = joParent.getString("description");
                        int age = joParent.getInt("age");
                        String images = joParent.getString("images");
                        String poster = joParent.getString("poster");
                        String tags = joParent.getString("tags");

                        TextView txt = findViewById(R.id.text_for_one_film_name);
                        txt.setText(name);
                        txt = findViewById(R.id.text_for_one_film_description);
                        txt.setText(description);
                        ImageView img = findViewById(R.id.image_for_one_film_photo);
                        Picasso.with(this)
                                .load("http://cinema.areas.su/up/images/" + poster)
                                .placeholder(R.drawable.simple_icon)
                                .error(R.drawable.simple_icon)
                                .into(img);
                        img = findViewById(R.id.image_for_one_film_limit);
                        int limit = R.drawable.limit_four;
                        switch(age) {
                            case 6: limit = R.drawable.limit_one;
                            case 12: limit = R.drawable.limit_two;
                            case 16: limit = R.drawable.limit_three;
                            case 18: limit = R.drawable.limit_four;
                        }
                        Picasso.with(this)
                                .load(limit)
                                .placeholder(R.drawable.simple_icon)
                                .error(R.drawable.simple_icon)
                                .into(img);
                        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, "http://cinema.areas.su/movies/" + id + "/episodes",
                                responses -> {
                                    try {
                                        JSONArray joParentz = new JSONArray(responses);
                                        JSONObject j = joParentz.getJSONObject(0);
                                        String preview = j.getString("preview");
                                        VideoView videoView = findViewById(R.id.video_for_one_film);
                                        videoView.setVideoPath("http://cinema.areas.su/up/video/" + preview);
                                        Log.e("FINDME", "video is loaded");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Log.e("FINDME", e.toString());
                                    }
                                }, error -> {
                                    Toast.makeText(this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                                });
                                queue.add(stringRequest2);

                        ChipGroup this_chip = findViewById(R.id.chip);
                        JSONArray arr = joParent.getJSONArray("tags");
                        for(int i = 0; i < arr.length(); i++) {
                            JSONObject j = arr.getJSONObject(i);
                            TextView c = new TextView(this);
                            c.setText(j.getString("tagName"));
                            c.setBackgroundResource(R.drawable.shape_to_view);
                            c.setGravity(Gravity.CENTER);
                            c.setPadding(20, 10, 20, 10);
                            c.setTextColor(Color.WHITE);
                            c.setTextSize(14);
                            c.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                            c.setOnClickListener(v -> {
                                try {
                                    Toast.makeText(this, "Скоро будет готово!\n\nInfo: [" + j.getInt("idTags") + "," + j.getString("tagName") + "]", Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                            this_chip.addView(c);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("FINDME", "Error: " + e.toString());
                    }

                }, error -> {
            Toast.makeText(this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
        });
        queue.add(stringRequest);
    }
}