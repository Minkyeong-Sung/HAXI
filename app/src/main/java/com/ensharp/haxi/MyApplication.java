package com.ensharp.haxi;

import android.app.Application;

import com.tsengvn.typekit.Typekit;

/**
 * Created by HYEON on 2016-10-23.
 */

public class MyApplication extends Application {

    public static String taxi_fare_string;
    public static int taxi_fare_int;

    public static String startAddress;
    public static String destinationAddress;

    public static String name;
    public static String address;


    @Override
    public void onCreate() {
        super.onCreate();

        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "fonts/NanumBarunGothic.otf"))
                .addBold(Typekit.createFromAsset(this, "fonts/NanumBarunGothicBold.otf"));
    }
}
