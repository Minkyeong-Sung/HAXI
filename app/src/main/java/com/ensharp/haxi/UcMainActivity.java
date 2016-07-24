package com.ensharp.haxi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

public class UcMainActivity extends Activity {

    private RelativeLayout mainLayout;
    private GoogleMap map;
    private SensorManager mSensorManager;
    private boolean first_path = false;
    private LatLng oldLatLng;
    private LatLng currentLatLng;


    private AccureCurrentPath mAccurePath;
    private SearchLocation searchLocation;

    Geocoder geocoder;
    Button start_search_button;
    Button destination_search_button;
    Button CompleteBoarding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uc_main);

        init_Property();
        CompleteBoarding = (Button)findViewById(R.id.btn_CompleteBoarding);
        CompleteBoarding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent runningIntent = new Intent(UcMainActivity.this, UcRunningActivity.class);
                startActivity(runningIntent);
            }
        });
    }
    public void init_Property()
    {
        // 메인 레이아웃 객체 참조
        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        // 지도 객체 참조
        map = ((MapFragment)getFragmentManager().findFragmentById(R.id.gmap)).getMap();
        // 센서 관리자 객체 참조
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        // 나침반을 표시할 뷰 생성
        boolean sideBottom = true;
        mAccurePath = new AccureCurrentPath();

        // 지오코더 객체 생성
        geocoder = new Geocoder(this, Locale.KOREAN);
        searchLocation = new SearchLocation(map,geocoder);

        //currentLocation = new CurrentLocation(map);
        //mCompassView = new CompassView(this);
        //mCompassView.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(sideBottom ? RelativeLayout.ALIGN_PARENT_BOTTOM : RelativeLayout.ALIGN_PARENT_TOP);
        // mainLayout.addView(mCompassView, params);
        //mCompassEnabled = true;

        //checkDangerousPermissions();
    }

}
