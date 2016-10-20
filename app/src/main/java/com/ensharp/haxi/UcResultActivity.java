package com.ensharp.haxi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class UcResultActivity extends Activity {

    Button notify;
    Button success;

    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uc_result);


        initProperty();

        // 이상해요 버튼
        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notifyIntent = new Intent(UcResultActivity.this, UcNotifyActivity.class);
                startActivity(notifyIntent);
            }
        });

        // 비슷해요 버튼
        success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent endIntent = new Intent(UcResultActivity.this, EndActivity.class);
                startActivity(endIntent);

            }
        });
    }

    public void initProperty()
    {
        notify = (Button)findViewById(R.id.btn_notify);
        success = (Button)findViewById(R.id.btn_success);
        imageView = (ImageView)findViewById(R.id.image_map);
        imageView.setImageBitmap(UcRunningActivity.mbitmap);

    }

}
