package com.ccit19.merdog_doctor;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ccit19.merdog_doctor.custom_dialog.CustomDialog;
import com.ccit19.merdog_doctor.databinding.ActivityRegit2Binding;
import com.ccit19.merdog_doctor.custom_dialog.CustomAnimationDialog;
import com.ccit19.merdog_doctor.ui.notifications.NotificationsFragment;
import com.ccit19.merdog_doctor.variable.MyGlobals;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class hospital_modify2 extends AppCompatActivity {
    ActivityRegit2Binding binding;
    private CustomAnimationDialog customAnimationDialog;
    private CustomDialog customDialog;
    private EditText hs_url, hs_info, hospital_name ,hs_address;
    private Button hs_regit_B ,select_map2, select_mylocation2;
    private String doctor_num, state, latitude, longitude, address, getaddress, getlocation;
    private GpsTracker gpsTracker;
    String u = MyGlobals.getInstance().getData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("병원 정보 수정");
        actionBar.setDisplayHomeAsUpEnabled(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hospital_modify2);
        setContentView(R.layout.activity_hospital_modify2);
        hs_address = findViewById(R.id.hs_address);
        hs_url = findViewById(R.id.hs_url);
        hs_info = findViewById(R.id.hs_info);
        hospital_name = findViewById(R.id.hospital_name);
        select_map2 = findViewById(R.id.select_map2);
        select_mylocation2 = findViewById(R.id.select_mylocation2);
        hs_regit_B = findViewById(R.id.hs_regit_B);
        doctor_num = SaveSharedPreference.getdoctornum(getApplicationContext().getApplicationContext());
        customAnimationDialog = new CustomAnimationDialog(hospital_modify2.this);
        String url = u + "/doctorapp/hospital_load";
        /* Create request */
        Map<String, String> params = new HashMap<String, String>();
        params.put("doctor_id", doctor_num);

        JsonObjectRequest loadForm = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        boolean success = false;
                        try {
                            success = response.getBoolean("result");
                            if (success) {
                                String str = "null";
                                if(response.getString("hospital_name").equals(str)){
                                    hospital_name.setText(null);
                                } else{
                                    hospital_name.setText(response.getString("hospital_name"));
                                }
                                if(response.getString("hospital_address").equals(str)){
                                        hs_address.setText(null);
                                } else{
                                    if(getIntent().getStringExtra("address") != null) {
                                        hs_address.setText(getIntent().getStringExtra("address"));
                                    } else {
                                        hs_address.setText(response.getString("hospital_address"));
                                    }
                                }
                                if(response.getString("hospital_url").equals(str)){
                                    hs_url.setText(null);
                                } else{
                                    hs_url.setText(response.getString("hospital_url"));
                                }
                                if(response.getString("hospital_intro").equals(str)){
                                    hs_info.setText(null);
                                } else{
                                    hs_info.setText(response.getString("hospital_intro"));
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "테스트 실패", Toast.LENGTH_LONG).show();
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
        AppController.getInstance(getApplicationContext()).addToRequestQueue(loadForm);

        // 다이알로그 확인
        hs_regit_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if(!hospital_name.getText().toString().isEmpty() && !hs_url.getText().toString().isEmpty() && !hs_info.getText().toString().isEmpty()
                        && !hs_address.getText().toString().isEmpty()) {
                    customDialog = new CustomDialog(hospital_modify2.this,"수정 하시겠습니까?",positiveListener,negativeListener);
                    customDialog.show();
                }else {
                    Toast.makeText(getApplicationContext(), "빈칸을 입력해 주세요", Toast.LENGTH_LONG).show();
                }
            }
        });

        //내 위치 선택
        select_mylocation2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gpsTracker = new GpsTracker(getApplication());
                address = getCurrentAddress(gpsTracker.getLatitude(), gpsTracker.getLongitude());
                hs_address.setText(address);
            }
        });

        //지도로 선택
        select_map2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), LocationState.class);
                intent.putExtra("state","2");
                startActivity(intent);

            }
        });
    }

    public String getCurrentAddress(double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(getApplication(), Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(getApplication(), "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(getApplication(), "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(getApplication(), "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString() + "\n";

    }

    //다이알로그 확인
    private View.OnClickListener positiveListener = new View.OnClickListener() {
        public void onClick(View v) {

                customAnimationDialog.show();
                String url = u + "/doctorapp/hospital_update";
                /* Create request */
                Map<String, String> params = new HashMap<String, String>();
                params.put("hospital_name", hospital_name.getText().toString());
                params.put("hospital_address", hs_address.getText().toString());
                params.put("hospital_url", hs_url.getText().toString());
                params.put("hospital_intro", hs_info.getText().toString());
                params.put("doctor_id", doctor_num);

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
                                        SaveSharedPreference.editLoction(getApplicationContext(), hs_address.getText().toString());
                                        Toast.makeText(getApplicationContext(), "수정 완료", Toast.LENGTH_LONG).show();
                                        onBackPressed();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "등록 실패", Toast.LENGTH_LONG).show();

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

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}


