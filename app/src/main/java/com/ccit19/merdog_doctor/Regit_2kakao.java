package com.ccit19.merdog_doctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ccit19.merdog_doctor.databinding.ActivityRegit2kakaoBinding;
import com.ccit19.merdog_doctor.custom_dialog.CustomAnimationDialog;
import com.ccit19.merdog_doctor.variable.MyGlobals;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ccit19.merdog_doctor.Regit_2Activity.numberGen;

public class Regit_2kakao extends AppCompatActivity {
    private CustomAnimationDialog customAnimationDialog;
    ActivityRegit2kakaoBinding binding;
    static final String TAG = Regit_2kakao.class.getSimpleName();
    String phone_number;
    String cert_number,doctor_num,kakaotag;
    private static final int MILLISINFUTURE = 180 * 1000;
    private static final int COUNT_DOWN_INTERVAL = 1000;
    int check = 0;
    int check2 = 0;
    int check3 = 0;
    String u = MyGlobals.getInstance().getData();
    private int count = 180;
    private CountDownTimer countDownTimer;
    Boolean Kakaolog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doctor_num = SaveSharedPreference.getdoctornum(getApplicationContext());
        //카카오로그인시 사라지게
        binding = DataBindingUtil.setContentView(this, R.layout.activity_regit_2kakao);
        phone_number = getIntent().getStringExtra("phone");
        //setContentView(R.layout.activity_regit_2kakao);
        binding.certSend.setVisibility(View.INVISIBLE);
        customAnimationDialog = new CustomAnimationDialog(Regit_2kakao.this);
        binding.doctorphone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String phonePattern = "^01[016789]{1}-?[0-9]{3,4}-?[0-9]{4}$";
                Matcher matcher = Pattern.compile(phonePattern).matcher(binding.doctorphone.getText());

