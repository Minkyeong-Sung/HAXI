package com.ensharp.haxi;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

public class UcNotifyActivity extends Activity {
    private  Context mContext;

    EditText name, phone, address;
    Button btn_send;
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uc_notify);

        next = (Button)findViewById(R.id.btn_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TedPermission(UcNotifyActivity.this)
                        .setPermissionListener(permissionlistener)
                        .setRationaleMessage("영수증 촬영을 위한 카메라 권한이 필요해요! 꼭 허락해주세요!")
                        .setDeniedMessage("왜 거부하셨어요...\n하지만 [설정] > [권한] 에서 권한을 허용할 수 있어요.")
                        .setPermissions(Manifest.permission.CAMERA)
                        .check();
            }
        });
        mContext = UcNotifyActivity.this;

        name = (EditText)findViewById(R.id.smsText);
        phone = (EditText)findViewById(R.id.edit_phoneNum);
        address = (EditText)findViewById(R.id.smsText3);
    }

    protected void onResume() {
        super.onResume();

        TelephonyManager telManager = (TelephonyManager)this.getSystemService(this.TELEPHONY_SERVICE);
        String phoneNumber = telManager.getLine1Number();

        phone.setText(phoneNumber);
    }

    public void senSMS(View v){
        String strName = name.getText().toString();
        String strPhone = phone.getText().toString();
        String strAddress = address.getText().toString();
        String content = "저는 위 영수증 사진과 같이 왕십리에서 탑승하여 세종대학교에 도착하였으나, 경로를 둘러서 간 것 같으니, 확인 부탁드립니다.";

        String total = "신고자" + strName + '\n' + "연락처" + strPhone + '\n' + "주소" + strAddress + '\n' + content ;

        if (total.length()>0 ){ //smsNum.length()>0 &&smsText.length()>0
            sendSMS("01048862255", total);
        }else{
            Toast.makeText(this, "모두 입력해 주세요", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendSMS(String smsNumber, String total){
        PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT_ACTION"), 0);
        PendingIntent deliveredIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED_ACTION"), 0);

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch(getResultCode()){
                    case Activity.RESULT_OK:
                        Toast.makeText(mContext, "전송 완료", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(mContext, "전송 실패", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(mContext, "서비스 지역이 아닙니다", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(mContext, "무선(Radio)가 꺼져있습니다", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(mContext, "PDU Null", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter("SMS_SENT_ACTION"));

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()){
                    case Activity.RESULT_OK:
                        Toast.makeText(mContext, "SMS 도착완료", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(mContext, "SMS 도착안됨", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter("SMS_DELIVERED_ACTION"));

        SmsManager mSmsManager = SmsManager.getDefault();
        mSmsManager.sendTextMessage("01048862255", null, total, sentIntent, deliveredIntent);

        // 지정 글자수 넘어갔을때 mms로 보내도록 //
        ArrayList<String> messageParts = mSmsManager.divideMessage(total);

        mSmsManager.sendMultipartTextMessage("01048862255", null, messageParts, null, null);

        Toast.makeText(this, "전송완료.", Toast.LENGTH_SHORT).show();
    }

    // TedPermission 권한 결과 받는부분
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(UcNotifyActivity.this, "권한 허가", Toast.LENGTH_SHORT).show();
            Intent notify2Intent = new Intent(UcNotifyActivity.this,UcNotifyActivity2.class);
            startActivity(notify2Intent);
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(UcNotifyActivity.this, "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }
    };
}