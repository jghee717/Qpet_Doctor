package com.ccit19.merdog_doctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import gun0912.tedbottompicker.TedBottomPicker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.ccit19.merdog_doctor.custom_dialog.CustomAnimationDialog;
import com.ccit19.merdog_doctor.variable.MyGlobals;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Regit_3Activity extends AppCompatActivity {
    private CustomAnimationDialog customAnimationDialog;
    final String TAG = getClass().getSimpleName();
    private ImageView doctor, license;
    private TextView textView5, textView6;
    //private Button btn_address;
    private Button btn_register2;
    private Button btn_back;
    private Bitmap bitmap1, bitmap2;
    private Uri filePath1, filePath2;
    private RequestManager requestManager;
    private boolean img_check = false;
    private boolean img_check2 = false;
    String u = MyGlobals.getInstance().getData();



    //갤러리에서 이미지 선택
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regit_3);

        doctor = findViewById(R.id.doctor);
        license = findViewById(R.id.license);
        btn_back = findViewById(R.id.btn_back);
        btn_register2 = findViewById(R.id.btn_register2);
        textView5 = findViewById(R.id.textView5);
        textView6 = findViewById(R.id.textView6);
        customAnimationDialog = new CustomAnimationDialog(Regit_3Activity.this);
        requestManager = Glide.with(this);

        // 6.0 마쉬멜로우 이상일 경우에는 권한 체크 후 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "권한 설정 완료");
            } else {
                Log.d(TAG, "권한 설정 요청");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        // 위쪽 이미지뷰 클릭시 다이알 로그
        doctor.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TedBottomPicker.with(Regit_3Activity.this)
                        //.setPeekHeight(getResources().getDisplayMetrics().heightPixels/2)
                        .setSelectedUri(filePath1)
                        .setPeekHeight(1200)
                        .show(uri -> {
                            Log.d("ted", "uri: " + uri);
                            Log.d("ted", "uri.getPath(): " + uri.getPath());
                            filePath1 = uri;
                            requestManager
                                    .load(uri)
                                    .into(doctor);
                            bitmap1 = resize(getApplicationContext(),filePath1,500);
                            img_check = true;
                        });
            }
        });
        // 아래 이미지 선택시 다이알로그 뛰우기
        license.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TedBottomPicker.with(Regit_3Activity.this)
                        //.setPeekHeight(getResources().getDisplayMetrics().heightPixels/2)
                        .setSelectedUri(filePath2)
                        .setPeekHeight(1200)
                        .show(uri -> {
                            Log.d("ted", "uri: " + uri);
                            Log.d("ted", "uri.getPath(): " + uri.getPath());
                            filePath2 = uri;
                            requestManager
                                    .load(uri)
                                    .into(license);
                            bitmap2 = resize(getApplicationContext(),filePath2,500);
                            img_check2 = true;
                        });
            }
        });

        // 회원가입 버튼 클릭시 서버와 통신
        btn_register2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customAnimationDialog.show();
                if (img_check && img_check2) {
                    String url;
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
                    String doctor_license_name = timeStamp + getFileName(filePath1) + "license_name";
                    String user_license_name = timeStamp + getFileName(filePath2) + "user_license";
                    String doctor_license = getStringImage(bitmap1);
                    String user_license = getStringImage(bitmap2);
                    String doctor_id = getIntent().getStringExtra("doctor_id");
                    String doctor_pw = getIntent().getStringExtra("doctor_pw");
                    String doctor_name = getIntent().getStringExtra("doctor_name");
                    String doctor_phone = getIntent().getStringExtra("doctor_phone");

                    Map<String, String> params = new HashMap<String, String>();

                    if(getIntent().getStringExtra("doctor_num") != null) {
                        url = u + "/doctorapp/re_register";
                        params.put("doctor_license_name", doctor_license_name);
                        params.put("user_license_name", user_license_name);
                        params.put("doctor_license", doctor_license);
                        params.put("user_license", user_license);
                        params.put("doctor_id", getIntent().getStringExtra("doctor_num"));
                    } else{
                        /* Create request */
                        url = u + "/doctorapp/register";
                        params.put("doctor_license_name", doctor_license_name);
                        params.put("user_license_name", user_license_name);
                        params.put("doctor_license", doctor_license);
                        params.put("user_license", user_license);
                        params.put("doctor_id", doctor_id);
                        params.put("doctor_pw", doctor_pw);
                        params.put("doctor_name", doctor_name);
                        params.put("doctor_phone", doctor_phone);
                        if(getIntent().getStringExtra("logintype") != null){
                            String logintype = getIntent().getStringExtra("logintype");
                            params.put("type",logintype);
                        }
                    }
                    JsonObjectRequest regitForm = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                            url, new JSONObject(params),
                            new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    customAnimationDialog.dismiss();
                                    boolean success = false;
                                    try {
                                        success = response.getBoolean("result");
                                        if (success) {
                                            FirebaseAuth.getInstance().signOut();
                                            LoginManager.getInstance().logOut();
                                            Toast.makeText(getApplicationContext(), "회원가입 되었습니다", Toast.LENGTH_LONG).show();
                                                    final Intent intent = new Intent(Regit_3Activity.this, LoginActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intent);
                                                    finish(); // Activity stack에서 제거
                                        } else {
                                            Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
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
                    AppController.getInstance(getApplicationContext()).addToRequestQueue(regitForm);
                } else {
                    Toast.makeText(getApplicationContext(), "사진을 두장 다 선택해주세요", Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customAnimationDialog.dismiss();
                onBackPressed();
            }
        });
    }

    // 이미지 이름 가지고 오기
    String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    // 이미지 인코딩해 문자열로 만들기
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    // 권한
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }

    //이미지 리사이즈
    private Bitmap resize(Context context, Uri uri, int resize){
        Bitmap resizeBitmap=null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); // 1번

            int width = options.outWidth;
            int height = options.outHeight;
            int samplesize = 1;

            while (true) {//2번
                if (width / 2 < resize || height / 2 < resize)
                    break;
                width /= 2;
                height /= 2;
                samplesize *= 2;
            }

            options.inSampleSize = samplesize;
            Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); //3번
            resizeBitmap=bitmap;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return resizeBitmap;
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