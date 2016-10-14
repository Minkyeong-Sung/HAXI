package com.ensharp.haxi.InternationalTaxi;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.ensharp.haxi.R;

import java.util.Locale;

public class InternationalTaxiActivity extends AppCompatActivity {
    ViewPager mViewPager;
    InternationalTaxi_main_fragment main_fragment = new InternationalTaxi_main_fragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_international_taxi);

        firstPage();
        mViewPager = (ViewPager)findViewById(R.id.pager);
        mViewPager.setAdapter(new SamplePagerAdapter(getSupportFragmentManager()));
    }
    /** Defining a FragmentPagerAdapter class for controlling the fragments to be shown when user swipes on the screen. */
    public class SamplePagerAdapter extends FragmentPagerAdapter {

        public SamplePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {

            if (position == 0) {
                return main_fragment;}
            else if(position == 1){
                return new InternationalTaxi_reservation_fragment();}
            else if(position == 2){
                return new InternationalTaxi_reservationConfirm_fragment();}
            else if(position == 3){
                return new InternationalTaxi_serviceInfo_fragment();}
            else {
                return new InternationalTaxi_fare_fragment();}
        }

        @Override
        public int getCount() {
            // 5개의 page가 있다.
            return 5;
        }
    }

    public void firstPage()
    {
        Locale locale = getResources().getConfiguration().locale;
        String language =  locale.getLanguage();

        main_fragment.url.delete(0,main_fragment.url.length());
        // ja_JP
        switch (language) {
            case "kr":
                main_fragment.url.append("http://www.intltaxi.co.kr/?lang=ko"); break;
            case "ja":
                main_fragment.url.append("http://www.intltaxi.co.kr/?lang=jp"); break;
            case "zh":
                main_fragment.url.append("http://www.intltaxi.co.kr/?lang=cn"); break;
            default:
                main_fragment.url.append("http://www.intltaxi.co.kr/?lang=en"); break;
        }
    }
}

