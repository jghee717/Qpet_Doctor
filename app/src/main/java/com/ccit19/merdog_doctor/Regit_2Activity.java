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
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ccit19.merdog_doctor.databinding.ActivityRegit2Binding;
import com.ccit19.merdog_doctor.custom_dialog.CustomAnimationDialog;
import com.ccit19.merdog_doctor.variable.MyGlobals;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regit_2Activity extends AppCompatActivity {
    private CustomAnimationDialog customAnimationDialog;
    ActivityRegit2Binding binding;
    String phone_number;
    String cert_number;
    private static final int MILLISINFUTURE = 180 * 1000;
    private static final int COUNT_DOWN_INTERVAL = 1000;
    int check = 0;
    int check2 = 0;
    int check3 = 0;
    private int count = 180;
    private CountDownTimer countDownTimer;
    String u = MyGlobals.getInstance().getData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_regit_2);
        binding.setActivity(this);
        phone_number = getIntent().getStringExtra("phone");
        binding.sDoctorPhone.setText(phone_number);
        //setContentView(R.layout.activity_regit_2);
        customAnimationDialog = new CustomAnimationDialog(Regit_2Activity.this);
        binding.idCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (!binding.sDoctorId.getText().toString().isEmpty()) {
                    String url = u+ "/doctorapp/check_id";
                    /* Create request */
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("doctor_id", binding.sDoctorId.getText().toString());

                    JsonObjectRequest idcheckForm = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                            url, new JSONObject(params),
                            new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    String idPattern = "^[a-z0-9\\-_]{5,20}$";   //공백제외 , -_포함, 영문소문자포함 5-20글자
                                    //String idPattern = "^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-z]).{5,15}$";
                                    Matcher matcher = Pattern.compile(idPattern).matcher(binding.sDoctorId.getText());

                                    boolean success = false;
                                    try {
                                        success = response.getBoolean("result");
                                        if (check == 0) {
                                            binding.idAlt.setText("5~20자의 영소문자, 숫자, 특수기호(_),(-)만 가능합니다");
                                            binding.idAlt.setTextColor(Color.parseColor("#E53A40"));
                                        } else if (check == 1) {
                                            if (!success) {
                                                binding.idAlt.setText("이미 사용중인 아이디입니다");
                                                binding.idAlt.setTextColor(Color.parseColor("#E53A40"));
                                                check = 0;
                                            } else if (success) {
                                                binding.idAlt.setText("사용가능한 아이디입니다.");
                                                binding.idAlt.setTextColor(Color.parseColor("#5CAB7D"));
                                                check = 2;
                                            }
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
                    AppController.getInstance(getApplicationContext()).addToRequestQueue(idcheckForm);
                } else {
                    binding.idAlt.setText("아이디를 입력해주세요.");
                    binding.idAlt.setTextColor(Color.parseColor("#E53A40"));
                }

            }
        });
        binding.sDoctorId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String idPattern = "^[a-z0-9\\-_]{5,20}$";   //공백제외 , -_포함, 영문소문자포함 5-20글자
                Matcher matcher = Pattern.compile(idPattern).matcher(binding.sDoctorId.getText());
                if (!matcher.matches()) {   //중복된 아이디값은 없는데 정규식에 위배
                    binding.idAlt.setText("5~20자의 영소문자, 숫자, 특수기호(_),(-)만 가능합니다");
                    binding.idAlt.setTextColor(Color.parseColor("#E53A40"));
                    check = 0;
                } else if (matcher.matches()) {       //중복된값이 아이디값이 있으면 사용중인 아이디 출력
                    binding.idAlt.setText("중복확인 버튼을 눌러주세요");
                    binding.idAlt.setTextColor(Color.parseColor("#E53A40"));
                    check = 1;
                }
