package com.ensharp.haxi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

public class UcMainActivity extends Activity {

    private RelativeLayout mainLayout;
    private GoogleMap map;
    private SensorManager mSensorManager;
    private CompassView mCompassView;
    private SearchLocation searchLocation;

    private Geocoder geocoder;
    private EditText start_location_input;
    private EditText destination_location_input;

    private Button current_button1;
    private Button current_button2;
    private Button start_search_button;
    private Button destination_search_button;
    private Button CompleteBoarding;

    public static StringBuilder URL = new StringBuilder("https://m.map.naver.com/spirra/findCarRoute.nhn?route=route3&output=json&coord_type=latlng&search=0&car=0&mileage=12.4&start=127.0738840,37.5514706&destination=126.9522394,37.4640070");
    public static StringBuilder start_URL_latlng;
    public static StringBuilder destination_URL_latlng;
    public static String[] split_stringBuilder;
    private static final int START = 1;
    private static final int DESTINATION = 2;

    private boolean mCompassEnabled;

    private boolean okCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uc_main);
        // 속성 및 버튼, 텍스트 박스 Initialization.
        init_Property();
        init_Button_And_Textbox();
        // 초기 Map 화면 서울로 보이게 만듬
        LatLng firstMapLocation = new LatLng(37.5666102, 126.9783881);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(firstMapLocation, 10));
    }

    public void init_Button_And_Textbox() {
        CompleteBoarding = (Button) findViewById(R.id.btn_CompleteBoarding);
        start_search_button = (Button) findViewById(R.id.start_btn);
        destination_search_button = (Button) findViewById(R.id.destination_btn);
        current_button1 = (Button) findViewById(R.id.current_location_btn);
        current_button2 = (Button) findViewById(R.id.current_location_btn2);

        start_location_input = (EditText) findViewById(R.id.start_input);
        destination_location_input = (EditText) findViewById(R.id.destination_input);

        // 출발지 입력 버튼 누를 시
        start_search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사용자가 입력한 주소 정보 확인
                String searchStr = start_location_input.getText().toString();
                // 주소 정보를 이용해 위치 좌표 찾기 메소드 호출
                searchLocation.findLocation(searchStr, START, start_location_input);
                hideSoftKeyboard(mainLayout);
            }
        });

        // 도착지 입력 버튼 누를 시
        destination_search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사용자가 입력한 주소 정보 확인
                String searchStr = destination_location_input.getText().toString();
                // 주소 정보를 이용해 위치 좌표 찾기 메소드 호출
                searchLocation.findLocation(searchStr, DESTINATION, destination_location_input);
                hideSoftKeyboard(mainLayout);
            }
        });

        // 현재 위치 버튼 누를 시
        current_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMyLocation(start_location_input, START);
            }
        });

        current_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMyLocation(destination_location_input, DESTINATION);
            }
        });

        // 탑승 완료 입력 버튼 누를 시
        CompleteBoarding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_Taxifare_distance();
            }
        });
    }


    protected void hideSoftKeyboard(View view) {
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void init_Property() {
        // 메인 레이아웃 객체 참조
        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        // 지도 객체 참조 및 지도 처음 위치 활성화
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.gmap)).getMap();
        // 센서 관리자 객체 참조
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // 나침반을 표시할 뷰 생성
        boolean sideBottom = true;

        // 지오코더 객체 생성
        geocoder = new Geocoder(this, Locale.KOREAN);
        searchLocation = new SearchLocation(map, geocoder);

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
            map.setMyLocationEnabled(false);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        if (mCompassEnabled) {
            mSensorManager.registerListener(mListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            // 내 위치 자동 표시 disable
            map.setMyLocationEnabled(false);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        if (mCompassEnabled) {
            mSensorManager.unregisterListener(mListener);
        }
    }

    public void show_Taxifare_distance() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        if (!SearchLocation.startMarker_flag) {
            builder.setTitle("출발지 입력을 하지 않았어")
                    .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .show();
        } else if (!SearchLocation.destinationMarker_flag) {
            builder.setTitle("도착지 입력을 하지 않았어")
                    .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .show();
        } else {
            if (isConnected()) {
            }
            // call AsynTask to perform network operation on separate thread
            URL = new StringBuilder("https://m.map.naver.com/spirra/findCarRoute.nhn?route=route3&output=json&coord_type=latlng&search=0&car=0&mileage=12.4" +
                    "&start=" + start_URL_latlng + "&destination=" + destination_URL_latlng);

            new HttpAsyncTask().execute(URL.toString());

        }
    }

    public static String GET(String url) {
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            int index_dist = result.indexOf("totalDistance");
            int index_gasPayPerLiter = result.indexOf("gasPayPerLiter");

            StringBuilder stringBuilder = new StringBuilder(result);

            stringBuilder.delete(0, index_dist);
            stringBuilder.delete(index_gasPayPerLiter - index_dist, stringBuilder.length());

            String a = stringBuilder.toString();
            split_stringBuilder = a.split("[:,]"); // 배열부분에 담는다

            AlertDialog.Builder builder = new AlertDialog.Builder(UcMainActivity.this);

            builder.setTitle("택시비 정보 입니다.")
                    .setMessage("택시비: " + split_stringBuilder[9] + "\n총 거리: " + split_stringBuilder[1] + "\n소요 시간: " + split_stringBuilder[3])
                    .setPositiveButton("누적거리 시작", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Intent runningIntent = new Intent(UcMainActivity.this, UcRunningActivity.class);
                            startActivity(runningIntent);
                        }
                    })
                    .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }).show();


        }
    }

    public void currentMyLocation(EditText input, int option) {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location myLocation = locationManager.getLastKnownLocation(provider);

        double latitude = myLocation.getLatitude();
        double longitude = myLocation.getLongitude();
        //String statr_string = getResources().getString(R.string.start);

        LatLng latLng = new LatLng(latitude, longitude);
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        // 출발지와 도착지 현재위치 Marker 구분해주기
        if (option == START) {
            if (SearchLocation.startMarker_flag == true)
                SearchLocation.startMarker.remove();
            SearchLocation.startMarker = map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                    .title("출발지\n" + latitude + "\n" + longitude)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .draggable(true));
            SearchLocation.startMarker.showInfoWindow();
            SearchLocation.startMarker_flag = true;
            this.start_URL_latlng = new StringBuilder(longitude + "," + latitude);
        } else if (option == DESTINATION) {
            if (SearchLocation.startMarker_flag == true)
                SearchLocation.destinationMarker.remove();

            SearchLocation.destinationMarker = map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                    .title("도착지\n" + latitude + "\n" + longitude)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .draggable(true));
            SearchLocation.destinationMarker.showInfoWindow();
            SearchLocation.destinationMarker_flag = true;
            this.destination_URL_latlng = new StringBuilder(longitude + "," + latitude);
        }

        input.setText("내 현재 위치");
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