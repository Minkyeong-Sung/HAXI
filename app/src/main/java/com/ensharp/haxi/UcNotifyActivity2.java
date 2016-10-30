package com.ensharp.haxi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uc_notify2);

        setLayout();

        notify_receipt.setVisibility(View.INVISIBLE);
        btnCamera.setVisibility(View.INVISIBLE);
        btnNext.setVisibility(View.INVISIBLE);
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
                        .title("신고하기 전 확인단계 입니다")
                        .content("주행했던 경로사진과 촬영했던 영수증사진이 함께 MMS로 전송됩니다.\n핸드폰에서 MMS 비용이 부과될 수 있습니다.\n전에 입력했던 정보들도 활용됩니다.\n확인 버튼을 누르면 다산콜센터(02-120)로 전송됩니다.")
                        .positiveText("확인")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Toast.makeText(UcNotifyActivity2.this, "agree", Toast.LENGTH_LONG).show();
                                sendMMS(mImageCaptureUri);
                            }
                        })
                        .negativeText("취소")
                        .show();
                break;
        }
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
            Toast toast = Toast.makeText(getApplicationContext(),
                    getString(R.string.UcN2Text1), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        else {
            uri = Uri.parse("" + uri);
            Intent it = new Intent(Intent.ACTION_SEND);
            it.putExtra("address", "01049122194");
            it.putExtra("sms_body", "TEST 내용입니다 문자메시지 12345678910 ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            it.putExtra(Intent.EXTRA_STREAM, uri);
            it.setType("image/*");
            startActivity(it);
            Toast toast = Toast.makeText(getApplicationContext(),
                    getString(R.string.UcN2Text3), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
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
        sendIntent.putExtra("address", "01000000000");
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

                notify_guideText.setText("영수증이 잘 보이시나요?");

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

        // Fragment
        fr = new FragmentStepTwo();
        fm = getFragmentManager();
    }
}