                if (!matcher.matches()) {
                    binding.phonecheck.setText("형식에 맞지 않는 번호입니다.");
                    binding.phonecheck.setTextColor(Color.parseColor("#E53A40"));
                    check = 0;
                } else if (matcher.matches()) {
                    binding.phonecheck.setText("중복확인 버튼을 눌러주세요.");
                    binding.phonecheck.setTextColor(Color.parseColor("#5CAB7D"));
                    check = 1;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        binding.sDoctorname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String NicPattern = "^[가-힣]*$";
                Matcher matcher = Pattern.compile(NicPattern).matcher(binding.sDoctorname.getText());
                if (!matcher.matches()) {
                    binding.doctornameAlt.setText("이름이 올바르지 않습니다.");
                    binding.doctornameAlt.setTextColor(Color.parseColor("#E53A40"));
                } else if (binding.sDoctorname.getText().toString().contains(" ")) {
                    binding.doctornameAlt.setText("공백은 입력이 불가합니다.");
                } else {
                    binding.doctornameAlt.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        binding.certSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = u + "/ajax/sms";
                /* Create request */
                Map<String, String> params = new HashMap<String, String>();
                params.put("phone", binding.doctorphone.getText().toString());
                cert_number = numberGen(6, 1);
                params.put("number", cert_number);
                JsonObjectRequest regitForm = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                        url, new JSONObject(params),
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                String pnPattern = "^01[016789]{1}-?[0-9]{3,4}-?[0-9]{4}$";

                                boolean success = false;
                                try {
                                    success = response.getBoolean("result");
                                    if (success) {
                                        Toast.makeText(getApplicationContext(), "인증번호가 발송되었습니다.", Toast.LENGTH_LONG).show();
                                        countDownTimer();
                                        countDownTimer.start();
                                        binding.certSend.setEnabled(false);
                                        binding.certnumCheck.setEnabled(true);
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
                AppController.getInstance(getApplicationContext()).addToRequestQueue(regitForm);
                binding.certNum.requestFocus();
            }
        });

        binding.certnumCheck.setOnClickListener(new View.OnClickListener() {// 인증번호 체크버튼 이벤트
            @Override
            public void onClick(View v) {
                if (cert_number.equals(binding.certNum.getText().toString())) {
                    binding.certnumAlt.setText("인증되었습니다.");
                    Toast.makeText(getApplicationContext(), "인증되었습니다.", Toast.LENGTH_LONG).show();
                    binding.certnumCheck.setEnabled(false);
                    binding.certSend.setText("인증완료");
                    binding.certNum.setEnabled(false);
                    binding.certnumCheck.setEnabled(false);
                    countDownTimer.cancel();
                    check2 = 1;
                } else {
                    binding.certnumAlt.setText("올바른 인증번호를 입력하세요.");
                    check2 = 0;
                }
            }
        });


        //중복확인
        binding.numbercheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!binding.doctorphone.getText().toString().isEmpty()) {
                    String url = u + "/doctorapp/check_phone";
                    /* Create request */
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("doctor_phone", binding.doctorphone.getText().toString());

                    JsonObjectRequest loginForm = new JsonObjectRequest(Request.Method.POST,
                            url, new JSONObject(params),
                            new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    boolean success = false;
                                    try {
                                        success = response.getBoolean("result");
//                                        if (success){
//                                            Toast.makeText(getApplicationContext(),"인증되었습니다.",Toast.LENGTH_LONG).show();
//
//                                        }else {
//                                            Toast.makeText(getApplicationContext(),"이미 있는 번호입니다.\n다른 번호를 입력해주세요.",Toast.LENGTH_LONG).show();
//
//                                        }
                                        if (check == 0) {
                                            binding.phonecheck.setText("올바른 전화번호를 입력해주세요.");
                                            binding.phonecheck.setTextColor(Color.parseColor("#E53A40"));
                                        } else if (check == 1) {
                                            if (!success) {
                                                binding.phonecheck.setText("이미 사용중인 전화번호입니다.");
                                                binding.phonecheck.setTextColor(Color.parseColor("#E53A40"));
                                                check = 0;
                                            } else {
                                                binding.phonecheck.setText("인증되었습니다.");
                                                binding.phonecheck.setTextColor(Color.parseColor("#5CAB7D"));
                                                binding.numbercheck.setVisibility(View.GONE);
                                                binding.certSend.setVisibility(View.VISIBLE);
                                            }
                                        }
                                        //    }
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
                    AppController.getInstance(view.getContext()).addToRequestQueue(loginForm);
                } else {
                    Toast.makeText(getApplicationContext(), "번호를 입력해주세요.", Toast.LENGTH_LONG).show();

                }
            }
        });

        //카카오로 회원가입시 버튼
        binding.finkakaoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                startProgress();
                if ((check ==1)&& (check2 == 1)&& !binding.sDoctorname.getText().toString().isEmpty() &&  binding.AgreedCheck.isChecked()) {
                    startProgress();
                    String doctor_name = binding.sDoctorname.getText().toString();
                    Intent intent = new Intent(Regit_2kakao.this, Regit_3Activity.class);
                    intent.putExtra("doctor_id",getIntent().getStringExtra("doctor_id"));
                    intent.putExtra("doctor_name", doctor_name);
                    intent.putExtra("doctor_phone", binding.doctorphone.getText().toString());
                    intent.putExtra("logintype",getIntent().getStringExtra("logintype"));
                    startActivity(intent);
                }else if (binding.sDoctorname.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "이름을 입력해 주세요.", Toast.LENGTH_LONG).show();
                } else if (!(check == 1)) {
                    Toast.makeText(getApplicationContext(), "휴대폰 번호가 올바르지 않습니다", Toast.LENGTH_LONG).show();
                } else if (!(check2 == 1)) {
                    Toast.makeText(getApplicationContext(), "휴대폰 인증을 해주세요.", Toast.LENGTH_LONG).show();
                } else if (!binding.AgreedCheck.isChecked()){
                    Toast.makeText(getApplicationContext(), "약관 동의를 해주세요.", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "올바르게 입력해주세요", Toast.LENGTH_LONG).show();
                }
            }
        });



    }



    public void countDownTimer() {//휴대폰 번호 카운트다운 타이머
        countDownTimer = new CountDownTimer(MILLISINFUTURE, COUNT_DOWN_INTERVAL) {
            public void onTick(long millisUntilFinished) {
                binding.certSend.setText((count / 60) + ":" + String.format("%02d", count % 60));//분:초로 구분되어 보여짐
                count--;
            }

            public void onFinish() {
                count = 180;
                cert_number = numberGen(6, 1);// 끝났을경우 인증번호로 다른거로 바꿔서 인증막음
                binding.certSend.setText("재전송");
                binding.certSend.setEnabled(true);// 전송버튼 활성화
                binding.certNum.setEnabled(false);// 번호입력상자 비활성화
                binding.certnumCheck.setEnabled(false);// 번호체크버튼 비활성화
            }
        };
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            countDownTimer.cancel();
        } catch (Exception e) {
        }
        countDownTimer = null;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
            }
        });
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AccessToken.setCurrentAccessToken(null);
        SessionManager<TwitterSession> sessionManager = TwitterCore.getInstance().getSessionManager(); //트위터 로그아웃
        sessionManager.clearActiveSession();  //트위터 로그아웃
        finish();

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