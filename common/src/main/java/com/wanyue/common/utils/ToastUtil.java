package com.wanyue.common.utils;

import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;
import com.wanyue.common.CommonApplication;

/**
 * 2017/8/3.
 */

public class ToastUtil {

    private static Toast sToast;
    private static long sLastTime;
    private static String sLastString;

    static {
        sToast = makeToast();
    }

    private static Toast makeToast() {
        Toast toast = Toast.makeText(CommonApplication.sInstance, "", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        return toast;
    }

    public static void show(int res) {
        show(WordUtil.getString(res));
    }

    public static void showPriority(String large,String small) {
        if(TextUtils.isEmpty(large)||large.equals("ok")){
            show(small);
        }else{
            show(large);
        }
    }

    public static void show(String s) {
        if (TextUtils.isEmpty(s)) {
            return;
        }
        if(s.equals("-1")){
            L.e("准备debug");
        }

        long curTime = System.currentTimeMillis();
        if (curTime - sLastTime > 2000) {
            sLastTime = curTime;
            sLastString = s;
            sToast.setText(s);
            sToast.show();
        } else {
            if (!s.equals(sLastString)) {
                sLastTime = curTime;
                sLastString = s;
                sToast = makeToast();
                sToast.setText(s);
                sToast.show();
            }
        }
    }
}
