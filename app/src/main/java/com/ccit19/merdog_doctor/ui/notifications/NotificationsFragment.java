package com.ccit19.merdog_doctor.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ccit19.merdog_doctor.AppController;
import com.ccit19.merdog_doctor.InfoModify;
import com.ccit19.merdog_doctor.MainActivity;
import com.ccit19.merdog_doctor.Records_Activity;
import com.ccit19.merdog_doctor.custom_dialog.CustomDialog;
import com.ccit19.merdog_doctor.drawal_account.Account_update;
import com.ccit19.merdog_doctor.drawal_account.Drawal;
import com.ccit19.merdog_doctor.LoginActivity;
import com.ccit19.merdog_doctor.R;
import com.ccit19.merdog_doctor.SaveSharedPreference;
import com.ccit19.merdog_doctor.drawal_account.Drawal_list;
import com.ccit19.merdog_doctor.drawal_account.setAccount;
import com.ccit19.merdog_doctor.hospital_modify;
import com.ccit19.merdog_doctor.hospital_modify2;
import com.ccit19.merdog_doctor.custom_dialog.CustomAnimationDialog;
import com.ccit19.merdog_doctor.variable.MyGlobals;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotificationsFragment extends Fragment {
    private CustomDialog customDialog;
    private CustomAnimationDialog customAnimationDialog;
    private NotificationsViewModel notificationsViewModel;
    private Button hs_regit;
    private ImageView imageView, hs_modify;;
    private TextView medical_records, mile, drawal, logout_button, list,s_doctorname, textView12, doctorRating, doctorMiles, adviceNum, hospital_name, textView4, textView9, textView10, textView22, tvv, address, hs_url, hs_info;
    private String doctor_name, doctor_id, doctor_num;
    // doctorMiles, doctorRating, adviceNum;
    int addcheck = 0;
    int urlcheck = 0;
    int infocheck = 0;
    private long mLastClickTime = 0;
    String u = MyGlobals.getInstance().getData();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.hide();
        actionBar.setTitle("내 정보");
        actionBar.setDisplayHomeAsUpEnabled(false);
        notificationsViewModel = ViewModelProviders.of(this).get(NotificationsViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        textView4 = root.findViewById(R.id.textView4);
        imageView = root.findViewById(R.id.imageView);
        hs_modify = root.findViewById(R.id.hs_modify);
        textView9 = root.findViewById(R.id.textView9);
        textView10 = root.findViewById(R.id.textView10);
        tvv = root.findViewById(R.id.tvv);
        textView22 = root.findViewById(R.id.textView22);
        address = root.findViewById(R.id.addressTextView);
        hospital_name = root.findViewById(R.id.hospital_name);
        hs_url = root.findViewById(R.id.hs_url);
        hs_info = root.findViewById(R.id.hs_info);
        hs_regit = root.findViewById(R.id.hs_regit);
        s_doctorname = root.findViewById(R.id.s_doctorname);
        doctor_name = SaveSharedPreference.getDoctorName(getActivity().getApplicationContext());
        doctorMiles = root.findViewById(R.id.doctorMiles);
        doctorRating = root.findViewById(R.id.doctorRating);
        adviceNum = root.findViewById(R.id.adviceNum);
        doctor_num = SaveSharedPreference.getdoctornum(getActivity().getApplicationContext());
        customAnimationDialog = new CustomAnimationDialog(root.getContext());
        medical_records = root.findViewById(R.id.medical_records);
        mile = root.findViewById(R.id.mile);
        drawal = root.findViewById(R.id.drawal);
        logout_button = root.findViewById(R.id.logout_button);
        list = root.findViewById(R.id.list);
        hs_regit.setVisibility(View.INVISIBLE);
        tvv.setVisibility(View.INVISIBLE);
        hospital_name.setVisibility(View.VISIBLE);
        hospital_name.setVisibility(View.INVISIBLE);
        address.setVisibility(View.INVISIBLE);
        hs_url.setVisibility(View.INVISIBLE);
        //입장 시 서버요청
        String url = u + "/doctorapp/mypage";
        /* Create request */
        Map<String, String> params = new HashMap<String, String>();
        params.put("doctor_id", doctor_num);
        JsonObjectRequest inForm = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        boolean success = false;
                        boolean hospital = false;
                        try {
                            success = response.getBoolean("result");
                            hospital = response.getBoolean("hospital");
                            if (success == true) {
                                if (hospital == true) {
                                    s_doctorname.setText(response.getString("doctor_name"));
                                    hospital_name.setVisibility(View.VISIBLE);
                                    address.setVisibility(View.VISIBLE);
                                    hs_url.setVisibility(View.VISIBLE);
                                    if(response.getString("hospital_name")=="null"){
                                        hospital_name.setText("병원이름을 등록해주세요");
                                    }else {
                                        hospital_name.setText(response.getString("hospital_name"));
                                    }
                                    if(response.getString("rating")=="null"){
                                        doctorRating.setText("0");
                                    }else {
                                        doctorRating.setText(response.getString("rating"));
                                    }
                                    if(response.getString("hospital_address")=="null"){
                                        address.setText("병원주소를 등록해주세요.");
                                    }else{
                                        address.setText(response.getString("hospital_address"));
                                    }
                                    if(response.getString("hospital_url")=="null"){
                                        hs_url.setText("병원홈페이지 주소를 등록해주세요.");
                                    }else{
                                        hs_url.setText(response.getString("hospital_url"));
                                    }
                                    if(response.getString("hospital_intro")=="null"){
                                        hs_info.setText("병원소개글을 등록해주세요.");
                                    }else{
                                        hs_info.setText(response.getString("hospital_intro"));
                                    }

                                    doctorMiles.setText(response.getString("point"));
                                    if(response.getString("chat_count").equals("")){
                                        adviceNum.setText("0");
                                    }else {
                                        adviceNum.setText(response.getString("chat_count"));
                                    }

                                } else {
                                    s_doctorname.setText(response.getString("doctor_name"));
                                    hospital_name.setVisibility(View.INVISIBLE);
                                    hs_regit.setVisibility(View.VISIBLE);
                                    tvv.setVisibility(View.VISIBLE);
                                    hs_modify.setVisibility(View.INVISIBLE);
                                    address.setVisibility(View.INVISIBLE);
                                    hs_url.setVisibility(View.INVISIBLE);
                                    hs_info.setVisibility(View.INVISIBLE);
                                    textView4.setVisibility(View.INVISIBLE);
                                    textView9.setVisibility(View.INVISIBLE);
                                    textView10.setVisibility(View.INVISIBLE);
                                    textView22.setVisibility(View.INVISIBLE);
                                    if(response.getString("rating")=="null"){
                                        doctorRating.setText("0");
                                    }else {
                                        doctorRating.setText(response.getString("rating"));
                                    }
                                    if(response.getString("chat_count").equals("")){
                                        adviceNum.setText("0");
                                    }else {
                                        adviceNum.setText(response.getString("chat_count"));
                                    }
                                    doctorMiles.setText(response.getString("point"));
                                }
                            } else {
                                Toast.makeText(getContext(), "false", Toast.LENGTH_LONG).show();
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
        AppController.getInstance(getContext()).addToRequestQueue(inForm);
//null처리
        if(doctorRating.getContext().toString().equals("")){
            doctorRating.setText("");
        }

//병원 정보.
        address.setClickable(false);
        address.setFocusable(false);
        address.setEnabled(false);
        hs_url.setClickable(false);
        hs_url.setFocusable(false);
        hs_url.setEnabled(false);
        hs_info.setClickable(false);
        hs_info.setFocusable(false);
        hs_info.setEnabled(false);
//병원 정보 등록 버튼
        hs_regit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent intent = new Intent(getContext(), hospital_modify.class);
                startActivity(intent);
            }
        });
        //병원정보수정
        hs_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent intent = new Intent(getContext(), hospital_modify2.class);
                startActivity(intent);
            }
        });

        //셋팅 버튼

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent intent = new Intent(view.getContext(), InfoModify.class);
                startActivity(intent); //로딩이 끝난 후, ChoiceFunction 이동
            }
        });
        //진료기록
        medical_records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent intent = new Intent(view.getContext(), Records_Activity.class);
                startActivity(intent); //로딩이 끝난 후, ChoiceFunction 이동
            }
        });
        //마일리지출금
        mile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent intent = new Intent(view.getContext(), Drawal.class);
                startActivity(intent); //로딩이 끝난 후, ChoiceFunction 이동
            }
        });
        //계좌설정
        drawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customAnimationDialog.show();
                String url = u + "/doctorapp/mypage";
                /* Create request */
                Map<String, String> params = new HashMap<String, String>();
                params.put("doctor_id", doctor_num);

                JsonObjectRequest updateForm = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                        url, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                customAnimationDialog.dismiss();
                                boolean success=false;
                                boolean account = false;
                                try {
                                    success = response.getBoolean("result");
                                    account = response.getBoolean("account");
                                    if (success==true) {
                                        if(account==true){
                                            Toast.makeText(getContext(), "계좌 수정", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(getContext(), Account_update.class);
                                            startActivity(intent);
                                        } else if(account==false) {
                                            Toast.makeText(getContext(), "등록된 계좌가 없습니다", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(getContext(), setAccount.class);
                                            startActivity(intent);
                                        }else{
                                        }
                                    }else{
                                        Toast.makeText(getContext(), "서버", Toast.LENGTH_LONG).show();
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
                AppController.getInstance(getContext()).addToRequestQueue(updateForm);

            }

        });
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent intent = new Intent(view.getContext(), Drawal_list.class);
                startActivity(intent); //로딩이 끝난 후, ChoiceFunction 이동
            }
        });

        //로그아웃


        //로그아웃
        logout_button.setOnClickListener(new View.OnClickListener() {
            //로그아웃 전 서버에 doctor_id 보내기
            @Override
            public void onClick(final View view) {
                customDialog = new CustomDialog(root.getContext(),"로그아웃 하시겠습니까?",positiveListener,negativeListener);
                customDialog.show();
            }
        });
        return root;

    }
    //로그아웃
    private View.OnClickListener positiveListener = new View.OnClickListener() {
        public void onClick(View v) {
            customAnimationDialog.show();
            String url = u + "/doctorapp/logout";
            /* Create request */
            Map<String, String> params = new HashMap<String, String>();
            params.put("doctor_id", doctor_num);
            JsonObjectRequest loginForm = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                    url, new JSONObject(params),
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            customAnimationDialog.dismiss();
                            boolean success = false;
                            try {
                                success = response.getBoolean("result");
                                if (success) {
                                    SaveSharedPreference.clearDoctorName(getContext());
                                    Intent intent = new Intent(getContext(), LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    //구글 로그어웃
                                    UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                                        @Override
                                        public void onCompleteLogout() {
                                            redirectLoginActivity();
                                        }
                                    }); //구글 로그아웃
                                    LoginManager.getInstance().logOut(); //페이스북 로그아웃

                                    SessionManager<TwitterSession> sessionManager = TwitterCore.getInstance().getSessionManager(); //트위터 로그아웃
                                    sessionManager.clearActiveSession();  //트위터 로그아웃
                                    getActivity().finish();

                                    // Activity stack에서 제거
                                } else {
                                    Toast.makeText(getContext(), "false", Toast.LENGTH_LONG).show();
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
            AppController.getInstance(getContext()).addToRequestQueue(loginForm);
            customDialog.dismiss();
        }
    };

    private View.OnClickListener negativeListener = new View.OnClickListener() {
        public void onClick(View v) {
            customDialog.dismiss();
        }
    };


    private void redirectLoginActivity() {
        final Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().finish();
    }

}