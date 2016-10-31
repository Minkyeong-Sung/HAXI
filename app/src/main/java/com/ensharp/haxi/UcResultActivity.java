package com.ensharp.haxi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
          //  Toast.makeText(UcResultActivity.this, "권한 허가", Toast.LENGTH_SHORT).show();
            Intent notifyIntent = new Intent(UcResultActivity.this, UcNotifyActivity.class);
            startActivity(notifyIntent);
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            //Toast.makeText(UcResultActivity.this, "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    public void initProperty()
    {
        notify = (Button)findViewById(R.id.btn_notify);
        success = (Button)findViewById(R.id.btn_success);
        imageView = (ImageView)findViewById(R.id.image_map);
        /* ucRunning액티비티에서 압축한 ScreenShot 적용 */
        imageView.setImageBitmap(UcRunningActivity.mbitmap);
        saveBitmaptoJpeg(UcRunningActivity.mbitmap, "", "pathMap");
        view_taxiFare = (TextView)findViewById(R.id.result_taxiFare);

    }

    public static void saveBitmaptoJpeg(Bitmap bitmap, String folder, String name){
        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
        // Get Absolute Path in External Sdcard
        String foler_name = "/"+folder+"/";
        String file_name = name+".jpg";
        String string_path = ex_storage+foler_name;

        File file_path;
        try{
            file_path = new File(string_path);
            if(!file_path.isDirectory()){
                file_path.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(string_path+file_name);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();

        }catch(FileNotFoundException exception){
            Log.e("FileNotFoundException", exception.getMessage());
        }catch(IOException exception){
            Log.e("IOException", exception.getMessage());
        }
    }

}
