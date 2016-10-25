package com.ensharp.haxi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;

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
                new TedPermission(UcResultActivity.this)
                        .setPermissionListener(permissionlistener)
                        .setRationaleMessage("핸드폰 번호를 자동으로 입력하기 위해 권한이 필요합니다.")
                        .setDeniedMessage("왜 거부하셨어요...\n하지만 [설정] > [권한] 에서 권한을 허용할 수 있어요.")
                        .setPermissions(Manifest.permission.READ_PHONE_STATE)
                        .check();
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

    // Ted Permission - 권한체크
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(UcResultActivity.this, "권한 허가", Toast.LENGTH_SHORT).show();
            Intent notifyIntent = new Intent(UcResultActivity.this, UcNotifyActivity.class);
            startActivity(notifyIntent);
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(UcResultActivity.this, "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    // 나눔고딕 폰트설정
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
