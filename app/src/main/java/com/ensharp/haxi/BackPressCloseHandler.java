package com.ensharp.haxi;

import android.app.Activity;
import android.widget.Toast;

public class BackPressCloseHandler {

    private long backKeyPressedTime = 0;

    private Activity activity;

    // 선언됬을 때 해당 Activity 정보를 가져옴 (context)
    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        // 뒤로가기 버튼 눌렀을 시 뒤로가기가 안먹힘
        // 누른 시간 2초 이하, 2초 이상 모두 return함으로써 어떤 경우에도 뒤로가기가 안 먹히게 만듬.
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            Toast.makeText(activity, "뒤로가기가 비활성화 되어있습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            Toast.makeText(activity, "뒤로가기가 비활성화 되어있습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
    }

}
