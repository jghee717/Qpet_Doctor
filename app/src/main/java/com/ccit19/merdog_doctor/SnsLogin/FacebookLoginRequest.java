package com.ccit19.merdog_doctor.SnsLogin;

import android.widget.Toast;

import com.ccit19.merdog_doctor.LoginActivity;
import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.android.exoplayer2.util.Log;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FacebookLoginRequest implements FacebookCallback<LoginResult> {
    private static final String TAG = FacebookLoginRequest.class.getSimpleName();
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private LoginActivity activity;

    public FacebookLoginRequest(LoginActivity activity) {
        this.activity = activity;
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
       // Log.d(TAG, "facebook:onSuccess:" + loginResult);
        handleFacebookAccessToken(loginResult.getAccessToken());
    }

    @Override
    public void onCancel() {
       // Log.d(TAG, "facebook:onCancel:");
    }

    @Override
    public void onError(FacebookException error) {
        Log.d(TAG, "facebook:onError:" + error.getMessage());
    }
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken : " + token);


        AuthCredential authCredential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(activity, task -> {

                    if(task.isSuccessful()) {
                        assert task.getResult() != null;
                        String provider = task.getResult().getCredential().getProvider();
                        Log.d(TAG, "facebook - signInWithCredential:success");
                        mFirebaseUser = mFirebaseAuth.getCurrentUser();
                        activity.checkUser(mFirebaseUser, provider);
                    } else {
                        Log.w(TAG, "facebook - signInWithCredential:failure", task.getException());
                        Toast.makeText(activity, "facebook - Authentication Failed.", Toast.LENGTH_SHORT).show();
                        activity.checkUser(null, null);
                    }

                });
    }


}

