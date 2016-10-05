package com.ensharp.haxi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ensharp.haxi.InternationalTaxi.InternationalTaxiActivity;


public class MainActivity extends Activity {

    Button openUc_activity;
    Button openIT_activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 스플래시 화면 띄우는 부분
        startActivity(new Intent(this, SplashActivity.class));

        openUc_activity = (Button)findViewById(R.id.btn_openUcActivity);
        openUc_activity.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent UcIntent = new Intent(MainActivity.this, UcMainActivity.class);
                startActivity(UcIntent);
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
}



