package com.wanyue.shop.photo.util;

import android.text.TextUtils;

import com.wanyue.common.utils.ListUtil;
import com.wanyue.shop.photo.bean.SelectPhotoBean;

import java.util.ArrayList;
import java.util.List;

public class Util {
    /*将转换为所需要的字符串list*/
    public static ArrayList<String> tranformData(List<SelectPhotoBean> photoPathArray) {
        int size = ListUtil.getSize(photoPathArray);
        ArrayList<String> list = new ArrayList<>(size);
        if (size == 0) {
            return list;
        }
        for (SelectPhotoBean selectPhotoBean : photoPathArray) {
            if (selectPhotoBean.isBtn()) {
                continue;
            }
            String photo = selectPhotoBean.getPhoto();
            if (!TextUtils.isEmpty(photo)) {
                list.add(selectPhotoBean.getPhoto());
            }
        }
        return list;
    }

    /*将字符串list转换为所需要的目标类型*/
    public static List<SelectPhotoBean> formatData(List<String> photoPathArray,int maxLength) {
        int size = ListUtil.getSize(photoPathArray);
        List<SelectPhotoBean> list = new ArrayList<>(size);
        if (size == 0) {
            list.add(new SelectPhotoBean(true));
        } else {
            for (String photo : photoPathArray) {
                list.add(new SelectPhotoBean(photo));
            }
            if (size < maxLength) {
                list.add(new SelectPhotoBean(true));
            }
        }
        return list;
    }

}
