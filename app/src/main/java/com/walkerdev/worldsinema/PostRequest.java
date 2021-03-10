package com.walkerdev.worldsinema;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class PostRequest {
    public String sendPost(Context context, long token, String[] name_body, String[] value_body, String URL) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        final String[] to_exit = new String[1];
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> to_exit[0] = response, error -> to_exit[0] = "error: " + error.toString()) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                for (int i = 0; i < name_body.length; i++) {
                    params.put(name_body[i], value_body[i]);
                }
                return params;
            }
        };
        stringRequest.setTag(500);
        requestQueue.add(stringRequest);
        return to_exit[0];
    }
    public String sendGet(Context context, long token, String URL) {
        final String[] to_exit = new String[1];
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {
                }, error -> to_exit[0] = "Произошла ошибка при запросе: " + error.toString() + "\n\nК сожалению, наши разработчики о ней уже в курсе, и делают вид, что пытаются её решить.");
        queue.add(stringRequest);
        return to_exit[0];
    }
}