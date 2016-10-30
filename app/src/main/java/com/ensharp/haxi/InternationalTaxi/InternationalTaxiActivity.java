package com.ensharp.haxi.InternationalTaxi;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.ensharp.haxi.R;

import java.util.Locale;

public class InternationalTaxiActivity extends AppCompatActivity {

    public static StringBuilder URL_locale = new StringBuilder("/?lang=ko");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_international_taxi);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Info"));
        tabLayout.addTab(tabLayout.newTab().setText("Fare"));
        tabLayout.addTab(tabLayout.newTab().setText("Reserv."));
        tabLayout.addTab(tabLayout.newTab().setText("Confirm"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        getLocale();

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

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
}

