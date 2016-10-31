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
                finishAffinity();
                System.runFinalizersOnExit(true);
                System.exit(0);
            }
        }, 3000);
    }

}
