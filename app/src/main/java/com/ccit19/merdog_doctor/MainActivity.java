package com.ccit19.merdog_doctor;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.ccit19.merdog_doctor.chat.ChatAcceptActivity;
import com.ccit19.merdog_doctor.chat.ChatRoomActivity;
import com.ccit19.merdog_doctor.databinding.ActivityMainBinding;
import com.ccit19.merdog_doctor.custom_dialog.BackPressCloseHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import java.util.ArrayList;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private TextView doctorRating;
    private BackPressCloseHandler backPressCloseHandler;
    final String TAG = getClass().getSimpleName();
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doctorRating=findViewById(R.id.doctorRating);
        backPressCloseHandler = new BackPressCloseHandler(MainActivity.this);
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null){
            Intent intent;
            switch(bundle.getString("android_channel_id")) {
                case "1": // 상담 요청
                    intent=new Intent(this, ChatAcceptActivity.class);
                    intent.putExtra("chat_request_id",bundle.getString("chat_request_id"));
                    startActivity(intent);
                    break;
                case "2": //체팅방 메시지
                    intent = new Intent(this, ChatRoomActivity.class);
                    intent.putExtra("chat_request_id",bundle.getString("chat_request_id"));
                    intent.putExtra("chat_room",bundle.getString("chat_room"));
                    intent.putExtra("chat_state", "ing");
                    startActivity(intent);
                    break;
                case "3":
                    break;
                case "4":
                    break;
                default:
                    break;
            }

        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setActivity(this);

        //setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);



        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }

        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("서비스를 이용하기 위해서는 권한 설정이 필요합니다.")
                .setDeniedMessage("왜 거부하셨어요...\n하지만 [설정] > [권한] 에서 권한을 허용할 수 있어요.")
                .setPermissions(Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }

//    @Override public void onBackPressed() {
//        //super.onBackPressed();
//        backPressCloseHandler.onBackPressed();
//    }

}

