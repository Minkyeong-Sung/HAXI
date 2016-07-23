package com.ensharp.haxi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UcMainActivity extends Activity {

    Button CompleteBoarding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uc_main);

        CompleteBoarding = (Button)findViewById(R.id.btn_CompleteBoarding);
        CompleteBoarding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent runningIntent = new Intent(UcMainActivity.this, UcRunningActivity.class);
                startActivity(runningIntent);
            }
        });
    }
}
