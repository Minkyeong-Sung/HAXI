package com.ensharp.haxi;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

public class EndActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                moveTaskToBack(true);
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }, 3000);
    }

}
