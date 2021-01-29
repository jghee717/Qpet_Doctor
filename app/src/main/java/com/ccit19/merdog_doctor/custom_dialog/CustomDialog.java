package com.ccit19.merdog_doctor.custom_dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.ccit19.merdog_doctor.R;

import androidx.annotation.NonNull;

public class CustomDialog extends Dialog {
    private Context c;
    private String setdialogText;
    private Button mPositiveButton;
    private Button mNegativeButton;
    private TextView dialogText;
    private View.OnClickListener mPositiveListener;
    private View.OnClickListener mNegativeListener;


    //생성자 생성
    public CustomDialog(@NonNull Context context, String text, View.OnClickListener positiveListener, View.OnClickListener negativeListener) {
        super(context);
        this.setdialogText = text;
        this.mPositiveListener = positiveListener;
        this.mNegativeListener = negativeListener;
        c=context;

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //다이얼로그 밖의 화면은 흐리게 만들어줌
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);
        setContentView(R.layout.logout_dialog);

        //셋팅
        mPositiveButton=(Button)findViewById(R.id.pbutton);
        mNegativeButton=(Button)findViewById(R.id.nbutton);
        dialogText = (TextView)findViewById(R.id.dialogText);
        //클릭 리스너 셋팅 (클릭버튼이 동작하도록 만들어줌.)
        dialogText.setText(setdialogText);
        mPositiveButton.setOnClickListener(mPositiveListener);
        mNegativeButton.setOnClickListener(mNegativeListener);
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


