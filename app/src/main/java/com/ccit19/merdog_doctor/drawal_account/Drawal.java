package com.ccit19.merdog_doctor.drawal_account;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.ccit19.merdog_doctor.MainActivity;
import com.ccit19.merdog_doctor.R;
import com.ccit19.merdog_doctor.SaveSharedPreference;
import com.ccit19.merdog_doctor.custom_dialog.CustomAnimationDialog;
import com.ccit19.merdog_doctor.custom_dialog.CustomDialog;
import com.ccit19.merdog_doctor.hospital_modify;
import com.ccit19.merdog_doctor.ui.notifications.NotificationsFragment;
import com.ccit19.merdog_doctor.variable.MyGlobals;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Drawal extends AppCompatActivity {
    private CustomDialog customDialog;
    private CustomAnimationDialog customAnimationDialog;
    private Button accountset_btn, back_btn, drawal_btn, account_regit;
    String doctor_num;
    double valuee;
    double drawal_milesInt;
    double total_milesInt;
    double feeInt;
    private TextView total_miles, fee, bank_name, bank_number, deposit_name,name,count,rating;
    private EditText drawal_miles;
    private String adviceNum, doctor_Rating;
    String u = MyGlobals.getInstance().getData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("마일리지 출금");
        actionBar.setDisplayHomeAsUpEnabled(false);
        setContentView(R.layout.activity_drawal);
        accountset_btn = findViewById(R.id.accountset_btn);
        drawal_btn = findViewById(R.id.drawal_btn);
        account_regit = findViewById(R.id.account_regit);
        back_btn = findViewById(R.id.back_btn);
        name = findViewById(R.id.name);
        count = findViewById(R.id.count);
        rating = findViewById(R.id.rating);
        total_miles = findViewById(R.id.total_miles);
        drawal_miles = findViewById(R.id.drawal_miles);
        fee = findViewById(R.id.fee);
        bank_name = findViewById(R.id.bank_name);
        bank_number = findViewById(R.id.bank_number);
        deposit_name = findViewById(R.id.deposit_name);
        drawal_miles.requestFocus();
        doctor_num = SaveSharedPreference.getdoctornum(getApplicationContext());
        customAnimationDialog = new CustomAnimationDialog(Drawal.this);
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
                                account_regit.setVisibility(View.GONE);
                                accountset_btn.setVisibility(View.VISIBLE);
                                bank_name.setText(response.getString("bank_name"));
                                bank_number.setText(response.getString("bank_number"));
                                deposit_name.setText(response.getString("bank_depo"));
                                total_miles.setText(response.getString("point"));
                                total_milesInt = Integer.parseInt(response.getString("point"));
                                name.setText(response.getString("doctor_name"));
                                //rating.setText(response.getString("rating"));
                                //count.setText(response.getString("chat_count"));

                            } else {
                                account_regit.setVisibility(View.VISIBLE);
                                accountset_btn.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "등록된 계좌가 없습니다. 계좌를 등록해주세요.", Toast.LENGTH_LONG).show();
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

        //수수료
        drawal_miles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAnimationDialog.show();
                String url = u + "/doctorapp/account_load";
                /* Create request */
                Map<String, String> params = new HashMap<String, String>();
                params.put("doctor_id", doctor_num);
                JsonObjectRequest inForm = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                        url, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                customAnimationDialog.dismiss();
                                boolean success = false;
                                try {
                                    success = response.getBoolean("result");
                                    if (success == true) {
                                        rating.setText(response.getString("rating"));
                                        count.setText(response.getString("chat_count"));
                                        double ratingt = Double.parseDouble(response.getString("rating"));
                                        double countt = Double.parseDouble(response.getString("chat_count"));
                                        if(ratingt>=4 && countt>=100){
                                            feeInt=0.2;
                                        }else{
                                            feeInt=0.3;
                                        }
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
                AppController.getInstance(getApplicationContext()).addToRequestQueue(inForm);
            }
        });
//수수료 계싼
        drawal_miles.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (drawal_miles.getText().toString().equals("")) {
                    drawal_miles.setText("0");
                } else {
                    drawal_milesInt = Integer.parseInt(drawal_miles.getText().toString());
                    valuee = drawal_milesInt * feeInt;
                    long valueelong;
                    valueelong = (long) valuee;
                    if(fee.equals("") || fee.equals("null") || fee.equals("0") || fee.equals(null)){
                        fee.setText("0");
                    }else {
                        fee.setText("" + valueelong);
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        //계좌설정버튼
        accountset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProgress();
                Intent intent = new Intent(getApplicationContext(), Account_update.class);
                startActivity(intent);
            }
        });
        account_regit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProgress();
                Intent intent = new Intent(getApplicationContext(), setAccount.class);
                startActivity(intent);
            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        //출금버튼
        drawal_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawal_miles.getText().toString().equals("")) {
                    drawal_miles.setText("0");
                } else {
                    drawal_milesInt = Integer.parseInt(drawal_miles.getText().toString());
                }

                if (drawal_milesInt > total_milesInt) {
                    Toast.makeText(getApplicationContext(), "출금 마일리지가 총 마일리지보다 초과되었습니다", Toast.LENGTH_LONG).show();
                } else if (drawal_milesInt < 10000) {
                    Toast.makeText(getApplicationContext(), "마일리지는 1만원 이상부터 가능합니다.", Toast.LENGTH_LONG).show();
                } else {
                    //커스텀 다이알로그 나타나기
                    customDialog = new CustomDialog(Drawal.this,"출금 하시겠습니까?",positiveListener,negativeListener);
                    customDialog.show();
                }
            }
        });
    }

    //다이알로그 확인
    private View.OnClickListener positiveListener = new View.OnClickListener() {
        public void onClick(View v) {
            customAnimationDialog.show();
            String url = u + "/doctorapp/withdraw_register";
            Map<String, String> params = new HashMap<String, String>();
            params.put("doctor_id", doctor_num);
            params.put("price", drawal_miles.getText().toString());
            JsonObjectRequest updateForm = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                    url, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            customAnimationDialog.dismiss();
                            boolean success = false;
                            try {
                                success = response.getBoolean("result");
                                if (success) {
                                    Toast.makeText(getApplicationContext(), "출금신청이 완료되었습니다.", Toast.LENGTH_LONG).show();
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
            AppController.getInstance(getApplicationContext()).addToRequestQueue(updateForm);
        }
    };

    //다이알로그 취소
    private View.OnClickListener negativeListener = new View.OnClickListener() {
        public void onClick(View v) {
            customDialog.dismiss();
        }
    };

    private void startProgress() {

        customAnimationDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                customAnimationDialog.dismiss();
            }
        }, 1000);

    }
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}