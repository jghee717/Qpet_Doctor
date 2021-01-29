package com.ccit19.merdog_doctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.ccit19.merdog_doctor.chat.ChatRoomActivity;
import com.ccit19.merdog_doctor.variable.MyGlobals;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class records_detail_Activity  extends AppCompatActivity {
    private String chat_id, chat_room, chat_state, chat_title, chat_content, pet_name, pet_main_type, pet_sub_type, pet_age, pet_gender, pet_birth, pet_img, pet_notice;
    private Button button;
    private ImageView dog_img;
    private TextView records_pet_name, records_pet_main_type, records_pet_sub_type,
            records_pet_age, records_pet_birth, records_pet_gender, records_pet_notice, records_chat_content;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("상세내역");
        actionBar.setDisplayHomeAsUpEnabled(false);        setContentView(R.layout.activity_records_detail);
        button = (Button)findViewById(R.id.chatroom_button);
        dog_img = (ImageView)findViewById(R.id.dog_img);
        records_pet_name = (TextView) findViewById(R.id.records_pet_name);
        records_pet_main_type = (TextView) findViewById(R.id.records_pet_main_type);
        records_pet_sub_type = (TextView) findViewById(R.id.records_pet_sub_type);
        records_pet_age = (TextView) findViewById(R.id.records_pet_age);
        records_pet_birth = (TextView) findViewById(R.id.records_pet_birth);
        records_pet_gender = (TextView) findViewById(R.id.records_pet_gender);
        records_pet_notice = (TextView) findViewById(R.id.records_pet_notice);
        records_chat_content = (TextView) findViewById(R.id.records_chat_content);

        String u = MyGlobals.getInstance().getData();
        String url = u + "/doctorapp/medical_records_detail";

        /* Create request */
        Map<String, String> params = new HashMap<String, String>();
        String s = getIntent().getExtras().getString("chat_id");
        params.put("chat_id", s);

        JsonObjectRequest Form = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        boolean success = false;
                        try {
                            success = response.getBoolean("result");
                            if (success) {
                                chat_id = response.getString("chat_id");
                                chat_room = response.getString("chat_room");
                                chat_state  = response.getString("chat_state");
                                chat_title = response.getString("chat_title");
                                chat_content = response.getString("chat_content");
                                pet_name = response.getString("pet_name");
                                pet_main_type = response.getString("pet_main_type");
                                pet_sub_type = response.getString("pet_sub_type");
                                pet_age = response.getString("pet_age");
                                pet_gender = response.getString("pet_gender");
                                pet_birth = response.getString("pet_birth");
                                pet_img = response.getString("pet_img");
                                pet_notice = response.getString("pet_notice");

                                records_pet_name.setText(pet_name);
                                records_pet_main_type.setText(pet_main_type);
                                records_pet_sub_type.setText(pet_sub_type);
                                records_pet_age.setText(pet_age);
                                records_pet_birth.setText(pet_birth);
                                records_pet_gender.setText(pet_gender);
                                records_pet_notice.setText(pet_notice);
                                records_chat_content.setText(chat_content);
                                Glide.with(getApplicationContext()).load(pet_img) .into(dog_img);
                            } else {
                                Toast.makeText(getApplicationContext(), "false", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "서버로부터 응답이 없습니다.", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(getApplicationContext(), "서버오류입니다.\n잠시후에 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        AppController.getInstance(getApplicationContext()).addToRequestQueue(Form);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                        Intent intent = new Intent(getApplicationContext(), ChatRoomActivity.class);
                        intent.putExtra("chat_room", chat_room);
                        intent.putExtra("chat_request_id", chat_id);
                        intent.putExtra("chat_state", chat_state);
                        startActivity(intent);
                }
            });
    }
}
