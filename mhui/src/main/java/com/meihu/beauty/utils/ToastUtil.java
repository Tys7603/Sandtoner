package com.meihu.beauty.utils;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.meihu.beauty.R;


/**
 *  on 2017/8/3.
 */

public class ToastUtil {

    @Nullable
    private static Toast mToast;

    public static void show(int res) {
        show(WordUtil.getString(MhDataManager.getInstance().getContext(),res));
    }

    public static final void show(final String message) {
        if (MhDataManager.getInstance().getContext() == null){
            return;
        }
        BackgroundTasks.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mToast != null) {
                    mToast.cancel();
                    mToast = null;
                }
                mToast = Toast.makeText(MhDataManager.getInstance().getContext(), message, Toast.LENGTH_SHORT);
                mToast.show();
            }
        });
    }

}
