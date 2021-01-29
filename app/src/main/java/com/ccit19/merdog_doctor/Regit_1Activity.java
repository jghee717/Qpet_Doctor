package com.ccit19.merdog_doctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ccit19.merdog_doctor.databinding.ActivityRegit1Binding;
import com.ccit19.merdog_doctor.custom_dialog.CustomAnimationDialog;
import com.ccit19.merdog_doctor.variable.MyGlobals;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regit_1Activity extends AppCompatActivity {
    private CustomAnimationDialog customAnimationDialog;
    ActivityRegit1Binding binding;
    private TextView phoneAlt;
    int check = 0;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view;

        binding = DataBindingUtil.setContentView(this, R.layout.activity_regit_1);
        binding.setActivity(this);
        binding.phoneAlt.setVisibility(View.INVISIBLE);
        customAnimationDialog = new CustomAnimationDialog(Regit_1Activity.this);
        //setContentView(R.layout.activity_regit_1);
        binding.phoneAlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent intent = new Intent(getApplicationContext(),findaccount.class);
                intent.putExtra("phone", binding.phonenum.getText().toString());
                startActivity(intent);
            }
        });

        binding.phonenum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String phonePattern = "^01[016789]{1}-?[0-9]{3,4}-?[0-9]{4}$";
                Matcher matcher = Pattern.compile(phonePattern).matcher(binding.phonenum.getText());
                if (!matcher.matches()) {
                    binding.phoneAlt2.setText("형식에 맞지 않는 번호입니다.");
                    binding.phoneAlt2.setTextColor(Color.parseColor("#E53A40"));
                    check = 0;
                } else if (matcher.matches()) {
                    binding.phoneAlt2.setText("확인버튼을 눌러주세요.");
                    binding.phoneAlt2.setTextColor(Color.parseColor("#5CAB7D"));
                    check = 1;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.nextB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startProgress();
                String u = MyGlobals.getInstance().getData();
                String url = u + "/doctorapp/check_phone";
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                if (!binding.phonenum.getText().toString().isEmpty()) {
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        customAnimationDialog.dismiss();
                        boolean success = false;
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        success = jsonObject.getBoolean("result");
                                        if (check == 0) {
                                            binding.phoneAlt2.setText("올바른 전화번호를 입력해주세요.");
                                            binding.phoneAlt2.setTextColor(Color.parseColor("#E53A40"));
                                        } else if (check == 1) {
                                            if (!success) {
                                                binding.phoneAlt2.setText("이미 사용중인 전화번호입니다.");
                                                binding.phoneAlt2.setTextColor(Color.parseColor("#E53A40"));
                                                binding.phoneAlt.setVisibility(View.VISIBLE);
                                                check = 0;
                                            } else {
                                                binding.phoneAlt2.setText("인증되었습니다.");
                                                binding.phoneAlt2.setTextColor(Color.parseColor("#5CAB7D"));
                                                Intent intent = new Intent(getApplicationContext(), Regit_2Activity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.putExtra("phone", binding.phonenum.getText().toString());
                                                startActivity(intent); //로딩이 끝난 후, ChoiceFunction 이동
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        customAnimationDialog.dismiss();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
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
                })
                {
                    @Override
                    public  Map<String, String> getParams() throws AuthFailureError
                    {
                        Map<String, String> params = new HashMap<String, String>();
                         String token = FirebaseInstanceId.getInstance().getToken();
                         params.put("doctor_phone", binding.phonenum.getText().toString());
                        return params;
                    }
                };
                queue.add(request);
                } else {
                   Toast.makeText(getApplicationContext(), "번호를 입력해주세요.", Toast.LENGTH_LONG).show();
              }
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customAnimationDialog.dismiss();
    }

    private void startProgress() {

        customAnimationDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                customAnimationDialog.dismiss();
            }
        }, 1000);

    }
}