package com.ccit19.merdog_doctor.chat;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ccit19.merdog_doctor.R;
import com.ccit19.merdog_doctor.custom_dialog.CustomAnimationDialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ImageActivity extends AppCompatActivity {
    private CustomAnimationDialog customAnimationDialog;
    private ImageView chat_image_view;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        customAnimationDialog = new CustomAnimationDialog(ImageActivity.this);


        chat_image_view = (ImageView)findViewById(R.id.chat_image_view);
        Glide.with(getApplicationContext()).load(getIntent().getStringExtra("Uri")).into(chat_image_view);

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customAnimationDialog.dismiss();
    }
}
