package com.ensharp.haxi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UcRunningActivity extends Activity {

    Button arrive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uc_running);

        arrive = (Button)findViewById(R.id.btn_arrive);
        arrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent(UcRunningActivity.this, UcResultActivity.class);
                startActivity(resultIntent);
            }
        });
    }
}
