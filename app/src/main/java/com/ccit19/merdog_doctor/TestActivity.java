package com.ccit19.merdog_doctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.ccit19.merdog_doctor.databinding.ActivityTestBinding;

public class TestActivity extends AppCompatActivity {
    ActivityTestBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_test);
        binding.setActivity(this);
        //setContentView(R.layout.activity_test);
        binding.accessToken.setText(getIntent().getStringExtra("id"));
        binding.refreshToken.setText(getIntent().getStringExtra("name"));
    }
}
