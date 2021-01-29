package com.ccit19.merdog_doctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.ccit19.merdog_doctor.databinding.ActivitySplashBinding;
import com.ccit19.merdog_doctor.variable.MyGlobals;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.KakaoSDK;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {
    //private SessionCallback callback;
    ActivitySplashBinding binding;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_splash);
        binding.setActivity(this);
        String u = MyGlobals.getInstance().getData();
        // KakaoSDK.init(new GlobalApplication.KakaoSDKAdapter());

        /*callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen();
        setContentView(R.layout.activity_splash);*/

        if(SaveSharedPreference.getDoctorName(SplashActivity.this).length() == 0) {
            // call Login Activity

            intent = new Intent(SplashActivity.this, LoginActivity.class);
        } else {
            //토큰체크
            String url = u + "/doctorapp/check_token";
            // Create request
            Map<String, String> params = new HashMap<String, String>();
            params.put("doctor_id", SaveSharedPreference.getdoctornum(getApplicationContext()));
            params.put("token", SaveSharedPreference.gettoken(getApplicationContext()));
            JsonObjectRequest tokencheck = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                    url, new JSONObject(params),
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            boolean success = false;
                            try {
                                success = response.getBoolean("result");
                                if (success) {
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
            });
            AppController.getInstance(getApplicationContext()).addToRequestQueue(tokencheck);
            // Call Next Activity
            intent = new Intent(SplashActivity.this, MainActivity.class);
           // intent.putExtra("STD_NUM", SaveSharedPreference.getDoctorName(this).toString());
        }
        Handler hd = new Handler();
        hd.postDelayed(new splashhandler(), 3000);

        }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private class splashhandler implements Runnable{
        public void run(){
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();//로딩이 끝난 후, ChoiceFunction 이동
        }
    }
    /*
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }
    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            redirectSignupActivity();
        }
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Logger.e(exception);
            }
        }
    }
    protected void redirectSignupActivity() {
        final Intent intent = new Intent(this, Regit_3Activity.class);
        startActivity(intent);
        finish();
    }*/
}

