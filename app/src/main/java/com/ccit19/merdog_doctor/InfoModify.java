package com.ccit19.merdog_doctor;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;
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
import com.ccit19.merdog_doctor.custom_dialog.CustomAnimationDialog;
import com.ccit19.merdog_doctor.variable.MyGlobals;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import android.widget.EditText;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InfoModify extends AppCompatActivity {
    private CustomAnimationDialog customAnimationDialog;
    private EditText cert_num, s_doctorPass, s_doctorPassVeri, s_doctorPhoneModify, certNum;
    private TextView textView20, textView17, Passcheck, PassVericheck, doctorphone, certnumcheck, phonecheck;
    private Button cert_send, certnum_check, button_Modify, numbercheck, passmodify;
    String doctor_num;
    String cert_number;
    private static final int MILLISINFUTURE = 180 * 1000;
    private static final int COUNT_DOWN_INTERVAL = 1000;
    int check = 0;
    int check2 = 0;
    int check3 = 0;
    int check4 =0;
    private int count = 180;
    private CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_info_modify);
        certNum = findViewById(R.id.certNum);
        passmodify = findViewById(R.id.passmodify);
        s_doctorPass = findViewById(R.id.s_doctorPass);
        s_doctorPassVeri = findViewById(R.id.s_doctorPassVeri);
        s_doctorPhoneModify = findViewById(R.id.s_doctorPhoneModify);
        doctor_num = SaveSharedPreference.getdoctornum(getApplication().getApplicationContext());
        textView20 = findViewById(R.id.textView20);
        cert_num = findViewById(R.id.cert_num);
        textView17 = findViewById(R.id.textView17);
        numbercheck = findViewById(R.id.numbercheck);
        Passcheck = findViewById(R.id.Passcheck);
        PassVericheck = findViewById(R.id.PassVericheck);
        PassVericheck = findViewById(R.id.PassVericheck);
        doctorphone = findViewById(R.id.doctorphone);
        certnumcheck = findViewById(R.id.certnumcheck);
        phonecheck = findViewById(R.id.phonecheck);
        cert_send = findViewById(R.id.cert_send);
        certnum_check = findViewById(R.id.certnum_check);
        button_Modify = (Button) findViewById(R.id.button_Modify);
        customAnimationDialog = new CustomAnimationDialog(InfoModify.this);
        super.onCreate(savedInstanceState);
        cert_send.setVisibility(View.GONE);
        certnum_check.setEnabled(false);
        String u = MyGlobals.getInstance().getData();

        String url = u + "/doctorapp/mypage_load";
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
                                if(response.getString("doctor_id")=="null"){
                                    Toast.makeText(getApplicationContext(), "카카오 회원은 불가능 합니다.", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                    startActivity(intent);
                                }else{
                                    textView20.setText(response.getString("doctor_id"));
                                }
                                textView17.setText(response.getString("doctor_name"));
                                doctorphone.setText(response.getString("doctor_phone"));
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

            }
        });
        AppController.getInstance(getApplicationContext()).addToRequestQueue(inForm);


        //정규식
        s_doctorPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String pwPattern = "^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-z]).{5,15}$";
                Matcher matcher = Pattern.compile(pwPattern).matcher(s_doctorPass.getText());

                pwPattern = "(.)\\1\\1\\1";
                Matcher matcher2 = Pattern.compile(pwPattern).matcher(s_doctorPass.getText());

                if (!matcher.matches()) {
                    Passcheck.setText("영문, 숫자, 특수문자 포함 5~15자리로 입력해주세요.");
                    check4=0;
                } else if (matcher2.find()) {
                    Passcheck.setText("같은문자는 4개이상 사용할수 없습니다.");
                    check4=0;
                } else if (s_doctorPass.getText().toString().contains(" ")) {
                    Passcheck.setText("공백은 입력이 불가합니다.");
                    check4=0;
                } else {
                    Passcheck.setText("");
                    check4=1;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (s_doctorPass.getText().toString().equals(s_doctorPassVeri.getText().toString()) && !s_doctorPassVeri.getText().toString().isEmpty()) {
                    PassVericheck.setText("비밀번호가 일치합니다.");
                    PassVericheck.setTextColor(Color.parseColor("#5CAB7D"));
                    check3 = 1;
                } else {
                    PassVericheck.setText("비밀번호가 일치하지 않습니다.");
                    PassVericheck.setTextColor(Color.parseColor("#E53A40"));
                    check3 = 0;
                }
            }
        });

        s_doctorPassVeri.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (s_doctorPass.getText().toString().equals(s_doctorPassVeri.getText().toString()) && !s_doctorPassVeri.getText().toString().isEmpty()) {
                    PassVericheck.setText("비밀번호가 일치합니다.");
                    PassVericheck.setTextColor(Color.parseColor("#5CAB7D"));
                    check3 = 1;
                } else {
                    PassVericheck.setText("비밀번호가 일치하지 않습니다.");
                    PassVericheck.setTextColor(Color.parseColor("#E53A40"));
                    check3 = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //인증번호
        cert_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customAnimationDialog.show();
                String url = u + "/ajax/sms";
                /* Create request */
                Map<String, String> params = new HashMap<String, String>();
                params.put("phone", s_doctorPhoneModify.getText().toString());
                cert_number = numberGen(6, 1);
                params.put("number", cert_number);
                JsonObjectRequest certForm = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                        url, new JSONObject(params),
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                customAnimationDialog.dismiss();
                                String pnPattern = "^01[016789]{1}-?[0-9]{3,4}-?[0-9]{4}$";

                                boolean success = false;
                                try {
                                    success = response.getBoolean("result");
                                    if (success) {
                                        Toast.makeText(getApplicationContext(), "인증번호가 발송되었습니다.", Toast.LENGTH_LONG).show();
                                        countDownTimer();
                                        countDownTimer.start();
                                        cert_send.setEnabled(false);
                                        certnum_check.setEnabled(true);

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
                AppController.getInstance(getApplicationContext()).addToRequestQueue(certForm);
                certNum.requestFocus();
            }
        });

        certnum_check.setOnClickListener(new View.OnClickListener() {// 인증번호 체크버튼 이벤트
            @Override
            public void onClick(View v) {
                if (cert_number.equals(certNum.getText().toString())) {
                    certnumcheck.setText("인증되었습니다.");
                    Toast.makeText(getApplicationContext(), "인증되었습니다.", Toast.LENGTH_LONG).show();
                    certnum_check.setEnabled(false);
                    cert_send.setText("인증완료");
                    certNum.setEnabled(false);
                    certnum_check.setEnabled(false);
                    countDownTimer.cancel();
                    check2 = 1;
                } else {
                    certnumcheck.setText("올바른 인증번호를 입력하세요.");
                    certnumcheck.setTextColor(Color.parseColor("#E53A40"));
                    check2 = 0;
                }
            }
        });
        //폰번호 입력시
        s_doctorPhoneModify.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String phonePattern = "^01[016789]{1}-?[0-9]{3,4}-?[0-9]{4}$";
                Matcher matcher = Pattern.compile(phonePattern).matcher(s_doctorPhoneModify.getText());

                if (!matcher.matches()) {
                    phonecheck.setText("형식에 맞지 않는 번호입니다.");
                    phonecheck.setTextColor(Color.parseColor("#E53A40"));
                    check = 0;
                } else if (matcher.matches()) {
                    phonecheck.setText("확인버튼을 눌러주세요.");
                    phonecheck.setTextColor(Color.parseColor("#5CAB7D"));
                    check = 1;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        //중복확인
        numbercheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customAnimationDialog.show();
                if (!s_doctorPhoneModify.getText().toString().isEmpty()) {
                    String url = u + "/doctorapp/check_phone";
                    /* Create request */
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("doctor_phone", s_doctorPhoneModify.getText().toString());

                    JsonObjectRequest loginForm = new JsonObjectRequest(Request.Method.POST,
                            url, new JSONObject(params),
                            new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    customAnimationDialog.dismiss();
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
                                            phonecheck.setText("올바른 전화번호를 입력해주세요.");
                                            phonecheck.setTextColor(Color.parseColor("#E53A40"));
                                        } else if (check == 1) {
                                            if (!success) {
                                                phonecheck.setText("이미 사용중인 전화번호입니다.");
                                                phonecheck.setTextColor(Color.parseColor("#E53A40"));
                                                check = 0;
                                            } else {
                                                phonecheck.setText("인증되었습니다.");
                                                phonecheck.setTextColor(Color.parseColor("#5CAB7D"));
                                                check = 2;
                                                numbercheck.setVisibility(View.GONE);
                                                cert_send.setVisibility(View.VISIBLE);
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
                    AppController.getInstance(view.getContext()).addToRequestQueue(loginForm);
                } else {
                    Toast.makeText(getApplicationContext(), "번호를 입력해주세요.", Toast.LENGTH_LONG).show();
                    customAnimationDialog.dismiss();
                }
            }
        });
        //폰수정클릭시
        button_Modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                customAnimationDialog.show();
                String url = u + "/doctorapp/mypage_update";
                /* Create request */
                Map<String, String> params = new HashMap<String, String>();
                params.put("doctor_id", doctor_num);
                params.put("doctor_phone", s_doctorPhoneModify.getText().toString());

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
                                        if ((check2 == 1)) {
                                            Toast.makeText(getApplicationContext(), "전화번호 수정 완료", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                        } else if (!(check2 == 1)) {
                                            Toast.makeText(getApplicationContext(), "휴대폰 인증을 해주세요.", Toast.LENGTH_LONG).show();
                                        } else if (!(check == 2)) {
                                            Toast.makeText(getApplicationContext(), "중복확인 버튼을 눌러주세요.", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "올바르게 입력해주세요", Toast.LENGTH_LONG).show();
                                        }
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
                        customAnimationDialog.dismiss();
                    }
                });
                AppController.getInstance(getApplicationContext()).addToRequestQueue(updateForm);
            }
        });

        //비밀번호수정클릭
        passmodify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                customAnimationDialog.show();
                String url = u + "/doctorapp/mypage_update";
                /* Create request */
                Map<String, String> params = new HashMap<String, String>();
                params.put("doctor_id", doctor_num);
                params.put("doctor_pw", s_doctorPass.getText().toString());

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
                                        if (s_doctorPass.getText().toString().equals(s_doctorPassVeri.getText().toString())) {
                                            if ((check3 == 1 && check4==1)) {
                                                Toast.makeText(getApplicationContext(), "비밀번호 수정 완료", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                                finish();
                                            } else if (!(check4 == 1)) {
                                                Toast.makeText(getApplicationContext(), "비밀번호를 올바르게 입력해주세요.", Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            Toast.makeText(getApplicationContext(), "올바르게 입력해주세요", Toast.LENGTH_LONG).show();
                                        }
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
                        customAnimationDialog.dismiss();
                    }
                });
                AppController.getInstance(getApplicationContext()).addToRequestQueue(updateForm);
            }
        });


    }// oncreate

    public static String numberGen(int len, int dupCd) {

        Random rand = new Random();
        String numStr = ""; //난수가 저장될 변수

        for (int i = 0; i < len; i++) {

            //0~9 까지 난수 생성
            String ran = Integer.toString(rand.nextInt(10));

            if (dupCd == 1) {
                //중복 허용시 numStr에 append
                numStr += ran;
            } else if (dupCd == 2) {
                //중복을 허용하지 않을시 중복된 값이 있는지 검사한다
                if (!numStr.contains(ran)) {
                    //중복된 값이 없으면 numStr에 append
                    numStr += ran;
                } else {
                    //생성된 난수가 중복되면 루틴을 다시 실행한다
                    i -= 1;
                }
            }
        }
        return numStr;
    }

    public void countDownTimer() {//휴대폰 번호 카운트다운 타이머
        countDownTimer = new CountDownTimer(MILLISINFUTURE, COUNT_DOWN_INTERVAL) {
            public void onTick(long millisUntilFinished) {
                cert_send.setText((count / 60) + ":" + String.format("%02d", count % 60));//분:초로 구분되어 보여짐
                count--;
            }

            public void onFinish() {
                count = 180;
                cert_number = numberGen(6, 1);// 끝났을경우 인증번호로 다른거로 바꿔서 인증막음
                cert_send.setText("재전송");
                cert_send.setEnabled(true);// 전송버튼 활성화
                certNum.setEnabled(false);// 번호입력상자 비활성화
                certnum_check.setEnabled(false);// 번호체크버튼 비활성화
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
        customAnimationDialog.dismiss();
    }
}
