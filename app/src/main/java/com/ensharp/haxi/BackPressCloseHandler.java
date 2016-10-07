package com.ensharp.haxi;

import android.app.Activity;
import android.widget.Toast;

public class BackPressCloseHandler {

    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    // 선언됬을 때 해당 Activity 정보를 가져옴 (context)
    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        // 뒤로가기 버튼 눌렀을 시 뒤로가기가 안먹힘
        // 누른 시간 2초 이하, 2초 이상 모두 return함으로써 어떤 경우에도 뒤로가기가 안 먹히게 만듬.
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            return;
        }
    }

    // 알림문구 출력부분
    public void showGuide() {
        toast = Toast.makeText(activity, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}
