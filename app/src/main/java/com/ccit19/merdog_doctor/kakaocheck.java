package com.ccit19.merdog_doctor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.ccit19.merdog_doctor.databinding.ActivityRegit2Binding;
import com.ccit19.merdog_doctor.databinding.ActivityRegit2kakaoBinding;
import com.ccit19.merdog_doctor.variable.MyGlobals;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.helper.log.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class kakaocheck extends AppCompatActivity {
    ActivityRegit2kakaoBinding binding;
    static final String TAG = kakaocheck.class.getSimpleName();

    String doctor_num;
    String u = MyGlobals.getInstance().getData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestMe();
        doctor_num = SaveSharedPreference.getdoctornum(getApplicationContext());
    }
    private void onClickLogout() {
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                redirectLoginActivity();
            }
        });
    }

    private void redirectLoginActivity() {
        final Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish(); // Activity stack에서 제거
    }

    private void requestMe() {
        // https://developers.kakao.com/apps/361201/settings/user
        // 앱 생성된 이름 선택(테스트 앱) → 사용자 관리 → 접근권한관리항목 설정
        List<String> keys = new ArrayList<>();
        keys.add("properties.nickname");
        keys.add("properties.profile_image");
        keys.add("kakao_account.email");

        UserManagement.getInstance().me(keys, new MeV2ResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                redirectLoginActivity();
            }

            @Override
            public void onSuccess(final MeV2Response response) {
                Log.e(TAG, "user id : " + response.getId());
                Log.e(TAG, "nickname : " + response.getNickname());
                Log.e(TAG, "email: " + response.getKakaoAccount().getEmail());
                //Logger.d("profile image: " + response.getKakaoAccount().getProfileImagePath());
                //redirectMainActivity();
                //서버에 카카오이메일 확인
                final String getkakaoid = String.valueOf(response.getId());
                //Intent intent = new Intent(getApplication(), Regit_2kakao.class);
                //intent.putExtra("doctor_kakao",getkakaoid);
                //startActivity(intent);
                String url = u + "/doctorapp/login";
                // Create request
                Map<String, String> params = new HashMap<String, String>();
                String token = FirebaseInstanceId.getInstance().getToken();
                params.put("doctor_id", getkakaoid);
                params.put("type", "kakao");
                params.put("fcm_token", token);
                JsonObjectRequest update2Form = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                        url, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                boolean success = false;
                                try {
                                    success = response.getBoolean("result");
                                    String doctornum = response.getString("doctor_num");
                                    String doctoraddress = response.getString("doctor_address");
                                    if (success){
                                        Toast.makeText(getApplicationContext(), "로그인 되었습니다.", Toast.LENGTH_LONG).show();
                                        SaveSharedPreference.setDoctorInfo(getApplicationContext(), getkakaoid, false, doctornum, doctoraddress,doctoraddress, response.getString("token"));
                                        Intent intent = new Intent(getApplication(), MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish(); // Activity stack에서 제거
                                    } else {
                                        switch(response.getString("message")) {
                                            case "관리자의 승인을 기다리는 중 입니다.":
                                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                                                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                                                    @Override
                                                    public void onCompleteLogout() {
                                                        Intent intent2 = new Intent(getApplication(), LoginActivity.class);
                                                        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent2);
                                                        finish();
                                                    }
                                                });
                                            break;
                                            case "승인거부":
                                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                                                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                                                    @Override
                                                    public void onCompleteLogout() {
                                                        Intent intent = new Intent(getApplication(), Regit_3Activity.class);
                                                        intent.putExtra("doctor_num", doctornum);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(intent);
                                                    }
                                                });
                                                break;
                                            default:
                                                Toast.makeText(getApplicationContext(), "카카오로 회원가입", Toast.LENGTH_LONG).show();
                                                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                                                    @Override
                                                    public void onCompleteLogout() {
                                                Intent intent3 = new Intent(getApplication(), Regit_2kakao.class);
                                                intent3.putExtra("logintype","kakao");
                                                intent3.putExtra("doctor_id",getkakaoid);
                                                intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent3);
                                                finish();
                                                    }
                                                });
                                                break;
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
                            Toast.makeText(getApplicationContext(),"인터넷 연결을 확인해주세요.",Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(),"서버오류입니다.\n잠시후에 다시 시도해주세요.",Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(),"인터넷 연결을 확인해주세요.",Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                        }
                        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                            @Override
                            public void onCompleteLogout() {
                                Intent intent2 = new Intent(getApplication(), LoginActivity.class);
                                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent2);
                                finish();
                            }
                        });
                    }
                });

                AppController.getInstance(getApplicationContext()).addToRequestQueue(update2Form);
                //카카오로 회원가입시 버튼

            }
        });

    }

    private void onClickUnlink() {
        // 앱 연결 해제는 카카오 플랫폼에 연결된 사용자와 앱의 연결을 영구 해제함으로서 일반적으로 사용자가 앱 탈퇴 요청을 하는 경우와 비슷하다.
        // 앱 연결 해제가 수행된 사용자는 영구적으로 복구가 불가능하며 카카오 플랫폼 서비스를 더이상 사용할 수 없다.
        // 단, 다시 앱 연결을 통해 새로운 데이터로 카카오 플랫폼 서비스를 이용할 수는 있다.
        final String appendMessage = getString(R.string.com_kakao_confirm_unlink);
        new AlertDialog.Builder(this)
                .setMessage(appendMessage)
                .setPositiveButton(getString(R.string.com_kakao_ok_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
                                    @Override
                                    public void onFailure(ErrorResult errorResult) {
                                        Logger.e(errorResult.toString());
                                    }

                                    @Override
                                    public void onSessionClosed(ErrorResult errorResult) {
                                        redirectLoginActivity();
                                    }

                                    @Override
                                    public void onNotSignedUp() {
                                        LoginActivity loginActivity = new LoginActivity();
                                        loginActivity.redirectSignupActivity();
                                    }

                                    @Override
                                    public void onSuccess(Long userId) {
                                        redirectLoginActivity();
                                    }
                                });
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(getString(R.string.com_kakao_cancel_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

    }

}