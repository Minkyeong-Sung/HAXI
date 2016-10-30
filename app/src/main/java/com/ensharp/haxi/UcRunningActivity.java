package com.ensharp.haxi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ensharp.haxi.Map.AccureCurrentPath;
import com.ensharp.haxi.Map.SearchLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.io.File;
import java.text.NumberFormat;

import static com.ensharp.haxi.UcMainActivity.split_stringBuilder;
import static java.lang.System.out;

public class UcRunningActivity extends Activity {

    private AccureCurrentPath mAccurePath = new AccureCurrentPath();
    private BackPressCloseHandler backPressCloseHandler;
    private GoogleMap map;
    private RelativeLayout mainLayout;

    /* 누적거리에 이용되는 LatLng 변수 */
    private LatLng oldLatLng;
    private LatLng currentLatLng;

    /* 출발지, 도착지에 이용되는 LatLng 변수 */
    private LatLng start_latLng;
    private LatLng desti_Latlng;

    private Marker new_taxi_marker;
    private GPSListener gpsListener;

    private boolean first_Taximakrer_show = false;
    private boolean first_path = false;
    private TextView taxi_fare;
    private Button arrive;

    /* SnapShot에 이용되는 변수 */
    public static Bitmap mbitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uc_running);

        init_Property();
        init_map();
        init_button();
        startLocationService();

        backPressCloseHandler = new BackPressCloseHandler(this);
        taxi_fare = (TextView) findViewById(R.id.text_taxifare);
        taxi_fare.setText(split_stringBuilder[9]);

        // UcResult 에서 쓸 taxi 요금데이터 구성 ( 3단위 콤마 적용)
        MyApplication.taxi_fare_int = Integer.parseInt(split_stringBuilder[9]);
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumIntegerDigits(5);
        MyApplication.taxi_fare_string = nf.format(MyApplication.taxi_fare_int);
    }

    // 나눔고딕 폰트 적용 부분분
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    public void init_map() {
        start_latLng = new LatLng((Double) UcMainActivity.locationInfo.get(0), (Double) UcMainActivity.locationInfo.get(1));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(start_latLng, 15));

        Marker startMarker = map.addMarker(new MarkerOptions().position(start_latLng)  // Marker 생성.
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        SearchLocation.startMarker.showInfoWindow();                                            // Marker 화면에 표시하기.
    }

    public void init_Property() {
        // 메인 레이아웃 객체 참조
        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        // 지도 객체 참조
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.gmap)).getMap();
    }

    public void init_button() {
        arrive = (Button) findViewById(R.id.btn_arrive);
        /* 도착 버튼 눌렀을시 스크린샷 활성화 */
        arrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("HYEON", "init_button중 arrive Button Click Event 에 접근했습니다");

                settingZoom();
                /* map.snapshot(callback)으로 인해 콜백 함수 활성화 */
                /* map.snapshot(callback)을 먼저실행 -> 이 콜백으로 옴 */
                GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {

                    @Override
                    public void onSnapshotReady(Bitmap snapshot) {
                        /* 구글 API가 지원하는 스냅샷 메소드 */
                        if (snapshot == null) {
                            Toast.makeText(getApplicationContext(), "null", Toast.LENGTH_SHORT).show();
                            Log.i("HYEON", "snapshot이 null 조건문에 만족했습니다");
                        }
                        else {

                            File fileCacheItem = new File("/sdcard/1.png");
//                            OutputStream out = null;
                            try {
                                 /* 저장 경로를 얻은 뒤 압축하기 */
                                 /* bitmap에 현재 구글지도 screenshot을 넣고 */
//                                fileCacheItem.createNewFile();
//                                out = new FileOutputStream(fileCacheItem);
                                snapshot.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                mbitmap = snapshot;

                                Intent resultIntent = new Intent(UcRunningActivity.this, UcResultActivity.class);
                                startActivity(resultIntent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                map.snapshot(callback);

            }
        });

    }

    /* 출발지 도착지에 따른 자동 Zoom 설정 */
    public void settingZoom()
    {
        desti_Latlng = new LatLng((Double)UcMainActivity.locationInfo.get(0), (Double)UcMainActivity.locationInfo.get(1));
        Marker destinationMarker = map.addMarker(new MarkerOptions()
                .position(desti_Latlng)  // Marker 생성.
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(start_latLng.latitude, start_latLng.longitude));
        builder.include(new LatLng(desti_Latlng.latitude, desti_Latlng.longitude));
        LatLngBounds bounds = builder.build();
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

        /* static 초기화 해주기 */
        UcMainActivity.locationInfo.clear();

        SystemClock.sleep(3000);

    }

    /**
    * 위치 정보 확인을 위해 정의한 메소드
    */
    private void startLocationService() {
        // 위치 관리자 객체 참조
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 위치 정보를 받을 리스너 생성
        gpsListener = new GPSListener();
        long minTime = 2000;
        float minDistance = 0;

        try {
            // GPS를 이용한 위치 요청
            manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    minTime,
                    minDistance,
                    gpsListener);

            // 네트워크를 이용한 위치 요청
            manager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    minTime,
                    minDistance,
                    gpsListener);

            // 위치 확인이 안되는 경우에도 최근에 확인된 위치 정보 먼저 확인
            Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                Double latitude = lastLocation.getLatitude();
                Double longitude = lastLocation.getLongitude();
            }
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }

        Toast.makeText(getApplicationContext(), "위치 확인이 시작되었습니다. 로그를 확인하세요.", Toast.LENGTH_SHORT).show();
    }

    /**
     * 리스너 클래스 정의
     */
    private class GPSListener implements LocationListener {
        /**
         * 위치 정보가 확인될 때 자동 호출되는 메소드
         */
        public void onLocationChanged(Location location) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();

            String msg = "Latitude : " + latitude + "\nLongitude:" + longitude;
            Log.i("GPSListener", msg);

            if (first_path) {
                currentLatLng = new LatLng(latitude, longitude);
                if (mAccurePath.drawPolyline(map, oldLatLng, currentLatLng)) {

                    /* line 설정 */
                    CircleOptions circleOptions = new CircleOptions();
                    circleOptions.center(currentLatLng).radius(0.2).strokeColor(Color.RED).fillColor(Color.RED);
                    map.addCircle(circleOptions);

                    /* 택시 마커가 존재하면 지워주고 다시 생성 */
                    if (first_Taximakrer_show)
                        new_taxi_marker.remove();

                    new_taxi_marker = map.addMarker(new MarkerOptions().position(new LatLng(latitude - 0.000012, longitude))
                            .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("taxi", 150, 150))));

                    /* 현재 위치는 다시 oldLatlng으로 바꿔주기 */
                    oldLatLng = currentLatLng;
                    first_Taximakrer_show = true;

                    /* 도착지 Latlng clear후 추가해주기 */
                    UcMainActivity.locationInfo.clear();
                    UcMainActivity.locationInfo.add(latitude);
                    UcMainActivity.locationInfo.add(longitude);
                    showCurrentLocation(latitude, longitude);
                } else {
                    return;
                }

            } else {
                oldLatLng = new LatLng(latitude, longitude);
                first_path = true;
                return;
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }


    // taxi 이미지 줄여주는 메소드
    public Bitmap resizeMapIcons(String iconName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    // 현재 위치의 지도를 보여주기 위해 정의한 메소드
    private void showCurrentLocation(Double latitude, Double longitude) {
        // 현재 위치를 이용해 LatLon 객체 생성
        LatLng curPoint = new LatLng(latitude, longitude);

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 19));

        // 지도 유형 설정. 지형도인 경우에는 GoogleMap.MAP_TYPE_TERRAIN, 위성 지도인 경우에는 GoogleMap.MAP_TYPE_SATELLITE
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // 현재 위치 주위에 아이콘을 표시하기 위해 정의한 메소드
        //showAllBankItems(latitude, longitude);
    }


    @Override
    public void onResume() {
        super.onResume();

        try {
            // 내 위치 자동 표시 enable
            map.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            // 내 위치 자동 표시 disable
            exitLocationManager();
            map.setMyLocationEnabled(false);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void exitLocationManager() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.removeUpdates(gpsListener);
    }


    /* 뒤로가기 버튼 눌렀을 시*/
    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

}
