package com.ensharp.haxi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gun0912.tedpermission.PermissionListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static android.widget.Toast.makeText;

public class UcNotifyActivity2 extends Activity {
    private static final String TAG = "TestImageCropActivity";

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;

    private Uri mImageCaptureUri;
    private AlertDialog mDialog;

    ImageView resultImage;

    public static Bitmap photo;

    Fragment fr;
    FragmentManager fm;

    ImageView notify_receipt;
    TextView notify_guideText;
    Button btnFirstCamera;
    Button btnCamera;
    Button btnNext;
    Button btnExit;

    private long backKeyPressedTime = 0;
    Toast toast;

    RelativeLayout rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uc_notify2);

        setLayout();

        notify_receipt.setVisibility(View.INVISIBLE);
        btnCamera.setVisibility(View.INVISIBLE);
        btnNext.setVisibility(View.INVISIBLE);
        btnExit.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            Intent endIntent = new Intent(UcNotifyActivity2.this, EndActivity.class);
            startActivity(endIntent);
            toast.cancel();
        }
    }

    public void showGuide() {
        toast = Toast.makeText(UcNotifyActivity2.this,
                "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     Button Click
     */
    public void onButtonClick(View v){
        switch (v.getId()) {
//            case R.id.btn_sns:
//                sendSMS("01049122194" , "hi nice to meet you");
//                break;
//            case R.id.btn_mms:
//                Log.e(TAG, "mImageCaptureUri = " + mImageCaptureUri);
//                sendMMS(mImageCaptureUri);
////          sendMMSG();
//                break;
            case R.id.btn_capture:
                doTakePhotoAction();
                setDismiss(mDialog);
                break;
            case R.id.notify_reCapture:
                doTakePhotoAction();
                setDismiss(mDialog);
                break;
            // 다음버튼
            case R.id.notify_next:
                new MaterialDialog.Builder(this)
                        .title(getString(R.string.notify3))
                        .content(getString(R.string.notify4))
                        .positiveText(getString(R.string.notify5))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                makeText(UcNotifyActivity2.this, "SMS신고는 완료됬습니다.\n보내는곳은 02-112 입니다.\nMMS창에서 전송을 눌러주세요", Toast.LENGTH_LONG).show();
                                autoSendSMS("01049122194", "부당요금을 신고합니다. 출발지는 " + MyApplication.startAddress + " 이며, 도착지는 " + MyApplication.destinationAddress +" 입니다. Naver, Daum, T-Map의 평균 택시요금은 " + MyApplication.taxi_fare_int + "원 이였으나 이 이상으로 요금이 많이나와 증거자료와 함께 신고합니다. 신고자는 " + MyApplication.name + "이며, 거주중인 주소는 " + MyApplication.address + " 입니다.");
                                sendMMS(mImageCaptureUri);
                            }
                        })
                        .negativeText(getString(R.string.notify6))
                        .show();
                break;
            case R.id.btn_exit:

                Intent exitIntent = new Intent(UcNotifyActivity2.this, EndActivity.class);
                startActivity(exitIntent);
                break;
        }
    }

    private void autoSendSMS(String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        makeText(getBaseContext(), "알림 문자 메시지가 전송되었습니다.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> messageParts = sms.divideMessage(message);

        //sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
        sms.sendMultipartTextMessage(phoneNumber, null, messageParts, null, null);
    }

    // TedPermission 권한 부분
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
           // Toast.makeText(UcNotifyActivity2.this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            //Toast.makeText(UcNotifyActivity2.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 다이얼로그 생성
     */
    private AlertDialog createDialog() {
        final View innerView = getLayoutInflater().inflate(R.layout.image_crop_row, null);

        Button camera = (Button)innerView.findViewById(R.id.btn_camera_crop);
        Button gellary = (Button)innerView.findViewById(R.id.btn_gellary_crop);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doTakePhotoAction();
                setDismiss(mDialog);
            }
        });

        gellary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doTakeAlbumAction();
                setDismiss(mDialog);
            }
        });

        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle("이미지 Crop");
        ab.setView(innerView);

        return ab.create();
    }

    /**
     * 다이얼로그 종료
     */
    private void setDismiss(AlertDialog dialog){
        if(dialog!=null&&dialog.isShowing()){
            dialog.dismiss();
        }
    }

    /**
     * SMS 발송
     */
    private void sendSMS(String reciver , String content){
        Uri uri = Uri.parse("smsto:"+reciver);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", content);
        startActivity(it);
    }

    /**
     * MMS 발송   (APP TAB BOX)
     */
    private void sendMMS(Uri uri){
        if(uri==null)
        {
            Toast toast = makeText(getApplicationContext(),
                    getString(R.string.UcN2Text1), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        else {
            uri = Uri.parse("" + uri);
            Uri uri2 = Uri.parse("file:///storage/emulated/0/pathMap.jpg");
            Intent it = new Intent(Intent.ACTION_SEND_MULTIPLE);
            ArrayList uris = new ArrayList();
            uris.add(mImageCaptureUri);
            uris.add(uri2);
            //it.putExtra("sms_body", "TEST 내용입니다 문자메시지 12345678910 ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            it.putExtra(Intent.EXTRA_STREAM, uris);
            it.setType("image/*");
            //it.putExtra("address", "01033911537");
            startActivity(it);

            new Handler().postDelayed(new Runnable() {// 10 초 후에 실행
                @Override
                public void run() {
                    notify_guideText.setText("신고문자가 완료되었습니다.");

                    rl.setBackgroundResource(R.drawable.notify2_background);
                    btnExit.setVisibility(View.VISIBLE);

                    resultImage.setVisibility(View.INVISIBLE);
                    btnCamera.setVisibility(View.INVISIBLE);
                    btnNext.setVisibility(View.INVISIBLE);
                }
            }, 10000);
        }
    }

    /**
     * MMS 발송 ( 첨부 파일 없음 )
     */
    private void sendMMSG(){
        Uri mmsUri = Uri.parse("mmsto:");
        Intent sendIntent = new Intent(Intent.ACTION_VIEW, mmsUri);
        sendIntent.addCategory("android.intent.category.DEFAULT");
        sendIntent.addCategory("android.intent.category.BROWSABLE");
        sendIntent.putExtra("address", "01049122194");
        sendIntent.putExtra("exit_on_sent", true);
        sendIntent.putExtra("subject", "dfdfdf");
        sendIntent.putExtra("sms_body", "dfdfsdf");
        Uri dataUri = Uri.parse(""+mImageCaptureUri);
        sendIntent.putExtra(Intent.EXTRA_STREAM, dataUri);

        startActivity(sendIntent);
    }


    /**
     * 카메라 호출 하기
     */
    private void doTakePhotoAction()
    {
        Log.i(TAG, "doTakePhotoAction()");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Crop된 이미지를 저장할 파일의 경로를 생성
        mImageCaptureUri = createSaveCropFile();
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    /**
     * 앨범 호출 하기
     */
    private void doTakeAlbumAction()
    {
        Log.i(TAG, "doTakeAlbumAction()");
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    /**
     * Result Code
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d(TAG, "onActivityResult");
        if(resultCode != RESULT_OK)
        {
            return;
        }

        switch(requestCode)
        {
            case PICK_FROM_ALBUM:
            {
                Log.d(TAG, "PICK_FROM_ALBUM");

                // 이후의 처리가 카메라와 같으므로 일단  break없이 진행합니다.
                // 실제 코드에서는 좀더 합리적인 방법을 선택하시기 바랍니다.
                mImageCaptureUri = data.getData();
                File original_file = getImageFile(mImageCaptureUri);

                mImageCaptureUri = createSaveCropFile();
                File cpoy_file = new File(mImageCaptureUri.getPath());

                // SD카드에 저장된 파일을 이미지 Crop을 위해 복사한다.
                copyFile(original_file , cpoy_file);
            }

            case PICK_FROM_CAMERA:
            {
                Log.d(TAG, "PICK_FROM_CAMERA");

                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
                // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                // Crop한 이미지를 저장할 Path
                intent.putExtra("output", mImageCaptureUri);

                // Return Data를 사용하면 번들 용량 제한으로 크기가 큰 이미지는
                // 넘겨 줄 수 없다.
//          intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_CAMERA);

                break;
            }

            case CROP_FROM_CAMERA:
            {
                Log.w(TAG, "CROP_FROM_CAMERA");

                // Crop 된 이미지를 넘겨 받습니다.
                Log.w(TAG, "mImageCaptureUri = " + mImageCaptureUri);

                String full_path = mImageCaptureUri.getPath();
                String photo_path = full_path.substring(4, full_path.length());

                Log.w(TAG, "비트맵 Image path = "+full_path);

                notify_guideText.setText(getString(R.string.notify2));

                photo = BitmapFactory.decodeFile(full_path);
                resultImage.setImageBitmap(photo);

                notify_receipt.setVisibility(View.VISIBLE);
                btnFirstCamera.setVisibility(View.INVISIBLE);
                btnCamera.setVisibility(View.VISIBLE);
                btnNext.setVisibility(View.VISIBLE);

                break;
            }
        }
    }

    /**
     * Crop된 이미지가 저장될 파일을 만든다.
     * @return Uri
     */
    private Uri createSaveCropFile(){
        Uri uri;
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
        return uri;
    }


    /**
     * 선택된 uri의 사진 Path를 가져온다.
     * uri 가 null 경우 마지막에 저장된 사진을 가져온다.
     * @param uri
     * @return
     */
    private File getImageFile(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        if (uri == null) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        Cursor mCursor = getContentResolver().query(uri, projection, null, null,
                MediaStore.Images.Media.DATE_MODIFIED + " desc");
        if(mCursor == null || mCursor.getCount() < 1) {
            return null; // no cursor or no record
        }
        int column_index = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        mCursor.moveToFirst();

        String path = mCursor.getString(column_index);

        if (mCursor !=null ) {
            mCursor.close();
            mCursor = null;
        }

        return new File(path);
    }

    /**
     * 파일 복사
     * @param srcFile : 복사할 File
     * @param destFile : 복사될 File
     * @return
     */
    public static boolean copyFile(File srcFile, File destFile) {
        boolean result = false;
        try {
            InputStream in = new FileInputStream(srcFile);
            try {
                result = copyToFile(in, destFile);
            } finally  {
                in.close();
            }
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    /**
     * Copy data from a source stream to destFile.
     * Return true if succeed, return false if failed.
     */
    private static boolean copyToFile(InputStream inputStream, File destFile) {
        try {
            OutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.close();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /*
     * Layout
     */
    private ImageView mPhotoImageView;

    private void setLayout(){
//        mPhotoImageView = (ImageView)findViewById(R.id.image_receipt);
        resultImage = (ImageView) findViewById(R.id.image_receipt);

        notify_receipt = (ImageView) findViewById(R.id.image_receipt);
        btnFirstCamera = (Button) findViewById(R.id.btn_capture);
        btnCamera = (Button) findViewById(R.id.notify_reCapture);
        btnNext = (Button) findViewById(R.id.notify_next);
        notify_guideText = (TextView) findViewById(R.id.notify_guideText);
        btnExit = (Button) findViewById(R.id.btn_exit);

        rl = (RelativeLayout) findViewById(R.id.notify2_layout);
        // Fragment
        fr = new FragmentStepTwo();
        fm = getFragmentManager();
    }
}