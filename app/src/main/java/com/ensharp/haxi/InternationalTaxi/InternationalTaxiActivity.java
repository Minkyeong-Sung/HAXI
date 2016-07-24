package com.ensharp.haxi.InternationalTaxi;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.Button;

import com.ensharp.haxi.InternationalTaxi_fare_fragment;
import com.ensharp.haxi.InternationalTaxi_reservationCheck_fragment;
import com.ensharp.haxi.InternationalTaxi_reservation_fragment;
import com.ensharp.haxi.InternationalTaxi_serviceInfo_fragment;
import com.ensharp.haxi.R;

public class InternationalTaxiActivity extends Activity {

    Button reservation_btn;
    Button serviceInfo_btn;
    Button fare_btn;
    Button reservationCheck_btn;

    InternationalTaxi_main_fragment main_fragment = new InternationalTaxi_main_fragment();
    InternationalTaxi_reservation_fragment reservation_fragment = new InternationalTaxi_reservation_fragment();
    InternationalTaxi_serviceInfo_fragment serviceInfo_fragment = new InternationalTaxi_serviceInfo_fragment();
    InternationalTaxi_fare_fragment fare_fragment = new InternationalTaxi_fare_fragment();
    InternationalTaxi_reservationCheck_fragment reservationCheck_fragment = new InternationalTaxi_reservationCheck_fragment();
    FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_international_taxi);

        firstPage();
        button_Init();
    }

    public void firstPage()
    {
        

    }

    public void button_Init() {
        reservation_btn = (Button) findViewById(R.id.reservation_btn);
        serviceInfo_btn = (Button) findViewById(R.id.serviceInfo_btn);
        fare_btn = (Button) findViewById(R.id.fare_btn);
        reservationCheck_btn = (Button) findViewById(R.id.reservationCheck_btn);

        reservation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.webView_fragment, reservation_fragment);
                ft.commit();
            }
        });

        serviceInfo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.webView_fragment, serviceInfo_fragment);
                ft.commit();

            }
        });

        fare_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.webView_fragment, fare_fragment);
                ft.commit();

            }
        });

        reservationCheck_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.webView_fragment, reservationCheck_fragment);
                ft.commit();
            }
        });
    }
}
