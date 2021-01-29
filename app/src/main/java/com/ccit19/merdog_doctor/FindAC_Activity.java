package com.ccit19.merdog_doctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.ccit19.merdog_doctor.databinding.ActivityFindAcBinding;

public class FindAC_Activity extends AppCompatActivity {
    ActivityFindAcBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_find_ac_);
        binding.setActivity(this);
        //setContentView(R.layout.activity_find_ac_);
    }
}
