package com.ensharp.haxi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class UcNotifyActivity2 extends Activity {

    private static final String TAG = "영수증 촬영";
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int LONG_DELAY = 4500;
    private Uri mImageCaptureUri;
    private AlertDialog mDialog;
    Button complete;

    Bitmap photo;

    Button notifyNext;
    Button notifyCrop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uc_notify2);
        setLayout();
//        complete = (Button)findViewById(R.id.btn_complete);
//        complete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent notify3Intent = new Intent(UcNotifyActivity2.this, MainActivity.class);
//                startActivity(notify3Intent);
//            }
//        });
//
//        new TedPermission(this)
//                .setPermissionListener(permissionlistener)
//                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
//                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                .check();

//        FragmentManager fm = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fm.beginTransaction();
//        fragmentTransaction.add(R.id.frameLayout_notify, new FragmentStepTwo());
//        fragmentTransaction.commit();

    }

    /**
     Button Click
     */
    public void onButtonClick(View v){
        switch (v.getId()) {
//            case R.id.btn_mms:
//                Log.e(TAG, "mImageCaptureUri = " + mImageCaptureUri);
//                sendMMS(mImageCaptureUri);
////			sendMMSG();
//                break;
            // 영수증 촬영 버튼을 눌렀을 때
            case R.id.btn_capture:
//                mDialog = createDialog();
//                mDialog.show();
                // 카메라 바로 실행시킴
//                doTakePhotoAction();
//                setDismiss(mDialog);

                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, PICK_FROM_CAMERA);

                // Fragment 전환 부분
                Fragment fr;
                fr = new FragmentStepTwo();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout_notify, fr);
                fragmentTransaction.commit();
                break;

            case R.id.notify_crop:

                break;

            case R.id.notify_next:
                break;
        }
    }

    // TedPermission 권한 부분
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(UcNotifyActivity2.this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(UcNotifyActivity2.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
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
        ab.setTitle("영수증 첨부");
        ab.setView(innerView);

        return  ab.create();
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
     * MMS 발송	(APP TAB BOX)
     */
    private void sendMMS(Uri uri){
        if(uri==null)
        {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "영수증 사진을 촬영해주세요", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        else {
            uri = Uri.parse("" + uri);
            Intent it = new Intent(Intent.ACTION_SEND);
            it.putExtra("address", "02-120");
            it.putExtra("sms_body", "바가지요금 영수증을 비교할 경로사진을 첨부해주세요.");
            it.putExtra(Intent.EXTRA_STREAM, uri);
            it.setType("image/*");
            startActivity(it);
            Toast toast = Toast.makeText(getApplicationContext(),
                    "경로 캡쳐본을 추가해주세요", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
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
//        Log.d(TAG, "onActivityResultX");
//        if(resultCode != RESULT_OK)
//        {
//            return;
//        }

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_FROM_CAMERA) {
                if (data != null) {
                    Log.e("Test", "result = " + data);
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    if (thumbnail != null) {
                        ImageView Imageview = (ImageView) findViewById(R.id.image_receipt);
                        Imageview.setImageBitmap(thumbnail);
                    }
                }

            } else if (requestCode == PICK_FROM_ALBUM) {
                if (data != null) {
                    Log.e("Test", "result = " + data);

                    Uri thumbnail = data.getData();
                    if (thumbnail != null) {
                        ImageView Imageview = (ImageView) findViewById(R.id.image_receipt);
                        Imageview.setImageURI(thumbnail);
                    }
                }
            }
        }

//        switch(requestCode)
//        {
//            case PICK_FROM_ALBUM:
//            {
//                Log.d(TAG, "PICK_FROM_ALBUM");
//
//                // 이후의 처리가 카메라와 같으므로 일단  break없이 진행합니다.
//                // 실제 코드에서는 좀더 합리적인 방법을 선택하시기 바랍니다.
//                mImageCaptureUri = data.getData();
//                File original_file = getImageFile(mImageCaptureUri);
//
//                mImageCaptureUri = createSaveCropFile();
//                File cpoy_file = new File(mImageCaptureUri.getPath());
//
//                // SD카드에 저장된 파일을 이미지 Crop을 위해 복사한다.
//                copyFile(original_file , cpoy_file);
//            }
//
//            case PICK_FROM_CAMERA:
//            {
//                Log.d(TAG, "PICK_FROM_CAMERA");
//
//                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
//                // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.
//
//                Intent intent = new Intent("com.android.camera.action.CROP");
//                intent.setDataAndType(mImageCaptureUri, "image/*");
//
//                // Crop한 이미지를 저장할 Path
//                intent.putExtra("output", mImageCaptureUri);
//
//                // Return Data를 사용하면 번들 용량 제한으로 크기가 큰 이미지는
//                // 넘겨 줄 수 없다.
////			intent.putExtra("return-data", true);
//                startActivityForResult(intent, CROP_FROM_CAMERA);
//
////                ImageView receiptImage;
////                receiptImage = (ImageView)findViewById(R.id.image_receipt);
////
////                String full_path = mImageCaptureUri.getPath();
////                String photo_path = full_path.substring(4, full_path.length());
////
////                photo = BitmapFactory.decodeFile(full_path);
////
////                Log.i("HYEON", "setImageBitmap을 마치기 전입니다");
////                Log.i("HYEON", photo.toString());
////
////                receiptImage.setImageBitmap(photo);
////
////                Log.i("HYEON", "setImageBitmap을 마쳤습니다");
//                break;
//            }
//
//            case CROP_FROM_CAMERA:
//            {
//                Log.w(TAG, "CROP_FROM_CAMERA");
//
//                // Crop 된 이미지를 넘겨 받습니다.
//                Log.w(TAG, "mImageCaptureUri = " + mImageCaptureUri);
//
//                String full_path = mImageCaptureUri.getPath();
//                String photo_path = full_path.substring(4, full_path.length());
//
//                Log.w(TAG, "비트맵 Image path = "+photo_path);
//
//                Log.i("HYEON", "1");
//
//                // 에러나는 부분
//                Bitmap photo = BitmapFactory.decodeFile(full_path);
//                Log.i("HYEON", "2");
//                mPhotoImageView.setImageBitmap(photo);
//                Log.i("HYEON", "3");
//
//
//                Fragment fr;
//                fr = new FragmentStepTwo();
//                FragmentManager fm = getFragmentManager();
//                FragmentTransaction fragmentTransaction = fm.beginTransaction();
//                fragmentTransaction.replace(R.id.frameLayout_notify, fr);
//                fragmentTransaction.commit();
//
//                break;
//            }
//        }
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
        mPhotoImageView = (ImageView)findViewById(R.id.image_receipt);
        notifyCrop = (Button)findViewById(R.id.notify_crop);
        notifyNext = (Button)findViewById(R.id.notify_next);
    }

    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));

    }
}
