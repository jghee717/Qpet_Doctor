package com.ccit19.merdog_doctor.custom_dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.ccit19.merdog_doctor.R;

public class ChatAcceptDialog extends ProgressDialog {

    private Context c;
    private TextView set_pet_name, set_address, set_pet_age, set_pet_gender, set_pet_birth, set_pet_main_type, set_pet_sub_type, set_pet_notice, okButton;
    private String Spet_name,Saddress, Spet_age, Spet_gender, Spet_birth, Spet_main_type, Spet_sub_type, Spet_notice;

    public ChatAcceptDialog(Context context,String pet_neme, String address, String pet_age, String pet_gender,
                            String pet_birth, String pet_main_type, String pet_sub_type, String pet_notice) {
        super(context);
//        this.address = set_address;
//        this.pet_age = set_pet_age;
//        this.pet_gender = set_pet_gender;
//        this.pet_birth = set_pet_birth;
//        this.pet_main_type = set_pet_main_type;
//        this.pet_sub_type = set_pet_sub_type;
//        this.pet_notice = set_pet_notice;


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);
        Spet_name = pet_neme;
        Saddress = address;
        Spet_age = pet_age;
        Spet_gender = pet_gender;
        Spet_birth = pet_birth;
        Spet_main_type = pet_main_type;
        Spet_sub_type = pet_sub_type;
        Spet_notice = pet_notice;
        c = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_accept_dialog);
        set_pet_name = findViewById(R.id.set_pet_name);
        set_address = findViewById(R.id.set_address);
        set_pet_age = findViewById(R.id.set_pet_age);
        set_pet_gender = findViewById(R.id.set_pet_gender);
        set_pet_birth = findViewById(R.id.set_pet_birth);
        set_pet_main_type = findViewById(R.id.set_pet_main_type);
        set_pet_sub_type = findViewById(R.id.set_pet_sub_type);
        set_pet_notice = findViewById(R.id.set_pet_notice);
        okButton = findViewById(R.id.okButton);

        set_pet_name.setText(Spet_name);
        set_address.setText(Saddress);
        set_pet_age.setText(Spet_age);
        set_pet_gender.setText(Spet_gender);
        set_pet_birth.setText(Spet_birth);
        set_pet_main_type.setText(Spet_main_type);
        set_pet_sub_type.setText(Spet_sub_type);
        set_pet_notice.setText(Spet_notice);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    @Override
    public void show() {
        super.show();
    }
    @Override
    public void dismiss() {
        super.dismiss();
    }


}
