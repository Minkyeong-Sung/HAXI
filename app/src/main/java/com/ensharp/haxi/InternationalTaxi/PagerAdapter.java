package com.ensharp.haxi.InternationalTaxi;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                InternationalTaxi_serviceInfo_fragment tab1 = new InternationalTaxi_serviceInfo_fragment();
                return tab1;
            case 1:
                InternationalTaxi_fare_fragment tab2 = new InternationalTaxi_fare_fragment();
                return tab2;
            case 2:
                InternationalTaxi_reservation_fragment tab3 = new InternationalTaxi_reservation_fragment();
                return tab3;
            case 3:
                InternationalTaxi_reservationConfirm_fragment tab4 = new InternationalTaxi_reservationConfirm_fragment();
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}