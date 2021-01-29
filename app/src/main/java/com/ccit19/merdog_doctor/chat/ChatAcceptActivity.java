package com.ccit19.merdog_doctor.chat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ccit19.merdog_doctor.AppController;
import com.ccit19.merdog_doctor.R;
import com.ccit19.merdog_doctor.SaveSharedPreference;
import com.ccit19.merdog_doctor.custom_dialog.ChatAcceptDialog;
import com.ccit19.merdog_doctor.custom_dialog.CustomAnimationDialog;
import com.ccit19.merdog_doctor.variable.MyGlobals;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ChatAcceptActivity  extends AppCompatActivity {
    private  Handler handler;
    private CustomAnimationDialog customAnimationDialog;
    private ChatAcceptDialog ChatAcceptDialog;
    private String chat_title, chat_content,address, pet_name, pet_age, pet_gender, pet_birth, pet_main_type, pet_sub_type, pet_notice, chat_room;
    private Button cencel, agreed, pet_info_button;
    private TextView userinfo, set_title, set_content, set_pet_name ;
    String u = MyGlobals.getInstance().getData();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chataccept);
        pet_info_button = (Button)findViewById(R.id.pet_info_button);
        cencel = (Button)findViewById(R.id.cencel);
        agreed = (Button)findViewById(R.id.agreed);
        userinfo = (TextView) findViewById(R.id.userinfo);
        set_title = (TextView) findViewById(R.id.set_title);
        set_content = (TextView) findViewById(R.id.set_content);
        set_pet_name = (TextView) findViewById(R.id.set_pet_name);

        customAnimationDialog = new CustomAnimationDialog(ChatAcceptActivity.this);

        String url = u + "/chat/response";
        /* Create request */
        Map<String, String> params = new HashMap<String, String>();
        Bundle bundle=getIntent().getExtras();
        String chat_request_id = bundle.getString("chat_request_id");
        params.put("chat_request_id",chat_request_id);
        JsonObjectRequest sendpetinfo = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        boolean success = false;
                        try {
                            success = response.getBoolean("result");
                            if (success) {
                                chat_title = response.getString("chat_title");
                                chat_content = response.getString("chat_content");
                                pet_name = response.getString("pet_name");
                                address = response.getString("address");
                                pet_age = response.getString("pet_age");
                                pet_gender = response.getString("pet_gender");
                                pet_birth = response.getString("pet_birth");
                                pet_main_type = response.getString("pet_main_type");
                                pet_sub_type = response.getString("pet_sub_type");
                                pet_notice = response.getString("pet_notice");
                                userinfo.setText(chat_title + "\n" + chat_content  + "\n" + address
                                        + "\n" + pet_name + "\n" + pet_age + "\n" + pet_gender
                                        + pet_birth + "\n" + pet_main_type+ pet_sub_type + "\n" + pet_notice);
                                set_title.setText(chat_title);
                                set_content.setText(chat_content);
                                set_pet_name.setText(pet_name);
                            } else {

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
        AppController.getInstance(getApplicationContext()).addToRequestQueue(sendpetinfo);

        // N초후 자동종료
       Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            onBackPressed();
            }
        }, 5000);


        //펫 상세보기
        pet_info_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacksAndMessages(null);
                ChatAcceptDialog = new ChatAcceptDialog(ChatAcceptActivity.this, pet_name, address, pet_age, pet_gender,pet_birth, pet_main_type, pet_sub_type, pet_notice);
                ChatAcceptDialog.show();
            }
        });

        //취소
        cencel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacksAndMessages(null);
                onBackPressed();
            }
        });
            // 수락
        agreed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacksAndMessages(null);
                customAnimationDialog.show();
                String url = u + "/chat/accept";
                /* Create request */
                Map<String, String> params = new HashMap<String, String>();
                Bundle bundle=getIntent().getExtras();
                String chat_request_id = bundle.getString("chat_request_id");
                params.put("chat_request_id",chat_request_id);
                params.put("doctor_id", SaveSharedPreference.getdoctornum(getApplicationContext()));
                JsonObjectRequest sendagreed = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                        url, new JSONObject(params),
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                boolean success = false;
                                try {
                                    success = response.getBoolean("result");
                                    if (success) {
                                        chat_room = response.getString("chat_room");
                                        Intent intent = new Intent(getApplicationContext(), ChatRoomActivity.class);
                                        intent.putExtra("chat_room",chat_room);
                                        intent.putExtra("chat_request_id",response.getString("chat_request_id"));
                                        intent.putExtra("chat_state","on");
                                        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish(); // Activity stack에서 제거
                                    } else {
                                        Toast.makeText(getApplicationContext(), "이미 수락된 상담입니다.", Toast.LENGTH_LONG).show();
                                        onBackPressed();
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
                AppController.getInstance(getApplicationContext()).addToRequestQueue(sendagreed);

            }
        });
    }

}
