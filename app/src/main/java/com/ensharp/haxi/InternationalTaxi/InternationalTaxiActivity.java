package com.ensharp.haxi.InternationalTaxi;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.ensharp.haxi.R;

import java.util.Locale;

public class InternationalTaxiActivity extends AppCompatActivity {

    InternationalTaxi_main_fragment main_fragment = new InternationalTaxi_main_fragment();


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

        //firstPage();
    }

    public void firstPage()
    {
        Locale locale = getResources().getConfiguration().locale;
        String language =  locale.getLanguage();

        main_fragment.url.delete(0,main_fragment.url.length());

        switch (language) {
            case "ko":
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

