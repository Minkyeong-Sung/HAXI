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
        // 한번 눌렸을 경우
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            // 해당 시간을 저장시켜놓는다
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        // 만약 2초 내에 눌렸다면
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            // 해당 activity 종료
            activity.finish();
            // 알림문구도 종료
            toast.cancel();
        }
    }

    // 알림문구 출력부분
    public void showGuide() {
        toast = Toast.makeText(activity, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}
