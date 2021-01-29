package com.ccit19.merdog_doctor;

import android.Manifest;

import android.app.AlertDialog;

import android.content.DialogInterface;

import android.content.Intent;

import android.content.pm.PackageManager;

import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;

import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.core.app.ActivityCompat;

import androidx.core.content.ContextCompat;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;

import android.view.View;
import android.widget.Button;
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
import com.ccit19.merdog_doctor.custom_dialog.CustomAnimationDialog;
import com.ccit19.merdog_doctor.variable.MyGlobals;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener, MapView.MapViewEventListener {

    private static final String LOG_TAG = "MapActivity";
    private CustomAnimationDialog customAnimationDialog;
    private MapView mMapView;
    private Button gps, AddressButton;
    private TextView addressTextView, addressOk;
    private MapReverseGeoCoder mReverseGeoCoder = null;
    private MapPoint MAP_POINT;
    private String address, latitude, longitude, doctor_num;
    private int i = 0, j = 0;
    private boolean addeessstate = true;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private GpsTracker gpsTracker;
    String u = MyGlobals.getInstance().getData();
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doctor_num = SaveSharedPreference.getdoctornum(getApplicationContext());
        setContentView(R.layout.activity_map);
        gps = (Button) findViewById(R.id.gps);
        mMapView = (MapView) findViewById(R.id.map_view);
        addressTextView = (TextView) findViewById(R.id.addressTextView);
        addressOk = (TextView) findViewById(R.id.addressOk);
        // AddressButton = (Button) findViewById(R.id.AddressButton);
        //mMapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
        mMapView.setMapViewEventListener(this);
        address = getIntent().getExtras().getString("location");
        customAnimationDialog = new CustomAnimationDialog(MapActivity.this);
        MAP_POINT = MapPoint.mapPointWithGeoCoord(getIntent().getExtras().getDouble("latitude"), getIntent().getExtras().getDouble("longitude"));

        //현 위치 설정 버튼
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mReverseGeoCoder = new MapReverseGeoCoder("39a57489c72bc2d5000877424a73cad2", mMapView.getMapCenterPoint(), MapActivity.this, MapActivity.this);
//                mReverseGeoCoder.startFindingAddress();// 좌표를 주소로 받는다
                gpsTracker = new GpsTracker(MapActivity.this);
                latitude = Double.toString(gpsTracker.getLatitude()).substring(0, 9);
                longitude = Double.toString(gpsTracker.getLongitude()).substring(0, 10);
                address = getCurrentAddress(gpsTracker.getLatitude(), gpsTracker.getLongitude());
                MAP_POINT = MapPoint.mapPointWithGeoCoord(gpsTracker.getLatitude(), gpsTracker.getLongitude());
                addressTextView.setText(address.replace("대한민국", ""));
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
                if (i > 0 && j == 0) {
                    removePOIItem(mMapView);
                    j = 1;
                }
            }
        });
        //주소 서버로 등록
        addressOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String state = getIntent().getStringExtra("state");
                switch(state) {
                    case "0":
                customAnimationDialog.show();
//                mReverseGeoCoder = new MapReverseGeoCoder("39a57489c72bc2d5000877424a73cad2", MAP_POINT, MapActivity.this, MapActivity.this);
//                mReverseGeoCoder.startFindingAddress();// 좌표를 주소로 받는다
                String url = u + "/doctorapp/location";
                /* Create request */
                Map<String, String> params = new HashMap<String, String>();
                params.put("doctor_id", doctor_num);
                params.put("address", address);
                params.put("latitude", latitude);
                params.put("longitude", longitude);
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
                                        SaveSharedPreference.editNowAddress(getApplicationContext(), address);
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                    } else {
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
                        break;
                    case "1":
                        Intent intent = new Intent(getApplicationContext(), hospital_modify.class);
                        intent.putExtra("address", address);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        break;
                    case "2":
                        Intent intent2 = new Intent(getApplicationContext(), hospital_modify2.class);
                        intent2.putExtra("address", address);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent2);
                        finish();
                        break;
                }
            }
        });

        //퍼미션 허용
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {
            checkRunTimePermission();
        }
    }

    //마커생성
    private void createDefaultMarker(MapView mapView) {
        MapPOIItem
                mMarker = new MapPOIItem();
        mMarker.setItemName("여기");
        mMarker.setTag(i);
        mMarker.setMapPoint(MAP_POINT);
        mMarker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        mMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        mapView.addPOIItem(mMarker);
        mapView.selectPOIItem(mMarker, true);
    }

    //마커 제거
    public void removePOIItem(MapView mapView) {
        MapPOIItem existingPOIItemStart = mMapView.findPOIItemByTag(i - 1);
        mMapView.removePOIItem(existingPOIItemStart);
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float accuracyInMeters) {
        MapPoint.GeoCoordinate mapPointGeo = currentLocation.getMapPointGeoCoord();
        Log.i(LOG_TAG, String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, accuracyInMeters));
    }

    //단말의 방향(Heading) 각도값을 통보받을 수 있다.
    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    //현위치 갱신 작업에 실패한 경우 호출된다.
    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    //현위치 트랙킹 기능이 사용자에 의해 취소된 경우 호출된다.
    //처음 현위치를 찾는 동안에 현위치를 찾는 중이라는 Alert Dialog 인터페이스가 사용자에게 노출된다.
    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    //주소를 찾은 경우 호출된다.
    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        mapReverseGeoCoder.toString();
        onFinishReverseGeoCoding(s);
    }

    //Reverse Geo-Coding 서비스 호출에 실패한 경우 호출된다.
    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
        onFinishReverseGeoCoding("Fail");
    }

    // Geo 서비스 호출 성공시 호출
    private void onFinishReverseGeoCoding(String result) {
        address = result;
        addressTextView.setText(address.replace("대한민국", ""));
    }

    // 초기 상태값 현재 내 위치로 이동
    @Override
    public void onMapViewInitialized(MapView mapView) {
        // MapView had loaded. Now, MapView APIs could be called safely.
//        mReverseGeoCoder = new MapReverseGeoCoder("39a57489c72bc2d5000877424a73cad2", mMapView.getMapCenterPoint(), MapActivity.this, MapActivity.this);
//        mReverseGeoCoder.startFindingAddress();
        //mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        addressTextView.setText(address.replace("대한민국", ""));
        createDefaultMarker(mMapView);
        double getlatitude = getIntent().getExtras().getDouble("latitude");
        double getlongitude = getIntent().getExtras().getDouble("longitude");
        MAP_POINT = MapPoint.mapPointWithGeoCoord(getlatitude, getlongitude);
        latitude = Double.toString(getlatitude).substring(0, 9);
        longitude = Double.toString(getlongitude).substring(0, 10);
        mMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(getlatitude, getlongitude), 1, true);
        i++;
    }

    // 지도 중앙이 움직였을때
    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapCenterPoint) {
        onRemoveCurrentLocationTrackingMode();
    }

    // 지도 줌되었을떄
    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int zoomLevel) {
        onRemoveCurrentLocationTrackingMode();
    }

    // 지도 한번 누르기 마커 생성
    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
        onMarkerCreated(mapPoint);

    }

    // 지도 두번 누르기 마커 생성
    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {
        onMarkerCreated(mapPoint);

    }

    // 지도 길게 누르기 마커 생성
    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {
        onMarkerCreated(mapPoint);
    }

    // 지도 드래그 중
    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
        onRemoveCurrentLocationTrackingMode();
    }

    // 지도 드래그가 끝났을경우
    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
        onRemoveCurrentLocationTrackingMode();
    }

    // 지도 이동이 끝났을떄
    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
        //mReverseGeoCoder = new MapReverseGeoCoder("6f504f9b73ad280372b2aff0036b6f32", MAP_POINT, MapActivity.this, MapActivity.this);
        //mReverseGeoCoder.startFindingAddress();
    }

    //현위치 마커 지워줌
    protected void onRemoveCurrentLocationTrackingMode() {
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mMapView.setShowCurrentLocationMarker(false);
    }

    // 마커 생성
    protected void onMarkerCreated(MapPoint mapPoint) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        MAP_POINT = MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude, mapPointGeo.longitude);
