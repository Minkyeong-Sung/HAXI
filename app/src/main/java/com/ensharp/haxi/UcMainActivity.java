package com.ensharp.haxi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import java.util.Locale;

public class UcMainActivity extends Activity {

    private RelativeLayout mainLayout;
    private GoogleMap map;
    private SensorManager mSensorManager;

    private UcRunningActivity ucRunningActivity;
    private CompassView mCompassView;
    private SearchLocation searchLocation;

    private Geocoder geocoder;
    private EditText start_location_input;
    private EditText destination_location_input;

    private Button start_search_button;
    private Button destination_search_button;
    private Button CompleteBoarding;

    private static final int START = 1;
    private static final int DESTINATION = 2;

    private boolean mCompassEnabled;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uc_main);

        // 속성 및 버튼, 텍스트 박스 Initialization.
        init_Property();
        init_Button_And_Textbox();
    }

    public void init_Button_And_Textbox()
    {
        CompleteBoarding = (Button)findViewById(R.id.btn_CompleteBoarding);
        start_search_button = (Button)findViewById(R.id.start_btn);
        destination_search_button = (Button)findViewById(R.id.destination_btn);
        start_location_input = (EditText)findViewById(R.id.start_input);
        destination_location_input = (EditText)findViewById(R.id.destination_input);

        start_search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사용자가 입력한 주소 정보 확인
                String searchStr = start_location_input.getText().toString();
                // 주소 정보를 이용해 위치 좌표 찾기 메소드 호출
                searchLocation.findLocation(searchStr,START,start_location_input);
            }
        });

        destination_search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사용자가 입력한 주소 정보 확인
                String searchStr = destination_location_input.getText().toString();
                // 주소 정보를 이용해 위치 좌표 찾기 메소드 호출
                searchLocation.findLocation(searchStr,DESTINATION,destination_location_input);
            }
        });

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

        // 지오코더 객체 생성
        geocoder = new Geocoder(this, Locale.KOREAN);
        searchLocation = new SearchLocation(map,geocoder);

        //currentLocation = new CurrentLocation(map);
        mCompassView = new CompassView(this);
        mCompassView.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(sideBottom ? RelativeLayout.ALIGN_PARENT_BOTTOM : RelativeLayout.ALIGN_PARENT_TOP);
        mainLayout.addView(mCompassView, params);
        mCompassEnabled = true;

        checkDangerousPermissions();
    }
    // 권한 설정 check 메소드
    private void checkDangerousPermissions() {
        String[] permissions = {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "권한 있음", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "권한 없음", Toast.LENGTH_LONG).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                Toast.makeText(this, "권한 설명 필요함.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, permissions[i] + " 권한이 승인됨.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, permissions[i] + " 권한이 승인되지 않음.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();

        try {
            // 내 위치 자동 표시 enable
            map.setMyLocationEnabled(true);
        } catch(SecurityException e) {
            e.printStackTrace();
        }

        if(mCompassEnabled) {
            mSensorManager.registerListener(mListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            // 내 위치 자동 표시 disable
            map.setMyLocationEnabled(false);
        } catch(SecurityException e) {
            e.printStackTrace();
        }

        if(mCompassEnabled) {
            mSensorManager.unregisterListener(mListener);
        }
    }
    /**
     * 센서의 정보를 받기 위한 리스너 객체 생성
     */
    private final SensorEventListener mListener = new SensorEventListener() {
        private int iOrientation = -1;

        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        // 센서의 값을 받을 수 있도록 호출되는 메소드
        public void onSensorChanged(SensorEvent event) {
            if (iOrientation < 0) {
                iOrientation = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
            }

            mCompassView.setAzimuth(event.values[0] + 90 * iOrientation);
            mCompassView.invalidate();
        }

    };
}
