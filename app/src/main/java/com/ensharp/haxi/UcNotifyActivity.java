package com.ensharp.haxi;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class UcNotifyActivity extends AppCompatActivity {
    private  Context mContext;

    EditText name, phone, address;
    Button btn_send;
    Button next;

    private Toolbar toolbar;
    private EditText inputName, inputContact, inputAddress;
    private TextInputLayout inputLayoutName, inputLayoutContact, inputLayoutAddress;
    private Button btnNext;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uc_notify);


        btnNext = (Button)findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notify2Intent = new Intent(UcNotifyActivity.this,UcNotifyActivity2.class);
                startActivity(notify2Intent);
            }
        });
        mContext = UcNotifyActivity.this;
//
//        name = (EditText)findViewById(R.id.smsText);
//        phone = (EditText)findViewById(R.id.smsText2);
//        address = (EditText)findViewById(R.id.smsText3);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutContact = (TextInputLayout) findViewById(R.id.input_layout_contact);
        inputLayoutAddress = (TextInputLayout) findViewById(R.id.input_layout_address);
        inputName = (EditText) findViewById(R.id.input_name);
        inputContact = (EditText) findViewById(R.id.input_contact);
        inputAddress = (EditText) findViewById(R.id.input_address);
//        btnSignUp = (Button) findViewById(R.id.btn_signup);

        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputContact.addTextChangedListener(new MyTextWatcher(inputContact));
        inputAddress.addTextChangedListener(new MyTextWatcher(inputAddress));

//        btnSignUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                submitForm();
//            }
//        });
    }


    public void sendSMS(View v){
        String strName = inputName.getText().toString();
        String strPhone = inputContact.getText().toString();
        String strAddress = inputAddress.getText().toString();
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

    private void submitForm() {
        if (!validateName()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        Toast.makeText(getApplicationContext(), "Thank You!", Toast.LENGTH_SHORT).show();
    }

    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {
        String email = inputContact.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutContact.setError(getString(R.string.err_msg_email));
            requestFocus(inputContact);
            return false;
        } else {
            inputLayoutContact.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (inputAddress.getText().toString().trim().isEmpty()) {
            inputLayoutAddress.setError(getString(R.string.err_msg_password));
            requestFocus(inputAddress);
            return false;
        } else {
            inputLayoutAddress.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_name:
                    validateName();
                    break;
                case R.id.input_contact:
                    validateEmail();
                    break;
                case R.id.input_address:
                    validatePassword();
                    break;
            }
        }
    }
}