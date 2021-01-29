package com.ccit19.merdog_doctor;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
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
import com.ccit19.merdog_doctor.chat.ChatRoomActivity;
import com.ccit19.merdog_doctor.chat.ChatRoomAdapter;
import com.ccit19.merdog_doctor.chat.ChatRoomView;
import com.ccit19.merdog_doctor.custom_dialog.CustomAnimationDialog;
import com.ccit19.merdog_doctor.variable.MyGlobals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Records_Activity extends AppCompatActivity {
    private CustomAnimationDialog customAnimationDialog;
    private ListView medical_records_List;
    private TextView editSearch;
    private JSONArray user_name, pet_name, date, chat_id;
    private ArrayList<RecordsViewHolder> Records_list = new ArrayList<>();
    private RecordsAdapter recordsadapter;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("진료기록");
        actionBar.setDisplayHomeAsUpEnabled(false);
        setContentView(R.layout.activity_records);

        medical_records_List = (ListView) findViewById(R.id.medical_records_List);
        editSearch = (TextView) findViewById(R.id.editSearch);
//        customAnimationDialog = new CustomAnimationDialog(getApplicationContext());
//        customAnimationDialog.show();

        String u = MyGlobals.getInstance().getData();
        String url = u + "/doctorapp/medical_records";

        /* Create request */
        Map<String, String> params = new HashMap<String, String>();
        params.put("doctor_id", SaveSharedPreference.getdoctornum(getApplicationContext()));

        JsonObjectRequest Form = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        boolean success = false;
                        try {
                            success = response.getBoolean("result");
                            if (success) {
                                user_name = response.getJSONArray("user_name");
                                pet_name = response.getJSONArray("pet_name");
                                date = response.getJSONArray("date");
                                chat_id = response.getJSONArray("chat_id");
                                int count = 0;
                                while (count < user_name.length()) {
                                    String get_pet_name;
                                    if(pet_name.getString(count).equals("뜗뜗뜗뜗뜗뜗뜗뜗")){
                                        get_pet_name = "삭제된 회원입니다";
                                    } else {
                                        get_pet_name = pet_name.getString(count);
                                    }
                                    String get_user_name = user_name.getString(count);
                                    String get_date = date.getString(count);
                                    String get_chat_id = chat_id.getString(count);

                                    RecordsViewHolder oItem = new RecordsViewHolder(get_user_name, get_pet_name, get_date, get_chat_id);
                                    oItem.user_name = get_user_name;
                                    oItem.pet_name = get_pet_name;
                                    oItem.date = get_date;
                                    oItem.chat_id = get_chat_id;
                                    //리스트 뷰 생성
                                    Records_list.add(oItem);
                                    recordsadapter = new RecordsAdapter(Records_list, getApplicationContext());
                                    medical_records_List.setAdapter(recordsadapter);
                                    count++;
                                }
                                recordsadapter.notifyDataSetChanged();
                                medical_records_List.setSelection(0);
                            } else {
                                Toast.makeText(getApplicationContext(), "false", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        customAnimationDialog.dismiss();
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
//                customAnimationDialog.dismiss();
            }
        });
        AppController.getInstance(getApplicationContext()).addToRequestQueue(Form);

        medical_records_List.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                String s = Records_list.get(position).chat_id;
                Intent intent = new Intent(getApplicationContext(), records_detail_Activity.class);
                intent.putExtra("chat_id", s);
//                intent.putExtra("chat_room", RoomList_ing.get(position).get_chat_room);
//                intent.putExtra("chat_request_id",  RoomList_ing.get(position).get_chat_request_id);
//                intent.putExtra("chat_state",  RoomList_ing.get(position).get_pet_state);
                startActivity(intent);
            }
        });


        // 검색
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editSearch.getText().toString().toLowerCase(Locale.getDefault());
//                recordsadapter.filter(text);
            }
        });
    }

}
