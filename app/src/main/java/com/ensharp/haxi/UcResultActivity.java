package com.ensharp.haxi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UcResultActivity extends Activity {

    Button notify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uc_result);

        notify = (Button)findViewById(R.id.btn_notify);
        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notifyIntent = new Intent(UcResultActivity.this, UcNotifyActivity.class);
                startActivity(notifyIntent);
            }
        });
    }
}