//                binding.idAlt.setText("5~20자의 영소문자, 숫자, 특수기호(_),(-)만 가능합니다");
            }
        });

        binding.sDoctorPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String pwPattern = "^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-z]).{5,15}$";
                Matcher matcher = Pattern.compile(pwPattern).matcher(binding.sDoctorPass.getText());

                pwPattern = "(.)\\1\\1\\1";
                Matcher matcher2 = Pattern.compile(pwPattern).matcher(binding.sDoctorPass.getText());

                if (!matcher.matches()) {
                    binding.passAlt.setText("영문, 숫자, 특수문자 포함 5~15자리로 입력해주세요.");
                } else if (matcher2.find()) {
                    binding.passAlt.setText("같은문자는 4개이상 사용할수 없습니다.");
                } else if (binding.sDoctorPass.getText().toString().contains(" ")) {
                    binding.passAlt.setText("공백은 입력이 불가합니다.");
                } else {
                    binding.passAlt.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (binding.sDoctorPass.getText().toString().equals(binding.sPassVeri.getText().toString()) && !binding.sPassVeri.getText().toString().isEmpty()) {
                    binding.passvrifAlt.setText("비밀번호가 일치합니다.");
                    binding.passvrifAlt.setTextColor(Color.parseColor("#5CAB7D"));
                    check3 = 1;
                } else {
                    binding.passvrifAlt.setText("비밀번호가 일치하지 않습니다.");
                    binding.passvrifAlt.setTextColor(Color.parseColor("#E53A40"));
                    check3 = 0;
                }
                //test.setText(BCrypt.hashpw(userpw.getText().toString(), BCrypt.gensalt(10)));//암호화코드
                //boolean isValidPassword = BCrypt.checkpw(password, passwordHashed); 검증코드
            }
        });
        binding.sPassVeri.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (binding.sDoctorPass.getText().toString().equals(binding.sPassVeri.getText().toString()) && !binding.sPassVeri.getText().toString().isEmpty()) {
                    binding.passvrifAlt.setText("비밀번호가 일치합니다.");
                    binding.passvrifAlt.setTextColor(Color.parseColor("#5CAB7D"));
                    check3 = 1;
                } else {
                    binding.passvrifAlt.setText("비밀번호가 일치하지 않습니다.");
                    binding.passvrifAlt.setTextColor(Color.parseColor("#E53A40"));
                    check3 = 0;
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
                params.put("phone", phone_number);
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



        binding.finButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (binding.sDoctorPass.getText().toString().equals(binding.sPassVeri.getText().toString())//패스워드 일치확인
                        && !binding.sDoctorId.getText().toString().isEmpty()) {
                    if ((check == 2) && (check2 == 1) && (check3 == 1) && !binding.sDoctorname.getText().toString().isEmpty() && binding.AgreedCheck.isChecked()) {
                        startProgress();
                        String doctor_id = binding.sDoctorId.getText().toString();
                        String doctor_pw = binding.sPassVeri.getText().toString();
                        String doctor_name = binding.sDoctorname.getText().toString();
                        String doctor_phone = getIntent().getStringExtra("phone");
                        Intent intent = new Intent(Regit_2Activity.this, Regit_3Activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("doctor_id", doctor_id);
                        intent.putExtra("doctor_pw", doctor_pw);
                        intent.putExtra("doctor_name", doctor_name);
                        intent.putExtra("doctor_phone", doctor_phone);
                        startActivity(intent);
                    } else if (!(check == 2)) {
                        Toast.makeText(getApplicationContext(), "중복확인 버튼을 눌러주세요.", Toast.LENGTH_LONG).show();
                    } else if (!(check3 == 1)) {
                        Toast.makeText(getApplicationContext(), "비밀번호를 올바르게 입력해주세요.", Toast.LENGTH_LONG).show();
                    } else if (binding.sDoctorname.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "이름을 입력해 주세요.", Toast.LENGTH_LONG).show();
                    }
                    else if (!(check2 == 1)) {
                        Toast.makeText(getApplicationContext(), "휴대폰 인증을 해주세요.", Toast.LENGTH_LONG).show();
                    }
                    else if (!binding.AgreedCheck.isChecked()){
                        Toast.makeText(getApplicationContext(), "약관 동의를 해주세요.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "올바르게 입력해주세요", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

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