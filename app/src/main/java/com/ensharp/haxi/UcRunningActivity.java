package com.ensharp.haxi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

public class UcRunningActivity extends Activity {

    private AccureCurrentPath mAccurePath = new AccureCurrentPath();
    private GoogleMap map;

    private RelativeLayout mainLayout;
    private LatLng oldLatLng;
    private LatLng currentLatLng;
    private Button arrive;

    private boolean first_path = false;

    public void setMap(GoogleMap map) {
        this.map = map;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uc_running);
        init_Property();
        init_button();
        startLocationService();
    }
    public void init_Property()
    {
        // 메인 레이아웃 객체 참조
        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        // 지도 객체 참조
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.gmap)).getMap();

    }

    public void init_button()
    {
        arrive = (Button)findViewById(R.id.btn_arrive);
        arrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent(UcRunningActivity.this, UcResultActivity.class);
                startActivity(resultIntent);
            }
        });

    }
    /**
     * 위치 정보 확인을 위해 정의한 메소드
     */
    private void startLocationService() {
        // 위치 관리자 객체 참조
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 위치 정보를 받을 리스너 생성
        GPSListener gpsListener = new GPSListener();
        long minTime = 1000;
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

                Toast.makeText(getApplicationContext(), "Last Known Location : " + "Latitude : " + latitude + "\nLongitude:" + longitude, Toast.LENGTH_LONG).show();
            }
        } catch(SecurityException ex) {
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

            String msg = "Latitude : "+ latitude + "\nLongitude:"+ longitude;
            Log.i("GPSListener", msg);


            if(first_path)
            {
                currentLatLng = new LatLng(latitude,longitude);
                if(mAccurePath.drawPolyline(map,oldLatLng,currentLatLng))
                {
                    CircleOptions circleOptions = new CircleOptions();
                    circleOptions.center(currentLatLng).radius(0.2).strokeColor(Color.RED).fillColor(Color.RED);
                    map.addCircle(circleOptions);

                    oldLatLng = currentLatLng;
                    showCurrentLocation(latitude, longitude);
                }
                else {
                    return;
                }

            }
            else
            {
                oldLatLng = new LatLng(latitude,longitude);
                first_path = true;
                return;
            }
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    } // 현재 위치의 지도를 보여주기 위해 정의한 메소드
    private void showCurrentLocation(Double latitude, Double longitude) {
        // 현재 위치를 이용해 LatLon 객체 생성
        LatLng curPoint = new LatLng(latitude, longitude);

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 21));

        // 지도 유형 설정. 지형도인 경우에는 GoogleMap.MAP_TYPE_TERRAIN, 위성 지도인 경우에는 GoogleMap.MAP_TYPE_SATELLITE
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // 현재 위치 주위에 아이콘을 표시하기 위해 정의한 메소드
        //showAllBankItems(latitude, longitude);
    }

}
