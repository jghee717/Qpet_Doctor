package com.ccit19.merdog_doctor;


import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
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
import com.ccit19.merdog_doctor.ui.home.HomeFragment;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;


public class LocationState extends AppCompatActivity {
    private WebView daum_webView;
    private String location = null, address;
    private Double latitude, longitude;
    private TextView daum_result;
    private Button locationbutton;
    private Handler handler;
    final Geocoder geocoder = new Geocoder(this);

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_state);

        daum_result = (TextView) findViewById(R.id.daum_result);


        // WebView 초기화

        init_webView();


        // 핸들러를 통한 JavaScript 이벤트 반응

        handler = new Handler();


    }


    public void init_webView() {

        // WebView 설정

        daum_webView = (WebView) findViewById(R.id.daum_webview);


        // JavaScript 허용

        daum_webView.getSettings().setJavaScriptEnabled(true);


        // JavaScript의 window.open 허용

        daum_webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);


        // JavaScript이벤트에 대응할 함수를 정의 한 클래스를 붙여줌

        daum_webView.addJavascriptInterface(new AndroidBridge(), "TestApp");


        // web client 를 chrome 으로 설정

        daum_webView.setWebChromeClient(new WebChromeClient());


        // webview url load. php 파일 주소

        daum_webView.loadUrl("http://ccitb.dothome.co.kr/usb/location.php");

    }


    private class AndroidBridge {

        @JavascriptInterface

        public void setAddress(final String arg1, final String arg2, final String arg3) {

            handler.post(new Runnable() {

                @Override

                public void run() {

                    daum_result.setText(String.format("%s %s %s", arg1, arg2, arg3));
                    location = String.format("%s %s %s", arg1, arg2, arg3);
                    if (daum_result.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "주소를 선택해 주세요.", Toast.LENGTH_LONG).show();
                    } else if (!daum_result.getText().toString().isEmpty()) {
                        List<Address> list = null;

                        try {
                            list = geocoder.getFromLocationName(
                                    location, // 지역 이름
                                    10); // 읽을 개수
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
                        }

                        if (list != null) {
                            if (list.size() == 0) {
                                daum_result.setText("해당되는 주소 정보는 없습니다");
                            } else {
                                latitude = list.get(0).getLatitude();        // 위도
                                longitude = list.get(0).getLongitude();    // 경도
                            }
                        }
                        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                        intent.putExtra("state",  getIntent().getStringExtra("state"));
                        intent.putExtra("longitude", longitude);
                        intent.putExtra("latitude", latitude);
                        intent.putExtra("location", location);
                        startActivity(intent);
                    }
                    // WebView를 초기화 하지않으면 재사용할 수 없음

                    init_webView();

                }
            });
        }
    }
}

