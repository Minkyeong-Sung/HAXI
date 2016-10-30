package com.ensharp.haxi;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by HYEON on 2016-10-29.
 */

public class CustomFont extends TextView{
    public CustomFont(Context context) {
        super(context);
        setType(context);
    }

    public CustomFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        setType(context);
    }

    public CustomFont(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setType(context);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public CustomFont(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setType(context);
    }

    private void setType(Context context) {
        //asset에 폰트 복사
        //NotoSnat 경령화된 폰트 위치: https://github.com/theeluwin/NotoSansKR-Hestia
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/NanumBarunGothic.otf" +
                ""));
    }
}
