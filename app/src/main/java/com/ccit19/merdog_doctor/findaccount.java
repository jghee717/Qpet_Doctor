package com.ccit19.merdog_doctor;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ccit19.merdog_doctor.custom_dialog.CustomAnimationDialog;
import com.ccit19.merdog_doctor.variable.MyGlobals;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class findaccount extends AppCompatActivity {
    private CustomAnimationDialog customAnimationDialog;
    private EditText doctorid,findpassphone,s_doctorPhoneModify,doctorname;
    private TextView phonecheck2,findidview,phonecheck,doctornamealt,doctoridalt;
    private Button acpass_button,button_Modify;
    String doctor_num;
    int check = 0;
    int check2 = 0;
    int check3 = 0;
    String u = MyGlobals.getInstance().getData();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findaccount);
        doctorid = findViewById(R.id.doctorid);
        findpassphone = findViewById(R.id.findpassphone);
        s_doctorPhoneModify = findViewById(R.id.s_doctorPhoneModify);
        doctorname = findViewById(R.id.doctorname);
        phonecheck2 = findViewById(R.id.phonecheck2);
        findidview = findViewById(R.id.findidview);
        phonecheck = findViewById(R.id.phonecheck);
        doctornamealt = findViewById(R.id.doctornamealt);
        doctoridalt = findViewById(R.id.doctoridalt);
        acpass_button = findViewById(R.id.acpass_button);
        button_Modify = findViewById(R.id.button_Modify);
        doctor_num = SaveSharedPreference.getdoctornum(getApplication().getApplicationContext());
        customAnimationDialog = new CustomAnimationDialog(findaccount.this);
        //아이디찾기 폰번호 입력시
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
                    phonecheck.setText("아이디 찾기를 눌러주세요.");
                    phonecheck.setTextColor(Color.parseColor("#5CAB7D"));
                    check = 1;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
//아이디찾기버튼
        button_Modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                customAnimationDialog.show();
                String url = u + "/doctorapp/find_id";
                /* Create request */
                Map<String, String> params = new HashMap<String, String>();
                params.put("doctor_name",doctorname.getText().toString());
                params.put("doctor_phone", s_doctorPhoneModify.getText().toString());

                JsonObjectRequest updateForm = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                        url, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                boolean success = false;
                                try {
                                    success = response.getBoolean("result");
                                    if (success == true) {
                                        customAnimationDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "아이디 찾기", Toast.LENGTH_LONG).show();
                                            findidview.setText(response.getString("doctor_id"));
                                            findidview.setVisibility(View.VISIBLE);

                                    } else {
                                        customAnimationDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "정보가 올바르지 않습니다.", Toast.LENGTH_LONG).show();
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

        //비밀번호찾기 폰번호 입력시
        s_doctorPhoneModify.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String phonePattern = "^01[016789]{1}-?[0-9]{3,4}-?[0-9]{4}$";
                Matcher matcher = Pattern.compile(phonePattern).matcher(s_doctorPhoneModify.getText());

                if (!matcher.matches()) {
                    phonecheck2.setText("형식에 맞지 않는 번호입니다.");
                    phonecheck2.setTextColor(Color.parseColor("#E53A40"));
                    check3 = 0;
                } else if (matcher.matches()) {
                    phonecheck2.setText("비밀번호 찾기를 눌러주세요.");
                    phonecheck2.setTextColor(Color.parseColor("#5CAB7D"));
                    check3 = 1;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
//비밀번호찾기버튼
        acpass_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                customAnimationDialog.show();
                String url = u + "/doctorapp/find_pw";
                /* Create request */
                Map<String, String> params = new HashMap<String, String>();
                params.put("doctor_id", doctorid.getText().toString());
                params.put("doctor_phone", findpassphone.getText().toString());

                JsonObjectRequest update2Form = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                        url, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                boolean success = false;
                                try {
                                    success = response.getBoolean("result");
                                    if (success == true) {
                                        customAnimationDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "임시비밀번호를 전송하였습니다.", Toast.LENGTH_LONG).show();
                                    } else {
                                        customAnimationDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "정보를 올바르게 입력해주세요.", Toast.LENGTH_LONG).show();
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
                AppController.getInstance(getApplicationContext()).addToRequestQueue(update2Form);
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customAnimationDialog.dismiss();
    }
}