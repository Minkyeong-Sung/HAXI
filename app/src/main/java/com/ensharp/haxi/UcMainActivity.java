package com.ensharp.haxi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ensharp.haxi.Map.GPSTracker;
import com.ensharp.haxi.Map.SearchLocation;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

public class UcMainActivity extends Activity implements PlaceSelectionListener {

    private RelativeLayout mainLayout;
    private GoogleMap map;
    private SensorManager mSensorManager;
    private SearchLocation searchLocation;
    private GPSTracker gps;

    private Geocoder geocoder;
    private EditText start_location_input;
    private EditText destination_location_input;

    private ImageButton current_button;
    private Button current_button1;
    private Button current_button2;
    private Button start_search_button;
    private Button destination_search_button;
    private Button CompleteBoarding;

    private double latitude;
    private double longitude;
    public static StringBuilder URL = new StringBuilder("https://m.map.naver.com/spirra/findCarRoute.nhn?route=route3&output=json&coord_type=latlng&search=0&car=0&mileage=12.4&start=127.0738840,37.5514706&destination=126.9522394,37.4640070");
    public static StringBuilder start_URL_latlng;
    public static StringBuilder destination_URL_latlng;
    public static String[] split_stringBuilder;

    private static final int START = 1;
    private static final int DESTINATION = 2;

    private static final String LOG_TAG = "PlaceSelectionListener";
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    private TextView locationTextView;
    private TextView attributionsTextView;

