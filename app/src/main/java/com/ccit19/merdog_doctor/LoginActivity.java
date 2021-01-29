package com.ccit19.merdog_doctor;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ccit19.merdog_doctor.SnsLogin.FacebookLoginRequest;
import com.ccit19.merdog_doctor.custom_dialog.BackPressCloseHandler;
import com.ccit19.merdog_doctor.custom_dialog.CustomAnimationDialog;
import com.ccit19.merdog_doctor.variable.MyGlobals;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import androidx.databinding.DataBindingUtil;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ccit19.merdog_doctor.databinding.ActivityLoginBinding;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    static final String TAG = LoginActivity.class.getSimpleName();
    private SessionCallback callback;
    ActivityLoginBinding binding;
    Context mContext;
    private TextView findac_btn;
    private String doctor_num;
    Boolean Kakaolog = true;
    private LoginButton com_kakao_login;
    private CustomAnimationDialog customAnimationDialog; //로딩
    private long mLastClickTime = 0;
    private BackPressCloseHandler backPressCloseHandler; // 두번둘러 종료
    String u = MyGlobals.getInstance().getData();
    //네이버
    public static OAuthLogin mOAuthLoginModule;
    private static OAuthLogin mOAuthLoginInstance;
    private static String OAUTH_CLIENT_ID = "SpicyHJ_IDqVyHad5Bma";
    private static String OAUTH_CLIENT_SECRET = "jfZOVxwSar";
    private static String OAUTH_CLIENT_NAME = "jghee717";
    //구글 로그인
    private static final int RC_SIGN_IN = 9001;    // 구글로그인 result 상수
    private GoogleSignInClient googleSignInClient;    // 구글api클라이언트
    private FirebaseAuth firebaseAuth;    // 파이어베이스 인증 객체 생성
    private SignInButton buttonGoogle;    // 구글  로그인 버튼
    //페이스북 로그인
    CallbackManager mCallbackManager;
    public static final int FACEBOOK_LOGIN_IN = 64206;
    private com.facebook.login.widget.LoginButton facebook_login_button;
    //트위터 로그인
    private TwitterLoginButton twitter_login_button;
    // 둥근 sns 아이콘
    private ImageView naver_circle_icon, kakao_circle_icon, facebook_circle_icon, google_circle_icon, twitter_circle_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //트위터 로그인 setContentView 위에 있어야함
        //This code must be entering before the setContentView to make the twitter login work...
        TwitterAuthConfig mTwitterAuthConfig = new TwitterAuthConfig(getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));
        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .twitterAuthConfig(mTwitterAuthConfig)
                .build();
        Twitter.initialize(twitterConfig); // 트위터 로그인 끝
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setActivity(this);
        com_kakao_login = findViewById(R.id.com_kakao_login);
        findac_btn = findViewById(R.id.findac_btn);
        mContext = LoginActivity.this;
        //KakaoSDK.init(new GlobalApplication.KakaoSDKAdapter());
        doctor_num = SaveSharedPreference.getdoctornum(getApplicationContext().getApplicationContext());
        callback = new SessionCallback();
        customAnimationDialog = new CustomAnimationDialog(LoginActivity.this); // 로딩

        // 파이어베이스 인증 객체 선언
        firebaseAuth = FirebaseAuth.getInstance();
        buttonGoogle = findViewById(R.id.btn_googleSignIn);
        backPressCloseHandler = new BackPressCloseHandler(LoginActivity.this);
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen();

        //페이스북
        mCallbackManager = CallbackManager.Factory.create();
        facebook_login_button = findViewById(R.id.facebook_login_button);

        // 트위터
        twitter_login_button = (TwitterLoginButton) findViewById(R.id.twitter_login_button);

        // 둥근 sns 아이콘
        naver_circle_icon = (ImageView) findViewById(R.id.naver_circle_icon);
        naver_circle_icon.setOnClickListener(Listener);

        kakao_circle_icon = (ImageView) findViewById(R.id.kakao_circle_icon);
        kakao_circle_icon.setOnClickListener(Listener);

        facebook_circle_icon = (ImageView) findViewById(R.id.facebook_circle_icon);
        facebook_circle_icon.setOnClickListener(Listener);

        google_circle_icon = (ImageView) findViewById(R.id.google_circle_icon);
        google_circle_icon.setOnClickListener(Listener);

        twitter_circle_icon = (ImageView) findViewById(R.id.twitter_circle_icon);
        twitter_circle_icon.setOnClickListener(Listener);

        //네이버
        initData();
        mOAuthLoginModule = OAuthLogin.getInstance();
        mOAuthLoginModule.init(
                LoginActivity.this
                , "SpicyHJ_IDqVyHad5Bma"
                , "jfZOVxwSar"
                , "jghee717"
        );
        binding.naverLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOAuthLoginModule.startOauthLoginActivity(LoginActivity.this, mOAuthLoginHandler);
            }
        });
        //로그인
        binding.eLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                customAnimationDialog.show();
                String url = u + "/doctorapp/login";
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        customAnimationDialog.dismiss();
                        boolean success = false;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getBoolean("result");
                            String doctornum = jsonObject.getString("doctor_num");
                            String doctoraddress = jsonObject.getString("doctor_address");
                            if (success) {
                                Toast.makeText(getApplicationContext(), "로그인 되었습니다.", Toast.LENGTH_LONG).show();
                                SaveSharedPreference.setDoctorInfo(getApplicationContext(), binding.doctorid.getText().toString(), false, doctornum, doctoraddress, "", jsonObject.getString("token"));
                                Intent intent = new Intent(getApplication(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish(); // Activity stack에서 제거
                            } else {
                                if (jsonObject.getString("message").equals("승인거부")) {
                                    Intent intent = new Intent(getApplication(), Regit_3Activity.class);
                                    intent.putExtra("doctor_num", jsonObject.getString("doctor_num"));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        customAnimationDialog.dismiss();
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
                }){
                    @Override
                    public  Map<String, String> getParams() throws AuthFailureError
                    {
                        Map<String, String> params = new HashMap<String, String>();
                        String token = FirebaseInstanceId.getInstance().getToken();
                        params.put("doctor_id", binding.doctorid.getText().toString());
                        params.put("doctor_pw", binding.doctorPw.getText().toString());
                        params.put("fcm_token", token);
                        return params;
                    }
                };
                queue.add(request);
            }
        });

        binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startProgress();
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent intent = new Intent(view.getContext(), Regit_1Activity.class);
                startActivity(intent);//로딩이 끝난 후, ChoiceFunction 이동
            }
        });
        findac_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startProgress();
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent intent = new Intent(view.getContext(), findaccount.class);
                startActivity(intent); //로딩이 끝난 후, ChoiceFunction 이동
            }
        });
        // 구글 로그인
        // Google 로그인을 앱에 통합
        // GoogleSignInOptions 개체를 구성할 때 requestIdToken을 호출
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(LoginActivity.this, googleSignInOptions);

        buttonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        //페이스북 로그인 버튼
        //facebook_login_button.setPermissions(getString(R.string.facebook_app_id),getString(R.string.facebook_login_value));
        facebook_login_button.registerCallback(mCallbackManager, new FacebookLoginRequest(this));

        //트위터 로그인 버튼

        twitter_login_button.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                signInToFirebaseWithTwitterSession(result.data);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }

            @Override
            public void failure(TwitterException exception) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }); // 트위터 로그인 버튼 끝
    }
    // 둥근 sns 아이콘 클릭
    View.OnClickListener Listener = new View.OnClickListener(){
        @Override
        public void onClick(View view)
        {
            switch (view.getId()) {
                case R.id.naver_circle_icon:
                    binding.naverLogin.performClick();
                    break;
                case R.id.kakao_circle_icon:
                    com_kakao_login.performClick();
                    break;
                case R.id.facebook_circle_icon:
                    facebook_login_button.performClick();
                    break;
                case R.id.google_circle_icon:
                    Intent signInIntent = googleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                    break;
                case R.id.twitter_circle_icon:
                    twitter_login_button.performClick();
                    break;
            }
        }
    };

    private void initData() {
        mOAuthLoginInstance = OAuthLogin.getInstance();

        mOAuthLoginInstance.showDevelopersLog(true);
        mOAuthLoginInstance.init(mContext, OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET, OAUTH_CLIENT_NAME);
        /*
         * 2015년 8월 이전에 등록하고 앱 정보 갱신을 안한 경우 기존에 설정해준 callback intent url 을 넣어줘야 로그인하는데 문제가 안생긴다.
         * 2015년 8월 이후에 등록했거나 그 뒤에 앱 정보 갱신을 하면서 package name 을 넣어준 경우 callback intent url 을 생략해도 된다.
         */
        //mOAuthLoginInstance.init(mContext, OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET, OAUTH_CLIENT_NAME, OAUTH_callback_intent_url);
    }

    private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {
                String accessToken = mOAuthLoginInstance.getAccessToken(mContext);
                String refreshToken = mOAuthLoginInstance.getRefreshToken(mContext);
                long expiresAt = mOAuthLoginInstance.getExpiresAt(mContext);
                String tokenType = mOAuthLoginInstance.getTokenType(mContext);
                new RequestApiTask().execute();
            } else {
                String errorCode = mOAuthLoginInstance.getLastErrorCode(mContext).getCode();
                String errorDesc = mOAuthLoginInstance.getLastErrorDesc(mContext);
                Toast.makeText(mContext, "errorCode:" + errorCode + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
            }
        }

    };

    private class RequestApiTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... params) {
            String url = "https://openapi.naver.com/v1/nid/me";
            String at = mOAuthLoginInstance.getAccessToken(mContext);
            return mOAuthLoginInstance.requestApi(mContext, at, url);
        }

        protected void onPostExecute(String content) {
            //id_naver=content["name"];
            JsonParser parser = new JsonParser();
            JsonElement dataObject = parser.parse(content)
                    .getAsJsonObject().get("response");
            JsonElement idObject = parser.parse(String.valueOf(dataObject))
                    .getAsJsonObject().get("id");
            String na_id = idObject.toString().replaceAll("\"", "");
            //new RequestNaverId().execute();
            String token = FirebaseInstanceId.getInstance().getToken();
            String url = u + "/doctorapp/login";// 자체 서버 통신
            // Create request
            Map<String, String> params = new HashMap<String, String>();
            params.put("type", "naver");
            params.put("doctor_id", na_id);
            params.put("fcm_token", FirebaseInstanceId.getInstance().getToken());
            JsonObjectRequest loginForm = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                    url, new JSONObject(params),
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            boolean success = false;
                            try {
                                success = response.getBoolean("result");
                                String doctornum = response.getString("doctor_num");
                                String doctoraddress = response.getString("doctor_address");
                                if (success) {
                                    Toast.makeText(getApplicationContext(), "로그인 되었습니다.", Toast.LENGTH_LONG).show();
                                    SaveSharedPreference.setDoctorInfo(getApplicationContext(), na_id, false, doctornum, doctoraddress, doctoraddress, response.getString("token"));
                                    Intent intent = new Intent(getApplication(), MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish(); // Activity stack에서 제거
                                } else {
                                    switch (response.getString("message")) {
                                        case "관리자의 승인을 기다리는 중 입니다.":
                                            Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                                            LoginManager.getInstance().logOut(); //페이스북 로그아웃
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
                                            Toast.makeText(getApplicationContext(),"네이버로 회원가입.",Toast.LENGTH_LONG).show();
                                            UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                                                @Override
                                                public void onCompleteLogout() {
                                                    Intent intent=new Intent(getApplicationContext(), Regit_2kakao.class);
                                                    intent.putExtra("doctor_id",na_id);
                                                    intent.putExtra("logintype","naver");
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intent);
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
            loginForm.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance(getApplicationContext()).addToRequestQueue(loginForm);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
        // 구글로그인 버튼 응답
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
        //페이스북 로그인 버튼 응답
        if (requestCode == FACEBOOK_LOGIN_IN) {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        } else {
        }
        //트위터 로그인 버튼 응답
        twitter_login_button.onActivityResult(requestCode, resultCode, data);
    }

    //구글 로그인
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        String provider = "google";
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 로그인 성공
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user, provider);
                        } else {
                            updateUI(null, null);
                            // 로그인 실패
                        }
                    }
                });
    }

    private void signInToFirebaseWithTwitterSession(TwitterSession session){
        AuthCredential credential = TwitterAuthProvider.getCredential(session.getAuthToken().token,
                session.getAuthToken().secret);
        String provider = "twitter";
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 로그인 성공
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user, provider);
                        } else {
                            updateUI(null, null);
                            // 로그인 실패
                        }
                    }
                });
    }

    // 구글 / 트위터 로그인 서버 올리기
    // 구글 로그인 서버 올리기
    private void updateUI(FirebaseUser user, String provider) { //update ui code here
        if (user != null) {
            String url = u + "/doctorapp/login";
            // Create request
            String googleuid = user.getUid();
            Map<String, String> params = new HashMap<String, String>();
            String token = FirebaseInstanceId.getInstance().getToken();
            params.put("doctor_id", googleuid);
            params.put("type", provider);
            params.put("fcm_token", token);
            JsonObjectRequest update2Form = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                    url, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            signOut();
                            boolean success = false;
                            try {
                                success = response.getBoolean("result");
                                String doctornum = response.getString("doctor_num");
                                String doctoraddress = response.getString("doctor_address");
                                if (success) {
                                    Toast.makeText(getApplicationContext(), "로그인 되었습니다.", Toast.LENGTH_LONG).show();
                                    SaveSharedPreference.setDoctorInfo(getApplicationContext(), googleuid, false, doctornum, doctoraddress, doctoraddress, response.getString("token"));
                                    Intent intent = new Intent(getApplication(), MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish(); // Activity stack에서 제거
                                } else {
                                    switch (response.getString("message")) {
                                        case "관리자의 승인을 기다리는 중 입니다.":
                                            Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                                            LoginManager.getInstance().logOut(); //페이스북 로그아웃
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
                                                    LoginManager.getInstance().logOut(); //페이스북 로그아웃
                                                }
                                            });
                                            break;
                                        default:
                                            Toast.makeText(getApplicationContext(), provider + "로 회원가입", Toast.LENGTH_LONG).show();
                                            UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                                                @Override
                                                public void onCompleteLogout() {
                                                    Intent intent3 = new Intent(getApplication(), Regit_2kakao.class);
                                                    intent3.putExtra("logintype", provider);
                                                    intent3.putExtra("doctor_id", googleuid);
                                                    intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intent3);
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
                    signOut();
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
            AppController.getInstance(getApplicationContext()).addToRequestQueue(update2Form);
        }
    }


    // 구글 로그아웃
    private void signOut() {
        firebaseAuth.signOut();
        googleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }

    //구글 로그인 끝
    //페이스북 로그인 시작
    public void checkUser(FirebaseUser user, String provider) {
        if (user != null) {
            String googleuid = user.getUid();
            String url = u + "doctorapp/login";
            // Create request
            Map<String, String> params = new HashMap<String, String>();
            String token = FirebaseInstanceId.getInstance().getToken();
            params.put("doctor_id", googleuid);
            params.put("type", "facebook");
            params.put("fcm_token", token);
            JsonObjectRequest update2Form = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                    url, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            signOut();
                            boolean success = false;
                            try {
                                success = response.getBoolean("result");
                                String doctornum = response.getString("doctor_num");
                                String doctoraddress = response.getString("doctor_address");
                                if (success) {
                                    Toast.makeText(getApplicationContext(), "로그인 되었습니다.", Toast.LENGTH_LONG).show();
                                    SaveSharedPreference.setDoctorInfo(getApplicationContext(), googleuid, false, doctornum, doctoraddress, doctoraddress, response.getString("token"));
                                    Intent intent = new Intent(getApplication(), MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish(); // Activity stack에서 제거
                                } else {
                                    switch (response.getString("message")) {
                                        case "관리자의 승인을 기다리는 중 입니다.":
                                            Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                                            LoginManager.getInstance().logOut(); //페이스북 로그아웃
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
                                                    LoginManager.getInstance().logOut(); //페이스북 로그아웃
                                                }
                                            });
                                            break;
                                        default:
                                            Toast.makeText(getApplicationContext(), "페이스북으로 회원가입", Toast.LENGTH_LONG).show();
                                            UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                                                @Override
                                                public void onCompleteLogout() {
                                                    Intent intent3 = new Intent(getApplication(), Regit_2kakao.class);
                                                    intent3.putExtra("logintype", "facebook");
                                                    intent3.putExtra("doctor_id", googleuid);
                                                    intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intent3);
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
                    signOut();
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
            AppController.getInstance(getApplicationContext()).addToRequestQueue(update2Form);
        }
    }

    //페이스북 로그인 시작
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    // 카카오 세션 나오기
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

    //카카오 로그인
    protected void redirectSignupActivity() {
        final Intent intent = new Intent(this, kakaocheck.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }
}
