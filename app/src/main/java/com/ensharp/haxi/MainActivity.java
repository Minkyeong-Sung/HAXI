package com.ensharp.haxi;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import com.ensharp.haxi.InternationalTaxi.InternationalTaxiActivity;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends Activity {

    Button openUc_activity;
    Button openIT_activity;

    public static StringBuilder URL_locale = new StringBuilder("/?lang=ko");
    Boolean permission_check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chkGpsService();
        // 스플래시 화면 띄우는 부분
        startActivity(new Intent(this, SplashActivity.class));

        // 사용 언어 받기
        getLocale();

        openUc_activity = (Button)findViewById(R.id.btn_openUcActivity);
        openUc_activity.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                new TedPermission(MainActivity.this)
                        .setPermissionListener(permissionlistener)
                        //.setRationaleMessage("현재 위치를 찾기위해 권한이 필요합니다!")
                        .setRationaleMessage(getString(R.string.MainText1))
                        //.setDeniedMessage("왜 거부하셨어요...\n하지만 [설정] > [권한] 에서 권한을 허용할 수 있어요.")
                        .setRationaleMessage(getString(R.string.MainText2))
                        .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check();
            }
        });

        openIT_activity = (Button)findViewById(R.id.btn_openInternationalTaxiActivity);
        openIT_activity.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent internationalIntent = new Intent(MainActivity.this, InternationalTaxiActivity.class);
                startActivity(internationalIntent);
            }
        });
    };


    public void getLocale()
    {
        Locale locale = getResources().getConfiguration().locale;
        String language =  locale.getLanguage();

        URL_locale.delete(0,URL_locale.length());

        switch (language) {
            case "ko":
                URL_locale.append("/?lang=ko"); break;
            case "ja":
                URL_locale.append("/?lang=jp"); break;
            case "zh":
                URL_locale.append("/?lang=cn"); break;
            default:
                URL_locale.append("/?lang=en"); break;
        }
    }
    // Ted Permission - 권한체크
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
          //  Toast.makeText(MainActivity.this, "권한 허가", Toast.LENGTH_SHORT).show();
            Intent UcIntent = new Intent(MainActivity.this, UcMainActivity.class);
            startActivity(UcIntent);
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
           // Toast.makeText(MainActivity.this, "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }


    };

    //GPS 설정 체크
    private boolean chkGpsService() {

        String gps = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!(gps.matches(".*gps.*") && gps.matches(".*network.*"))) {

            // GPS OFF 일때 Dialog 표시
            AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
           // gsDialog.setTitle("위치 서비스 설정");
            gsDialog.setTitle(getString(R.string.MainText3));
           // gsDialog.setMessage("무선 네트워크 사용, GPS 위성 사용을 모두 체크하셔야 정확한 위치 서비스가 가능합니다.\n위치 서비스 기능을 설정하시겠습니까?");
            gsDialog.setTitle(getString(R.string.MainText4));
            gsDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // GPS설정 화면으로 이동
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivity(intent);
                }
            })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    }).create().show();
            return false;

        } else {
            return true;
        }
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
           // Toast.makeText(this, "권한 있음", Toast.LENGTH_LONG).show();
        } else {
           // Toast.makeText(this, "권한 없음", Toast.LENGTH_LONG).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
               // Toast.makeText(this, "권한 설명 필요함.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
    }
}



