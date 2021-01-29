package com.ccit19.merdog_doctor.ui.home;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ccit19.merdog_doctor.AppController;
import com.ccit19.merdog_doctor.GpsTracker;
import com.ccit19.merdog_doctor.LocationState;
import com.ccit19.merdog_doctor.MainActivity;
import com.ccit19.merdog_doctor.R;
import com.ccit19.merdog_doctor.Regit_2Activity;
import com.ccit19.merdog_doctor.SaveSharedPreference;
import com.ccit19.merdog_doctor.custom_dialog.CustomAnimationDialog;
import com.ccit19.merdog_doctor.hospital_modify;
import com.ccit19.merdog_doctor.variable.MyGlobals;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {
    private CustomAnimationDialog customAnimationDialog;
    private HomeViewModel homeViewModel;
    private String doctor_id;
    private TextView location, state_textview;
    private Button select_mylocation, select_map;
    private ImageButton chat_state_button;
    private Switch Activityswitch;
    private String doctor_num, state, latitude, longitude, address, getaddress, getlocation;
    private GpsTracker gpsTracker;
    private Boolean Check;
    private long mLastClickTime = 0;
    private Geocoder geocoder;
    String u = MyGlobals.getInstance().getData();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.hide();
        actionBar.setTitle("홈");
        actionBar.setDisplayHomeAsUpEnabled(false);
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        Activityswitch = root.findViewById(R.id.Activityswitch);
        location = root.findViewById(R.id.location);
        select_mylocation = root.findViewById(R.id.select_mylocation);
        select_map = root.findViewById(R.id.select_map);
        doctor_num = SaveSharedPreference.getdoctornum(getActivity().getApplicationContext());
        Check = SaveSharedPreference.getState(getActivity().getApplicationContext());
        getlocation = SaveSharedPreference.getlocation(getActivity().getApplicationContext());
        customAnimationDialog = new CustomAnimationDialog(root.getContext());
        chat_state_button = root.findViewById(R.id.chat_state_button);
        state_textview =  root.findViewById(R.id.state_textview);
        geocoder = new Geocoder(root.getContext());

        // 상태값 표시
        if (Check) {
            Activityswitch.setChecked(Check);
            location.setText(SaveSharedPreference.getnowaddress(getActivity().getApplicationContext()).replace("대한민국",""));
            location.setGravity(Gravity.CENTER_VERTICAL);
            chat_state_button.setBackgroundResource(R.drawable.chat_state_on);
            state_textview.setText("상담을 진행중입니다.");
        } else {
            Activityswitch.setChecked(Check);
            chat_state_button.setBackgroundResource(R.drawable.chat_state_off);
            state_textview.setText("버튼을 눌러 상담을 진행해주세요.");
        }



        //지도로 현위치선택
        select_mylocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startProgress();
            if (Check) {
                    String url = u + "/doctorapp/state";
                    gpsTracker = new GpsTracker(getActivity());
                    latitude = Double.toString(gpsTracker.getLatitude());
                    longitude = Double.toString(gpsTracker.getLongitude());
                    address = getCurrentAddress(gpsTracker.getLatitude(), gpsTracker.getLongitude());
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("doctor_id", doctor_num);
                    params.put("address", address);
                    params.put("latitude", latitude);
                    params.put("longitude", longitude);
                    params.put("doctor_state", "on");
                    final JsonObjectRequest locationcheck = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                            url, new JSONObject(params),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    boolean success = false;
                                    try {
                                        success = response.getBoolean("result");
                                        if (success) {
                                            SaveSharedPreference.editNowAddress(getActivity().getApplicationContext(), address);
                                            location.setText(address.replace("대한민국",""));
                                            location.setGravity(Gravity.CENTER_VERTICAL);
                                        } else {
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                Toast.makeText(getActivity().getApplicationContext(), "서버로부터 응답이 없습니다.", Toast.LENGTH_LONG).show();
                            } else if (error instanceof AuthFailureError) {
                                Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                            } else if (error instanceof ServerError) {
                                Toast.makeText(getActivity().getApplicationContext(), "서버오류입니다.\n잠시후에 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                            } else if (error instanceof NetworkError) {
                                Toast.makeText(getActivity().getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
                            } else if (error instanceof ParseError) {
                                Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    AppController.getInstance(getActivity().getApplicationContext()).addToRequestQueue(locationcheck);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "상담 상태를 on으로 해주세요.", Toast.LENGTH_LONG).show();
                }
            }
        });

        //on off 이미지 버튼
        chat_state_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Check){
                    Activityswitch.setChecked(false);
                }else {
                    Activityswitch.setChecked(true);
                }
            }
        });

        //지도로 위치선택
        select_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (Check) {
                    Intent intent = new Intent(getActivity(), LocationState.class);
                    intent.putExtra("state","0");
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "상담 상태를 on으로 해주세요.", Toast.LENGTH_LONG).show();
                }
            }
        });

        //스위치 버튼
        Activityswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // 스위치 버튼이 체크되었는지 검사하여 텍스트뷰에 각 경우에 맞게 출력합니다.
                String url = u + "/doctorapp/state";
                /* Create request */
                Map<String, String> params = new HashMap<String, String>();
                if (isChecked) {
                    state_textview.setText("상담을 진행중입니다.");
                    chat_state_button.setBackgroundResource(R.drawable.chat_state_on);
                    Check = true;
                    state = "on";
                    if(!getlocation.equals("")) {
                        List<Address> list = null;
                        try {
                            list = geocoder.getFromLocationName(
                                    getlocation, // 지역 이름
                                    10); // 읽을 개수
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
                        }
                        if (list != null) {
                            if (list.size() == 0) {
                            } else {
                                latitude = Double.toString(list.get(0).getLatitude());        // 위도
                                longitude = Double.toString(list.get(0).getLongitude());    // 경도
                            }
                        }
                    }
                    params.put("address", getlocation);
                    params.put("latitude", latitude);
                    params.put("longitude", longitude);
                } else {
                    state_textview.setText("버튼을 눌러 상담을 진행해주세요.");
                    chat_state_button.setBackgroundResource(R.drawable.chat_state_off);
                    state = "off";
                    Check = false;
                }
                params.put("doctor_id", doctor_num);
                params.put("doctor_state", state);
                JsonObjectRequest idcheckForm = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                        url, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                boolean success = false;
                                try {
                                    success = response.getBoolean("result");
                                    if (success) {
                                        SaveSharedPreference.editState(getActivity().getApplicationContext(), Check);
                                        SaveSharedPreference.editNowAddress(getActivity().getApplicationContext(), response.getString("address"));
                                        location.setText(response.getString("address").replace("대한민국","")); // db에 주소 값있으면 넣어줘야함
                                            location.setGravity(Gravity.CENTER_VERTICAL);
                                    } else {
                                        SaveSharedPreference.editState(getActivity().getApplicationContext(), false);
                                        Activityswitch.setChecked(false);
                                        Toast.makeText(getActivity().getApplicationContext(), "병원 주소를 입력해주세요.", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getActivity().getApplicationContext(), hospital_modify.class);
                                        startActivity(intent);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getActivity().getApplicationContext(), "서버로부터 응답이 없습니다.", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(getActivity().getApplicationContext(), "서버오류입니다.\n잠시후에 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getActivity().getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                idcheckForm.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                AppController.getInstance(getActivity().getApplicationContext()).addToRequestQueue(idcheckForm);
            }
        });

        return root;
    }

    public String getCurrentAddress(double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(getActivity(), "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(getActivity(), "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(getActivity(), "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString() + "\n";
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