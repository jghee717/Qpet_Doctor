package com.ccit19.merdog_doctor.drawal_account;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ccit19.merdog_doctor.AppController;
import com.ccit19.merdog_doctor.MainActivity;
import com.ccit19.merdog_doctor.R;
import com.ccit19.merdog_doctor.SaveSharedPreference;
import com.ccit19.merdog_doctor.custom_dialog.CustomAnimationDialog;
import com.ccit19.merdog_doctor.custom_dialog.CustomDialog;
import com.ccit19.merdog_doctor.ui.notifications.NotificationsFragment;
import com.ccit19.merdog_doctor.variable.MyGlobals;

import org.json.JSONException;
import org.json.JSONObject;

public class Account_update extends AppCompatActivity {
    private CustomDialog customDialog;
    private CustomAnimationDialog customAnimationDialog;
    private Button setaccount_btn, back_btn, accountupdate;
    String doctor_num;
    String text1;
    private Spinner bank_name;
    private EditText bank_number, deposit_name;
    private TextView bank_name_load, accountmenual,setac_btn;
    String u = MyGlobals.getInstance().getData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("계좌 수정");
        actionBar.setDisplayHomeAsUpEnabled(false);
        setContentView(R.layout.activity_account_update);
        setaccount_btn = findViewById(R.id.setaccount_btn);
        back_btn = findViewById(R.id.back_btn);
        bank_number = findViewById(R.id.bank_number);
        bank_name_load = findViewById(R.id.bank_name_load);
        setac_btn = findViewById(R.id.setac_btn);
        deposit_name = findViewById(R.id.deposit_name);
        //확인버튼
        //뱅크네임 설정
        bank_name = (Spinner) findViewById(R.id.bank_name);
        doctor_num = SaveSharedPreference.getdoctornum(getApplicationContext());
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bank_name.setAdapter(adapter);
        customAnimationDialog = new CustomAnimationDialog(Account_update.this);
        String url = u + "/doctorapp/account_load";
        /* Create request */
        Map<String, String> params = new HashMap<String, String>();
        params.put("doctor_id", doctor_num);
        JsonObjectRequest inForm = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        boolean success = false;
                        try {
                            success = response.getBoolean("result");
                            if (success == true) {
                                bank_number.setText(response.getString("bank_number"));
                                deposit_name.setText(response.getString("bank_depo"));
                                bank_name_load.setText(response.getString("bank_name"));
                            } else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance(getApplicationContext()).addToRequestQueue(inForm);

        setac_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //커스텀 다이알로그 나타나기
                if(!bank_name.getSelectedItem().toString().equals("은행명") && !bank_number.getText().toString().isEmpty() && !deposit_name.getText().toString().isEmpty()) {
                    customDialog = new CustomDialog(Account_update.this,"계좌를 수정 하시겠습니까?",positiveListener,negativeListener);
                    customDialog.show();
                }else if (bank_name.getSelectedItem().toString().equals("은행명")){
                    Toast.makeText(getApplicationContext(), "은행명을 선택해 주세요.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "빈칸을 채워주세요.", Toast.LENGTH_LONG).show();
                }
            }
        });


        bank_name_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bank_name_load.setVisibility(View.INVISIBLE);
            }
        });

       /* bank_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    bank_name_load.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/
    }

    //다이알로그 확인
    private View.OnClickListener positiveListener = new View.OnClickListener() {
        public void onClick(View v) {
            customAnimationDialog.show();
            String url = u + "/doctorapp/account_update";

            Map<String, String> params = new HashMap<String, String>();
            params.put("doctor_id", doctor_num);
            params.put("bank_name", bank_name.getSelectedItem().toString());
            ;
            params.put("bank_number", bank_number.getText().toString());
            params.put("bank_depo", deposit_name.getText().toString());
            JsonObjectRequest updateForm = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                    url, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            boolean success = false;
                            try {
                                success = response.getBoolean("result");
                                if (success == true) {
                                    Toast.makeText(getApplicationContext(), "설정 완료", Toast.LENGTH_LONG).show();
                                    onBackPressed();
                                } else {
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            AppController.getInstance(getApplicationContext()).addToRequestQueue(updateForm);
        }
    };

    //다이알로그 취소
    private View.OnClickListener negativeListener = new View.OnClickListener() {
        public void onClick(View v) {
            customDialog.dismiss();
        }
    };

    //스피너 아이템 설정
    public void setonItemSelected(AdapterView<?> parent, View view,
                                  int pos, long id) {

    }

    public void onNothingSelected(AdapterView<?> parent) {

    }
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}