    private String str_destination;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uc_main);

        // 권한 추가부분 - HYEON
        // 안드로이드 누가(7.0) 대응
        // 현재 뭐가 먼저 실행되서(?) 앱이 종료 되는데, 이후에 바로 권한체크 확인문이 뜨긴함
        // 재 실행 시, 권한있음 토스트문구가 뜨면서 정상작동
        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .check();


        gps = new GPSTracker(UcMainActivity.this);

        // 속성 및 버튼, 텍스트 박스 Initialization.
        init_Property();
        init_Button_And_Textbox();
        currentMyLocation(start_location_input, START);

        // 초기 Map 화면 서울로 보이게 만듬
        LatLng firstMapLocation = new LatLng(37.5666102, 126.9783881);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(firstMapLocation, 11));

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(this);
        autocompleteFragment.setHint("도착지를 입력하세요");
        autocompleteFragment.setBoundsBias(BOUNDS_MOUNTAIN_VIEW);


    }

    @Override
    public void onPlaceSelected(Place place) {
        Log.i(LOG_TAG, "Place Selected: " + place.getName());
//        locationTextView.setText(getString(R.string.formatted_place_data, place
//                .getName(), place.getAddress(), place.getPhoneNumber(), place
//                .getWebsiteUri(), place.getRating(), place.getId()));
        str_destination = place.getName().toString();
        if (!TextUtils.isEmpty(place.getAttributions())){
            attributionsTextView.setText(Html.fromHtml(place.getAttributions().toString()));
        }

        searchLocation.findLocation(str_destination, DESTINATION, destination_location_input);
        setInitflag();
        Log.i(LOG_TAG, "onPlaceSelected 함수에서 findLoaction 를 완료하였습니다");
    }

    @Override
    public void onError(Status status) {
        Log.e(LOG_TAG, "onError: Status = " + status.toString());
        Toast.makeText(this, "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

    public void init_Button_And_Textbox() {
        CompleteBoarding = (Button) findViewById(R.id.btn_CompleteBoarding);
//        start_search_button = (Button) findViewById(R.id.start_btn);
//        destination_search_button = (Button) findViewById(R.id.destination_btn);
//        current_button = (ImageButton)findViewById(R.id.iBtn_currentLocation);
//        current_button1 = (Button) findViewById(R.id.current_location_btn);
//        current_button2 = (Button) findViewById(R.id.current_location_btn2);



//        start_location_input = (EditText) findViewById(R.id.start_input);
        destination_location_input = (EditText) findViewById(R.id.destination_input);

//        // 출발지 입력 버튼 누를 시
//        start_search_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 사용자가 입력한 주소 정보 확인
//                String searchStr = start_location_input.getText().toString();
//                // 주소 정보를 이용해 위치 좌표 찾기 메소드 호출
//                searchLocation.findLocation(searchStr, START, start_location_input);
//                setInitflag();
//            }
//        });

//        // 도착지 입력 버튼 누를 시
//        destination_search_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 사용자가 입력한 주소 정보 확인
//                String searchStr = destination_location_input.getText().toString();
//                // 주소 정보를 이용해 위치 좌표 찾기 메소드 호출
//                searchLocation.findLocation(searchStr, DESTINATION, destination_location_input);
//                setInitflag();
//            }
//        });

//        current_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                currentMyLocation(start_location_input, START);
//                setInitflag();
//            }
//        });

//        // 현재 위치 버튼 누를 시
//        current_button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                currentMyLocation(start_location_input, START);
//                setInitflag();
//                hideSoftKeyboard(mainLayout);
//            }
//        });
//
//        current_button2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setInitflag();
//                currentMyLocation(destination_location_input, DESTINATION);
//                hideSoftKeyboard(mainLayout);
//            }
//        });



        // 탑승 완료 입력 버튼 누를 시
        CompleteBoarding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchStr = str_destination.toString();
                // 주소 정보를 이용해 위치 좌표 찾기 메소드 호출
                searchLocation.findLocation(searchStr, DESTINATION, destination_location_input);
                setInitflag();
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


        // 권한체크 - HYEON
        // 현재 7.0 누가에서 여기를 그냥 건너뛰는바람에 뒤에 Activity에서 오류가남
        // 16.10.06 03:46
        // 권한체크 더 알아볼 것
        checkDangerousPermissions();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        map.setMyLocationEnabled(true);
        //map.getUiSettings().setMyLocationButtonEnabled(false);
        //map.setPadding(0,900,0,0);
        // 센서 관리자 객체 참조
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // 지오코더 객체 생성
        geocoder = new Geocoder(this, Locale.KOREAN);
        searchLocation = new SearchLocation(map, geocoder);
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
        } catch (SecurityException e) {
            e.printStackTrace();
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
    }

    public void setInitflag()
    {
        SearchLocation.start_move_flag = false;
        SearchLocation.destination_move_flag = false;
        hideSoftKeyboard(mainLayout);
    }


    // 예상 택시 요금 및 거리를 띄어주는 Alert 창.
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
            // URL 갱신후
            URL = new StringBuilder("https://m.map.naver.com/spirra/findCarRoute.nhn?route=route3&output=json&coord_type=latlng&search=0&car=0&mileage=12.4" +
                    "&start=" + start_URL_latlng + "&destination=" + destination_URL_latlng);
            // 이 URL에 대한 Json 파싱시작 -> HttpAsyncTask() 메소드로 감.
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
            // GET 메소드로 이동동
           return GET(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        // AsyncTask 작업이 완료된 후 실행되는 Method -> Json 파싱 다듬기 및 Alert창 띄우기
        @Override
        protected void onPostExecute(String result) {
            int index_dist = result.indexOf("totalDistance");                                      // Json 파싱 후 전체 Text에서 짜르고 싶은 부분을 나누기 위해
            int index_gasPayPerLiter = result.indexOf("gasPayPerLiter");                           // 첫 index 값 과 끝 index 값 저장을 위한 변수 생성.

            StringBuilder stringBuilder = new StringBuilder(result);                               // Json 파싱한 Text ( result )를 StringBuilder에 넣기

            stringBuilder.delete(0, index_dist);
            stringBuilder.delete(index_gasPayPerLiter - index_dist, stringBuilder.length());       // Json 파싱 결과값 다듬기

            String a = stringBuilder.toString();                                                   // 총 거리, 소요 시간, 택시비 정보를
            split_stringBuilder = a.split("[:,]");                                                 // 배열부분에 담는다

            Intent runningIntent = new Intent(UcMainActivity.this, UcRunningActivity.class);
            startActivity(runningIntent);

//            AlertDialog.Builder builder = new AlertDialog.Builder(UcMainActivity.this);            // Alert창 띄우기
//            builder.setTitle("택시비 정보 입니다.")
//                    .setMessage("택시비: " + split_stringBuilder[9] + "\n총 거리: " + split_stringBuilder[1] + "\n소요 시간: " + split_stringBuilder[3])
//                    .setPositiveButton("누적거리 시작", new DialogInterface.OnClickListener(){
//                        @Override
//                        public void onClick(DialogInterface dialog, int id) {
//                            Intent runningIntent = new Intent(((Dialog) dialog).getContext(), UcRunningActivity.class);
//                            startActivity(runningIntent);                                          // OK 버튼 누를시 누적거리 Activity 띄우기
//                        }
//                    })
//                    .setNegativeButton("확인", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int id) {                                   // No 버튼 누를시 Alert창 닫기.
//                            dialog.cancel();
//                        }
//                    }).show();
        }
    }

    // 현재위치 버튼을 눌렀을 시에 대한 Method
    public void currentMyLocation(EditText input, int option) {

        // gpsTracker를 이용해 현재 위치를 받기
        if(gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        } else {
            gps.showSettingsAlert();
        }

        LatLng latLng = new LatLng(latitude, longitude);
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));                                   // 해당 위경도로 카메라 이동!

        // 출발지와 도착지 현재위치 Marker 구분해주기
        if (option == START) {

            if (SearchLocation.startMarker_flag == true)                                            // 출발지 Marker가 존재하면 지워주고 새로 표시하기.
                SearchLocation.startMarker.remove();

            SearchLocation.startMarker = map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))  // Marker 생성.
                    .title("출발지\n" + latitude + "\n" + longitude)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .draggable(true));
            SearchLocation.startMarker.showInfoWindow();                                            // Marker 화면에 표시하기.
            SearchLocation.startMarker_flag = true;                                                 // Marker 생성했다고 표시해주기.
            this.start_URL_latlng = new StringBuilder(longitude + "," + latitude);                  // 해당 위 경도값 URL에 넣어주기위한 변수.
        }

        else if (option == DESTINATION) {
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

//        input.setText("내 현재 위치");                                                              // EditText에 표시해주기.
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(UcMainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(UcMainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }


    };

}