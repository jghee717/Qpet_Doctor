package com.ccit19.merdog_doctor.drawal_account;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class setAccount extends AppCompatActivity {
    private CustomDialog customDialog;
    private Button setaccount_btn, back_btn, account_update;
    String doctor_num;
    String text1;
    private Spinner bank_name;
    private EditText bank_number, deposit_name, birth;
    private CustomAnimationDialog customAnimationDialog;
    String u = MyGlobals.getInstance().getData();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("계좌 설정");
        actionBar.setDisplayHomeAsUpEnabled(false);
        setContentView(R.layout.activity_set_account);
        setaccount_btn = findViewById(R.id.setaccount_btn);
        customAnimationDialog = new CustomAnimationDialog(this);
        back_btn = findViewById(R.id.back_btn);
        bank_number = findViewById(R.id.bank_number);
        deposit_name = findViewById(R.id.deposit_name);
        // birth = findViewById(R.id.birth);
        //확인버튼
        //뱅크네임 설정
        bank_name = (Spinner) findViewById(R.id.bank_name);
        doctor_num = SaveSharedPreference.getdoctornum(getApplicationContext());
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bank_name.setAdapter(adapter);
/*
        //계좌 맞는지 확인
        setaccount_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //금융결제원 테스트베드url
                String url = "https://openapi.open-platform.or.kr/inquiry/real_name";
                Map<String, String> params = new HashMap<String, String>();
                //개설기관[표준코드]
                params.put("bank_code_std", bank_name.getSelectedItem().toString());
                //계좌번호
                params.put("account_num", bank_number.getText().toString());
                //예금주 주민번호 앞부터 7자리
                params.put("account_holder_info", birth.getText().toString());
                //시발 계좌등록신청 시간
                //params.put("tran_dtime")
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
                                        Intent intent = new Intent(getApplicationContext(), setActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "서버연결", Toast.LENGTH_LONG).show();

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
        });
*/

        //계좌 등록버튼
        setaccount_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //커스텀 다이알로그 나타나기
                if(!bank_name.getSelectedItem().toString().equals("은행명") && !bank_number.getText().toString().isEmpty() && !deposit_name.getText().toString().isEmpty()) {
                    customDialog = new CustomDialog(setAccount.this, "계좌를 설정 하시겠습니까?", positiveListener, negativeListener);
                    customDialog.show();
                }else if (bank_name.getSelectedItem().toString().equals("은행명")){
                    Toast.makeText(getApplicationContext(), "은행명을 선택해 주세요.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "빈칸을 채워주세요.", Toast.LENGTH_LONG).show();
                }
            }
        });

        //주민등록번호
      /*  birth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        }); */

    }

    //다이알로그 확인
    private View.OnClickListener positiveListener = new View.OnClickListener() {
        public void onClick(View v) {
            customAnimationDialog.show();
            String url = u + "/doctorapp/account_register";
            Map<String, String> params = new HashMap<String, String>();
            params.put("doctor_id", doctor_num);
            params.put("bank_name", bank_name.getSelectedItem().toString());
            // params.put("doctor_birth",birth.getText().toString());
            params.put("bank_number", bank_number.getText().toString());
            params.put("bank_depo", deposit_name.getText().toString());
            JsonObjectRequest updateForm = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                    url, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            customAnimationDialog.dismiss();
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
                    customAnimationDialog.dismiss();
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

    // 스피너 아이템
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        bank_name.setSelection(0);
        String[] text1 = {"은행명", "신한은행", "농협", "카카오뱅크", "KB국민은행", "우리은행", "KEB하나은행", "SC제일은행", "한국씨티은행"};
    }

    public void onNothingSelected(AdapterView<?> parent) {

    }
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}