package com.ensharp.haxi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsengvn.typekit.TypekitContextWrapper;

public class UcResultActivity extends Activity {

    Button notify;
    Button success;

    TextView view_taxiFare;

    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uc_result);


        initProperty();

        view_taxiFare.setText(MyApplication.taxi_fare_string);

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

    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));

    }

    public void initProperty()
    {
        notify = (Button)findViewById(R.id.btn_notify);
        success = (Button)findViewById(R.id.btn_success);
        imageView = (ImageView)findViewById(R.id.image_map);
        /* ucRunning액티비티에서 압축한 ScreenShot 적용 */
        imageView.setImageBitmap(UcRunningActivity.mbitmap);
        view_taxiFare = (TextView)findViewById(R.id.result_taxiFare);

    }

}