//        mReverseGeoCoder = new MapReverseGeoCoder("39a57489c72bc2d5000877424a73cad2", MAP_POINT, MapActivity.this, MapActivity.this);
//        mReverseGeoCoder.startFindingAddress();
        latitude = Double.toString(mapPointGeo.latitude).substring(0, 9);
        longitude = Double.toString(mapPointGeo.longitude).substring(0, 10);
        address = getCurrentAddress(mapPointGeo.latitude, mapPointGeo.longitude);
        addressTextView.setText(address.replace("대한민국", ""));
        createDefaultMarker(mMapView);
        if (i > 0 && j == 0) {
            removePOIItem(mMapView);
        } else if (j == 1) {
            j = 0;
        }
        i++;
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mMapView.setShowCurrentLocationMarker(false);
    }

    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if (check_result) {
                Log.d("@@@", "start");
                //위치 값을 가져올 수 있음
                //mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                    Toast.makeText(MapActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();


                } else {

                    Toast.makeText(MapActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission() {

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MapActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음
            //mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);


        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(MapActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MapActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MapActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    // 지오코드
    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public String getCurrentAddress(double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(MapActivity.this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(MapActivity.this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(MapActivity.this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(MapActivity.this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString() + "\n";

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customAnimationDialog.dismiss();
    }
}