package com.wanyue.shop.view.pop;

import android.content.Context;

import androidx.annotation.NonNull;
import com.lxj.xpopup.core.CenterPopupView;

/**
 * The type Base center pop view.
 */
public class BaseCenterPopView extends CenterPopupView {

    /**
     * Instantiates a new Base center pop view.
     *
     * @param context the context
     */
    public BaseCenterPopView(@NonNull Context context) {
        super(context);
    }

    @Override
    public void destroy(){
        if(dialog!=null) {
            dialog.dismiss();
        }
        onDetachedFromWindow();
        if(popupInfo!=null){
            popupInfo.atView = null;
            popupInfo.watchView = null;
            popupInfo.xPopupCallback = null;
            popupInfo = null;
        }
    }
}
